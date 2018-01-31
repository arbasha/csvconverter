package com.csv.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.csv.model.InvalidDeal;

public interface InvalidDealRepository extends JpaRepository<InvalidDeal, Long> {

}
