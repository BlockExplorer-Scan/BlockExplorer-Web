package com.pwnpub.search.app;

import com.pwnpub.search.config.CoinName;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author soobeenwong
 * @date 2019-01-05 2:16 PM
 * @desc 提供手机h5版展示
 */
@Controller
@RequestMapping("App")
public class AppController {

    @Autowired
    private TransportClient client;

    @Autowired
    private CoinName coinName;

    @RequestMapping("/index")
    public String index(HttpServletRequest request,@RequestParam("addresses") String addresses) {

//        String addresseStr = request.getParameter("addresses");
        request.setAttribute("addresses", addresses);
        return "index";
    }

    @PostMapping("/queryTransactionRecordBackup")
    @ResponseBody
    public ResponseResult queryTransactionRecordBackup(HttpServletRequest request,
                                                       @RequestParam String addresseStr) {


        if (addresseStr != null && addresseStr != "") {

            String[] addresses = addresseStr.split(",");

            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> listNew = new ArrayList<>();

            for (String address : addresses) {

                BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

                boolQueryBuilder.should(QueryBuilders.matchQuery("from", address));
                boolQueryBuilder.should(QueryBuilders.matchQuery("to", address));


                SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch("erc20")
                        .setTypes("data")
                        .addSort("blockNumber", SortOrder.DESC)
                        .setSearchType(SearchType.QUERY_THEN_FETCH)
                        .setQuery(boolQueryBuilder);
                ;

                SearchResponse searchResponse = searchRequestBuilder.get();

                for (SearchHit hit : searchResponse.getHits()) {

                    Object blockNumber = hit.getSourceAsMap().get("blockNumber");
                    Object blockHash = hit.getSourceAsMap().get("blockHash");

                    //通过区块number去处理
                    BoolQueryBuilder boolQueryBuilder1 = QueryBuilders.boolQuery();

                    boolQueryBuilder1.must(QueryBuilders.matchQuery("blockNumber", blockNumber));

                    SearchRequestBuilder searchRequestBuilder1 = this.client.prepareSearch("erc20")
                            .setTypes("data")
                            .addSort("blockNumber", SortOrder.DESC)
                            .setSearchType(SearchType.QUERY_THEN_FETCH)
                            .setQuery(boolQueryBuilder);


                    SearchResponse searchResponse1 = searchRequestBuilder1.get();
                    List<Map<String, Object>> listInner = new ArrayList<>();
                    for (SearchHit hit1 : searchResponse1.getHits()) {
                        Object from = hit1.getSourceAsMap().get("from");
                        Object to = hit1.getSourceAsMap().get("to");
                        Object data = hit1.getSourceAsMap().get("data");
                        Object status = hit1.getSourceAsMap().get("status");

                        Map<String, Object> mapInner = new HashMap<>();
                        mapInner.put("from", from);
                        mapInner.put("to", to);
                        mapInner.put("data", data);
                        mapInner.put("status", status);

                        listInner.add(mapInner);

                        Map<String, Object> mapOuter = new HashMap<>();
                        mapOuter.put("blockNumber", blockNumber);
                        mapOuter.put("blockHash", blockHash);
                        mapOuter.put("transactions", listInner);

                        list.add(mapOuter);

                    }

                }

                Set set = new HashSet();

                set.addAll(list);
                listNew.addAll(set);
            }
            return ResponseResult.build(200, "获取数据成功", listNew);
        } else {
            return ResponseResult.build(401, "请求数据为null，请输入数据");
        }
    }

