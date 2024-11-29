package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.servises.RoleServiceImpl;
import ru.kata.spring.boot_security.demo.servises.UserServiceImpl;

import java.util.Set;

@Controller
public class TestController {

    final private UserServiceImpl userService;
    final private RoleServiceImpl roleService;

    @Autowired
    public TestController(UserServiceImpl userService, RoleServiceImpl roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/admin")
    public String showIndexPageAdmin() {
        return "admin";
    }

    @GetMapping("/user")
    public String showIndexPageUser() {
        return "user";
    }

    @GetMapping(value = {"/", "/index"})
    public String index() {
        if (userService.ifLogin("admin")) {
            return "redirect:/admin";
        }
        return "index";
    }

    @PostMapping("/create")
    public String createOne() {
        if (userService.ifLogin("login")) {
            return "redirect:/admin";
        }
        Role roleAdmin = new Role(1L, "ROLE_ADMIN");
        Role roleUser = new Role(2L, "ROLE_USER");
        roleService.saveRole(roleAdmin);
        roleService.saveRole(roleUser);
        User admin = new User("Ivan", "Ivanov", "22", "admin", "admin",
                "admin@mail.ru", Set.of(roleAdmin));
        User user = new User("Maksim", "Petrov", "33", "user", "user",
                "user@mail.ru", Set.of(roleUser));

        userService.saveUser(user);
        userService.saveUser(admin);
        return "redirect:/admin";
    }
}
