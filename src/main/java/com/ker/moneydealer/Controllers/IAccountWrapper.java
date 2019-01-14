package com.ker.moneydealer.Controllers;

import com.ker.moneydealer.Models.Account;
import com.ker.moneydealer.Models.IAccount;

import java.math.BigDecimal;

public interface IAccountWrapper {

    BigDecimal getCurrentBalance();
    void decreaseCurrentBalance(BigDecimal money);
    void increaseBalance(BigDecimal valueOf);
    void setAccount(IAccount account);
    boolean checkAvailability(BigDecimal dealAmount);
    boolean isLocked();

    Integer getAcctId();

    String getAcctName();

    IAccount getAccount();

    void lock();
    void unlock();
}
