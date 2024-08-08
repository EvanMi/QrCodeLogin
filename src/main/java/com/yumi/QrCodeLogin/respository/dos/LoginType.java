package com.yumi.QrCodeLogin.respository.dos;

public enum LoginType {
    PC(0), MOBILE(1);
    private Integer code;

    LoginType(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public static LoginType fromCode(Integer code) {
        for (LoginType value : values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        throw new IllegalStateException("非法code");
    }
}
