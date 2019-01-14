package com.ker.moneydealer;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.ker.moneydealer.Builder.AccountBuilder;
import com.ker.moneydealer.Controllers.AccountDealer;
import com.ker.moneydealer.Controllers.AccountManager;
import com.ker.moneydealer.Controllers.AccountWrapper;
import com.ker.moneydealer.Exceptions.DealException;
import com.ker.moneydealer.Guiceonfiguration.MoneyDealerConfiguration;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

public class TestDealer {
    private AccountWrapper acctWrapper1 = null;
    private AccountWrapper acctWrapper2 = null;
    private AccountDealer dealer = null;
    private AccountManager acctManager;

    @Before
    public void beforeEach() throws SQLException {
        Injector injector = Guice.createInjector(new MoneyDealerConfiguration());
        acctManager = injector.getInstance(AccountManager.class);
        acctManager.init_db();
        dealer = new AccountDealer(acctManager);
        acctWrapper1 = acctManager.getAccount(acctManager.createAccount("John Doe", BigDecimal.valueOf(1000.50)));
        acctWrapper2 = acctManager.getAccount(acctManager.createAccount("Doe Johnson", BigDecimal.valueOf(100.50)));
    }

    @Test
    public void TestSendCorrect() throws Exception {
        assertEquals(acctWrapper1.getCurrentBalance(), BigDecimal.valueOf(1000.5));
        assertEquals(acctWrapper2.getCurrentBalance(), BigDecimal.valueOf(100.5));
        try {
            dealer.deal(acctWrapper1, acctWrapper2, BigDecimal.valueOf(1000.0));
        } catch (Exception e) {
            throw e;
        }
        acctWrapper1 = acctManager.getAccount(1);
        acctWrapper2 = acctManager.getAccount(2);
        assertEquals(acctWrapper1.getCurrentBalance(), BigDecimal.valueOf(0.5));
        assertEquals(acctWrapper2.getCurrentBalance(), BigDecimal.valueOf(1100.5));

        try {
            dealer.deal(2, 1, BigDecimal.valueOf(500.5));
        } catch (Exception e) {}
        acctWrapper1 = acctManager.getAccount(1);
        acctWrapper2 = acctManager.getAccount(2);
        assertEquals(acctWrapper1.getCurrentBalance(), BigDecimal.valueOf(501.0));
        assertEquals(acctWrapper2.getCurrentBalance(), BigDecimal.valueOf(600.0));
    }

    @Test
    public void TestSendIncorrect() {
        try{
            dealer.deal(acctWrapper1, acctWrapper2, BigDecimal.valueOf(1500.0));
        }
        catch (Exception ex){
            assertEquals("Not enough money on the account 1", ex.getMessage());
        }
        assertEquals(acctWrapper1.getCurrentBalance(), BigDecimal.valueOf(1000.5));
        assertEquals(acctWrapper2.getCurrentBalance(), BigDecimal.valueOf(100.5));
    }

    @Test
    public void TestSendIncorrectFrom(){
        try{
            dealer.deal(3, 2, BigDecimal.valueOf(100.0));
        }
        catch (IllegalArgumentException ex){
            assertEquals("Account with id '3' doesn't exist", ex.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(acctWrapper1.getCurrentBalance(), BigDecimal.valueOf(1000.5));
        assertEquals(acctWrapper2.getCurrentBalance(), BigDecimal.valueOf(100.5));
    }

    @Test
    public void TestSendIncorrectTo(){
        try{
            dealer.deal(1, 3, BigDecimal.valueOf(100.0));
        }
        catch (Exception ex){
            assertEquals("Account with id '3' doesn't exist", ex.getMessage());
        }
        assertEquals(acctWrapper1.getCurrentBalance(), BigDecimal.valueOf(1000.5));
        assertEquals(acctWrapper2.getCurrentBalance(), BigDecimal.valueOf(100.5));
    }

    @Test
    public void TestSendIncorrectSend(){
        try{
            dealer.deal(acctWrapper2, acctWrapper1, BigDecimal.valueOf(-100.0));
        }
        catch (Exception ex){
            assertEquals("Deal amount is set incorrectly", ex.getMessage());
        }
        assertEquals(acctWrapper1.getCurrentBalance(), BigDecimal.valueOf(1000.5));
        assertEquals(acctWrapper2.getCurrentBalance(), BigDecimal.valueOf(100.5));
    }

    @Test
    public void TestSendNothing() throws Exception {
        dealer.deal(acctWrapper2, acctWrapper1, BigDecimal.valueOf(0.0));

        assertEquals(acctWrapper1.getCurrentBalance(), BigDecimal.valueOf(1000.5));
        assertEquals(acctWrapper2.getCurrentBalance(), BigDecimal.valueOf(100.5));
    }

    @Test
    public void TestSendSelf() throws Exception{
        dealer.deal(acctWrapper1, acctWrapper2, BigDecimal.valueOf(100.0));

        assertEquals(acctWrapper1.getCurrentBalance(), BigDecimal.valueOf(1000.5));
        assertEquals(acctWrapper2.getCurrentBalance(), BigDecimal.valueOf(100.5));
    }

    @Test /*(expected = Exception.class)*/
    public void testSendToBlocked() throws Exception {
        acctWrapper2.lock();
        try {
            dealer.deal(acctWrapper1, acctWrapper2, BigDecimal.valueOf(100.0));
        }
        catch (Exception ex) {
            assertEquals(ex.getMessage(), "Reciever is locked");
            assertEquals(acctWrapper1.getCurrentBalance(), BigDecimal.valueOf(1000.50));
            assertEquals(acctWrapper2.getCurrentBalance(), BigDecimal.valueOf(100.50));
        }
    }

    @Test
    public void testSendFromBlocked() {
        acctWrapper1.lock();
        try {
            dealer.deal(acctWrapper1, acctWrapper2, BigDecimal.valueOf(100.0));
        } catch (Exception ex) {
            assertEquals(ex.getMessage(), "Sender is locked");
            assertEquals(acctWrapper1.getCurrentBalance(), BigDecimal.valueOf(1000.50));
            assertEquals(acctWrapper2.getCurrentBalance(), BigDecimal.valueOf(100.50));
        }
    }

    @Test
    public void testMultiThreadDeals() throws InterruptedException, SQLException {
        for (int i=0; i<10; i++){
            new Thread(() -> {
                try {
                    dealer.deal(acctWrapper1, acctWrapper2, BigDecimal.valueOf(10.0));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }

        Thread.sleep(1000);
        acctWrapper1 = acctManager.getAccount(1);
        acctWrapper2 = acctManager.getAccount(2);
        assertEquals(acctWrapper1.getCurrentBalance(), BigDecimal.valueOf(900.5));
        assertEquals(acctWrapper2.getCurrentBalance(), BigDecimal.valueOf(200.5));
    }


}
