/**
 *  Copyright 2016-2018 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.atlas.petclinicgateway.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

@Configuration
@EnableOAuth2Sso
@Order(value = 0)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	/*
	 * See https://www.baeldung.com/spring-security-oauth2-enable-resource-server-vs-enable-oauth2-sso
	 */
	
	@Autowired
    private ResourceServerTokenServices resourceServerTokenServices;
	
	@Override
    public void configure(HttpSecurity http) throws Exception { 
        http.csrf().disable()
            .authorizeRequests()
            .antMatchers("/auth-server/**",
              "/login").permitAll()
            .anyRequest().authenticated().and()
            .logout().permitAll().logoutSuccessUrl("/");
    }
}