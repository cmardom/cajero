import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CajeroAutomatico {

    private static final Map<String, Double> cuentas = new HashMap<>();
    private static Connection connection;

    public static void main(String[] args) {
        conectarBaseDeDatos();
        cargarCuentas();
        Scanner scanner = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("\nCajero Automático");
            System.out.println("1- Retirar fondos");
            System.out.println("2- Ingresar fondos");
            System.out.println("3- Consultar movimientos");
            System.out.println("4- Listar todas las cuentas de un cliente (se pide DNI)");
            System.out.println("5- Consultar cuentas con saldo menor a una cantidad");
            System.out.println("0- Salir");
            System.out.print("Seleccione una opción: ");
            opcion = scanner.nextInt();

            switch (opcion) {
                case 1: // Retirar fondos
                    retirarFondos();
                    break;
                case 2: // Ingresar fondos
                    ingresarFondos();
                    break;
                case 3: // Consultar movimientos
                    consultarMovimientos();
                    break;
                case 4: // Listar todas las cuentas de un cliente (se pide DNI)
                    listarCuentasCliente();
                    break;
                case 5: // Consultar cuentas con saldo menor a una cantidad
                    consultarCuentasSaldoMenor();
                    break;
                case 0: // Salir
                    System.out.println("Saliendo del sistema.");
                    break;
                default:
                    System.out.println("Opción no válida. Intente nuevamente.");
            }
        } while (opcion != 0);

        scanner.close();
        desconectarBaseDeDatos();
    }

    private static void conectarBaseDeDatos() {
        try {
            System.setProperty("jdbc.drivers", "com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/cuentas_bancarias";
            String user = "root";
            String pass = "root";
            connection = DriverManager.getConnection(url, user, pass);
            System.out.println("Conexión a la base de datos exitosa.");
        } catch (SQLException e) {
            System.out.println("Error al conectar a la base de datos: " + e.getMessage());
            System.exit(1); // Salir del programa si la conexión falla
        }
    }


    private static void desconectarBaseDeDatos() {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Desconexión de la base de datos exitosa.");
            }
        } catch (SQLException e) {
            System.out.println("Error al desconectar de la base de datos: " + e.getMessage());
        }
    }

    private static void retirarFondos() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese la cantidad a retirar: ");
        double cantidadRetiro = scanner.nextDouble();
        if (cantidadRetiro <= 0) {
            System.out.println("La cantidad debe ser mayor a cero.");
            return;
        }

        System.out.print("Ingrese su DNI: ");
        String dni = scanner.next();

        try {
            String consulta = "INSERT INTO movimientos VALUES (?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(consulta)) {
                ps.setString(1, dni);
                ps.setObject(2, LocalDateTime.now());
                ps.setDouble(3, cantidadRetiro * -1);
                ps.executeUpdate();
            }

            double saldo = cuentas.get(dni);
            if (cantidadRetiro <= saldo) {
                saldo -= cantidadRetiro;
                cuentas.put(dni, saldo);
                System.out.println("Ha retirado " + cantidadRetiro + "€. Saldo actual: " + saldo + "€.");
            } else {
                System.out.println("No se puede realizar la operación. Saldo insuficiente. Saldo actual: " + saldo + "€.");
            }
        } catch (SQLException e) {
            System.out.println("Error al ejecutar la consulta SQL: " + e.getMessage());
        }
    }

    private static void ingresarFondos() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese la cantidad a depositar: ");
        double cantidadDeposito = scanner.nextDouble();
        if (cantidadDeposito <= 0) {
            System.out.println("La cantidad debe ser mayor a cero.");
            return;
        }

        System.out.print("Ingrese su DNI: ");
        String dni = scanner.next();

        try {
            String consulta = "INSERT INTO movimientos VALUES (?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(consulta)) {
                ps.setString(1, dni);
                ps.setObject(2, LocalDateTime.now());
                ps.setDouble(3, cantidadDeposito);
                ps.executeUpdate();
            }

            double saldo = cuentas.get(dni);
            saldo += cantidadDeposito;
            cuentas.put(dni, saldo);
            System.out.println("Ha ingresado " + cantidadDeposito + "€. Saldo actual: " + saldo + "€.");
        } catch (SQLException e) {
            System.out.println("Error al ejecutar la consulta SQL: " + e.getMessage());
        }
    }

    private static void consultarMovimientos() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Introduzca el Número de Cuenta : ");
        String dni = scanner.next();

        try {
            String consulta = "SELECT num_cuenta, fecha, importe FROM movimientos WHERE num_cuenta = ?";
            try (PreparedStatement ps = connection.prepareStatement(consulta)) {
                ps.setString(1, dni);
                ResultSet rs = ps.executeQuery();
                System.out.println("      Cuenta                Fecha           Importe");
                System.out.println("-------------------- ------------------- ------------- ");
                while (rs.next()) {
                    String cuenta = rs.getString("num_cuenta");
                    LocalDateTime fecha = rs.getObject("fecha", LocalDateTime.class);
                    double importe = rs.getDouble("importe");
                    System.out.println(cuenta + " " + fecha + " " + importe);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al ejecutar la consulta SQL: " + e.getMessage());
        }
    }

    private static void listarCuentasCliente() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese su DNI: ");
        String dni = scanner.next();

        try {
            String consulta = "SELECT num_cuenta, saldo_actual FROM cuentas WHERE dni = ?";
            try (PreparedStatement ps = connection.prepareStatement(consulta)) {
                ps.setString(1, dni);
                ResultSet rs = ps.executeQuery();
                System.out.println("----- Cuentas del cliente (DNI: " + dni + ") -----");
                while (rs.next()) {
                    String cuenta = rs.getString("num_cuenta");
                    double saldo = rs.getDouble("saldo_actual");
                    System.out.println("Cuenta: " + cuenta + " - Saldo: " + saldo + "€");
                }
                System.out.println("----------------------------------------------");
            }
        } catch (SQLException e) {
            System.out.println("Error al ejecutar la consulta SQL: " + e.getMessage());
        }
    }

    private static void consultarCuentasSaldoMenor() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese la cantidad máxima de saldo: ");
        double maxSaldo = scanner.nextDouble();

        try {
            String consulta = "SELECT num_cuenta, saldo_actual FROM cuentas WHERE saldo_actual < ?";
            try (PreparedStatement ps = connection.prepareStatement(consulta)) {
                ps.setDouble(1, maxSaldo);
                ResultSet rs = ps.executeQuery();
                System.out.println("----- Cuentas con saldo menor a " + maxSaldo + "€ -----");
                while (rs.next()) {
                    String cuenta = rs.getString("num_cuenta");
                    double saldo = rs.getDouble("saldo_actual");
                    System.out.println("Cuenta: " + cuenta + " - Saldo: " + saldo + "€");
                }
                System.out.println("----------------------------------------------");
            }
        } catch (SQLException e) {
            System.out.println("Error al ejecutar la consulta SQL: " + e.getMessage());
        }
    }

    private static void cargarCuentas() {
        try {
            String consulta = "SELECT dni, SUM(saldo_actual) as saldo FROM cuentas GROUP BY dni";
            try (PreparedStatement ps = connection.prepareStatement(consulta)) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String dni = rs.getString("dni");
                    double saldo = rs.getDouble("saldo");
                    cuentas.put(dni, saldo);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al ejecutar la consulta SQL: " + e.getMessage());
        }
    }
}
