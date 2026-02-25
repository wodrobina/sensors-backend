package eu.wodrobina.rodos.sensorreading;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class TestUtils {

    private TestUtils() {
    }

    public static String generateTestPublicKeyBase64() {
        KeyPairGenerator generator = null;
        try {
            generator = KeyPairGenerator.getInstance("Ed25519");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Something were wrong with key pair generation.");
        }
        KeyPair pair = generator.generateKeyPair();
        String publicKeyBase64 = Base64.getEncoder()
                .encodeToString(pair.getPublic().getEncoded());
        return publicKeyBase64;
    }
}
