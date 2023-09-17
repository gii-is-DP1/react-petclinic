import { useState } from "react";
import { Link } from "react-router-dom";
import { Button, ButtonGroup, Table } from "reactstrap";
import tokenService from "../../services/token.service";
import deleteFromList from "../../util/deleteFromList";
import getErrorModal from "../../util/getErrorModal";
import getIdFromUrl from "../../util/getIdFromUrl";
import useFetchState from "../../util/useFetchState";

const jwt = tokenService.getLocalAccessToken();

export default function VisitListAdmin() {
  const petId = getIdFromUrl(2);
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const [visits, setVisits] = useFetchState(
    [],
    `/api/v1/pets/${petId}/visits`,
    jwt,
    setMessage,
    setVisible
  );
  const [alerts, setAlerts] = useState([]);

  const modal = getErrorModal(setVisible, visible, message);
  const visitList = visits.map((visit) => {
    return (
      <tr key={visit.id}>
        <td>{new Date(visit.datetime).toLocaleString()}</td>
        <td>
          {visit.description ? visit.description : "No description provided"}
        </td>
        <td>
          {visit.vet.firstName} {visit.vet.lastName}
        </td>
        <td>
          <ButtonGroup>
            <Button
              size="sm"
              aria-label={"edit-" + visit.id}
              color="primary"
              tag={Link}
              to={`/pets/${petId}/visits/${visit.id}`}
            >
              Edit
            </Button>
            <Button
              size="sm"
              aria-label={"delete-" + visit.id}
              color="danger"
              onClick={() =>
                deleteFromList(
                  `/api/v1/pets/${petId}/visits/${visit.id}`,
                  visit.id,
                  [visits, setVisits],
                  [alerts, setAlerts],
                  setMessage,
                  setVisible
                )
              }
            >
              Delete
            </Button>
          </ButtonGroup>
        </td>
      </tr>
    );
  });

  return (
    <div className="admin-page-container">
      <h1 className="text-center">Visits</h1>
      {alerts.map((a) => a.alert)}
      {modal}
      <div className="float-right">
        <Button color="success" tag={Link} to={`/pets/${petId}/visits/new`}>
          Add Visit
        </Button>{" "}
        <Button color="primary" tag={Link} to={`/pets/`}>
          Back
        </Button>
      </div>
      <div>
        <Table aria-label="visits" className="mt-4">
          <thead>
            <tr>
              <th>Date and Time</th>
              <th>Description</th>
              <th>Vet</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>{visitList}</tbody>
        </Table>
      </div>
    </div>
  );
}
