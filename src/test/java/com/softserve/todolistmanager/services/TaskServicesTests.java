/*
package com.softserve.todolistmanager.services;


import com.softserve.todolistmanager.exception.NullEntityReferenceException;
import com.softserve.todolistmanager.model.Priority;
import com.softserve.todolistmanager.model.Task;
import com.softserve.todolistmanager.repository.TaskRepository;
import com.softserve.todolistmanager.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class TaskServicesTests {

    @MockBean
    private TaskRepository taskRepositoryMock;
    @InjectMocks
    private TaskServiceImpl taskServiceMock;
    private Task expected;
    private List<Task> expectedList;

    @BeforeEach
    public void initData() {
        expected = new Task();
        expected.setName("TestTask");
        expected.setPriority(Priority.MEDIUM);
        expectedList = new ArrayList<>();
    }

    @AfterEach
    public void clearData() {
        expected = null;
        expectedList = null;
    }

    @Test
    void testCreateTask() {
        when(taskRepositoryMock.save(any(Task.class))).thenReturn(expected);

        assertEquals(expected, taskServiceMock.create(expected));
        verify(taskRepositoryMock).save(expected);
    }

    @Test
    void testCreateNullTask() {
        when(taskRepositoryMock.save(isNull())).thenThrow(IllegalArgumentException.class);

        assertThrows(NullEntityReferenceException.class, () -> taskServiceMock.create(null));
        verify(taskRepositoryMock, never()).save(isNull());
    }

    @Test
    void testGetTaskById() {
        when(taskRepositoryMock.findById(anyLong())).thenReturn(Optional.of(expected));

        assertEquals(expected, taskServiceMock.readById(5));
        verify(taskRepositoryMock).findById(5L);
    }

    @Test
    void testGetTaskByIncorrectId() {
        when(taskRepositoryMock.findById(anyLong())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> taskServiceMock.readById(5));
        verify(taskRepositoryMock).findById(5L);
    }

    @Test
    void testUpdateTaskByName() {
        Mockito.when(taskRepositoryMock.findById(anyLong())).thenReturn(Optional.of(expected));
        Mockito.when(taskRepositoryMock.save(any(Task.class))).thenReturn(expected);

        assertEquals(expected, taskServiceMock.update(expected));
        verify(taskRepositoryMock).findById(anyLong());
        verify(taskRepositoryMock).save(expected);
    }

    @Test
    void testUpdateTaskWithIncorrectName() {
        expected.setName(" ");
        Mockito.when(taskRepositoryMock.findById(any(long.class))).thenReturn(Optional.of(expected));
        Mockito.when(taskRepositoryMock.save(any(Task.class))).thenReturn(nullable(Task.class));

        assertNotEquals(expected, taskServiceMock.update(expected));
        verify(taskRepositoryMock, never()).save(new Task());
    }

    @Test
    void testUpdateTaskWithNullData() {
        Mockito.when(taskRepositoryMock.save(isNull())).thenThrow(NullEntityReferenceException.class);

        assertThrows(NullEntityReferenceException.class, () -> taskServiceMock.update(null));
        verify(taskRepositoryMock, never()).save(new Task());
    }

    @Test
    void testDeleteTask() {
        Mockito.when(taskRepositoryMock.findById(anyLong())).thenReturn(Optional.of(new Task()));
        doNothing().when(taskRepositoryMock).delete(any(Task.class));
        taskServiceMock.delete(anyLong());

        assertDoesNotThrow(() -> taskServiceMock.delete(anyLong()));
        verify(taskRepositoryMock, times(2)).findById(anyLong());
        verify(taskRepositoryMock, times(2)).delete(any(Task.class));
    }

    @Test
    void testDeleteTaskWithIncorrectId() {
        Mockito.when(taskRepositoryMock.findById(anyLong())).thenReturn(Optional.of(new Task()));
        Mockito.when(taskServiceMock.readById(any(long.class))).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> taskServiceMock.delete(anyLong()));
        verify(taskRepositoryMock, never()).delete(new Task());
    }

    @Test
    public void testDeleteTaskWithNullValueTest() {
        Mockito.when(taskRepositoryMock.findById(isNull())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> taskServiceMock.delete(anyLong()));
    }

    @Test
    void testGetAllTask() {
        int expected = 1;
        expectedList.add(this.expected);
        Mockito.when(taskServiceMock.getAll()).thenReturn(expectedList);

        assertEquals(expected, taskServiceMock.getAll().size());
    }

    @Test
    void testGetEmptyTaskList() {
        Mockito.when(taskServiceMock.getAll()).thenReturn(expectedList);

        assertEquals(expectedList, taskServiceMock.getAll());
    }

    @Test
    void testGetTasksByTodoId() {
        expectedList.add(expected);
        expectedList.add(expected);
        Mockito.when(taskRepositoryMock.getByTodoId(anyLong())).thenReturn(expectedList);

        assertEquals(expectedList, taskServiceMock.getByTodoId(anyLong()));
        assertEquals(2, taskServiceMock.getByTodoId(anyLong()).size());
    }

    @Test
    void testGetTasksByIncorrectTodoId() {
        Mockito.when(taskRepositoryMock.getByTodoId(anyLong())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> taskServiceMock.getByTodoId(anyLong()));
    }

}
*/
