import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Scanner;

import static java.lang.System.exit;

public class client {

    private static String serverURL = "http://localhost:";
    static Scanner in = new Scanner(System.in);
    static OutputStream out;
    public static void main(String[] args) {
        System.out.println("Введите порт");
        String port = in.nextLine();
        serverURL=serverURL+port+"/";
        while (true)
        {
            try {
                System.out.println("\nВыберите запрос:");
                System.out.println("1.Get  2.Put  3.Post  4.Delete  5.Copy  6.Move  0.Exit");

                switch (in.nextInt()) {
                    case 1 -> {
                        sendGET();
                    }
                    case 2 -> {
                        sendPUT();
                    }
                    case 3 -> {
                        sendPOST();
                    }
                    case 4 -> {
                        sendDELETE();
                    }
                    case 5 -> {
                        sendCOPY();
                    }
                    case 6 -> {
                        sendMOVE();
                    }
                    case 0 -> {
                        exit(0);
                    }
                }
            }
            catch (RuntimeException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void sendGET() {
        URL url;
        HttpURLConnection conn;
        String filename;
        try {
            System.out.println("Введите имя файла:");
            filename=in.next();
            url = new URL(serverURL + filename);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            if (conn.getResponseCode() != 200) {
                InputStream errorStream = conn.getErrorStream();
                errorStream.close();
                System.out.println(errorStream);
            }
            else {
                InputStream in = conn.getInputStream();
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    System.out.write(buffer, 0, bytesRead);
                }
                in.close();
            }
            conn.disconnect();
        }
        catch (MalformedURLException e){
            System.out.println("Ошибка URL");
        }
        catch (IOException e)
        {
            System.out.println("Ошибка, невозможно считать файл!");
        }
    }

    public static void sendPOST() {
        URL url;
        HttpURLConnection conn;
        try {
            System.out.println("Введите путь где файл будет сохранен:");
            String filepath=in.next();
            Path path = Path.of(filepath);
            url = new URL(serverURL + path);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            out = conn.getOutputStream();
            out.flush();
            out.close();
            if (conn.getResponseCode() != 200) {
                InputStream errorStream = conn.getErrorStream();
                errorStream.close();
                System.out.println(errorStream);
            }
            System.out.println("POST Response:");
            System.out.println(conn.getResponseMessage());
            conn.disconnect();
        }
        catch (MalformedURLException e){
            System.out.println("Ошибка URL");
        }
        catch (IOException e) {
            System.out.println("Ошибка, невозможно считать файл!");
        }
    }

    public static void sendPUT() {
        URL url;
        HttpURLConnection conn;
        String filename;
        try {
            System.out.println("Введите имя файла, который хотите изменить:");
            filename=in.next();
            url = new URL(serverURL + filename);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setDoOutput(true);
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            out.flush();
            System.out.println("Введите новые данные");
            out.write(in.next());
            out.close();
            if (conn.getResponseCode() != 200) {
                InputStream errorStream = conn.getErrorStream();
                errorStream.close();
                System.out.println(errorStream);
            }
            System.out.println("PUT Response:");
            System.out.println(conn.getResponseMessage());
            conn.disconnect();
        }
        catch (MalformedURLException e){
            System.out.println("Ошибка URL");
        }
        catch (IOException e)
        {
            System.out.println("Ошибка , невозможно считать файл!");
        }
    }

    public static void sendDELETE() {
        URL url;
        HttpURLConnection conn;
        String filename;
        try {
            System.out.println("Введите имя файла:");
            filename=in.next();
            url = new URL(serverURL + filename);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.connect();
            if (conn.getResponseCode() != 200) {
                InputStream errorStream = conn.getErrorStream();
                errorStream.close();
                System.out.println(errorStream);
            }
            System.out.println("DELETE Response:");
            System.out.println(conn.getResponseMessage());
            conn.disconnect();
        }
        catch (MalformedURLException e){
            System.out.println("Ошибка URL");
        }
        catch (IOException e) {
            System.out.println("Ошибка, невозможно считать файл!");
        }
    }

    public static void sendMOVE() {
        URL url;
        HttpURLConnection conn;
        String filename;
        String destination;
        try {
            System.out.println("Введите имя файла:");
            filename = in.next();
            System.out.println("Введите путь, куда скопировать файл:");
            destination = in.next();
            url = new URL(serverURL + filename);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            if (conn.getResponseCode() != 200) {
                InputStream errorStream = conn.getErrorStream();
                errorStream.close();
            }else{
                InputStream in = conn.getInputStream();
                byte[] response = in.readAllBytes();
                in.close();
                conn.disconnect();
                url = new URL(serverURL + destination);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                out = conn.getOutputStream();
                out.write(response);
                out.flush();
                out.close();

                if (conn.getResponseCode() != 200) {
                    InputStream errorStream = conn.getErrorStream();
                    errorStream.close();
                    System.out.println(errorStream);
                }else {
                    url = new URL(serverURL + filename);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("DELETE");
                    conn.connect();
                    if (conn.getResponseCode() != 200) {
                        InputStream errorStream = conn.getErrorStream();
                        errorStream.close();
                        System.out.println(errorStream);
                    }
                }
                System.out.println("MOVE Response:");
                System.out.println(conn.getResponseMessage());
                conn.disconnect();
            }
        }
        catch (MalformedURLException e) {
            System.out.println("Ошибка URL");
        }
        catch (IOException e) {
            System.out.println("Ошибка, невозможно считать файл!");
        }
    }

    public static void sendCOPY() {
        URL url;
        HttpURLConnection conn;
        String filename;
        String destination;
        try {
            System.out.println("Введите имя файла:");
            filename = in.next();
            System.out.println("Введите путь, куда скопировать файл:");
            destination = in.next();
            url = new URL(serverURL + filename);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            if (conn.getResponseCode() != 200) {
                InputStream errorStream = conn.getErrorStream();
                errorStream.close();
                System.out.println(errorStream);
            }else{
                InputStream in = conn.getInputStream();
                byte[] response = in.readAllBytes();
                in.close();
                conn.disconnect();
                url = new URL(serverURL + destination);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                out = conn.getOutputStream();
                out.write(response);
                out.flush();
                out.close();
                if (conn.getResponseCode() != 200) {
                    InputStream errorStream = conn.getErrorStream();
                    errorStream.close();
                    System.out.println(errorStream);
                }
                System.out.println("MOVE Response:");
                System.out.println(conn.getResponseMessage());
            }
            conn.disconnect();
        } catch (MalformedURLException e) {
            System.out.println("Ошибка URL");
        } catch (IOException e) {
            System.out.println("Ошибка, невозможно считать файл!");
        }
    }

}