package com.softserve.todolistmanager.service.impl;

import com.softserve.todolistmanager.dto.TaskDto;
import com.softserve.todolistmanager.dto.TaskTransformer;
import com.softserve.todolistmanager.exception.NullEntityReferenceException;
import com.softserve.todolistmanager.model.Task;
import com.softserve.todolistmanager.repository.TaskRepository;
import com.softserve.todolistmanager.service.StateService;
import com.softserve.todolistmanager.service.TaskService;
import com.softserve.todolistmanager.service.ToDoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final StateService stateService;
    private final ToDoService toDoService;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, StateService stateService, ToDoService toDoService) {
        this.taskRepository = taskRepository;
        this.stateService = stateService;
        this.toDoService = toDoService;
    }

    @Override
    public Task create(TaskDto taskDto) {
        if (taskDto == null) {
            throw new NullEntityReferenceException("Task cannot be 'null'");
        }
        return taskRepository.save(getCreatedTask(taskDto));
    }

    @Override
    public Task readById(long id) {
        return taskRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Task with id '%s' not found", id)));
    }

    @Override
    public Task update(TaskDto taskDto) {
        if (taskDto == null) {
            throw new NullEntityReferenceException("Task cannot be 'null'");
        }
        Task updatedTask = getUpdatedTask(taskDto);
        Task taskFromDB = readById(updatedTask.getId());
        updatedTask.setTodo(taskFromDB.getTodo());
        return taskRepository.save(updatedTask);
    }

    @Override
    public void delete(long id) {
        taskRepository.delete(readById(id));
    }

    @Override
    public List<Task> getAll() {
        List<Task> tasks = taskRepository.findAll();
        return tasks.isEmpty() ? new ArrayList<>() : tasks;
    }

    @Override
    public List<Task> getByTodoId(long todoId) {
        List<Task> tasks = taskRepository.getByTodoId(todoId);
        return tasks.isEmpty() ? new ArrayList<>() : tasks;
    }

    private Task getCreatedTask(TaskDto taskDto) {
        return TaskTransformer.convertToEntity(
                taskDto,
                toDoService.readById(taskDto.getTodoId()),
                stateService.getByName("New")
        );
    }

    private Task getUpdatedTask(TaskDto taskDto) {
        return TaskTransformer.convertToEntity(
                taskDto,
                toDoService.readById(taskDto.getTodoId()),
                stateService.readById(taskDto.getStateId())
        );
    }
}
