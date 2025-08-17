package org.example;

import org.json.*;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.sql.SQLException;
import org.example.ChatNode;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import io.github.cdimascio.dotenv.Dotenv;
import org.example.KenTree;
import org.example.Chat;
import org.example.CustomExceptions.NotImplementedException;


public class Main {
    static Dotenv dotenv = Dotenv.load();

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
            ChatNode res = new ChatNode(new JSONObject(response.body()));
            //todo: should do something with the response I assume?
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        // quick request
        //makeRequest("Hi how are you", Optional.empty());

        try (Chat chat = Chat.getExistingChat("57406b91-5450-4419-8191-6f094d50070f")) {
            throw new NotImplementedException(); //todo: next steps?
        } catch (SQLException e) {
            System.err.println("Something went wrong...");
            throw new RuntimeException("SQL bug it seems");
        }
    }

}