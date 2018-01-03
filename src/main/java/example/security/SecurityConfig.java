package example.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.User.UserBuilder;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

// @EnableWebSecurity
@EnableWebFluxSecurity
public class SecurityConfig {

    // @Bean
	// public UserDetailsService userDetailsService() throws Exception {
	// 	// ensure the passwords are encoded properly
	// 	// UserBuilder users = User.withDefaultPasswordEncoder();
	// 	// InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
	// 	// manager.createUser(users.username("user").password("password").roles("USER").build());
	// 	// manager.createUser(users.username("admin").password("password").roles("USER","ADMIN").build());
    //     // return manager;
    //     UserDetails user = User.withDefaultPasswordEncoder().username("user").password("password").roles("USER").build();
    //     return new InMemoryUserDetailsManager(user);
    // }

    @Bean
    public MapReactiveUserDetailsService userDetailsRepository() {
        UserDetails user = User.withDefaultPasswordEncoder().username("user").password("password").roles("USER").build();
        UserDetails admin = User.withDefaultPasswordEncoder().username("admin").password("password").roles("USER", "ADMIN").build();
        return new MapReactiveUserDetailsService(user, admin);
    }

}