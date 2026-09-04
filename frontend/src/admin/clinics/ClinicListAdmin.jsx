import { useState } from "react";
import { Link } from "react-router-dom";
import { Button, ButtonGroup, Table } from "reactstrap";
import tokenService from "../../services/token.service";
import "../../static/css/admin/adminPage.css";
import deleteFromList from "../../util/deleteFromList";
import getErrorModal from "../../util/getErrorModal";
import useFetchState from "../../util/useFetchState";

const jwt = tokenService.getLocalAccessToken();

export default function ClinicListAdmin() {
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const [clinics, setClinics] = useFetchState(
    [],
    `/api/v1/clinics`,
    jwt,
    setMessage,
    setVisible
  );
  const [alerts, setAlerts] = useState([]);

  const clinicList = clinics.map((clinic) => {
    return (
      <tr key={clinic.id}>
        <td>{clinic.name}</td>
        <td>{clinic.address}</td>
        <td>{clinic.telephone}</td>
        <td>{clinic.clinicOwner.firstName} {clinic.clinicOwner.lastName}</td>
        <td>{clinic.plan}</td>
        <td>
          <ButtonGroup>
            <Button
              size="sm"
              color="primary"
              aria-label={"edit-" + clinic.id}
              tag={Link}
              to={"/clinics/" + clinic.id}
            >
              Edit
            </Button>
            <Button
              size="sm"
              color="danger"
              aria-label={"delete-" + clinic.id}
              onClick={() =>
                deleteFromList(
                  `/api/v1/clinics/${clinic.id}`,
                  clinic.id,
                  [clinics, setClinics],
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
    <div>
      <div className="admin-page-container">
        <h1 className="text-center">Clinic Owners</h1>
        {alerts.map((a) => a.alert)}
        {modal}
        <div className="float-right">
          <Button color="success" tag={Link} to="/clinics/new">
            Add Clinic Owner
          </Button>
        </div>
        <div>
          <Table aria-label="owners" className="mt-4">
            <thead>
              <tr>
                <th width="15%">Name</th>
                <th width="20%">Address</th>
                <th width="15%">Telephone</th>
                <th width="15%">Clinic Owner</th>
                <th width="15%">Plan</th>
                <th width="20%">Actions</th>
              </tr>
            </thead>
            <tbody>{clinicList}</tbody>
          </Table>
        </div>
      </div>
    </div>
  );
}
