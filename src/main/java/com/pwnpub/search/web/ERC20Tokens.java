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
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.InternalCardinality;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.web3j.protocol.Web3j;

import java.math.BigInteger;
import java.util.*;

import static com.pwnpub.search.entity.EsTableEnum.*;

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

    @Autowired
    ConfigurableApplicationContext configurableApplicationContext;

    @Autowired
    Web3j web3j;

    private static final Logger logger = LogManager.getLogger(ERC20Tokens.class);

    //Transfers 列表
    @GetMapping("/queryERC20TokenTransfers")
    public ResponseResult queryERC20TokenTransfers(
            @RequestParam(name = "contractAddress", required = true) String contractAddress,
            @RequestParam(name = "pageStart", required = false, defaultValue = "0") Integer pageStart,
            @RequestParam(name = "pageNum", required = false, defaultValue = "20") Integer pageNum

    ) {
        List<Map<String, Object>> list = new ArrayList<>();

        if (contractAddress != null) {

            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

            boolQueryBuilder.must(QueryBuilders.matchQuery("status", coinName.getErc20()));
            boolQueryBuilder.must(QueryBuilders.matchQuery("address", contractAddress));


            SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch(ERC20.toString())
                    .setTypes("data")
                    .setSearchType(SearchType.QUERY_THEN_FETCH)
                    .addSort("blockNumber", SortOrder.DESC)
                    .setQuery(boolQueryBuilder)
                    .setFrom(pageStart)
                    .setSize(pageNum);

            SearchResponse searchResponse = searchRequestBuilder.get();

            for (SearchHit hit : searchResponse.getHits()) {

                //根据address查询Erc20Token Name
                String tokenName = configurableApplicationContext.getEnvironment().getProperty(contractAddress);
                if(StringUtils.isEmpty(tokenName)){
                    //链上的币名
                    tokenName = CommonUtils.getTokenName(web3j, contractAddress);
                }

                hit.getSourceAsMap().put("statusName", tokenName);

                if (hit.getSourceAsMap().get("timestamp") != null && CommonUtils.isNumeric0(hit.getSourceAsMap().get("timestamp").toString())) {
                    Long time = Long.parseLong(hit.getSourceAsMap().get("timestamp").toString()) / 1000;
                    hit.getSourceAsMap().put("timestamp", time);
                }

                list.add(hit.getSourceAsMap());
            }

        }
        return ResponseResult.build(200, "query erc20 transfer datas success", list);
    }

    /**
     * Total Supply 代币发行总量   TransfersCount 转账总数
     * @param contractAddress
     * @return
     */
    @GetMapping("/queryERC20TokenCounts")
    public ResponseResult queryERC20TokenCounts(
            @RequestParam(name = "contractAddress", required = true) String contractAddress

    ) {
        Map<String, Object> map = new HashMap<>();

        if (contractAddress != null) {

            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

            boolQueryBuilder.must(QueryBuilders.matchQuery("status", coinName.getErc20()));
            boolQueryBuilder.must(QueryBuilders.matchQuery("address", contractAddress));


            SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch(ERC20.toString())
                    .setTypes("data")
                    .setSearchType(SearchType.QUERY_THEN_FETCH)
                    .addSort("blockNumber", SortOrder.DESC)
                    .setQuery(boolQueryBuilder);

            SearchResponse searchResponse = searchRequestBuilder.get();

            long totalHits = searchResponse.getHits().getTotalHits();
            map.put("Transfers", totalHits);


            try {
                BigInteger tokenTotalSupply = CommonUtils.getTokenTotalSupply(web3j, contractAddress);
                int decimals = CommonUtils.getTokenDecimals(web3j, contractAddress);
                map.put("TokenTotalSupply", tokenTotalSupply);
                map.put("decimals", decimals);

                String tokenName = configurableApplicationContext.getEnvironment().getProperty(contractAddress);
                if (StringUtils.isEmpty(tokenName)) {
                    //链上的币名
                    tokenName = CommonUtils.getTokenName(web3j, contractAddress);
                }

                map.put("statusName", tokenName);

            } catch (Exception e) {
                e.printStackTrace();
                return ResponseResult.build(201, "call web3j failed...");
            }

        }
        return ResponseResult.build(200, "query erc20 transfer datas success", map);
    }

    /**
     * 获取所有erc20的合约地址
     * @return
     */
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
         * 统计出共有多少代币并且遍历
         */
        BoolQueryBuilder boolQueryBuilderToken = new BoolQueryBuilder();
        boolQueryBuilderToken.must(new TermQueryBuilder("status", "erc20"));
        //聚合处理
        SearchSourceBuilder sourceBuilderToken = new SearchSourceBuilder();
        TermsAggregationBuilder termsAggregationBuilderToken =
                AggregationBuilders.terms("group_token_count").field("address").size(1000000);
        sourceBuilderToken.aggregation(termsAggregationBuilderToken);
        sourceBuilderToken.query(boolQueryBuilderToken);
        //查询索引对象
        SearchRequest searchRequestToken = new SearchRequest(ERC20.toString());
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
        Map<String, Object> map = new HashMap<>();
        for (Terms.Bucket token : termsToken.getBuckets()) {
            Object key = token.getKey();

            String tokenName = configurableApplicationContext.getEnvironment().getProperty(key.toString());
            if(StringUtils.isEmpty(tokenName)){
                //链上的币名
                tokenName = CommonUtils.getTokenName(web3j, key.toString());
            }
                map.put(key.toString(),tokenName);

//
//
//            if (key.equals(configurableApplicationContext.getEnvironment().getProperty(key.toString()))) {
//                map.put("CCB", key);
//            }
//            if (key.equals(configurableApplicationContext.getEnvironment().getProperty("coin.ETH"))) {
//                map.put("ETH", key);
//            }
//            if (key.equals(configurableApplicationContext.getEnvironment().getProperty("coin.GQB"))) {
//                map.put("GQB", key);
//            }
//            if (key.equals(configurableApplicationContext.getEnvironment().getProperty("coin.HDB"))) {
//                map.put("HDB", key);
//            }
//            if (key.equals(configurableApplicationContext.getEnvironment().getProperty("coin.ROSS"))) {
//                map.put("ROSS", key);
//            }
//            if (key.equals(configurableApplicationContext.getEnvironment().getProperty("coin.TIT"))) {
//                map.put("TIT", key);
//            }
//            if (key.equals(configurableApplicationContext.getEnvironment().getProperty("coin.UGB"))) {
//                map.put("UGB", key);
//            }

        }
        list.add(map);
        logger.info("  joon -- list size : {} ",list.size());
        return ResponseResult.build(200, "query Token Tracker success", list);
    }

    //查询Holders总量
    @GetMapping("/queryERC20HoldersCountsTest")
    public ResponseResult queryERC20HoldersCountsTest() {

        Set<Object> set = new HashSet<>();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        boolQueryBuilder.must(QueryBuilders.matchQuery("status", coinName.getErc20()));

        SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch(ERC20.toString())
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

    //遍历Holders   //出现统计问题
    @PostMapping("/queryERC20Holders")
    public ResponseResult queryERC20Holders(
            @RequestParam(name = "contractAddress") String contractAddress,
            @RequestParam(name = "timestamp") long timestamp,
            @RequestParam(name = "token") String token,
            @RequestParam(name = "sign") String sign,
            @RequestParam(name = "pageStart", required = false, defaultValue = "0") Integer pageStart,
            @RequestParam(name = "pageNum", required = false, defaultValue = "10") Integer pageNum
    ) {

        try {
            Date date = new Date();
            long outTime = date.getTime() + 30 * 1000 * 60;
            if (timestamp > outTime) {
                return ResponseResult.build(300, "error", "time out");
            }
            BoolQueryBuilder tokemBoolQueryBuilder = new BoolQueryBuilder();
            tokemBoolQueryBuilder.must(QueryBuilders.termQuery("token", token));
            //聚合处理
            SearchSourceBuilder tokenSourceBuilder = new SearchSourceBuilder();
            tokenSourceBuilder.query(tokemBoolQueryBuilder);

            //查询索引对象
            SearchRequest searchRequest = new SearchRequest("user");
            searchRequest.types("data");
            searchRequest.source(tokenSourceBuilder);

            SearchResponse response = this.client.search(searchRequest).get();
            long count = response.getHits().getTotalHits();

            if (count == 0) {
                return ResponseResult.build(300, "error", " token invalid ");
            }
            String signStr = "contractAddress=" + contractAddress + "&timestamp=" + timestamp + "&token=" + token;
            String signVerify = DigestUtils.md5DigestAsHex(signStr.getBytes());

            if(!sign.equals(signVerify)){
                return ResponseResult.build(300, "error", " sign invalid ");
            }

        /*
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

        return ResponseResult.build(200, "query Holders success", list); */

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        boolQueryBuilder.must(QueryBuilders.matchQuery("erc20name", contractAddress));

        SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch(ERC20TOKEN.toString())
                .setTypes("data")
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                .addSort("quantity", SortOrder.DESC)
                .setQuery(boolQueryBuilder)
                .setFrom(pageStart)
                .setSize(pageNum);

        SearchResponse searchResponse = searchRequestBuilder.get();

        List<Map<String, Object>> list = new ArrayList<>();

        for (SearchHit hit : searchResponse.getHits()) {

            Map<String, Object> map = new HashMap<>();

            map.put("Address", hit.getSourceAsMap().get("address"));
            map.put("Quantity", hit.getSourceAsMap().get("quantity"));
            map.put("Percentage", hit.getSourceAsMap().get("percentage"));
            list.add(map);
        }

            return ResponseResult.build(200, "query Holders success", list);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ResponseResult.build(300, "error", null);
    }

    /**
     *
     * hj--统计Holder数量
     * @param contractAddress
     * @return
     */
    @GetMapping("/queryERC20HoldersCounts")
    public ResponseResult queryERC20HoldersCounts(
            //0x70f4f4731f6473abc60a31dcc1e9b7b702e8b9c3
            @RequestParam(name = "contractAddress", required = true) String contractAddress
    ) {

        //hj：通过全部ERC20交易去统计holders实时数量
        try {

            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
            boolQueryBuilder.must(new TermQueryBuilder("status","erc20"));
            boolQueryBuilder.must(new TermQueryBuilder("address",contractAddress));
            //聚合处理
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            CardinalityAggregationBuilder cardinalityAggregationBuilder =
                    AggregationBuilders.cardinality("to_count").field("to").precisionThreshold(4000);
            sourceBuilder.aggregation(cardinalityAggregationBuilder);
            sourceBuilder.query(boolQueryBuilder);

            //查询索引对象
            SearchRequest searchRequest = new SearchRequest(ERC20.toString());
            searchRequest.types("data");
            searchRequest.source(sourceBuilder);
            SearchResponse response = client.search(searchRequest).get();

            InternalCardinality internalCardinality = response.getAggregations().get("to_count");

            logger.info("账户地址数量 -- {}",internalCardinality.getValue());

            return ResponseResult.build(200, "query HoldersCounts success", internalCardinality.getValue());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseResult.build(201, "query HoldersCounts failed");

        //soob：通过定时任务Job统计数据去获取holders数量
        /*try {

            BoolQueryBuilder query = QueryBuilders.boolQuery();
            query.must(QueryBuilders.matchQuery("erc20name", contractAddress));

            long totalHits = this.client.prepareSearch(ERC20TOKEN.toString())
                    .setTypes("data")
                    .setQuery(query)
                    .get()
                    .getHits()
                    .getTotalHits();
            return ResponseResult.build(200, "query HoldersCounts success",totalHits);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseResult.build(201,"query HoldersCounts failed");*/

    }


    /**
     *
     * @param contractAddress
     * @param pageStart
     * @param pageNum
     * @return
     * @time 2019-8-6
     */
    @GetMapping("/queryERC20ByTokenAddress")
    public ResponseResult queryERC20ByTokenAddress(
            @RequestParam(name = "contractAddress", required = true) String contractAddress,
            @RequestParam(name = "tokenAddress", required = true) String tokenAddress,
            @RequestParam(name = "pageStart", required = false, defaultValue = "0") Integer pageStart,
            @RequestParam(name = "pageNum", required = false, defaultValue = "25") Integer pageNum

    ) {

        logger.info("Get into queryERC20ByTokenAddress method");
        List<Map<String, Object>> list = new ArrayList<>();
        List<Map<String, Object>> list2 = new ArrayList<>();
        List<Object> list3 = new ArrayList<>();

        if (contractAddress != null) {

            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

            boolQueryBuilder.must(QueryBuilders.matchQuery("status", "erc20")); //???
            boolQueryBuilder.must(QueryBuilders.matchQuery("address", tokenAddress)); //同时匹配代币

            BoolQueryBuilder query = QueryBuilders.boolQuery();

            query.should(QueryBuilders.matchQuery("from", contractAddress));
            query.should(QueryBuilders.matchQuery("to", contractAddress));

            boolQueryBuilder.must(query);



            SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch(ERC20.toString())
                    .setTypes("data")
                    .setSearchType(SearchType.QUERY_THEN_FETCH)
                    .addSort("blockNumber", SortOrder.DESC)   //新添加
                    .setQuery(boolQueryBuilder)
                    .setFrom(pageStart)
                    .setSize(pageNum);

            SearchResponse searchResponse = searchRequestBuilder.get();
            long totalHits = searchResponse.getHits().getTotalHits();

            for (SearchHit hit : searchResponse.getHits()) {

                Object address = hit.getSourceAsMap().get("address");

                //从配置文件获取ERC20Token名称
                String tokenName = null;
                tokenName = configurableApplicationContext.getEnvironment().getProperty(address.toString());
                logger.info("从配置文件中读取到的币名是" + tokenName);
                if(StringUtils.isEmpty(tokenName)){
                    //链上的币名
                    try {
                        logger.info("通过请求web3j获取币名...");
                        tokenName = CommonUtils.getTokenName(web3j, address.toString());
                        logger.info("请求web3j获取到的币名是：" + tokenName);
                        hit.getSourceAsMap().put("statusName", tokenName);

                    } catch (Exception e) {
                        hit.getSourceAsMap().put("statusName", tokenName);
                        e.printStackTrace();
                        logger.info("通过请求web3j获取币名失败...");
                    }
                } else {
                    hit.getSourceAsMap().put("statusName", tokenName); //从配置文件中读取币名
                }

                list.add(hit.getSourceAsMap());
            }

            Map<String, Object> map = new HashMap<>();
            map.put("total", totalHits);
            list2.add(map);

        }

        list3.add(list);
        list3.add(list2);
        return ResponseResult.build(200, "query erc20 transfer datas success", list3);
    }

}
