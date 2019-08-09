package com.espay.controller;

import com.alibaba.fastjson.JSONObject;
import com.espay.pojo.Brand;
import com.espay.pojo.ResultInfo;
import com.espay.pojo.TokenCache;
import com.espay.pojo.User;
import com.espay.service.IBrandService;
import com.espay.service.IUserService;
import com.espay.util.MailUtil;
import com.espay.util.Md5Util;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private IUserService userService;
    @Autowired
    private IBrandService brandService;

    @Autowired
    private JavaMailSenderImpl mailSender;

    @Autowired
    private MongoTemplate mongoTemplate;


    private static Logger logger = Logger.getLogger(UserController.class);

    private static MailUtil mailUtil = new MailUtil();

    private ResultInfo verification(User user) {
        ResultInfo resultInfo = new ResultInfo();
        //用户名格式2~16位
        if (user.getUsername() != null && !user.getUsername().matches("^[\\u4e00-\\u9fa5_a-zA-Z][\\u4e00-\\u9fa5_a-zA-Z0-9]{1,15}$")) {
            resultInfo.setStatusCode(300);
            resultInfo.setMessage("用户名为2-16位中文英文数字下划线组合");
            return resultInfo;
        }
        //密码格式数字字母下划线
        if (user.getPassword() != null && !user.getPassword().matches("[a-zA-Z0-9_]{6,16}$")) {
            resultInfo.setMessage("密码为6-16位字母数字下划线组合");
            resultInfo.setStatusCode(300);
            return resultInfo;
        }
        //邮箱格式验证
        if (user.getEmail() != null && !user.getEmail().matches("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$")) {
            resultInfo.setStatusCode(300);
            resultInfo.setMessage("邮箱格式不正确");
            return resultInfo;
        }

        if (user.getRoot() == null || user.getActive() == null) {
            resultInfo.setMessage("信息不完整");
            resultInfo.setStatusCode(300);
            return resultInfo;
        }
        //校验用户名唯一性
        if (user.getUsername() != null) {
            List<User> user_name = userService.getUserByName(user.getUsername());
            if (user_name.size() > 0) {
                resultInfo.setMessage("用户名已存在");
                resultInfo.setStatusCode(300);
                return resultInfo;
            }
        }
        //校验邮箱唯一性
        if (user.getEmail() != null) {
            List<User> user_email = userService.findUserByEmail(user.getEmail());
            if (user_email.size() > 0) {
                resultInfo.setMessage("邮箱已存在");
                resultInfo.setStatusCode(300);
                return resultInfo;
            }
        }
        resultInfo.setStatusCode(200);
        resultInfo.setMessage("验证通过");
        return resultInfo;
    }

    /**
     * 用户查询
     *
     * @param map
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getUsersByPage", method = RequestMethod.POST)
    public ResultInfo getUsersByPage(@RequestBody Map<String, Object> map, HttpServletRequest request) {
        ResultInfo resultInfo = new ResultInfo();
        try {
            //验证管理员权限
            resultInfo = verifyUserRight(request);
            if(resultInfo.getStatusCode() != null){
                return resultInfo;
            }

            String username = map.get("username") == null ? "" : map.get("username").toString();
            String pageIndex = map.get("pageIndex").toString();
            String pageSize = map.get("pageSize").toString();
            Map<String, Object> paraMap = new HashMap<>();
            paraMap.put("username", username);
            paraMap.put("pageIndex", (Integer.parseInt(pageIndex) - 1) * Integer.parseInt(pageSize));
            paraMap.put("pageSize", Integer.parseInt(pageSize));
            Map<String, Object> resultMap = userService.getUserListByLimit(paraMap);
            resultInfo.setMessage("用户列表查询成功");
            resultInfo.setStatusCode(200);
            resultInfo.setData(resultMap);
        } catch (Exception e) {
            resultInfo.setMessage("用户查询失败");
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return resultInfo;
    }

    /**
     * 新增用户
     *
     * @param userJson
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public ResultInfo addUser(@RequestBody JSONObject userJson, HttpServletRequest request) {
        ResultInfo resultInfo = new ResultInfo();
        try {
            //验证管理员权限
            resultInfo = verifyUserRight(request);
            if(resultInfo.getStatusCode() != null){
                return resultInfo;
            }

            String username = userJson.getString("username");
            String password = userJson.getString("password");
            String email = userJson.getString("email");
            String brandName = userJson.getString("brand");
            Boolean root = userJson.getBoolean("root");
            Boolean active = userJson.getBoolean("active");
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(email);
            user.setActive(active);
            user.setRoot(root);
            resultInfo = verification(user);
            if (resultInfo.getStatusCode() == 200) {
                Brand brand = brandService.getBrandByName(brandName);
                if (brand == null) {
                    brandService.addBrand(brandName);
                }
                resultInfo.setData(userService.addUser(username, password, email, brandName, root, active));
                resultInfo.setMessage("新增用户成功");
                resultInfo.setStatusCode(200);
            }
        } catch (Exception e) {
            resultInfo.setMessage("新增用户失败");
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return resultInfo;
    }

    /**
     * 编辑用户
     *
     * @param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public ResultInfo changeUserStatus(@RequestBody JSONObject userJson, HttpServletRequest request) {
        ResultInfo resultInfo = new ResultInfo();
        User user = new User();
        try {
            String password = userJson.getString("password");
            String email = userJson.getString("email");
            String brandName = userJson.getString("brand");
            Boolean root = userJson.getBoolean("root");
            Boolean active = userJson.getBoolean("active");
            Integer id=userJson.getInteger("id");
            user.setPassword(password);
            user.setEmail(email);
            user.setActive(active);
            user.setRoot(root);
            user.setId(id);
            resultInfo = verification(user);
            if (resultInfo.getStatusCode() != 200){
                return resultInfo;
            }

            if(StringUtils.hasText(password)){
                user.setPassword(Md5Util.EncoderByMd5(password));
            }

            if(brandName != null){
                Brand brand = brandService.getBrandByName(brandName);
                if (brand == null) {
                    Integer brandId = brandService.addBrand(brandName);
                    user.setBrandId(brandId);
                } else {
                    user.setBrandId(brand.getId());
                }
            }

            userService.editUser(user);
            resultInfo.setStatusCode(200);
            resultInfo.setMessage("更改成功");
        } catch (Exception e) {
            resultInfo.setMessage("更改失败");
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return resultInfo;
    }

    /**
     * 用户登陆
     *
     * @param map
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ResultInfo login(@RequestBody Map<String, String> map, HttpServletRequest request) {
        ResultInfo resultInfo = new ResultInfo();
        try {
            String username = map.get("username");
            String password = map.get("password");
            List<User> list = userService.getUserByName(username);
            if (list.size() > 0 && list.get(0).getActive() && Md5Util.checkPass(password, list.get(0).getPassword())) {
                User user = list.get(0);
                SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss");
                format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
                user.setLastSince(format.format(new Date()));
                userService.editUser(user);
                String token = UUID.randomUUID().toString().replace("-", "").toLowerCase();
                Map<String, Object> cacheMap = new HashMap<>();
                cacheMap.put("token", token);
                cacheMap.put("root", list.get(0).getRoot());
                request.getSession().setAttribute("token", cacheMap);
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("token", token);
                resultMap.put("root", user.getRoot());
                resultMap.put("id", user.getId());
                resultMap.put("username", username);
                resultMap.put("brandId", user.getBrandId());
                resultInfo.setData(resultMap);
                resultInfo.setStatusCode(200);
                resultInfo.setMessage("登陆成功");
            } else {
                resultInfo.setMessage("用户名或密码错误");
            }
        } catch (Exception e) {
            resultInfo.setMessage("登陆失败");
            logger.error(e.getMessage());
            e.printStackTrace();
        }

        return resultInfo;
    }

    /**
     * 用户名唯一性验证
     *
     * @param username
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "username_unique", method = RequestMethod.GET)
    public ResultInfo nameUnique(String username) {
        ResultInfo resultInfo = new ResultInfo();
        List<User> user = userService.getUserByName(username);
        if (user.size() == 0) {
            resultInfo.setStatusCode(200);
        } else {
            resultInfo.setStatusCode(300);
            resultInfo.setMessage("用户名已存在");
        }
        return resultInfo;
    }

    /**
     * 邮箱唯一性校验
     *
     * @param email
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "email_unique", method = RequestMethod.GET)
    public ResultInfo emailUnique(String email) {
        ResultInfo resultInfo = new ResultInfo();
        List<User> user = userService.findUserByEmail(email);
        if (user.size() == 0) {
            resultInfo.setStatusCode(200);
        } else {
            resultInfo.setStatusCode(300);
            resultInfo.setMessage("邮箱已存在");
        }
        return resultInfo;
    }


    /**
     * 邮箱重置密码
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "resetPass", method = RequestMethod.GET)
    public ResultInfo restPass(HttpServletRequest request) {
        ResultInfo resultInfo = new ResultInfo();
        try {
            String token = request.getParameter("token");
            String password = request.getParameter("password");
            TokenCache cache = mongoTemplate.find(new Query(where("token").is(token)), TokenCache.class).get(0);
            if (cache != null) {
                User user = userService.findUserByEmail(cache.getEmail()).get(0);
                user.setPassword(Md5Util.EncoderByMd5(password));
                userService.editUser(user);
                resultInfo.setMessage("密码重置成功");
                resultInfo.setStatusCode(200);
            } else {
                resultInfo.setMessage("信息已过期，请保证在发送邮件后30分钟内及时修改密码");
                resultInfo.setStatusCode(300);
            }
        } catch (Exception e) {
            resultInfo.setStatusCode(500);
            resultInfo.setMessage("重置密码失败");
            e.printStackTrace();
        }
        return resultInfo;
    }

    @ResponseBody
    @RequestMapping(value = "sendEmail", method = RequestMethod.GET)
    public ResultInfo sendEmail(HttpServletRequest request) {
        ResultInfo resultInfo = new ResultInfo();
        String to = request.getParameter("to");
        try {
            List<User> list = userService.findUserByEmail(to);
            if (list.size() == 0) {
                resultInfo.setMessage("此邮箱未注册");
                resultInfo.setStatusCode(500);
                return resultInfo;
            }
            mailUtil.sendEmail(to, mailSender, request, mongoTemplate);
            resultInfo.setStatusCode(200);
            resultInfo.setMessage("邮件已经发送，到收件箱查看吧，收件箱没有垃圾邮件里肯定有!");
        } catch (Exception e) {
            resultInfo.setMessage("邮件发送失败");
            resultInfo.setStatusCode(500);
            e.printStackTrace();
        }
        return resultInfo;
    }

    /**
     * 验证用户权限
     * @param request
     * @return
     */
    private ResultInfo verifyUserRight(HttpServletRequest request){
        ResultInfo resultInfo = new ResultInfo();
        HttpSession session = request.getSession();
        Map map = (Map<String, Object>)session.getAttribute("token");
        if(map != null && (Boolean)map.get("root")){
            return resultInfo;
        }else{
            resultInfo.setStatusCode(300);
            resultInfo.setMessage("权限不足");
        }
        return resultInfo;
    }

}
