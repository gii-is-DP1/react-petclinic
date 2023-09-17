import { useState } from "react";
import { Table } from "reactstrap";
import tokenService from "../../services/token.service";
import "../../static/css/admin/adminPage.css";
import getErrorModal from "../../util/getErrorModal";
import useFetchState from "../../util/useFetchState";

const user = tokenService.getUser();
const jwt = tokenService.getLocalAccessToken();

export default function OwnerListClinicOwner(){
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const [owners, ] = useFetchState(
    [],
    `/api/v1/clinics/owners?userId=${user.id}`,
    jwt,
    setMessage,
    setVisible
  );
  const [alerts, ] = useState([]);

  const ownerList = owners.map((owner) => {

    return (
      <tr key={owner.id}>
        <td style={{ whiteSpace: "nowrap" }}>
          {owner.firstName} {owner.lastName}
        </td>
        <td>{owner.address}</td>
        <td>{owner.city}</td>
        <td>{owner.telephone}</td>
        <td>{owner.user.username}</td>
        <td>{owner.clinic.name}</td>
      </tr>
    );
  });

  const modal = getErrorModal(setVisible, visible, message);

  return (
    <div>
      <div className="admin-page-container">
        <h1 className="text-center">Owners</h1>
        {alerts.map((a) => a.alert)}
        {modal}
        <div>
          <Table aria-label="owners" className="mt-4">
            <thead>
              <tr>
                <th width="15%">Name</th>
                <th width="20%">Address</th>
                <th width="15%">City</th>
                <th width="15%">Telephone</th>
                <th width="15%">User</th>
                <th width="20%">Clinic</th>
              </tr>
            </thead>
            <tbody>{ownerList}</tbody>
          </Table>
        </div>
      </div>
    </div>
  );
}