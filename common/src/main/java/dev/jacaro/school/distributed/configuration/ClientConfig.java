package dev.jacaro.school.distributed.configuration;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Properties;

public record ClientConfig(@NotNull String serverIP, int port, int[] threads, int[] payloadSize) {

    public static ClientConfig build(Properties properties) {
        String serverIP = properties.getProperty("server");
        int port = Integer.parseInt(properties.getProperty("port"));
        int[] threads = buildIntArray(properties.getProperty("threads"));
        int[] payloadSizes = buildIntArray(properties.getProperty("payloadSize"));

        return new ClientConfig(serverIP, port, threads, payloadSizes);
    }

    private static int[] buildIntArray(String input) {
        if (input == null)
            throw new RuntimeException("String must not be null");


        String[] list = input.split(",");


        int[] array = new int[list.length];

        for (int i = 0; i < array.length; i++) {
            array[i] = Integer.parseInt(list[i]);
        }

        return array;
    }
}
