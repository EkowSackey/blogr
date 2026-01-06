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


public class UserService {

    private final MongoClient client = MongoConfig.getClient();
    private final UserRepository urepo = new UserRepository(client);

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
        User userByUsername = urepo.findById(urepo.findByUsername(usernameOrEmail));
        User userByEmail = urepo.findById(urepo.findByEmail(usernameOrEmail));

        if (userByEmail == null & userByUsername == null){
            throw new UserNotFoundException("User with this username  or email does not exist");
        }
        User user;
        if ( userByUsername != null){
            user = userByUsername;
        }
        else
            user = userByEmail;

        String storedPassword = user.password();
        if(!PasswordUtil.verify(password, storedPassword)){
            throw new InvalidCredentialsException("Invalid credentials. Try again");
        }

        else
           return urepo.findByUsername(user.username());
    }
}
