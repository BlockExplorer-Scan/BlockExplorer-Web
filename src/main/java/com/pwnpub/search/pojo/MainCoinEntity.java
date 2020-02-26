package com.pwnpub.search.pojo;

/**
 *
 * @date 2018-12-29 3:00 PM
 * @desc 主币内部转账
 */
public class MainCoinEntity {


    /**
     * from : 0x43b7525973714ea6d6f4750366001d9cde4563ff
     * input : 0x
     * to : 0x4b9df148250333ffa8e05f9435fc277c10fd2de0
     * transactionHash : 0xfcc46ecb259f6033afbee5ae0b0f2ec0b7d892d8510047b2a21bc0d17978ddc0
     * type : CALL
     * value : 0x2c51f24792b0000
     */

    private String from;
    private String input;
    private String to;
    private String transactionHash;
    private String type;
    private String value;
    private Integer blockNumber;

    public Integer getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(Integer blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
