package ru.otus.core.service;

import ru.otus.core.model.Address;
import ru.otus.core.model.Phone;
import ru.otus.core.model.User;

import java.util.ArrayList;
import java.util.List;

public class DBDataInitializer {

    public static void createUsers(DBServiceUser dbServiceUser) {
        List<User> users = new ArrayList<>();
        int i = 19;
        int y = 50;
        int z = 67;

        users.add(new User("Petrov Pyotr", "user1", "11111"));
        users.add(new User("Ivanov Ivan", "user2", "11112"));
        users.add(new User("Semyonov Semyon", "user3", "11113"));
        users.add(new User("Ilyahov Ilya", "user4", "11114"));
        users.add(new User("Borisov Boris", "user5", "11115"));
        users.add(new User("Sergeev Vladimir", "user6", "11116"));

        for (var user : users) {
            i++;
            y++;
            z++;
            user.setAddress(new Address("Симферопольский бульвар, " + i));
            user.addPhone(new Phone("(495) 345 45 " + y));
            user.addPhone(new Phone("960 234 72 " + z));
        }
        dbServiceUser.saveUsers(users);
    }

}
