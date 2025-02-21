package com.smilepayz.india.bean;


public class TradeAdditionalReq {

    private String ifscCode;

    private String taxNumber;

    private String payerAccountNo;

    private String network;


    //------------------------------


    public void setNetwork(String network) {
        this.network = network;
    }

    public String getNetwork() {
        return network;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public String getTaxNumber() {
        return taxNumber;
    }

    public void setTaxNumber(String taxNumber) {
        this.taxNumber = taxNumber;
    }

    public String getPayerAccountNo() {
        return payerAccountNo;
    }

    public void setPayerAccountNo(String payerAccountNo) {
        this.payerAccountNo = payerAccountNo;
    }
}
