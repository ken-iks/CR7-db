package org.example;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

public class ChatNode {
    private static final Logger logger = LoggerFactory.getLogger(ChatNode.class);
    String response;
    int numTokens;
    LocalDateTime timestamp;

    //TODO: implement parsing for tokens and timestamp (in diff sections of JSON body)
    public ChatNode(JSONObject responseObject) {
        // output is a json array
        JSONArray data = responseObject.getJSONArray("output");
        Optional<Object> dataObject = data.toList().stream().filter(
                x -> {
                    JSONObject obj = (JSONObject) x;
                    return obj.has("content") && obj.getJSONArray("content").toList().size()==1;
                }
        ).findFirst();

        // now we know we can case the optional to an object and that we ca go straight to the contents
        JSONObject content = dataObject.map(x -> {
            JSONObject obj = (JSONObject) x;
            return obj.getJSONArray("content").getJSONObject(0);
        }).orElseThrow(() -> new RuntimeException("Bug in data!!"));

        // now we can easily use our content!
        setResponse(content.getString("text"));
        throw new RuntimeException("Not implemented yet");

    }
    // Takes a string UNIX timestamp and converts to
    private LocalDateTime convertFromString(String s) {
        try {
            long ts = Long.parseLong(s);
            Instant instant = Instant.ofEpochSecond(ts);
            return LocalDateTime.ofInstant(instant, ZoneId.of("America/Los_Angeles"));
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Could not convert string to date: " + s);
        }
    }

    public String getResponse() { return this.response; };
    private void setResponse(String response) { this.response = response; };
    public int getNumTokens() { return this.numTokens; };
    private void setNumTokens(int numTokens) { this.numTokens = numTokens; };
    public LocalDateTime getTimestamp() { return this.timestamp; };
    private void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; };

    //TODO: finish impl
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        throw new RuntimeException("Not implemented");
    }
}

