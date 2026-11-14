package com.example.homework.towns;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST API used by the React app (with Axios):
 *   GET    /api/counties      list of counties (for the drop-down)
 *   GET    /api/towns         list of towns
 *   GET    /api/towns/{id}    one town
 *   POST   /api/towns         create
 *   PUT    /api/towns/{id}    update
 *   DELETE /api/towns/{id}    delete
 */
@RestController
@RequestMapping("/api")
public class TownApiController {

    /** JSON shape of a town (flat, so no Hibernate objects are serialized). */
    public record TownDto(Long id, String name, Long countyId, String countyName,
                          boolean countySeat, boolean countyLevel) {
        static TownDto of(Town t) {
            return new TownDto(t.getId(), t.getName(), t.getCounty().getId(), t.getCounty().getName(),
                    t.isCountySeat(), t.isCountyLevel());
        }
    }

    public record CountyDto(Long id, String name) {}

    private final TownRepository townRepository;
    private final CountyRepository countyRepository;

    public TownApiController(TownRepository townRepository, CountyRepository countyRepository) {
        this.townRepository = townRepository;
        this.countyRepository = countyRepository;
    }

    @GetMapping("/counties")
    public List<CountyDto> counties() {
        return countyRepository.findAll().stream()
                .map(c -> new CountyDto(c.getId(), c.getName()))
                .sorted((a, b) -> a.name().compareTo(b.name()))
                .toList();
    }

    @GetMapping("/towns")
    public List<TownDto> towns() {
        return townRepository.findAllByOrderByNameAsc().stream().map(TownDto::of).toList();
    }

    @GetMapping("/towns/{id}")
    public ResponseEntity<?> town(@PathVariable Long id) {
        return townRepository.findById(id)
                .<ResponseEntity<?>>map(t -> ResponseEntity.ok(TownDto.of(t)))
                .orElseGet(() -> notFound(id));
    }

    @PostMapping("/towns")
    public ResponseEntity<?> create(@RequestBody TownDto in) {
        String error = validate(in);
        if (error != null) return ResponseEntity.badRequest().body(Map.of("error", error));
        County county = countyRepository.findById(in.countyId()).orElseThrow();
        Town saved = townRepository.save(new Town(in.name().trim(), county, in.countySeat(), in.countyLevel()));
        return ResponseEntity.status(HttpStatus.CREATED).body(TownDto.of(saved));
    }

    @PutMapping("/towns/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody TownDto in) {
        Optional<Town> existing = townRepository.findById(id);
        if (existing.isEmpty()) return notFound(id);
        String error = validate(in);
        if (error != null) return ResponseEntity.badRequest().body(Map.of("error", error));
        Town t = existing.get();
        t.setName(in.name().trim());
        t.setCounty(countyRepository.findById(in.countyId()).orElseThrow());
        t.setCountySeat(in.countySeat());
        t.setCountyLevel(in.countyLevel());
        return ResponseEntity.ok(TownDto.of(townRepository.save(t)));
    }

    @DeleteMapping("/towns/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!townRepository.existsById(id)) return notFound(id);
        townRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /** Server-side validation (the React form validates too, but the server must not trust it). */
    private String validate(TownDto in) {
        if (in == null) return "Missing data.";
        if (in.name() == null || in.name().isBlank()) return "The town name is required.";
        if (in.name().trim().length() > 100) return "The town name can be at most 100 characters.";
        if (in.countyId() == null || !countyRepository.existsById(in.countyId())) return "Please choose a valid county.";
        return null;
    }

    private ResponseEntity<?> notFound(Long id) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Town " + id + " not found."));
    }
}
