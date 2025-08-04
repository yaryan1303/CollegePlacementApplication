package com.college.PlacementApl.Service.Impl;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.college.PlacementApl.Model.Department;
import com.college.PlacementApl.Repository.DepartmentRepository;
import com.college.PlacementApl.Service.DepartmentService;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    private DepartmentRepository departmentRepository;

    @Autowired
    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public Department createDepartment(Department department) {

        return departmentRepository.save(department);
    }

    public List<Department> getAllDepartments() {
        
        return departmentRepository.findAll();
    }

    public Department updateDepartment(Long id, Department department) {
        Department existingDepartment = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        existingDepartment.setName(department.getName());

        return departmentRepository.save(existingDepartment);
    }

}

