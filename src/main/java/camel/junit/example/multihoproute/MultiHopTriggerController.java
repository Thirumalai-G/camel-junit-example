package camel.junit.example.multihoproute;

import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MultiHopTriggerController {

    @Autowired
    private ProducerTemplate producerTemplate;

    @GetMapping("/triggerMultiHopRoute/{name}")
    public void triggerMultiHopRoute(@PathVariable String name) {
        producerTemplate.sendBody("direct:India", name);
    }

}
