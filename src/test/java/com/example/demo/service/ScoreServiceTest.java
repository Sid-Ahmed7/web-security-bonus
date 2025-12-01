package com.example.demo.service;

import com.example.demo.entity.Score;
import com.example.demo.repository.ScoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScoreServiceTest {

    @Mock
    private ScoreRepository scoreRepository;

    @InjectMocks
    private ScoreService scoreService;

    private Score score;

    @BeforeEach
    void setUp() {
        score = new Score(1L, 1000L, 1L, 1L);
    }

    @Test
    void findAll_ShouldReturnAllScores() {
        // Given
        Score score2 = new Score(2L, 2000L, 2L, 1L);
        List<Score> expectedScores = Arrays.asList(score, score2);
        when(scoreRepository.findAll()).thenReturn(expectedScores);

        // When
        List<Score> result = scoreService.findAll();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(score, score2);
        verify(scoreRepository, times(1)).findAll();
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenNoScores() {
        // Given
        when(scoreRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<Score> result = scoreService.findAll();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(scoreRepository, times(1)).findAll();
    }

    @Test
    void findById_ShouldReturnScore_WhenExists() {
        // Given
        Long scoreId = 1L;
        when(scoreRepository.findById(scoreId)).thenReturn(Optional.of(score));

        // When
        Optional<Score> result = scoreService.findById(scoreId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getScore()).isEqualTo(1000L);
        assertThat(result.get().getUserId()).isEqualTo(1L);
        assertThat(result.get().getGameId()).isEqualTo(1L);
        verify(scoreRepository, times(1)).findById(scoreId);
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotExists() {
        // Given
        Long scoreId = 999L;
        when(scoreRepository.findById(scoreId)).thenReturn(Optional.empty());

        // When
        Optional<Score> result = scoreService.findById(scoreId);

        // Then
        assertThat(result).isEmpty();
        verify(scoreRepository, times(1)).findById(scoreId);
    }

    @Test
    void save_ShouldSaveAndReturnScore() {
        // Given
        Score newScore = new Score(null, 1500L, 2L, 3L);
        Score savedScore = new Score(3L, 1500L, 2L, 3L);
        when(scoreRepository.save(any(Score.class))).thenReturn(savedScore);

        // When
        Score result = scoreService.save(newScore);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getScore()).isEqualTo(1500L);
        assertThat(result.getUserId()).isEqualTo(2L);
        assertThat(result.getGameId()).isEqualTo(3L);
        verify(scoreRepository, times(1)).save(newScore);
    }

    @Test
    void save_ShouldUpdateExistingScore() {
        // Given
        Score existingScore = new Score(1L, 2500L, 1L, 1L);
        when(scoreRepository.save(any(Score.class))).thenReturn(existingScore);

        // When
        Score result = scoreService.save(existingScore);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getScore()).isEqualTo(2500L);
        verify(scoreRepository, times(1)).save(existingScore);
    }

    @Test
    void deleteById_ShouldCallRepositoryDelete() {
        // Given
        Long scoreId = 1L;
        doNothing().when(scoreRepository).deleteById(scoreId);

        // When
        scoreService.deleteById(scoreId);

        // Then
        verify(scoreRepository, times(1)).deleteById(scoreId);
    }

    @Test
    void update_ShouldUpdateScore_WhenExists() {
        // Given
        Long scoreId = 1L;
        Score updatedDetails = new Score(null, 3000L, 5L, 10L);
        Score existingScore = new Score(1L, 1000L, 1L, 1L);
        
        when(scoreRepository.findById(scoreId)).thenReturn(Optional.of(existingScore));
        when(scoreRepository.save(any(Score.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Optional<Score> result = scoreService.update(scoreId, updatedDetails);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getScore()).isEqualTo(3000L);
        assertThat(result.get().getUserId()).isEqualTo(5L);
        assertThat(result.get().getGameId()).isEqualTo(10L);
        verify(scoreRepository, times(1)).findById(scoreId);
        verify(scoreRepository, times(1)).save(any(Score.class));
    }

    @Test
    void update_ShouldReturnEmpty_WhenScoreNotExists() {
        // Given
        Long scoreId = 999L;
        Score updatedDetails = new Score(null, 3000L, 5L, 10L);
        when(scoreRepository.findById(scoreId)).thenReturn(Optional.empty());

        // When
        Optional<Score> result = scoreService.update(scoreId, updatedDetails);

        // Then
        assertThat(result).isEmpty();
        verify(scoreRepository, times(1)).findById(scoreId);
        verify(scoreRepository, never()).save(any(Score.class));
    }

    @Test
    void update_ShouldOnlyUpdateProvidedFields() {
        // Given
        Long scoreId = 1L;
        Score updatedDetails = new Score(null, 5000L, 3L, 2L);
        Score existingScore = new Score(1L, 1000L, 1L, 1L);
        
        when(scoreRepository.findById(scoreId)).thenReturn(Optional.of(existingScore));
        when(scoreRepository.save(any(Score.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Optional<Score> result = scoreService.update(scoreId, updatedDetails);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L); // ID should remain unchanged
        assertThat(result.get().getScore()).isEqualTo(5000L);
        assertThat(result.get().getUserId()).isEqualTo(3L);
        assertThat(result.get().getGameId()).isEqualTo(2L);
        verify(scoreRepository, times(1)).findById(scoreId);
        verify(scoreRepository, times(1)).save(any(Score.class));
    }
}