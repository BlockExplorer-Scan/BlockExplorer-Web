package com.pwnpub.search.entity;

/**
 * @author soobeenwong
 * @date 2019-03-26 10:44 AM
 * @desc 邮件实体类
 */
public class EmailEntity {

    private String receiver;
    private String subject;
    private String text;
    private String content;

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
