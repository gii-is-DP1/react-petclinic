package org.springframework.samples.petclinic.consultation;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class TicketEventPublisher {

	private final SimpMessagingTemplate broker;

	public TicketEventPublisher(SimpMessagingTemplate broker) {
		this.broker = broker;
	}

	// Solo publicamos cuando la transacción se ha confirmado
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void onTicketEvent(TicketEvent event) {
		String topic = "/topic/consultations/" + event.consultationId() + "/tickets";
		broker.convertAndSend(topic, event);
	}
}
