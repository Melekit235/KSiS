import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;

public class FileModel {
    private String dir = "files";
    private FileSystem fs = FileSystems.getDefault();

    public void getFileContent(HttpExchange exchange, String filename) throws IOException {
        Path filePath = fs.getPath(dir, filename);
        if (Files.exists(filePath)) {
            exchange.sendResponseHeaders(200, 0); // отправляем успешный ответ с заголовком Transfer-Encoding: chunked
            try (OutputStream out = exchange.getResponseBody();
                 InputStream in = Files.newInputStream(filePath)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                    out.flush();
                }
            }
        } else {
            throw new IOException();
        }
    }

    public void saveFileContent(String filename, InputStream content, boolean append) throws IOException {
        Path filePath = fs.getPath(dir, filename);
        if (!Files.exists(filePath)) {
            Files.createFile(filePath);
        }

        try (OutputStream out = Files.newOutputStream(filePath, append ? StandardOpenOption.APPEND : StandardOpenOption.TRUNCATE_EXISTING)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = content.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }

    public void deleteFile(String filename) throws IOException {
        Path filePath = fs.getPath(dir, filename);
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        } else {
            throw new IOException("Файл не найден");
        }
    }
}
