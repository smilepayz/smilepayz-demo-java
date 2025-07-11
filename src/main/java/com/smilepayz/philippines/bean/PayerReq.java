package com.smilepayz.philippines.bean;


/**
 * payer info
 */
public class PayerReq {

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
     * address
     */
    private String address;

    /**
     * identity
     */
    private String identity;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }
}
