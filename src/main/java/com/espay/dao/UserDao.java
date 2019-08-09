package com.espay.dao;

import com.espay.pojo.User;

import java.util.List;
import java.util.Map;

/**
 * 用户管理Mapper
 */
public interface UserDao {
    /**
     * 分页查询
     * @param map
     * @return
     */
    List<User> getUserListByLimit(Map<String, Object> map);

    /**
     * 查询用户总数
     * @return
     */
    Integer getUserCount(String username);

    /**
     * 新增用户
     * @param user
     * @return
     */
    Integer addUser(User user);

    /**
     * 用户编辑
     * @param user
     */
    void editUser(User user);

    /**
     * 根据用户名查询
     * @param username
     * @return
     */
    List<User> findUserByName(String username);

    /**
     * 根据邮箱查询
     * @param email
     * @return
     */
    List<User> findUserByEmail(String email);
}