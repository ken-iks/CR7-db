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
    private static JSONObject toJsonBody(List<ChatNode> responses) {
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
    private static ChatNode makeRequest(String s, Optional<JSONObject> context) {
        JSONObject obj = context.orElse(new JSONObject());
        obj.put("model", "gpt-4.1");
        obj.put("input", s);

        HttpRequest fullRequest = requestBase.POST(HttpRequest.BodyPublishers.ofString(obj.toString())).build();
        try {
            HttpResponse<String> response = client.send(fullRequest, HttpResponse.BodyHandlers.ofString());
            ChatNode res = new ChatNode(response.body());
            return res;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    //todo: next steps are to turn user inputs into chatnodes. and then need a function (probably in chat class) that turns list of chatnodes (both gpt responses and user questions) to JSONobject context so i can maintain a chat
    //todo: also need to then get persistance for context with tags
    //todo: once done, i can figure out an algorithm to select most appropriate context and construct JSONObject context from DB object
    //todo: workflow is: prompt -> get tags -> pull appropriate context from db -> make request
    //todo: db will then add both prompt and response - both tagged appropriately
    public static void main(String[] args) {
        // quick request
        //makeRequest("Hi how are you", Optional.empty());

        try (Chat chat = Chat.getExistingChat("57406b91-5450-4419-8191-6f094d50070f")) {
            System.out.println(makeRequest("Just testing something quickly", Optional.empty()));
            //throw new NotImplementedException(); //todo: next steps?
        } catch (SQLException e) {
            System.err.println("Something went wrong...");
            throw new RuntimeException("SQL bug it seems");
        }
    }
}