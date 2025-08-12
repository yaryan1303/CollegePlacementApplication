package com.college.PlacementApl.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.college.PlacementApl.Model.Company;
import com.college.PlacementApl.dtos.CompanyStatsDto;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    @Query("SELECT new com.college.PlacementApl.dtos.CompanyStatsDto(" +
            "c.name, " +
            "COUNT(DISTINCT v), " +
            "COUNT(DISTINCT a), " +
            "COUNT(DISTINCT pr)) " +
            "FROM Company c " +
            "LEFT JOIN c.visits v " +
            "LEFT JOIN StudentApplication a ON a.visit = v " +
            "LEFT JOIN PlacementRecord pr ON pr.company = c " +
            "GROUP BY c.name " +
            "ORDER BY c.name")
    List<CompanyStatsDto> getCompanyStats();


    Optional<Company> findByName(String name);
    Optional<Company> findByContactEmail(String contactEmail);


}
