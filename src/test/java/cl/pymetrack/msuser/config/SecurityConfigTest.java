package cl.pymetrack.msuser.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.AuthenticationManager;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class SecurityConfigTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() {
        // Esto fuerza a que se cargue SecurityConfig y se ejecuten todos los @Bean
        assertNotNull(context.getBean(SecurityConfig.class));
    }

    @Test
    void beansAreCreated() {
        // Verificamos que los beans principales se hayan creado correctamente
        assertNotNull(context.getBean(SecurityFilterChain.class));
        assertNotNull(context.getBean(AuthenticationManager.class));
    }
}