package com.espay.controller;

import com.espay.pojo.ResultInfo;
import com.espay.service.IBrandService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 品牌controller
 */
@Controller
@RequestMapping("/brand")
public class BrandController {
    @Autowired
    private IBrandService iBrandService;

    private Logger logger = Logger.getLogger(BrandController.class);

    /**
     * 查询品牌列表
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public ResultInfo getBrands(){
        ResultInfo resultInfo = new ResultInfo();
        try {
            resultInfo.setData(iBrandService.getBrands());
            resultInfo.setStatusCode(200);
            resultInfo.setMessage("查询品牌成功");
        }catch (Exception e){
            resultInfo.setMessage("查询失败");
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return resultInfo;
    }
}
