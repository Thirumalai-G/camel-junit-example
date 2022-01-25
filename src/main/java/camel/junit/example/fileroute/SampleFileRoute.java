package camel.junit.example.fileroute;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.file.Paths;

@Component
public class SampleFileRoute extends RouteBuilder {

    private String inboundPath;

    public SampleFileRoute(@Value("${file.inbound.path}") String inboundPath) {
        this.inboundPath = inboundPath;
    }

    @Override
    public void configure() {

        String fileInbound = UriComponentsBuilder.newInstance().scheme("file")
                .path(Paths.get(inboundPath).toAbsolutePath().toString())
                .queryParam("include", "^.*\\.(xlsx|csv)$")
                .build().toString();

        from(fileInbound).id("SAMPLE_FILE_ROUTE")
                .log("Received file: ${header.CamelFileName}")
                .choice()
                .when((exchange) -> exchange.getIn().getHeader("CamelFileName").toString()
                        .endsWith("csv")).setHeader("status", simple("SUCCESS")).log("Hurray !! Its a CSV !!")
                .otherwise().setHeader("status", simple("FAILURE")).log("Oh No!! Its an XLSX..")
                .end();
    }
}
