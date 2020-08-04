package ru.otus.core.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;
import ru.otus.core.model.User;
import ru.otus.core.service.DBServiceUser;

import java.util.ArrayList;


@Controller
public class UserController {

    private final DBServiceUser repository;

    public UserController(DBServiceUser repository) {
        this.repository = repository;
    }

    @GetMapping({"/", "/user/list"})
    public String userListView(Model model) {
        model.addAttribute("users", repository.getAllUsers().orElse(new ArrayList<User>()));
        return "userList.html";
    }

    @GetMapping("/user/create")
    public String userCreateView(Model model) {
        model.addAttribute("user", new User());
        return "userCreate.html";
    }

    @PostMapping("/user/save")
    public RedirectView userSave(@ModelAttribute User user) {
        repository.saveUser(user);
        return new RedirectView("/user/list", true);
    }

}
