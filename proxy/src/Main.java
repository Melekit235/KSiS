import java.io.*;
import java.net.*;

public class Main {
    private static final int connectionTimeout = 5000;

    public static void main(String[] args) {
        int port = 8080;
        //String targetUrl = "https://www.google.com/webhp?hl=be&sa=X&ved=0ahUKEwjKhvbEr6r-AhVNl4sKHQcwCcoQPAgI";

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Прокси-сервер слушает на порту: " + port);


            int i = 0;
            while (i < 100) {

                Socket clientSocket = serverSocket.accept();
                System.out.println("Подключение от клиента: " + clientSocket.getInetAddress().getHostAddress());
                InputStream clientInput = clientSocket.getInputStream();
                OutputStream clientOutput = clientSocket.getOutputStream();


                BufferedReader clientReader = new BufferedReader(new InputStreamReader(clientInput));
                String requestUrl = clientReader.readLine();
                System.out.println("Запрос от клиента: " + requestUrl);

                /*String[] parts = requestUrl.split(" ");

                String method = parts[0];
                String hostAndPort = parts[1];
                String httpVersion = parts[2];

                String[] hostAndPortParts = hostAndPort.split(":");
                String host = hostAndPortParts[0];
                int portt = Integer.parseInt(hostAndPortParts[1]);

                System.out.println("Method: " + method);
                System.out.println("Host: " + host);
                System.out.println("Port: " + portt);
                System.out.println("HTTP Version: " + httpVersion);*/

                URL url = new URL(requestUrl);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setConnectTimeout(connectionTimeout);
                connection.setReadTimeout(connectionTimeout);

                InputStream serverInput = connection.getInputStream();
                BufferedReader serverReader = new BufferedReader(new InputStreamReader(serverInput));

                String inputLine;
                while ((inputLine = serverReader.readLine()) != null) {
                    System.out.println(inputLine);
                    clientOutput.write(inputLine.getBytes());
                }

                serverReader.close();
                serverInput.close();
                clientOutput.close();
                clientInput.close();
                clientSocket.close();
                i++;

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}