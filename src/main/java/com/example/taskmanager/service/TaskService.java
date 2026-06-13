package com.example.taskmanager.service;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class TaskService {

    private final TaskRepository repo;

    public TaskService(TaskRepository repo) {
        this.repo = repo;
    }

    public List<Task> findAll() { return repo.findAll(); }

    public List<Task> findByStatus(Task.Status status) { return repo.findByStatus(status); }

    public Task findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Task not found: " + id));
    }

    public Task create(Task task) { return repo.save(task); }

    public Task update(Long id, Task patch) {
        Task existing = findById(id);
        existing.setTitle(patch.getTitle());
        existing.setDescription(patch.getDescription());
        if (patch.getStatus() != null) {
            existing.setStatus(patch.getStatus());
        }
        return repo.save(existing);
    }

    public void delete(Long id) {
        findById(id);
        repo.deleteById(id);
    }
}