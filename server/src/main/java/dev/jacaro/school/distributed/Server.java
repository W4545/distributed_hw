package dev.jacaro.school.distributed;

import dev.jacaro.school.distributed.configuration.RunConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.ServerSocket;
import java.util.Properties;

import static java.lang.System.err;
import static java.lang.System.out;

public class Server {

    public static void main(String @NotNull [] args) {
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



        try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(properties.getProperty("port")))) {
            var socket = serverSocket.accept();

            final var printWriter = new PrintWriter(socket.getOutputStream(), true);
            final var reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.printf("Client \"%s\" connected. Waiting for commands%n", socket.getInetAddress().toString());

            var running = true;

            while (running) {
                var clientString = reader.readLine();
                out.printf("CLIENT SAID: %s%n", clientString);

                if (clientString.equals("BYE")) {
                    running = false;
                    continue;
                }

                if (!clientString.equals("PREPARE RUN CONFIGURATION RECEIVE"))
                    throw new RuntimeException(String.format("Unexpected response from client: %s%n", clientString));

                RunConfiguration runConfiguration = RunConfiguration.objectDeSerial(reader.readLine());

                out.println("RUN CONFIGURATION RECEIVED");
                printWriter.println("RUN CONFIGURATION LOADED");

                clientString = reader.readLine();
                out.printf("CLIENT SAID: %s%n", clientString);

                if (!clientString.equals("START EXECUTION"))
                    throw new RuntimeException(String.format("Unexpected response from client: %s%n", clientString));

                var runResult = ParallelExecutor.executeConfiguration(runConfiguration);

                printWriter.println("EXECUTION COMPLETE");
                out.println(runResult);

                printWriter.println(runResult.objectSerial());

                clientString = reader.readLine();
                out.printf("CLIENT SAID: %s%n", clientString);

                if (!clientString.equals("RESULT RECEIVED"))
                    throw new RuntimeException(String.format("Unexpected response from client: %s%n", clientString));

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
