package com.college.PlacementApl.Repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.college.PlacementApl.Model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
