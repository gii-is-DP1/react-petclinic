import { useEffect, useRef, useState } from 'react';
import ticketService from '../../services/ticket.service';
import tokenService from '../../services/token.service';
import getDeleteAlertsOrModal from '../../util/getDeleteAlertsOrModal';
import getErrorModal from '../../util/getErrorModal';
import getIdFromUrl from '../../util/getIdFromUrl';
import useFetchState from '../../util/useFetchState';

const jwt = tokenService.getLocalAccessToken();

export default function TicketListAdmin() {
    const emptyTicket = {
        id: null,
        description: '',
    };
    const id = getIdFromUrl(2);
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [consultation, setConsultation] = useFetchState({}, `/api/v1/consultations/${id}`, jwt, setMessage, setVisible, id);
    const [tickets, setTickets] = useFetchState([], `/api/v1/consultations/${id}/tickets`, jwt, setMessage, setVisible);
    const [newTicket, setNewTicket] = useState(emptyTicket);
    const [alerts, setAlerts] = useState([]);

    const conversationRef = useRef(null);

    function handleChange(event) {
        const target = event.target;
        const value = target.value;
        const name = target.name;
        setNewTicket({ ...newTicket, [name]: value })
    }

    function handleSubmit(event) {
        event.preventDefault();

        fetch(`/api/v1/consultations/${id}/tickets`, {
            method: 'POST',
            headers: {
                "Authorization": `Bearer ${jwt}`,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(newTicket),
        })
            .then(response => response.json())
            .then(json => {
                if (json.message) {
                    setMessage(json.message);
                    setVisible(true);
                }
                else {
                    setTickets([...tickets, json]);
                    setNewTicket(emptyTicket);
                }
            })
            .catch((message) => alert(message));
    }

    function handleClose(event) {
        event.preventDefault();
        const aux = consultation;
        aux.status = "CLOSED"

        fetch(`/api/v1/consultations/${id}`, {
            method: 'PUT',
            headers: {
                "Authorization": `Bearer ${jwt}`,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(aux),
        })
            .then(response => response.json())
            .then(json => {
                if (json.message) {
                    setMessage(json.message);
                    setVisible(true);
                }
                else {
                    setConsultation({ ...consultation, status: "CLOSED" });
                    getDeleteAlertsOrModal({ message: "Consultation closed!" }, id, alerts, setAlerts, setMessage, setVisible)
                }
            }).catch((message) => alert(message));

    }

    useEffect(() => {
        if (conversationRef.current) {
          conversationRef.current.scrollTop = conversationRef.current.scrollHeight;
        }
      }, [tickets, newTicket]);

    const modal = getErrorModal(setVisible, visible, message);

    const ticketList = ticketService.getTicketList([tickets, setTickets], "ADMIN", [alerts, setAlerts], setMessage, setVisible);
    const ticketForm = ticketService.getTicketForm(newTicket, consultation.status, "ADMIN", handleChange, handleSubmit);
    const ticketClose = ticketService.getTicketCloseButton(consultation, handleClose)

    return (
        <div className="ticket-page">
                {ticketClose}
                {alerts.map((a) => a.alert)}
                {modal}
                <h3>{consultation.title}</h3>
                <div className="conversation-container" ref={conversationRef} style={{maxHeight: "65vh"}}>
                    {ticketList}
                </div>
                {ticketForm}
            </div>
            
    );
}
