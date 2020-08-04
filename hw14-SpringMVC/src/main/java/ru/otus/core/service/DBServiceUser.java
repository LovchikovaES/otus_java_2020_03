package ru.otus.core.service;

import ru.otus.core.model.User;

import java.util.List;
import java.util.Optional;

public interface DBServiceUser {

    User saveUser(User user);

    Optional<User> getUser(long id);

    void saveUsers(List<User> users);

    Optional<List<User>> getAllUsers();

    Optional<User> getByLogin(String login);
}
