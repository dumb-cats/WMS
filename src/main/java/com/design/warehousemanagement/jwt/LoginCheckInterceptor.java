package com.design.warehousemanagement.jwt;

import com.alibaba.fastjson.JSONObject;

import com.design.warehousemanagement.common.Result;
import com.design.warehousemanagement.anno.RequiresRole;
import com.design.warehousemanagement.util.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
@Slf4j
@Component
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override//目标资源运行前运行，true=放行，false=不放行
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
        String url=req.getRequestURL().toString();
        log.info("请求的url:{}",url);

        if (url.matches(".*/login/\\d+")) {
            log.info("登录操作：放行...");
            return true;
        }
        if (url.matches("http://localhost:3000/user/registerRole_2")) {
            log.info("注册操作：放行...");
            return true;
        }
       //放行OPTIONS请求
        String method = req.getMethod();
        if ("OPTIONS".equals(method)) {
            return true;
        }
        String jwt=req.getHeader("token");
        log.info(jwt);
        log.info("token");
        if (!StringUtils.hasLength(jwt)){
            log.info("请求头token为空，返回未登录信息");
            Result error= Result.error("NOT_LOGIN");
            //手动转换 对象--json ------>阿里巴巴fastJSON
            String notLogin= JSONObject.toJSONString(error);
            resp.getWriter().write(notLogin);
            return false;
        }
        Claims claims;
        try{
            claims = JwtUtils.parseJWT(jwt);
        }catch (Exception e){
            e.printStackTrace();
            log.info("解析令牌失败，返回未登录的错误信息");
            Result error=Result.error("NOT_NULL");
            String notLogin= JSONObject.toJSONString(error);
            resp.getWriter().write(notLogin);
            return false;
        }

        // 角色权限校验逻辑
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            RequiresRole requiresRole = handlerMethod.getMethodAnnotation(RequiresRole.class);
            if (requiresRole != null) {
                Integer userRole = claims.get("roleId", Integer.class);
                log.info("用户角色ID: {}, 需要角色ID: {}", userRole, requiresRole.value());
                if (userRole == null || userRole < requiresRole.value()) {
                    log.info("权限不足，拒绝访问");
                    Result error = Result.error("FORBIDDEN");
                    String forbidden = JSONObject.toJSONString(error);
                    resp.setStatus(403);
                    resp.getWriter().write(forbidden);
                    return false;
                }
            }
        }

        log.info("令牌合法，放行");
        return true;
    }

    @Override//目标资源运行后运行
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
 /*       System.out.println("目标资源运行后运行...");*/
    }

    @Override//视图渲染完毕后运行，最后运行
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
     /*   System.out.println("视图渲染完毕后运行，最后运行...");*/
    }
}
