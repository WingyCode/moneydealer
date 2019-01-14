package com.ker.moneydealer;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.ker.moneydealer.Controllers.AccountWrapper;
import com.ker.moneydealer.Controllers.AccountManager;
import com.ker.moneydealer.Controllers.IAccountManager;
import com.ker.moneydealer.Guiceonfiguration.MoneyDealerConfiguration;
import com.ker.moneydealer.Models.Account;
import org.junit.Before;
import org.junit.Test;

import javax.management.AttributeNotFoundException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class TestAccountManager {
    private IAccountManager acctManager;
    @Before
    public void beforeEach() throws SQLException {
        Injector injector = Guice.createInjector(new MoneyDealerConfiguration());
        acctManager = injector.getInstance(AccountManager.class);
        acctManager.init_db();
        acctManager.createAccount("John Doe", BigDecimal.valueOf(1000.50));
        acctManager.createAccount("Donald Johnson", BigDecimal.valueOf(100.00));
    }

    @Test
    public void testCreateAccount() throws SQLException {
        ArrayList<Account> accts = (ArrayList<Account>) acctManager.getAllAccounts();

        assertEquals(accts.size(), 2);
        assertEquals(accts.get(0).getId(), java.util.Optional.of(1).get());
        assertEquals(accts.get(0).getName(), "John Doe");
        assertEquals(accts.get(0).getMoney(), BigDecimal.valueOf(1000.50));
        assertEquals(accts.get(1).getId(), java.util.Optional.of(2).get());
        assertEquals(accts.get(1).getName(), "Donald Johnson");
        assertEquals(accts.get(1).getMoney(), BigDecimal.valueOf(100.00));
    }

    @Test
    public void testGetById() throws SQLException {
        AccountWrapper acct = acctManager.getAccount(1);
        assertEquals(acct.getAcctName(), "John Doe");
        assertEquals(acct.getCurrentBalance(), BigDecimal.valueOf(1000.50));
    }

    @Test (expected = IllegalArgumentException.class)
    public void testGetByIncorrectId() throws SQLException {
        AccountWrapper acct = acctManager.getAccount(3);
    }

    @Test
    public void testAccountsCount() throws SQLException {
        assertEquals(acctManager.getAccountsCount(), 2);
    }

    @Test
    public void testDeleteAccountById() throws SQLException, AttributeNotFoundException {
        acctManager.createAccount("test", BigDecimal.valueOf(100.00));
        assertEquals(acctManager.getAccountsCount(), 3);
        acctManager.deleteAccount(3);
        assertEquals(acctManager.getAccountsCount(), 2);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testDeleteIncorrectId() throws SQLException, AttributeNotFoundException {
        acctManager.deleteAccount(4);
    }

    @Test
    public void testChangeBalance() throws SQLException {
        AccountWrapper accountWrapper = acctManager.getAccount(2);
        accountWrapper.increaseBalance(BigDecimal.valueOf(100.00));
        acctManager.saveAccount(accountWrapper);
        accountWrapper = acctManager.getAccount(2);
        assertEquals(accountWrapper.getCurrentBalance(), BigDecimal.valueOf(200.00));
    }

    @Test
    public void testChangeLockState() throws SQLException {
        AccountWrapper acctWrapper = acctManager.getAccount(2);
        assertFalse(acctWrapper.isLocked());
        acctWrapper.lock();
        assertTrue(acctWrapper.isLocked());
        acctManager.saveAccount(acctWrapper);
        acctWrapper = acctManager.getAccount(2);
        assertTrue(acctWrapper.isLocked());
    }
}
