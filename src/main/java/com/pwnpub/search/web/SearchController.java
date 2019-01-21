package com.pwnpub.search.web;

import com.pwnpub.search.config.CoinName;
import com.pwnpub.search.pojo.BlockEntityAll;
import com.pwnpub.search.pojo.TransactionEntityAll;
import com.pwnpub.search.utils.CommonUtils;
import com.pwnpub.search.utils.ResponseResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author soobeenwong
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

    private static final Logger logger = LogManager.getLogger(SearchController.class);

    private final int Seconds = 86400000;

    @GetMapping("/index")
    public String index( HttpServletRequest request,HttpServletResponse response){

        return "index";
    }

    @GetMapping("/get/allblock/data")
    public ResponseEntity get(@RequestParam(name = "id",defaultValue = "")String id){

        GetResponse result = this.client.prepareGet("block", "data", id).get();

        if (!result.isExists()) {

            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity(result.getSource(), HttpStatus.OK);

    }

    //添加区块数据
    //@PostMapping("/add/block/data")
    public ResponseEntity addBlock(BlockEntityAll.BlockBean blockEntity) {
        try (
                XContentBuilder content = XContentFactory.jsonBuilder().startObject()
                        .field("difficulty", blockEntity.getDifficulty())
                        .field("difficultyRaw", blockEntity.getDifficultyRaw())
                        .field("extraData", blockEntity.getExtraData())
                        .field("gasLimit", blockEntity.getGasLimit())
                        .field("gasLimitRaw", blockEntity.getGasLimitRaw())
                        .field("gasUsed", blockEntity.getGasUsed())
                        .field("gasUsedRaw", blockEntity.getGasUsedRaw())
                        .field("hash", blockEntity.getHash())
                        .field("logsBloom", blockEntity.getLogsBloom())
                        .field("miner", blockEntity.getMiner())
                        .field("mixHash", blockEntity.getMixHash())
                        .field("nonceRaw", blockEntity.getNonceRaw())
                        .field("numberRaw", blockEntity.getNumberRaw())
                        .field("parentHash", blockEntity.getParentHash())
                        .field("receiptsRoot", blockEntity.getReceiptsRoot())
                        .field("sha3Uncles", blockEntity.getSha3Uncles())
                        .field("size", blockEntity.getSize())
                        .field("sizeRaw", blockEntity.getSizeRaw())
                        .field("stateRoot", blockEntity.getStateRoot())
                        .field("timestamp", blockEntity.getTimestamp())
                        .field("timestampRaw", blockEntity.getTimestampRaw())
                        .field("totalDifficulty", blockEntity.getTotalDifficulty())
                        .field("totalDifficultyRaw", blockEntity.getTotalDifficultyRaw())
                        .field("transactions", blockEntity.getTransactions()) //数组
                        .field("transactionsRoot", blockEntity.getTransactionsRoot())
                        .field("uncles", blockEntity.getUncles())     //数组
                        .endObject())
        {
            IndexResponse response = this.client.prepareIndex("block","data")
                    .setSource(content)
                    .get();

            this.client.prepareUpdate();
            return new ResponseEntity(response.getId(), HttpStatus.OK);

        }catch (IOException e){
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    //添加交易数据
    @PostMapping("/add/transaction/data")
    public ResponseEntity addTransaction(@RequestBody TransactionEntityAll transactionEntity,
                                         HttpServletRequest request)


    {
        long currentTimeMillis = System.currentTimeMillis();

        try (
                XContentBuilder content = XContentFactory.jsonBuilder().startObject()
                        .field("blockHash", transactionEntity.getBlockHash())
                        .field("blockNumber", transactionEntity.getBlockNumber())
                        .field("blockNumberRaw", transactionEntity.getBlockNumberRaw())
                        .field("chainId", transactionEntity.getChainId())
                        .field("from", transactionEntity.getFrom())
                        .field("to", transactionEntity.getTo())
                        .field("gas", transactionEntity.getGas())
                        .field("gasPrice", transactionEntity.getGasPrice())
                        .field("gasPriceRaw", transactionEntity.getGasPriceRaw())
                        .field("gasRaw", transactionEntity.getGasRaw())
                        .field("hash", transactionEntity.getHash())
                        .field("input", transactionEntity.getInput())
                        .field("nonce", transactionEntity.getNonce())
                        .field("nonceRaw", transactionEntity.getNonceRaw())
                        .field("r", transactionEntity.getR())
                        .field("s", transactionEntity.getS())
                        .field("transactionIndex", transactionEntity.getTransactionIndex())
                        .field("transactionIndexRaw", transactionEntity.getTransactionIndexRaw())
                        .field("v", transactionEntity.getV())
                        .field("value", transactionEntity.getValue())
                        .field("valueRaw", transactionEntity.getValueRaw())
                        .field("timestamp", String.valueOf(currentTimeMillis))
                        .field("timestampDay", new SimpleDateFormat("yyyy-MM-dd").format(currentTimeMillis))
                        .field("status", transactionEntity.getStatus())
                        .endObject())
        {
            IndexResponse response = this.client.prepareIndex("transaction","data")
                    .setSource(content)
                    .get();
            return new ResponseEntity(response.getId(), HttpStatus.OK);

        }catch (IOException e){
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @DeleteMapping("/delete/block/data")
    public ResponseEntity delete(@RequestParam(name = "id", defaultValue = "false") String id){

        DeleteResponse response = this.client.prepareDelete("block", "data",id).get();

        return new ResponseEntity(response.getResult().toString(), HttpStatus.OK);

    }

    @PutMapping("/update/block/data")
    public ResponseEntity update(
            @RequestParam(name = "id") String id,
            @RequestParam(name = "title") String title,
            @RequestParam(name = "author") String author
    ){
        UpdateRequest update = new UpdateRequest("block", "data", id);

        try {

            XContentBuilder content = XContentFactory.jsonBuilder().startObject();

            if (title != null) {
                content.field("title", title);
            }
            if (title != null) {
                content.field("author", author);
            }

            content.endObject();

            update.doc(content);

        }catch (IOException e){
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            UpdateResponse updateResponse = this.client.update(update).get();
            return new ResponseEntity(updateResponse.getResult().toString(), HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/query/block/data")
    public ResponseResult queryBlock(
            @RequestParam(name = "pageStart", required = false, defaultValue = "0")Integer pageStart,
            @RequestParam(name = "pageNum", required = false, defaultValue = "10")Integer pageNum

    ){
        logger.info("get into query/block/data");
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch("block")
                .setTypes("data")
                .addSort("number", SortOrder.DESC)
                .setSearchType(SearchType.QUERY_THEN_FETCH) //小数量查询
                .setQuery(boolQueryBuilder)
                .setFrom(pageStart)
                .setSize(pageNum);

        SearchResponse searchResponse = searchRequestBuilder.get();

        List<Map<String, Object>> list = new ArrayList<>();

        for (SearchHit hit : searchResponse.getHits()) {
            list.add(hit.getSourceAsMap());
        }

        return ResponseResult.build(200, "query all block datas success", list);
    }

    @GetMapping("/queryBlockByValue")
    public ResponseResult queryBlockByNum(
            @RequestParam(name = "hash", required = false)String hash,
            @RequestParam(name = "number", required = false)Integer number

    ){
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if (number != null) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("number", number));
        }

        if (hash != null) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("hash", hash));
        }


        SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch("block")
                .setTypes("data")
                .setSearchType(SearchType.QUERY_THEN_FETCH) //小数量查询
                .setQuery(boolQueryBuilder);

        SearchResponse searchResponse = searchRequestBuilder.get();

        List<Map<String, Object>> list = new ArrayList<>();

        for (SearchHit hit : searchResponse.getHits()) {
            list.add(hit.getSourceAsMap());
        }

        return ResponseResult.build(200, "query specific block success...", list);
    }

    @GetMapping("/query/transaction/data")
    public ResponseResult queryTransaction(
            @RequestParam(name = "pageStart", required = false, defaultValue = "0")Integer pageStart,
            @RequestParam(name = "pageNum", required = false, defaultValue = "10")Integer pageNum

    ){

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch("transaction")
                .setTypes("data")
                .addSort("blockNumber", SortOrder.DESC)
                .setSearchType(SearchType.QUERY_THEN_FETCH) //大数量查询
                .setQuery(boolQueryBuilder)
                .setFrom(pageStart)
                .setSize(pageNum);


        SearchResponse searchResponse = searchRequestBuilder.get();

        List<Map<String, Object>> list = new ArrayList<>();

        for (SearchHit hit : searchResponse.getHits()) {
            list.add(hit.getSourceAsMap());
        }

        return ResponseResult.build(200, "query all transaction datas success", list);
    }

    //分页 0-25    添加统计数量
    @GetMapping("/queryTransactionByValue")
    public ResponseResult queryTransactionByValue(
            @RequestParam(name = "hash", required = false)String hash,
            @RequestParam(name = "blockNumber", required = false)Integer blockNumber,
            @RequestParam(name = "miner", required = false) String miner,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "to", required = false) String to,
            @RequestParam(name = "from", required = false) String from,
            @RequestParam(name = "pageStart", required = false, defaultValue = "0")Integer pageStart,
            @RequestParam(name = "pageNum", required = false, defaultValue = "25")Integer pageNum


    ){
        logger.info("get into queryTransactionByValue , 【Paging query】");
        List<Map<String, Object>> list = new ArrayList<>();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if (hash != null) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("hash", hash));
        }

        if (blockNumber != null) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("blockNumber", blockNumber));
        }

        if (miner != null) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("from", miner));
        }

        if (status != null) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("status", status));
        }

        if (to != null) {
            BoolQueryBuilder query = QueryBuilders.boolQuery();
            query.should(QueryBuilders.matchQuery("to", to));
            query.should(QueryBuilders.matchQuery("from", to));
            boolQueryBuilder.must(query);

        }

        if (from != null) {
            BoolQueryBuilder q = QueryBuilders.boolQuery();
            q.should(QueryBuilders.matchQuery("from", from));
            q.should(QueryBuilders.matchQuery("to", from));
            boolQueryBuilder.must(q);
        }

        SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch("transaction")
                .setTypes("data")
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                .setQuery(boolQueryBuilder)
                .addSort("blockNumber", SortOrder.DESC)
                .setFrom(pageStart)
                .setSize(pageNum);

        SearchResponse searchResponse = searchRequestBuilder.get();

        for (SearchHit hit : searchResponse.getHits()) {
            list.add(hit.getSourceAsMap());
        }

        return ResponseResult.build(200, "query specific transaction success...", list);
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

        SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch("transaction")
                .setTypes("data")
                .setSearchType(SearchType.QUERY_THEN_FETCH) //大数量查询
                .setQuery(boolQueryBuilder);

        SearchResponse searchResponse = searchRequestBuilder.get();
        long currentCounts = searchResponse.getHits().getTotalHits();

        return currentCounts;
    }

    //analyze erc20 event
    @GetMapping("/queryERC20ByTransaction")
    public ResponseResult queryERC20ByTransaction(
            @RequestParam(name = "transactionHash", required = true )String transactionHash

    ){
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if (transactionHash != null) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("transactionHash", transactionHash));
        }

        SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch("erc20")
                .setTypes("data")

                .setSearchType(SearchType.QUERY_THEN_FETCH) //小数量查询
                .setQuery(boolQueryBuilder);

        SearchResponse searchResponse = searchRequestBuilder.get();

        List<Map<String, Object>> list = new ArrayList<>();

        for (SearchHit hit : searchResponse.getHits()) {
            hit.getSourceAsMap().put("statusName", coinName.getErc20Name());
            list.add(hit.getSourceAsMap());
        }

        return ResponseResult.build(200, "query erc20 transfer datas success", list);
    }


    //查询erc20 所有转账记录  分页
    @GetMapping("/queryERC20ByContractAddress")
    public ResponseResult queryERC20ByContractAddress(
            @RequestParam(name = "contractAddress", required = true )String contractAddress,
            @RequestParam(name = "pageStart", required = false, defaultValue = "0")Integer pageStart,
            @RequestParam(name = "pageNum", required = false, defaultValue = "25")Integer pageNum

    ){
        List<Map<String, Object>> list = new ArrayList<>();

        if (contractAddress != null) {

            //boolQueryBuilder.must(QueryBuilders.matchQuery("status", "erc20"));

            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            BoolQueryBuilder query = QueryBuilders.boolQuery();

            boolQueryBuilder.should(QueryBuilders.matchQuery("from", contractAddress));
            boolQueryBuilder.should(QueryBuilders.matchQuery("to", contractAddress));
            query.must(boolQueryBuilder);


            SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch("erc20")
                    .setTypes("data")
                    .setSearchType(SearchType.QUERY_THEN_FETCH)
                    .addSort("blockNumber", SortOrder.DESC)   //新添加
                    .setQuery(query)
                    .setFrom(pageStart)
                    .setSize(pageNum);

            SearchResponse searchResponse = searchRequestBuilder.get();

            for (SearchHit hit : searchResponse.getHits()) {

                hit.getSourceAsMap().put("statusName", coinName.getErc20Name());
                list.add(hit.getSourceAsMap());
            }

        }
        return ResponseResult.build(200, "query erc20 transfer datas success", list);
    }

    //主币内部转账记录
    @GetMapping("/queryMainCoinByContractAddress")
    public ResponseResult queryMainCoinByContractAddress(
            @RequestParam(name = "contractAddress", required = true )String contractAddress,
            @RequestParam(name = "pageStart", required = false, defaultValue = "0")Integer pageStart,
            @RequestParam(name = "pageNum", required = false, defaultValue = "25")Integer pageNum

    ){
        List<Map<String, Object>> list = new ArrayList<>();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if (contractAddress != null) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("to", contractAddress));

            SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch("maincoin")
                    .setTypes("data")
                    .addSort("blockNumber", SortOrder.DESC)
                    .setSearchType(SearchType.QUERY_THEN_FETCH)
                    .setQuery(boolQueryBuilder)
                    .setFrom(pageStart)
                    .setSize(pageNum);


            SearchResponse searchResponse = searchRequestBuilder.get();

            for (SearchHit hit : searchResponse.getHits()) {
                list.add(hit.getSourceAsMap());
            }

        }

        if (contractAddress != null) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("from", contractAddress));

            SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch("maincoin")
                    .setTypes("data")
                    .addSort("blockNumber", SortOrder.DESC)
                    .setSearchType(SearchType.QUERY_THEN_FETCH)
                    .setQuery(boolQueryBuilder);


            SearchResponse searchResponse = searchRequestBuilder.get();

            for (SearchHit hit : searchResponse.getHits()) {
                list.add(hit.getSourceAsMap());
            }
        }

        return ResponseResult.build(200, "query maincoin transfer datas success", list);
    }

    //统计交易数量
    @GetMapping("/queryTxsCounts")
    public ResponseResult queryTxsCounts(
            @RequestParam(name = "address", required = true)String address


    ){

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if (address != null) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("from", address));
            boolQueryBuilder.should(QueryBuilders.matchQuery("to", address));
        }

        SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch("transaction")
                .setTypes("data")
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                .setQuery(boolQueryBuilder);
        SearchResponse searchResponse = searchRequestBuilder.get();
        long totalHits = searchResponse.getHits().getTotalHits();

        Map<String, Object> map = new HashMap<>();
        map.put("txns", totalHits);
        map.put("EtherValue", 100);
        //获取余额
        Web3j web3 = Web3j.build(new HttpService("http://n8.ledx.xyz"));

        Web3ClientVersion web3ClientVersion;
        try {
            web3ClientVersion = web3.web3ClientVersion().send();
            String clientVersion = web3ClientVersion.getWeb3ClientVersion();
            logger.info("私链的版本是："+clientVersion);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("节点连接失败！");
        }
        //获取余额
        try {
            EthGetBalance ethGetBalance = web3.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
            if(ethGetBalance!=null){
                // 打印账户余额
                System.out.println(ethGetBalance.getBalance());
                // 将单位转为以太，方便查看
                BigDecimal balanceETH = Convert.fromWei(ethGetBalance.getBalance().toString(), Convert.Unit.ETHER);
                map.put("balance", balanceETH);
            } else {
                map.put("balance", 0);
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("获取主币余额失败...");
        }

        return ResponseResult.build(200, "query specific transaction success...", map);
    }

    //全局搜索   匹配长度
    @GetMapping("/search")
    public ResponseResult search(@RequestParam(name = "data", required = true) String data) {
        try {
            if (!StringUtils.isEmpty(data)) {

                Map<String, Object> map = new HashMap<>();

                int lenth = data.length();
                if (lenth <= 12) { //区块高度

                    map.put("type", "blockNumber");
                    Web3j web3j = Web3j.build(new HttpService("http://n8.ledx.xyz"));

                    BigInteger bigInteger = web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false)
                            .send().getBlock().getNumber();

                    if (!CommonUtils.isNumeric0(data) || Integer.parseInt(data) > bigInteger.intValue()) {
                        map.put("status", "1");
                    }else {
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

    @GetMapping("/date")
    public ResponseResult date() {
        return ResponseResult.build(200, "success",new Date().getTime()/1000);
    }

}
