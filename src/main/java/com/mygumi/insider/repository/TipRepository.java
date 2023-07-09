package com.mygumi.insider.repository;

import com.mygumi.insider.dto.Tip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface TipRepository extends JpaRepository<Tip, Long>{
    Optional<Tip> findById(int id);

}
