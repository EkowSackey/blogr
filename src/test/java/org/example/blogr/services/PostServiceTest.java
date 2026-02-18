package org.example.blogr.services;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.blogr.domain.Post;
import org.example.blogr.domain.Tag;
import org.example.blogr.exceptions.PostNotFoundException;
import org.example.blogr.repositories.PostRepository;
import org.example.blogr.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for PostService using a test MongoDB database.
 * Tests run against a real MongoDB instance (test database)
 */
class PostServiceTest {

    private static final String TEST_DATABASE = "test";
    private static final String MONGO_URI = "mongodb://localhost:27017";

    private MongoClient mongoClient;
    private PostRepository postRepository;
    private PostService postService;
    private UserRepository userRepository;
    private UserService userService;
    private ObjectId testAuthorId;

    @BeforeEach
    void setUp() {
        // Connect to the test database
        mongoClient = MongoClients.create(MONGO_URI);
        postRepository = new PostRepository(mongoClient, TEST_DATABASE);
        postService = new PostService(postRepository);
        userRepository = new UserRepository(mongoClient, TEST_DATABASE);
        userService = new UserService(userRepository);
        
        // Clean up collections before each test
        mongoClient.getDatabase(TEST_DATABASE).getCollection("posts").deleteMany(new Document());
        mongoClient.getDatabase(TEST_DATABASE).getCollection("users").deleteMany(new Document());
        
        // Create a test author
        userService.register("testauthor", "author@example.com", "password123");
        testAuthorId = userRepository.findByUsername("testauthor");
    }

    @AfterEach
    void tearDown() {
        // Clean up after each test
        if (mongoClient != null) {
            mongoClient.getDatabase(TEST_DATABASE).getCollection("posts").deleteMany(new Document());
            mongoClient.getDatabase(TEST_DATABASE).getCollection("users").deleteMany(new Document());
            mongoClient.close();
        }
    }

    @Nested
    @DisplayName("Get Posts Tests")
    class GetPostsTests {

        @Test
        @DisplayName("getPosts - should return all posts")
        void getPosts() {
            // Arrange
            postService.createPost(
                "Test Title",
                "<p>Test content</p>",
                new Date(),
                new Date(),
                testAuthorId,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
            );

            // Act
            List<Post> result = postService.getPosts();

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("Test Title", result.get(0).title());
        }

