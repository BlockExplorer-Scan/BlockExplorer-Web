package com.pwnpub.search.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @author soobeenwong
 * @date 2018-12-13 3:48 PM
 * @desc TestData
 */
public class TestFunction {

    private static final Logger logger = LogManager.getLogger(TestFunction.class);

    public static void main(String[] args) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        long currentTimeMillis = System.currentTimeMillis(); //当前系统时间
        System.out.println(currentTimeMillis * 1000);
        long latestCurrentTimeMillis = currentTimeMillis - 86400000 * 14;

        String currentTime = formatter.format(currentTimeMillis);
        String latestTime = formatter.format(1547928047);
        String latestTime2 = formatter.format(1547937564);

        System.out.println("===================");
        System.out.println("latestTime2"+latestTime);
        System.out.println("+++++++++++++++++");

        System.out.println("current time is: " + currentTime);

        System.out.println("latest time is: "  + latestTime);

        System.out.println();

        Web3j web3 = Web3j.build(new HttpService("http://localhost:8545"));

        //String str = web3.toAscii("g");
        logger.warn("warn");
        logger.info("info");
        logger.debug("debug");
        logger.error("error");

        long time1 = 1547934999;
        String result1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(time1 * 1000));
        System.out.println(result1);



    }

}
