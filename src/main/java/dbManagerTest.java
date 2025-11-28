public class dbManagerTest {
    public static void main(String[] args) {
        dbManager db = new dbManager();

        db.crearTabla();

        System.out.println("Insertando datos de prueba...");
        db.insertarRegistro(10, 20, 30);
        db.insertarRegistro(15, 25, 35);
        db.insertarRegistro(100, 200, 300);

    }
}
