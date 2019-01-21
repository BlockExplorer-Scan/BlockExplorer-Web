package com.pwnpub.search.pojo;

import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.util.List;

/**
 * @author soobeenwong
 * @date 2018-11-26 11:55 PM
 * @desc 未过滤的block
 */
public class BlockEntityAll {


    /**
     * block : {"difficulty":901002,"difficultyRaw":"0xdbf8a","extraData":"0xd983010812846765746888676f312e31312e328664617277696e","gasLimit":37805677,"gasLimitRaw":"0x240de6d","gasUsed":0,"gasUsedRaw":"0x0","hash":"0xf54c2b86ae08cc57100cc237cdd0206cef1493d4bcfb1e05e40a15959987743e","logsBloom":"0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000","miner":"0xe2aa8726de02a9cbaddbe52d5c93600e04c66286","mixHash":"0xc40044b756c071f3c0b5e2dd6c7355c4aa6003dc66f6ad805e9dc4eea7fc2877","nonce":8930227762357725265,"nonceRaw":"0x7bee8ad5e2a9e851","number":4844,"numberRaw":"0x12ec","parentHash":"0x44f5d6b7e024e38f574d3472068af05a3a37c4b8f80b828b98b136ced351a5f4","receiptsRoot":"0x56e81f171bcc55a6ff8345e692c0f86e5b48e01b996cadc001622fb5e363b421","sha3Uncles":"0x1dcc4de8dec75d7aab85b567b6ccd41ad312451b948a7413f0a142fd40d49347","size":540,"sizeRaw":"0x21c","stateRoot":"0x108b75a7099d53e0c0e78597ee93cf2e0bc71d9317605f20ff164ccc4cae8a58","timestamp":1543292820,"timestampRaw":"0x5bfcc794","totalDifficulty":2243942972,"totalDifficultyRaw":"0x85bfda3c","transactions":[],"transactionsRoot":"0x56e81f171bcc55a6ff8345e692c0f86e5b48e01b996cadc001622fb5e363b421","uncles":[]}
     * id : 106
     * jsonrpc : 2.0
     * result : {"$ref":"$.block"}
     */

    private BlockBean block;
    private int id;
    private String jsonrpc;
    private ResultBean result;

    public BlockBean getBlock() {
        return block;
    }

