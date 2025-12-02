import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class dbManager {

    private static final String URL = "jdbc:sqlite:monitorBD.db";

    private Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }
        return conn;
    }

    public void crearTabla() {
        String sql = "CREATE TABLE IF NOT EXISTS datos_sensor (\n"
                + " id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n"
                + " x INTEGER NOT NULL,\n"
                + " y INTEGER NOT NULL,\n"
                + " z INTEGER NOT NULL,\n"
                + " fecha_de_captura TEXT NOT NULL,\n"
                + " hora_de_captura TEXT NOT NULL\n"
                + ");";

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabla 'datos_sensor' verificada o creada correctamente.");
        } catch (SQLException e) {
            System.out.println("Error creando la tabla: " + e.getMessage());
        }
    }

    public void insertarRegistro(int x, int y, int z) {
        String sql = "INSERT INTO datos_sensor(x, y, z, fecha_de_captura, hora_de_captura) VALUES(?,?,?,?,?)";

        String fecha = LocalDate.now().toString();
        String hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, x);
            pstmt.setInt(2, y);
            pstmt.setInt(3, z);
            pstmt.setString(4, fecha);
            pstmt.setString(5, hora);

            pstmt.executeUpdate();
            System.out.println("Registro guardado: X=" + x + ", Y=" + y + ", Z=" + z + " a las " + hora);

        } catch (SQLException e) {
            System.out.println("Error insertando datos: " + e.getMessage());
        }
    }
    public List<String> obtenerHistorial(String fechaFiltro, String horaFiltro) {
        List<String> historial = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT x, y, z, fecha_de_captura, hora_de_captura FROM datos_sensor WHERE 1=1");

        if (fechaFiltro != null && !fechaFiltro.isEmpty()) {
            sql.append(" AND fecha_de_captura = ?");
        }
        if (horaFiltro != null && !horaFiltro.isEmpty()) {
            sql.append(" AND hora_de_captura LIKE ?");
        }

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int indice = 1;
            if (fechaFiltro != null && !fechaFiltro.isEmpty()) {
                pstmt.setString(indice++, fechaFiltro);
            }
            if (horaFiltro != null && !horaFiltro.isEmpty()) {
                pstmt.setString(indice++, horaFiltro + "%");
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int x = rs.getInt("x");
                    int y = rs.getInt("y");
                    int z = rs.getInt("z");
                    String f = rs.getString("fecha_de_captura");
                    String h = rs.getString("hora_de_captura");

                    historial.add("x:" + x + ", y:" + y + ", z:" + z + ", fecha:" + f + ", hora:" + h);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return historial;
    }
}
