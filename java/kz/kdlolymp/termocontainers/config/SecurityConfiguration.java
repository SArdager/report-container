package kz.kdlolymp.termocontainers.config;

import kz.kdlolymp.termocontainers.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import java.util.Properties;


@Configuration
@EnableAutoConfiguration
@EnableWebSecurity
public class SecurityConfiguration {
    @Autowired
    private UserService userService;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
            .csrf().disable()
            .authorizeRequests()
//                .antMatchers("/registration").not().fullyAuthenticated()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/work-starter", "/user/**", "/changePassword").hasAnyRole("ADMIN", "USER")
                .antMatchers("/**").permitAll()
            .anyRequest().authenticated()
            .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .successHandler(changeTemporaryPasswordAuthenticationHandler())
//                .defaultSuccessUrl("work-starter")
            .and()
                .logout()
                .permitAll()
                .logoutSuccessUrl("/")
            .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                .maximumSessions(1)
                .expiredUrl("/login");
//                .addFilter(securityContextHolderAwareRequestFilter())
//                .addFilterAfter(securityContextHolderAwareRequestFilter(), UsernamePasswordAuthenticationFilter.class)
//                .addFilterBefore(securityContextHolderAwareRequestFilter(), AnonymousAuthenticationFilter.class);
//                .
////                .authenticationManager(customAuthenticationManager());
//                .authenticationProvider(customAuthenticationProvider());
        return http.build();
    }


    @Autowired
    protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
        auth.inMemoryAuthentication();
    }
    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationSuccessHandler changeTemporaryPasswordAuthenticationHandler(){
        return new ChangeTemporaryPasswordAuthenticationHandler();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
//    @Bean
//    public SecurityContextHolderAwareRequestFilter securityContextHolderAwareRequestFilter() {
//        return new SecurityContextHolderAwareRequestFilter();
//    }
//    @Bean
//    public FilterRegistrationBean deactivateSecurityContextHolderAwareRequestFilter(@Qualifier("securityContextHolderAwareRequestFilter") SecurityContextHolderAwareRequestFilter filter) {
//        return deactivate(filter);
//    }
//
//    private FilterRegistrationBean deactivate(SecurityContextHolderAwareRequestFilter filter) {
//        FilterRegistrationBean registrationBean = new FilterRegistrationBean<>(filter);
//        registrationBean.setEnabled(false); // container shouldn't register this filter under its ApplicationContext as this filter already registered within springSecurityFilterChain as bean
//        return registrationBean;
//    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public InMemoryUserDetailsManager inMemoryUserDetailsService() {
//        UserDetails user = User.builder()
//            .username("superadmin")
//            .password(passwordEncoder().encode((CharSequence)"cdlOlymp21"))
//            .roles("ADMIN")
//            .build();
//        return new InMemoryUserDetailsManager(user);
//    }

//    @Bean
//    public CustomAuthenticationManager customAuthenticationManager() {
//        CustomAuthenticationManager customAuthenticationManager = new CustomAuthenticationManager();
//        return customAuthenticationManager;
//    }
//
//    @Bean
//    public CustomAuthenticationProvider customAuthenticationProvider(){
//        CustomAuthenticationProvider provider = new CustomAuthenticationProvider();
//        return provider;
//    }
    @Bean
    public UserService userService(){ return new UserService();}
    @Bean
    public UserRightsService userRightsService(){ return new UserRightsService();}
    @Bean
    public BranchService branchService() { return new BranchService(); }
    @Bean
    public DepartmentService departmentService() { return new DepartmentService(); }
    @Bean
    public TimeStandardService standardService() { return new TimeStandardService(); }
    @Bean
    public AlarmGroupService alarmGroupService() { return new AlarmGroupService(); }

    @Bean
    public JavaMailSender getJavaMailSender(){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.kdlolymp.kz");
        mailSender.setPort(25);

        mailSender.setUsername("a.saduakasov@kdlolymp.kz");
        mailSender.setPassword("GUNnTa8mbu");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "false");
        props.put("mail.smtp.ssl.trust", "smtp.kdlolymp.kz");
//        props.put("mail.smtp.socketFactory.port", "25");
//        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//        mail.smtp.ssl.protocols=TLSv1.2
    return mailSender;
    }

    @Bean
    public HttpMessageConverter createImageHttpMessageConverter() {
        return new BufferedImageHttpMessageConverter();
    }
}
