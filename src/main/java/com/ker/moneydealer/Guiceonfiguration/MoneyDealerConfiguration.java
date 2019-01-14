package com.ker.moneydealer.Guiceonfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.ker.moneydealer.Controllers.AccountManager;
import com.ker.moneydealer.Controllers.IAccountManager;

public class MoneyDealerConfiguration extends AbstractModule {
    @Override
    protected void configure() {
        bind(String.class).annotatedWith(Names.named("JdbcInMemory")).toInstance("jdbc:sqlite::memory:");
        bind(IAccountManager.class).to(AccountManager.class).in(Singleton.class);
    }
}
