package com.tickets.server;

import com.tickets.common.ITicketService;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class TicketServiceImpl extends UnicastRemoteObject implements ITicketService {

    private List<Map<String, Object>> tickets;
    private int nextTicketId;

    public TicketServiceImpl() throws RemoteException {
        super();
        this.tickets = new ArrayList<>();
        this.nextTicketId = 1;
    }

    @Override
    public synchronized String crearTicket(String user, String title, String desc, String priority) throws RemoteException {
        Map<String, Object> ticket = new HashMap<>();
        ticket.put("id", nextTicketId);
        ticket.put("user", user);
        ticket.put("title", title);
        ticket.put("desc", desc);
        ticket.put("priority", priority);
        ticket.put("state", "OPEN");
        ticket.put("assignee", "None");
        ticket.put("comments", new ArrayList<Map<String, String>>());

        tickets.add(ticket);
        return "OK|code=201|ticket=" + (nextTicketId++);
    }

    @Override
    public synchronized Map<String, Object> consultarTicket(int id) throws RemoteException {
        for (Map<String, Object> ticket : tickets) {
            if ((int) ticket.get("id") == id) {
                return ticket;
            }
        }
        return null;
    }

    @Override
    public synchronized List<Integer> listarTickets(String state, String assignee) throws RemoteException {
        List<Integer> resultado = new ArrayList<>();
        for (Map<String, Object> t : tickets) {
            boolean matches = true;
            if (state != null && !t.get("state").equals(state)) {
                matches = false;
            }
            if (assignee != null && !t.get("assignee").equals(assignee)) {
                matches = false;
            }
            if (matches) {
                resultado.add((Integer) t.get("id"));
            }
        }
        return resultado;
    }

    @Override
    public synchronized boolean cambiarEstado(int id, String nuevoEstado) throws RemoteException {
        Map<String, Object> t = consultarTicket(id);
        if (t == null) {
            return false;
        }

        String estadoActual = (String) t.get("state");

        // Ejemplo de lógica de transición (ajústala a tu diseño original)
        boolean esValido = false;
        if (estadoActual.equals("OPEN") && nuevoEstado.equals("ASSIGNED")) {
            esValido = true;
        } else if (estadoActual.equals("ASSIGNED") && nuevoEstado.equals("IN_PROGRESS")) {
            esValido = true;
        } else if (estadoActual.equals("IN_PROGRESS") && nuevoEstado.equals("RESOLVED")) {
            esValido = true;
        } else if (estadoActual.equals("RESOLVED") && nuevoEstado.equals("CLOSED")) {
            esValido = true;
        }

        if (esValido) {
            t.put("state", nuevoEstado);
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean asignarTicket(int id, String responsable) throws RemoteException {
        Map<String, Object> t = consultarTicket(id);
        if (t == null) {
            return false;
        }
        t.put("assignee", responsable);
        return true;
    }

    @Override
    public synchronized void añadirComentario(int id, String user, String texto) throws RemoteException {
        Map<String, Object> t = consultarTicket(id);
        if (t != null) {
            List<Map<String, String>> comentarios = (List<Map<String, String>>) t.get("comments");
            Map<String, String> nuevoComentario = new HashMap<>();
            nuevoComentario.put("user", user);
            nuevoComentario.put("text", texto);
            comentarios.add(nuevoComentario);
        }
    }
}
