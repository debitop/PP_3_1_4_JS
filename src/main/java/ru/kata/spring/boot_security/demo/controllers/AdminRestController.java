package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.servises.UserServiceImpl;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
public class AdminRestController {

    final private UserServiceImpl userService;

    @Autowired
    public AdminRestController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    public ResponseEntity<User> showUserPage(Principal principal) {
        return new ResponseEntity<>((User) userService.loadUserByUsername(principal.getName()), HttpStatus.OK);
    }

    @GetMapping("/admin")
    public ResponseEntity<List<User>> showAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<User> showUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/admin")
    public ResponseEntity<HttpStatus> addUser(@RequestBody User user) {
        userService.saveUser(user);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping("/admin")
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        userService.updateUser(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Long> deleteUser(@PathVariable Long id) {
        userService.removeUserById(id);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }
}
