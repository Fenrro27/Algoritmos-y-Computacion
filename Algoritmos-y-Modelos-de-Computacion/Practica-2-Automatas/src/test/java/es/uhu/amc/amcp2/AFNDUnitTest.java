package es.uhu.amc.amcp2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AFNDUnitTest {
    @Test
    public void testLoad() {
        Assertions.assertAll( () -> {
            AFNDDummy testAfd = new AFNDDummy();
            testAfd.load("ruta-al-fichero");
        });
    }

    @Test
    public void testReconocer() {
        Assertions.assertAll( () -> {
            AFNDDummy testAfd = new AFNDDummy();
            testAfd.load("ruta-al-fichero");

            // Comprobar cadena de estados reconocible
            Assertions.assertTrue(testAfd.reconocer("cadena reconocible"));

            // Comprobar cadena no reconocible
            Assertions.assertFalse(testAfd.reconocer("cadena NO reconocible"));
        });
    }

    @Test
    public void testEsFinal() {
        Assertions.assertAll( () -> {
            AFNDDummy testAfd = new AFNDDummy();
            testAfd.load("ruta-al-fichero");

            // Comprobar cadena de estados reconocible
            Assertions.assertTrue(testAfd.esFinal("estado final"));

            // Comprobar cadena no reconocible
            Assertions.assertFalse(testAfd.reconocer("estado no final"));
        });
    }
}
