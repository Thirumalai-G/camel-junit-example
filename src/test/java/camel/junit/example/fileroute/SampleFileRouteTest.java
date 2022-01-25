package camel.junit.example.fileroute;

import org.apache.camel.EndpointInject;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@ExtendWith(MockitoExtension.class)
class SampleFileRouteTest extends CamelTestSupport {

    private static Path inboundPath;

    @EndpointInject(value = "mock:listenForComplete")
    private MockEndpoint listenForComplete;

    private File inboundFile;

    @BeforeAll
    static void beforeAll() throws IOException {
        inboundPath = Paths.get("src/test/resources")
                .toAbsolutePath();
        Files.createDirectory(inboundPath);
    }

    @AfterAll
    static void afterAll() throws IOException {
        FileUtils.deleteDirectory(inboundPath.toFile());
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new SampleFileRoute("src/test/resources");
    }

    @Override
    protected void doPostSetup() throws Exception {
        AdviceWith.adviceWith(context, "SAMPLE_FILE_ROUTE",
                a -> a.onCompletion().onCompleteOnly().to("mock:listenForComplete"));
    }

    @AfterEach
    void after() throws Exception {
        Files.deleteIfExists(Paths.get(inboundFile.getAbsolutePath()));
    }

    @Test
    void testForSuccessValidFile() throws IOException, InterruptedException {
        inboundFile = new File(inboundPath.toString(), "test.csv");
        inboundFile.createNewFile();
        listenForComplete.expectedHeaderReceived("status", "SUCCESS");
        listenForComplete.assertIsSatisfied();
    }

    @Test
    void testForFailValidFile() throws IOException, InterruptedException {
        inboundFile = new File(inboundPath.toString(), "test.xlsx");
        inboundFile.createNewFile();
        listenForComplete.expectedHeaderReceived("status", "FAILURE");
        listenForComplete.assertIsSatisfied();
    }

    @Test
    void testForInvalidFile() throws IOException, InterruptedException {
        inboundFile = new File(inboundPath.toString(), "test.txt");
        inboundFile.createNewFile();
        listenForComplete.expectedHeaderReceived("status","SUCCESS");
        listenForComplete.assertIsNotSatisfied();
    }
}