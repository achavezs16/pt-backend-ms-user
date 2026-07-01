package cl.pymetrack.msuser.config;

import cl.pymetrack.msuser.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class SecurityConfigTest {

    @Test
    void authenticationProvider_DeberiaCrearseCorrectamente() {
        UserDetailsServiceImpl userDetailsService = mock(UserDetailsServiceImpl.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

        SecurityConfig securityConfig = new SecurityConfig(userDetailsService, passwordEncoder);

        DaoAuthenticationProvider provider = securityConfig.authenticationProvider();

        assertNotNull(provider);
    }
}