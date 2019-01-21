package com.pwnpub.search.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author soobeenwong
 * @date 2019-01-14 11:32 AM
 * @desc coinname based on xxx standard protocols
 */
@Component
@ConfigurationProperties(prefix = "coin")
public class CoinName {

    private String maincoin;
    private String erc20;
    private String maincoinName;
    private String erc20Name;

    public String getMaincoin() {
        return maincoin;
    }

    public void setMaincoin(String maincoin) {
        this.maincoin = maincoin;
    }

    public String getErc20() {
        return erc20;
    }

    public void setErc20(String erc20) {
        this.erc20 = erc20;
    }

    public String getMaincoinName() {
        return maincoinName;
    }

    public void setMaincoinName(String maincoinName) {
        this.maincoinName = maincoinName;
    }

    public String getErc20Name() {
        return erc20Name;
    }

    public void setErc20Name(String erc20Name) {
        this.erc20Name = erc20Name;
    }
}
