package cliente;

import java.io.*;
import java.net.*;

public class Cliente {
    public static void main(String[] args) {
        try {
            // Intentar conectar al servidor
            Socket socket = new Socket("127.0.0.1", 8888); // Dirección y puerto del servidor
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Menú de operaciones
            System.out.println("Cliente conectado al servidor de gestión de tickets.");
            System.out.println("Opciones disponibles:");
            System.out.println("1. Crear Ticket");
            System.out.println("2. Asignar Ticket");
            System.out.println("3. Cambiar Estado de Ticket");
            System.out.println("4. Consultar Ticket");
            System.out.println("5. Cerrar Sesión");

            // Crear un buffer para leer la entrada del usuario
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            // Loop principal para interactuar con el servidor
            while (true) {
                System.out.print("Seleccione una opción: ");
                String opcion = reader.readLine();

                switch (opcion) {
                    case "1":
                        // Crear un ticket
                        crearTicket(out, in);
                        break;
                    case "2":
                        // Asignar un ticket
                        asignarTicket(out, in);
                        break;
                    case "3":
                        // Cambiar el estado de un ticket
                        cambiarEstadoTicket(out, in);
                        break;
                    case "4":
                        // Consultar un ticket
                        consultarTicket(out, in);
                        break;
                    case "5":
                        // Cerrar sesión
                        cerrarSesion(out, in);
                        socket.close();  // Cerrar la conexión al servidor
                        return;
                    default:
                        System.out.println("Opción no válida.");
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("Error al conectar o leer del servidor: " + e.getMessage());
        }
    }

    // Métodos para crear, asignar, cambiar estado, consultar y cerrar sesión
    private static void crearTicket(PrintWriter out, BufferedReader in) throws IOException {
        System.out.print("Ingrese el título del ticket: ");
        String titulo = new BufferedReader(new InputStreamReader(System.in)).readLine();
        System.out.print("Ingrese la descripción del ticket: ");
        String descripcion = new BufferedReader(new InputStreamReader(System.in)).readLine();
        System.out.print("Ingrese la prioridad del ticket (LOW, MEDIUM, HIGH): ");
        String prioridad = new BufferedReader(new InputStreamReader(System.in)).readLine();

        String mensaje = "CREATE|user=ana|title=" + titulo + "|desc=" + descripcion + "|priority=" + prioridad;
        System.out.println("Enviando mensaje: " + mensaje);
        out.println(mensaje);  // Enviar el mensaje al servidor

        // Esperar y leer la respuesta del servidor
        System.out.println("Esperando respuesta del servidor...");
        String response = in.readLine();
        System.out.println("Respuesta del servidor: " + response);
    }

    private static void asignarTicket(PrintWriter out, BufferedReader in) throws IOException {
        System.out.print("Ingrese el ID del ticket: ");
        String ticketId = new BufferedReader(new InputStreamReader(System.in)).readLine();
        System.out.print("Ingrese el usuario al que se asignará el ticket: ");
        String usuario = new BufferedReader(new InputStreamReader(System.in)).readLine();

        String mensaje = "ASSIGN|ticket=" + ticketId + "|assignee=" + usuario;
        out.println(mensaje);

        String response = in.readLine();
        System.out.println("Respuesta del servidor: " + response);
    }

    private static void cambiarEstadoTicket(PrintWriter out, BufferedReader in) throws IOException {
        System.out.print("Ingrese el ID del ticket: ");
        String ticketId = new BufferedReader(new InputStreamReader(System.in)).readLine();
        System.out.print("Ingrese el nuevo estado (IN_PROGRESS, RESOLVED, CLOSED): ");
        String estado = new BufferedReader(new InputStreamReader(System.in)).readLine();

        String mensaje = "STATE|ticket=" + ticketId + "|new_state=" + estado;
        out.println(mensaje);

        String response = in.readLine();
        System.out.println("Respuesta del servidor: " + response);
    }

    private static void consultarTicket(PrintWriter out, BufferedReader in) throws IOException {
        System.out.print("Ingrese el ID del ticket a consultar: ");
        String ticketId = new BufferedReader(new InputStreamReader(System.in)).readLine();

        String mensaje = "GET|ticket=" + ticketId;
        out.println(mensaje);

        String response = in.readLine();
        System.out.println("Respuesta del servidor: " + response);
    }

    private static void cerrarSesion(PrintWriter out, BufferedReader in) throws IOException {
        out.println("cerrarSesion");
        String response = in.readLine();
        System.out.println("Respuesta del servidor: " + response);
    }
}