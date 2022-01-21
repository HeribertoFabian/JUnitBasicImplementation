package org.guzman.junit5app.samples.models;

import org.guzman.junit5app.samples.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CuentaTest {
    Cuenta cuenta;

    @BeforeAll
    static void beforeAll() {
        System.out.println("Inicilizando el  test");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("Finalizando el test");

    }

    @BeforeEach
    void initMethodoTest(TestInfo testInfo, TestReporter testReporter) {
        this.cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));
        testReporter.publishEntry("Ejecutando: " +testInfo.getTestMethod().get().getName() + " #Objetivo: " + testInfo.getDisplayName() );

    }

    @AfterEach
    void tearDown() {
        System.out.println("Finalizando los metodos ");

    }

    @Test
    @DisplayName("Probando nombre de la cuenta")
    void testNombreCuenta() {

        String esperado = "Andres";
        String real = cuenta.getPersona();

        assertNotNull(real, () -> "la cuenta no puede ser nula");
        assertTrue(real.equals("Andres"), () -> "Esperado debe ser igual al real");
        assertEquals(esperado, real, () -> "Valor inesperado, se esperaba: " + esperado + " pero se obtuvo: " + real);
    }

    @Test
    @DisplayName("Probando el saldo de la cuenta")
    void testSaldoCuentaDev() {
        boolean isDev = "dev".equals(System.getProperty("ENV"));
        assumeTrue(isDev);
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("Probando el saldo de la cuenta2")
    void testSaldoCuentaDev2() {
        boolean isDev = "dev".equals(System.getProperty("ENV"));
        assumingThat(isDev, () -> {
            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        });

    }

    @Test
    @DisplayName("Pases por referencia vs valor")
    void testReferenciaCuenta() {
        Cuenta cuenta1 = new Cuenta("John Doe", new BigDecimal("8900.997"));
        Cuenta cuenta2 = new Cuenta("John Doe", new BigDecimal("8900.997"));

        //assertNotEquals(cuenta2, cuenta1);
        assertEquals(cuenta2, cuenta1);
    }

    @Test
    @DisplayName("Probando metodos de debito")
    void testDebitoCuenta() {
        cuenta.debito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.12345", cuenta.getSaldo().toPlainString());
    }

    @Test
    @DisplayName("Probando metodo credito")
    void testCreditoCuenta() {
        cuenta.credito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(1100, cuenta.getSaldo().intValue());
        assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
    }

    @Test
    @DisplayName("Manejo de Excepciones")
    void testDineroInsuficienteException() {
        Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
            cuenta.debito(new BigDecimal("1005"));
        });

        String actual = exception.getMessage();
        String esperado = "Dinero Insuficiente";

        assertEquals(esperado, actual);
    }

    @Test
    @DisplayName("Transferencias de dinero")
    void testTransferirDineroCuentas() {
        Cuenta cuenta1 = new Cuenta("Jhone Doe", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("800"));

        Banco banco = new Banco();
        banco.setNombre("Banco del estado");
        banco.transferir(cuenta2, cuenta1, new BigDecimal("500"));

        assertEquals("3000", cuenta1.getSaldo().toPlainString());
    }

    @Test
    @DisplayName("Relacion Banco-cuentas cuenta-Banco")
    void testRelacionBancoCuentas() {
        Cuenta cuenta1 = new Cuenta("Jhone Doe", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("800"));

        Banco banco = new Banco();
        banco.addCuenta(cuenta1);
        banco.addCuenta(cuenta2);

        banco.setNombre("Banco del Estado");
        banco.transferir(cuenta2, cuenta1, new BigDecimal("500"));

        assertAll(() -> {
            assertEquals("3000", cuenta1.getSaldo().toPlainString(), () -> "El valor del saldo de la cuenta 1 no es el esperado");
        }, () -> {
            assertEquals("300", cuenta2.getSaldo().toPlainString(), () -> "El valor del saldo de la cuenta 2 no es el esperado");
        }, () -> {
            assertEquals(2, banco.getCuentas().size(), () -> "El tamaño de la lista de cuentas es incorrecto se esperaba 2 y se obtuvo " + banco.getCuentas().size());
        }, () -> {
            assertEquals("Banco del Estado", cuenta1.getBanco().getNombre(), () -> "El nombre del banco del estado es incorrecto " + banco.getNombre());
        }, () -> {
            assertEquals("Andres", banco.getCuentas().stream().filter(c -> c.getPersona().equals("Andres")).findFirst().get().getPersona(), () -> "No se encontró ningun elemento con el nombre Andres");
        }, () -> {
            assertTrue(banco.getCuentas().stream().anyMatch(c -> c.getPersona().equals("Jhone Doe")), () -> "No se encontró ningun elemento con el nombre Jhone Doe");
        });
    }


    @Test
    @Disabled
    void ErrorForzado() {
        fail();
    }

    @Tag("requerimientos")
    @Nested
    @DisplayName("Validaciones del SO")
    class SistemaOperativoTest {
        @Test
        @EnabledOnOs(OS.WINDOWS)
        void tesSoloWindows() {
        }

        @Test
        @EnabledOnOs({OS.LINUX, OS.MAC})
        void tesSoloLinuxMac() {

        }

        @Test
        @DisabledOnOs(OS.WINDOWS)
        void testDiabledWindows() {
        }
    }

    @Tag("requerimientos")
    @Nested
    @DisplayName("Validando requerimientos Java")
    class JavaVersionTest {
        @Test
        @EnabledOnJre(JRE.JAVA_8)
        void soloJDK8() {
        }

        @Test
        @EnabledOnJre(JRE.JAVA_11)
        void testSoloJRE11() {

        }

        @Test
        @DisabledOnJre(JRE.JAVA_11)
        void testNoJDK11() {
        }
    }

    @Tag("requerimientos")
    @Nested
    @DisplayName("Probando propiedades del sistema")
    class SystemPropertiesTest {
        @Test
        void imprimirSystemProperties() {
            Properties properties = System.getProperties();

            properties.forEach((k, v) -> System.out.println(k + " : " + v));

        }

        @Test
        @EnabledIfSystemProperty(named = "java.version", matches = "11.0.11")
        @DisplayName("Java Version con System properties")
        void testJavaVersion() {
        }
    }

    @Tag("requerimientos")
    @Nested
    @DisplayName("Probando variables de ambiente")
    class VariableAmbienteTest {
        @Test
        void imprimirVariablesAmbiente() {
            Map<String, String> getenv = System.getenv();
            getenv.forEach((k, v) -> System.out.println(k + ":" + v));
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = ".*dk-11.0.12.*")
        void testJavaHome() {

        }

        @Test
        @EnabledIfEnvironmentVariable(named = "NUMBER_OF_PROCESSORS", matches = "12")
        void testProcesadores() {
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "DEV")
        void testEnv_dev() {

        }

        @Test
        @DisabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "PROD")
        void testENV_prod() {

        }
    }


    @RepeatedTest(value = 5, name = "{displayName} repeticion numero: {currentRepetition} de {totalRepetition}")
    @DisplayName("TestDebito Repeated")
    void testDebitoCuentaRepeated(RepetitionInfo info) {
        cuenta.debito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.12345", cuenta.getSaldo().toPlainString());
        if (info.getCurrentRepetition() == 3) {
            System.out.println("Estamos en la repeticion " + info.getCurrentRepetition() + " de " + info.getTotalRepetitions());
        }
    }

    @Tag("params")
    @Nested
    class PruebasParametrizadas {

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @ValueSource(strings = {"100", "200", "300", "500", "700", "1000.12345"})
        @DisplayName("1.- Parameterized ValueSource tesDebitoCuenta")
        void testDebitoCuentaParameterizedValueSource(String monto) {
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) >= 0);
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvSource({"1,100", "2,200", "3,300", "4,500", "5,700", "6,1000.12345"})
        @DisplayName("2.- Parameterized CSVSource tesDebitoCuenta")
        void testDebitoCuentaParameterizedCSVSource(String index, String monto) {
            System.out.println(index + " -> " + monto);
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) >= 0);
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvSource({"200,100", "250,200", "300,300", "510,500", "750,700", "1000.12345,1000.12345"})
        @DisplayName("3.- Prueba parametrizada 2 valores")
        void testDebitoCuentaParameterizedCSVSourceAlterno1(String saldo, String monto) {
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) >= 0);
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvSource({"200,100, jose, Andres", "250,200, Maria, Maria", "300,300, Pedro, Pedro", "510,500, stay, stay", "750,700, manuel, amanuel", "1000.12345,1000.12345, juan, pedro"})
        @DisplayName("4.- Prueba parametrizada 4 valores")
        void testDebitoCuentaParameterizedCSVSourceAlterno2(String saldo, String monto, String esperado, String actual) {
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            cuenta.setPersona(actual);

            assertNotNull(cuenta.getSaldo());
            assertNotNull(cuenta.getPersona());
            assertEquals(esperado, actual);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) >= 0);
        }

        @Disabled
        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvFileSource(resources = "/data.csv")
        @DisplayName("5.- Parameterized CsvFileSource tesDebitoCuenta")
        void testDebitoCuentaParameterizedCsvFileSource(String monto) {
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) >= 0);
        }
    }

    //  La siguiente prueba parametrizada queda fuera debido a que usa un metodo estatico
        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @MethodSource("montoList")
        @DisplayName("Parameterized MethodSource tesDebitoCuenta")
        @Tag("params")
        void testDebitoCuentaParameterizedCsvFileSourceMethodSource(String monto) {
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        static List<String> montoList() {
            return Arrays.asList("100", "200", "300", "500", "700", "1000.12345");

    }

    @Nested
    @Tag("timeout")
class ejemploTimeOutTest {

        @Test
        @Timeout(1)
        void pruebaTimeOut() throws InterruptedException {
            TimeUnit.SECONDS.sleep(2);
        }


        @Test
        @Timeout(value = 1000, unit = TimeUnit.MILLISECONDS)
        void pruebaTimeOut2() throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(1100);
        }

        @Test
        void testTimeoutAssertions() {
            assertTimeout(Duration.ofSeconds(5), ()->{
                TimeUnit.MILLISECONDS.sleep(5500);
            }, "falla de Timeout");
        }
    }
}