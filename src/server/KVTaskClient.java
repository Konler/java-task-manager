//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package server;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

public class KVTaskClient {
    private final String uriBase;
    private final String apiToken;
    private final HttpClient httpClient;

    public KVTaskClient(String uriBase) throws IOException, InterruptedException {
        this.uriBase = uriBase;
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5L)).build();
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(uriBase + "/register")).version(Version.HTTP_1_1).build();
        HttpResponse<String> response = this.httpClient.send(request, BodyHandlers.ofString());
        this.apiToken = (String) response.body();
    }

    public void put(String key, String json) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().POST(BodyPublishers.ofString(json)).uri(URI.create(this.uriBase + "/save/" + key + "?API_TOKEN=" + this.apiToken)).version(Version.HTTP_1_1).build();
        HttpResponse<String> response = this.httpClient.send(request, BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("Сервер вернул код результата " + response.statusCode());
        }
    }

    public String load(String key) throws IOException, InterruptedException {
        String result = null;
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(this.uriBase + "/load/" + key + "?API_TOKEN=" + this.apiToken)).version(Version.HTTP_1_1).build();
        HttpResponse<String> response = this.httpClient.send(request, BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            result = (String) response.body();
            return result;
        } else {
            throw new IOException("Сервер вернул код результата " + response.statusCode());
        }
    }
}
