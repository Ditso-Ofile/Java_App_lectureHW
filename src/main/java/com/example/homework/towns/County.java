package com.example.homework.towns;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** "counties" table of towns_db (read-only lookup table for the towns CRUD). */
@Entity
@Table(name = "counties")
public class County {

    @Id
    private Long id;

    @Column(name = "cname", nullable = false, length = 50)
    private String name;

    protected County() {}

    public County(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
}
