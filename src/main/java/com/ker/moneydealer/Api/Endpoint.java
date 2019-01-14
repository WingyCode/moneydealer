package com.ker.moneydealer.Api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.ker.moneydealer.Controllers.AccountDealer;
import com.ker.moneydealer.Controllers.AccountManager;
import com.ker.moneydealer.Controllers.AccountWrapper;
import com.ker.moneydealer.Exceptions.DealException;
import com.ker.moneydealer.Guiceonfiguration.MoneyDealerConfiguration;
import com.ker.moneydealer.Models.Account;
import com.ker.moneydealer.Models.IAccount;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

@Path("/")
public class Endpoint {
    @Context
    ServletContext context;

    private AccountManager acctManager;
    private ObjectMapper mapper = new ObjectMapper();

    public Endpoint() {}

    private Response getIOExceptionResponse(String message) throws JsonProcessingException {
        MoneyDealerException ei = new MoneyDealerException("Payload can't be parsed", message);
        return Response.status(400).entity(mapper.writeValueAsString(ei)).build();

    }

    private Response getSqlErrorResponse(String message) throws JsonProcessingException {
        MoneyDealerException ei = new MoneyDealerException("Database problem occurred", message);
        return Response.status(500).entity(mapper.writeValueAsString(ei)).build();
    }

    private Response getIllegalArgumentResponse(int id, String message) throws JsonProcessingException {
        MoneyDealerException ei = new MoneyDealerException(String.format("Incorrect account id is provided: '%s'", id), message);
        return Response.status(400).entity(mapper.writeValueAsString(ei)).build();
    }

    private Response getDealErrorResponse(String message) throws JsonProcessingException {
        MoneyDealerException ei = new MoneyDealerException("Deal Failed", message);
        return Response.status(400).entity(mapper.writeValueAsString(ei)).build();
    }

    private void initAccountManager() throws SQLException {
        Object obj = context.getAttribute("accountManager");
        if (obj == null){
            Injector injector = Guice.createInjector(new MoneyDealerConfiguration());
            acctManager = injector.getInstance(AccountManager.class);
            acctManager.init_db();
            context.setAttribute("accountManager", acctManager);
        }
        else{
            acctManager = (AccountManager) obj;
        }
    }

    @GET
    @Path("hello")
    @Produces(MediaType.TEXT_HTML)
    public String sayPlainTextHello() {
        return "<h1>Hello!<h1>";
    }

    @GET
    @Path("/accounts")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllAccounts() throws JsonProcessingException {
        try {
            initAccountManager();
            return Response.status(200).entity(mapper.writeValueAsString(this.acctManager.getAllAccounts())).build();
        } catch (SQLException e) {
            return getSqlErrorResponse(e.getMessage());
        }
    }

    @GET
    @Path("/accounts/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccount(@PathParam("id") int id) throws JsonProcessingException {
        try {
            initAccountManager();
            return Response.status(200).entity(mapper.writeValueAsString(this.acctManager.getAccount(id).getAccount())).build();
        } catch (SQLException e) {
            return getSqlErrorResponse(e.getMessage());
        }
        catch (IllegalArgumentException ex){
            return  getIllegalArgumentResponse(id, ex.getMessage());
        }
    }
    
    @POST
    @Path("/accounts")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAccount(String json) throws JsonProcessingException {
        try {
            initAccountManager();
            IAccount account = mapper.readValue(json, Account.class);
            int id = this.acctManager.createAccount(account.getName(), account.getMoney());
            return Response.status(201).entity(String.valueOf(id)).build();
        }
        catch (IOException ex){
            return getIOExceptionResponse(ex.getMessage());
        }
        catch (SQLException ex) {
            return getSqlErrorResponse(ex.getMessage());
        }
    }

    @DELETE
    @Path("/accounts/{id}")
    public Response deleteAccount(@PathParam("id") Integer id) throws JsonProcessingException {
        try{
            initAccountManager();
            acctManager.deleteAccount(id);
            return Response.status(200).entity("Ok").build();
        }
        catch (IllegalArgumentException ex){
            return  getIllegalArgumentResponse(id, ex.getMessage());
        }
        catch (SQLException ex){
            return getSqlErrorResponse(ex.getMessage());
        }
    }

    @PUT
    @Path("/accounts/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateAccount(@PathParam("id") Integer id, String json) throws JsonProcessingException {
        try {
            initAccountManager();
            AccountWrapper acct = this.acctManager.getAccount(id);
            Account acctNew = mapper.readValue(json, Account.class);
            acct.getAccount().setName(acctNew.getName());
            acct.getAccount().setMoney(acctNew.getMoney());
            acct.getAccount().setBlocked(acctNew.isBlocked());
            this.acctManager.saveAccount(acct);
            return Response.status(200).entity("Ok").build();
        }catch (IllegalArgumentException ex){
            return getIllegalArgumentResponse(id, ex.getMessage());
        }catch (SQLException ex){
            return getSqlErrorResponse(ex.getMessage());
        }
        catch (IOException ex){
            return getIOExceptionResponse(ex.getMessage());
        }
    }

    @PUT
    @Path("/accounts/{id}/lock")
    public Response lockAccount(@PathParam("id") Integer id) throws JsonProcessingException {
        try {
            initAccountManager();
            this.acctManager.lock(id);
            return Response.status(200).entity("Ok").build();
        } catch (SQLException e) {
            return getSqlErrorResponse(e.getMessage());
        }
        catch (IllegalArgumentException ex){
            return getIllegalArgumentResponse(id, ex.getMessage());
        }
    }

    @PUT
    @Path("/accounts/{id}/unlock")
    public Response unlockAccount(@PathParam("id") Integer id) throws JsonProcessingException {
        try {
            initAccountManager();
            this.acctManager.unlock(id);
            return Response.status(200).entity("Ok").build();
        } catch (SQLException e) {
            return getSqlErrorResponse(e.getMessage());
        }
        catch (IllegalArgumentException ex){
            return getIllegalArgumentResponse(id, ex.getMessage());
        }

    }

    @POST
    @Path("/deal")
    public Response makeDeal(@QueryParam("sender") int sender, @QueryParam("receiver") int receiver,
                             @QueryParam("money") BigDecimal money) throws JsonProcessingException {
        try {
            initAccountManager();
            AccountDealer dealer = new AccountDealer(this.acctManager);
            dealer.deal(sender, receiver, money);
            return Response.status(200).entity("Ok").build();
        } catch (SQLException ex) {
            return getSqlErrorResponse(ex.getMessage());
        } catch (DealException ex) {
            return getDealErrorResponse(ex.getMessage());
        }

    }
}