    public void setBlock(BlockBean block) {
        this.block = block;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class BlockBean {
        /**
         * difficulty : 901002
         * difficultyRaw : 0xdbf8a
         * extraData : 0xd983010812846765746888676f312e31312e328664617277696e
         * gasLimit : 37805677
         * gasLimitRaw : 0x240de6d
         * gasUsed : 0
         * gasUsedRaw : 0x0
         * hash : 0xf54c2b86ae08cc57100cc237cdd0206cef1493d4bcfb1e05e40a15959987743e
         * logsBloom : 0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000
         * miner : 0xe2aa8726de02a9cbaddbe52d5c93600e04c66286
         * mixHash : 0xc40044b756c071f3c0b5e2dd6c7355c4aa6003dc66f6ad805e9dc4eea7fc2877
         * nonce : 8930227762357725265
         * nonceRaw : 0x7bee8ad5e2a9e851
         * number : 4844
         * numberRaw : 0x12ec
         * parentHash : 0x44f5d6b7e024e38f574d3472068af05a3a37c4b8f80b828b98b136ced351a5f4
         * receiptsRoot : 0x56e81f171bcc55a6ff8345e692c0f86e5b48e01b996cadc001622fb5e363b421
         * sha3Uncles : 0x1dcc4de8dec75d7aab85b567b6ccd41ad312451b948a7413f0a142fd40d49347
         * size : 540
         * sizeRaw : 0x21c
         * stateRoot : 0x108b75a7099d53e0c0e78597ee93cf2e0bc71d9317605f20ff164ccc4cae8a58
         * timestamp : 1543292820
         * timestampRaw : 0x5bfcc794
         * totalDifficulty : 2243942972
         * totalDifficultyRaw : 0x85bfda3c
         * transactions : []
         * transactionsRoot : 0x56e81f171bcc55a6ff8345e692c0f86e5b48e01b996cadc001622fb5e363b421
         * uncles : []
         */

        private int difficulty;
        private String difficultyRaw;
        private String extraData;
        private int gasLimit;
        private String gasLimitRaw;
        private int gasUsed;
        private String gasUsedRaw;
        private String hash;
        private String logsBloom;
        private String miner;
        private String mixHash;
        private long nonce;
        private String nonceRaw;
        private int number;
        private String numberRaw;
        private String parentHash;
        private String receiptsRoot;
        private String sha3Uncles;
        private int size;
        private String sizeRaw;
        private String stateRoot;
        private int timestamp;
        private String timestampRaw;
        private long totalDifficulty;
        private String totalDifficultyRaw;
        private String transactionsRoot;
        private List<?> transactions;
        private List<?> uncles;

        public int getDifficulty() {
            return difficulty;
        }

        public void setDifficulty(int difficulty) {
            this.difficulty = difficulty;
        }

        public String getDifficultyRaw() {
            return difficultyRaw;
        }

        public void setDifficultyRaw(String difficultyRaw) {
            this.difficultyRaw = difficultyRaw;
        }

        public String getExtraData() {
            return extraData;
        }

        public void setExtraData(String extraData) {
            this.extraData = extraData;
        }

        public int getGasLimit() {
            return gasLimit;
        }

        public void setGasLimit(int gasLimit) {
            this.gasLimit = gasLimit;
        }

        public String getGasLimitRaw() {
            return gasLimitRaw;
        }

        public void setGasLimitRaw(String gasLimitRaw) {
            this.gasLimitRaw = gasLimitRaw;
        }

        public int getGasUsed() {
            return gasUsed;
        }

        public void setGasUsed(int gasUsed) {
            this.gasUsed = gasUsed;
        }

        public String getGasUsedRaw() {
            return gasUsedRaw;
        }

        public void setGasUsedRaw(String gasUsedRaw) {
            this.gasUsedRaw = gasUsedRaw;
        }

        public String getHash() {
            return hash;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        public String getLogsBloom() {
            return logsBloom;
        }

        public void setLogsBloom(String logsBloom) {
            this.logsBloom = logsBloom;
        }

        public String getMiner() {
            return miner;
        }

        public void setMiner(String miner) {
            this.miner = miner;
        }

        public String getMixHash() {
            return mixHash;
        }

        public void setMixHash(String mixHash) {
            this.mixHash = mixHash;
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

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getNumberRaw() {
            return numberRaw;
        }

        public void setNumberRaw(String numberRaw) {
            this.numberRaw = numberRaw;
        }

        public String getParentHash() {
            return parentHash;
        }

        public void setParentHash(String parentHash) {
            this.parentHash = parentHash;
        }

        public String getReceiptsRoot() {
            return receiptsRoot;
        }

        public void setReceiptsRoot(String receiptsRoot) {
            this.receiptsRoot = receiptsRoot;
        }

        public String getSha3Uncles() {
            return sha3Uncles;
        }

        public void setSha3Uncles(String sha3Uncles) {
            this.sha3Uncles = sha3Uncles;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public String getSizeRaw() {
            return sizeRaw;
        }

        public void setSizeRaw(String sizeRaw) {
            this.sizeRaw = sizeRaw;
        }

        public String getStateRoot() {
            return stateRoot;
        }

        public void setStateRoot(String stateRoot) {
            this.stateRoot = stateRoot;
        }

        public int getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(int timestamp) {
            this.timestamp = timestamp;
        }

        public String getTimestampRaw() {
            return timestampRaw;
        }

        public void setTimestampRaw(String timestampRaw) {
            this.timestampRaw = timestampRaw;
        }

        public long getTotalDifficulty() {
            return totalDifficulty;
        }

        public void setTotalDifficulty(long totalDifficulty) {
            this.totalDifficulty = totalDifficulty;
        }

        public String getTotalDifficultyRaw() {
            return totalDifficultyRaw;
        }

        public void setTotalDifficultyRaw(String totalDifficultyRaw) {
            this.totalDifficultyRaw = totalDifficultyRaw;
        }

        public String getTransactionsRoot() {
            return transactionsRoot;
        }

        public void setTransactionsRoot(String transactionsRoot) {
            this.transactionsRoot = transactionsRoot;
        }

        public List<?> getTransactions() {
            return transactions;
        }

        public void setTransactions(List<?> transactions) {
            this.transactions = transactions;
        }

        public List<?> getUncles() {
            return uncles;
        }

        public void setUncles(List<?> uncles) {
            this.uncles = uncles;
        }
    }

    public static class ResultBean {
        /**
         * $ref : $.block
         */

        private String $ref;

        public String get$ref() {
            return $ref;
        }

        public void set$ref(String $ref) {
            this.$ref = $ref;
        }
    }
}
