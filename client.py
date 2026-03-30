import socket

HOST = "127.0.0.1"
PORT = 5000


def enviar_mensaje(mensaje):
    client = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    client.connect((HOST, PORT))

    client.sendall((mensaje + "\n").encode())

    respuesta = client.recv(1024).decode()
    print("Respuesta:", respuesta)

    client.close()


while True:
    print("\n--- MENÚ CLIENTE ---")
    print("1. CREATE")
    print("2. GET")
    print("3. LIST")
    print("4. STATE")
    print("5. ASSIGN")
    print("6. COMMENT")
    print("7. REASSIGN")
    print("8. SALIR")

    opcion = input("Elige una opción: ")

    if opcion == "1":
        user = input("Usuario: ")
        title = input("Título: ")
        desc = input("Descripción: ")
        priority = input("Prioridad (LOW/MEDIUM/HIGH): ")

        mensaje = f"CREATE|user={user}|title={title}|desc={desc}|priority={priority}"
        enviar_mensaje(mensaje)

    elif opcion == "2":
        ticket = input("ID del ticket: ")

        mensaje = f"GET|ticket={ticket}"
        enviar_mensaje(mensaje)

    elif opcion == "3":
        print("1. Sin filtro")
        print("2. Filtrar por estado")
        print("3. Filtrar por asignado")

        subop = input("Elige opción: ")

        if subop == "1":
            mensaje = "LIST"

        elif subop == "2":
            state = input("Estado: ")
            mensaje = f"LIST|state={state}"

        elif subop == "3":
            assignee = input("Asignado: ")
            mensaje = f"LIST|assignee={assignee}"

        else:
            print("Opción inválida")
            continue

        enviar_mensaje(mensaje)
    elif opcion == "4":
        ticket = input("ID del ticket: ")
        new_state = input("Nuevo estado: ")

        mensaje = f"STATE|ticket={ticket}|new_state={new_state}"
        enviar_mensaje(mensaje)

    elif opcion == "5":
        ticket = input("ID del ticket: ")
        assignee = input("Asignar a: ")

        mensaje = f"ASSIGN|ticket={ticket}|assignee={assignee}"
        enviar_mensaje(mensaje)

    elif opcion == "6":
        ticket = input("ID del ticket: ")
        user = input("Usuario: ")
        text = input("Comentario: ")

        mensaje = f"COMMENT|ticket={ticket}|user={user}|text={text}"
        enviar_mensaje(mensaje)

    elif opcion == "7":
        ticket = input("ID del ticket: ")
        assignee = input("Nuevo asignado: ")

        mensaje = f"REASSIGN|ticket={ticket}|assignee={assignee}"
        enviar_mensaje(mensaje)

    elif opcion == "8":
        print("Saliendo del cliente...")
        break

    else:
        print("Opción no válida")
