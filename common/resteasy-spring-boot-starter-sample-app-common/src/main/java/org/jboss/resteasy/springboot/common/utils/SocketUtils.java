/*
 * Copyright 2022-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.resteasy.springboot.common.utils;


import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Random;

import javax.net.ServerSocketFactory;


// Spring has removed SockUtils: https://github.com/onobc/spring-cloud-function/blob/2fb90d5a35f6db2178cdb8be4ad9815cacb7cd84/spring-cloud-function-web/src/test/java/org/springframework/cloud/function/web/TestSocketUtils.java
// So we use a copy here:
/**
 * Simple test utility to find a random available TCP port.
 * <p>Inspired by the now removed {@code org.springframework.util.SocketUtils} and is only used in a testing capacity.
 *
 * @author Chris Bono
 * @deprecated will soon be removed or consolidated - do not use further
 */
@Deprecated
public final class SocketUtils {

    private static final Random random = new Random(System.nanoTime());

    /**
     * Find an available TCP port randomly selected from the range {@code 1024-65535}.
     *
     * @return an available TCP port number
     * @throws IllegalStateException if no available port could be found
     */
    public static int findAvailableTcpPort() {
        int minPort = 1024;
        int maxPort = 65535;
        int portRange = maxPort - minPort;
        int candidatePort;
        int searchCounter = 0;
        do {
            if (searchCounter > portRange) {
                throw new IllegalStateException(String.format(
                        "Could not find an available TCP port after %d attempts", searchCounter));
            }
            candidatePort = minPort + random.nextInt(portRange + 1);
            searchCounter++;
        }
        while (!isPortAvailable(candidatePort));

        return candidatePort;
    }

    private static boolean isPortAvailable(int port) {
        try {
            ServerSocket serverSocket = ServerSocketFactory.getDefault().createServerSocket(
                    port, 1, InetAddress.getByName("localhost"));
            serverSocket.close();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}