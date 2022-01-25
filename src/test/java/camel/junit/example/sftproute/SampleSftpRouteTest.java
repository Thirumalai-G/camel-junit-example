package camel.junit.example.sftproute;

import org.apache.camel.RoutesBuilder;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static com.github.stefanbirkner.fakesftpserver.lambda.FakeSftpServer.withSftpServer;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
class SampleSftpRouteTest extends CamelTestSupport {

    private static Path inboundPath;

    private SftpInfo sftpInfo;

    private File inboundFile;

    private int order;

    @BeforeAll
    static void beforeAll() throws IOException {
        inboundPath = Paths.get("src/test/resources/").toAbsolutePath();
        Files.createDirectory(inboundPath);
    }

    @AfterAll
    static void afterAll() throws IOException {
        FileUtils.deleteDirectory(inboundPath.toFile());
    }

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        withSftpServer(server -> {
            server.addUser("tst", "tst");
            sftpInfo = SftpInfo.builder().host("0.0.0.0").port(server.getPort()).path("/").build();
        });
        super.setUp();
    }

    @Override
    @AfterEach
    public void tearDown() throws Exception {
        inboundFile.delete();
        super.tearDown();
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        if (++order == 1) {
            sftpInfo.setUser("wrong");
            sftpInfo.setPassword("wrong");
        } else {
            sftpInfo.setUser("tst");
            sftpInfo.setPassword("tst");
        }
        return new SampleSftpRoute(inboundPath.toString(), sftpInfo);
    }

    @Test
    void testsftpSuccess() throws Exception {
        inboundFile = new File(inboundPath.toString(), "test.txt");
        inboundFile.createNewFile();
        await().atMost(10, TimeUnit.SECONDS);
        withSftpServer(server -> assertTrue(server.existsFile("/test.txt")));
        assertFalse(inboundFile.exists());
    }

    @Test
    void testsftpFailure() throws Exception {
        sftpInfo.setUser("user");
        sftpInfo.setPassword("wrong");
        inboundFile = new File(inboundPath.toString(), "test.txt");
        inboundFile.createNewFile();
        await().atMost(10, TimeUnit.SECONDS);
        withSftpServer(server -> assertFalse(server.existsFile("/test.txt")));
        assertTrue(inboundFile.exists());
    }
}