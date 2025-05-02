package com.tander.matches.controller;

import com.tander.matches.dto.CreateMatchDTO;
import com.tander.matches.dto.MatchDTO;
import com.tander.matches.service.MatchesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/matches")
public class MatchesController {
    @Autowired
    private MatchesService matchesService;

    @GetMapping
    public List<MatchDTO> getAllMatches() {
        return matchesService.getAllMatches();
    }

    @GetMapping("/profile/{id}")
    public List<MatchDTO> getUserMatches(@PathVariable Long id) {
        return matchesService.getUserMatches(id);
    }

    @GetMapping("/getMatch")
    public Optional<MatchDTO> getMatchByUserIds(@RequestParam long profileId1, @RequestParam long profileId2) {
        return matchesService.getMatchByUserIds(profileId1, profileId2);
    }

    @PostMapping(value = "/create", consumes = "application/json", produces = "application/json")
    public MatchDTO createMatch(@RequestBody CreateMatchDTO matchDTO) {
        return matchesService.createMatch(matchDTO);
    }

    @PutMapping("/{id}")
    public MatchDTO updateMatch(@PathVariable Long id, @RequestBody MatchDTO matchDTO) {
        return matchesService.updateMatch(id, matchDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteMatch(@PathVariable Long id) {
        matchesService.deleteMatch(id);
    }
}
