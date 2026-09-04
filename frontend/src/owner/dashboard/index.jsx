import { Calendar, momentLocalizer } from "react-big-calendar";
import { Link } from "react-router-dom";
import { Button, Modal, ModalBody, ModalFooter, ModalHeader } from "reactstrap";
import moment from "moment";
import "react-big-calendar/lib/css/react-big-calendar.css";
import "../../static/css/owner/dashboard.css";
import { useState, useEffect } from "react";

require("moment/locale/es.js");

const localizer = momentLocalizer(moment);

export default function OwnerDashboard() {
  let [events, setEvents] = useState([]);
  let [plan, setPlan] = useState("");
  let [message, setMessage] = useState(null);
  let [modalShow, setModalShow] = useState(false);

  const jwt = JSON.parse(window.localStorage.getItem("jwt"));

  function handleShow() {
    setModalShow(!modalShow);
  }

  async function setUp() {
    const visits = await (
      await fetch(`/api/v1/visits/`, {
        headers: {
          Authorization: `Bearer ${jwt}`,
          "Content-Type": "application/json",
        },
      })
    ).json();
    if (visits.message) setMessage(visits.message);
    else {
      const events = visits.map((visit) => {
        const start = new Date(visit.datetime);
        let end = new Date(visit.datetime);
        end.setMinutes(start.getMinutes() + 30);
        return {
          visitId: Number(visit.id),
          petId: Number(visit.pet.id),
          start: start,
          end: end,
          title: `Visit for ${visit.pet.name} with Vet ${visit.vet.firstName} ${visit.vet.lastName}`,
          description: visit.description,
        };
      });
      setEvents(events);
    }

    const owner = await (
      await fetch(`/api/v1/plan`, {
        headers: {
          Authorization: `Bearer ${jwt}`,
        },
      })
    ).json();
    if (owner.message) setMessage(owner.message);
    else {
      setPlan(owner.clinic.plan);
    }
  }

  useEffect(() => {
    setUp();
  }, []);

  return (
    <>
      <div className="owner-dashboard-page-container">
        <h1 className="text-center dashboard-title">Dashboard</h1>
        {plan === "GOLD" ||
          (plan === "PLATINUM" && (
            <div style={{ height: `${600}px` }} className="calendar-container">
              <Calendar
                localizer={localizer}
                events={events}
                startAccessor="start"
                endAccessor="end"
                views={{
                  month: true,
                  work_week: true,
                  day: true,
                  agenda: true,
                }}
                onSelectEvent={(e) =>
                  (window.location.href = `/myPets/${e.petId}/visits/${e.visitId}`)
                }
              />
            </div>
          ))}
      </div>
      <Modal
        isOpen={modalShow}
        toggle={handleShow}
        backdrop="static"
        keyboard={false}
      >
        <ModalHeader>Error!</ModalHeader>
        <ModalBody>{message || ""}</ModalBody>
        <ModalFooter>
          <Button color="primary" tag={Link} to={`/`}>
            Back
          </Button>
        </ModalFooter>
      </Modal>
    </>
  );
}
