package com.softserve.todolistmanager.controller;

import com.softserve.todolistmanager.dto.TaskDto;
import com.softserve.todolistmanager.dto.TaskTransformer;
import com.softserve.todolistmanager.model.Priority;
import com.softserve.todolistmanager.model.Task;
import com.softserve.todolistmanager.repository.UserRepository;
import com.softserve.todolistmanager.security.UserDetailsServiceImpl;
import com.softserve.todolistmanager.service.StateService;
import com.softserve.todolistmanager.service.TaskService;
import com.softserve.todolistmanager.service.ToDoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;
    private final ToDoService todoService;
    private final StateService stateService;
    private final UserRepository userRepository;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public TaskController(TaskService taskService, ToDoService todoService, StateService stateService, UserRepository userRepository, UserDetailsServiceImpl userDetailsService) {
        this.taskService = taskService;
        this.todoService = todoService;
        this.stateService = stateService;
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/create/todos/{todo_id}")
    @PreAuthorize("hasAuthority('ADMIN') or @toDoServiceImpl.readById(#todoId).owner.email.equals(authentication.name)")
    public String create(@PathVariable("todo_id") long todoId, Model model) {
        model.addAttribute("task", new TaskDto());
        model.addAttribute("todo", todoService.readById(todoId));
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("owner", userRepository.findByEmail(userDetailsService.getCurrentUsername()));
        return "create-task";
    }

    @PostMapping("/create/todos/{todo_id}")
    @PreAuthorize("hasAuthority('ADMIN') or @toDoServiceImpl.readById(#todoId).owner.email.equals(authentication.name)")
    public String create(@PathVariable("todo_id") long todoId, Model model,
                         @Validated @ModelAttribute("task") TaskDto taskDto, BindingResult result) {
        if (result.hasErrors()) {
            model.addAttribute("todo", todoService.readById(todoId));
            model.addAttribute("priorities", Priority.values());
            return "create-task";
        }
        taskService.create(taskDto);
        return "redirect:/todos/" + todoId + "/tasks";
    }

    @GetMapping("/{task_id}/update/todos/{todo_id}")
    @PreAuthorize("hasAuthority('ADMIN') or @toDoServiceImpl.readById(#todoId).owner.email.equals(authentication.name)")
    public String update(@PathVariable("task_id") long taskId, @PathVariable("todo_id") long todoId, Model model) {
        model.addAttribute("task", TaskTransformer.convertToDto(taskService.readById(taskId)));
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("states", stateService.getAll());
        model.addAttribute("owner", userRepository.findByEmail(userDetailsService.getCurrentUsername()));
        return "update-task";
    }

    @PostMapping("/{task_id}/update/todos/{todo_id}")
    @PreAuthorize("hasAuthority('ADMIN') or @toDoServiceImpl.readById(#todoId).owner.email.equals(authentication.name)")
    public String update(@PathVariable("task_id") long taskId, @PathVariable("todo_id") long todoId, Model model,
                         @Validated @ModelAttribute("task") TaskDto taskDto, BindingResult result) {
        if (result.hasErrors()) {
            model.addAttribute("priorities", Priority.values());
            model.addAttribute("states", stateService.getAll());
            return "update-task";
        }
        taskService.update(taskDto);
        return "redirect:/todos/" + todoId + "/tasks";
    }

    @GetMapping("/{task_id}/delete/todos/{todo_id}")
    @PreAuthorize("hasAuthority('ADMIN') or @toDoServiceImpl.readById(#todoId).owner.email.equals(authentication.name)")
    public String delete(@PathVariable("task_id") long taskId, @PathVariable("todo_id") long todoId) {
        taskService.delete(taskId);
        return "redirect:/todos/" + todoId + "/tasks";
    }

}
