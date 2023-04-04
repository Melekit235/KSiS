import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ServerController implements HttpHandler {
    private FileModel model;
    public ServerController(FileModel model) {
        this.model = model;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String requestURI = exchange.getRequestURI().toString();
        byte[] response = new byte[0];
        int statusCode = 200;
        String filename = requestURI.substring(requestURI.indexOf('/') + 1);

        switch (requestMethod) {
            case "GET":
                try {
                    model.getFileContent(exchange, filename);
                } catch (IOException | RuntimeException e) {
                    sendErrorResponse(exchange, 404, "Файл не найден");
                    return;
                }
                break;
            case "POST":
                try {
                    InputStream requestBody = exchange.getRequestBody();
                    model.saveFileContent(filename, requestBody, true);
                } catch (IOException | RuntimeException e) {
                    sendErrorResponse(exchange, 500, "Ошибка сервера");
                    return;
                }
                break;
            case "PUT":
                try {
                    InputStream requestBody = exchange.getRequestBody();
                    model.saveFileContent(filename, requestBody, false);
                } catch (IOException | RuntimeException e) {
                    sendErrorResponse(exchange, 500, "Ошибка сервера");
                    return;
                }
                break;
            case "DELETE":
                try {
                    model.deleteFile(filename);
                } catch (IOException | RuntimeException e) {
                    sendErrorResponse(exchange, 404, "Файл не найден");
                    return;
                }
                break;
            default:
                sendErrorResponse(exchange, 405, "Метод недоступен");
                return;

        }
        exchange.sendResponseHeaders(statusCode, response.length);
        OutputStream os = exchange.getResponseBody();
        os.write(response);
        os.close();
    }

    private void sendErrorResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        String response =  statusCode+" "+message;
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}