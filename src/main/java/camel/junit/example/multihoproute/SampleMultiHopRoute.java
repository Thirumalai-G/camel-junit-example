package camel.junit.example.multihoproute;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class SampleMultiHopRoute extends RouteBuilder {

    @Override
    public void configure() {

        from("direct:India").id("SAMPLE_MULTIHOP_ROUTE")
                .log("Welcome to India !!")
                .to("direct:Delhi")
                .to("direct:Mumbai")
                .to("direct:Kolkata")
                .to("direct:Chennai");

        from("direct:Delhi")
                .bean(Delhi.class);

        from("direct:Mumbai")
                .bean(Mumbai.class);

        from("direct:Kolkata")
                .bean(Kolkata.class);

        from("direct:Chennai")
                .bean(Chennai.class);
    }


}
