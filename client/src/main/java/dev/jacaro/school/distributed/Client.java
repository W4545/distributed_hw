package dev.jacaro.school.distributed;

import dev.jacaro.school.distributed.configuration.ClientConfig;
import dev.jacaro.school.distributed.configuration.RunConfiguration;
import dev.jacaro.school.distributed.configuration.RunResult;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Properties;

import static java.lang.System.out;
import static java.lang.System.err;

public class Client {

    public static RunConfiguration[] generateRunConfigurations(ClientConfig config) {
        int[] maxStartArray = buildMaxArray(config);

        RunConfiguration[] runConfigurations =
                new RunConfiguration[config.threads().length * config.payloadSize().length];

        for (int i = 0; i < config.threads().length; i++) {
            for (int j = 0; j < config.payloadSize().length; j++) {
                final var runConfiguration = new RunConfiguration(
                        config.threads()[i],
                        config.payloadSize()[j],
                        Arrays.copyOfRange(maxStartArray,
                        0,
                        config.payloadSize()[j]));

                runConfigurations[i * config.payloadSize().length + j] = runConfiguration;
            }
        }

        return runConfigurations;
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            err.println("Please provide a path to config file.");
            System.exit(1);
        }
        out.println(args[0]);

        var properties = new Properties();


        try {
            properties.load(new FileInputStream(args[0]));
        } catch (IOException e) {
            err.printf("Failed to find the file at the provided location: %s%n", args[0]);
            System.exit(2);
        }

        var config = ClientConfig.build(properties);

        var runConfigurations = generateRunConfigurations(config);

        out.println(Arrays.toString(runConfigurations));

        var runResults = new RunResult[runConfigurations.length];

        try (Socket socket = new Socket(config.serverIP(), config.port())) {
            final var printWriter = new PrintWriter(socket.getOutputStream(), true);
            final var reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            for (int i = 0; i < runConfigurations.length; i++) {
                RunConfiguration runConfiguration = runConfigurations[i];
                out.println("Sending run configuration to server");
                printWriter.println("PREPARE RUN CONFIGURATION RECEIVE");
                printWriter.flush();

                printWriter.println(runConfiguration.objectSerial());
                out.println("Run configuration written, awaiting confirmation");

                var serverReturn = reader.readLine();

                out.printf("Server response: %s%n", serverReturn);

                if (!serverReturn.equals("RUN CONFIGURATION LOADED"))
                    throw new RuntimeException(String.format("Unexpected response from server: %s%n", serverReturn));

                printWriter.println("START EXECUTION");
                out.println("STARTING EXECUTION");

                serverReturn = reader.readLine();

                if (!serverReturn.equals("EXECUTION COMPLETE"))
                    throw new RuntimeException(String.format("Unexpected response from server: %s%n", serverReturn));

                out.println("EXECUTION COMPLETE, AWAITING RESULTS");
                var runResult = RunResult.objectDeSerial(reader.readLine());

                printWriter.println("RESULT RECEIVED");
                out.println("RESULTS RECEIVED");

                out.printf("Run Configuration: %d threads, %d arraySize%n",
                        runConfiguration.threadCount(), runConfiguration.arraySize());
                if (runConfiguration.arraySize() == 100) {
                    out.printf("Start array: %s%n", Arrays.toString(runConfiguration.startArray()));

                    out.printf("Sorted array: %s%n", Arrays.toString(runResult.resultArray()));
                }

                out.printf("ExecutionTime: %dns%n", runResult.executionTime());
                out.println("-------------------------------------------------");
                runResults[i] = runResult;
            }

            printWriter.println("BYE");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (var runResult : runResults) {
            for (int i = 0; i < runResult.resultArray().length - 1; i++) {
                if (runResult.resultArray()[i] > runResult.resultArray()[i + 1]) {
                    err.printf("Result out of order: %s%n", runResult);
                }
            }
        }
    }

    private static int[] buildMaxArray(ClientConfig clientConfig) {
        int maxPayloadSize = -1;
        for (var payloadSize : clientConfig.payloadSize()) {
            if (payloadSize > maxPayloadSize)
                maxPayloadSize = payloadSize;
        }

        var array = new int[maxPayloadSize];

        for (int i = 0; i < maxPayloadSize; i++) {
            array[i] = (int) (Math.random() * 50000 + 1);
        }

        return array;
    }
}
