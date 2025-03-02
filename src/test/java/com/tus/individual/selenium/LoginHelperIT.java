package com.tus.individual.selenium;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;

public class LoginHelperIT {

    public static String loginAndGetToken() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String loginPayload = "{ \"email\": \"admin@networksys.com\", \"password\": \"Admin123!\" }";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8081/api/auth/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(loginPayload))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("🔍 Response Code: " + response.statusCode());
            System.out.println("🔍 Response Body: " + response.body());

            if (response.statusCode() == 200) {
                JSONObject jsonResponse = new JSONObject(response.body());
                return jsonResponse.getString("token");
            } else {
                System.out.println("❌ Login API failed! Response: " + response.body());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
