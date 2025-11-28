import java.util.Base64;

public class seguridad {

    private static final String CLAVE = "esqteveoysonabanlasviejasdefeid";

    public static String encriptar(String datos) {
        return xorCipher(datos);
    }

    public static String desencriptar(String datosEncriptados) {
        return xorCipher(datosEncriptados);
    }

    private static String xorCipher(String input) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            output.append((char) (input.charAt(i) ^ CLAVE.charAt(i % CLAVE.length())));
        }
        return output.toString();
    }
}