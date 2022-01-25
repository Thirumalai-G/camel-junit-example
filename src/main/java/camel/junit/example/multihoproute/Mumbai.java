package camel.junit.example.multihoproute;

import org.apache.camel.Handler;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

@Component
public class Mumbai {

    @Handler
    public void handler(@RequestBody String name) {
        System.out.println("Hello Mr " + name + "!!! Welcome to Mumbai !!");
    }
}
