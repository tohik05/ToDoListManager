package com.softserve.todolistmanager.service;


import com.softserve.todolistmanager.model.User;

import java.util.List;

public interface UserService {
    User create(User user);
    User readById(long id);
    User update(long roleId, User user);
    void delete(long id);
    List<User> getAll();

}
