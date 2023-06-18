package petition;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;

import java.util.Random;

@Api(name = "petitions",
        version = "v1",
        audiences = "341007588468-66vattbt33n2ib1iq67elrgn6nke8ghc.apps.googleusercontent.com",
        clientIds = "341007588468-66vattbt33n2ib1iq67elrgn6nke8ghc.apps.googleusercontent.com",
        namespace =
        @ApiNamespace(
                ownerDomain = "helloworld.example.com",
                ownerName = "helloworld.example.com")
)
public class PetitionEndpoint {
    Random r = new Random();

    // remember: return Primitives and enums are not allowed.
    @ApiMethod(name = "getRandom", httpMethod = HttpMethod.GET)
    public RandomResult random() {
        return new RandomResult(r.nextInt(6) + 1);
    }
}
