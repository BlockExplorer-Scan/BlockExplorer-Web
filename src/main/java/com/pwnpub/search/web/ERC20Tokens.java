package com.pwnpub.search.web;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/queryERC20TokenTransfers")
    public ResponseResult queryERC20ByContractAddress(
            @RequestParam(name = "contractAddress", required = true )String contractAddress,
            @RequestParam(name = "pageStart", required = false, defaultValue = "0")Integer pageStart,
            @RequestParam(name = "pageNum", required = false, defaultValue = "20")Integer pageNum

    ){
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

                Object transactionHash = hit.getSourceAsMap().get("transactionHash");
                //web3  获取quantity


                hit.getSourceAsMap().put("statusName", coinName.getErc20Name());

                list.add(hit.getSourceAsMap());
            }

        }
        return ResponseResult.build(200, "query erc20 transfer datas success", list);
    }

}
