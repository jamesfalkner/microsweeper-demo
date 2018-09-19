package com.example.microsweeper.service;

import com.example.microsweeper.model.Score;
import com.microsoft.azure.documentdb.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.transaction.UserTransaction;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.example.microsweeper.service.EnvironmentType.DEVELOPMENT;
import static com.example.microsweeper.service.EnvironmentType.PRODUCTION;

@ApplicationScoped
@EnvironmentAlternative(PRODUCTION)
public class ScoreboardServiceCosmos implements ScoreboardService {


    private DocumentClient documentClient;

    private Logger LOG = Logger.getLogger(ScoreboardServiceCosmos.class.getName());

    @PostConstruct
    public void connect() {
        String uri = System.getenv("COSMOSDB_URI");
        String key = System.getenv("COSMOSDB_KEY");

        documentClient = new DocumentClient(uri, key,
                ConnectionPolicy.GetDefault(), ConsistencyLevel.Session);
    }

    public DocumentClient getDocumentClient() {
        return documentClient;
    }

    @Override
    public List<Score> getScoreboard() {
        List<Score> scores = new ArrayList<>();

        // Retrieve the Scores documents
        List<Document> documentList = documentClient
                .queryDocuments(getScoresCollection().getSelfLink(),
                        "SELECT * FROM root r WHERE r.entityType = 'scoreItem'",
                        null).getQueryIterable().toList();

        // De-serialize the documents in to TodoItems.
        for (Document todoItemDocument : documentList) {
            scores.add(Score.fromJSON(todoItemDocument.toString()));
        }

        LOG.info("Fetched scores from AzureDB: " + scores);
        return scores;

    }

    @Override
    @Transactional
    public void addScore(Score score) throws Exception {
        createScoreItem(score);
        LOG.info("Stored score in AzureDB: " + score);
    }

    @Override
    public void clearScores() throws Exception {
        documentClient.deleteCollection(getScoresCollection().getSelfLink(), null);
        collectionCache = null;
        LOG.info("Cleared scores in AzureDB");
    }


    // Cache for the database object, so we don't have to query for it to
    // retrieve self links.
    private static Database databaseCache;

    // Cache for the collection object, so we don't have to query for it to
    // retrieve self links.
    private static DocumentCollection collectionCache;

    // The name of our database.
    private static final String DATABASE_ID = "ScoresDB";

    // The name of our collection.
    private static final String COLLECTION_ID = "ScoresCollection";

    private Score createScoreItem(Score score) {
        // Serialize the TodoItem as a JSON Document.
        Document scoreItemDocument = new Document(score.toJSON());

        // Annotate the document as a ScoreItem for retrieval (so that we can
        // store multiple entity types in the collection).
        scoreItemDocument.set("entityType", "scoreItem");

        try {
            // Persist the document using the DocumentClient.
            scoreItemDocument = documentClient.createDocument(
                    getScoresCollection().getSelfLink(), scoreItemDocument, null,
                    false).getResource();
        } catch (DocumentClientException e) {
            e.printStackTrace();
            return null;
        }

        return Score.fromJSON(scoreItemDocument.toString());
    }

    private Database getScoreDatabase() {
        if (databaseCache == null) {
            // Get the database if it exists
            List<Database> databaseList = documentClient
                    .queryDatabases(
                            "SELECT * FROM root r WHERE r.id='" + DATABASE_ID
                                    + "'", null).getQueryIterable().toList();

            if (databaseList.size() > 0) {
                // Cache the database object so we won't have to query for it
                // later to retrieve the selfLink.
                databaseCache = databaseList.get(0);
            } else {
                // Create the database if it doesn't exist.
                try {
                    Database databaseDefinition = new Database();
                    databaseDefinition.setId(DATABASE_ID);

                    databaseCache = documentClient.createDatabase(
                            databaseDefinition, null).getResource();
                } catch (DocumentClientException e) {
                    // Something has gone terribly wrong - the app wasn't
                    // able to query or create the collection.
                    // Verify your connection, endpoint, and key.
                    e.printStackTrace();
                }
            }
        }

        return databaseCache;
    }

    private DocumentCollection getScoresCollection() {
        if (collectionCache == null) {
            // Get the collection if it exists.
            List<DocumentCollection> collectionList = documentClient
                    .queryCollections(
                            getScoreDatabase().getSelfLink(),
                            "SELECT * FROM root r WHERE r.id='" + COLLECTION_ID
                                    + "'", null).getQueryIterable().toList();

            if (collectionList.size() > 0) {
                // Cache the collection object so we won't have to query for it
                // later to retrieve the selfLink.
                collectionCache = collectionList.get(0);
            } else {
                // Create the collection if it doesn't exist.
                try {
                    DocumentCollection collectionDefinition = new DocumentCollection();
                    collectionDefinition.setId(COLLECTION_ID);

                    collectionCache = documentClient.createCollection(
                            getScoreDatabase().getSelfLink(),
                            collectionDefinition, null).getResource();
                } catch (DocumentClientException e) {
                    // Something has gone terribly wrong - the app wasn't
                    // able to query or create the collection.
                    // Verify your connection, endpoint, and key.
                    e.printStackTrace();
                }
            }
        }

        return collectionCache;
    }
}