import { useState } from "react";
import { Link } from "react-router-dom";
import { Button, ButtonGroup, Table } from "reactstrap";
import tokenService from "../../services/token.service";
import useFetchState from "../../util/useFetchState";
import getErrorModal from "../../util/getErrorModal";
import "../../static/css/admin/adminPage.css";
import { useNavigate } from "react-router-dom";

const user = tokenService.getUser();
const jwt = tokenService.getLocalAccessToken();

export default function ClinicsList() {
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const [clinics, setClinics] = useFetchState(
    [],
    `/api/v1/clinics?userId=${user.id}`,
    jwt,
    setMessage,
    setVisible
  );
  const [alerts, setAlerts] = useState([]);

  const navigator = useNavigate();

  const clinicsList =
    clinics.map((clinic) => {
        return (
          <tr key={clinic.id}>
            <td className="text-center">{clinic.name}</td>
            <td className="text-center">{clinic.address}</td>
            <td className="text-center">{clinic.telephone}</td>
            <td className="text-center">{clinic.plan}</td>
            <td className="text-center">
              <ButtonGroup>
                <Button
                  size="sm"
                  color="primary"
                  aria-label={"edit-" + clinic.name}
                  tag={Link}
                  to={"/clinics/" + clinic.id}
                >
                  Edit
                </Button>
                <Button
                  size="sm"
                  color="danger"
                  aria-label={"delete-" + clinic.name}
                  onClick={() => {
                    let confirmMessage = window.confirm("Are you sure you want to delete it?");

                    if(!confirmMessage) return;

                    fetch(`/api/v1/clinics/${clinic.id}`, {
                      method: "DELETE",
                      headers: {
                        "Content-Type": "application/json",
                        Authorization: `Bearer ${jwt}`,
                      },
                    })
                      .then((res) => {
                        if (res.status === 200) {
                          setMessage("Clinic deleted successfully");
                          setVisible(true);
                          navigator(0);
                        }
                      })
                      .catch((err) => {
                        setMessage(err.message);
                        setVisible(true);
                      });
                  }}
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
        <h1 className="text-center">My Clinics</h1>
        {alerts.map((a) => a.alert)}
        {modal}
        <div className="float-right">
          <Button color="success" tag={Link} to="/clinics/new">
            Add clinic
          </Button>
        </div>
        <div>
          <Table aria-label="clinics" className="mt-4">
            <thead>
              <tr>
                <th width="15%" className="text-center">Name</th>
                <th width="15%" className="text-center">Address</th>
                <th width="15%" className="text-center">Telephone</th>
                <th width="15%" className="text-center">Plan</th>
                <th width="30%" className="text-center">Actions</th>
              </tr>
            </thead>
            <tbody>{clinicsList}</tbody>
          </Table>
        </div>
      </div>
    </div>
  );
}
