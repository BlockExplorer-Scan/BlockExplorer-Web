package com.pwnpub.search.web;

import com.pwnpub.search.config.CoinName;
import com.pwnpub.search.utils.CommonUtils;
import com.pwnpub.search.utils.ResponseResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetCode;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.utils.Convert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.pwnpub.search.entity.EsTableEnum.*;

/**
 *
 * @date 2018-11-02 8:39 PM
 * @desc 搜索接口
 */
@RestController
//@RequestMapping("Search")
public class SearchController {

    @Autowired
    private TransportClient client;

    @Autowired
    private CoinName coinName;

    @Autowired
    ConfigurableApplicationContext configurableApplicationContext;

    @Autowired
    Web3j web3j;

    private static final Logger logger = LogManager.getLogger(SearchController.class);

    private final int Seconds = 86400000;

    @GetMapping("/index")
    public String index(HttpServletRequest request, HttpServletResponse response) {

        return "index";
    }

    @GetMapping("/get/allblock/data")
    public ResponseEntity get(@RequestParam(name = "id", defaultValue = "") String id) {

        GetResponse result = this.client.prepareGet(BLOCK.toString(), "data", id).get();

        if (!result.isExists()) {

            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity(result.getSource(), HttpStatus.OK);

    }

    @GetMapping("/query/block/data")
    public ResponseResult queryBlock(
            @RequestParam(name = "pageStart", required = false, defaultValue = "0") Integer pageStart,
            @RequestParam(name = "pageNum", required = false, defaultValue = "10") Integer pageNum

    ) {
        logger.info("get into query/block/data");
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch(BLOCK.toString())
                .setTypes("data")
                .addSort("number", SortOrder.DESC)
                .setSearchType(SearchType.QUERY_THEN_FETCH) //小数量查询
                .setQuery(boolQueryBuilder)
                .setFrom(pageStart)
                .setSize(pageNum);

        SearchResponse searchResponse = searchRequestBuilder.get();

        List<Map<String, Object>> list = new ArrayList<>();

        for (SearchHit hit : searchResponse.getHits()) {

            String maincoinName = configurableApplicationContext.getEnvironment().getProperty("maincoinName");
            hit.getSourceAsMap().put("maincoinName", maincoinName);

            String blockReward = configurableApplicationContext.getEnvironment().getProperty("blockReward");
            hit.getSourceAsMap().put("blockReward", blockReward);

            list.add(hit.getSourceAsMap());
        }

        return ResponseResult.build(200, "query all block datas success", list);
    }

    @GetMapping("/queryBlockByValue")
    public ResponseResult queryBlockByNum(
            @RequestParam(name = "hash", required = false) String hash,
            @RequestParam(name = "number", required = false) Integer number

    ) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if (number != null) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("number", number));
        }

        if (hash != null) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("hash", hash));
        }


        SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch(BLOCK.toString())
                .setTypes("data")
                .setSearchType(SearchType.QUERY_THEN_FETCH) //小数量查询
                .setQuery(boolQueryBuilder);

        SearchResponse searchResponse = searchRequestBuilder.get();

        List<Map<String, Object>> list = new ArrayList<>();

        for (SearchHit hit : searchResponse.getHits()) {
            String maincoinName = configurableApplicationContext.getEnvironment().getProperty("maincoinName");
            hit.getSourceAsMap().put("maincoinName", maincoinName);

            String blockReward = configurableApplicationContext.getEnvironment().getProperty("blockReward");
            hit.getSourceAsMap().put("blockReward", blockReward);

            list.add(hit.getSourceAsMap());

        }

        return ResponseResult.build(200, "query specific block success...", list);
    }

    @GetMapping("/query/transaction/data")
    public ResponseResult queryTransaction(
            @RequestParam(name = "pageStart", required = false, defaultValue = "0") Integer pageStart,
            @RequestParam(name = "pageNum", required = false, defaultValue = "10") Integer pageNum

    ) {

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch(TRANSACTION.toString())
                .setTypes("data")
                .addSort("blockNumber", SortOrder.DESC)
                .setSearchType(SearchType.QUERY_THEN_FETCH) //大数量查询
                .setQuery(boolQueryBuilder)
                .setFrom(pageStart)
                .setSize(pageNum);


        SearchResponse searchResponse = searchRequestBuilder.get();

        List<Map<String, Object>> list = new ArrayList<>();

        for (SearchHit hit : searchResponse.getHits()) {
            String maincoinName = configurableApplicationContext.getEnvironment().getProperty("maincoinName");
            hit.getSourceAsMap().put("maincoinName", maincoinName);

            list.add(hit.getSourceAsMap());

        }

        return ResponseResult.build(200, "query all transaction datas success", list);
    }

    /**
     *
     * @param hash
     * @param blockNumber
     * @param miner
     * @param status
     * @param to
     * @param from
     * @param pageStart
     * @param pageNum
     * @param address type=from or to, default address
     * @return
     */
    //分页 0-25    添加统计数量
    @GetMapping("/queryTransactionByValue")
    public ResponseResult queryTransactionByValue(
            @RequestParam(name = "hash", required = false) String hash,
            @RequestParam(name = "blockNumber", required = false) Integer blockNumber,
            @RequestParam(name = "miner", required = false) String miner,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "to", required = false) String to,
            @RequestParam(name = "from", required = false) String from,
            @RequestParam(name = "pageStart", required = false, defaultValue = "0") Integer pageStart,
            @RequestParam(name = "pageNum", required = false, defaultValue = "25") Integer pageNum,
            @RequestParam(name = "address", required = false) String address


    ) {
        logger.info("get into queryTransactionByValue , 【Paging query】");
        List<Map<String, Object>> list = new ArrayList<>();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if (hash != null) {
            logger.info("参数：hash");
            boolQueryBuilder.must(QueryBuilders.matchQuery("hash", hash));
        }

        if (blockNumber != null) {
            logger.info("参数：blockNumber");
            boolQueryBuilder.must(QueryBuilders.matchQuery("blockNumber", blockNumber));
        }

        if (miner != null) {
            logger.info("参数：miner");
            boolQueryBuilder.must(QueryBuilders.matchQuery("from", miner));
        }

        if (status != null) {
            logger.info("参数：status");
            boolQueryBuilder.must(QueryBuilders.matchQuery("status", status));
        }

        if (to != null) {
            logger.info("参数：to");
            BoolQueryBuilder query = QueryBuilders.boolQuery();
            query.should(QueryBuilders.matchQuery("to", to));
            query.should(QueryBuilders.matchQuery("from", to));
            boolQueryBuilder.must(query);

        }

        if (from != null) {
            logger.info("参数：from");
            BoolQueryBuilder q = QueryBuilders.boolQuery();
            q.should(QueryBuilders.matchQuery("from", from));
            q.should(QueryBuilders.matchQuery("to", from));
            boolQueryBuilder.must(q);
        }

        if (address != null) {
            logger.info("参数：address （from or to）");
            BoolQueryBuilder q = QueryBuilders.boolQuery();
            q.should(QueryBuilders.matchQuery("from", address));
            q.should(QueryBuilders.matchQuery("to", address));
            boolQueryBuilder.must(q);

        }

        SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch(TRANSACTION.toString())
                .setTypes("data")
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                .setQuery(boolQueryBuilder)
                .addSort("blockNumber", SortOrder.DESC)
                .setFrom(pageStart)
                .setSize(pageNum);

        SearchResponse searchResponse = searchRequestBuilder.get();
        long totalHits = searchResponse.getHits().getTotalHits();

        for (SearchHit hit : searchResponse.getHits()) {

            //优化返回值
            Object valueStr = hit.getSourceAsMap().get("valueStr");
            if (valueStr != null && !"".equals(valueStr)) {
                hit.getSourceAsMap().put("value", valueStr);   //返回值是否覆盖了value   是

                String maincoinName = configurableApplicationContext.getEnvironment().getProperty("maincoinName");
                hit.getSourceAsMap().put("maincoinName", maincoinName);

                list.add(hit.getSourceAsMap());

            } else {
                String maincoinName = configurableApplicationContext.getEnvironment().getProperty("maincoinName");
                hit.getSourceAsMap().put("maincoinName", maincoinName);

                list.add(hit.getSourceAsMap());

            }
        }

        return ResponseResult.build(200, "query specific transaction success...", list);
    }

    /**
     * 通过交易哈希地址，查询主币内部转账消息（详细交易页面）
     * @param hash
     * @return
     */
    @GetMapping("/queryTransactionByContract")
    public ResponseResult queryTransactionByContract(@RequestParam(name = "hash", required = true) String hash){

        List<Map<String, Object>> list = new ArrayList();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if (hash != null) {
            logger.info("参数：hash");
            boolQueryBuilder.must(QueryBuilders.matchQuery("transactionHash", hash));
        }
        try {
            SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch(MAINCOIN.toString())
                    .setTypes("data")
                    .setSearchType(SearchType.QUERY_THEN_FETCH)
                    .setQuery(boolQueryBuilder);

            SearchResponse searchResponse = searchRequestBuilder.get();

            for (SearchHit hit : searchResponse.getHits()) {
                list.add(hit.getSourceAsMap());
            }

            return ResponseResult.build(200, "get maincoin transfer success.", list);
        } catch (Exception e) {
            logger.info(e.getMessage());
            return ResponseResult.build(201, "get maincoin transfer failed.");
        }

    }

    @GetMapping("/query/transaction/counts")
    public ResponseResult queryTransactionCounts() {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        long currentTimeMillis = System.currentTimeMillis();

        List list = new ArrayList<>();

        Map<String, Long> map = new TreeMap<>();

        for (int i = 1; i < 15; i++) {

            long latestTime = currentTimeMillis - Seconds * i;

            String latestDay = formatter.format(latestTime);
            long latestCounts = getTransactionCounts(latestDay);

            map.put(latestDay, latestCounts);

        }

        list.add(map);

        return ResponseResult.build(200, "query transaction counts success...", list);
    }

    public long getTransactionCounts(String currentDay) {

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        boolQueryBuilder.must(QueryBuilders.matchQuery("timestampDay", currentDay));

        SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch(TRANSACTION.toString())
                .setTypes("data")
                .setSearchType(SearchType.QUERY_THEN_FETCH) //大数量查询
                .setQuery(boolQueryBuilder);

        SearchResponse searchResponse = searchRequestBuilder.get();
        long currentCounts = searchResponse.getHits().getTotalHits();

        return currentCounts;
    }

    //analyze erc20 event   //存在重复
    @GetMapping("/queryERC20ByTransaction")
    public ResponseResult queryERC20ByTransaction(
            @RequestParam(name = "transactionHash", required = true) String transactionHash

    ) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if (transactionHash != null) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("transactionHash", transactionHash));
        }

        SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch(ERC20.toString())
                .setTypes("data")

                .setSearchType(SearchType.QUERY_THEN_FETCH) //小数量查询
                .setQuery(boolQueryBuilder).setSize(1000);

        SearchResponse searchResponse = searchRequestBuilder.get();

        List<Map<String, Object>> list = new ArrayList<>();

        for (SearchHit hit : searchResponse.getHits()) {

            Object status = hit.getSourceAsMap().get("status");
            if (status.equals("erc20")) {
                Object address = hit.getSourceAsMap().get("address");
                String tokenName = configurableApplicationContext.getEnvironment().getProperty(address.toString());
                if(StringUtils.isEmpty(tokenName)){
                    //链上的币名
                    tokenName = CommonUtils.getTokenName(web3j, address.toString());
                    hit.getSourceAsMap().put("statusName", tokenName);

                    list.add(hit.getSourceAsMap());
                } else {
                    hit.getSourceAsMap().put("statusName", tokenName);
                    list.add(hit.getSourceAsMap());
                }

            //status：other token
            } else {
                list.add(hit.getSourceAsMap());
            }

        }

        return ResponseResult.build(200, "query erc20 transfer datas success", list);
    }


    //查询erc20 所有转账记录  分页
    @GetMapping("/queryERC20ByContractAddress")
    public ResponseResult queryERC20ByContractAddress(
            @RequestParam(name = "contractAddress", required = true) String contractAddress,
            @RequestParam(name = "pageStart", required = false, defaultValue = "0") Integer pageStart,
            @RequestParam(name = "pageNum", required = false, defaultValue = "25") Integer pageNum

    ) {

        logger.info("Get into queryERC20ByContractAddress method");
        List<Map<String, Object>> list = new ArrayList<>();
        List<Map<String, Object>> list2 = new ArrayList<>();
        List<Object> list3 = new ArrayList<>();

        if (contractAddress != null) {

            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

            boolQueryBuilder.must(QueryBuilders.matchQuery("status", "erc20")); //???

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

    //主币内部转账记录
    @GetMapping("/queryMainCoinByContractAddress")
    public ResponseResult queryMainCoinByContractAddress(
            @RequestParam(name = "contractAddress", required = true) String contractAddress,
            @RequestParam(name = "pageStart", required = false, defaultValue = "0") Integer pageStart,
            @RequestParam(name = "pageNum", required = false, defaultValue = "25") Integer pageNum

    ) {
        List<Map<String, Object>> list = new ArrayList<>();
        List<Map<String, Object>> list2= new ArrayList<>();
        List<Object> list3= new ArrayList<>();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if (contractAddress != null) {

            BoolQueryBuilder q = QueryBuilders.boolQuery();
            q.should(QueryBuilders.matchQuery("from", contractAddress));
            q.should(QueryBuilders.matchQuery("to", contractAddress));

            boolQueryBuilder.must(q);

            SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch(MAINCOIN.toString())
                    .setTypes("data")
                    .addSort("blockNumber", SortOrder.DESC)
                    .setSearchType(SearchType.QUERY_THEN_FETCH)
                    .setQuery(boolQueryBuilder)
                    .setFrom(pageStart)
                    .setSize(pageNum);


            SearchResponse searchResponse = searchRequestBuilder.get();
            long totalHits = searchResponse.getHits().getTotalHits();

            for (SearchHit hit : searchResponse.getHits()) {
                list.add(hit.getSourceAsMap());
            }
            Map<String, Object> map = new HashMap<>();
            map.put("total", totalHits);
            list2.add(map);

        }
        list3.add(list);
        list3.add(list2);


        /* if (contractAddress != null) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("from", contractAddress));

            SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch(MAINCOIN.toString())
                    .setTypes("data")
                    .addSort("blockNumber", SortOrder.DESC)
                    .setSearchType(SearchType.QUERY_THEN_FETCH)
                    .setQuery(boolQueryBuilder);


            SearchResponse searchResponse = searchRequestBuilder.get();

            for (SearchHit hit : searchResponse.getHits()) {
                list.add(hit.getSourceAsMap());
            }
        } */

        return ResponseResult.build(200, "query maincoin transfer datas success", list3);
    }

    //统计交易数量
    @GetMapping("/queryTxsCounts")
    public ResponseResult queryTxsCounts(
            @RequestParam(name = "address", required = true) String address


    ) {

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if (address != null) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("from", address));
            boolQueryBuilder.should(QueryBuilders.matchQuery("to", address));
        }

        SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch(TRANSACTION.toString())
                .setTypes("data")
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                .setQuery(boolQueryBuilder);
        SearchResponse searchResponse = searchRequestBuilder.get();
        long totalHits = searchResponse.getHits().getTotalHits();

        Map<String, Object> map = new HashMap<>();
        map.put("txns", totalHits);
        map.put("value", 100);

        String tokenName = configurableApplicationContext.getEnvironment().getProperty("maincoinName");
        map.put("maincoinName", tokenName);
        //获取余额

        Web3ClientVersion web3ClientVersion;
        try {
            web3ClientVersion = web3j.web3ClientVersion().send();
            String clientVersion = web3ClientVersion.getWeb3ClientVersion();
            logger.info("私链的版本是：" + clientVersion);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("节点连接失败！");
        }
        //获取余额
        try {
            EthGetBalance ethGetBalance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
            if (ethGetBalance != null) {
                // 打印账户余额
                System.out.println(ethGetBalance.getBalance());
                // 将单位转为以太，方便查看
                BigDecimal balanceETH = Convert.fromWei(ethGetBalance.getBalance().toString(), Convert.Unit.ETHER);
                map.put("balance", balanceETH);
            } else {
                map.put("balance", 0);
            }
           EthGetCode ethGetCode =  web3j.ethGetCode(address, DefaultBlockParameterName.LATEST).send();
           map.put("code",ethGetCode.getCode());
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("获取主币余额失败...");
        }

        return ResponseResult.build(200, "query specific transaction success...", map);
    }

    //全局搜索   匹配长度  测试
    @GetMapping("/search")
    public ResponseResult search(@RequestParam(name = "data", required = true) String data) {
        try {
            if (!StringUtils.isEmpty(data)) {

                Map<String, Object> map = new HashMap<>();

                int lenth = data.length();
                if (lenth <= 12) { //区块高度

                    map.put("type", "blockNumber");

                    BigInteger bigInteger = web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false)
                            .send().getBlock().getNumber();

                    if (!CommonUtils.isNumeric0(data) || Integer.parseInt(data) > bigInteger.intValue()) {
                        map.put("status", "1");
                    } else {
                        map.put("status", "0");
                    }

                } else if (lenth > 12 && lenth < 60) { //钱包地址 && 合约地址

                    map.put("type", "address");

                } else if (lenth >= 60) {//交易哈希

                    map.put("type", "transaction");

                }

                map.put("data", data);
                return ResponseResult.build(200, "success", map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseResult.build(200, "null", null);
    }

    //获取服务器系统时间
    @GetMapping("/date")
    public ResponseResult date() {
        return ResponseResult.build(200, "success", new Date().getTime() / 1000);
    }


    //登录
    @PostMapping("/login")
    public ResponseResult login(@RequestParam String username,@RequestParam String password) {
        try {
            String md5 = DigestUtils.md5DigestAsHex(password.getBytes());

            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
            boolQueryBuilder.must(new TermQueryBuilder("username", username));
            boolQueryBuilder.must(new TermQueryBuilder("password", md5));
            //聚合处理
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(boolQueryBuilder);

            //查询索引对象
            SearchRequest searchRequest = new SearchRequest("user");
            searchRequest.types("data");
            searchRequest.source(sourceBuilder);

            SearchResponse response = client.search(searchRequest).get();
            String token = "";
            for (SearchHit hit : response.getHits()) {
                token = hit.getSourceAsMap().get("token").toString();
                return ResponseResult.build(200, "success", token);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.build(300, "error", null);
    }


    @GetMapping("/getAllTxCounts")
    public ResponseResult getAllTxCounts(){

        long totalHits = 0;
        try {
            totalHits = this.client.prepareSearch(TRANSACTION.toString())
                    .setTypes("data")
                    .setSearchType(SearchType.QUERY_THEN_FETCH)
                    .get()
                    .getHits()
                    .getTotalHits();

            return ResponseResult.build(200, "Get AllTxTounts Success.", totalHits);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.build(300, "Get AllTxCounts Failed.");
        }




    }




}
