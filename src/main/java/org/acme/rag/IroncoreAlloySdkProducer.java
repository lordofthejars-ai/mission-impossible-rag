package org.acme.rag;

import com.ironcorelabs.ironcore_alloy_java.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class IroncoreAlloySdkProducer {

    private final float approximationFactor = 7.2f;
    private final byte[] keyByteArray = "hJdwvEeg5mxTu9qWcWrljfKs1ga4MpQ9MzXgLxtlkwX//yA=".getBytes();

    @Produces
    Standalone createStandaloneSdk() throws AlloyException {

        // Vectors

        StandaloneSecret standaloneSecret =
                new StandaloneSecret(1, new Secret(keyByteArray));

        VectorSecret vectorSecret =
                VectorSecret.newWithScalingFactor(approximationFactor, new RotatableSecret(standaloneSecret, null));

        Map<SecretPath, VectorSecret> vectorSecrets = Collections.singletonMap(new SecretPath("documents"), vectorSecret);

        // Standard

        StandardSecrets standardSecrets = new StandardSecrets(null, new ArrayList<>());

        // Deterministic

        Map<SecretPath, RotatableSecret> deterministicSecrets = new HashMap<>();

        StandaloneConfiguration config =
                new StandaloneConfiguration(standardSecrets, deterministicSecrets, vectorSecrets);

        return new Standalone(config);
    }

}
