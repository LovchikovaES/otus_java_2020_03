package ru.otus.core.dao;

import ru.otus.core.model.User;
import ru.otus.core.sessionmanager.SessionManager;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    Optional<User> findById(long id);

    User saveUser(User user);

    Optional<List<User>> findAllUsers();

    Optional<User> findByLogin(String login);

    SessionManager getSessionManager();
}