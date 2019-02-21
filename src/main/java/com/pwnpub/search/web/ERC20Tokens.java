package com.pwnpub.search.web;

import com.pwnpub.search.config.CoinName;
import com.pwnpub.search.utils.CommonUtils;
import com.pwnpub.search.utils.ResponseResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ExecutionException;

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

    private static final Logger logger = LogManager.getLogger(ERC20Tokens.class);

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

                if (hit.getSourceAsMap().get("timestamp") != null && CommonUtils.isNumeric0(hit.getSourceAsMap().get("timestamp").toString())) {
                    Long time = Long.parseLong(hit.getSourceAsMap().get("timestamp").toString()) / 1000;
                    hit.getSourceAsMap().put("timestamp", time);
                }

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
                map.put("statusName", coinName.getErc20Name());
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
    ) {

        /*Set<Object> set = new HashSet<>();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        boolQueryBuilder.must(QueryBuilders.matchQuery("status", coinName.getErc20()));

        SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch("erc20")
                .setTypes("data")
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                .addSort("blockNumber", SortOrder.DESC)
                .setQuery(boolQueryBuilder)
                 .setFrom(pageStart)
                .setSize(pageNum);

        for (SearchHit hit : searchRequestBuilder.get().getHits()) {
            Object address = hit.getSourceAsMap().get("address");
            set.add(address);

        }*/


        /**
         * 统计出共有多少代币
         */
        BoolQueryBuilder boolQueryBuilderToken = new BoolQueryBuilder();
        boolQueryBuilderToken.must(new TermQueryBuilder("status", "erc20"));
        //聚合处理
        SearchSourceBuilder sourceBuilderToken = new SearchSourceBuilder();
        TermsAggregationBuilder termsAggregationBuilderToken = AggregationBuilders.terms("group_token_count").field("address");
        sourceBuilderToken.aggregation(termsAggregationBuilderToken);
        sourceBuilderToken.query(boolQueryBuilderToken);
        //查询索引对象
        SearchRequest searchRequestToken = new SearchRequest("erc20");
        searchRequestToken.types("data");
        searchRequestToken.source(sourceBuilderToken);
        SearchResponse responseToken = null;
        try {
            responseToken = client.search(searchRequestToken).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Terms termsToken = responseToken.getAggregations().get("group_token_count");
        logger.info(" joon -- StorageJob - erc20 代币量 -- {}", termsToken.getBuckets().size());
        List<Object> list = new ArrayList<>();
        for (Terms.Bucket token : termsToken.getBuckets()) {
            Object key = token.getKey();
            list.add(key);
        }

        return ResponseResult.build(200, "query Token Tracker success", list);
    }

    //查询Holders总量
    @GetMapping("/queryERC20HoldersCountsTest")
    public ResponseResult queryERC20HoldersCountsTest() {

        Set<Object> set = new HashSet<>();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        boolQueryBuilder.must(QueryBuilders.matchQuery("status", coinName.getErc20()));

        SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch("erc20")
                .setTypes("data")
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                .setQuery(boolQueryBuilder)
                .setSize(10000);

        for (SearchHit hit : searchRequestBuilder.get().getHits()) {
            set.add(hit.getSourceAsMap().get("from"));

            set.add(hit.getSourceAsMap().get("to"));
        }
        long size = set.size();

        return ResponseResult.build(200, "query Holders Counts success", size);
    }

    //遍历Holders
    @GetMapping("/queryERC20Holders")
    public ResponseResult queryERC20Holders(
            @RequestParam(name = "contractAddress", required = true) String contractAddress,
            @RequestParam(name = "pageStart", required = false, defaultValue = "0") Integer pageStart,
            @RequestParam(name = "pageNum", required = false, defaultValue = "20") Integer pageNum
    ) {

        Web3j web3 = Web3j.build(new HttpService("http://n8.ledx.xyz"));

        BigInteger tokenTotalSupply = CommonUtils.getTokenTotalSupply(web3, contractAddress);
        BigDecimal tokenTotalSupply1 =new BigDecimal(tokenTotalSupply);

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

        SearchResponse searchResponse = searchRequestBuilder.get();

        for (SearchHit hit : searchResponse.getHits()) {
            set.add(hit.getSourceAsMap().get("from"));
            set.add(hit.getSourceAsMap().get("to"));
        }

        Iterator<Object> iterator = set.iterator();

        List<Map<String, Object>> list = new ArrayList<>();
        while (iterator.hasNext()) {
            String next = (String)iterator.next();
            BigInteger tokenBalance = CommonUtils.getTokenBalance(web3, next, contractAddress);
            BigDecimal tokenBalance1 =new BigDecimal(tokenBalance);
            BigDecimal divide = tokenBalance1.divide(tokenTotalSupply1, 6, BigDecimal.ROUND_HALF_UP);


            Map<String,Object> map = new HashMap<>();
            map.put("Address", next);
            map.put("Quantity", tokenBalance);
            map.put("Percentage", divide);
            list.add(map);

        }

        return ResponseResult.build(200, "query Holders success", list);
    }

    //hj
    //遍历Holders
    @GetMapping("/queryERC20HoldersCounts")
    public ResponseResult queryERC20HoldersCounts(
            //0x70f4f4731f6473abc60a31dcc1e9b7b702e8b9c3
            @RequestParam(name = "contractAddress", required = true) String contractAddress
    ) {

        try {

            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
            boolQueryBuilder.must(new TermQueryBuilder("status","erc20"));
            boolQueryBuilder.must(new TermQueryBuilder("address",contractAddress));
            //聚合处理
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("group_to_count").field("to");
            sourceBuilder.aggregation(termsAggregationBuilder);
            sourceBuilder.query(boolQueryBuilder);

            //查询索引对象
            SearchRequest searchRequest = new SearchRequest("erc20");
            searchRequest.types("data");
            searchRequest.source(sourceBuilder);
            SearchResponse response = client.search(searchRequest).get();

            Terms terms = response.getAggregations().get("group_to_count");

            logger.info("账户地址数量 -- {}",terms.getBuckets().size());

            return ResponseResult.build(200, "query Holders success", terms.getBuckets().size());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }



}
