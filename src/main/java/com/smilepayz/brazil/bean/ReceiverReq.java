package com.smilepayz.brazil.bean;

/**
 * receiver info
 */
public class ReceiverReq {

    /**
     * name
     */
    private String name;

    /**
     * email
     */
    private String email;

    /**
     * phone
     */
    private String phone;

    /**
     * taxNumber
     */
    private String taxNumber;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTaxNumber() {
        return taxNumber;
    }

    public void setTaxNumber(String taxNumber) {
        this.taxNumber = taxNumber;
    }
}
