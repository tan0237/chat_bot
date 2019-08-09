package com.espay.util;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class LoginInterceptor implements HandlerInterceptor {
    /**
     * Handler执行完成之后调用这个方法
     */
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response, Object handler, Exception exc)throws Exception {

    }

    /**
     * Handler执行之后，ModelAndView返回之前调用这个方法
     */
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView){

    }

    /**
     * Handler执行之前调用这个方法
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");

        String auth = request.getHeader("Authorization");
        //获取Session
        HttpSession session = request.getSession();
        Map token = (Map<String, Object>) session.getAttribute("token");
        PrintWriter out = null;

        if(auth == null || token == null || !auth.equals(token.get("token"))){
            Map<String, Integer> map = new HashMap<>();
            map.put("statusCode", 502);
            out = response.getWriter();
            out.write(JSONObject.toJSONString(map));
            out.flush();
            out.close();
            return false;
        }
        return true;
    }

}
