import {
  Button,
  ButtonGroup,
  Col,
  Container,
  Input,
  Row,
  Table,
} from "reactstrap";
import { Link } from "react-router-dom";
import { useEffect, useState } from "react";

export default function OwnerConsultationList() {
  let [consultations, setConsultations] = useState([]);
  let [filtered, setFiltered] = useState(null);
  let [filter, setFilter] = useState("");
  let [search, setSearch] = useState("");
  let [plan, setPlan] = useState("");
  let [message, setMessage] = useState(null);

  const jwt = JSON.parse(window.localStorage.getItem("jwt"));

  function handleClear(event) {
    setFiltered([...consultations]);
    setFilter("");
    setSearch("");
  }

  function handleFilter(event) {
    const value = event.target.value;
    let filteredConsultations;

    if (value === "") {
      if (search !== "")
        filteredConsultations = [...consultations].filter((i) =>
          i.pet.name.toLowerCase().includes(search)
        );
      else filteredConsultations = [...consultations];
    } else {
      if (search !== "")
        filteredConsultations = [...consultations].filter(
          (i) => i.status === value && i.pet.name.toLowerCase().includes(search)
        );
      else
        filteredConsultations = [...consultations].filter(
          (i) => i.status === value
        );
    }
    setFiltered(filteredConsultations);
    setFilter(value);
  }

  function getConsultationList(consultations, plan) {
    return consultations.map((c) => {
      return (
        <tr key={c.id}>
          <td>{c.title}</td>
          <td>{c.status}</td>
          <td>{c.pet?.name}</td>
          <td>{c.isClinicComment ? "Clinic Owner" : "Clinic Vet"}</td>
          <td>{new Date(c.creationDate).toLocaleString()}</td>
          <td>
            <ButtonGroup>
              <Button
                size="sm"
                color="info"
                tag={Link}
                to={`/consultations/${c.id}/tickets`}
              >
                Details
              </Button>
              {plan === "PLATINUM" ? (
                <Button
                  size="sm"
                  color="primary"
                  tag={Link}
                  to={"/consultations/" + c.id}
                >
                  Edit
                </Button>
              ) : (
                <></>
              )}
            </ButtonGroup>
          </td>
        </tr>
      );
    });
  }

  function handleChange(event) {
    const value = event.target.value;
    let filteredConsultations;

    if (value === "") {
      if (filter !== "")
        filteredConsultations = [...consultations].filter(
          (i) => i.status === filter
        );
      else filteredConsultations = [...consultations];
    } else {
      if (filter !== "")
        filteredConsultations = [...consultations].filter(
          (i) => i.status === filter && i.pet.name.toLowerCase().includes(value)
        );
      else
        filteredConsultations = [...consultations].filter((i) =>
          i.pet.name.toLowerCase().includes(value)
        );
    }

    setFiltered(filteredConsultations);
    setSearch(value);
  }

  async function setUp() {
    const consultations = await (
      await fetch("/api/v1/consultations", {
        headers: {
          Authorization: `Bearer ${jwt}`,
          "Content-Type": "application/json",
        },
      })
    ).json();

    setConsultations(consultations);
    setFiltered(consultations);

    const owner = await (
      await fetch(`/api/v1/plan`, {
        headers: {
          Authorization: `Bearer ${jwt}`,
        },
      })
    ).json();
    if (owner.message) setMessage(owner.message);
    else setPlan(owner.clinic.plan);
  }

  useEffect(() => {
    setUp();
  }, []);

  useEffect(() => {}, [filtered]);

  return (
    <div>
      <Container style={{ marginTop: "15px" }} fluid>
        <h1 className="text-center">Consultations</h1>
        <Row className="row-cols-auto g-3 align-items-center">
          <Col>
            {plan === "PLATINUM" ? (
              <Button color="success" tag={Link} to="/consultations/new">
                Add Consultation
              </Button>
            ) : (
              <></>
            )}
            <Button color="link" onClick={handleFilter} value="PENDING">
              Pending
            </Button>
            <Button color="link" onClick={handleFilter} value="ANSWERED">
              Answered
            </Button>
            <Button color="link" onClick={handleFilter} value="CLOSED">
              Closed
            </Button>
            <Button color="link" onClick={handleFilter} value="">
              All
            </Button>
          </Col>
          <Col className="col-sm-3">
            <Input
              type="search"
              placeholder="Introduce a pet name to search by it"
              value={search || ""}
              onChange={handleChange}
            />
          </Col>
          <Col className="col-sm-3">
            <Button color="link" onClick={handleClear}>
              Clear All
            </Button>
          </Col>
        </Row>
        <Table className="mt-4">
          <thead>
            <tr>
              <th>Title</th>
              <th>Status</th>
              <th>Pet</th>
              <th>Sent To</th>
              <th>Creation Date</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {filtered
              ? getConsultationList(filtered, plan)
              : getConsultationList(consultations, plan)}
          </tbody>
        </Table>
      </Container>
    </div>
  );
}
