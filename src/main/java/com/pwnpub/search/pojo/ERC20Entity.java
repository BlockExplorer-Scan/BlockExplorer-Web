package com.pwnpub.search.pojo;

import java.util.List;

/**
 *
 * @date 2018-12-29 11:29 AM
 * @desc ERC20 transfer
 */
public class ERC20Entity {


    /**
     * address : 0x70f4f4731f6473abc60a31dcc1e9b7b702e8b9c3
     * blockHash : 0x99b514ab159de07701d640bc2c82371d88edc53d3a10e2a9754e04882469fc4f
     * blockNumber : 1296417
     * blockNumberRaw : 0x13c821
     * data : 0x00000000000000000000000000000000000000000000000027f7d0bdb92000000000000000000000000000000000000000000000000000000000000000000060000000000000000000000000000000000000000000000000000000000000008000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000
     * logIndex : 4
     * logIndexRaw : 0x4
     * removed : false
     * topics : ["0x06b541ddaa720db2b10a4d0cdac39b8d360425fc073085fac19bc82614677987","0x0000000000000000000000008591b1c41ac0804d35fabd9b532001494aff0081","0x0000000000000000000000008591b1c41ac0804d35fabd9b532001494aff0081","0x00000000000000000000000043b7525973714ea6d6f4750366001d9cde4563ff"]
     * transactionHash : 0xc98f2d66f2975c2577dd53c7ede761dddfc2b35bce0621ecced4ba0f82bf17fa
     * transactionIndex : 1
     * transactionIndexRaw : 0x1
     */

    private String address;
    private String blockHash;
    private Integer blockNumber;
    private String blockNumberRaw;
    private String data;
    private int logIndex;
    private String logIndexRaw;
    private boolean removed;
    private String transactionHash;
    private int transactionIndex;
    private String transactionIndexRaw;
    private List<String> topics;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public Integer getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(Integer blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getBlockNumberRaw() {
        return blockNumberRaw;
    }

    public void setBlockNumberRaw(String blockNumberRaw) {
        this.blockNumberRaw = blockNumberRaw;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getLogIndex() {
        return logIndex;
    }

    public void setLogIndex(int logIndex) {
        this.logIndex = logIndex;
    }

    public String getLogIndexRaw() {
        return logIndexRaw;
    }

    public void setLogIndexRaw(String logIndexRaw) {
        this.logIndexRaw = logIndexRaw;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public int getTransactionIndex() {
        return transactionIndex;
    }

    public void setTransactionIndex(int transactionIndex) {
        this.transactionIndex = transactionIndex;
    }

    public String getTransactionIndexRaw() {
        return transactionIndexRaw;
    }

    public void setTransactionIndexRaw(String transactionIndexRaw) {
        this.transactionIndexRaw = transactionIndexRaw;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }
}
