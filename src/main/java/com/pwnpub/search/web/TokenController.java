package com.pwnpub.search.web;

import com.pwnpub.search.pojo.TokenEntity;
import com.pwnpub.search.utils.CommonUtils;
import com.pwnpub.search.utils.ResponseResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.protocol.Web3j;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pwnpub.search.entity.EsTableEnum.ERC20;

/**
 * @author soobeenwong
 * @date 2019-06-24 11:08
 * @desc 钱包管理类
 */

@RestController
@RequestMapping("/Token")
public class TokenController {

    private static final Logger logger = LogManager.getLogger(TokenController.class);

    @Autowired
    private TransportClient client;

    @Autowired
    Web3j web3j;

    @Autowired
    ConfigurableApplicationContext configurableApplicationContext;


    //获取所有erc20的合约地址
    @GetMapping("/getTokenBalance")
    public ResponseResult queryERC20TokenContract(@RequestParam(name = "address", required = true) String address) {

        /**
         * 统计出共有多少代币并且遍历
         */
        BoolQueryBuilder boolQueryBuilderToken = new BoolQueryBuilder();
        boolQueryBuilderToken.must(new TermQueryBuilder("status", "erc20"));
        //聚合处理
        SearchSourceBuilder sourceBuilderToken = new SearchSourceBuilder();
        TermsAggregationBuilder termsAggregationBuilderToken = AggregationBuilders.terms("group_token_count").field("address");
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

        List<TokenEntity> listToken = new ArrayList<>();
        for (Terms.Bucket token : termsToken.getBuckets()) {
            Object key = token.getKey();

            String tokenBalance = CommonUtils.getTokenBalance(web3j, address, key.toString()).toString();
            if (!tokenBalance.equals("0")) {

                String tokenName = configurableApplicationContext.getEnvironment().getProperty(key.toString());
                if (StringUtils.isEmpty(tokenName)) {
                    //链上的币名
                    tokenName = CommonUtils.getTokenName(web3j, key.toString());
                }
                listToken.add(new TokenEntity(tokenName, tokenBalance, key.toString()));
            }
        }

        return ResponseResult.build(200, "success", listToken);
    }
}
