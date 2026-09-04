import { useState } from "react";
import { Link } from "react-router-dom";
import { Button, ButtonGroup, Table } from "reactstrap";
import tokenService from "../../services/token.service";
import useFetchState from "../../util/useFetchState";
import getErrorModal from "../../util/getErrorModal";
import deleteFromList from "../../util/deleteFromList";

const jwt = tokenService.getLocalAccessToken();

export default function PetListAdmin() {
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const [pets, setPets] = useFetchState(
    [],
    `/api/v1/pets`,
    jwt,
    setMessage,
    setVisible
  );
  const [alerts, setAlerts] = useState([]);

  const petList = pets.map((pet) => {
    return (
      <tr key={pet.id}>
        <td>{pet.name}</td>
        <td>{pet.birthDate}</td>
        <td>{pet.type?.name}</td>
        <td>{pet.owner.user.username}</td>
        <td>
          <Button
            aria-label={"visits-" + pet.id}
            size="sm"
            color="info"
            tag={Link}
            to={`/pets/${pet.id}/visits`}
          >
            Visits
          </Button>
        </td>
        <td>
          <ButtonGroup>
            <Button
              aria-label={"edit-" + pet.id}
              size="sm"
              color="primary"
              tag={Link}
              to={"/pets/" + pet.id}
            >
              Edit
            </Button>
            <Button
              aria-label={"delete-" + pet.id}
              size="sm"
              color="danger"
              onClick={() =>
                deleteFromList(
                  `/api/v1/pets/${pet.id}`,
                  pet.id,
                  [pets, setPets],
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
        <h1 className="text-center">Pets</h1>
        {alerts.map((a) => a.alert)}
        {modal}
        <Button color="success" tag={Link} to="/pets/new">
          Add Pet
        </Button>
        <div>
          <Table aria-label="pets" className="mt-4">
            <thead>
              <tr>
                <th>Name</th>
                <th>Birth Date</th>
                <th>Type</th>
                <th>Owner</th>
                <th>Visits</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>{petList}</tbody>
          </Table>
        </div>
      </div>
  );
}
