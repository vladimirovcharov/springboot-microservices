package org.example.departmentservice.client;

import org.example.departmentservice.model.Employee;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

@HttpExchange
public interface EmployeeClient {
    @GetExchange("/employees/department/{departmentId}")
    List<Employee> findByDepartment(@PathVariable("departmentId") Long departmentId);
}
