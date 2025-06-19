package com.smilepayz.philippines.bean;

import java.util.List;

/**
 * @Author Moore
 * @Date 2024/7/2 10:42
 **/
public class InquiryBalanceReq {

    private String accountNo;
    private List<String> balanceTypes;


    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public List<String> getBalanceTypes() {
        return balanceTypes;
    }

    public void setBalanceTypes(List<String> balanceTypes) {
        this.balanceTypes = balanceTypes;
    }
}
