package com.tickets.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

/**
 * Interfaz que define las operaciones del sistema de tickets.
 * Sustituye al protocolo de texto (CREATE, GET, LIST...) de la P1.
 */
public interface ITicketService extends Remote {
    
    // CREATE -> crearTicket
    String crearTicket(String user, String title, String desc, String priority) throws RemoteException;
    
    // GET -> consultarTicket
    Map<String, Object> consultarTicket(int id) throws RemoteException;
    
    // LIST -> listarTickets
    List<Integer> listarTickets(String estado, String asignado) throws RemoteException;
    
    // STATE -> cambiarEstado
    boolean cambiarEstado(int id, String nuevoEstado) throws RemoteException;
    
    // ASSIGN/REASSIGN -> asignarTicket
    boolean asignarTicket(int id, String responsable) throws RemoteException;
    
    // COMMENT -> añadirComentario
    void añadirComentario(int id, String user, String texto) throws RemoteException;
}