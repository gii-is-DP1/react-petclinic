import { Link } from "react-router-dom";
import { Button, ButtonGroup, Card, CardBody, CardText, CardTitle, Col, Form, FormGroup, Input, Row } from "reactstrap";
import "../static/css/owner/ticketPage.css";
import deleteFromList from "../util/deleteFromList";

class TicketService {
    getTicketList([tickets, setTickets], auth, [alerts, setAlerts], setMessage, setVisible, plan = null) {
        return tickets.map((t, index) => {
            const status = t.consultation.status;
            const removeOwnerVet = () => deleteFromList(`/api/v1/consultations/${t.consultation.id}/tickets/${t.id}`, t.id, [tickets, setTickets],
                [alerts, setAlerts], setMessage, setVisible, { date: t.creationDate });
            const removeAdmin = () => deleteFromList(`/api/v1/consultations/${t.consultation.id}/tickets/${t.id}`, t.id, [tickets, setTickets],
                [alerts, setAlerts], setMessage, setVisible, { date: t.creationDate });
            const length = tickets.length;
            let buttons;
            if (auth === "OWNER") {
                buttons = index === length - 1 && plan === "PLATINUM" && t.user.authority.authority === "OWNER" && status !== "CLOSED" ?
                    <ButtonGroup>
                        <Button aria-label={"edit-" + t.id} size="sm" color="primary" tag={Link}
                            to={`consultations/${t.consultation.id}/tickets/${t.id}`}>
                            Edit
                        </Button>
                        <Button aria-label={"delete-" + t.id} size="sm" color="danger" onClick={removeOwnerVet}>
                            Delete
                        </Button>
                    </ButtonGroup> :
                    <></>;
            } else if (auth === "VET") {
                buttons = index === length - 1 && t.user.authority.authority === "VET" && status !== "CLOSED" ?
                    <ButtonGroup>
                        <Button aria-label={"edit-" + t.id} size="sm" color="primary" tag={Link}
                            to={`consultations/${t.consultation.id}/tickets/${t.id}`}>
                            Edit
                        </Button>
                        <Button aria-label={"delete-" + t.id} size="sm" color="danger" onClick={removeOwnerVet}>
                            Delete
                        </Button>
                    </ButtonGroup> :
                    <></>;
            } else {
                buttons = <ButtonGroup>
                    <Button aria-label={"edit-" + t.id} size="sm" color="primary" tag={Link}
                        to={`consultations/${t.consultation.id}/tickets/${t.id}`}>
                        Edit
                    </Button>
                    <Button aria-label={"delete-" + t.id} size="sm" color="danger" onClick={removeAdmin}>
                        Delete
                    </Button>
                </ButtonGroup>;
            }

            const alignment = t.user?.authority?.authority === "OWNER" ?
                "me-auto" :
                "ms-auto";
            const style = t.user?.authority?.authority === "OWNER" ?
                { maxWidth: "80%", backgroundColor: "#88FFFF" } :
                { maxWidth: "80%", backgroundColor: "#A6FF7D", alignSelf: "end" };

            return (
                <div key={t.id}>
                    <Card mb="3" aria-label={"ticket-" + t.id} className={alignment} style={style}>
                        <Row className="no-gutters">
                            <Col md="8">
                                <CardBody>
                                    <CardTitle tag="h5">
                                        {t.user.username} -&gt; {t.description}
                                    </CardTitle>
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

    getTicketForm(newTicket, status, auth, handleChange, handleSubmit) {
        if (auth === "ADMIN" || status !== "CLOSED")
            return (
                <div className="ticket-input-div">
                    <Form onSubmit={handleSubmit}>
                        <FormGroup>
                            <Input type="textarea" required name="description" id="description" value={newTicket.description || ''}
                                onChange={handleChange} />
                        </FormGroup>
                        <FormGroup>
                            <Button color="primary" type="submit">Save</Button>
                        </FormGroup>
                    </Form>
                </div>
            );
        else return <></>;
    }

    getTicketCloseButton(consultation, handleClose) {
        if (consultation.status !== "CLOSED") {
            return <Row style={{margin: "30px 0"}}>
                <Row>
                    <h2 className="text-center">Consultation Number {consultation.id}</h2>
                </Row>
                <Row>
                    <Button color="warning" onClick={handleClose} >
                        Close Consultation
                    </Button>
                </Row>
            </Row>
        } else
            return <h2 className="text-center">Consultation Number {consultation.id}</h2>


    }

}

const ticketService = new TicketService();

export default ticketService;