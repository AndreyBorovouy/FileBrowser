package com.waverley.fileBrowser.filter;

import com.waverley.fileBrowser.service.api.UserService;
import com.waverley.fileBrowser.service.impl.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by anton.kovalenko on 12/26/17.
 */
@Component
public class AuthFilter implements Filter {

    @Autowired
    private AuthService authService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        if (authService.checkTimeChanging()) {
            servletRequest.getRequestDispatcher("/home").forward(servletRequest, servletResponse);
        }
        else if (!SecurityContextHolder.getContext().getAuthentication().isAuthenticated()){
            authService.authenticate();
            servletRequest.getRequestDispatcher("/home").forward(servletRequest, servletResponse);
        }
        else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {

    }
}
