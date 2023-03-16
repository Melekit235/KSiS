import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;


class ServerSomething extends Thread {

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    public ServerSomething(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        start();
    }

    @Override
    public void run() {
        String word;
        try {
            word = in.readLine();
            try {
                out.write(word + "\n");
                out.flush();
            } catch (IOException ignored) {}

                while (true) {
                    word = in.readLine();
                    if(word.equals("stop")) {
                        this.downService();
                        break;
                    }
                    System.out.println("Echoing: " + word);
                    for (ServerSomething vr : Server.serverList) {
                        vr.send(word); 
                    }
                }



        } catch (IOException e) {
            this.downService();
        }
    }

    private void downService() {
        try {
            if(!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
                for (ServerSomething vr : Server.serverList) {
                    if(vr.equals(this)) vr.interrupt();
                    Server.serverList.remove(this);
                }
            }
        } catch (IOException ignored) {}
    }


    private void send(String msg) {
        try {
            out.write(msg + "\n");
            out.flush();
        } catch (IOException ignored) {}
    }
}

public class Server{

    public static int PORT;
    public static LinkedList<ServerSomething> serverList = new LinkedList<>();


    public static void main(String[] args) throws IOException {
        ServerSocket server = null;

        boolean isAble = false;

        while (!isAble){
            Scanner in = new Scanner(System.in);
            System.out.print("Input a port: ");
            PORT = in.nextInt();

            try{
                server = new ServerSocket(PORT);
                isAble = true;

            }
            catch (Throwable e){
                System.out.println("no");
                isAble=false;
            }
        }


        try(FileWriter writer = new FileWriter("port.txt", false))
        {
            String str = Integer.toString(PORT);
            writer.write(str);

            writer.flush();
        }
        catch(IOException ex){

            System.out.println(ex.getMessage());
        }




        System.out.println("Server Started");
        try {
            while (true) {
                Socket socket = server.accept();
                try {
                    serverList.add(new ServerSomething(socket));
                } catch (IOException e) {
                    socket.close();
                }
            }
        } finally {
            server.close();
        }
    }
}