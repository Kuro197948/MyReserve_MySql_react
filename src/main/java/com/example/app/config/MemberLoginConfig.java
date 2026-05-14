package com.example.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class MemberLoginConfig {

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      .cors(cors -> {})
      .authorizeHttpRequests(auth -> auth
        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
        .requestMatchers(
          "/loginhome",
          "/members/**",
          "/admins/**",
          "/api/**",
          "/css/**", "/js/**", "/images/**"
        ).permitAll()
        .anyRequest().permitAll()
      )
      .formLogin(form -> form.disable())
      .httpBasic(basic -> basic.disable())
      .csrf(csrf -> csrf.disable());

    return http.build();
  }
}