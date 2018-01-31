package com.csv.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.csv.model.Deal;

public interface DealRepository extends JpaRepository<Deal, Long> {

}
