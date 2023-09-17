import { useState } from "react";
import { Link } from "react-router-dom";
import { Button, ButtonGroup, Table } from "reactstrap";
import tokenService from "../../services/token.service";
import deleteFromList from "../../util/deleteFromList";
import getErrorModal from "../../util/getErrorModal";
import useFetchState from "../../util/useFetchState";

const jwt = tokenService.getLocalAccessToken();

export default function VetListAdmin() {
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const [vets, setVets] = useFetchState(
    [],
    `/api/v1/vets`,
    jwt,
    setMessage,
    setVisible
  );
  const [alerts, setAlerts] = useState([]);

  const vetList = vets.map((vet) => {
    let specialtiesAux = vet.specialties
      .map((s) => s.name)
      .toString()
      .replaceAll(",", ", ");
    return (
      <tr key={vet.id}>
        <td style={{ whiteSpace: "nowrap" }}>
          {vet.firstName} {vet.lastName}
        </td>
        <td>{vet.city}</td>
        <td style={{ whiteSpace: "break-spaces" }}>{specialtiesAux}</td>
        <td>{vet.clinic.name}</td>
        <td>{vet.user.username}</td>
        <td>
          <ButtonGroup>
            <Button
              size="sm"
              aria-label={"edit-" + vet.id}
              color="primary"
              tag={Link}
              to={"/vets/" + vet.id}
            >
              Edit
            </Button>
            <Button
              size="sm"
              aria-label={"delete-" + vet.id}
              color="danger"
              onClick={() =>
                deleteFromList(
                  `/api/v1/vets/${vet.id}`,
                  vet.id,
                  [vets, setVets],
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
  const modal = getErrorModal(setVisible, visible, message);

  return (
    <div className="admin-page-container">
      <h1 className="text-center">Vets</h1>
      {alerts.map((a) => a.alert)}
      {modal}
      <div className="float-right">
        <Button color="success" tag={Link} to="/vets/new">
          Add Vet
        </Button>{" "}
        <Button color="info" tag={Link} to="/vets/specialties">
          Specialties
        </Button>
      </div>
      <div>
        <Table aria-label="vets" className="mt-4">
          <thead>
            <tr>
              <th width="15%">Name</th>
              <th width="15%">City</th>
              <th width="20%">Specialties</th>
              <th width="15%">Clinic</th>
              <th width="15%">User</th>
              <th width="20%">Actions</th>
            </tr>
          </thead>
          <tbody>{vetList}</tbody>
        </Table>
      </div>
    </div>
  );
}
