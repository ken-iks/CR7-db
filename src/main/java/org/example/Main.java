package org.example;

import org.json.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import io.github.cdimascio.dotenv.Dotenv;


public class Main {
    static Dotenv dotenv = Dotenv.load();

    private static class ChatResponse {
        String response;
        int numTokens;
        LocalDateTime timestamp;

        //TODO: implement parsing for tokens and timestamp (in diff sections of JSON body)
        public ChatResponse(JSONObject responseObject) {
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

    //TODO: implement
    private static JSONObject toJsonBody(List<ChatResponse> responses) {
        throw new RuntimeException("Not implemented");
    }

    static HttpClient client = HttpClient.newBuilder().build();
    static HttpRequest.Builder requestBase = HttpRequest.newBuilder()
            .uri(URI.create(dotenv.get("BASE_URL")))
            .header("Content-Type", "application/json")
            .header("Authorization", """
                    Bearer %s""".formatted(dotenv.get("API_KEY")));

    // make JSON request and get return body
    // the extra context if needed will be given as a second arg
    private static void makeRequest(String s, Optional<JSONObject> context) {
        JSONObject obj = context.orElse(new JSONObject());
        obj.put("model", "gpt-4.1");
        obj.put("input", s);

        HttpRequest fullRequest = requestBase.POST(HttpRequest.BodyPublishers.ofString(obj.toString())).build();
        try {
            HttpResponse<String> response = client.send(fullRequest, HttpResponse.BodyHandlers.ofString());
            ChatResponse res = new ChatResponse(new JSONObject(response.body()));

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        // quick request
        makeRequest("Hi how are you", Optional.empty());
    }

}