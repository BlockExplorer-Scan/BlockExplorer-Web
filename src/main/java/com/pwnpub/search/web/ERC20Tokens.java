package com.pwnpub.search.web;

import com.pwnpub.search.config.CoinName;
import com.pwnpub.search.utils.CommonUtils;
import com.pwnpub.search.utils.ResponseResult;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.math.BigInteger;
import java.util.*;

/**
 * @author soobeenwong
 * @date 2019-01-26 7:27 PM
 * @desc ERC20 Top Tokens
 */
@RestController
@RequestMapping("/ERC20Tokens")
public class ERC20Tokens {

    @Autowired
    private TransportClient client;

    @Autowired
    private CoinName coinName;

    //Transfers 列表
    @GetMapping("/queryERC20TokenTransfers")
    public ResponseResult queryERC20ByContractAddress(
            @RequestParam(name = "contractAddress", required = true) String contractAddress,
            @RequestParam(name = "pageStart", required = false, defaultValue = "0") Integer pageStart,
            @RequestParam(name = "pageNum", required = false, defaultValue = "20") Integer pageNum

    ) {
        List<Map<String, Object>> list = new ArrayList<>();

        if (contractAddress != null) {

            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

            boolQueryBuilder.must(QueryBuilders.matchQuery("status", coinName.getErc20()));
            boolQueryBuilder.must(QueryBuilders.matchQuery("address", contractAddress));


            SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch("erc20")
                    .setTypes("data")
                    .setSearchType(SearchType.QUERY_THEN_FETCH)
                    .addSort("blockNumber", SortOrder.DESC)
                    .setQuery(boolQueryBuilder)
                    .setFrom(pageStart)
                    .setSize(pageNum);

            SearchResponse searchResponse = searchRequestBuilder.get();

            for (SearchHit hit : searchResponse.getHits()) {

                hit.getSourceAsMap().put("statusName", coinName.getErc20Name());
                Long time = Long.parseLong(hit.getSourceAsMap().get("timestamp").toString())/1000;
                hit.getSourceAsMap().put("timestamp",time);
                list.add(hit.getSourceAsMap());
            }

        }
        return ResponseResult.build(200, "query erc20 transfer datas success", list);
    }

    //Total Supply 代币发行总量   TransfersCount 转账总数
    @GetMapping("/queryERC20TokenCounts")
    public ResponseResult queryERC20TokenCounts(
            @RequestParam(name = "contractAddress", required = true) String contractAddress

    ) {
        Map<String, Object> map = new HashMap<>();

        if (contractAddress != null) {

            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

            boolQueryBuilder.must(QueryBuilders.matchQuery("status", coinName.getErc20()));
            boolQueryBuilder.must(QueryBuilders.matchQuery("address", contractAddress));


            SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch("erc20")
                    .setTypes("data")
                    .setSearchType(SearchType.QUERY_THEN_FETCH)
                    .addSort("blockNumber", SortOrder.DESC)
                    .setQuery(boolQueryBuilder);

            SearchResponse searchResponse = searchRequestBuilder.get();

            long totalHits = searchResponse.getHits().getTotalHits();
            map.put("Transfers", totalHits);

            Web3j web3 = null;
            try {
                web3 = Web3j.build(new HttpService("http://n8.ledx.xyz"));
                BigInteger tokenTotalSupply = CommonUtils.getTokenTotalSupply(web3, contractAddress);
                int decimals = CommonUtils.getTokenDecimals(web3, contractAddress);
                map.put("TokenTotalSupply", tokenTotalSupply);
                map.put("decimals", decimals);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseResult.build(201, "call web3j failed...");
            }

        }
        return ResponseResult.build(200, "query erc20 transfer datas success", map);
    }

    //获取所有erc20的合约地址
    @GetMapping("/queryERC20Contracts")
    public ResponseResult queryERC20TokenContract(
            @RequestParam(name = "pageStart", required = false, defaultValue = "0") Integer pageStart,
            @RequestParam(name = "pageNum", required = false, defaultValue = "20") Integer pageNum
    ) {

        Set<Object> set = new HashSet<>();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        boolQueryBuilder.must(QueryBuilders.matchQuery("status", coinName.getErc20()));

        SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch("erc20")
                .setTypes("data")
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                .addSort("blockNumber", SortOrder.DESC)
                .setQuery(boolQueryBuilder)
                .setFrom(pageStart)
                .setSize(pageNum);

        for (SearchHit hit : searchRequestBuilder.get().getHits()){
            Object address = hit.getSourceAsMap().get("address");
            set.add(address);

        }

        return ResponseResult.build(200, "query Token Tracker success", set);
    }

    //查询Holders总量
    @GetMapping("/queryERC20HoldersCounts")
    public ResponseResult queryERC20TokenContract() {

        Set<Object> set = new HashSet<>();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        boolQueryBuilder.must(QueryBuilders.matchQuery("status", coinName.getErc20()));

        SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch("erc20")
                .setTypes("data")
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                .setQuery(boolQueryBuilder);

        for (SearchHit hit : searchRequestBuilder.get().getHits()){
            set.add(hit.getSourceAsMap().get("from"));
            set.add(hit.getSourceAsMap().get("to"));
        }
        long size = set.size();

        return ResponseResult.build(200, "query Holders Counts success", size);
    }




}
