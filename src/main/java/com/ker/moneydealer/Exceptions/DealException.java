package com.ker.moneydealer.Exceptions;

public class DealException extends Exception{
    private String message;
    public DealException(String msg) {
        super(msg);
    }
}