        @Test
        @DisplayName("getPosts - should return empty list when no posts exist")
        void getPosts_empty() {
            // Act
            List<Post> result = postService.getPosts();

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Query Posts Tests")
    class QueryPostsTests {

        @Test
        @DisplayName("getPostsByTitle - should return matching posts")
        void getPostsByTitle_found() {
            // Arrange
            postService.createPost(
                "Test Title",
                "<p>Test content</p>",
                new Date(),
                new Date(),
                testAuthorId,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
            );

            // Act
            List<Post> result = postService.getPostsByTitle("Test");

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("Test Title", result.get(0).title());
        }

        @Test
        @DisplayName("getPostsByTitle - should throw PostNotFoundException when no matches")
        void getPostsByTitle_notFound() {
            // Act & Assert
            assertThrows(PostNotFoundException.class, () ->
                postService.getPostsByTitle("NonExistent")
            );
        }

        @Test
        @DisplayName("getPostsByTag - should return matching posts")
        void getPostsByTag_found() {
            // Arrange
            List<Tag> tags = List.of(new Tag("java"));
            postService.createPost(
                "Test Title",
                "<p>Test content</p>",
                new Date(),
                new Date(),
                testAuthorId,
                new ArrayList<>(),
                tags,
                new ArrayList<>()
            );

            // Act
            List<Post> result = postService.getPostsByTag("java");

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("getPostsByTag - should throw PostNotFoundException when no matches")
        void getPostsByTag_notFound() {
            // Act & Assert
            assertThrows(PostNotFoundException.class, () ->
                postService.getPostsByTag("nonexistent")
            );
        }

        @Test
        @DisplayName("getUserPosts - should return posts by author")
        void getUserPosts_success() {
            // Arrange
            postService.createPost(
                "Test Title",
                "<p>Test content</p>",
                new Date(),
                new Date(),
                testAuthorId,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
            );

            // Act
            List<Post> result = postService.getUserPosts(testAuthorId);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("getUserPosts - should throw PostNotFoundException when user has no posts")
        void getUserPosts_noPosts() {
            // Arrange
            ObjectId userIdWithNoPosts = new ObjectId();

            // Act & Assert
            assertThrows(PostNotFoundException.class, () ->
                postService.getUserPosts(userIdWithNoPosts)
            );
        }
    }

    @Nested
    @DisplayName("CRUD Tests")
    class CrudTests {

        @Test
        @DisplayName("createPost - should create and return post")
        void createPost() {
            // Act
            Post result = postService.createPost(
                "New Post",
                "<p>Content</p>",
                new Date(),
                new Date(),
                testAuthorId,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
            );

            // Assert
            assertNotNull(result);
            assertEquals("New Post", result.title());
            
            // Verify it was saved
            List<Post> allPosts = postService.getPosts();
            assertEquals(1, allPosts.size());
        }

        @Test
        @DisplayName("updatePost - should update post fields")
        void updatePost() {
            // Arrange
            postService.createPost(
                "Original Title",
                "<p>Original content</p>",
                new Date(),
                new Date(),
                testAuthorId,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
            );
            
            // Get the post ID from the saved post
            List<Post> posts = postService.getPosts();
            ObjectId postId = posts.get(0).postId();

            Post updatedPost = new Post(
                null,
                "Updated Title",
                "<p>Updated content</p>",
                new Date(),
                new Date(),
                testAuthorId,
                new ArrayList<>(),
                0,
                new ArrayList<>(),
                new ArrayList<>(),
                0.0
            );

            // Act
            postService.updatePost(postId, updatedPost);

            // Assert - verify the post was updated
            List<Post> result = postService.getPosts();
            assertEquals("Updated Title", result.get(0).title());
        }

        @Test
        @DisplayName("deletePost - should delete post by ID")
        void deletePost() {
            // Arrange
            postService.createPost(
                "Test Title",
                "<p>Test content</p>",
                new Date(),
                new Date(),
                testAuthorId,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
            );
            
            List<Post> posts = postService.getPosts();
            ObjectId postId = posts.get(0).postId();

            // Act
            postService.deletePost(postId);

            // Assert
            List<Post> result = postService.getPosts();
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Review Tests")
    class ReviewTests {

        @Test
        @DisplayName("addReview - should add review and comment")
        void addReview() {
            // Arrange
            postService.createPost(
                "Test Title",
                "<p>Test content</p>",
                new Date(),
                new Date(),
                testAuthorId,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
            );
            
            List<Post> posts = postService.getPosts();
            ObjectId postId = posts.get(0).postId();
            ObjectId userId = testAuthorId;

            // Act
            postService.addReview(4.5, "Great post!", userId, postId);

            // Assert - verify the review was added
            List<Post> result = postService.getPosts();
            assertEquals(1, result.get(0).reviews().size());
        }

        @Test
        @DisplayName("deleteComment - should delete comment from post")
        void deleteComment() {
            // Arrange
            postService.createPost(
                "Test Title",
                "<p>Test content</p>",
                new Date(),
                new Date(),
                testAuthorId,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
            );
            
            List<Post> posts = postService.getPosts();
            ObjectId postId = posts.get(0).postId();
            
            // Add a comment first
            postService.addReview(4.0, "Nice!", testAuthorId, postId);
            
            // Get the comment ID
            posts = postService.getPosts();
            ObjectId commentId = posts.get(0).comments().get(0).commentId();

            // Act
            postService.deleteComment(postId, commentId);

            // Assert - verify the comment was deleted
            List<Post> result = postService.getPosts();
            assertTrue(result.get(0).comments().isEmpty());
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("createPost - should handle empty tags and comments")
        void createPost_emptyCollections() {
            // Act
            Post result = postService.createPost(
                "Post with no tags",
                "<p>Content</p>",
                new Date(),
                new Date(),
                testAuthorId,
                null,  // null comments
                null,  // null tags
                null   // null reviews
            );

            // Assert
            assertNotNull(result);
        }

        @Test
        @DisplayName("getPostsByTitle - should handle case-insensitive search")
        void getPostsByTitle_caseInsensitive() {
            // Arrange
            postService.createPost(
                "Test Title",
                "<p>Test content</p>",
                new Date(),
                new Date(),
                testAuthorId,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
            );

            // Act
            List<Post> result = postService.getPostsByTitle("test");

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
        }
    }
}
