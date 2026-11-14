package com.example.homework.towns;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/** "towns" table of towns_db - this is the table of the React CRUD. */
@Entity
@Table(name = "towns")
public class Town {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tname", nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "countyid", nullable = false)
    private County county;

    @Column(name = "countyseat", nullable = false)
    private boolean countySeat;

    @Column(name = "countylevel", nullable = false)
    private boolean countyLevel;

    protected Town() {}

    public Town(String name, County county, boolean countySeat, boolean countyLevel) {
        this.name = name;
        this.county = county;
        this.countySeat = countySeat;
        this.countyLevel = countyLevel;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public County getCounty() { return county; }
    public void setCounty(County county) { this.county = county; }
    public boolean isCountySeat() { return countySeat; }
    public void setCountySeat(boolean countySeat) { this.countySeat = countySeat; }
    public boolean isCountyLevel() { return countyLevel; }
    public void setCountyLevel(boolean countyLevel) { this.countyLevel = countyLevel; }
}
