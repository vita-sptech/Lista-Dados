package integracaoSlack;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;

public class Slack {
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final String URL = "https://hooks.slack.com/services/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";

    public static void sendMessage(JSONObject objeto) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(
                URI.create(URL))
                .header("accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objeto.toString())
        ).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

       System.out.printf("""
               Status: %s
               Response: %s
               %n""", response.statusCode(), response.body());
    }
}
