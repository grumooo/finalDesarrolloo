import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class clientHandler implements Runnable {

    private Socket socket;
    private dbManager db;

    public clientHandler(Socket socket, dbManager db) {
        this.socket = socket;
        this.db = db;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             java.io.PrintWriter out = new java.io.PrintWriter(socket.getOutputStream(), true)) {

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                String mensajeClaro = seguridad.desencriptar(inputLine);

                if (mensajeClaro.startsWith("x:")) {
                    procesarDatosSensor(mensajeClaro);
                } else if (mensajeClaro.startsWith("PEDIR_HISTORIAL")) {
                    System.out.println("Solicitud de historial recibida.");

                    String fecha = "";
                    String hora = "";

                    if (mensajeClaro.contains(":")) {
                        String contenido = mensajeClaro.split(":")[1]; // "2025-11-26;14:30"

                        if (contenido.contains(";")) {
                            String[] partes = contenido.split(";");
                            fecha = partes[0];
                            if (partes.length > 1) hora = partes[1];
                        } else {
                            fecha = contenido;
                        }
                    }

                    java.util.List<String> lista = db.obtenerHistorial(fecha, hora);

                    for (String registro : lista) {
                        out.println(seguridad.encriptar(registro));
                    }
                    out.println(seguridad.encriptar("FIN_HISTORIAL"));
                }
            }
        } catch (IOException e) {
            System.out.println("Cliente desconectado.");
        }
    }

    private void procesarDatosSensor(String data) {
        try {

            String[] partes = data.replace(" ", "").split(",");

            int x = Integer.parseInt(partes[0].split(":")[1]);
            int y = Integer.parseInt(partes[1].split(":")[1]);
            int z = Integer.parseInt(partes[2].split(":")[1]);

            db.insertarRegistro(x, y, z);

        } catch (Exception e) {
            System.err.println("Error al procesar formato de datos: " + data);
        }
    }
}
