package com.pwnpub.search.test;

import com.pwnpub.search.config.CoinName;
import com.pwnpub.search.utils.ResponseResult;
import com.pwnpub.search.web.ERC20Tokens;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.InternalCardinality;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.protocol.Web3j;

import static com.pwnpub.search.entity.EsTableEnum.ERC20;
import static com.pwnpub.search.entity.EsTableEnum.ERC20TOKEN;

/**
 * @author soobeenwong
 * @date 2019-07-15 19:01
 * @desc 测试公链数据
 */

@RestController
@RequestMapping("/Test")
public class ChainTest {


    @Autowired
    private TransportClient client;

    @Autowired
    private CoinName coinName;

    @Autowired
    ConfigurableApplicationContext configurableApplicationContext;

    @Autowired
    Web3j web3j;

    private static final Logger logger = LogManager.getLogger(ERC20Tokens.class);

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
                    AggregationBuilders.cardinality("to_count").field("to").precisionThreshold(40000);
            sourceBuilder.aggregation(cardinalityAggregationBuilder);
            sourceBuilder.query(boolQueryBuilder);

            //查询索引对象
            SearchRequest searchRequest = new SearchRequest(ERC20.toString());
            searchRequest.types("data");
            searchRequest.source(sourceBuilder);
            SearchResponse response = client.search(searchRequest).get();

            InternalCardinality internalCardinality = response.getAggregations().get("to_count");

            logger.info("账户地址数量 -- {}",internalCardinality.getValue());

            return ResponseResult.build(200, "new query HoldersCounts success", internalCardinality.getValue());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseResult.build(401, "query HoldersCounts failed");

    }


}
