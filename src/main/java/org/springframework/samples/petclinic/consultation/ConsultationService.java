package org.springframework.samples.petclinic.consultation;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.exceptions.AccessDeniedException;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.samples.petclinic.exceptions.UpperPlanFeatureException;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.clinic.PricingPlan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConsultationService {

	private TicketRepository ticketRepository;
	private ConsultationRepository consultationRepository;

	@Autowired
	public ConsultationService(TicketRepository ticketRepository, ConsultationRepository consultationRepository) {
		this.ticketRepository = ticketRepository;
		this.consultationRepository = consultationRepository;
	}

	@Transactional(readOnly = true)
	public Iterable<Consultation> findAll() throws DataAccessException {
		return consultationRepository.findAllByOrderByCreationDateDesc();
	}

	@Transactional(readOnly = true)
	public Iterable<Consultation> findAllConsultationsByOwner(int ownerId) throws DataAccessException {
		return consultationRepository.findConsultationsByOwner(ownerId);
	}

	@Transactional(readOnly = true)
	public Consultation findConsultationById(int id) throws DataAccessException {
		return this.consultationRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Consultation", "ID", id));
	}

	@Transactional(readOnly = true)
	public List<Consultation> findAllByClinicOwnerUserId(int userId) throws DataAccessException {
		return this.consultationRepository.findAllByClinicOwnerUserId(userId);
	}

	@Transactional(readOnly = true)
	public List<Consultation> findAllByClinicId(int clinicId) throws DataAccessException {
		System.out.println("LLEGA AL SERVICE");
		System.out.println(clinicId);
		return this.consultationRepository.findAllByClinicId(clinicId);
	}

	@Transactional
	public Consultation saveConsultation(Consultation consultation) throws DataAccessException {
		consultationRepository.save(consultation);
		return consultation;
	}

	@Transactional
	public Consultation updateConsultation(Consultation consultation, int id) throws DataAccessException {
		Consultation toUpdate = findConsultationById(id);
		BeanUtils.copyProperties(consultation, toUpdate, "id", "creationDate", "owner", "pet");
		return saveConsultation(toUpdate);
	}

	@Transactional
	public void deleteConsultation(int id) throws DataAccessException {
		Consultation toDelete = findConsultationById(id);
		this.consultationRepository.delete(toDelete);
	}

	@Transactional(readOnly = true)
	public Iterable<Ticket> findAllTicketsByConsultation(int consultationId) throws DataAccessException {
		return ticketRepository.findTicketsByConsultation(consultationId);
	}

	@Transactional(readOnly = true)
	public Ticket findTicketById(int id) throws DataAccessException {
		return this.ticketRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Ticket", "ID", id));
	}

	@Transactional
	public Ticket saveTicket(Ticket ticket) throws DataAccessException {
		this.ticketRepository.save(ticket);
		return ticket;
	}

	@Transactional
	public Ticket updateTicket(Ticket ticket, int id) throws DataAccessException {
		Ticket toUpdate = findTicketById(id);
		BeanUtils.copyProperties(ticket, toUpdate, "id", "creationDate", "consultation", "user");
		return saveTicket(toUpdate);
	}

	@Transactional
	public void deleteTicket(int id) throws DataAccessException {
		Ticket toDelete = findTicketById(id);
		this.ticketRepository.delete(toDelete);
		updateConsultationStatus(toDelete.getConsultation());
	}

	@Transactional(readOnly = true)
	public void checkLastTicketAndStatus(Consultation consultation, Ticket ticket) {
		List<Ticket> tickets = (List<Ticket>) findAllTicketsByConsultation(consultation.getId());
		if (!tickets.get(tickets.size() - 1).getId().equals(ticket.getId()))
			throw new AccessDeniedException("You can only update or delete the last ticket in a consultation!");
		if (consultation.getStatus().equals(ConsultationStatus.CLOSED))
			throw new AccessDeniedException("This consultation is closed!");
	}

	@Transactional
	public Ticket updateOwnerTicket(Ticket ticket, Integer targetId, Owner owner) {
		if (owner.getClinic().getPlan().equals(PricingPlan.PLATINUM)) {
			return updateTicket(ticket, targetId);
		} else
			throw new UpperPlanFeatureException(PricingPlan.PLATINUM, owner.getClinic().getPlan());
	}

	@Transactional
	public void deleteOwnerTicket(Ticket ticket, Owner owner) {
		if (owner.getClinic().getPlan().equals(PricingPlan.PLATINUM)) {
			deleteTicket(ticket.getId());
		} else
			throw new UpperPlanFeatureException(PricingPlan.PLATINUM, owner.getClinic().getPlan());
	}

	@Transactional
	public void deleteAdminTicket(Ticket ticket, Consultation consultation) {
		List<Ticket> tickets = (List<Ticket>) findAllTicketsByConsultation(consultation.getId());
		for (Ticket t : tickets) {
			if (t.getCreationDate().isAfter(ticket.getCreationDate()))
				this.ticketRepository.delete(t);
		}
		this.ticketRepository.delete(ticket);
		updateConsultationStatus(consultation);

	}

	private void updateConsultationStatus(Consultation consultation) {
		List<Ticket> tickets = (List<Ticket>) findAllTicketsByConsultation(consultation.getId());
		if (!tickets.isEmpty()) {
			if (tickets.get(tickets.size() - 1).getUser().getAuthority().getAuthority().equals("OWNER"))
				consultation.setStatus(ConsultationStatus.PENDING);
			else
				consultation.setStatus(ConsultationStatus.ANSWERED);
			saveConsultation(consultation);
		} else
			consultation.setStatus(ConsultationStatus.PENDING);
	}

	public void checkIfTicketInConsultation(Consultation consultation, Ticket ticket) {
		List<Ticket> tickets = this.ticketRepository.findTicketsByConsultation(consultation.getId());
		if (!tickets.contains(ticket))
			throw new AccessDeniedException("The ticket " + ticket.getId() + " doesn't belong to the consultation "
					+ consultation.getId() + ".");
	}

	public Map<String, Object> getOwnerConsultationsStats(int ownerId) {
		Map<String, Object> res = new HashMap<>();
		Integer countAll = this.consultationRepository.countAllByOwner(ownerId);
		int pets = this.consultationRepository.countAllPetsOfOwner(ownerId);
		if (countAll > 0) {
			Map<String, Integer> consultationsByYear = getConsultationsByYear(ownerId);
			res.put("consultationsByYear", consultationsByYear);

			if (pets > 1) {
				Map<String, Integer> consultationsByPet = getConsultationsByPet(ownerId);
				Double avgConsultationsByPet = (double) countAll / pets;
				res.put("consultationsByPet", consultationsByPet);
				res.put("avgConsultationsByPet", avgConsultationsByPet);
			}

			int years = LocalDate.now().getYear() - this.consultationRepository.getYearOfFirstConsultation(ownerId);
			if (years >= 1) {
				Double avgConsultationsByYear = (double) countAll / (years + 1);
				res.put("avgConsultationsByYear", avgConsultationsByYear);
			}
		}

		res.put("totalConsultations", countAll);

		return res;
	}

	public Map<String, Object> getAdminConsultationsStats() {
		Map<String, Object> res = new HashMap<>();
		Integer countAll = this.consultationRepository.countAll();
		if (this.consultationRepository.countAllPlatinums() > 0) {
			Double avgConsultationsByPlatinum = (double) countAll / this.consultationRepository.countAllPlatinums();

			res.put("avgConsultationsByPlatinum", avgConsultationsByPlatinum);
		}
		Double avgConsultationsByOwners = (double) countAll / this.consultationRepository.countAllOwners();

		res.put("totalConsultations", countAll);
		res.put("avgConsultationsByOwners", avgConsultationsByOwners);

		return res;
	}

	private Map<String, Integer> getConsultationsByYear(int userId) {
		Map<String, Integer> unsortedConsultationsByYear = new HashMap<>();
		this.consultationRepository.countConsultationsGroupedByYear(userId).forEach(m -> {
			String key = m.get("year").toString();
			Integer value = m.get("consultations");
			unsortedConsultationsByYear.put(key, value);
		});
		return unsortedConsultationsByYear.entrySet().stream()
				.sorted(Map.Entry.comparingByKey(Comparator.reverseOrder())).collect(Collectors.toMap(Map.Entry::getKey,
						Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
	}

	private Map<String, Integer> getConsultationsByPet(int userId) {
		Map<String, Integer> unsortedConsultationsByPet = new HashMap<>();
		this.consultationRepository.countConsultationsGroupedByPet(userId).forEach(m -> {
			String key = m.get("pet");
			Integer value = Integer.parseInt(m.get("consultations"));
			unsortedConsultationsByPet.put(key, value);
		});
		return unsortedConsultationsByPet;
	}
}

