package servidor;

import java.io.*;
import java.net.*;
import java.util.*;

public class Servidor {
    public static void main(String[] args) {
        new Servidor(8888);  // Inicia el servidor en el puerto 8888
    }

    private Map<Integer, Ticket> tickets; // Mapa para almacenar los tickets
    private int ticketIdCounter;          // Contador para generar ID de tickets

    public Servidor(int puerto) {
        tickets = new HashMap<>();
        ticketIdCounter = 1; // Comienza con ID 1 para los tickets

        try {
            ServerSocket serverSocket = new ServerSocket(puerto);
            System.out.println("Servidor iniciado en el puerto " + puerto);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Cliente conectado: " + socket.getInetAddress());
                manejarCliente(socket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void manejarCliente(Socket socket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);  // Auto-flush activado
        ) {
            String estado = "autenticado"; // Directamente autenticado, sin verificar

            while (true) {
                String linea = in.readLine();
                if (linea == null) {
                    System.out.println("Cliente desconectado");
                    break;
                }
                System.out.println("Mensaje recibido: " + linea);

                switch (estado) {
                    case "autenticado":
                        if (linea.equals("cerrarSesion")) {
                            estado = "cerrandoSesion";
                            out.println("Confirmar cierre de sesión");
                        } else if (linea.startsWith("crearTicket")) {
                            estado = "creandoTicket";
                            crearTicket(linea, out);
                        } else if (linea.startsWith("asignarTicket")) {
                            estado = "asignandoTicket";
                            asignarTicket(linea, out);
                        } else if (linea.startsWith("cambiarEstado")) {
                            estado = "cambiandoEstado";
                            cambiarEstadoTicket(linea, out);
                        } else if (linea.startsWith("consultarTicket")) {
                            estado = "consultandoTicket";
                            consultarTicket(linea, out);
                        } else {
                            out.println("Comando no reconocido.");
                        }
                        break;

                    case "cerrandoSesion":
                        if (linea.equals("confirmarCierre")) {
                            out.println("Sesion Cerrada con éxito");
                            System.out.println("Sesión cerrada correctamente");
                            socket.close(); // Cierra el socket y termina la conexión
                            return;
                        } else {
                            out.println("Comando no reconocido. La sesión está cerrando.");
                        }
                        break;

                    default:
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Crear un nuevo ticket
    private void crearTicket(String mensaje, PrintWriter out) {
        String[] campos = mensaje.split(" ");
        if (campos.length == 5) {
            String titulo = campos[1];
            String descripcion = campos[2];
            String prioridad = campos[3];

            Ticket nuevoTicket = new Ticket(ticketIdCounter++, titulo, descripcion, prioridad, "OPEN");
            tickets.put(nuevoTicket.getId(), nuevoTicket);
            out.println("Ticket creado con éxito: ID " + nuevoTicket.getId());
            System.out.println("Ticket creado: " + nuevoTicket);
        } else {
            out.println("Formato incorrecto para crear ticket");
        }
    }

    // Asignar un ticket a un usuario
    private void asignarTicket(String mensaje, PrintWriter out) {
        String[] campos = mensaje.split(" ");
        if (campos.length == 3) {
            int ticketId = Integer.parseInt(campos[1]);
            String asignadoA = campos[2];

            Ticket ticket = tickets.get(ticketId);
            if (ticket != null && ticket.getEstado().equals("OPEN")) {
                ticket.setAsignadoA(asignadoA);
                ticket.setEstado("ASSIGNED");
                out.println("Ticket asignado con éxito a " + asignadoA);
                System.out.println("Ticket asignado: " + ticket);
            } else {
                out.println("Error: Ticket no encontrado o ya no está en estado OPEN");
            }
        } else {
            out.println("Formato incorrecto para asignar ticket");
        }
    }

    // Cambiar el estado de un ticket
    private void cambiarEstadoTicket(String mensaje, PrintWriter out) {
        String[] campos = mensaje.split(" ");
        if (campos.length == 3) {
            int ticketId = Integer.parseInt(campos[1]);
            String nuevoEstado = campos[2];

            Ticket ticket = tickets.get(ticketId);
            if (ticket != null) {
                if (nuevoEstado.equals("IN_PROGRESS") && ticket.getEstado().equals("ASSIGNED")) {
                    ticket.setEstado("IN_PROGRESS");
                    out.println("Estado del ticket actualizado a IN_PROGRESS");
                } else if (nuevoEstado.equals("RESOLVED") && ticket.getEstado().equals("IN_PROGRESS")) {
                    ticket.setEstado("RESOLVED");
                    out.println("Estado del ticket actualizado a RESOLVED");
                } else if (nuevoEstado.equals("CLOSED") && ticket.getEstado().equals("RESOLVED")) {
                    ticket.setEstado("CLOSED");
                    out.println("Estado del ticket actualizado a CLOSED");
                } else {
                    out.println("Transición de estado no permitida");
                }
                System.out.println("Ticket actualizado: " + ticket);
            } else {
                out.println("Ticket no encontrado");
            }
        } else {
            out.println("Formato incorrecto para cambiar el estado del ticket");
        }
    }

    // Consultar un ticket
    private void consultarTicket(String mensaje, PrintWriter out) {
        String[] campos = mensaje.split(" ");
        if (campos.length == 2) {
            int ticketId = Integer.parseInt(campos[1]);

            Ticket ticket = tickets.get(ticketId);
            if (ticket != null) {
                out.println("Ticket encontrado: " + ticket);
                System.out.println("Consulta de ticket: " + ticket);
            } else {
                out.println("Ticket no encontrado");
            }
        } else {
            out.println("Formato incorrecto para consultar ticket");
        }
    }

    // Clase para representar un ticket
    class Ticket {
        private int id;
        private String titulo;
        private String descripcion;
        private String prioridad;
        private String estado;
        private String asignadoA;

        public Ticket(int id, String titulo, String descripcion, String prioridad, String estado) {
            this.id = id;
            this.titulo = titulo;
            this.descripcion = descripcion;
            this.prioridad = prioridad;
            this.estado = estado;
            this.asignadoA = "No asignado";
        }

        public int getId() {
            return id;
        }

        public String getEstado() {
            return estado;
        }

        public void setEstado(String estado) {
            this.estado = estado;
        }

        public String getAsignadoA() {
            return asignadoA;
        }

        public void setAsignadoA(String asignadoA) {
            this.asignadoA = asignadoA;
        }

        @Override
        public String toString() {
            return "Ticket{id=" + id + ", titulo='" + titulo + "', descripcion='" + descripcion + "', prioridad='" + prioridad + "', estado='" + estado + "', asignadoA='" + asignadoA + "'}";
        }
    }
}