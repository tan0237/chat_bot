package com.espay.service;

import com.espay.pojo.Brand;

import java.util.List;

public interface IBrandService {

    /**
     * 查询单一品牌
     * @param name
     * @return
     */
    Brand getBrandByName(String name);

    /**
     * 查询所有品牌
     * @return
     */
    List<Brand> getBrands();

    /**
     * 新增品牌
     * @param name
     * @return
     */
    Integer addBrand(String name);

}
