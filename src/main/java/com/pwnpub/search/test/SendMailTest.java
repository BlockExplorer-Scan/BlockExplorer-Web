package com.pwnpub.search.test;

import com.pwnpub.search.config.EmailConfig;
import com.pwnpub.search.entity.EmailEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author soobeenwong
 * @date 2019-03-26 10:46 AM
 * @desc 测试邮件发送
 */
@RestController
@RequestMapping("/Email")
public class SendMailTest {

    private static Logger log = LoggerFactory.getLogger(SendMailTest.class);

    @Autowired
    EmailConfig mc;

    @PostMapping("/send")
    public void testEmailConfig(){

        try {
            int i = 0;
            int i1 = 5/i;
            System.out.println(i1);
        } catch (Throwable e){
            e.printStackTrace();
            EmailEntity email = new EmailEntity();
            email.setReceiver("soobeencn@gmail.com");
            email.setSubject("异常信息汇报");
            email.setContent(getEmessage(e)); //邮件内容

            //mc.sendSimpleMail(email);
            log.info("异常信息发送至邮件成功，消息内容为：" + getEmessage(e));
        }
    }

    private static String getEmessage (Throwable throwable){
        //StringWriter输出异常信息
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        pw.flush();
        sw.flush();
        return sw.toString();
    }


}