    @PostMapping("/test")
    @ResponseBody
    public ResponseResult queryMainCoinByContractAddress(HttpServletRequest request) {


        String[] addresses = request.getParameterValues("addresses");

        List<Map<String, Object>> list = new ArrayList<>();

        if (addresses != null) {
            for (String address : addresses) {

                BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

                boolQueryBuilder.should(QueryBuilders.matchQuery("from", address));
                boolQueryBuilder.should(QueryBuilders.matchQuery("to", address));


                SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch("erc20")
                        .setTypes("data")
                        .addSort("blockNumber", SortOrder.DESC)
                        .setSearchType(SearchType.QUERY_THEN_FETCH)
                        .setQuery(boolQueryBuilder);

                SearchResponse searchResponse = searchRequestBuilder.get();

                for (SearchHit hit : searchResponse.getHits()) {

                    Object blockNumber = hit.getSourceAsMap().get("blockNumber");

                    List<Map<String, Object>> listInner = new ArrayList<>();


                    Object from = hit.getSourceAsMap().get("from");
                    Object to = hit.getSourceAsMap().get("to");
                    Object data = hit.getSourceAsMap().get("data");
                    Object status = hit.getSourceAsMap().get("status");

                    Map<String, Object> mapInner = new HashMap<>();
                    mapInner.put("from", from);
                    mapInner.put("to", to);
                    mapInner.put("data", data);
                    mapInner.put("status", status);

                    listInner.add(mapInner);


                    Object blockHash = hit.getSourceAsMap().get("blockHash");


                    Map<String, Object> mapOuter = new HashMap<>();
                    mapOuter.put("blockNumber", blockNumber);
                    mapOuter.put("blockHash", blockHash);
                    mapOuter.put("transactions", listInner);

                    list.add(mapOuter);

                }


                return ResponseResult.build(200, "获取数据成功", list);

            }
        } else {
            return ResponseResult.build(401, "请求数据为null，请输入数据");
        }

        return null;

    }

    @PostMapping("/query/latestBlock")
    @ResponseBody
    public ResponseResult queryBlock() {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch("block")
                .setTypes("data")
                .addSort("number", SortOrder.DESC)
                .setSearchType(SearchType.QUERY_THEN_FETCH) //小数量查询
                .setQuery(boolQueryBuilder)
                .setFrom(0)
                .setSize(1);

        SearchResponse searchResponse = searchRequestBuilder.get();

        List<Object> list = new ArrayList<>();

        for (SearchHit hit : searchResponse.getHits()) {
            list.add(hit.getSourceAsMap().get("number"));

        }

        return ResponseResult.build(200, "The Latest block number is：", list);
    }

