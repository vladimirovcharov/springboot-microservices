package org.example.departmentservice.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import org.example.departmentservice.client.EmployeeClient;
import org.example.departmentservice.model.Department;
import org.example.departmentservice.repository.DepartmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/departments")
@AllArgsConstructor
public class DepartmentController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DepartmentController.class);

    private DepartmentRepository repository;
    private EmployeeClient employeeClient;

    @PostMapping
    public Department add(@RequestBody Department department) {
        LOGGER.info("Department add: {}", department);
        return repository.addDepartment(department);
    }

    @GetMapping
    public List<Department> findAll() {
        LOGGER.info("Department find all");
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Department findById(@PathVariable Long id) {
        LOGGER.info("Department find: id={}", id);
        return repository.findById(id);
    }

    @GetMapping("/with-employees")
    @CircuitBreaker(name = "employee-service", fallbackMethod = "getEmptyDepartments")
    public ResponseEntity<List<Department>> findAllWithEmployees() {
        LOGGER.info("Department find all with employees");
        List<Department> departments = repository.findAll();
        departments.forEach(department -> department.setEmployees(employeeClient.findByDepartment(department.getId())));
        return new ResponseEntity<>(departments, HttpStatus.OK);
    }

    public ResponseEntity<List<Department>> getEmptyDepartments(Exception e) {
        LOGGER.error("Oops! Something went wrong! Try again later. {}", e.getMessage());
        return new ResponseEntity<>(Collections.emptyList(), HttpStatus.SERVICE_UNAVAILABLE);
    }
}