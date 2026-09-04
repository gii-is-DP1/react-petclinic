import "../../../../static/css/owner/ticketPage.css";
import { Link } from "react-router-dom";
import { useEffect, useRef, useState } from "react";
import {
  Button,
  ButtonGroup,
  Card,
  CardBody,
  CardText,
  CardTitle,
  Col,
  Form,
  FormGroup,
  Input,
  Row,
} from "reactstrap";
import tokenService from "../../../../services/token.service";

export default function VetConsultationTickets() {
  let [consultation, setConsultation] = useState({
    id: null,
    title: "",
    status: null,
  });

  let [tickets, setTickets] = useState([]);
  let [newTicket, setNewTicket] = useState({
    id: null,
    description: "",
  });

  let [message, setMessage] = useState(null);

  const conversationRef = useRef(null);

  const jwt = tokenService.getLocalAccessToken();
  let pathArray = window.location.pathname.split("/");
  const id = pathArray[2];

  function handleChange(event) {
    const target = event.target;
    const value = target.value;
    setNewTicket({
      ...newTicket,
      description: value,
    });
  }

  async function handleSubmit(event) {
    event.preventDefault();

    const response = await (
      await fetch(`/api/v1/consultations/${id}/tickets`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${jwt}`,
          Accept: "application/json",
          "Content-Type": "application/json",
        },
        body: JSON.stringify(newTicket),
      })
    ).json();
    if (response.message) setMessage(response.message);
    else {
      setTickets([...tickets, response]);
      setNewTicket({ id: null, description: "" });
    }
  }

  async function remove(ticketId) {
    await fetch(`/api/v1/consultations/${id}/tickets/${ticketId}`, {
      method: "DELETE",
      headers: {
        Authorization: `Bearer ${jwt}`,
        Accept: "application/json",
        "Content-Type": "application/json",
      },
    })
      .then((response) => {
        if (response.status === 200) {
          let updatedTickets = tickets.filter((i) => i.id !== ticketId);
          setTickets(updatedTickets);
        }
        return response.json();
      })
      .then(function (data) {
        alert(data.message);
      });
  }

  async function handleClose(event) {
    event.preventDefault();
    let consultationRequest = consultation;
    consultationRequest.status = "CLOSED";

    const response = await (
      await fetch(`/api/v1/consultations/${id}`, {
        method: "PUT",
        headers: {
          Authorization: `Bearer ${jwt}`,
          Accept: "application/json",
          "Content-Type": "application/json",
        },
        body: JSON.stringify(consultationRequest),
      })
    ).json();
    if (response.message) setMessage(response.message);
    else {
      setConsultation({
        ...consultation,
        status: "CLOSED",
      })
    }
  }

  async function setUp() {
    const consultation = await (
      await fetch(`/api/v1/consultations/${id}`, {
        headers: {
          Authorization: `Bearer ${jwt}`,
        },
      })
    ).json();
    if (consultation.message) setMessage(consultation.message);
    else setConsultation(consultation);
    if (!message) {
      const tickets = await (
        await fetch(`/api/v1/consultations/${id}/tickets`, {
          headers: {
            Authorization: `Bearer ${jwt}`,
          },
        })
      ).json();
      if (tickets.message) setMessage(tickets.message);
      else setTickets(tickets);
    }
  }

  useEffect(() => {
    setUp();
  }, []);

  useEffect(() => {}, [consultation.status]);

  function getTicketList(tickets, status) {
    
    const length = tickets.length;
        return tickets.map((t, index) => {
            const buttons = index === length - 1 && t.user.authority.authority === "VET" && status !== "CLOSED" ?
                <ButtonGroup>
                    <Button size="sm" color="primary" tag={Link}
                        to={`consultations/${t.consultation.id}/tickets/${t.id}`}>
                        Edit
                    </Button>
                    <Button size="sm" color="danger" onClick={() => remove(t.id)}>
                        Delete
                    </Button>
                </ButtonGroup> :
                <></>;
            const alignment = t.user?.authority?.authority === "OWNER" ?
                "me-auto" :
                "ms-auto";
            const style = t.user?.authority?.authority === "OWNER" ?
                { maxWidth: "80%", backgroundColor: "#88FFFF" } :
                { maxWidth: "80%", backgroundColor: "#A6FF7D", alignSelf: "end" };


            return (
                <div key={t.id}>
                    <Card mb="3" className={alignment} style={style}>
                        <Row className="no-gutters">
                            <Col md="8">
                                <CardBody>
                                    <CardTitle>{t.user.username}-&gt; {t.description}</CardTitle>
                                    {/* <CardText>{t.description}</CardText> */}
                                    <CardText><small className="text-muted">{new Date(t.creationDate).toLocaleString()}</small></CardText>
                                    {buttons}
                                </CardBody>
                            </Col>
                        </Row>
                    </Card>
                    <br></br>
                </div>
            );
        })
  }

  function getTicketInput(newTicket, status) {
    if (status !== "CLOSED")
      return (
        <div className="ticket-input-div">
          <Form onSubmit={handleSubmit}>
            <FormGroup>
              <Input
                type="textarea"
                required
                name="description"
                id="description"
                value={newTicket.description || ""}
                onChange={handleChange}
              />
            </FormGroup>
            <FormGroup>
              <Button color="primary" type="submit">
                Save
              </Button>{" "}
            </FormGroup>
          </Form>
        </div>
      );
    else return <></>;
  }

  useEffect(() => {
    if (conversationRef.current) {
      conversationRef.current.scrollTop = conversationRef.current.scrollHeight;
    }
  }, [tickets, newTicket]);

  if (message) return <h2 className="text-center">{message}</h2>;

  return (
    <div className="ticket-page">
      <h2 className="text-center">Consultation Number {consultation.id}</h2>
      <h3>{consultation.title}</h3>
      {consultation.status !== "CLOSED" && (
        <Button color="danger" onClick={handleClose} style={{marginBottom: "50px"}}>
          Close Consultation
        </Button>
      )}
      <div className="conversation-container" ref={conversationRef} style={{maxHeight: "65vh"}}>
        {getTicketList(tickets, consultation.status)}
      </div>
      {getTicketInput(newTicket, consultation.status)}
    </div>
  );
}
