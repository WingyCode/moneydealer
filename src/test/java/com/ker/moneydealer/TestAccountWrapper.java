package com.ker.moneydealer;

import com.ker.moneydealer.Builder.AccountBuilder;
import com.ker.moneydealer.Controllers.AccountWrapper;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class TestAccountWrapper
{
    private AccountWrapper acctWrapper;

    @Before
    public void beforeEach(){
        acctWrapper = AccountBuilder.create("Name Lastname", BigDecimal.valueOf(100.5));
    }

    @Test (expected = IllegalArgumentException.class)
    public  void testCreateIncorrectBalance(){
        AccountWrapper acct = AccountBuilder.create("test", BigDecimal.valueOf(-100));
    }

    @Test
    public void testCheckCurrentBalance(){
        assertEquals(BigDecimal.valueOf(100.5), acctWrapper.getCurrentBalance());
    }

    @Test
    public void testCheckAvailability(){
        assertTrue(acctWrapper.checkAvailability(BigDecimal.valueOf(100.45)));
        assertFalse(acctWrapper.checkAvailability(BigDecimal.valueOf(100.55)));
    }

    @Test
    public void testDecreaseBalanceCorrect(){
        acctWrapper.decreaseCurrentBalance(BigDecimal.valueOf(50.5));
        assertEquals(BigDecimal.valueOf(50.0), acctWrapper.getCurrentBalance());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDecreaseBalanceNotEnough(){
        acctWrapper.decreaseCurrentBalance(BigDecimal.valueOf(100.51));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDecreaseBalanceNegative(){
        acctWrapper.decreaseCurrentBalance(BigDecimal.valueOf(-100.00));
    }

    @Test
    public void testIncreaseBalance(){
        acctWrapper.increaseBalance(BigDecimal.valueOf(100.5));
        assertEquals(acctWrapper.getCurrentBalance(), BigDecimal.valueOf(201.0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIncreaseIncorrect(){
        acctWrapper.increaseBalance(BigDecimal.valueOf(-100.50));
    }

    @Test
    public void testIsBlocked(){
        assertFalse(acctWrapper.isLocked());
        acctWrapper.lock();
        assertTrue(acctWrapper.isLocked());
        acctWrapper.unlock();
    }

    // todo accuracy
//    @Test (expected = IllegalArgumentException.class)
//    public  void testCreateIncorrectAccount(){
//        Account acct = AccountBuilder.create("test", "test", BigDecimal.valueOf(100.001));
//        System.out.println(acct.getMoney());
//    }


}
