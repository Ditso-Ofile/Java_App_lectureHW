package com.example.homework.towns;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TownRepository extends JpaRepository<Town, Long> {
    List<Town> findAllByOrderByNameAsc();
}
