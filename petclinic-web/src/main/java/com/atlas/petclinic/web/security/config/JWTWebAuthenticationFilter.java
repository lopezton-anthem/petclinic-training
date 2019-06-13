package com.atlas.petclinic.web.security.config;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.antheminc.oss.nimbus.app.extension.config.DefaultClientUserDetails;
import com.antheminc.oss.nimbus.support.JustLogit;
import com.atlas.petclinic.web.security.utils.JWTAuthConstants;
import com.atlas.petclinic.web.security.utils.JWTAuthTokenUtil;

import lombok.RequiredArgsConstructor;

/**
 * 
 * @author Swetha Vemuri
 *
 */
@RequiredArgsConstructor
public class JWTWebAuthenticationFilter extends OncePerRequestFilter{

	private JustLogit logit = new JustLogit(this.getClass());
	
	private final JWTAuthTokenUtil tokenUtil;
	private final ClientUserDetailsServiceImpl userDetailsService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String header = request.getHeader(JWTAuthConstants.HEADER_STRING);
		String username = null;
		String authToken = null;
		
		if (header != null && header.startsWith(JWTAuthConstants.TOKEN_PREFIX)) {
            authToken = header.replace(JWTAuthConstants.TOKEN_PREFIX,"");
            username = tokenUtil.getUsernameFromToken(authToken);
		}
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        	DefaultClientUserDetails userDetails = (DefaultClientUserDetails) userDetailsService.loadUserByUsername(username);
        	if(tokenUtil.validateToken(authToken, userDetails)) {
        		 logit.trace(() -> "@@ webauthtoken valid for logged in user " + userDetails.getUsername()+ " @@ for request "+request.getRequestURI());
        		 UsernamePasswordAuthenticationToken authentication = 
                 		new UsernamePasswordAuthenticationToken(userDetails, null,Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN")));
                 authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                 SecurityContextHolder.getContext().setAuthentication(authentication);
        	}       
        }

        filterChain.doFilter(request, response);
	}
}