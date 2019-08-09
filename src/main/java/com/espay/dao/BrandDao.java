package com.espay.dao;

import com.espay.pojo.Brand;

import java.util.List;

/**
 * 品牌Mapper
 */
public interface BrandDao {
    /**
     * 根据品牌名查询
     * @param name
     * @return
     */
    Brand getBrandByName(String name);

    /**
     * 查询品牌列表
     * @return
     */
    List<Brand> getBrands();

    /**
     * 增加品牌
     * @param brand
     * @return
     */
    Integer addBrand(Brand brand);

}
