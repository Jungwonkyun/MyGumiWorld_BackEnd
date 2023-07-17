package com.mygumi.insider.repository;

import com.mygumi.insider.dto.MealDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MealRepository extends JpaRepository<MealDTO, Long> {
    List<MealDTO> findByTitle(String title);
}