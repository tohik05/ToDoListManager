package com.softserve.todolistmanager.service.impl;

import com.softserve.todolistmanager.exception.NullEntityReferenceException;
import com.softserve.todolistmanager.model.State;
import com.softserve.todolistmanager.repository.StateRepository;
import com.softserve.todolistmanager.service.StateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StateServiceImpl implements StateService {
    private final StateRepository stateRepository;

    @Autowired
    public StateServiceImpl(StateRepository stateRepository) {
        this.stateRepository = stateRepository;
    }

    @Override
    public State create(State state) {
        if (state == null) {
            throw new NullEntityReferenceException("State cannot be 'null'");
        }
        return stateRepository.save(state);
    }

    @Override
    public State readById(long id) {
        return stateRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("State with id '%s' not found", id)));
    }

    @Override
    public State update(State state) {
        if (state == null) {
            throw new NullEntityReferenceException("State cannot be 'null'");
        }
        readById(state.getId());
        return stateRepository.save(state);
    }

    @Override
    public void delete(long id) {
        stateRepository.delete(readById(id));
    }

    @Override
    public State getByName(String name) {
        Optional<State> stateByName = Optional.ofNullable(stateRepository.findByName(name));
        if (stateByName.isEmpty()) {
            throw new EntityNotFoundException(String.format("State with name '%s' not found", name));
        }
        return stateByName.get();
    }

    @Override
    public List<State> getAll() {
        List<State> states = stateRepository.getAll();
        return states.isEmpty() ? new ArrayList<>() : states;
    }
}
