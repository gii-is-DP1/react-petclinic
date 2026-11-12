import applyTicketEvent from "./applyTicketEvent";

const t1 = { id: 1, description: "Hola" };
const t2 = { id: 2, description: "Adiós" };

describe("applyTicketEvent", () => {
  test("CREATED añade el ticket al final", () => {
    expect(applyTicketEvent([t1], { type: "CREATED", ticketId: 2, ticket: t2 })).toEqual([t1, t2]);
  });

  test("CREATED no duplica un ticket que ya está", () => {
    const tickets = [t1];
    expect(applyTicketEvent(tickets, { type: "CREATED", ticketId: 1, ticket: t1 })).toBe(tickets);
  });

  test("UPDATED sustituye el ticket", () => {
    const edited = { id: 1, description: "Editado" };
    expect(applyTicketEvent([t1, t2], { type: "UPDATED", ticketId: 1, ticket: edited })).toEqual([edited, t2]);
  });

  test("DELETED elimina el ticket", () => {
    expect(applyTicketEvent([t1, t2], { type: "DELETED", ticketId: 1, ticket: null })).toEqual([t2]);
  });

  test("un tipo desconocido no cambia la lista", () => {
    const tickets = [t1];
    expect(applyTicketEvent(tickets, { type: "OTRO" })).toBe(tickets);
  });
});
