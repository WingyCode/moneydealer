package com.ker.moneydealer.Controllers;

import com.ker.moneydealer.Models.Account;
import com.ker.moneydealer.Models.IAccount;

import java.math.BigDecimal;

public class AccountWrapper implements IAccountWrapper {
    private IAccount acct = null;

    public AccountWrapper(){}

    public BigDecimal getCurrentBalance(){
        return acct.getMoney();
    }

    @Override
    public void decreaseCurrentBalance(BigDecimal money){
        if (money.compareTo(BigDecimal.valueOf(0)) < 0)
            throw  new IllegalArgumentException("Money can't be negative");
        if (this.getCurrentBalance().compareTo(money) < 0)
            throw new IllegalArgumentException(String.format("The current balance is less then %s", money));


        this.acct.setMoney(this.acct.getMoney().subtract(money));
    }

    @Override
    public void increaseBalance(BigDecimal money) {
        if (money.compareTo(BigDecimal.valueOf(0)) < 0){
            throw new IllegalArgumentException("Money can't be negative");
        }
        this.acct.setMoney(this.acct.getMoney().add(money));
    }

    @Override
    public void setAccount(IAccount account) {
        this.acct = account;
    }

    @Override
    public boolean checkAvailability(BigDecimal dealAmount) {
        if (this.getCurrentBalance().compareTo(dealAmount) < 0)
            return false;
        return true;
    }

    @Override
    public boolean isLocked() {
        return this.getAccount().isBlocked();
    }

    @Override
    public Integer getAcctId() {
        return acct.getId();
    }

    @Override
    public String getAcctName() {
        return this.acct.getName();
    }

    @Override
    public IAccount getAccount() {
        return this.acct;
    }

    @Override
    public void lock() {
        this.getAccount().setBlocked(true);
    }


    public void unlock() {
        this.getAccount().setBlocked(false);
    }
}
