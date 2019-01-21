package com.pwnpub.search.pojo;

/**
 * @author soobeenwong
 * @date 2018-12-18 3:32 PM
 * @desc 重新定义交易实体类
 */
public class TransactionEntityAll {


    /**
     * blockHash : 0xaae3ff00c174f6736a370e60bf743227264d7b03232c1d57eb5f8ac525deb12d
     * blockNumber : 411807
     * blockNumberRaw : 0x6489f
     * chainId : 10
     * from : 0x16682e461be8927a541355363c49d7e5b471799b
     * gas : 43649
     * gasPrice : 4000000000
     * gasPriceRaw : 0xee6b2800
     * gasRaw : 0xaa81
     * hash : 0x297dd28a90820676699ffd7a5336e9a589fde83f8770a68980fef16d456d4a78
     * input : 0x095cf5c693fdf797a98f5d6c65f057592f1c3b99a001b049
     * nonce : 8
     * nonceRaw : 0x8
     * r : 0xd77d254640567bcf25d305ba97c1732e2836e5e5690ebb8e0a0b7a05f4ea9da0
     * s : 0x702320c6643c42f6682e7a339020fcec2d070b5c0e7491d744a93b8a4fe4a2d7
     * to : 0x93f71425f0b7e00f89d834d0729910de82cf0216
     * transactionIndex : 0
     * transactionIndexRaw : 0x0
     * v : 56
     * value : 0
     * valueRaw : 0x0
     */

    private String blockHash;
    private long blockNumber;
    private String blockNumberRaw;
    private int chainId;
    private String from;
    private long gas;
    private long gasPrice;
    private String gasPriceRaw;
    private String gasRaw;
    private String hash;
    private String input;
    private long nonce;
    private String nonceRaw;
    private String r;
    private String s;
    private String to;
    private long transactionIndex;
    private String transactionIndexRaw;
    private int v;
    private long value;
    private String valueRaw;

    private String timestamp;
    private String timestampDay;

    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public TransactionEntityAll(String timestamp, String timestampDay) {
        this.timestamp = timestamp;
        this.timestampDay = timestampDay;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimestampDay() {
        return timestampDay;
    }

    public void setTimestampDay(String timestampDay) {
        this.timestampDay = timestampDay;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public long getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(long blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getBlockNumberRaw() {
        return blockNumberRaw;
    }

    public void setBlockNumberRaw(String blockNumberRaw) {
        this.blockNumberRaw = blockNumberRaw;
    }

    public int getChainId() {
        return chainId;
    }

    public void setChainId(int chainId) {
        this.chainId = chainId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public long getGas() {
        return gas;
    }

    public void setGas(long gas) {
        this.gas = gas;
    }

    public long getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(long gasPrice) {
        this.gasPrice = gasPrice;
    }

    public String getGasPriceRaw() {
        return gasPriceRaw;
    }

    public void setGasPriceRaw(String gasPriceRaw) {
        this.gasPriceRaw = gasPriceRaw;
    }

    public String getGasRaw() {
        return gasRaw;
    }

    public void setGasRaw(String gasRaw) {
        this.gasRaw = gasRaw;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public long getNonce() {
        return nonce;
    }

    public void setNonce(long nonce) {
        this.nonce = nonce;
    }

    public String getNonceRaw() {
        return nonceRaw;
    }

    public void setNonceRaw(String nonceRaw) {
        this.nonceRaw = nonceRaw;
    }

    public String getR() {
        return r;
    }

    public void setR(String r) {
        this.r = r;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public long getTransactionIndex() {
        return transactionIndex;
    }

    public void setTransactionIndex(long transactionIndex) {
        this.transactionIndex = transactionIndex;
    }

    public String getTransactionIndexRaw() {
        return transactionIndexRaw;
    }

    public void setTransactionIndexRaw(String transactionIndexRaw) {
        this.transactionIndexRaw = transactionIndexRaw;
    }

    public int getV() {
        return v;
    }

    public void setV(int v) {
        this.v = v;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public String getValueRaw() {
        return valueRaw;
    }

    public void setValueRaw(String valueRaw) {
        this.valueRaw = valueRaw;
    }
}
