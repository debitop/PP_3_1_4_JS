package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.servises.RoleServiceImpl;
import ru.kata.spring.boot_security.demo.servises.UserServiceImpl;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Set;


@Controller
public class UserController {

    final private UserServiceImpl userService;
    final private RoleServiceImpl roleService;

    @Autowired
    public UserController(UserServiceImpl userService, RoleServiceImpl roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }


    @GetMapping(value = {"/", "/index"})
    public String index() {
        if (userService.ifLogin("admin")) {
            return "redirect:/user";
        }
        return "index";
    }

    @PostMapping("/create")
    public String createOne() {
        if (userService.ifLogin("login")) {
            return "redirect:/user";
        }
        Role roleAdmin = new Role(1l, "ROLE_ADMIN");
        Role roleUser = new Role(2l, "ROLE_USER");
        roleService.saveRole(roleAdmin);
        roleService.saveRole(roleUser);
        User admin = new User("admin", "admin", Set.of(roleAdmin));
        User user = new User("user", "user", Set.of(roleUser));
        userService.saveUser(user);
        userService.saveUser(admin);
        return "redirect:/user";
    }

    @GetMapping("/admin")
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/user")
    public String user(Model model, Principal principal) {
        model.addAttribute("users", userService.loadUserByUsername(principal.getName()));
        return "user";
    }


    @GetMapping("/admin/edit")
    public String addUser(@ModelAttribute("user") User user, Model model) {
        if (user.getId() != null) {
            model.addAttribute("user", userService.getUserById(user.getId()));
        }
        model.addAttribute("roles", roleService.findAllRole());
        return "edit";
    }

    @PostMapping("/admin/edit")
    public String save(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, Model model) {
        model.addAttribute("roles", roleService.findAllRole());
        if (bindingResult.hasErrors()) {
            return "edit";
        } else {
            if (user.getId() == null) {
                if (userService.ifLogin(user.getLogin())) {
                    model.addAttribute("errorMessage", "Login is already in use");
                    return "edit";
                }
                userService.saveUser(user);
            } else {
                userService.updateUser(user);
            }
            return "redirect:/admin";
        }
    }

    @PostMapping(value = "/admin/delete")
    public String deleteUser(@ModelAttribute("user") User user) {
        userService.removeUserById(user.getId());
        return "redirect:/admin";
    }
}
