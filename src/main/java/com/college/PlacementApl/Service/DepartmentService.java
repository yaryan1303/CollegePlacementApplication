package com.college.PlacementApl.Service;

import com.college.PlacementApl.Model.Department;

import java.util.List;

public interface DepartmentService {

    Department createDepartment(Department department);

    List<Department> getAllDepartments();

    Department updateDepartment(Long id, Department department);
}
