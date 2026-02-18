package org.example.blogr.services;

import org.bson.types.ObjectId;
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

    private final UserRepository userRepository;

    /**
     * Constructor injection for dependency injection.
     * @param userRepository the user repository to use
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void register(String username, String email, String password){
        User userWithUsername = userRepository.findById(userRepository.findByUsername(username));
        if (userWithUsername != null){
            throw new DuplicateUsernameException("User with this username already exists");
        }

        User userWithEmail = userRepository.findById(userRepository.findByEmail(email));
        if (userWithEmail != null){
            throw new DuplicateEmailException("User with this email already exists");
        }

        User user = new User(username, email, PasswordUtil.hash(password), String.valueOf(Role.REGULAR));
        userRepository.createUser(user);
    }

    public ObjectId login(String usernameOrEmail, String password){
        ObjectId userByUsername = userRepository.findByUsername(usernameOrEmail);
        ObjectId userByEmail = userRepository.findByEmail(usernameOrEmail);

        if (userByEmail == null && userByUsername == null){
            throw new UserNotFoundException("User with this username  or email does not exist");
        }

        ObjectId userId = userByUsername != null ? userByUsername : userByEmail;
        User user = userRepository.findById(userId);

        String storedPassword = user.password();
        if(!PasswordUtil.verify(password, storedPassword)){
            throw new InvalidCredentialsException("Invalid credentials. Try again");
        }

        return userId;
    }

    public User getMyProfile(ObjectId id){
        User u = userRepository.findById(id);
        if (u != null){
            return u;
        }

        throw new UserNotFoundException("User with this ID does not exist");
    }

    public List<User> findUsersByUsername(String searchTerm){
        List<User> users = userRepository.searchByUsername(searchTerm);

        if (users != null && !users.isEmpty()){
            return users;
        }

        throw new UserNotFoundException("No users with this username");
    }

    public ObjectId findUserByUsername(String searchTerm){
        ObjectId userId = userRepository.findByUsername(searchTerm);

        if (userId != null){
            return userId;
        }

        throw new UserNotFoundException("No users with this username");
    }

}
