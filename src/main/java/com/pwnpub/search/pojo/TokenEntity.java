package com.pwnpub.search.pojo;

/**
 * @program BlockExplorer-Web
 * @author: joon.h
 * @create: 2019/06/24 14:58
 */
public class TokenEntity {

    private String tokenName;
    private String tokenNumber;
    private String tokenAddress;

    public TokenEntity(String tokenName, String tokenNumber, String tokenAddress) {
        this.tokenName = tokenName;
        this.tokenNumber = tokenNumber;
        this.tokenAddress = tokenAddress;
    }

    public String getTokenName() {
        return tokenName;
    }

    public String getTokenNumber() {
        return tokenNumber;
    }

    public String getTokenAddress() {
        return tokenAddress;
    }
}
