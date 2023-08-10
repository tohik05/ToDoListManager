package com.softserve.todolistmanager.service.impl;

import com.softserve.todolistmanager.exception.NullEntityReferenceException;
import com.softserve.todolistmanager.model.ToDo;
import com.softserve.todolistmanager.repository.ToDoRepository;
import com.softserve.todolistmanager.service.ToDoService;
import com.softserve.todolistmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ToDoServiceImpl implements ToDoService {

    private final ToDoRepository todoRepository;
    private final UserService userService;

    @Autowired
    public ToDoServiceImpl(ToDoRepository todoRepository, UserService userService) {
        this.todoRepository = todoRepository;
        this.userService = userService;
    }

    @Override
    public ToDo create(ToDo toDo) {
        if (toDo == null) {
            throw new NullEntityReferenceException("ToDo cannot be 'null'");
        }
        toDo.setCreatedAt(LocalDateTime.now());
        return todoRepository.save(toDo);
    }

    @Override
    public ToDo readById(long id) {
        return todoRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("ToDo with id '%s' not found", id)));
    }

    @Override
    public ToDo update(ToDo toDo) {
        if (toDo == null) {
            throw new NullEntityReferenceException("ToDo cannot be 'null'");
        }
        ToDo toDoFromDB = readById(toDo.getId());
        toDo.setOwner(toDoFromDB.getOwner());
        toDo.setCollaborators(toDoFromDB.getCollaborators());
        return todoRepository.save(toDo);
    }

    @Override
    public void delete(long id) {
        todoRepository.delete(readById(id));
    }

    @Override
    public List<ToDo> getAll() {
        List<ToDo> todos = todoRepository.findAll();
        return todos.isEmpty() ? new ArrayList<>() : todos;
    }

    @Override
    public List<ToDo> getByUserId(long userId) {
        List<ToDo> todos = todoRepository.getByUserId(userId);
        return todos.isEmpty() ? new ArrayList<>() : todos;
    }
}
