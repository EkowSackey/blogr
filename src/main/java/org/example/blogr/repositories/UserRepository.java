package org.example.blogr.repositories;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import org.example.blogr.domain.User;
import org.example.blogr.exceptions.UserNotFoundException;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class UserRepository {
    private final MongoCollection<Document> collection;
    private static final String DEFAULT_DATABASE = "lab4";

    /**
     * Constructor that uses the default database name.
     * @param client the MongoDB client
     */
    public UserRepository(MongoClient client){
        this(client, DEFAULT_DATABASE);
    }

    /**
     * Constructor that allows specifying a database name (useful for testing).
     * @param client the MongoDB client
     * @param databaseName the database name to use
     */
    public UserRepository(MongoClient client, String databaseName){
        this.collection = client.getDatabase(databaseName).getCollection("users");
    }

    private Document toDoc(User user){
        var d = new Document();
        d.put("username", user.username());
        d.put("email", user.email());
        d.put("role", user.role());
        d.put("password", user.password());

        return d;
    }

    private User toDomain(Document d){
        return new User(d.getString("username"),
                d.getString("email"),
                d.getString("password"),
                d.getString("role"));
    }

    public void createUser(User user){
        var doc = toDoc(user);
        InsertOneResult result = collection.insertOne(doc);
        System.out.println("Inserted a document with the following id: "
                + Objects.requireNonNull(result.getInsertedId())
                .asObjectId().getValue());
    }

    public List<User> getAllUsers(){
        FindIterable<Document> docs = collection.find();
        List<User> users = new ArrayList<>();
        docs.forEach( doc -> users.add(toDomain(doc)));
        return users;
    }

    public User findById(ObjectId id){

        Bson filter = Filters.eq("_id",id);
        Document doc = collection.find(filter).first();
        if (doc != null)
            return toDomain(doc);
        else return null;
    }

    public ObjectId findByUsername(String username){
        Bson filter = Filters.eq("username", username);
        Document doc = collection.find(filter).first();
        if(doc != null)
            return doc.getObjectId("_id");
        else return null;
    }

    public List<User> searchByUsername(String username){
        Bson filter = Filters.eq("username", Pattern.compile(Pattern.quote(username), Pattern.CASE_INSENSITIVE));
        FindIterable<Document> docs = collection.find(filter);
        List<User> users = new ArrayList<>();
        docs.forEach( doc -> users.add(toDomain(doc)));
        return users;
    }

    public ObjectId findByEmail(String email){
        Bson filter = Filters.eq("email", email);
        Document doc = collection.find(filter).first();
        if (doc != null)
            return doc.getObjectId("_id");
        else return null;
    }

    public void updateUser(ObjectId id, String field, String value){
        Bson filter = Filters.eq("_id", id);
        Bson update = Updates.set(field, value);

        UpdateResult result = collection.updateOne(filter, update);
        System.out.println("Modified fields:" + result.getModifiedCount());
    }

    public void deleteUser(ObjectId id){
        User u = findById(id);

        if (u != null)
            collection.deleteOne(Filters.eq("_id", id));
        else throw new UserNotFoundException("User with ID: " + id + " does not exist");
    }
}
