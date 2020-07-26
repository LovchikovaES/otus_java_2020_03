package ru.otus.core.controllers;

import org.springframework.web.bind.annotation.*;
import ru.otus.core.model.User;
import ru.otus.core.service.DBServiceUser;

@RestController
public class UserRestController {

    private final DBServiceUser repository;

    public UserRestController(DBServiceUser repository) {
        this.repository = repository;
    }

    @GetMapping("/api/user/{id}")
    public User getUserById(@PathVariable(name = "id") long id) {
        return repository.getUser(id).isPresent() ? repository.getUser(id).get() : null;
    }

    @PostMapping("/api/user")
    public User saveUser(@RequestBody User user) {
        return repository.saveUser(user);
    }
}
