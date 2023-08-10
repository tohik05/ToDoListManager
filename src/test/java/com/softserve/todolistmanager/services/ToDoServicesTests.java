package com.softserve.todolistmanager.services;

import com.softserve.todolistmanager.exception.NullEntityReferenceException;
import com.softserve.todolistmanager.model.ToDo;
import com.softserve.todolistmanager.model.User;
import com.softserve.todolistmanager.repository.ToDoRepository;
import com.softserve.todolistmanager.service.impl.ToDoServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class ToDoServicesTests {

    @Mock
    private ToDoRepository todoRepositoryMock;
    @InjectMocks
    private ToDoServiceImpl todoServiceMock;
    private ToDo expected;
    private List<ToDo> expectedList;

    private final String TODO_WITH_ID_NOT_FOUND = "ToDo with id '0' not found";

    @BeforeEach
    public void initData() {
        User user = new User();
        user.setFirstName("Anton");
        user.setLastName("Khoroshok");
        user.setEmail("email@gmail.com");
        user.setPassword("123456789");
        expected = new ToDo();
        expected.setTitle("ToDoTitle");
        expected.setCreatedAt(LocalDateTime.now());
        expected.setOwner(user);
        expectedList = new ArrayList<>();
    }


    @Test
    public void testCreateToDoWithValidData() {
        when(todoRepositoryMock.save(expected)).thenReturn(expected);
        ToDo actual = todoServiceMock.create(expected);

        assertEquals(expected, actual);
        verify(todoRepositoryMock, times(1)).save(expected);
    }

    @Test
    public void testCreateToDoWithNullData() {
        lenient().when(todoRepositoryMock.save(isNull())).thenThrow(IllegalArgumentException.class);

        assertThrows(NullEntityReferenceException.class, () -> todoServiceMock.create(null));
        verify(todoRepositoryMock, never()).save(new ToDo());
    }

    @Test
    public void testReadToDoByCorrectId() {
        when(todoRepositoryMock.findById(anyLong())).thenReturn(Optional.of(expected));

        assertEquals(expected, todoServiceMock.readById(anyLong()));
        verify(todoRepositoryMock, times(1)).findById(anyLong());
    }

    @Test
    public void testReadToDoByIncorrectId() {
        assertEquals(TODO_WITH_ID_NOT_FOUND, assertThrows(EntityNotFoundException.class, () -> todoServiceMock.readById(anyLong())).getMessage());
        verify(todoRepositoryMock, times(1)).findById(anyLong());
    }

    @Test
    public void testUpdateToDoWithValidData() {
        when(todoRepositoryMock.findById(anyLong())).thenReturn(Optional.of(expected));
        when(todoRepositoryMock.save(expected)).thenReturn(expected);
        ToDo actual = todoServiceMock.update(expected);

        assertEquals(expected, actual);
        verify(todoRepositoryMock, times(1)).findById(anyLong());
        verify(todoRepositoryMock, times(1)).save(expected);
    }

    @Test
    public void testUpdateToDoWithNullData() {
        assertEquals("ToDo cannot be 'null'", assertThrows(NullEntityReferenceException.class, () -> todoServiceMock.update(null)).getMessage());
        verify(todoRepositoryMock, never()).save(new ToDo());
    }

    @Test
    public void testDeleteToDoWithCorrectId() {
        when(todoRepositoryMock.findById(anyLong())).thenReturn(Optional.of(new ToDo()));
        doNothing().when(todoRepositoryMock).delete(any(ToDo.class));
        todoServiceMock.delete(anyLong());

        verify(todoRepositoryMock, times(1)).findById(anyLong());
        verify(todoRepositoryMock, times(1)).delete(any(ToDo.class));
    }

    @Test
    public void testDeleteToDoWithIncorrectId() {
        assertEquals(TODO_WITH_ID_NOT_FOUND, assertThrows(EntityNotFoundException.class, () -> todoServiceMock.delete(anyLong())).getMessage());
        verify(todoRepositoryMock, never()).delete(new ToDo());
    }

    @Test
    void testGetAllToDo() {
        int expectedSize = 1;
        expectedList.add(expected);
        Mockito.when(todoServiceMock.getAll()).thenReturn(expectedList);

        assertEquals(expectedSize, todoServiceMock.getAll().size());
    }

    @Test
    void testGetEmptyTaskList() {
        Mockito.when(todoServiceMock.getAll()).thenReturn(expectedList);

        assertEquals(expectedList, todoServiceMock.getAll());
    }

    @Test
    void testGetToDoByUserId() {
        expectedList.add(expected);
        expectedList.add(expected);
        when(todoRepositoryMock.getByUserId(anyLong())).thenReturn(expectedList);

        assertEquals(expectedList, todoRepositoryMock.getByUserId(anyLong()));
        assertEquals(2, todoServiceMock.getByUserId(anyLong()).size());
        verify(todoRepositoryMock, times(2)).getByUserId(anyLong());
    }

    @Test
    void testGetToDoByIncorrectUserId() {
        when(todoRepositoryMock.getByUserId(anyLong())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> todoServiceMock.getByUserId(anyLong()));
        verify(todoRepositoryMock, times(1)).getByUserId(anyLong());
    }

    @AfterEach
    public void clearData() {
        expected = null;
    }
}
