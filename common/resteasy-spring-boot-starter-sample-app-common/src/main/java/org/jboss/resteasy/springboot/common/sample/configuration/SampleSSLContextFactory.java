package org.jboss.resteasy.springboot.common.sample.configuration;

import java.io.InputStream;
import java.security.KeyStore;
import javax.net.ssl.SSLContext;
import org.apache.http.ssl.SSLContextBuilder;

public class SampleSSLContextFactory {

    /**
     * Loads and initialize an SSLContext
     * @param keystoreFileName - A file name for the keystore. 
     * @param keystoreSecret - Secret to use for reading the keystore file.
     * @return - The SSLContext created.
     */
    public static SSLContext sslContext(final String keystoreFileName, final String keystoreSecret) {

        final char[] password = keystoreSecret.toCharArray();
        
        try {
            final KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            final InputStream stream = ClassLoader.getSystemResourceAsStream(keystoreFileName);
            
            keyStore.load(stream, password);

            return SSLContextBuilder.create()
                    .loadKeyMaterial(keyStore, password)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    
}
