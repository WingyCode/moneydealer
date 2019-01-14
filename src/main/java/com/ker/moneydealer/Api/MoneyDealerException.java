package com.ker.moneydealer.Api;

public class MoneyDealerException {
    private final String errorMessage;
    private final String detailedMessage;

    public String getErrorMessage(){
        return this.errorMessage;
    }

    public String getDetailedMessage() {
        return detailedMessage;
    }

    MoneyDealerException(String errorMessage, String detailedMessage) {
        this.errorMessage = errorMessage;
        this.detailedMessage = detailedMessage;
    }

}
