package com.espay.service;

import com.espay.pojo.User;

import java.util.List;
import java.util.Map;

public interface IUserService {
    /**
     * 分页查询
     * @param map
     * @return
     */
    Map<String, Object> getUserListByLimit(Map<String, Object> map);

    /**
     * 新增用户
     * @param username
     * @param password
     * @param email
     * @param brandName
     * @return
     */
    Integer addUser(String username, String password, String email, String brandName,boolean root,boolean active);

    /**
     * 编辑用户
     * @param user
     */
    void editUser(User user);

    /**
     * 查询用户
     * @param name
     * @return
     */
    List<User> getUserByName(String name);

    /**
     * 查询用户
     * @param email
     * @return
     */
    List<User> findUserByEmail(String email);
}
