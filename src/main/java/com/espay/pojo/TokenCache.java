package com.espay.pojo;

import java.util.Date;

/**
 * 邮箱验证缓存
 */
public class TokenCache {
    private String token;
    private String email;
    private Date createAt;

    public TokenCache(String token, String email, Date createAt) {
        this.token = token;
        this.email = email;
        this.createAt = createAt;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

}
