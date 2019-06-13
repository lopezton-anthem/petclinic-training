/**
 * 
 */
package com.atlas.petclinic.web.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.antheminc.oss.nimbus.domain.session.SessionProvider;
import com.atlas.petclinic.web.security.utils.JWTAuthTokenUtil;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
	
	// uncomment to enable security and ensure that auth token is passed to maintain session 
		http.authorizeRequests().antMatchers("/login","/petclinic/**").permitAll()
		//	.anyRequest().authenticated()
			.and()
		//	.addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
			.csrf().disable();
	}
	
	@Override
	@Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Autowired
    public void globalUserDetails(AuthenticationManagerBuilder auth, SessionProvider sessionProvider) throws Exception {
        auth.userDetailsService(clientUserDetailsService(sessionProvider));
    }
    
    @Bean
	public ClientUserDetailsServiceImpl clientUserDetailsService(SessionProvider sessionProvider) {
    	return new ClientUserDetailsServiceImpl(sessionProvider);
	}
    
    @Bean
    public JWTAuthTokenUtil jwtAuthTokenUtil() {
    	return new JWTAuthTokenUtil();
    }
    
    @Bean
    public JWTWebAuthenticationFilter jwtWebAuthenticationFilter(ClientUserDetailsServiceImpl userDetailsService) {
    	return new JWTWebAuthenticationFilter(jwtAuthTokenUtil(), userDetailsService);
    }
}
