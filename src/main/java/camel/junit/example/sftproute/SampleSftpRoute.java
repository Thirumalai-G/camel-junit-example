package camel.junit.example.sftproute;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class SampleSftpRoute extends RouteBuilder {

    private String inboundPath;
    private SftpInfo sftpInfo;

    public SampleSftpRoute(String inboundPath, SftpInfo sftpInfo) {
        this.inboundPath = inboundPath;
        this.sftpInfo = sftpInfo;
    }


    @Override
    public void configure() {

        onException(Exception.class)
                .maximumRedeliveries(2)
                .redeliveryDelay(20)
                .log("SFTP Transfer Failure for File: ${header.CamelFileName}")
                .handled(true);


        String fileInbound = UriComponentsBuilder.newInstance().scheme("file")
                .path(inboundPath)
                .build().toString();

        String outboundLocation = UriComponentsBuilder.newInstance()
                .scheme("sftp").host(sftpInfo.getHost()).port(sftpInfo.getPort()).userInfo(sftpInfo.getUser())
                .path(sftpInfo.getPath()).queryParam("preferredAuthentications", "password")
                .queryParam("password", sftpInfo.getPassword())
                .queryParam("jschLoggingLevel", "WARN")
                .build().toString();

        from(fileInbound).id("SAMPLE_SFTP_ROUTE")
                .log("Starting SFTP Transfer for File: ${header.CamelFileName}")
                .to(outboundLocation)
                .log("SFTP Transfer Success for File: ${header.CamelFileName}");
    }
}
