package com.softserve.todolistmanager.services;

import com.softserve.todolistmanager.exception.NullEntityReferenceException;
import com.softserve.todolistmanager.model.User;
import com.softserve.todolistmanager.repository.UserRepository;
import com.softserve.todolistmanager.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class UserServicesTests {
    @Mock
    private UserRepository userRepositoryMock;
    @InjectMocks
    private UserServiceImpl userServiceMock;

    @Test
    @Transactional
    public void createEmptyUserTest() {
        when(userRepositoryMock.save(new User())).thenThrow(new NullEntityReferenceException("User cannot be 'null'"));

        NullEntityReferenceException thrown = assertThrows(
                NullEntityReferenceException.class,
                () -> userServiceMock.create(null)
        );

        assertEquals(NullEntityReferenceException.class, thrown.getClass());
        assertEquals(thrown.getMessage(), "User cannot be 'null'");
    }

/*    @Test
    @Transactional
    public void updateEmptyUserTest() {
        NullEntityReferenceException thrown = assertThrows(
                NullEntityReferenceException.class,
                () -> userServiceMock.update(null)
        );

        assertEquals(NullEntityReferenceException.class, thrown.getClass());
        assertEquals(thrown.getMessage(), "User cannot be 'null'");
    }*/

    @Test
    @Transactional
    public void deleteNotExistUserTest() {
        long id = 20L;
        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> userServiceMock.delete(id)
        );
        assertEquals(EntityNotFoundException.class, thrown.getClass());
        assertEquals(thrown.getMessage(), "User with id " + id + " not found");
    }

    @Test
    @Transactional
    public void readNotExistUserTest() {
        long id = 20;
        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> userServiceMock.readById(id)
        );

        assertEquals(EntityNotFoundException.class, thrown.getClass());
        assertEquals(thrown.getMessage(), "User with id " + id + " not found");
    }

    @Test
    @Transactional
    public void getAllEmptyListToDoTest() {
        when(userRepositoryMock.findAll()).thenReturn(new ArrayList<>());

        List<User> actual = userServiceMock.getAll();

        verify(userRepositoryMock).findAll();
        assertEquals(0, actual.size());
    }

}

