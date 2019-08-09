package com.espay.pojo;

import java.util.Date;

/**
 * 在线客服
 */
public class Staff {
    private String username;
    private Integer brandId;
    private Date createAt;

    public Staff(String username, Integer brandId, Date createAt) {
        this.username = username;
        this.brandId = brandId;
        this.createAt = createAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }
}
