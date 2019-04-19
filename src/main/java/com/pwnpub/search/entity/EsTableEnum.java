package com.pwnpub.search.entity;

/**
 * @author soobeenwong
 * @date 2019-04-18 3:02 PM
 * @desc Es
 */
public enum EsTableEnum {


    BLOCK ("block_new"),
    ERC20 ("erc20_new"),
    MAINCOIN ("maincoin_new"),
    TRANSACTION ("transaction_new"),
    ERC20TOKEN("erc20token_new"); //关于统计，待修改

    public String table;

    EsTableEnum(String table) {
        this.table = table;
    }

    @Override
    public String toString() {
        return table;
    }
}
