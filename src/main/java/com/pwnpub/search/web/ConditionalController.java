package com.pwnpub.search.web;

import com.pwnpub.search.utils.CommonUtils;
import com.pwnpub.search.utils.ResponseResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.protocol.Web3j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pwnpub.search.entity.EsTableEnum.*;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;

/**
 * @author Giannis
 * @date 2019-08-28 14:28
 * @desc 条件搜索控制器
 */
@RestController
@RequestMapping("/Conditional")
public class ConditionalController {

    @Autowired
    private TransportClient client;

    @Autowired
    ConfigurableApplicationContext configurableApplicationContext;

    @Autowired
    Web3j web3j;

    private static final Logger logger = LogManager.getLogger(ConditionalController.class);

    @GetMapping("/rangeQueryOuter")
    public ResponseResult rangeQueryOuter(@RequestParam(name = "timeStart", required = false) String timeStart,
                                          @RequestParam(name = "timeEnd", required = false) String timeEnd,
                                          @RequestParam(name = "from", required = false) String from,
                                          @RequestParam(name = "to", required = false) String to,
                                          @RequestParam(name = "transferStart", required = false) String transferStart,
                                          @RequestParam(name = "transferEnd", required = false) String transferEnd,
                                          @RequestParam(name = "address", required = false) String address,
                                          @RequestParam(name = "pageStart", required = false, defaultValue = "0") Integer pageStart,
                                          @RequestParam(name = "pageNum", required = false, defaultValue = "20") Integer pageNum) {

        //时间范围
        if ((timeStart != null && timeEnd != null) ||
                (transferStart != null && transferEnd != null) ||
                (from != null || to != null)) {


            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();


            if (timeStart != null && timeEnd != null) {
                RangeQueryBuilder qb1 = rangeQuery("timestamp")
                        .from(timeStart).to(timeEnd);

                boolQueryBuilder.must(qb1);
            }

            //
            if (transferStart != null && transferEnd != null) {

                RangeQueryBuilder qb2 = rangeQuery("value")
                        .gte(transferStart).lte(transferEnd);

                boolQueryBuilder.must(qb2);
            }

            if (address == null) {
                if (from != null) {
                    boolQueryBuilder.must(QueryBuilders.matchQuery("from", from));
                }

                if (to != null) {
                    boolQueryBuilder.must(QueryBuilders.matchQuery("to", to));
                }
            } else if (address != null) {
                if (from != null || to != null) {

                    BoolQueryBuilder searchAdressBool = new BoolQueryBuilder();
                    if (from != null) {
                        searchAdressBool.should(QueryBuilders.termQuery("from", from));
                    }
                    if (to != null) {
                        searchAdressBool.should(QueryBuilders.termQuery("to", to));
                    }
                    boolQueryBuilder.must(searchAdressBool);

                    if (address != null) {
                        BoolQueryBuilder adressBool = new BoolQueryBuilder();
                        adressBool.should(QueryBuilders.termQuery("from", address));
                        adressBool.should(QueryBuilders.termQuery("to", address));
                        boolQueryBuilder.must(adressBool);
                    }
                }
            }


            SearchResponse response = client.prepareSearch(TRANSACTION.toString())
                    .setTypes("data")
                    .addSort("blockNumber", SortOrder.DESC)
                    .setSearchType(SearchType.QUERY_THEN_FETCH)
                    .setQuery(boolQueryBuilder)
                    .setFrom(pageStart)
                    .setSize(pageNum)
                    .get();

            SearchHits hits = response.getHits();

            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> list2 = new ArrayList<>();
            List<Object> list3 = new ArrayList<>();

            for (SearchHit searchHit : hits.getHits()) {

                list.add(searchHit.getSourceAsMap());
            }

            Map<String, Object> map = new HashMap<>();
            map.put("total", hits.getTotalHits());
            list2.add(map);

            list3.add(list);
            list3.add(list2);

            return ResponseResult.build(200, "Get Conditional Transaction Success", list3);

        }

        return ResponseResult.build(201, "Get Conditional Transaction Failed");

    }


