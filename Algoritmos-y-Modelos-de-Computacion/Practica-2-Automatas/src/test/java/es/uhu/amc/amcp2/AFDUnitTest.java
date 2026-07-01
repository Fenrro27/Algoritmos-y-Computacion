package es.uhu.amc.amcp2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class AFDUnitTest {
    @Test
    public void testLoad() {
        Assertions.assertAll( () -> {
            AFDDummy testAfd = new AFDDummy();
            testAfd.load("ruta-al-fichero");
        });
    }

    @Test
    public void testReconocer() {
        Assertions.assertAll( () -> {
            AFDDummy testAfd = new AFDDummy();
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
            AFDDummy testAfd = new AFDDummy();
            testAfd.load("ruta-al-fichero");

            // Comprobar cadena de estados reconocible
            Assertions.assertTrue(testAfd.esFinal("estado final"));

            // Comprobar cadena no reconocible
            Assertions.assertFalse(testAfd.reconocer("estado no final"));
        });
    }
}
