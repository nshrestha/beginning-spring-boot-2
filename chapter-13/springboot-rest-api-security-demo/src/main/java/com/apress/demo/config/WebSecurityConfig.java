/**
 * 
 */
package com.apress.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.apress.demo.security.RestAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Siva
 *
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter 
{
       
    @Autowired
    private UserDetailsService customUserDetailsService;
    
    @Autowired
    private RestAuthenticationSuccessHandler authenticationSuccessHandler;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) 
        throws Exception 
    {
        auth
            .userDetailsService(customUserDetailsService)
            .passwordEncoder(passwordEncoder())
            ;
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	http
    		.csrf().disable()
	    	
	        .authorizeRequests()
		        .antMatchers("/","/login","/contact").permitAll()
		        .antMatchers("/api/admin/**").hasRole("ADMIN")
		        .antMatchers("/api/**").authenticated()
		        .and()
        
            .exceptionHandling()
            	.authenticationEntryPoint(new Http401AuthenticationEntryPoint("Basic realm=\"MyApp\""))
            	.and()
            	
            .formLogin()
                .permitAll()
                .loginProcessingUrl("/login")
                .successHandler(authenticationSuccessHandler)
                .failureHandler(new SimpleUrlAuthenticationFailureHandler())
                .and()
            .logout()
                .permitAll()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
             ;
        
    }
}

class Http401AuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final String headerValue;

    public Http401AuthenticationEntryPoint(String headerValue) {
        this.headerValue = headerValue;
    }

    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setHeader("WWW-Authenticate", this.headerValue);
        response.sendError(401, authException.getMessage());
    }
}
