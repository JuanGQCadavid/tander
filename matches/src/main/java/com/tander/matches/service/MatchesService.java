package com.tander.matches.service;

import com.tander.commons.model.Notification;
import com.tander.commons.model.payloads.MatchNotification;
import com.tander.matches.dto.CreateMatchDTO;
import com.tander.matches.dto.MatchDTO;
import com.tander.matches.model.Match;
import com.tander.matches.model.Status;
import com.tander.matches.repository.MatchesRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
@Slf4j
public class MatchesService {
    @Autowired
    private MatchesRepository matchesRepository;
    private final KafkaTemplate<String, Notification> kafki;


    public MatchDTO createMatch(CreateMatchDTO matchDTO) {
        Match match = Match.builder()
                .profileId1(matchDTO.getProfileId1())
                .profileId2(matchDTO.getProfileId2())
                .profileStatus1(matchDTO.getProfileStatus1())
                .profileStatus2(Status.NO_ANSWER)
                .build();
        matchesRepository.save(match);
        log.info("Match {} created", match.getMatchId());
        return mapToMatchDto(match);
    }

    public MatchDTO updateMatch(Long id, MatchDTO matchDTO) {
        Match match = matchesRepository.findById(id).orElse(null);

        if (match != null) {
            match.setProfileStatus1(matchDTO.getProfileStatus1());
            match.setProfileStatus2(matchDTO.getProfileStatus2());
            matchesRepository.save(match);
            if (matchDTO.getProfileStatus1().equals(Status.LIKE) && matchDTO.getProfileStatus2().equals(Status.LIKE)) {
                Notification notification = new Notification();
                MatchNotification matchNotification = new MatchNotification();
                matchNotification.setDateOfMatch(String.valueOf(OffsetDateTime.now()));
                matchNotification.setUserIDA(String.valueOf(matchDTO.getProfileId1()));
                matchNotification.setUserIDB(String.valueOf(matchDTO.getProfileId2()));
                matchNotification.setUserNameA("");
                matchNotification.setUserNameB("");
                notification.setMatchNotification(matchNotification);
                kafki.send("notification", notification);
                kafki.send("chat", notification);
            }
            log.info("Match {} updated", match.getMatchId());
            return mapToMatchDto(match);
        } else {
            throw new RuntimeException("Match not found");
        }
    }

    public void deleteMatch(Long id) {
        matchesRepository.deleteById(id);
        log.info("Match {} deleted", id);
    }

    public List<MatchDTO> getAllMatches() {
        List<Match> matches = new ArrayList<>();
        matchesRepository.findAll().forEach(matches::add);
        return matches.stream().map(this::mapToMatchDto).toList();
    }

    public List<MatchDTO> getUserMatches(Long profileId) {
        List<Match> matches = new ArrayList<>(matchesRepository.findUserMatches(profileId));
        return matches.stream().map(this::mapToMatchDto).toList();
    }

    public List<MatchDTO> getUnansweredMatches(Long profileId) {
        List<Match> matches = new ArrayList<>(matchesRepository.findUnansweredMatches(profileId));
        return matches.stream().map(this::mapToMatchDto).toList();
    }

    public List<MatchDTO> getAnsweredMatches(Long profileId) {
        List<Match> matches = new ArrayList<>(matchesRepository.findAnsweredMatches(profileId));
        return matches.stream().map(this::mapToMatchDto).toList();
    }

    public Optional<MatchDTO> getMatchByUserIds(Long user1Id, Long user2Id) {
        Optional<Match> match = matchesRepository.findByUserIds(user1Id, user2Id);
        return match.map(this::mapToMatchDto);
    }


    private MatchDTO mapToMatchDto(Match match) {
        return MatchDTO.builder()
                .matchId(match.getMatchId())
                .profileId1(match.getProfileId1())
                .profileId2(match.getProfileId2())
                .profileStatus1(match.getProfileStatus1())
                .profileStatus2(match.getProfileStatus2())
                .createdAt(match.getCreatedAt())
                .build();
    }


}
