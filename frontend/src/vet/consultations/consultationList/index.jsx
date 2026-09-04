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
import tokenService from "../../../services/token.service";

export default function VetConsultationList() {
  let [consultations, setConsultations] = useState([]);
  let [filtered, setFiltered] = useState(null);
  let [filter, setFilter] = useState("");
  let [search, setSearch] = useState("");

  const user = tokenService.getUser();
  const jwt = tokenService.getLocalAccessToken();

  function handleFilter(event) {
    const value = event.target.value;
    let filteredConsultations;

    if (value === "") {
      if (search !== "")
        filteredConsultations = [...consultations].filter((i) =>
          i.owner.user.username.includes(search)
        );
      else filteredConsultations = [...consultations];
    } else {
      if (search !== "")
        filteredConsultations = [...consultations].filter(
          (i) => i.status === value && i.owner.user.username.includes(search)
        );
      else
        filteredConsultations = [...consultations].filter(
          (i) => i.status === value
        );
    }
    setFiltered(filteredConsultations);
    setFilter(value);
  }

  function getConsultationList(consultations) {
    return consultations.map((c) => {
      return (
        <tr key={c.id}>
          <td>{c.title}</td>
          <td>{c.status}</td>
          <td>{c.pet?.name}</td>
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
          (i) => i.status === filter && i.owner.user.username.includes(value)
        );
      else
        filteredConsultations = [...consultations].filter((i) =>
          i.owner.user.username.includes(value)
        );
    }

    setFiltered(filteredConsultations);
    setSearch(value);
  }

  async function setUp() {
    const consultations = await (
      await fetch(`/api/v1/consultations?userId=${user.id}`, {
        headers: {
          Authorization: `Bearer ${jwt}`,
          "Content-Type": "application/json",
        },
      })
    ).json();

    setConsultations(consultations);
    setFiltered(consultations);
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
              Clear Filters
            </Button>
          </Col>
          <Col className="col-sm-3">
            <Input
              type="search"
              placeholder="Introduce an owner name to search by it"
              value={search || ""}
              onChange={handleChange}
            />
          </Col>
        </Row>
        <Table className="mt-4">
          <thead>
            <tr>
              <th>Title</th>
              <th>Status</th>
              <th>Owner</th>
              <th>Creation Date</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {filtered
              ? getConsultationList(filtered)
              : getConsultationList(consultations)}
          </tbody>
        </Table>
      </Container>
    </div>
  );
}
