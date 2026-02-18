package org.example.blogr.services;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.blogr.domain.User;
import org.example.blogr.exceptions.DuplicateEmailException;
import org.example.blogr.exceptions.DuplicateUsernameException;
import org.example.blogr.exceptions.InvalidCredentialsException;
import org.example.blogr.exceptions.UserNotFoundException;
import org.example.blogr.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for UserService using a test MongoDB database.
 * Tests run against a real MongoDB instance (test database),
 */
class UserServiceTest {

    private static final String TEST_DATABASE = "test";
    private static final String MONGO_URI = "mongodb://localhost:27017";

    private MongoClient mongoClient;
    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        // Connect to the test database
        mongoClient = MongoClients.create(MONGO_URI);
        userRepository = new UserRepository(mongoClient, TEST_DATABASE);
        userService = new UserService(userRepository);
        
        // Clean up the users collection before each test
        mongoClient.getDatabase(TEST_DATABASE).getCollection("users").deleteMany(new Document());
    }

    @AfterEach
    void tearDown() {
        // Clean up after each test
        if (mongoClient != null) {
            mongoClient.getDatabase(TEST_DATABASE).getCollection("users").deleteMany(new Document());
            mongoClient.close();
        }
    }

    @Nested
    @DisplayName("Register Tests")
    class RegisterTests {

        @Test
        @DisplayName("register - should create user with valid data")
        void register_success() {
            // Act
            userService.register("testuser", "test@example.com", "password123");

            // Assert - verify user was created by finding it
            ObjectId userId = userRepository.findByUsername("testuser");
            assertNotNull(userId);
            
            User createdUser = userRepository.findById(userId);
            assertNotNull(createdUser);
            assertEquals("testuser", createdUser.username());
            assertEquals("test@example.com", createdUser.email());
        }

        @Test
        @DisplayName("register - should throw DuplicateUsernameException when username exists")
        void register_duplicateUsername() {
            // Arrange - create an existing user
            userService.register("existinguser", "existing@example.com", "password123");

            // Act & Assert
            assertThrows(DuplicateUsernameException.class, () ->
                userService.register("existinguser", "new@example.com", "password123")
            );
        }

        @Test
        @DisplayName("register - should throw DuplicateEmailException when email exists")
        void register_duplicateEmail() {
            // Arrange - create an existing user
            userService.register("existinguser", "existing@example.com", "password123");

            // Act & Assert
            assertThrows(DuplicateEmailException.class, () ->
                userService.register("newuser", "existing@example.com", "password123")
            );
        }
    }

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        @DisplayName("login - should return userId when login with valid username")
        void login_withUsername_success() {
            // Arrange
            userService.register("testuser", "test@example.com", "password123");

            // Act
            ObjectId result = userService.login("testuser", "password123");

            // Assert
            assertNotNull(result);
        }

        @Test
        @DisplayName("login - should return userId when login with valid email")
        void login_withEmail_success() {
            // Arrange
            userService.register("testuser", "test@example.com", "password123");

            // Act
            ObjectId result = userService.login("test@example.com", "password123");

            // Assert
            assertNotNull(result);
        }

        @Test
        @DisplayName("login - should throw UserNotFoundException when user not found")
        void login_userNotFound() {
            // Act & Assert
            assertThrows(UserNotFoundException.class, () ->
                userService.login("nonexistent", "password123")
            );
        }

        @Test
        @DisplayName("login - should throw InvalidCredentialsException when password is wrong")
        void login_invalidPassword() {
            // Arrange
            userService.register("testuser", "test@example.com", "password123");

            // Act & Assert
            assertThrows(InvalidCredentialsException.class, () ->
                userService.login("testuser", "wrongpassword")
            );
        }
    }

    @Nested
    @DisplayName("Profile Tests")
    class ProfileTests {

        @Test
        @DisplayName("getMyProfile - should return user when ID exists")
        void getMyProfile_success() {
            // Arrange
            userService.register("testuser", "test@example.com", "password123");
            ObjectId userId = userRepository.findByUsername("testuser");

            // Act
            User result = userService.getMyProfile(userId);

            // Assert
            assertNotNull(result);
            assertEquals("testuser", result.username());
            assertEquals("test@example.com", result.email());
        }

        @Test
        @DisplayName("getMyProfile - should throw UserNotFoundException when ID not found")
        void getMyProfile_notFound() {
            // Arrange
            ObjectId nonExistentId = new ObjectId();

            // Act & Assert
            assertThrows(UserNotFoundException.class, () ->
                userService.getMyProfile(nonExistentId)
            );
        }
    }

    @Nested
    @DisplayName("Search Tests")
    class SearchTests {

        @Test
        @DisplayName("findUsersByUsername - should return list of matching users")
        void findUsersByUsername_success() {
            // Arrange
            userService.register("testuser1", "test1@example.com", "password123");
            userService.register("testuser2", "test2@example.com", "password123");

            // Act
            List<User> result = userService.findUsersByUsername("test");

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("findUsersByUsername - should throw UserNotFoundException when no matches")
        void findUsersByUsername_notFound() {
            // Act & Assert
            assertThrows(UserNotFoundException.class, () ->
                userService.findUsersByUsername("nonexistent")
            );
        }

        @Test
        @DisplayName("findUserByUsername - should return userId when username exists")
        void findUserByUsername_success() {
            // Arrange
            userService.register("testuser", "test@example.com", "password123");

            // Act
            ObjectId result = userService.findUserByUsername("testuser");

            // Assert
            assertNotNull(result);
        }

        @Test
        @DisplayName("findUserByUsername - should throw UserNotFoundException when username not found")
        void findUserByUsername_notFound() {
            // Act & Assert
            assertThrows(UserNotFoundException.class, () ->
                userService.findUserByUsername("nonexistent")
            );
        }
    }
}
