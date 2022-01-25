package camel.junit.example.sftproute;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sftp")
@Getter
@Setter
@Builder
public class SftpInfo {
    private String host;
    private int port;
    private String user;
    private String path;
    private String password;
}