    //主币内部转账条件查询
    @GetMapping("/rangeQueryInner")
    public ResponseResult rangeQueryInner(@RequestParam(name = "timeStart", required = false) String timeStart,
                                          @RequestParam(name = "timeEnd", required = false) String timeEnd,
                                          @RequestParam(name = "from", required = false) String from,
                                          @RequestParam(name = "to", required = false) String to,
                                          @RequestParam(name = "transferStart", required = false) String transferStart,
                                          @RequestParam(name = "transferEnd", required = false) String transferEnd,
                                          @RequestParam(name = "address", required = true) String address,
                                          @RequestParam(name = "pageStart", required = false, defaultValue = "0") Integer pageStart,
                                          @RequestParam(name = "pageNum", required = false, defaultValue = "20") Integer pageNum) {

        if ((timeStart != null && timeEnd != null) ||
                (transferStart != null && transferEnd != null) ||
                (from != null || to != null) || address != null) {


            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();


            if (timeStart != null && timeEnd != null) {
                RangeQueryBuilder qb1 = rangeQuery("timestamp")
                        .from(timeStart).to(timeEnd);

                boolQueryBuilder.must(qb1);
            }

            if (transferStart != null && transferEnd != null) {
                RangeQueryBuilder qb2 = rangeQuery("value")
                        .gte(transferStart).lte(transferEnd);

                boolQueryBuilder.must(qb2);
            }

            if (from != null || to != null) {

                BoolQueryBuilder searchAdressBool = new BoolQueryBuilder();
                if (from != null) {
                    searchAdressBool.should(QueryBuilders.termQuery("from", from));
                }
                if (to != null) {
                    searchAdressBool.should(QueryBuilders.termQuery("to", to));
                }
                boolQueryBuilder.must(searchAdressBool);
            }

            if (address != null) {
                BoolQueryBuilder adressBool = new BoolQueryBuilder();
                adressBool.should(QueryBuilders.termQuery("from", address));
                adressBool.should(QueryBuilders.termQuery("to", address));
                boolQueryBuilder.must(adressBool);
            }
          /*  if (from != null) {
                boolQueryBuilder.must(QueryBuilders.matchQuery("from", from));
            }

            if (to != null) {
                boolQueryBuilder.must(QueryBuilders.matchQuery("to", to));
            }*/

            SearchResponse response = client.prepareSearch(MAINCOIN.toString())
                    .setTypes("data")
                    .addSort("blockNumber", SortOrder.DESC)
                    .setSearchType(SearchType.QUERY_THEN_FETCH)
                    .setQuery(boolQueryBuilder)
                    .setFrom(pageStart)
                    .setSize(pageNum)
                    .get();

            SearchHits hits = response.getHits();

            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> list2 = new ArrayList<>();
            List<Object> list3 = new ArrayList<>();

            for (SearchHit searchHit : hits.getHits()) {

                //searchHit.getSourceAsMap().put("timestamp", Long.valueOf(searchHit.getSourceAsMap().get("timestamp").toString()) / 1000);

                list.add(searchHit.getSourceAsMap());
            }
            Map<String, Object> map = new HashMap<>();
            map.put("total", hits.getTotalHits());
            list2.add(map);

            list3.add(list);
            list3.add(list2);

            return ResponseResult.build(200, "Get Conditional Transaction Success", list3);

        }

        return ResponseResult.build(201, "Get Conditional Transaction Failed");

    }

