package com.ker.moneydealer.Controllers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.table.TableUtils;
import com.ker.moneydealer.Builder.AccountBuilder;
import com.ker.moneydealer.Exceptions.DealException;
import com.ker.moneydealer.Models.Account;
import com.ker.moneydealer.Models.IAccount;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

@Singleton
public class AccountManager implements IAccountManager {
    private JdbcConnectionSource connectionSource;
    private Dao<Account, String> accountDao;
    private String connectionString;

    @Inject
    public AccountManager(@Named("JdbcInMemory") String connectionString) {
        this.connectionString = connectionString;
    }

    @Override
    public void init_db() throws SQLException {
        connectionSource = new JdbcConnectionSource(this.connectionString);
        accountDao = DaoManager.createDao(connectionSource, Account.class);
        TableUtils.createTable(connectionSource, Account.class);
    }

    @Override
    public int createAccount(String name, BigDecimal balance) throws SQLException {
        AccountWrapper acct = AccountBuilder.create(name, balance);
        accountDao.create((Account)acct.getAccount());
        return acct.getAcctId();

    }

    @Override
    public List<Account> getAllAccounts() throws SQLException {
        return accountDao.queryForAll();
    }

    @Override
    public AccountWrapper getAccount(int id) throws SQLException {
        AccountWrapper acctWrapper = new AccountWrapper();
        IAccount acct = accountDao.queryForId(String.valueOf(id));
        if (acct == null) throw new IllegalArgumentException(String.format("Account with id '%s' doesn't exist", id));
        acctWrapper.setAccount((Account) acct);
        return acctWrapper;
    }

    @Override
    public Boolean accountExists(int id) throws SQLException {
        return accountDao.idExists(String.valueOf(id));
    }

    @Override
    public void saveAccount(AccountWrapper acct) throws SQLException{
        if (!this.accountExists(acct.getAcctId())){
            throw new IllegalArgumentException(String.format("Can not find an account with id '%s'", acct.getAcctId()));
        }
        accountDao.update((Account) acct.getAccount());
    }

    @Override
    public long getAccountsCount() throws SQLException {
        return accountDao.countOf();
    }

    @Override
    public void deleteAccount(int id) throws SQLException{
        if (!this.accountExists(id)){
            throw new IllegalArgumentException(String.format("Can not find an account with id '%s'", id));
        }

        accountDao.deleteById(String.valueOf(id));
    }

    @Override
    public void sendMoney(AccountWrapper sender, AccountWrapper reciever, BigDecimal money) throws SQLException, DealException{
        if (sender == null)
            throw new DealException("Sender is set incorrectly");

        if (reciever == null)
            throw new DealException("Reciever is set incorrectly");

        if (sender.isLocked())
            throw new DealException("Sender is locked");

        if (reciever.isLocked())
            throw new DealException("Reciever is locked");

        if (money.compareTo(BigDecimal.valueOf(0)) < 0)
            throw new DealException("Deal amount is set incorrectly");

        if (!sender.checkAvailability(money))
            throw new DealException(String.format("Not enough money on the account %s", sender.getAcctId()));
        TransactionManager.callInTransaction(this.connectionSource, ()-> {
                sender.decreaseCurrentBalance(money);
                reciever.increaseBalance(money);
                accountDao.update((Account) sender.getAccount());
                accountDao.update((Account) reciever.getAccount());
                return null;
            });

    }

    @Override
    public void lock(Integer id) throws SQLException {
        AccountWrapper acct = this.getAccount(id);
        acct.lock();
        accountDao.update((Account) acct.getAccount());
    }

    @Override
    public void unlock(Integer id) throws SQLException {
        AccountWrapper acct = this.getAccount(id);
        acct.unlock();
        accountDao.update((Account) acct.getAccount());
    }
}