    @PostMapping("/query")
    @ResponseBody
    public ResponseResult query(HttpServletRequest request, @RequestParam String addresseStr) {


        if (addresseStr != null && addresseStr != "") {

            String[] addresses = addresseStr.split(",");
            List<Map<String, Object>> list = new ArrayList<>();
            for (String address : addresses) {

                BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

                boolQueryBuilder.should(QueryBuilders.matchQuery("from", address));
                boolQueryBuilder.should(QueryBuilders.matchQuery("to", address));


                SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch("erc20")
                        .setTypes("data")
                        .addSort("blockNumber", SortOrder.DESC)
                        .setSearchType(SearchType.QUERY_THEN_FETCH)
                        .setQuery(boolQueryBuilder);

                SearchResponse searchResponse = searchRequestBuilder.get();

                for (SearchHit hit : searchResponse.getHits()) {

                    Object blockNumber = hit.getSourceAsMap().get("blockNumber");

                    //通过区块号码遍历

                    Object blockHash = hit.getSourceAsMap().get("blockHash");
                    Map<String, Object> mapOuter = new HashMap<>();

                    //通过区块number去查询erc20
                    /*****************************查询erc20*******************************/
                    BoolQueryBuilder query = QueryBuilders.boolQuery();
                    BoolQueryBuilder q = QueryBuilders.boolQuery();
                    q.should(QueryBuilders.matchQuery("from", address));
                    q.should(QueryBuilders.matchQuery("to", address));

                    query.must(QueryBuilders.matchQuery("blockNumber", blockNumber));
                    query.must(QueryBuilders.matchQuery("status", "erc20"));
                    query.must(q);


                    SearchRequestBuilder searchRequestBuilder1 = this.client.prepareSearch("erc20")
                            .setTypes("data")
                            .addSort("blockNumber", SortOrder.DESC)
                            .setSearchType(SearchType.QUERY_THEN_FETCH)
                            .setQuery(q);

                    SearchResponse searchResponse1 = searchRequestBuilder1.get();
                    List<Map<String, Object>> listInner = new ArrayList<>();

                    for (SearchHit hit1 : searchResponse1.getHits()) {

                        Map<String, Object> mapInner = new HashMap<>();
                        mapInner.put("from", hit1.getSourceAsMap().get("from"));
                        mapInner.put("to", hit1.getSourceAsMap().get("to"));
                        mapInner.put("data", hit1.getSourceAsMap().get("data"));
                        mapInner.put("status", hit1.getSourceAsMap().get("status"));
                        mapInner.put("statusName", coinName.getErc20Name());

                        listInner.add(mapInner);

                        mapOuter.put("blockNumber", blockNumber);
                        mapOuter.put("blockHash", blockHash);
                        mapOuter.put("transactions", listInner);

                    }
                    /*******************************erc20处理完成***************************************/

                    /********************************开始处理主币****************************************/

                    BoolQueryBuilder query2 = QueryBuilders.boolQuery();
                    BoolQueryBuilder q2 = QueryBuilders.boolQuery();
                    q2.should(QueryBuilders.matchQuery("from", address));
                    q2.should(QueryBuilders.matchQuery("to", address));

                    query2.must(QueryBuilders.matchQuery("blockNumber", blockNumber));
                    query2.must(QueryBuilders.matchQuery("status", "maincoin"));
                    query2.must(q);


                    SearchRequestBuilder searchRequestBuilder2 = this.client.prepareSearch("maincoin")
                            .setTypes("data")
                            .addSort("blockNumber", SortOrder.DESC)
                            .setSearchType(SearchType.QUERY_THEN_FETCH)
                            .setQuery(q2);

                    SearchResponse searchResponse2 = searchRequestBuilder2.get();
                    List<Map<String, Object>> listInner2 = new ArrayList<>();
                    for (SearchHit hit2 : searchResponse2.getHits()) {

                        Map<String, Object> mapInner = new HashMap<>();
                        mapInner.put("from", hit2.getSourceAsMap().get("from"));
                        mapInner.put("to", hit2.getSourceAsMap().get("to"));
                        mapInner.put("data", hit2.getSourceAsMap().get("value"));
                        mapInner.put("status", hit2.getSourceAsMap().get("status"));
                        mapInner.put("statusName", coinName.getMaincoinName());

                        listInner2.add(mapInner);

                        mapOuter.put("blockNumber", blockNumber);
                        mapOuter.put("blockHash", blockHash);
                        mapOuter.put("transactions", listInner2);

                    }

                    /**********************************主币处理完成*************************************/

                    BoolQueryBuilder boolQueryBuilder3 = QueryBuilders.boolQuery();
                    boolQueryBuilder3.must(QueryBuilders.matchQuery("number", blockNumber));
                    SearchRequestBuilder searchRequestBuilder3 = this.client.prepareSearch("block")
                            .setTypes("data")
                            .setSearchType(SearchType.QUERY_THEN_FETCH)
                            .setQuery(boolQueryBuilder3);
                    SearchResponse searchResponse3 = searchRequestBuilder3.get();

                    for (SearchHit hit3 : searchResponse3.getHits()) {
                        Object timestamp = hit3.getSourceAsMap().get("timestamp");
                        mapOuter.put("timestamp", timestamp);
                    }

                    list.add(mapOuter);

                }

                Set set = new HashSet();
                List<Map<String, Object>> listNew = new ArrayList<>();
                set.addAll(list);
                listNew.addAll(set);

                return ResponseResult.build(200, "获取数据成功", listNew);

            }
        } else {
            return ResponseResult.build(401, "请求数据为null，请输入数据");
        }

        return null;
    }




