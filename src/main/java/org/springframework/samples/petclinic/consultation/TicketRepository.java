package org.springframework.samples.petclinic.consultation;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface TicketRepository extends CrudRepository<Ticket, Integer> {

	@Query("SELECT t FROM Ticket t WHERE t.consultation.id = :consultationId ORDER BY t.creationDate")
	public List<Ticket> findTicketsByConsultation(@Param("consultationId") int consultationId);

}
