package ru.kata.spring.boot_security.demo.servises;

import ru.kata.spring.boot_security.demo.models.Role;

import java.util.Set;

public interface RoleService {
    Set<Role> findAllRole();

    void saveRole(Role role);
}
