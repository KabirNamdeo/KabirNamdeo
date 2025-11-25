package com.kabir.student.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.kabir.student.config.JwtProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

	public record TokenPair(String token, Instant expiresAt) {
	}

	private final JwtProperties properties;
	private final CustomUserDetailsService userDetailsService;

	public TokenPair generateToken(UserDetails userDetails) {
		Instant issuedAt = Instant.now();
		Instant expiresAt = issuedAt.plusMillis(properties.getExpiration());
		String token = Jwts.builder()
				.setSubject(userDetails.getUsername())
				.claim("roles", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
				.setIssuedAt(Date.from(issuedAt))
				.setExpiration(Date.from(expiresAt))
				.signWith(getSigningKey())
				.compact();
		return new TokenPair(token, expiresAt);
	}

	public boolean validateToken(String token) {
		if (!StringUtils.hasText(token)) {
			return false;
		}
		try {
			Jwts.parserBuilder()
					.setSigningKey(getSigningKey())
					.build()
					.parseClaimsJws(token);
			return true;
		}
		catch (Exception ex) {
			return false;
		}
	}

	public Authentication getAuthentication(String token) {
		Jws<Claims> claims = Jwts.parserBuilder()
				.setSigningKey(getSigningKey())
				.build()
				.parseClaimsJws(token);
		String username = claims.getBody().getSubject();
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	}

	private Key getSigningKey() {
		return Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
	}
}

