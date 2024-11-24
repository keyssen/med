package com.cpo.med.configuration;

import com.cpo.med.model.request.ProfileDefaultCreateRq;
import com.cpo.med.persistence.entity.enums.ProfileRole;
import com.cpo.med.service.ProfileService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static com.cpo.med.utils.Constants.LOGIN_URL;
import static com.cpo.med.utils.Constants.SIGNUP_URL;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
@EnableWebSecurity
public class SecurityConfiguration {
    private final ProfileService profileService;
    private final AuthenticationSuccessHandler successHandler;
    private final PasswordEncoderConfiguration passwordEncoderConfiguration;

    public SecurityConfiguration(ProfileService profileService, AuthenticationSuccessHandler successHandler, PasswordEncoderConfiguration passwordEncoderConfiguration) {
        this.profileService = profileService;
        this.successHandler = successHandler;
        this.passwordEncoderConfiguration = passwordEncoderConfiguration;
        createAdminOnStartup();
    }

    private void createAdminOnStartup() {
        if (profileService.findByEmail("nonomusay@gmail.com") == null) {
            ProfileDefaultCreateRq profileDefaultCreateRq = new ProfileDefaultCreateRq(
                    "adminPassword",
                    "nonomusay@gmail.com",
                    false
            );
            profileService.create(profileDefaultCreateRq, ProfileRole.ADMINISTRATOR);
        }

    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(SIGNUP_URL).permitAll()
                        .requestMatchers(HttpMethod.GET, LOGIN_URL).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage(LOGIN_URL)
                        .permitAll()
                        .successHandler(successHandler)
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage(LOGIN_URL)
                        .successHandler(successHandler)
                )
                .logout(logoutConfigurer -> logoutConfigurer.invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/login?logout")
                        .permitAll());
        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(profileService);
        authProvider.setPasswordEncoder(passwordEncoderConfiguration.createPasswordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http
                .getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(profileService);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers("/css/**")
                .requestMatchers("/js/**")
                .requestMatchers("/templates/**")
                .requestMatchers("/webjars/**");
    }
}
