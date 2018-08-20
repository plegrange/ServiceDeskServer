import com.sun.deploy.net.socket.UnixSocketImpl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

public class Server implements Observer {
    private Socket socket;
    private Vector clients;
    private ServerSocket serverSocket;
    private StartServerThread startServerThread;
    private ClientThread clientThread;
    private int port;
    private boolean listening;

    public static void main(String[] args) {
        new Server().startServer();
    }

    public Server() {
        this.clients = new Vector();
        this.port = 5555;
        this.listening = false;
    }

    public void startServer() {
        if (!listening) {
            this.startServerThread = new StartServerThread();
            this.startServerThread.start();
            this.listening = true;
        }
    }

    public void stopServer() {
        if (this.listening) {
            this.startServerThread.stopServerThread();
            java.util.Enumeration e = this.clients.elements();
            while (e.hasMoreElements()) {
                ClientThread ct = (ClientThread) e.nextElement();
                ct.stopClient();
            }
            this.listening = false;
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        this.clients.removeElement(o);
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    private class StartServerThread extends Thread {
        private boolean listen;

        public StartServerThread() {
            this.listen = false;
        }

        public void run() {
            this.listen = true;
            try {
                Server.this.serverSocket = new ServerSocket(Server.this.port);
                while (this.listen) {
                    Server.this.socket = Server.this.serverSocket.accept();
                    System.out.println("Client Connected");
                    try {
                        Server.this.clientThread = new ClientThread(Server.this.socket);
                        Thread t = new Thread(Server.this.clientThread);
                        Server.this.clientThread.addObserver(Server.this);
                        Server.this.clients.addElement(Server.this.clientThread);
                        t.start();
                    } catch (IOException ioe) {

                    }
                }
            } catch (IOException ioe) {
                this.stopServerThread();
            }
        }

        public void stopServerThread() {
            try {
                Server.this.serverSocket.close();
            } catch (IOException ioe) {

            }
            this.listen = false;
        }
    }
}
