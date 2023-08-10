package com.softserve.todolistmanager.service.impl;

import com.softserve.todolistmanager.exception.NullEntityReferenceException;
import com.softserve.todolistmanager.model.User;
import com.softserve.todolistmanager.repository.UserRepository;
import com.softserve.todolistmanager.security.UserDetailsSecurity;
import com.softserve.todolistmanager.security.UserDetailsServiceImpl;
import com.softserve.todolistmanager.service.RoleService;
import com.softserve.todolistmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service("userServiceImpl")
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder, UserDetailsServiceImpl userDetailsService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public User create(User user) {
        if (user == null) {
            throw new NullEntityReferenceException("User cannot be 'null'");
        }
        user.setRole(roleService.readById(2));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User readById(long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with id '%s' not found", id)));
    }

    @Override
    public User update(long roleId, User user) {
        if (user == null) {
            throw new NullEntityReferenceException("User cannot be 'null'");
        }
        User oldUser = readById(user.getId());
        if (userRepository.findByEmail(userDetailsService.getCurrentUsername()).getRole().getName().equals("ADMIN")) {
            user.setRole(roleService.readById(roleId));
        } else {
            user.setRole(oldUser.getRole());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public void delete(long id) {
        userRepository.delete(readById(id));
    }

    @Override
    public List<User> getAll() {
        List<User> users = userRepository.findAll();
        return users.isEmpty() ? new ArrayList<>() : users;
    }

    @Override // method for security from UserDetailsService implementation with Email
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            return new UserDetailsSecurity(userRepository.findByEmail(email));
        } catch (RuntimeException e) {
            throw new UsernameNotFoundException("User not found!");
        }
    }
}
