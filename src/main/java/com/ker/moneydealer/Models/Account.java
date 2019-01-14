package com.ker.moneydealer.Models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.math.BigDecimal;

@DatabaseTable(tableName = "accounts")
public class Account implements IAccount{
    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField
    private String name;
    @DatabaseField
    private volatile BigDecimal money;
    @DatabaseField
    private Boolean blocked = false;

    public Account(){}

    public Account(Integer id, String name, BigDecimal initMoney){
        this.id = id;
        this.name = name;
        this.money = initMoney;
    }

    @Override
    public Integer getId() {
        return this.id;
    }
    @Override
    public String getName() {
        return name;
    }
    @Override
    public BigDecimal getMoney() {
        return money;
    }
    @Override
    public Boolean isBlocked() {
        return this.blocked;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
    @Override
    public void setMoney(BigDecimal money) {
        this.money = money;
    }
    @Override
    public void setBlocked(Boolean state) {
        this.blocked = state;
    }




}
