package com.ker.moneydealer.Builder;

import com.ker.moneydealer.Controllers.AccountWrapper;
import com.ker.moneydealer.Models.Account;

import java.math.BigDecimal;

public class AccountBuilder {
    private AccountBuilder(){
        throw new IllegalStateException("Utility class");
    }

    public static AccountWrapper create(String name, BigDecimal money){
        if (money.compareTo(BigDecimal.valueOf(0)) < 0) {
            throw new IllegalArgumentException("Money can't be less then 0");
        }
        AccountWrapper acctWrapper = new AccountWrapper();
        acctWrapper.setAccount(new Account(null, name, money));
        return acctWrapper;
    }
}