    //
    @GetMapping("/rangeQueryERC20")
    public ResponseResult rangeQueryERC20(@RequestParam(name = "tokenName", required = false) String tokenName,
                                          @RequestParam(name = "timeStart", required = false) String timeStart,
                                          @RequestParam(name = "timeEnd", required = false) String timeEnd,
                                          @RequestParam(name = "from", required = false) String from,
                                          @RequestParam(name = "to", required = false) String to,
                                          @RequestParam(name = "transferStart", required = false) String transferStart,
                                          @RequestParam(name = "transferEnd", required = false) String transferEnd,
                                          @RequestParam(name = "address", required = true) String address,
                                          @RequestParam(name = "pageStart", required = false, defaultValue = "0") Integer pageStart,
                                          @RequestParam(name = "pageNum", required = false, defaultValue = "20") Integer pageNum) {

        if ((timeStart != null && timeEnd != null) ||
                (transferStart != null && transferEnd != null) ||
                from != null || to != null ||
                tokenName != null || address != null) {


            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

            boolQueryBuilder.must(QueryBuilders.termQuery("status", "erc20"));

            if (tokenName != null) {
                boolQueryBuilder.must(QueryBuilders.matchQuery("address", tokenName));
            }

            if (timeStart != null && timeEnd != null) {
                RangeQueryBuilder qb1 = rangeQuery("timestamp")
                        .from(timeStart).to(timeEnd);

                boolQueryBuilder.must(qb1);
            }

            if (transferStart != null && transferEnd != null) {
                RangeQueryBuilder qb2 = rangeQuery("data")
                        .gte(transferStart).lte(transferEnd);

                boolQueryBuilder.must(qb2);
            }

            if (from != null || to != null) {

                BoolQueryBuilder searchAdressBool = new BoolQueryBuilder();
                if (from != null) {
                    searchAdressBool.should(QueryBuilders.termQuery("from", from));
                }
                if (to != null) {
                    searchAdressBool.should(QueryBuilders.termQuery("to", to));
                }
                boolQueryBuilder.must(searchAdressBool);
            }

            if (address != null) {
                BoolQueryBuilder adressBool = new BoolQueryBuilder();
                adressBool.should(QueryBuilders.termQuery("from", address));
                adressBool.should(QueryBuilders.termQuery("to", address));
                boolQueryBuilder.must(adressBool);
            }


            SearchResponse response = client.prepareSearch(ERC20.toString())
                    .setTypes("data")
                    .addSort("blockNumber", SortOrder.DESC)
                    .setFrom(pageStart)
                    .setSize(pageNum)
                    .setQuery(boolQueryBuilder)
                    .get();

            SearchHits hits = response.getHits();

            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> list2 = new ArrayList<>();
            List<Object> list3 = new ArrayList<>();

            for (SearchHit searchHit : hits.getHits()) {

                //searchHit.getSourceAsMap().put("timestamp", Long.valueOf(searchHit.getSourceAsMap().get("timestamp").toString()) / 1000);

                Object addressHit = searchHit.getSourceAsMap().get("address");

                //从配置文件获取ERC20Token名称
                String tokenName2 = null;
                tokenName2 = configurableApplicationContext.getEnvironment().getProperty(addressHit.toString());
                logger.info("从配置文件中读取到的币名是" + tokenName2);
                if (StringUtils.isEmpty(tokenName2)) {
                    //链上的币名
                    try {
                        logger.info("通过请求web3j获取币名...");
                        tokenName2 = CommonUtils.getTokenName(web3j, addressHit.toString());
                        logger.info("请求web3j获取到的币名是：" + tokenName2);
                        searchHit.getSourceAsMap().put("statusName", tokenName2);

                    } catch (Exception e) {
                        searchHit.getSourceAsMap().put("statusName", tokenName2);
                        e.printStackTrace();
                        logger.info("通过请求web3j获取币名失败...");
                    }
                } else {
                    searchHit.getSourceAsMap().put("statusName", tokenName2); //从配置文件中读取币名
                }

                list.add(searchHit.getSourceAsMap());
            }

            Map<String, Object> map = new HashMap<>();
            map.put("total", hits.getTotalHits());
            list2.add(map);

            list3.add(list);
            list3.add(list2);

            return ResponseResult.build(200, "Get Conditional Transaction Success", list3);

        }

        return ResponseResult.build(201, "Get Conditional Transaction Failed");

    }
}
