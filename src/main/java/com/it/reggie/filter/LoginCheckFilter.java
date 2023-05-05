package com.it.reggie.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest ;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1. 获取本次请求的URI
        //2. 判断







        log.info("拦截到请求 {}" ,request.getRequestURI());
        filterChain.doFilter(request,response);
    }
}
