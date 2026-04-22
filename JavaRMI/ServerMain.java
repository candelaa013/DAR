package com.tickets.server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerMain {
    public static void main(String[] args) {
        try {
            // 1. Creamos la instancia del servicio
            TicketServiceImpl servicio = new TicketServiceImpl();
            
            // 2. Iniciamos el Registro RMI en el puerto 1099
            Registry registry = LocateRegistry.createRegistry(1099);
            
            // 3. Registramos el servicio con un nombre único
            registry.rebind("TicketService", servicio);
            
            System.out.println(">>> Servidor de Tickets (RMI) funcionando correctamente.");
            System.out.println(">>> Esperando llamadas de clientes...");
            
        } catch (Exception e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}