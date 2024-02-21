package ir.maktabsharif.config;

import ir.maktabsharif.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;

    public SecurityConfiguration(UserService userService, BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(a -> a.requestMatchers("admin/**").hasRole("ADMIN"));
        httpSecurity.authorizeHttpRequests(a -> a.requestMatchers("customer/**").hasRole("CUSTOMER"));
        httpSecurity.authorizeHttpRequests(a -> a.requestMatchers("tradesman/**").hasRole("TRADESMAN").anyRequest().permitAll());
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        httpSecurity.cors(AbstractHttpConfigurer::disable);
        httpSecurity.httpBasic(Customizer.withDefaults());
        httpSecurity.formLogin(Customizer.withDefaults());
        return httpSecurity.build();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService
                (
                        username -> userService.findByUserName(username)
                                .orElseThrow(
                                        () -> new UsernameNotFoundException
                                                (String.format("%s not found!", username))
                                )
                ).passwordEncoder(passwordEncoder);
    }


}
