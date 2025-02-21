package com.smilepayz.thailand.bean;

/**
 * @Author Moore
 * @Date 2024/7/2 10:42
 **/
public class InquiryOrderStatsuReq {

    private Integer tradeType;
    private String tradeNo;

    private String orderNo;


    public Integer getTradeType() {
        return tradeType;
    }

    public void setTradeType(Integer tradeType) {
        this.tradeType = tradeType;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
}
