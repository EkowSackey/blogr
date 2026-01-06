package org.example.blogr.Utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    private static final int COST = 12;

    public static String hash(String password){
        if (password == null)
            throw new IllegalArgumentException("Password is null");
        String salt = BCrypt.gensalt(COST);
        return BCrypt.hashpw(password, salt);
    }

    public static boolean verify(String password, String storedHash){
        if (password == null)
            return false;

        try{
            return BCrypt.checkpw(password, storedHash);
        }catch (IllegalArgumentException e){
            return false;
        }

    }

}
