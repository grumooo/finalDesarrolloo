import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

public class clientTest {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 5000);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            Random rand = new Random();

            for (int i = 0; i < 5; i++) {
                int x = rand.nextInt(100);
                int y = rand.nextInt(100);
                int z = rand.nextInt(100);

                String mensaje = "x:" + x + ", y:" + y + ", z:" + z;
                out.println(mensaje);
                System.out.println("Enviando: " + mensaje);

                Thread.sleep(1000); // Esperar 1 segundo entre envíos
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}