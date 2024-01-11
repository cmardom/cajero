import java.util.Scanner;

public class CajeroAutomatico {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        double saldo = 1000.00; // Saldo inicial
        int opcion;

        do {
            System.out.println("\nCajero Automático");
            System.out.println("1- Retirar fondos");
            System.out.println("2- Ingresar fondos");
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
                    } else {
                        System.out.println("No se puede realizar la operación. Saldo insuficiente. Saldo actual: " + saldo + "€.");
                    }
                    break;
                case 2: // Ingresar fondos
                    System.out.print("Ingrese la cantidad a depositar: ");
                    double cantidadDeposito = scanner.nextDouble();
                    saldo += cantidadDeposito;
                    System.out.println("Ha ingresado " + cantidadDeposito + "€. Saldo actual: " + saldo + "€.");
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
}
