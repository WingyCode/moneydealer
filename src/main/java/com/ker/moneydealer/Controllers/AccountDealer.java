package com.ker.moneydealer.Controllers;

import com.ker.moneydealer.Exceptions.DealException;

import java.math.BigDecimal;
import java.sql.SQLException;

public class AccountDealer {
    private AccountManager accountManager;

    public AccountDealer(AccountManager accountManager){
        this.accountManager = accountManager;
    }

    public void deal(Integer senderId, Integer receiverId, BigDecimal money) throws SQLException, DealException {
        synchronized (this) {
            AccountWrapper sender = this.accountManager.getAccount(senderId);
            AccountWrapper receiver = this.accountManager.getAccount(receiverId);
            this.accountManager.sendMoney(sender, receiver, money);
        }
    }

    public void deal(AccountWrapper sender, AccountWrapper reciever, BigDecimal money) throws SQLException, DealException {
        this.deal(sender.getAcctId(), reciever.getAcctId(), money);
    }
}
