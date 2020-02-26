package com.pwnpub.search.pojo;

/**
 * @program BlockExplorer-Web
 * @create: 2019/06/24 14:58
 */
public class TokenEntity {

    private String tokenName;
    private String tokenNumber;
    private String tokenAddress;
    private int decimals;

    public TokenEntity(String tokenName, String tokenNumber, String tokenAddress,int decimals) {
        this.tokenName = tokenName;
        this.tokenNumber = tokenNumber;
        this.tokenAddress = tokenAddress;
        this.decimals = decimals;
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

    public int getDecimals() {
        return decimals;
    }
}
