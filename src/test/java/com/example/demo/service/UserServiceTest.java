package com.example.demo.service;

import com.example.demo.config.JwtService;
import com.example.demo.entity.Game;
import com.example.demo.entity.User;
import com.example.demo.entity.dto.GameDTO;
import com.example.demo.repository.GameRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    private User user;
    private Game game;

    @BeforeEach
    void setUp() {
        user = new User("testuser", "test@example.com", "Password123!");
        game = new Game();
        game.setId(1L);
    }

    @Test
    void getAll_ShouldReturnAllUsers() {
        // Given
        User user2 = new User("testuser2", "test2@example.com", "Password123!");
        List<User> expectedUsers = Arrays.asList(user, user2);
        when(userRepository.findAll()).thenReturn(expectedUsers);

        // When
        List<User> result = userService.getAll();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(user, user2);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getOneById_ShouldReturnUser_WhenExists() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        User result = userService.getOneById(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getOneBySlug_ShouldReturnUser_WhenExists() {
        // Given
        String slug = "testuser";
        user.setSlug(slug);
        when(userRepository.findBySlug(slug)).thenReturn(user);

        // When
        User result = userService.getOneBySlug(slug);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getSlug()).isEqualTo(slug);
        verify(userRepository, times(1)).findBySlug(slug);
    }

    @Test
    void createUser_ShouldCreateUserWithSlug() {
        // Given
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        User result = userService.createUser(user);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getSlug()).isNotNull();
        assertThat(result.getSlug()).isEqualTo("testuser");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void findByemail_ShouldReturnUser_WhenExists() {
        // Given
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(user);

        // When
        User result = userService.findByemail(email);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(email);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void updateUser_ShouldUpdateUserFields() {
        // Given
        Long userId = 1L;
        User updatedUser = new User("newusername", "newemail@example.com", "NewPassword123!", "New bio");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        User result = userService.updateUser(userId, updatedUser);

        // Then
        assertThat(result).isNotNull();
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_ShouldNotUpdatePassword_WhenNull() {
        // Given
        Long userId = 1L;
        User updatedUser = new User("newusername", "newemail@example.com", null, "New bio");
        String originalPassword = "Password123!";
        user.setPassword(originalPassword);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        User result = userService.updateUser(userId, updatedUser);

        // Then
        assertThat(result.getPassword()).isEqualTo(originalPassword);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateBanner_ShouldUpdateBannerPicture() {
        // Given
        Long userId = 1L;
        User userWithBanner = new User();
        userWithBanner.setBannerPicture("new-banner.jpg");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        User result = userService.updateBanner(userId, userWithBanner);

        // Then
        assertThat(result.getBannerPicture()).isEqualTo("new-banner.jpg");
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateProfilePicture_ShouldUpdateProfilePicture() {
        // Given
        Long userId = 1L;
        User userWithProfile = new User();
        userWithProfile.setProfilePicture("new-profile.jpg");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        User result = userService.updateProfilePicture(userId, userWithProfile);

        // Then
        assertThat(result.getProfilePicture()).isEqualTo("new-profile.jpg");
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void deleteUser_ShouldCallRepositoryDelete() {
        // Given
        Long userId = 1L;
        doNothing().when(userRepository).deleteById(userId);

        // When
        userService.deleteUser(userId);

        // Then
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void findUserIdByUsername_ShouldReturnUserId_WhenExists() {
        // Given
        String username = "testuser";
        user.setSlug(username);
        when(userRepository.findByUsername(username)).thenReturn(user);

        // When
        Long result = userService.findUserIdByUsername(username);

        // Then
        assertThat(result).isEqualTo(user.getId());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void findUserIdByUsername_ShouldThrowException_WhenNotExists() {
        // Given
        String username = "nonexistent";
        when(userRepository.findByUsername(username)).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> userService.findUserIdByUsername(username))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found");
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void findUserSlugByUsername_ShouldReturnSlug_WhenExists() {
        // Given
        String username = "testuser";
        String slug = "testuser";
        user.setSlug(slug);
        when(userRepository.findByUsername(username)).thenReturn(user);

        // When
        String result = userService.findUserSlugByUsername(username);

        // Then
        assertThat(result).isEqualTo(slug);
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void findUserSlugByUsername_ShouldThrowException_WhenNotExists() {
        // Given
        String username = "nonexistent";
        when(userRepository.findByUsername(username)).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> userService.findUserSlugByUsername(username))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User slug not found");
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void addGame_ShouldAddGameToUser_WhenGameExists() {
        // Given
        Long userId = 1L;
        Long gameId = 1L;
        user.setGames(new ArrayList<>());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        userService.addGame(userId, gameId);

        // Then
        assertThat(user.getGames()).contains(game);
        verify(userRepository, times(1)).findById(userId);
        verify(gameRepository, times(1)).findById(gameId);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void addGame_ShouldCreateAndAddGame_WhenGameNotExists() {
        // Given
        Long userId = 1L;
        Long gameId = 2L;
        user.setGames(new ArrayList<>());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(gameRepository.findById(gameId)).thenReturn(Optional.empty());
        when(gameRepository.save(any(Game.class))).thenReturn(game);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        userService.addGame(userId, gameId);

        // Then
        verify(userRepository, times(1)).findById(userId);
        verify(gameRepository, times(1)).findById(gameId);
        verify(gameRepository, times(1)).save(any(Game.class));
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void addGame_ShouldThrowException_WhenGameAlreadyInList() {
        // Given
        Long userId = 1L;
        Long gameId = 1L;
        List<Game> games = new ArrayList<>();
        games.add(game);
        user.setGames(games);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

        // When & Then
        assertThatThrownBy(() -> userService.addGame(userId, gameId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Game is already registered in the user list");
        verify(userRepository, times(1)).findById(userId);
        verify(gameRepository, times(1)).findById(gameId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void addGame_ShouldThrowException_WhenUserNotFound() {
        // Given
        Long userId = 999L;
        Long gameId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.addGame(userId, gameId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found with id: " + userId);
        verify(userRepository, times(1)).findById(userId);
        verify(gameRepository, never()).findById(anyLong());
    }

    @Test
    void getList_ShouldReturnGameDTOList_WhenUserExists() {
        // Given
        Long userId = 1L;
        Game game2 = new Game();
        game2.setId(2L);
        List<Game> games = Arrays.asList(game, game2);
        user.setGames(games);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        List<GameDTO> result = userService.getList(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(1).getId()).isEqualTo(2L);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getList_ShouldThrowException_WhenUserNotFound() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.getList(userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found with id: " + userId);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void deleteGame_ShouldRemoveGameFromUser_WhenExists() {
        // Given
        Long userId = 1L;
        Long gameId = 1L;
        List<Game> games = new ArrayList<>();
        games.add(game);
        user.setGames(games);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        List<GameDTO> result = userService.deleteGame(userId, gameId);

        // Then
        assertThat(result).isNotNull();
        assertThat(user.getGames()).doesNotContain(game);
        verify(userRepository, times(1)).findById(userId);
        verify(gameRepository, times(1)).findById(gameId);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void deleteGame_ShouldThrowException_WhenGameNotInList() {
        // Given
        Long userId = 1L;
        Long gameId = 999L;
        user.setGames(new ArrayList<>());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(gameRepository.findById(gameId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.deleteGame(userId, gameId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("le jeux n'est pas dans la liste: " + gameId);
        verify(userRepository, times(1)).findById(userId);
        verify(gameRepository, times(1)).findById(gameId);
    }

    @Test
    void deleteGame_ShouldThrowException_WhenUserNotFound() {
        // Given
        Long userId = 999L;
        Long gameId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.deleteGame(userId, gameId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found with id: " + userId);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getIdInToken_ShouldReturnUserId_WhenTokenValid() {
        // Given
        String token = "valid.jwt.token";
        String username = "testuser";
        when(jwtService.extractUsername(token)).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(user);

        // When
        Long result = userService.getIdInToken(token);

        // Then
        assertThat(result).isEqualTo(user.getId());
        verify(jwtService, times(1)).extractUsername(token);
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void getSlugInToken_ShouldReturnUserSlug_WhenTokenValid() {
        // Given
        String token = "valid.jwt.token";
        String username = "testuser";
        String slug = "testuser";
        user.setSlug(slug);
        when(jwtService.extractUsername(token)).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(user);

        // When
        String result = userService.getSlugInToken(token);

        // Then
        assertThat(result).isEqualTo(slug);
        verify(jwtService, times(1)).extractUsername(token);
        verify(userRepository, times(1)).findByUsername(username);
    }
}