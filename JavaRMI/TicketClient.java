package com.tickets.client;

import com.tickets.common.ITicketService;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.Map;
import java.util.List;

public class TicketClient {

    public static void main(String[] args) {
        // En una ejecución distribuida real, cambia "localhost" por la IP del servidor
        String host = "localhost"; 
        
        try {
            // 1. Intentar conectar con el registro RMI
            Registry registry = LocateRegistry.getRegistry(host, 1099);

            // 2. Buscar el servicio remoto
            ITicketService servicio = (ITicketService) registry.lookup("TicketService");

            Scanner sc = new Scanner(System.in);

            while (true) {
                System.out.println("\n--- MENÚ CLIENTE RMI (JAVA) ---");
                System.out.println("1. CREATE (Crear ticket)");
                System.out.println("2. GET (Consultar ticket)");
                System.out.println("3. LIST (Listar IDs)");
                System.out.println("4. STATE (Cambiar estado)");
                System.out.println("5. ASSIGN (Asignar responsable)");
                System.out.println("6. COMMENT (Añadir comentario)");
                System.out.println("7. SALIR");
                System.out.print("Selecciona una opción: ");
                
                String op = sc.nextLine();

                if (op.equals("7")) {
                    System.out.println("Saliendo del sistema...");
                    break;
                }

                try {
                    switch (op) {
                        case "1":
                            System.out.print("Usuario: "); String u = sc.nextLine();
                            System.out.print("Título: "); String t = sc.nextLine();
                            System.out.print("Descripción: "); String d = sc.nextLine();
                            System.out.print("Prioridad (LOW/MEDIUM/HIGH): "); String p = sc.nextLine();
                            String res = servicio.crearTicket(u, t, d, p);
                            System.out.println("Servidor dice: " + res);
                            break;

                        case "2":
                            System.out.print("Introduce ID del Ticket: ");
                            int idGet = Integer.parseInt(sc.nextLine());
                            Map<String, Object> ticket = servicio.consultarTicket(idGet);
                            if (ticket != null) {
                                System.out.println("Datos del Ticket: " + ticket);
                            } else {
                                System.out.println("Error: Ticket no encontrado.");
                            }
                            break;

                        case "3":
                            System.out.println("Listando todos los tickets...");
                            List<Integer> lista = servicio.listarTickets(null, null);
                            System.out.println("IDs registrados: " + lista);
                            break;

                        case "4":
                            System.out.print("ID del ticket: ");
                            int idS = Integer.parseInt(sc.nextLine());
                            System.out.print("Nuevo estado (ASSIGNED/IN_PROGRESS/RESOLVED/CLOSED): ");
                            String nestado = sc.nextLine();
                            boolean okS = servicio.cambiarEstado(idS, nestado);
                            if (okS) {
                                System.out.println("Estado actualizado con éxito.");
                            } else {
                                System.out.println("Error: Transición no permitida o ID inválido.");
                            }
                            break;

                        case "5":
                            System.out.print("ID del ticket: ");
                            int idA = Integer.parseInt(sc.nextLine());
                            System.out.print("Nuevo responsable: ");
                            String resp = sc.nextLine();
                            boolean okA = servicio.asignarTicket(idA, resp);
                            System.out.println(okA ? "Asignación realizada." : "Error al asignar.");
                            break;

                        case "6":
                            System.out.print("ID del ticket: ");
                            int idC = Integer.parseInt(sc.nextLine());
                            System.out.print("Tu nombre/usuario: ");
                            String userC = sc.nextLine();
                            System.out.print("Mensaje del comentario: ");
                            String textoC = sc.nextLine();
                            servicio.añadirComentario(idC, userC, textoC);
                            System.out.println("Comentario añadido correctamente.");
                            break;

                        default:
                            System.out.println("Opción no válida.");
                            break;
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Error: Debes introducir un número válido para el ID.");
                }

            }

        } catch (java.rmi.ConnectException e) {
            System.err.println("Error: No se pudo conectar con el servidor RMI. Comprueba que ServerMain esté corriendo.");
        } catch (java.rmi.NotBoundException e) {
            System.err.println("Error: El servicio 'TicketService' no se encuentra registrado.");
        } catch (Exception e) {
            System.err.println("Error en el cliente: " + e.toString());
            e.printStackTrace();
        }
    }
}