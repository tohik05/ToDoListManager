package com.softserve.todolistmanager.controller;

import com.softserve.todolistmanager.model.User;
import com.softserve.todolistmanager.repository.UserRepository;
import com.softserve.todolistmanager.security.UserDetailsServiceImpl;
import com.softserve.todolistmanager.service.RoleService;
import com.softserve.todolistmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public UserController(UserService userService, RoleService roleService, PasswordEncoder passwordEncoder, UserRepository userRepository, UserDetailsServiceImpl userDetailsService) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/create")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String create(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("owner", userRepository.findByEmail(userDetailsService.getCurrentUsername()));
        return "create-user";
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String create(@Validated @ModelAttribute("user") User user, BindingResult result) {
        if (result.hasErrors()) {
            return "create-user";
        }
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        user.setRole(roleService.readById(2));
        userService.create(user);
        return "redirect:/users/all";
    }

    @GetMapping("/{id}/read")
    @PreAuthorize("hasAuthority('ADMIN') or @userServiceImpl.readById(#id).email.equals(authentication.name)")
    public String read(@PathVariable long id, Model model) {
        model.addAttribute("user", userService.readById(id));
        model.addAttribute("owner", userRepository.findByEmail(userDetailsService.getCurrentUsername()));
        return "user-info";
    }

    @GetMapping("/{id}/update")
    @PreAuthorize("hasAuthority('ADMIN') or @userServiceImpl.readById(#id).email.equals(authentication.name)")
    public String update(@PathVariable long id, Model model) {
        model.addAttribute("user", userService.readById(id));
        model.addAttribute("roles", roleService.getAll());
        model.addAttribute("owner", userRepository.findByEmail(userDetailsService.getCurrentUsername()));
        return "update-user";
    }


    @PostMapping("/{id}/update")
    @PreAuthorize("hasAuthority('ADMIN') or @userServiceImpl.readById(#id).email.equals(authentication.name)")
    public String update(@PathVariable long id, Model model,
                         @Validated @ModelAttribute("user") User user,
                         @RequestParam("roleId") long roleId, BindingResult result) {
//        User oldUser = userService.readById(id);
        if (result.hasErrors()) {
            user.setRole(userService.readById(id).getRole());
            model.addAttribute("roles", roleService.getAll());
            return "update-user";
        }
//        if (userRepository.findByEmail(userDetailsService.getCurrentUsername()).getRole().getName().equals("ADMIN")) {
//            user.setRole(roleService.readById(roleId));
//        } else {
//            user.setRole(oldUser.getRole());
//        }
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.update(roleId, user);
        return "redirect:/users/" + id + "/read";
    }


    @GetMapping("/{id}/delete")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String delete(@PathVariable("id") long id) {
        userService.delete(id);
        return "redirect:/users/all";
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String getAll(Model model) {
        model.addAttribute("users", userService.getAll());
        model.addAttribute("owner", userRepository.findByEmail(userDetailsService.getCurrentUsername()));
        return "users-list";
    }
}
