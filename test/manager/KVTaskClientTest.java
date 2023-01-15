//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package manager;

import org.junit.jupiter.api.*;
import server.KVServer;
import server.KVTaskClient;

import java.io.IOException;
import java.time.Instant;

class KVTaskClientTest {
    private KVTaskClient taskClient;
    private static KVServer kvServer;

    KVTaskClientTest() {
    }

    @BeforeAll
    static void initBeforeAll() {
        try {
            kvServer = new KVServer();
            kvServer.start();
        } catch (IOException var1) {
            kvServer = null;
        }

    }

    @AfterAll
    static void releaseAfterAll() {
        if (kvServer != null) {
            kvServer.stop();
        }

    }

    @BeforeEach
    void initBeforeEach() throws IOException, InterruptedException {
        this.taskClient = new KVTaskClient("http://localhost:8078");
    }

    @Test
    void testPutAndLoad() throws IOException, InterruptedException {
        try {
            String var10000 = this.getClass().getSimpleName();
            String key = var10000 + Instant.now().getEpochSecond();
            Assertions.assertThrows(IOException.class, () -> {
                this.taskClient.load((String) null);
            });
            Assertions.assertThrows(IOException.class, () -> {
                this.taskClient.load(key);
            });
            this.taskClient.put(key, key);
            Assertions.assertEquals(key, this.taskClient.load(key));
            this.taskClient.put(key, key + key);
            Assertions.assertEquals(key + key, this.taskClient.load(key));
        } catch (Throwable var2) {
            throw var2;
        }
    }
}
