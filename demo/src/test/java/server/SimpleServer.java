package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;

public class SimpleServer {

    public static void main(String[] args) throws Exception {
        int port = 8080;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("server start listener port: " + port);

        while (true) {
            System.out.println("server start listener ------- port: " + port);
            try {
                Socket clientSocket = serverSocket.accept();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date = sdf.format(System.currentTimeMillis());
                System.out.println(date+" new client connect: " + clientSocket.getInetAddress());
                Thread clientThread = new Thread(new ClientHandler(clientSocket));
                clientThread.start();
            }catch (Exception e){
                System.out.println("server err="+e.getMessage());
            }
        }
    }
}