package com.tander.search.controller;

import com.tander.profile.dto.ProfileDTO;
import com.tander.profile.model.Preferences;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.tander.search.service.SearchService;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping("")
    public List<ProfileDTO> search(@RequestParam(required = false) Long userId) {
        return searchService.search(userId);
    }

    @PostMapping("/custom")
    public List<ProfileDTO> searchByPreferences(@Valid @RequestBody Preferences preferences) {
        return searchService.searchByPreferences(preferences);
    };

    @GetMapping("/unAnswered/{id}")
    public List<ProfileDTO> getUnAnsweredMatches(@PathVariable Long id) {
        return searchService.getUnAnsweredMatches(id);
    }
}
