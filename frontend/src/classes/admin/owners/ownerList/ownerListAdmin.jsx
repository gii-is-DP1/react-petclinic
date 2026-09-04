import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Alert, Button, ButtonGroup, Container, Table } from 'reactstrap';
import tokenService from '../../../services/token.service';

const jwt = tokenService.getLocalAccessToken();

export default function OwnerListAdmin() {
  const [owners, setOwners] = useState([]);
  const [alerts, setAlerts] = useState([]);

  useEffect(() => {
    let ignore = false;
    fetch(`/api/v1/owners`, {
      headers: {
        "Authorization": `Bearer ${jwt}`,
      },
    })
      .then(response => response.json())
      .then(json => {
        if (!ignore) {
          setOwners(json);
        }
      });
    return () => {
      ignore = true;
    };
  }, []);

  async function remove(id) {
    let confirmMessage = window.confirm("Are you sure you want to delete it?");
    if (confirmMessage) {
      await fetch(`/api/v1/owners/${id}`, {
        method: "DELETE",
        headers: {
          "Authorization": `Bearer ${jwt}`,
          Accept: "application/json",
          "Content-Type": "application/json",
        },
      }).then((response) => {
        if (response.status === 200) {
          setOwners(owners.filter((i) => i.id !== id));
        }
        return response.json();
      }).then(json => {
        const alertId = `alert-${id}`
        setAlerts([
          ...alerts,
          {
            alert: <Alert toggle={() => dismiss(alertId)} key={"alert-" + id} id={alertId} color="info">
              {json.message}
            </Alert>,
            id: alertId
          }
        ]);
      });
    }
  }

  function dismiss(id) {
    setAlerts(alerts.filter(i => i.id !== id))
  }

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
        <td>{owner.plan}</td>
        <td>
          <ButtonGroup>
            <Button size="sm" color="primary" tag={Link} to={"/owners/" + owner.id}>Edit</Button>
            <Button size="sm" color="danger" onClick={() => remove(owner.id)}>Delete</Button>
          </ButtonGroup>
        </td>
      </tr>
    );
  });

  return (
    <div>
      <Container fluid style={{ marginTop: "15px" }}>

        <h1 className="text-center">Owners</h1>
        {alerts.map((a) => a.alert)}
        <div className="float-right">
          <Button color="success" tag={Link} to="/owners/new">
            Add Owner
          </Button>
        </div>
        <Table className="mt-4">
          <thead>
            <tr>
              <th width="10%">Name</th>
              <th width="10%">Address</th>
              <th width="10%">City</th>
              <th width="10%">Telephone</th>
              <th width="10%">User</th>
              <th width="10%">Plan</th>
              <th width="40%">Actions</th>
            </tr>
          </thead>
          <tbody>{ownerList}</tbody>
        </Table>
      </Container>
    </div>
  );
}