    //新版h5返回
    @PostMapping("/queryTransactionRecord")
    @ResponseBody
    public ResponseResult queryTransactionRecord(HttpServletRequest request,
                                @RequestParam String addresseStr,
                                @RequestParam(name = "pageStart", required = false, defaultValue = "0")Integer pageStart,
                                @RequestParam(name = "pageNum", required = false, defaultValue = "20")Integer pageNum) {



        if (addresseStr != null && addresseStr != "") {

            String[] addresses = addresseStr.split(",");
            List<Map<String, Object>> list = new ArrayList<>();


            for (String address : addresses) {

                //查询最近20个区块
                SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch("block")
                        .setTypes("data")
                        .addSort("number", SortOrder.DESC)
                        .setSearchType(SearchType.QUERY_THEN_FETCH)
                        .setFrom(pageStart)
                        .setSize(pageNum);

                SearchResponse searchResponse = searchRequestBuilder.get();
                for (SearchHit searchHit : searchResponse.getHits()) {

                    Map<String, Object> mapOuter = new HashMap<>();

                    Object number = searchHit.getSourceAsMap().get("number");
                    Object timestamp = searchHit.getSourceAsMap().get("timestamp");
                    Object blockHash = searchHit.getSourceAsMap().get("hash");
                    List<Map<String, Object>> listInner = new ArrayList<>();
                    //根据区块号码查询ERC20交易
                    BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
                    BoolQueryBuilder query = QueryBuilders.boolQuery();
                    boolQueryBuilder.must(QueryBuilders.matchQuery("blockNumber", number));
                    boolQueryBuilder.must(QueryBuilders.matchQuery("status", coinName.getErc20()));

                    query.should(QueryBuilders.matchQuery("from", address));
                    query.should(QueryBuilders.matchQuery("to", address));

                    boolQueryBuilder.must(query);

                    SearchRequestBuilder searchRequestBuilder2 = this.client.prepareSearch("erc20")
                            .setTypes("data")
                            .addSort("blockNumber", SortOrder.DESC)
                            .setSearchType(SearchType.QUERY_THEN_FETCH)
                            .setQuery(boolQueryBuilder);
                    SearchResponse searchResponse2 = searchRequestBuilder2.get();

                    Map<String, Object> mapInner1 = new HashMap<>();

                    //拿到from to data
                    for (SearchHit searchHit2:searchResponse2.getHits()) {
                        Object from = searchHit2.getSourceAsMap().get("from");
                        Object to = searchHit2.getSourceAsMap().get("to");
                        Object data = searchHit2.getSourceAsMap().get("data");
                        Object status = searchHit2.getSourceAsMap().get("status");
                        Object statusName = searchHit2.getSourceAsMap().get("statusName");
                        Object transactionHash = searchHit2.getSourceAsMap().get("transactionHash");
                        //封装
                        mapInner1.put("from", from);
                        mapInner1.put("to", to);
                        mapInner1.put("data", data);
                        mapInner1.put("status", status);
                        mapInner1.put("statusName", coinName.getErc20Name());
                        mapInner1.put("transactionHash", transactionHash);

                        listInner.add(mapInner1);

                    }

                    //根据区块号码，查询主币内部交易
                    BoolQueryBuilder boolQueryBuilder2 = QueryBuilders.boolQuery();
                    BoolQueryBuilder query2 = QueryBuilders.boolQuery();

                    boolQueryBuilder2.must(QueryBuilders.matchQuery("blockNumber", number));
                    boolQueryBuilder2.must(QueryBuilders.matchQuery("status", coinName.getMaincoin()));

                    query2.should(QueryBuilders.matchQuery("from", address));
                    query2.should(QueryBuilders.matchQuery("to", address));

                    boolQueryBuilder2.must(query2);

                    SearchRequestBuilder searchRequestBuilder3 = this.client.prepareSearch("maincoin")
                            .setTypes("data")
                            .addSort("blockNumber", SortOrder.DESC)
                            .setSearchType(SearchType.QUERY_THEN_FETCH)
                            .setQuery(boolQueryBuilder2);
                    SearchResponse searchResponse3 = searchRequestBuilder3.get();

                    Map<String, Object> mapInner2 = new HashMap<>();
                    //拿到from to data
                    for (SearchHit searchHit3:searchResponse3.getHits()) {
                        Object from = searchHit3.getSourceAsMap().get("from");
                        Object to = searchHit3.getSourceAsMap().get("to");
                        Object data = searchHit3.getSourceAsMap().get("value");
                        Object status = searchHit3.getSourceAsMap().get("status");
                        Object statusName = searchHit3.getSourceAsMap().get("statusName");
                        //封装
                        mapInner2.put("from", from);
                        mapInner2.put("to", to);
                        mapInner2.put("data", data);
                        mapInner2.put("status", status);
                        mapInner2.put("statusName", coinName.getMaincoinName());

                        listInner.add(mapInner2);

                    }

                    //listInner//去重

                    mapOuter.put("blockHash", blockHash);
                    mapOuter.put("blockNumber", number);
                    mapOuter.put("transactions", listInner);
                    mapOuter.put("timestamp", timestamp);

                    list.add(mapOuter);

                }


            }



            return ResponseResult.build(200, "h5:查询交易成功", list);
        }
        return null;
    }

}
