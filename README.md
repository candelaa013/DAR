# DAR - Gestión de Incidencias (Tickets)
Este proyecto implementa un sistema cliente-servidor para la gestión de incidencias (tickets) en red.
Permite crear, consultar y gestionar tickets mediante un protocolo de aplicación propio.

## Funcionalidades
- Crear incidencias (CREATE)
- Consultar incidencias (GET)
- Listar incidencias (LIST)
- Cambiar estado (STATE)
- Asignar responsable (ASSIGN)
- Reasignar responsable (REASSIGN)
- Añadir comentarios (COMMENT)
  
## Protocolo
El sistema utiliza un protocolo de aplicación basado en mensajes de texto estructurados.

Se han definido:
- Formato de mensajes mediante ABNF
- Tipos de operaciones
- Gestión de errores

## Arquitectura
- Cliente → realiza peticiones
- Servidor → procesa solicitudes y gestiona los tickets
- Comunicación mediante sockets TCP

## Comandos a ejecutar
### Servidor
python3 server.py
### Cliente
python3 client.py
