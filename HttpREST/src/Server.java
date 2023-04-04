import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Server {

    public static void main(String[] args){
        boolean flag = false;
        while (!flag){
            try {
                Scanner in = new Scanner(System.in);
                System.out.println("Введите номер порта");
                HttpServer server = HttpServer.create(new InetSocketAddress(in.nextInt()),100);
                in.close();
                flag = true;
                System.out.println("Сервер запущен");
                server.createContext("/", new ServerController(new FileModel()));//
                server.setExecutor(null);
                server.start();
            }catch (InputMismatchException e){
                System.out.println("Неверный ввод");
            }catch (BindException e){
                System.out.println("Порт занят");
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}