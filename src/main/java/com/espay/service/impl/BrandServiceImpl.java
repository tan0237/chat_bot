package com.espay.service.impl;

import com.espay.dao.BrandDao;
import com.espay.pojo.Brand;
import com.espay.service.IBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandServiceImpl implements IBrandService {
    @Autowired
    private BrandDao brandDao;

    @Override
    public Brand getBrandByName(String name) {
        return brandDao.getBrandByName(name);
    }

    @Override
    public List<Brand> getBrands() {
        return brandDao.getBrands();
    }

    @Override
    public Integer addBrand(String name) {
        Brand brand = new Brand();
        brand.setBrand_name(name);
        brandDao.addBrand(brand);
        return brand.getId();
    }
}
