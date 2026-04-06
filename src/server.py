import socket

HOST = "127.0.0.1"
PORT = 5000

tickets = []
next_ticket_id = 1

server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
server.bind((HOST, PORT))
server.listen(5)

print("Servidor escuchando...")

while True:
    conn, addr = server.accept()
    print("Cliente conectado:", addr)

    data = conn.recv(1024).decode().strip()
    print("Mensaje recibido:", data)

    partes = data.split("|")
    tipo = partes[0]

    campos = {}

    for parte in partes[1:]:
        if "=" in parte:
            clave, valor = parte.split("=", 1)
            campos[clave] = valor

    print("Tipo:", tipo)
    print("Campos:", campos)

    if tipo == "CREATE":
        obligatorios = ["user", "title", "desc", "priority"]
        falta = None

        for campo in obligatorios:
            if campo not in campos:
                falta = campo
                break

        if falta is not None:
            respuesta = f"ERROR|code=422|message=MISSING_FIELD|detail={falta}\n"
        else:
            ticket = {
                "id": next_ticket_id,
                "user": campos["user"],
                "title": campos["title"],
                "desc": campos["desc"],
                "priority": campos["priority"],
                "state": "OPEN",
                "assignee": None,
                "comments": []
            }

            tickets.append(ticket)
            respuesta = f"OK|code=201|ticket={next_ticket_id}|state=OPEN\n"
            print("Ticket creado:", ticket)
            next_ticket_id += 1

    elif tipo == "GET":
        if "ticket" not in campos:
            respuesta = "ERROR|code=422|message=MISSING_FIELD|detail=ticket\n"
        else:
            try:
                ticket_id = int(campos["ticket"])
                encontrado = None

                for ticket in tickets:
                    if ticket["id"] == ticket_id:
                        encontrado = ticket
                        break

                if encontrado is None:
                    respuesta = "ERROR|code=404|message=TICKET_NOT_FOUND\n"
                else:
                    respuesta = (
                        f"OK|code=200|ticket={encontrado['id']}"
                        f"|user={encontrado['user']}"
                        f"|title={encontrado['title']}"
                        f"|desc={encontrado['desc']}"
                        f"|priority={encontrado['priority']}"
                        f"|state={encontrado['state']}"
                        f"|assignee={encontrado['assignee']}"
                        f"|comments_count={len(encontrado['comments'])}\n"
                    )

            except ValueError:
                respuesta = "ERROR|code=400|message=INVALID_VALUE|detail=ticket\n"

    elif tipo == "LIST":
        resultado = tickets

        # filtro por estado
        if "state" in campos:
            resultado = [t for t in resultado if t["state"] == campos["state"]]

        # filtro por asignado
        if "assignee" in campos:
            resultado = [t for t in resultado if t["assignee"] == campos["assignee"]]

        if len(resultado) == 0:
            respuesta = "OK|code=200|count=0|items=\n"
        else:
            ids = [str(t["id"]) for t in resultado]
            lista_ids = ",".join(ids)
            respuesta = f"OK|code=200|count={len(resultado)}|items={lista_ids}\n" 

    elif tipo == "STATE":
        if "ticket" not in campos:
            respuesta = "ERROR|code=422|message=MISSING_FIELD|detail=ticket\n"

        elif "new_state" not in campos:
            respuesta = "ERROR|code=422|message=MISSING_FIELD|detail=new_state\n"

        else:
            try:
                ticket_id = int(campos["ticket"])
                new_state = campos["new_state"]

                encontrado = None

                for ticket in tickets:
                    if ticket["id"] == ticket_id:
                        encontrado = ticket
                        break

                if encontrado is None:
                    respuesta = "ERROR|code=404|message=TICKET_NOT_FOUND\n"
                else:
                    estado_actual = encontrado["state"]

                    transiciones_validas = {
                        "OPEN": ["ASSIGNED"],
                        "ASSIGNED": ["IN_PROGRESS"],
                        "IN_PROGRESS": ["RESOLVED"],
                        "RESOLVED": ["CLOSED"]
                    }

                    if estado_actual in transiciones_validas and new_state in transiciones_validas[estado_actual]:
                        encontrado["state"] = new_state
                        respuesta = f"OK|code=200|ticket={ticket_id}|old_state={estado_actual}|new_state={new_state}\n"
                    else:
                        respuesta = "ERROR|code=409|message=INVALID_STATE_TRANSITION\n"

            except ValueError:
                respuesta = "ERROR|code=400|message=INVALID_VALUE|detail=ticket\n"

    elif tipo == "ASSIGN":
        if "ticket" not in campos:
            respuesta = "ERROR|code=422|message=MISSING_FIELD|detail=ticket\n"

        elif "assignee" not in campos:
            respuesta = "ERROR|code=422|message=MISSING_FIELD|detail=assignee\n"

        else:
            try:
                ticket_id = int(campos["ticket"])
                assignee = campos["assignee"]

                encontrado = None

                for ticket in tickets:
                    if ticket["id"] == ticket_id:
                        encontrado = ticket
                        break

                if encontrado is None:
                    respuesta = "ERROR|code=404|message=TICKET_NOT_FOUND\n"
                else:
                    encontrado["assignee"] = assignee
                    respuesta = f"OK|code=200|ticket={ticket_id}|assignee={assignee}\n"

            except ValueError:
                respuesta = "ERROR|code=400|message=INVALID_VALUE|detail=ticket\n"
    elif tipo == "REASSIGN":
        if "ticket" not in campos:
            respuesta = "ERROR|code=422|message=MISSING_FIELD|detail=ticket\n"

        elif "assignee" not in campos:
            respuesta = "ERROR|code=422|message=MISSING_FIELD|detail=assignee\n"

        else:
            try:
                ticket_id = int(campos["ticket"])
                new_assignee = campos["assignee"]

                encontrado = None

                for ticket in tickets:
                    if ticket["id"] == ticket_id:
                        encontrado = ticket
                        break

                if encontrado is None:
                    respuesta = "ERROR|code=404|message=TICKET_NOT_FOUND\n"
                else:
                    old_assignee = encontrado["assignee"]
                    encontrado["assignee"] = new_assignee

                    respuesta = (
                        f"OK|code=200|ticket={ticket_id}"
                        f"|old_assignee={old_assignee}"
                        f"|new_assignee={new_assignee}\n"
                    )

            except ValueError:
                respuesta = "ERROR|code=400|message=INVALID_VALUE|detail=ticket\n"

    elif tipo == "COMMENT":
        if "ticket" not in campos:
            respuesta = "ERROR|code=422|message=MISSING_FIELD|detail=ticket\n"

        elif "user" not in campos:
            respuesta = "ERROR|code=422|message=MISSING_FIELD|detail=user\n"

        elif "text" not in campos:
            respuesta = "ERROR|code=422|message=MISSING_FIELD|detail=text\n"

        else:
            try:
                ticket_id = int(campos["ticket"])
                user = campos["user"]
                text = campos["text"]

                encontrado = None

                for ticket in tickets:
                    if ticket["id"] == ticket_id:
                        encontrado = ticket
                        break

                if encontrado is None:
                    respuesta = "ERROR|code=404|message=TICKET_NOT_FOUND\n"
                else:
                    comentario = {
                        "user": user,
                        "text": text
                    }

                    encontrado["comments"].append(comentario)
                    respuesta = f"OK|code=200|ticket={ticket_id}|message=COMMENT_ADDED\n"

            except ValueError:
                respuesta = "ERROR|code=400|message=INVALID_VALUE|detail=ticket\n"

    else:
        respuesta = "ERROR|code=400|message=UNKNOWN_OPERATION\n"

    conn.sendall(respuesta.encode())
    conn.close()
