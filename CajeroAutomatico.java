import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class CajeroAutomatico {

    private static final String MOVIMIENTOS_FILE = "movimientos.txt";

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        double saldo = 1000.00; // Saldo inicial
        int opcion;


        do {
            System.out.println("\nCajero Automático");
            System.out.println("1- Retirar fondos");
            System.out.println("2- Ingresar fondos");
            System.out.println("3- Consultar movimientos");
            System.out.println("0- Salir");
            System.out.print("Seleccione una opción: ");
            opcion = scanner.nextInt();

            switch (opcion) {
                case 1: // Retirar fondos
                    System.out.print("Ingrese la cantidad a retirar: ");
                    double cantidadRetiro = scanner.nextDouble();
                    if (cantidadRetiro <= saldo) {
                        saldo -= cantidadRetiro;
                        System.out.println("Ha retirado " + cantidadRetiro + "€. Saldo actual: " + saldo + "€.");
                        registrarMovimiento("Retirada de fondos: " + cantidadRetiro + "€");
                    } else {
                        System.out.println("No se puede realizar la operación. Saldo insuficiente. Saldo actual: " + saldo + "€.");
                    }
                    break;
                case 2: // Ingresar fondos
                    System.out.print("Ingrese la cantidad a depositar: ");
                    double cantidadDeposito = scanner.nextDouble();
                    saldo += cantidadDeposito;
                    System.out.println("Ha ingresado " + cantidadDeposito + "€. Saldo actual: " + saldo + "€.");
                    registrarMovimiento("Ingreso de fondos: " + cantidadDeposito + "€");
                    break;
                case 3:
                    // Lógica de Consulta de movimientos
                    consultarMovimientos();
                    break;
                case 0: // Salir
                    System.out.println("Saliendo del sistema.");
                    break;
                default:
                    System.out.println("Opción no válida. Intente nuevamente.");
            }
        } while (opcion != 0);

        scanner.close();
    }

    private static void consultarMovimientos() {
        try (BufferedReader reader = new BufferedReader(new FileReader(MOVIMIENTOS_FILE))) {
            System.out.println("----- Movimientos -----");
            String linea;
            while ((linea = reader.readLine()) != null) {
                System.out.println(linea);
            }
            System.out.println("------------------------");
        } catch (IOException e) {
            System.out.println("Error al leer los movimientos desde el archivo.");
        }
    }

    private static void registrarMovimiento(String movimiento) {
        try (FileWriter writer = new FileWriter(MOVIMIENTOS_FILE, true)) {
            writer.append(movimiento).append("\n");
        } catch (IOException e) {
            System.out.println("Error al registrar el movimiento en el archivo.");
        }
    }
}
