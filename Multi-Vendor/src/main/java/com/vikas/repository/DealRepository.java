package com.vikas.repository;

import com.vikas.model.Deal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface DealRepository extends JpaRepository<Deal, Long> {
    
}
