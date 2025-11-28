import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class server {
    private static final int PORT = 5000;

    public static void main(String[] args) {
        dbManager db = new dbManager();
        db.crearTabla();
        System.out.println("Iniciando Servidor en el puerto " + PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor esperando conexiones...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                
                System.out.println("--> Nuevo cliente conectado: " + clientSocket.getInetAddress());

                clientHandler clientHandler = new clientHandler(clientSocket, db);
                new Thread(clientHandler).start();
            }

        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }
}