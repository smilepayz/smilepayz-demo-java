package com.smilepayz.v2.bean;

import java.util.List;

/**
 * @Author Moore
 * @Date 2024/7/2 10:42
 **/
public class InquiryBalanceReq {

    private String partnerReferenceNo;
    private String accountNo;
    private List<String> balanceTypes;

    public String getPartnerReferenceNo() {
        return partnerReferenceNo;
    }

    public void setPartnerReferenceNo(String partnerReferenceNo) {
        this.partnerReferenceNo = partnerReferenceNo;
    }

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
