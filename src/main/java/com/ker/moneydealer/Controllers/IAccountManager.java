package com.ker.moneydealer.Controllers;

import com.ker.moneydealer.Models.Account;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public interface IAccountManager {
    void init_db() throws SQLException;

    int createAccount(String name, BigDecimal balance) throws SQLException;

    List<Account> getAllAccounts() throws SQLException;

    AccountWrapper getAccount(int id) throws SQLException;

    Boolean accountExists(int id) throws SQLException;

    void saveAccount(AccountWrapper acct) throws SQLException;

    long getAccountsCount() throws SQLException;

    void deleteAccount(int id) throws SQLException;

    void sendMoney(AccountWrapper sender, AccountWrapper reciever, BigDecimal money) throws Exception;

    void lock(Integer id) throws SQLException;

    void unlock(Integer id) throws SQLException;
}
