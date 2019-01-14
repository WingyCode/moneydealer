package com.ker.moneydealer.Models;

import java.math.BigDecimal;

public interface IAccount {
    String id = "";
    String name = "";
    BigDecimal money = new BigDecimal(0.00f);
    Boolean blocked = false;

    Integer getId();
    String getName();
    BigDecimal getMoney();

    Boolean isBlocked();
    void setName(String name);
    void setMoney(BigDecimal money) ;
    void setBlocked(Boolean state);
}
