package ClientServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class InquiryManagerServer {
    ServerSocket myServer;

    public InquiryManagerServer() {
        try {
            this.myServer = new ServerSocket(5000);
            System.out.println("השרת פועל ומקשיב על port 5000...");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    public void start(){
        while (true){
            try {
                System.out.println("ממתין להתחברויות..................");
                Socket connection = myServer.accept();
                System.out.println("חיבור חדש----" + connection.getInetAddress().getHostAddress());
                HandleClient client = new HandleClient(connection);
                client.start();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
    public void stop(){
        if(myServer!=null && !myServer.isClosed()){
            try {
                myServer.close();
                System.out.println("Server stopped.");
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }}
}
