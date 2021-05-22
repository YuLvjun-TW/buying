package com.muke.buying.config;

import com.muke.buying.service.MyAuthenticationFailureHandler;
import com.muke.buying.service.MyAuthenticationSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// 开启方法级注解安全验证
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 表单提交
        http.formLogin()
                //自定义登录页面
                .loginPage("/login.html")
                //设置走自定义登录逻辑，必须和表单提交的借口一样
                .loginProcessingUrl("/login")
//                .successForwardUrl("/home")
                .successHandler(new MyAuthenticationSuccessHandler("/home.html"))
//                .failureForwardUrl("/error")
                .failureHandler(new MyAuthenticationFailureHandler("/error.html"));

        // 授权
        http.authorizeRequests()
                // 放行
                .antMatchers("/error.html").permitAll()
                .antMatchers("/login.html").permitAll()
//                .antMatchers("/user").hasAnyAuthority("user", "admin")
//                .antMatchers("/admin").hasAnyAuthority("admin", "customer")
                // hasRole()会自动加上前缀ROLE_
//                .antMatchers("/user").hasRole("user")
//                .antMatchers("/admin").hasRole("admin")
                // 所有请求都必须认证才能访问，必须登录
                .anyRequest().authenticated();

        // 关闭CSRF跨域
        http.csrf().disable();
    }

    @Bean
    public PasswordEncoder getPw() {
        return new BCryptPasswordEncoder();
    }
}
