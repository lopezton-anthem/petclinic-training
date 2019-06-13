package com.atlas.petclinic.web.security.utils;

import java.io.Serializable;
import java.util.Date;

import com.antheminc.oss.nimbus.app.extension.config.DefaultClientUserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * 
 * @author Swetha Vemuri
 *
 */
public class JWTAuthTokenUtil implements Serializable {

	private static final long serialVersionUID = -7334582314902071663L;

	public String generateToken(DefaultClientUserDetails subject) {
		String token = Jwts.builder().setSubject(subject.getUsername())
				.setExpiration(new Date(System.currentTimeMillis() + JWTAuthConstants.EXPIRATION_TIME))
				.signWith(SignatureAlgorithm.HS512, JWTAuthConstants.SECRET).compact();

		return token;
	}

	public Date getExpirationDateFromToken(String token) {
		Date expiration;
		try {
			final Claims claims = getAllClaimsFromToken(token);
			expiration = claims.getExpiration();
		} catch (Exception e) {
			expiration = null;
			e.getStackTrace();
		}
		return expiration;
	}

	public String getUsernameFromToken(String token) {
		String username;
		try {
			final Claims claims = getAllClaimsFromToken(token);
			username = claims.getSubject();
		} catch (Exception e) {
			username = null;
			e.getStackTrace();
		}
		return username;
	}

	public Boolean validateToken(String token, DefaultClientUserDetails userDetails) {
		final String username = getUsernameFromToken(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(JWTAuthConstants.SECRET).parseClaimsJws(token).getBody();
	}

	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

}