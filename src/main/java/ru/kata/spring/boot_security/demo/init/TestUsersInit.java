package ru.kata.spring.boot_security.demo.init;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;
import ru.kata.spring.boot_security.demo.servises.RoleService;
import ru.kata.spring.boot_security.demo.servises.UserService;

import javax.annotation.PostConstruct;
import java.util.Set;

@Component
public class TestUsersInit {
    private final UserService userService;
    private final RoleService roleService;
    private final UserRepository userRepository;

    @Autowired
    public TestUsersInit(UserService userService, RoleService roleService, UserRepository userRepository) {
        this.userService = userService;
        this.roleService = roleService;
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void init() {
        if (userRepository.findByLogin("admin").isEmpty()) {
            Role roleAdmin = (new Role("ROLE_ADMIN"));
            roleService.saveRole(roleAdmin);
            User admin = new User("Ivan", "Ivanov", "22", "admin", "admin",
                    "admin@mail.ru", Set.of(roleAdmin));
            userService.saveUser(admin);
        }
        if (userRepository.findByLogin("user").isEmpty()) {
            Role roleUser = (new Role("ROLE_USER"));
            roleService.saveRole(roleUser);
            User user = new User("Maksim", "Petrov", "33", "user", "user",
                    "user@mail.ru", Set.of(roleUser));
            userService.saveUser(user);
        }
    }
}
