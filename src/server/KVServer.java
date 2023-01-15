//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class KVServer {
    public static final int PORT = 8078;
    private final String apiToken = this.generateApiToken();
    private final HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 8078), 0);
    private final Map<String, String> data = new HashMap();

    public KVServer() throws IOException {
        this.server.createContext("/register", this::register);
        this.server.createContext("/save", this::save);
        this.server.createContext("/load", this::load);
    }

    private void load(HttpExchange h) throws IOException {
        try {
            System.out.println("\n/load");
            boolean hasAuth = this.hasAuth(h);
            if (!hasAuth) {
                System.out.println("Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
                h.sendResponseHeaders(403, 0L);
                return;
            }

            if ("GET".equals(h.getRequestMethod())) {
                String key = h.getRequestURI().getPath().substring("/load/".length());
                if (key.isEmpty()) {
                    System.out.println("Key для выгрузки пустой. key указывается в пути: /load/{key}");
                    h.sendResponseHeaders(400, 0L);
                    return;
                }

                String value = (String) this.data.get(key);
                if (value == null) {
                    System.out.println("Value для выгрузки пустой");
                    h.sendResponseHeaders(400, 0L);
                    return;
                }

                byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
                h.sendResponseHeaders(200, (long) bytes.length);
                OutputStream os = h.getResponseBody();

                try {
                    os.write(bytes);
                } catch (Throwable var14) {
                    if (os != null) {
                        try {
                            os.close();
                        } catch (Throwable var13) {
                            var14.addSuppressed(var13);
                        }
                    }

                    throw var14;
                }
                if (os != null) {
                    os.close();
                }
            } else {
                System.out.println("/load ждёт GET-запрос, а получил: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0L);
            }
        } finally {
            h.close();
        }
    }

    private void save(HttpExchange h) throws IOException {
        try {
            System.out.println("\n/save");
            boolean hasAuth = this.hasAuth(h);
            if (!hasAuth) {
                System.out.println("Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
                h.sendResponseHeaders(403, 0L);
                return;
            }

            if (!"POST".equals(h.getRequestMethod())) {
                System.out.println("/save ждёт POST-запрос, а получил: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0L);
                return;
            }

            String key = h.getRequestURI().getPath().substring("/save/".length());
            if (key.isEmpty()) {
                System.out.println("Key для сохранения пустой. key указывается в пути: /save/{key}");
                h.sendResponseHeaders(400, 0L);
                return;
            }

            String value = this.readText(h);
            if (!value.isEmpty()) {
                this.data.put(key, value);
                System.out.println("Значение для ключа " + key + " успешно обновлено!");
                h.sendResponseHeaders(200, 0L);
                return;
            }

            System.out.println("Value для сохранения пустой. value указывается в теле запроса");
            h.sendResponseHeaders(400, 0L);
        } finally {
            h.close();
        }

    }

    private void register(HttpExchange h) throws IOException {
        try {
            System.out.println("\n/register");
            if ("GET".equals(h.getRequestMethod())) {
                this.sendText(h, this.apiToken);
            } else {
                System.out.println("/register ждёт GET-запрос, а получил " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0L);
            }
        } finally {
            h.close();
        }

    }

    public void start() {
        System.out.println("Запускаем сервер на порту 8078");
        System.out.println("Открой в браузере http://localhost:8078/");
        System.out.println("API_TOKEN: " + this.apiToken);
        this.server.start();
    }

    public void stop() {
        System.out.println("Останавливаем сервер");
        this.server.stop(1);
    }

    private String generateApiToken() {
        return "" + System.currentTimeMillis();
    }

    protected boolean hasAuth(HttpExchange h) {
        String rawQuery = h.getRequestURI().getRawQuery();
        return rawQuery != null && (rawQuery.contains("API_TOKEN=" + this.apiToken) || rawQuery.contains("API_TOKEN=DEBUG"));
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, (long) resp.length);
        h.getResponseBody().write(resp);
    }
}
