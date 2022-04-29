package com.sunzy.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.sunzy.reggie.common.BaseContext;
import com.sunzy.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 判断员工是否登录
 */
@WebFilter(filterName = "LoginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    public static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String requestUri = request.getRequestURI();
        log.info("请求的uri {}", requestUri);
        // 判断请求是否需要处理, 一下请求不需要处理
        String[] urls = new String[]{
            "/employee/login",
            "/employee/logout",
//            "/employee/**",
            "/backend/**",
            "/front/**",
            "/common/**",
            "/user/login",
            "/user/sendMsg"
        };

        // 对以上请求放行
        if(check(urls, requestUri)){
            log.info("放行操作");
            filterChain.doFilter(request, response);
            return;
        }
        // 判断员工是否登录
        if(request.getSession().getAttribute("employee") != null){
            log.info("用户已登录！");


            Long id = (Long) request.getSession().getAttribute("employee");
            log.info("登录用户的ID:{}", id);
            BaseContext.setCurrentId(id);
            filterChain.doFilter(request, response);
            return;
        }

        if(request.getSession().getAttribute("user") != null){
            Long id = (Long) request.getSession().getAttribute("user");
            log.info("登录用户的ID:{}", id);
            BaseContext.setCurrentId(id);
            log.info("BaseContextid:{}", BaseContext.getCurrentId());
            log.info("用户已登录！");
            filterChain.doFilter(request, response);

            return;
        }
        // 用户未登录 返回相应数据
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    public boolean check(String[] urls, String uri){
        for(String url : urls){
            if(ANT_PATH_MATCHER.match(url, uri)){
                return true;
            }
        }
        return false;
    }
}
