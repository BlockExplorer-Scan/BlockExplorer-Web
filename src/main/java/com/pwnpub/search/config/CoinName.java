package com.pwnpub.search.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 *
 * @date 2019-01-14 11:32 AM
 * @desc coinname based on xxx standard protocols
 *
 * coin.HDB=0x70f4f4731f6473abc60a31dcc1e9b7b702e8b9c3
 * coin.CCB=0x8758c9b8ab0859519521672672564f9485974117
 * coin.TIT=0xd08530408d2f00d1c8dc4cb7636986e713162813
 * coin.ROSS=0xc1fe51a933d9bb15eeabadffd81918241121988c
 * coin.ETH=0x780a98e34611007737d40c433d1dd7459fd9b1e0
 * coin.GQB=0x0e2294163f0ccd4a016732e9cfddeb583cbfba13
 * coin.UGB=0xe53cea843853eb70d8d3cbf18e68ad388ab7d20f
 */
@Component
@ConfigurationProperties(prefix = "coin")
public class CoinName {

    private String maincoin;
    private String erc20;
    private String maincoinName;
    private String erc20Name;

    private String HDB;
    private String CCB;
    private String TIT;
    private String ROSS;
    private String ETH;
    private String GQB;
    private String UGB;

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

    public String getHDB() {
        return HDB;
    }

    public void setHDB(String HDB) {
        this.HDB = HDB;
    }

    public String getCCB() {
        return CCB;
    }

    public void setCCB(String CCB) {
        this.CCB = CCB;
    }

    public String getTIT() {
        return TIT;
    }

    public void setTIT(String TIT) {
        this.TIT = TIT;
    }

    public String getROSS() {
        return ROSS;
    }

    public void setROSS(String ROSS) {
        this.ROSS = ROSS;
    }

    public String getETH() {
        return ETH;
    }

    public void setETH(String ETH) {
        this.ETH = ETH;
    }

    public String getGQB() {
        return GQB;
    }

    public void setGQB(String GQB) {
        this.GQB = GQB;
    }

    public String getUGB() {
        return UGB;
    }

    public void setUGB(String UGB) {
        this.UGB = UGB;
    }
}
