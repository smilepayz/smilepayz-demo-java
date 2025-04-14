package com.smilepayz.peru.bean;

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
     * address
     */
    private String address;

    /**
     * For Peru: DNI：ID Card, CE：Foreigner ID card,PAS：Passport,RUC：Taxpayer registration number
     * Foe Colombia: CC : Domestic documents,CE：Foreigner documents
     */
    private String idType;

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getIdType() {
        return idType;
    }
}
