package org.jboss.resteasy.springboot;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.net.ssl.SSLContext;
import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;
import org.springframework.core.env.Environment;
import io.netty.handler.ssl.ClientAuth;

public class ReactorNettyServerConfig {
    
    private static final String REACTOR_NETTY_SERVER_PORT_PROPERTY = "server.port";
    private static final int REACTOR_NETTY_SERVER_PORT_HTTP_DEFAULT = 8080;
    private static final int REACTOR_NETTY_SERVER_PORT_HTTPS_DEFAULT = 8443;

    private final Integer port;
    private final Duration idleTimeout;
    private final SSLContext sslContext;
    private final ClientAuth clientAuth;
    private final SecurityDomain securityDomain;
    /**
     * Clean up tasks which are expected to run after servicing every request.  All given tasks will 
     * be run for every request
     */
    private final List<Runnable> cleanupTasks;
    
    public ReactorNettyServerConfig(final int port, final Duration idleTimeout, final SSLContext sslContext,
            final ClientAuth clientAuth, final SecurityDomain securityDomain, final List<Runnable> cleanupTasks) {
        this.port = port;
        this.idleTimeout = idleTimeout;
        this.sslContext = sslContext;
        this.clientAuth = clientAuth;
        this.securityDomain = securityDomain;
        this.cleanupTasks = cleanupTasks;
    }
    
    public Integer getPort() {
        return port;
    }

    public Duration getIdleTimeout() {
        return idleTimeout;
    }
    
    public SSLContext getSslContext() {
        return sslContext;
    }
    
    public ClientAuth getClientAuth() {
        return clientAuth;
    }
    
    public SecurityDomain getSecurityDomain() {
        return securityDomain;
    }

    public List<Runnable> getCleanupTasks() {
        return cleanupTasks;
    }

    public static class Builder {
        
        private Integer port;
        private Duration idleTimeout;
        private SSLContext sslContext;
        private ClientAuth clientAuth = ClientAuth.REQUIRE;
        private SecurityDomain securityDomain;
        private List<Runnable> cleanupTasks;
        
        public Builder() {}
        
        public Builder withPort(final int port) {
            this.port = port;
            return this;
        }
        
        public Builder withIdleTimeout(final Duration idleTimeout) {
            this.idleTimeout = idleTimeout;
            return this;
        }

        public Builder withSSLContext(final SSLContext sslContext) {
            this.sslContext = sslContext;
            return this;
        }
        
        /**
         * Applies the setting to indicate if/how client authentication is performed.
         * @param clientAuth - See {@link io.netty.handler.ssl.ClientAuth}. Null values not allowed.
         * @return - A modified instance of the builder with the new {@code clientAuth} setting.
         */
        public Builder withClientAuth(final ClientAuth clientAuth) {
            Objects.requireNonNull(clientAuth);
            this.clientAuth = clientAuth;
            return this;
        }
        
        public Builder withSecurityDomain(final SecurityDomain securityDomain) {
            this.securityDomain = securityDomain;
            return this;
        }

        /**
         * Tasks to be run after servicing of request.  All tasks will run 
         * after every request.  Given tasks will be run on netty event loop thread.
         * Hence tasks need to be non-blocking and finish quickly.  Long running tasks
         * or blocking runnables will slow down netty server.
         *
         * @param tasks tasks to run after service request
         * @return A modified instance of the builder with the clean up tasks added.
         * @see org.jboss.resteasy.plugins.server.reactor.netty.ReactorNettyJaxrsServer
         */
        public Builder withCleanupTasks(final Runnable... tasks){
            Objects.requireNonNull(tasks);
            this.cleanupTasks = Arrays.asList(tasks);
            return this;
        }
        
        public ReactorNettyServerConfig build(){

            return new ReactorNettyServerConfig(
                    Optional.ofNullable(port)
                    .orElseGet(() -> 
                        Optional.ofNullable(sslContext)
                            .map(c -> REACTOR_NETTY_SERVER_PORT_HTTPS_DEFAULT)
                            .orElseGet(() -> REACTOR_NETTY_SERVER_PORT_HTTP_DEFAULT)), 
                    idleTimeout, 
                    sslContext,
                    clientAuth,
                    securityDomain,
                    cleanupTasks);
        }
        
    }
        
    
    public static ReactorNettyServerConfig defaultConfig(final Environment env) {

        Objects.requireNonNull(env);
        
        return new ReactorNettyServerConfig.Builder().withPort(Integer.parseInt(
                env.getProperty(REACTOR_NETTY_SERVER_PORT_PROPERTY,
                        String.valueOf(REACTOR_NETTY_SERVER_PORT_HTTP_DEFAULT))))
                .build();
     
    }
}
