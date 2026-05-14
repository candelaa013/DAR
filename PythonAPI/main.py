from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import Optional, List

app = FastAPI(
    title="API REST de Gestión de Tickets",
    description="Servicio REST para gestionar tickets de soporte",
    version="1.0.0"
)

tickets = []
next_ticket_id = 1


class TicketCreate(BaseModel):
    user: str
    title: str
    desc: str
    priority: str


class StateUpdate(BaseModel):
    new_state: str


class AssignUpdate(BaseModel):
    assignee: str


class CommentCreate(BaseModel):
    user: str
    text: str


@app.get("/")
def root():
    return {"mensaje": "API de tickets funcionando"}


@app.post("/tickets", status_code=201)
def crear_ticket(ticket_data: TicketCreate):
    global next_ticket_id

    ticket = {
        "id": next_ticket_id,
        "user": ticket_data.user,
        "title": ticket_data.title,
        "desc": ticket_data.desc,
        "priority": ticket_data.priority,
        "state": "OPEN",
        "assignee": None,
        "comments": []
    }

    tickets.append(ticket)
    next_ticket_id += 1

    return ticket


@app.get("/tickets/{ticket_id}")
def consultar_ticket(ticket_id: int):
    for ticket in tickets:
        if ticket["id"] == ticket_id:
            return ticket

    raise HTTPException(status_code=404, detail="Ticket no encontrado")


@app.get("/tickets")
def listar_tickets(state: Optional[str] = None, assignee: Optional[str] = None):
    resultado = tickets

    if state is not None:
        resultado = [t for t in resultado if t["state"] == state]

    if assignee is not None:
        resultado = [t for t in resultado if t["assignee"] == assignee]

    return {
        "count": len(resultado),
        "items": resultado
    }


@app.patch("/tickets/{ticket_id}/estado")
def cambiar_estado(ticket_id: int, data: StateUpdate):
    ticket = None

    for t in tickets:
        if t["id"] == ticket_id:
            ticket = t
            break

    if ticket is None:
        raise HTTPException(status_code=404, detail="Ticket no encontrado")

    transiciones_validas = {
        "OPEN": ["ASSIGNED"],
        "ASSIGNED": ["IN_PROGRESS"],
        "IN_PROGRESS": ["RESOLVED"],
        "RESOLVED": ["CLOSED"]
    }

    estado_actual = ticket["state"]
    nuevo_estado = data.new_state

    if estado_actual in transiciones_validas and nuevo_estado in transiciones_validas[estado_actual]:
        ticket["state"] = nuevo_estado
        return {
            "ticket": ticket_id,
            "old_state": estado_actual,
            "new_state": nuevo_estado
        }

    raise HTTPException(status_code=400, detail="Transición de estado no permitida")


@app.patch("/tickets/{ticket_id}/responsable")
def asignar_ticket(ticket_id: int, data: AssignUpdate):
    for ticket in tickets:
        if ticket["id"] == ticket_id:
            old_assignee = ticket["assignee"]
            ticket["assignee"] = data.assignee
            return {
                "ticket": ticket_id,
                "old_assignee": old_assignee,
                "new_assignee": data.assignee
            }

    raise HTTPException(status_code=404, detail="Ticket no encontrado")


@app.post("/tickets/{ticket_id}/comentarios")
def añadir_comentario(ticket_id: int, data: CommentCreate):
    for ticket in tickets:
        if ticket["id"] == ticket_id:
            comentario = {
                "user": data.user,
                "text": data.text
            }
            ticket["comments"].append(comentario)
            return {
                "ticket": ticket_id,
                "message": "Comentario añadido correctamente"
            }

    raise HTTPException(status_code=404, detail="Ticket no encontrado")