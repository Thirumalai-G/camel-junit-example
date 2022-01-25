package camel.junit.example.multihoproute;

import org.apache.camel.RoutesBuilder;
import org.apache.camel.spi.Registry;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SampleMultiHopRouteTest extends CamelTestSupport {

    @InjectMocks
    private SampleMultiHopRoute sampleMultiHopRoute;

    @Mock
    private Delhi delhi;

    @Mock
    private Mumbai mumbai;

    @Mock
    private Kolkata kolkata;

    @Mock
    private Chennai chennai;

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        Registry registry = context.getRegistry();
        registry.bind("delhi", Delhi.class, delhi);
        registry.bind("mumbai", Mumbai.class, mumbai);
        registry.bind("kolkata", Kolkata.class, kolkata);
        registry.bind("chennai", Chennai.class, chennai);
        return sampleMultiHopRoute;
    }

    @Test
    void testMultiHopRoute() {
        doNothing().when(delhi).handler("Junit");
        doNothing().when(mumbai).handler("Junit");
        doNothing().when(kolkata).handler("Junit");
        doNothing().when(chennai).handler("Junit");
        template.sendBody("direct:India", "Junit");
        verify(delhi).handler("Junit");
        verify(mumbai).handler("Junit");
        verify(kolkata).handler("Junit");
        verify(chennai).handler("Junit");
    }
}