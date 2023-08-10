package com.softserve.todolistmanager.service;

import com.softserve.todolistmanager.dto.TaskDto;
import com.softserve.todolistmanager.model.Task;

import java.util.List;

public interface TaskService {
    Task create(TaskDto taskDto);
    Task readById(long id);
    Task update(TaskDto taskDto);
    void delete(long id);

    List<Task> getAll();
    List<Task> getByTodoId(long todoId);
}
