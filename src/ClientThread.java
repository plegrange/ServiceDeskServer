import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Observable;

public class ClientThread extends Observable implements Runnable {
    private BufferedReader br;
    private PrintWriter pw;

    private Socket socket;
    private boolean running;

    public ClientThread(Socket socket) throws IOException {
        this.socket = socket;
        running = false;
        try {
            br = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()));
            pw = new PrintWriter(socket.getOutputStream(), true);
            running = true;
        } catch (IOException ioe) {
            throw ioe;
        }
    }

    public void stopClient() {
        try {
            this.socket.close();
        } catch (IOException ioe) {

        }
    }

    @Override
    public void run() {
        String msg = "";
        pw.println("Successfully Connected to Server");
        try {
            while ((msg = br.readLine()) != null && running) {
                pw.println(msg);
            }
            running = false;
        } catch (IOException ioe) {
            running = false;
        }
        try {
            this.socket.close();

            System.out.println("Closing Connection");
        }catch (IOException ioe){

        }
        this.setChanged();
        this.notifyObservers(this);
    }
}
