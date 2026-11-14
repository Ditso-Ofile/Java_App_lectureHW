package com.example.homework.towns;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fills the H2 database from towns_db (data/counties.csv, data/towns.csv) on the FIRST start only.
 * Later starts keep the data in C:\data, so the changes made with the React CRUD are not lost.
 */
@Component
public class DataLoader implements CommandLineRunner {

    private final CountyRepository countyRepository;
    private final TownRepository townRepository;

    public DataLoader(CountyRepository countyRepository, TownRepository townRepository) {
        this.countyRepository = countyRepository;
        this.townRepository = townRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (countyRepository.count() == 0) {
            List<County> counties = new ArrayList<>();
            for (String[] r : readCsv("data/counties.csv")) {
                counties.add(new County(Long.parseLong(r[0]), r[1]));
            }
            countyRepository.saveAll(counties);
        }
        if (townRepository.count() == 0) {
            Map<Long, County> byId = new HashMap<>();
            countyRepository.findAll().forEach(c -> byId.put(c.getId(), c));
            List<Town> towns = new ArrayList<>();
            for (String[] r : readCsv("data/towns.csv")) {
                towns.add(new Town(r[1], byId.get(Long.parseLong(r[2])), "1".equals(r[3]), "1".equals(r[4])));
            }
            townRepository.saveAll(towns);
        }
    }

    /** Simple CSV reader (the town names contain no commas or quotes). Skips the header row. */
    private List<String[]> readCsv(String path) throws Exception {
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new ClassPathResource(path).getInputStream(), StandardCharsets.UTF_8))) {
            String line = br.readLine(); // header
            while ((line = br.readLine()) != null) {
                if (!line.isBlank()) rows.add(line.split(",", -1));
            }
        }
        return rows;
    }
}
