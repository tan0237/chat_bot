package com.espay.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Document(collection = "custom_service_chat")
public class CustomServiceChat {
    @Id
    private String id;
    @Field(value = "customerId")
    private String customerId;
    @Field(value = "serviceId")
    private String serviceId;
    @Field(value = "customerName")
    private String customerName;
    @Field(value = "serviceName")
    private String serviceName;
    @Field(value = "customIp")
    private String customIp;
    @Field(value = "recordTime")
    private Date recordTime;
    @Field(value = "chatRecordList")
    private List<ChatRecord> chatRecordList;
    @Field(value = "satisfaction")
    private  int satisfaction;

    public CustomServiceChat() {
    }

    public CustomServiceChat(String id, String customerId, String serviceId, String customerName, String serviceName,
                             String customIp, Date recordTime, List<ChatRecord> chatRecordList, int satisfaction) {
        this.id = id;
        this.customerId = customerId;
        this.serviceId = serviceId;
        this.customerName = customerName;
        this.serviceName = serviceName;
        this.customIp = customIp;
        this.recordTime = recordTime;
        this.chatRecordList = chatRecordList;
        this.satisfaction = satisfaction;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getCustomIp() {
        return customIp;
    }

    public void setCustomIp(String customIp) {
        this.customIp = customIp;
    }

    public Date getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }

    public List<ChatRecord> getChatRecordList() {
        return chatRecordList;
    }

    public void setChatRecordList(List<ChatRecord> chatRecordList) {
        this.chatRecordList = chatRecordList;
    }

    public int getSatisfaction() {
        return satisfaction;
    }

    public void setSatisfaction(int satisfaction) {
        this.satisfaction = satisfaction;
    }
}
