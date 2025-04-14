package com.smilepayz.peru.common;


public enum AreaEnum {

    INDONESIA(10, CurrencyEnum.IDR, 62),
    THAILAND(11, CurrencyEnum.THB, 66),
    INDIA(12, CurrencyEnum.INR, 91),
    BRAZIL(13, CurrencyEnum.BRL, 55),
    MEXICO(14, CurrencyEnum.MXN, 52),
    PERU(15, CurrencyEnum.PEN, 53),
    COLOMBIA(16, CurrencyEnum.COL, 54),
    CHILE(17, CurrencyEnum.CLP, 56),
    USDT(30, CurrencyEnum.USDT, 0);

    private final Integer code;
    private final CurrencyEnum currency;
    private final Integer countryId;


    AreaEnum(int code, CurrencyEnum currency, int countryId) {
        this.code = code;
        this.currency = currency;
        this.countryId = countryId;
    }

    public Integer getCode() {
        return code;
    }

    public CurrencyEnum getCurrency() {
        return currency;
    }

    public Integer getCountryId() {
        return countryId;
    }
}
