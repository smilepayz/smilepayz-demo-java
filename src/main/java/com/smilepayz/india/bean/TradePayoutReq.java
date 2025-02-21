package com.smilepayz.india.bean;


public class TradePayoutReq extends TradeReq {


    /**
     * paymentMethod
     */
    private String paymentMethod;

    /**
     * The withdrawal account number may be a bank card number, a wallet account number, or something else
     */
    private String cashAccount;

    /**
     * India Pay out to Bank ,11 digits
     */
    private String ifscCode;

    /**
     * payer info
     */
    private PayerReq payer;

    /**
     * receiver info
     */
    private ReceiverReq receiver;

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getCashAccount() {
        return cashAccount;
    }

    public void setCashAccount(String cashAccount) {
        this.cashAccount = cashAccount;
    }

    public PayerReq getPayer() {
        return payer;
    }

    public void setPayer(PayerReq payer) {
        this.payer = payer;
    }

    public ReceiverReq getReceiver() {
        return receiver;
    }

    public void setReceiver(ReceiverReq receiver) {
        this.receiver = receiver;
    }
}
