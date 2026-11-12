/** Aplica un TicketEvent a la lista de tickets (función pura). */
export default function applyTicketEvent(tickets, event) {
  switch (event.type) {
    case "CREATED": // evitamos duplicados si ya lo añadimos al hacer POST
      return tickets.some((t) => t.id === event.ticketId)
        ? tickets
        : [...tickets, event.ticket];
    case "UPDATED":
      return tickets.map((t) => (t.id === event.ticketId ? event.ticket : t));
    case "DELETED":
      return tickets.filter((t) => t.id !== event.ticketId);
    default:
      return tickets;
  }
}
