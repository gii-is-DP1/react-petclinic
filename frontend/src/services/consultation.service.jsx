import { Link } from "react-router-dom";
import { Button, ButtonGroup } from "reactstrap";
import deleteFromList from "../util/deleteFromList";

class ConsultationService {
    getConsultationList([consultations, setConsultations], [filtered, setFiltered], [alerts, setAlerts], setMessage, setVisible, plan = null) {
        let displayedConsultations;
        if (filtered.length > 0) displayedConsultations = filtered;
        else displayedConsultations = consultations;
        return displayedConsultations.map((c) => {
            return (
                <tr key={c.id}>
                    <td>{c.title}</td>
                    <td>{c.status}</td>
                    {!plan ?
                        <td>{c.owner.user.username}</td> : <></>
                    }
                    <td>{c.pet.name}</td>
                    <td>{c.owner.clinic.name}</td>
                    <td>{c.isClinicComment ? "Clinic Owner" : "Clinic Vet"}</td>
                    <td>{(new Date(c.creationDate)).toLocaleString()}</td>
                    <td>
                        <ButtonGroup>
                            <Button aria-label={"details-" + c.id} size="sm" color="info" tag={Link}
                                to={`/consultations/${c.id}/tickets`}>
                                Details
                            </Button>
                            {!plan || plan === "PLATINUM" ?
                                <Button aria-label={"edit-" + c.id} size="sm" color="primary" tag={Link}
                                    to={"/consultations/" + c.id}>
                                    Edit
                                </Button> :
                                <></>
                            }
                            {!plan ?
                                <Button aria-label={"delete-" + c.id} size="sm" color="danger"
                                    onClick={() => deleteFromList(`/api/v1/consultations/${c.id}`, c.id, [consultations, setConsultations],
                                        [alerts, setAlerts], setMessage, setVisible, { filtered: filtered, setFiltered: setFiltered })}>
                                    Delete
                                </Button> :
                                <></>
                            }
                        </ButtonGroup>
                    </td>
                </tr>
            );
        });
    }
}
const consultationService = new ConsultationService();

export default consultationService;