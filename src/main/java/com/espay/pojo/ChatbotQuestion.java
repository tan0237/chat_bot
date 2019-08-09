package com.espay.pojo;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


import java.util.Date;

@Document
public class ChatbotQuestion {
    @Field(value = "text")
    private String text;
    @Field(value = "created_at")
    private Date createAt;
    @Field(value = "occurence")
    private int occurrence;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public int getOccurrence() {
        return occurrence;
    }

    public void setOccurrence(int occurrence) {
        this.occurrence = occurrence;
    }
}
