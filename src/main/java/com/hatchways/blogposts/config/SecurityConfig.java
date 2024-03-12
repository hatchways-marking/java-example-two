package com.hatchways.blogposts.config;

import com.hatchways.blogposts.exception.JwtAuthenticationEntryPoint;
import com.hatchways.blogposts.service.UserService;
import com.hatchways.blogposts.util.AuthUtil;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.HeaderBearerTokenResolver;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Value("${blogposts.security.public-key}")
  RSAPublicKey rsaPublicKey;

  @Value("${blogposts.security.private-key}")
  RSAPrivateKey rsaPrivateKey;

  @Value("${blogposts.security.token.issuer}")
  private String tokenIssuer;

  @Value("${blogposts.security.token.expiration-time}")
  private Integer tokenExpirationTime;

  private final UserService userService;

  private final JwtAuthenticationEntryPoint authenticationEntryPoint;

  public static final String AUTHENTICATION_HEADER = "x-access-token";

  public SecurityConfig(
      UserService userService, JwtAuthenticationEntryPoint authenticationEntryPoint) {
    this.userService = userService;
    this.authenticationEntryPoint = authenticationEntryPoint;
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http = http.cors().and().csrf().disable();
    http = http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and();

    http.authorizeRequests()
        .antMatchers("/api/login")
        .permitAll()
        .antMatchers("/api/register")
        .permitAll()
        .anyRequest()
        .authenticated()
        .and()
        .oauth2ResourceServer()
        .jwt()
        .and()
        .authenticationEntryPoint(authenticationEntryPoint);
  }

  @Override
  @Bean
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  JwtDecoder jwtDecoder() {
    return NimbusJwtDecoder.withPublicKey(rsaPublicKey).build();
  }

  @Bean
  JwtEncoder jwtEncoder() {
    JWK jwk = new RSAKey.Builder(this.rsaPublicKey).privateKey(this.rsaPrivateKey).build();
    JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));
    return new NimbusJwtEncoder(jwkSource);
  }

  @Bean
  AuthUtil authUtilBean() {
    return new AuthUtil(tokenIssuer, tokenExpirationTime, jwtEncoder());
  }

  @Bean
  BearerTokenResolver bearerTokenResolver() {
    HeaderBearerTokenResolver bearerTokenResolver =
        new HeaderBearerTokenResolver(AUTHENTICATION_HEADER);
    return bearerTokenResolver;
  }
}
