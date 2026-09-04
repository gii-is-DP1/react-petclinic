import { useState } from "react";
import { Link } from "react-router-dom";
import { Button, ButtonGroup, Table } from "reactstrap";
import tokenService from "../../services/token.service";
import "../../static/css/admin/adminPage.css";
import deleteFromList from "../../util/deleteFromList";
import getErrorModal from "../../util/getErrorModal";
import useFetchState from "../../util/useFetchState";

const jwt = tokenService.getLocalAccessToken();

export default function SpecialtyListAdmin() {
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const [specialties, setSpecialties] = useFetchState(
    [],
    `/api/v1/vets/specialties`,
    jwt,
    setMessage,
    setVisible
  );
  const [alerts, setAlerts] = useState([]);

  const specialtiesList = specialties.map((s) => {
    return (
      <tr key={s.id}>
        <td style={{ whiteSpace: "nowrap" }}>{s.name}</td>
        <td>
          <ButtonGroup>
            <Button
              size="sm"
              aria-label={"edit-" + s.id}
              color="primary"
              tag={Link}
              to={"/vets/specialties/" + s.id}
            >
              Edit
            </Button>
            <Button
              size="sm"
              aria-label={"delete-" + s.id}
              color="danger"
              onClick={() =>
                deleteFromList(
                  `/api/v1/vets/specialties/${s.id}`,
                  s.id,
                  [specialties, setSpecialties],
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
      <h1 className="text-center">Specialties</h1>
      {alerts.map((a) => a.alert)}
      {modal}
      <Button color="success" tag={Link} to="/vets/specialties/new">
        Add Specialty
      </Button>{" "}
      <Button color="info" tag={Link} to="/vets">
        Back
      </Button>
      <div>
        <Table aria-label="specialties" className="mt-4">
          <thead>
            <tr>
              <th width="20%">Name</th>
              <th width="20%">Actions</th>
            </tr>
          </thead>
          <tbody>{specialtiesList}</tbody>
        </Table>
      </div>
    </div>
  );
}
