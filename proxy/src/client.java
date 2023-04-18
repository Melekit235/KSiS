import java.io.*;
import java.net.*;

public class client {
    public static void main(String[] args) {
        String proxyHost = "127.0.0.1";
        int proxyPort = 8080;

        try {
            int i = 0;
            while (i<100) {
            Socket proxySocket = new Socket(proxyHost, proxyPort);
            System.out.println("Подключение к прокси-серверу: " + proxyHost + ":" + proxyPort);

            InputStream proxyInput = proxySocket.getInputStream();
            OutputStream proxyOutput = proxySocket.getOutputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter proxyWriter = new PrintWriter(new OutputStreamWriter(proxyOutput));
            BufferedReader proxyReader = new BufferedReader(new InputStreamReader(proxyInput));

                System.out.print("Введите URL: ");
                String requestUrl = reader.readLine();

                proxyWriter.println(requestUrl);
                proxyWriter.flush();

                String inputLine;
                while ((inputLine = proxyReader.readLine()) != null) {
                    System.out.println("Ответ от прокси-сервера: " + inputLine);
                }
                i++;
                proxyReader.close();
                proxyWriter.close();
                proxyOutput.close();
                proxyInput.close();
                proxySocket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}