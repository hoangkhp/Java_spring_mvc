// package vn.hoidanit.laptopshop.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
// import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
// import org.springframework.session.security.web.authentication.SpringSessionRememberMeServices;

// import jakarta.servlet.DispatcherType;
// import vn.hoidanit.laptopshop.service.CustomUserDetailsService;
// import vn.hoidanit.laptopshop.service.UserService;

// @Configuration
// @EnableMethodSecurity(securedEnabled = true)
// public class SecurityConfiguration {

//     @Bean
//     public PasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder();
//     }

//     @Bean
//     public UserDetailsService userDetailsService(UserService userService) {
//         return new CustomUserDetailsService(userService);
//     }

//     @Bean
//     public DaoAuthenticationProvider authProvider(
//             PasswordEncoder passwordEncoder,
//             UserDetailsService userDetailsService) {
//         DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//         authProvider.setUserDetailsService(userDetailsService);
//         authProvider.setPasswordEncoder(passwordEncoder);
//         authProvider.setHideUserNotFoundExceptions(false);
//         return authProvider;
//     }

//     @Bean
//     public AuthenticationSuccessHandler customSuccessHandler() {
//         return new CustomSuccessHandler();
//     }


//     @Bean
//     public SpringSessionRememberMeServices rememberMeServices() {
// 	SpringSessionRememberMeServices rememberMeServices =
// 			new SpringSessionRememberMeServices();
// 	// optionally customize
// 	rememberMeServices.setAlwaysRemember(true);
// 	return rememberMeServices;
//     //remember Me se tu dong thay doi thoi gian het han cua session
//     //setCookies gia tri lon
//     //chi su dung trong truong hop khong canlogin
// }


//     @Bean
//     SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//         // v6. lamda
//         http
//                 .authorizeHttpRequests(authorize -> authorize
//                         .dispatcherTypeMatchers(DispatcherType.FORWARD,
//                                 DispatcherType.INCLUDE)
//                         .permitAll()
//                         .requestMatchers("/admin/**").hasRole("ADMIN")
//                         .requestMatchers("/", "/login", "/product/**", "/client/**", "/css/**", "/js/**", "/images/**","/register/").permitAll()
//                         .anyRequest().authenticated())

//                         .sessionManagement((sessionManagement) -> sessionManagement
// 			            .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)//tao session moi
// 	                    .invalidSessionUrl("/logout?expired")//het han se log out
// 				        .maximumSessions(1)//tai 1 thoi diem co bnh tk dang nhap
// 	                    .maxSessionsPreventsLogin(false))//Neu vuotqua so luong session, nguoi max sau se da nguoi truoc ra

// 	                    .logout(logout->logout.deleteCookies("JSESSIONID").invalidateHttpSession(true))
//                         // rememberMe
//                         .rememberMe(r -> r.rememberMeServices(rememberMeServices()))
//                         .formLogin(formLogin -> formLogin
//                         .loginPage("/login")
//                         .failureUrl("/login?error")
//                         .permitAll())
//                 .exceptionHandling(ex -> ex.accessDeniedPage("/access-deny"));        

//         return http.build();
//     }

// }
package vn.hoidanit.laptopshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.session.security.web.authentication.SpringSessionRememberMeServices;

import jakarta.servlet.DispatcherType;
import vn.hoidanit.laptopshop.service.CustomUserDetailsService;
import vn.hoidanit.laptopshop.service.UserService;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserService userService) {
        return new CustomUserDetailsService(userService);
    }

    @Bean
    public DaoAuthenticationProvider authProvider(
            PasswordEncoder passwordEncoder,
            UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        // authProvider.setHideUserNotFoundExceptions(false);
        return authProvider;
    }

    @Bean
    public AuthenticationSuccessHandler customSuccessHandler() {
        return new CustomSuccessHandler();
    }

    @Bean
    public SpringSessionRememberMeServices rememberMeServices() {
        SpringSessionRememberMeServices rememberMeServices = new SpringSessionRememberMeServices();
        // optionally customize
        rememberMeServices.setAlwaysRemember(true);

        return rememberMeServices;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // v6. lamda
        http
                .authorizeHttpRequests(authorize -> authorize
                        .dispatcherTypeMatchers(DispatcherType.FORWARD,
                                DispatcherType.INCLUDE)
                        .permitAll()

                        .requestMatchers("/", "/login", "/product/**",
                                "/client/**", "/css/**", "/js/**", "/images/**")
                        .permitAll()

                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        .anyRequest().authenticated())

                .sessionManagement((sessionManagement) -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                        .invalidSessionUrl("/logout?expired")
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false))

                .logout(logout -> logout.deleteCookies("JSESSIONID").invalidateHttpSession(true))

                .rememberMe(r -> r.rememberMeServices(rememberMeServices()))
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .failureUrl("/login?error")
                        .successHandler(customSuccessHandler())
                        .permitAll())
                .exceptionHandling(ex -> ex.accessDeniedPage("/access-deny"));

        return http.build();
    }

}

