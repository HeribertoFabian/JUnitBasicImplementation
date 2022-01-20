package org.guzman.junit5app.samples.models;

import org.guzman.junit5app.samples.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CuentaTest {


    @Test
    void testNombreCuenta() {
        Cuenta cuenta = new Cuenta();
        cuenta.setPersona("Andres");
        String esperado = "Andres";
        String real = cuenta.getPersona();

        assertNotNull(real, () -> "la cuenta no puede ser nula");
        assertTrue(real.equals("Andres"), () -> "Esperado debe ser igual al real");
        assertEquals(esperado, real, () -> "Valor inesperado, se esperaba: " + esperado + " pero se obtuvo: " + real);
    }

    @Test
    void testSaldoCuenta() {
        Cuenta cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testReferenciaCuenta() {
        Cuenta cuenta = new Cuenta("John Doe", new BigDecimal("8900.997"));
        Cuenta cuenta2 = new Cuenta("John Doe", new BigDecimal("8900.997"));

        //assertNotEquals(cuenta2, cuenta);
        assertEquals(cuenta2, cuenta);
    }

    @Test
    void testDebitoCuenta() {
        Cuenta cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));
        cuenta.debito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.12345", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testCreditoCuenta() {
        Cuenta cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));
        cuenta.credito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(1100, cuenta.getSaldo().intValue());
        assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testDineroInsuficienteException() {
        Cuenta cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));
        Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
            cuenta.debito(new BigDecimal("1005"));
        });

        String actual = exception.getMessage();
        String esperado = "Dinero Insuficiente";

        assertEquals(esperado, actual);
    }

    @Test
    void testTransferirDineroCuentas() {
        Cuenta cuenta1 = new Cuenta("Jhone Doe", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("800"));

        Banco banco = new Banco();
        banco.setNombre("Banco del estado");
        banco.transferir(cuenta2, cuenta1, new BigDecimal("500"));

        assertEquals("3000", cuenta1.getSaldo().toPlainString());
    }

    @Test
    void testRelacionBancoCuentas() {
        Cuenta cuenta1 = new Cuenta("Jhone Doe", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("800"));

        Banco banco = new Banco();
        banco.addCuenta(cuenta1);
        banco.addCuenta(cuenta2);

        banco.setNombre("Banco del Estado");
        banco.transferir(cuenta2, cuenta1, new BigDecimal("500"));

        assertAll(() -> {
            assertEquals("3000", cuenta1.getSaldo().toPlainString(),
                    () -> "El valor del saldo de la cuenta 1 no es el esperado");
        }, () -> {
            assertEquals("300", cuenta2.getSaldo().toPlainString(),
                    () -> "El valor del saldo de la cuenta 2 no es el esperado");
        }, () -> {
            assertEquals(2, banco.getCuentas().size(),
                    () -> "El tamaño de la lista de cuentas es incorrecto se esperaba 2 y se obtuvo " + banco.getCuentas().size());
        }, () -> {
            assertEquals("Banco del Estado", cuenta1.getBanco().getNombre(),
                    () -> "El nombre del banco del estado es incorrecto " + banco.getNombre());
        }, () -> {
            assertEquals("Andres", banco.getCuentas().stream().filter(c -> c.getPersona().equals("Andres")).findFirst().get().getPersona(),
                    () -> "No se encontró ningun elemento con el nombre Andres");
        }, () -> {
            assertTrue(banco.getCuentas().stream().anyMatch(c -> c.getPersona().equals("Jhone Doe")),
                    () -> "No se encontró ningun elemento con el nombre Jhone Doe");
        });
    }
}