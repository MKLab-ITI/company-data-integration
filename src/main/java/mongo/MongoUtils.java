package mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 *
 * @author vasgat
 */
public class MongoUtils {

    /**
     * Creates new mongodb client
     *
     * @param username
     * @param password
     * @param db
     * @return
     */
    public static MongoClient newClient(String username, String password, String db) {
        List<ServerAddress> seeds = new ArrayList<ServerAddress>();
        seeds.add(new ServerAddress("localhost"));
        List<MongoCredential> credentials = new ArrayList<MongoCredential>();
        credentials.add(
                MongoCredential.createScramSha1Credential(
                        username,
                        db,
                        password.toCharArray()
                )
        );

        return new MongoClient(seeds, credentials);
    }

    /**
     * Connects to mongoDB and returns a specified collection
     *
     * @param mongoDB name
     * @param Collection collection
     * @return DBCollection object
     */
    public static MongoCollection connect(MongoClient client, String database, String collection) {
        MongoDatabase db = client.getDatabase(database);
        MongoCollection Collection = db.getCollection(collection);
        return Collection;
    }

    /**
     * Inserts a document in mongoDB
     *
     * @param mongoDB name
     * @param Collection name
     * @param document
     */
    public static ObjectId insertDoc(MongoClient mongoClient, String database, String collection, Document document) {
        MongoCollection Collection = connect(mongoClient, database, collection);
        Collection.insertOne(document);
        return (ObjectId) document.get("_id");
    }

    public static ObjectId insertDoc(MongoCollection collection, Document document) {
        collection.insertOne(document);
        return (ObjectId) document.get("_id");
    }

}
