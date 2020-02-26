package com.pwnpub.search.web;

import com.pwnpub.search.pojo.BlockEntityAll;
import com.pwnpub.search.pojo.TransactionEntityAll;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 *
 * @date 2019-01-21 5:17 PM
 * @desc es curd
 */
@RestController
@RequestMapping("Manager")
public class ManagerController {

    @Autowired
    private TransportClient client;

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

}
