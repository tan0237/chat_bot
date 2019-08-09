package com.espay.service.impl;

import com.espay.dao.BrandDao;
import com.espay.dao.UserDao;
import com.espay.pojo.Brand;
import com.espay.pojo.User;
import com.espay.service.IUserService;
import com.espay.util.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    public UserDao userDao;

    @Autowired
    public BrandDao brandDao;

    @Override
    public Map<String, Object> getUserListByLimit(Map<String, Object> map) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("items", userDao.getUserListByLimit(map));
        resultMap.put("rows", userDao.getUserCount(map.get("username").toString()));
        return resultMap;
    }

    @Override
    public List<User> findUserByEmail(String email) {
        return userDao.findUserByEmail(email);
    }

    @Override
    public Integer addUser(String username, String password, String email, String brandName,boolean root,boolean active) {
        User user = new User();
        try {
            SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
            format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
            Brand brand = brandDao.getBrandByName(brandName);
            if(brand != null){
                user.setBrandId(brand.getId());
            }else{
                brand = new Brand();
                brand.setBrand_name(brandName);
                brandDao.addBrand(brand);
                user.setBrandId(brand.getId());
            }
            user.setPassword(Md5Util.EncoderByMd5(password));
            user.setUsername(username);
            user.setEmail(email);
            user.setActive(active);
            user.setRoot(root);
            String now = format.format(new Date());
            user.setCreateTime(now);
            user.setLastSince(now);
            userDao.addUser(user);
        }catch (Exception e){
            e.printStackTrace();
        }
        return user.getId();
    }

    @Override
    public List<User> getUserByName(String name) {
        return userDao.findUserByName(name);
    }

    @Override
    public void editUser(User user) {
        userDao.editUser(user);
    }
}
