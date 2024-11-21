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
        Role roleAdmin = new Role(1L, "ROLE_ADMIN");
        Role roleUser = new Role(2L, "ROLE_USER");
        roleService.saveRole(roleAdmin);
        roleService.saveRole(roleUser);
        User admin = new User("admin", "admin", Set.of(roleAdmin));
        User user = new User("user", "user", Set.of(roleUser));
        userService.saveUser(user);
        userService.saveUser(admin);
        return "redirect:/user";
    }

    @GetMapping("/admin")
    public String listUsers(Model model, Principal principal) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("currentUser", userService.loadUserByUsername(principal.getName()));
        model.addAttribute("roles", roleService.findAllRole());
        return "users";
    }

    @GetMapping("/user")
    public String user(Model model, Principal principal) {
        model.addAttribute("currentUser", userService.loadUserByUsername(principal.getName()));
        return "user";
    }

    @GetMapping("/admin/edit")
    public String addUser(@ModelAttribute("user") User user, Model model, Principal principal) {
        if (user.getId() != null) {
            model.addAttribute("user", userService.getUserById(user.getId()));
        }
        model.addAttribute("roles", roleService.findAllRole());
        model.addAttribute("currentUser", userService.loadUserByUsername(principal.getName()));
        return "create";
    }

    @PostMapping("/admin/edit")
    public String save(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, Model model, Principal principal) {
        model.addAttribute("roles", roleService.findAllRole());
        model.addAttribute("currentUser", userService.loadUserByUsername(principal.getName()));
        if (bindingResult.hasErrors()) {
            System.out.println("error in binding result");
            System.out.println(bindingResult.getAllErrors());
            System.out.println(bindingResult.getFieldErrors());
            return "create";
        } else {
            if (user.getId() == null) {
                if (userService.ifLogin(user.getLogin())) {
                    model.addAttribute("errorMessage", "Login is already in use");
                    return "create";
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
