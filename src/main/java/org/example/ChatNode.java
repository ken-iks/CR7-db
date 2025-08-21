package org.example;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.stream.IntStream;

public class ChatNode {
    private static final Logger logger = LoggerFactory.getLogger(ChatNode.class);
    private String response;
    private int numTokens;
    private LocalDateTime timestamp;

    public ChatNode(String response) {
        JSONObject responseObject = new JSONObject(response);
        // get timestamp
        long unixTimestamp = responseObject.getLong("created_at");
        setTimestamp(convertFromLong(unixTimestamp));

        // get tokens
        setNumTokens(responseObject.getJSONObject("usage").getInt("total_tokens"));

        // get output
        JSONArray data = responseObject.getJSONArray("output");
        Optional<JSONObject> dataObject = IntStream.range(0, data.length())
                .mapToObj(data::getJSONObject)
                .peek(obj -> logger.info(obj.toString()))
                .filter(obj -> obj.has("content") && obj.getJSONArray("content").length() == 1)
                .findFirst();

        // now we know we can case the optional to an object and that we ca go straight to the contents
        JSONObject content = dataObject.map(obj -> {
            return obj.getJSONArray("content").getJSONObject(0);
        }).orElseThrow(() -> new RuntimeException("Bug in data!!"));

        // now we can easily use our content!
        setResponse(content.getString("text"));
    }
    // Takes a string UNIX timestamp and converts to
    private LocalDateTime convertFromLong(Long ts) {
        try {
            Instant instant = Instant.ofEpochSecond(ts);
            return LocalDateTime.ofInstant(instant, ZoneId.of("America/Los_Angeles"));
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Could not convert string to date: " + ts);
        }
    }

    public String getResponse() { return this.response; };
    private void setResponse(String response) { this.response = response; };
    public int getNumTokens() { return this.numTokens; };
    private void setNumTokens(int numTokens) { this.numTokens = numTokens; };
    public LocalDateTime getTimestamp() { return this.timestamp; };
    private void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; };

    @Override
    public String toString() {
        return "Response was: '%s', and was sent at %s.\nThis response took %d tokens".formatted(
                getResponse(),
                getTimestamp().toString(),
                getNumTokens()
        );
    }
}

