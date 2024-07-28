package server;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private String IM=null;
    private String ip="";

    public ClientHandler(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream(), true);
         this.ip = clientSocket.getInetAddress().toString();
    }

    public void run() {
        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if((!Util.isNullOrEmpty(inputLine))&&inputLine.startsWith("$CMD=IMEI=")){
                    IM=inputLine.substring(10).trim();
                }
                String result ="";
                if(!Util.isNullOrEmpty(inputLine)){
                     result = Util.dealCMD(inputLine,out,IM);

                }
                if (!Util.isNullOrEmpty(result)) {
                    out.println(result); //reply
                }

                System.out.println("received msg=" + inputLine + "  reply=" + result);
                if (inputLine.equals("bye"))
                    break;
            }

            in.close();
            out.close();
            Util.removeDeviceByIM(IM);
            out=null;
            clientSocket.close();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = sdf.format(System.currentTimeMillis());
            System.out.println(date+" client closed ip"+ip);
        } catch (Exception e) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = sdf.format(System.currentTimeMillis());
            System.out.println(date+" client talk err="+e+ "  ip="+ip);
        } finally {
            try {
                in.close();
                out.close();
                out=null;
                clientSocket.close();
            } catch (Exception e) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date = sdf.format(System.currentTimeMillis());
                System.out.println(date+" client close finally err" + e.getMessage()+" ip="+ip);
            }
        }
    }
}