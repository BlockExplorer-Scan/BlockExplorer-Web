package com.pwnpub.search.web;

import com.alibaba.fastjson.JSONObject;
import com.pwnpub.search.utils.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author soobeenwong
 * @date 2019-03-08 6:40 PM
 * @desc 提供socket查询的接口
 */
@RestController
@RequestMapping("/Socket")
public class SocketController {

    @Autowired
    ConfigurableApplicationContext configurableApplicationContext;

    @GetMapping("/getMainCoinName")
    public ResponseResult getMainCoinName(){

        String tokenName = configurableApplicationContext.getEnvironment().getProperty("maincoinName");

        return ResponseResult.build(200, "Socket获取主币名称成功", tokenName);
    }


    @GetMapping("/getConfig")
    public ResponseResult getConfig(){

        String hideErc20Address = configurableApplicationContext.getEnvironment().getProperty("hideErc20Address");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("hideErc20Address",hideErc20Address);

        return ResponseResult.build(200, "getConfig", jsonObject);
    }

}
