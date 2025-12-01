package com.example.demo.service;

import com.example.demo.entity.Commentary;
import com.example.demo.entity.User;
import com.example.demo.repository.CommentaryRepository;
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
class CommentaryServiceTest {

    @Mock
    private CommentaryRepository commentaryRepository;

    @InjectMocks
    private CommentaryService commentaryService;

    private Commentary commentary;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User("testuser", "test@example.com", "Password123!");
        commentary = new Commentary("Great game!", user, 1);
    }

    @Test
    void getAll_ShouldReturnAllCommentaries() {
        // Given
        Commentary commentary2 = new Commentary("Amazing experience!", user, 2);
        List<Commentary> expectedCommentaries = Arrays.asList(commentary, commentary2);
        when(commentaryRepository.findAll()).thenReturn(expectedCommentaries);

        // When
        List<Commentary> result = commentaryService.getAll();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(commentary, commentary2);
        verify(commentaryRepository, times(1)).findAll();
    }

    @Test
    void getAll_ShouldReturnEmptyList_WhenNoCommentaries() {
        // Given
        when(commentaryRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<Commentary> result = commentaryService.getAll();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(commentaryRepository, times(1)).findAll();
    }

    @Test
    void getOneById_ShouldReturnCommentary_WhenExists() {
        // Given
        Long commentaryId = 1L;
        when(commentaryRepository.findById(commentaryId)).thenReturn(Optional.of(commentary));

        // When
        Commentary result = commentaryService.getOneById(commentaryId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("Great game!");
        assertThat(result.getGameId()).isEqualTo(1);
        verify(commentaryRepository, times(1)).findById(commentaryId);
    }

    @Test
    void createCommentary_ShouldSaveAndReturnCommentary() {
        // Given
        when(commentaryRepository.save(any(Commentary.class))).thenReturn(commentary);

        // When
        Commentary result = commentaryService.createCommentary(commentary);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("Great game!");
        assertThat(result.getGameId()).isEqualTo(1);
        verify(commentaryRepository, times(1)).save(commentary);
    }

    @Test
    void createCommentary_ShouldHandleDifferentContent() {
        // Given
        Commentary newCommentary = new Commentary("Different content", user, 5);
        when(commentaryRepository.save(any(Commentary.class))).thenReturn(newCommentary);

        // When
        Commentary result = commentaryService.createCommentary(newCommentary);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("Different content");
        assertThat(result.getGameId()).isEqualTo(5);
        verify(commentaryRepository, times(1)).save(newCommentary);
    }

    @Test
    void deleteCommentary_ShouldCallRepositoryDelete() {
        // Given
        Long commentaryId = 1L;
        doNothing().when(commentaryRepository).deleteById(commentaryId);

        // When
        commentaryService.deleteCommentary(commentaryId);

        // Then
        verify(commentaryRepository, times(1)).deleteById(commentaryId);
    }

    @Test
    void deleteCommentary_ShouldHandleDifferentIds() {
        // Given
        Long commentaryId = 999L;
        doNothing().when(commentaryRepository).deleteById(commentaryId);

        // When
        commentaryService.deleteCommentary(commentaryId);

        // Then
        verify(commentaryRepository, times(1)).deleteById(commentaryId);
    }
}