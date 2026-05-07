package cl.pymetrack.msuser.config;

import cl.pymetrack.msuser.model.Pyme;
import cl.pymetrack.msuser.model.Role;
import cl.pymetrack.msuser.repository.PymeRepository;
import cl.pymetrack.msuser.repository.UserRepository;
import cl.pymetrack.msuser.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class DemoDataInitializer {

    @Bean
    @Transactional
    public CommandLineRunner seedDemoData(
            PymeRepository pymeRepository,
            UserRepository userRepository,
            UserService userService
    ) {
        return args -> {
            Pyme pyme1 = pymeRepository.findByRutPyme("76.111.111-1")
                    .orElseGet(() -> {
                        Pyme pyme = new Pyme("TechStore Demo", "76.111.111-1", "contacto@techstore.cl");
                        pyme.setTelefonoContactoPyme("+56911111111");
                        pyme.setDireccionSucursalPyme("Av. Demo 123");
                        pyme.setComunaSucursalPyme("Santiago");
                        pyme.setRegionSucursalPyme("Región Metropolitana");
                        return pymeRepository.save(pyme);
                    });

            Pyme pyme2 = pymeRepository.findByRutPyme("76.222.222-2")
                    .orElseGet(() -> {
                        Pyme pyme = new Pyme("EcoMarket Demo", "76.222.222-2", "contacto@ecomarket.cl");
                        pyme.setTelefonoContactoPyme("+56922222222");
                        pyme.setDireccionSucursalPyme("Calle Demo 456");
                        pyme.setComunaSucursalPyme("Valparaíso");
                        pyme.setRegionSucursalPyme("Valparaíso");
                        return pymeRepository.save(pyme);
                    });

            if (!userRepository.existsByEmail("pyme1@demo.cl")) {
                userService.createUser(
                        "pyme1@demo.cl",
                        "12345678",
                        "Representante",
                        "TechStore",
                        Role.PYME,
                        pyme1.getId()
                );
            }

            if (!userRepository.existsByEmail("pyme2@demo.cl")) {
                userService.createUser(
                        "pyme2@demo.cl",
                        "12345678",
                        "Representante",
                        "EcoMarket",
                        Role.PYME,
                        pyme2.getId()
                );
            }

            if (!userRepository.existsByEmail("admin@pymetrack.cl")) {
                userService.createUser(
                        "admin@pymetrack.cl",
                        "12345678",
                        "Administrador",
                        "PymeTrack",
                        Role.ADMIN,
                        null
                );
            }
        };
    }
}
