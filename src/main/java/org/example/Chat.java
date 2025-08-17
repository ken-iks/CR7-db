package org.example;

import org.example.CustomExceptions.*;
import java.sql.*;
import java.util.UUID;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class Chat implements AutoCloseable {
    static Logger logger = LoggerFactory.getLogger(Chat.class);
    String userId;
    Connection connection;

    private Chat(String userId, Connection connection) {
        this.userId = userId;
        this.connection = connection;
        logger.info("User created with id: {}", userId);
    }

    public static Chat createNewChat() {
        // create a new sqlite database file
        String uuid = UUID.randomUUID().toString();
        String dbURL = "jdbc:sqlite:%s.db".formatted(uuid);
        try {
            Connection c = DriverManager.getConnection(dbURL);
            logger.info("New driver created!");
            Statement makeTable = c.createStatement();
            makeTable.addBatch(createTableCommand);
            makeTable.executeBatch();
            logger.info("Created table!");
            return new Chat(uuid, c);
        } catch (SQLException e) {
            throw new  RuntimeException(e);
        }
    }

    public static Chat getExistingChat(String userId) {
        logger.info("Connecting to existing chat...");
        String dbURL = "jdbc:sqlite:%s.db".formatted(userId);
        try {
            Connection c = DriverManager.getConnection(dbURL);
            logger.info("Connection established!");
            return new Chat(userId, c);
        } catch (SQLException e) {
            throw new  RuntimeException(e);
        }
    }

    private static String createTableCommand = """
            CREATE TABLE CHAT(
                MESSAGE TEXT NOT NULL,
                ROLE TEXT,
                TOKENS INTEGER,
                TAG1 TEXT,
                TAG2 TEXT,
                TAG3 TEXT,
                TAG4 TEXT,
                TAG5 TEXT
            );
            CREATE INDEX TAGS ON CHAT(TAG1, TAG2, TAG3, TAG4, TAG5);
            """;

    // remember to close the connection!
    @Override
    public void close() throws SQLException {
        if (this.connection != null && !this.connection.isClosed()) {
            this.connection.close();
        }
    }
}
