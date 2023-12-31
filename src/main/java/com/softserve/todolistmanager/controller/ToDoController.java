package com.softserve.todolistmanager.controller;

import com.softserve.todolistmanager.model.Task;
import com.softserve.todolistmanager.model.ToDo;
import com.softserve.todolistmanager.model.User;
import com.softserve.todolistmanager.repository.UserRepository;
import com.softserve.todolistmanager.security.UserDetailsServiceImpl;
import com.softserve.todolistmanager.service.TaskService;
import com.softserve.todolistmanager.service.ToDoService;
import com.softserve.todolistmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/todos")
public class ToDoController {

    private final ToDoService todoService;
    private final TaskService taskService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public ToDoController(ToDoService todoService, TaskService taskService, UserService userService, UserRepository userRepository, UserDetailsServiceImpl userDetailsService) {
        this.todoService = todoService;
        this.taskService = taskService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/create/users/{owner_id}")
    @PreAuthorize("hasAuthority('ADMIN') or @userServiceImpl.readById(#ownerId).email.equals(authentication.name)")
    public String create(@PathVariable("owner_id") long ownerId, Model model) {
        model.addAttribute("todo", new ToDo());
        model.addAttribute("ownerId", ownerId);
        model.addAttribute("owner", userRepository.findByEmail(userDetailsService.getCurrentUsername()));
        return "create-todo";
    }

    @PostMapping("/create/users/{owner_id}")
    @PreAuthorize("hasAuthority('ADMIN') or @userServiceImpl.readById(#ownerId).email.equals(authentication.name)")
    public String create(@PathVariable("owner_id") long ownerId, @Validated @ModelAttribute("todo") ToDo toDo, BindingResult result) {
        if (result.hasErrors()) {
            return "create-todo";
        }
        toDo.setOwner(userService.readById(ownerId));
        todoService.create(toDo);
        return "redirect:/todos/all/users/" + ownerId;
    }

    @GetMapping("/{id}/tasks")
    @PreAuthorize("hasAuthority('ADMIN') or @toDoServiceImpl.readById(#id).owner.email.equals(authentication.name)" +
            "or @toDoServiceImpl.readById(#id).collaborators.contains(@userRepository.findByEmail(authentication.name))")
    public String read(@PathVariable long id, Model model) {
        ToDo toDo = todoService.readById(id);
        List<User> users = userService.getAll().stream()
                .filter(user -> user.getId() != toDo.getOwner().getId())
                .collect(Collectors.toList());
        model.addAttribute("todo", toDo);
        model.addAttribute("tasks", taskService.getByTodoId(id));
        model.addAttribute("users", users);
        model.addAttribute("owner", userRepository.findByEmail(userDetailsService.getCurrentUsername()));
        return "todo-tasks";
    }

    @GetMapping("/{todo_id}/update/users/{owner_id}")
    @PreAuthorize("hasAuthority('ADMIN') or @userServiceImpl.readById(#ownerId).email.equals(authentication.name)")
    public String update(@PathVariable("todo_id") long todoId, @PathVariable("owner_id") long ownerId, Model model) {
        model.addAttribute("todo", todoService.readById(todoId));
        model.addAttribute("owner", userRepository.findByEmail(userDetailsService.getCurrentUsername()));
        return "update-todo";
    }

    @PostMapping("/{todo_id}/update/users/{owner_id}")
    @PreAuthorize("hasAuthority('ADMIN') or @userServiceImpl.readById(#ownerId).email.equals(authentication.name)")
    public String update(@PathVariable("todo_id") long todoId, @PathVariable("owner_id") long ownerId,
                         @Validated @ModelAttribute("todo") ToDo todo, BindingResult result) {
        if (result.hasErrors()) {
            todo.setOwner(userService.readById(ownerId));
            return "update-todo";
        }
        todoService.update(todo);
        return "redirect:/todos/all/users/" + ownerId;
    }

    @GetMapping("/{todo_id}/delete/users/{owner_id}")
    @PreAuthorize("hasAuthority('ADMIN') or @userServiceImpl.readById(#ownerId).email.equals(authentication.name)")
    public String delete(@PathVariable("todo_id") long todoId, @PathVariable("owner_id") long ownerId) {
        todoService.delete(todoId);
        return "redirect:/todos/all/users/" + ownerId;
    }

    @GetMapping("/all/users/{user_id}")
    @PreAuthorize("hasAuthority('ADMIN') or @userServiceImpl.readById(#userId).email.equals(authentication.name)")
    public String getAll(@PathVariable("user_id") long userId, Model model) {
        model.addAttribute("todos", todoService.getByUserId(userId));
        model.addAttribute("user", userService.readById(userId));
        model.addAttribute("owner", userRepository.findByEmail(userDetailsService.getCurrentUsername()));
        return "todos-user";
    }

    @GetMapping("/{id}/add")
    @PreAuthorize("hasAuthority('ADMIN') or @toDoServiceImpl.readById(#id).owner.email.equals(authentication.name)")
    public String addCollaborator(@PathVariable long id, @RequestParam("user_id") long userId) {
        ToDo todo = todoService.readById(id);
        List<User> collaborators = todo.getCollaborators();
        collaborators.add(userService.readById(userId));
        todo.setCollaborators(collaborators);
        todoService.update(todo);
        return "redirect:/todos/" + id + "/tasks";
    }

    @GetMapping("/{id}/remove")
    @PreAuthorize("hasAuthority('ADMIN') or @toDoServiceImpl.readById(#id).owner.email.equals(authentication.name)")
    public String removeCollaborator(@PathVariable long id, @RequestParam("user_id") long userId) {
        ToDo todo = todoService.readById(id);
        List<User> collaborators = todo.getCollaborators();
        collaborators.remove(userService.readById(userId));
        todo.setCollaborators(collaborators);
        todoService.update(todo);
        return "redirect:/todos/" + id + "/tasks";
    }
}
