package org.example.blogr.services;

import com.mongodb.client.MongoClient;
import org.bson.types.ObjectId;
import org.example.blogr.Config.MongoConfig;
import org.example.blogr.Utils.PasswordUtil;
import org.example.blogr.domain.Role;
import org.example.blogr.domain.User;
import org.example.blogr.exceptions.DuplicateEmailException;
import org.example.blogr.exceptions.DuplicateUsernameException;
import org.example.blogr.exceptions.InvalidCredentialsException;
import org.example.blogr.exceptions.UserNotFoundException;
import org.example.blogr.repositories.UserRepository;

import java.util.List;



public class UserService {

    private final UserRepository urepo;

    /**
     * Default constructor that uses the production MongoDB configuration.
     * Used by the application in production.
     */
    public UserService() {
        MongoClient client = MongoConfig.getClient();
        this.urepo = new UserRepository(client);
    }

    /**
     * Constructor that allows specifying a database name (useful for testing).
     * @param databaseName the database name to use
     */
    public UserService(String databaseName) {
        MongoClient client = MongoConfig.getClient();
        this.urepo = new UserRepository(client, databaseName);
    }

    /**
     * Constructor injection for dependency injection (useful for testing with mocks).
     * @param urepo the user repository to use
     */
    public UserService(UserRepository urepo) {
        this.urepo = urepo;
    }


    public void register(String username, String email, String password){
        User userWithUsername = urepo.findById(urepo.findByUsername(username));
        if (userWithUsername != null){
            throw new DuplicateUsernameException("User with this username already exists");
        }

        User userWithEmail = urepo.findById(urepo.findByEmail(email));
        if (userWithEmail != null){
            throw new DuplicateEmailException("User with this email already exists");
        }

        User user = new User(username, email, PasswordUtil.hash(password), String.valueOf(Role.REGULAR));
        urepo.createUser(user);
    }

    public ObjectId login(String usernameOrEmail, String password){
        ObjectId userByUsername = urepo.findByUsername(usernameOrEmail);
        ObjectId userByEmail = urepo.findByEmail(usernameOrEmail);

        if (userByEmail == null & userByUsername == null){
            throw new UserNotFoundException("User with this username  or email does not exist");
        }

        ObjectId userId = userByUsername != null ? userByUsername : userByEmail;
        User user = urepo.findById(userId);

        String storedPassword = user.password();
        if(!PasswordUtil.verify(password, storedPassword)){
            throw new InvalidCredentialsException("Invalid credentials. Try again");
        }

        return userId;
    }

    public User getMyProfile(ObjectId id){
        User u = urepo.findById(id);
        if (u != null){
            return u;
        }

        throw new UserNotFoundException("User with this ID does not exist");
    }

    public List<User> findUsersByUsername(String searchTerm){
        List<User> users = urepo.searchByUsername(searchTerm);

        if (users != null && !users.isEmpty()){
            return users;
        }

        throw new UserNotFoundException("No users with this username");
    }

    public ObjectId findUserByUsername(String searchTerm){
        ObjectId userId = urepo.findByUsername(searchTerm);

        if (userId != null){
            return userId;
        }

        throw new UserNotFoundException("No users with this username");
    }

}
