package org.springframework.samples.petclinic.consultation;

// Mensaje que viaja por el topic de cada consulta
public record TicketEvent(TicketEventType type, Integer consultationId,
		Integer ticketId, Ticket ticket) {

	public enum TicketEventType { CREATED, UPDATED, DELETED }

	public static TicketEvent of(TicketEventType type, Ticket ticket) {
		return new TicketEvent(type, ticket.getConsultation().getId(),
				ticket.getId(), type == TicketEventType.DELETED ? null : ticket);
	}
}
