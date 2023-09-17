import { useState } from "react";
import { Link } from "react-router-dom";
import {
  Form,
  Input,
  Label,
  Row
} from "reactstrap";
import tokenService from "../../services/token.service";
import getErrorModal from "../../util/getErrorModal";
import getIdFromUrl from "../../util/getIdFromUrl";
import useFetchData from "../../util/useFetchData";
import useFetchState from "../../util/useFetchState";

const jwt = tokenService.getLocalAccessToken();

export default function VetEditAdmin() {
  const emptyItem = {
    id: "",
    firstName: "",
    lastName: "",
    specialties: [],
    user: {},
  };
  const id = getIdFromUrl(2);
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const [vet, setVet] = useFetchState(
    emptyItem,
    `/api/v1/vets/${id}`,
    jwt,
    setMessage,
    setVisible,
    id
  );
  const specialties = useFetchData(`/api/v1/vets/specialties`, jwt);
  const users = useFetchData(`/api/v1/users`, jwt);

  function handleChange(event) {
    const target = event.target;
    const value = target.value;
    const name = target.name;
    if (name === "user") {
      const user = users.find((u) => u.id === Number(value));
      setVet({ ...vet, user: user });
    } else setVet({ ...vet, [name]: value });
  }

  function handleSpecialtyChange(event) {
    const target = event.target;
    const checked = target.checked;
    const name = target.name;
    if (checked) {
      setVet({
        ...vet,
        specialties: [
          ...vet.specialties,
          specialties.find((s) => s.name === name),
        ],
      });
    } else
      setVet({
        ...vet,
        specialties: vet.specialties.filter((s) => s.name !== name),
      });
  }

  function handleSubmit(event) {
    event.preventDefault();

    fetch("/api/v1/vets" + (vet.id ? "/" + vet.id : ""), {
      method: vet.id ? "PUT" : "POST",
      headers: {
        Authorization: `Bearer ${jwt}`,
        Accept: "application/json",
        "Content-Type": "application/json",
      },
      body: JSON.stringify(vet),
    })
      .then((response) => response.json())
      .then((json) => {
        if (json.message) {
          setMessage(json.message);
          setVisible(true);
        } else window.location.href = "/vets";
      })
      .catch((message) => alert(message));
  }

  const modal = getErrorModal(setVisible, visible, message);
  const selectedSpecialties = vet.specialties.map(
    (specialty) => specialty.name
  );
  const specialtiesBoxes = specialties.map((specialty) => {
    if (selectedSpecialties?.includes(specialty.name)) {
      return (
        <div className="custom-form-input" key={specialty.name}>
          <Input
            type="checkbox"
            name={specialty.name}
            onChange={handleSpecialtyChange}
            checked
          />
          <Label for={specialty.name} style={{marginLeft: "15px"}}> {specialty.name}</Label>
        </div>
      );
    } else {
      return (
        <div className="custom-form-input" key={specialty.name}>
          <Input
            type="checkbox"
            key={specialty.name}
            name={specialty.name}
            onChange={handleSpecialtyChange}
          />
          <Label for={specialty.name} style={{marginLeft: "15px"}}> {specialty.name}</Label>
        </div>
      );
    }
  });
  const userOptions = users.map((user) => (
    <option key={user.id} value={user.id}>
      {user.username}
    </option>
  ));

  return (
    <div className="auth-page-container">
      {<h2>{vet.id ? "Edit Vet" : "Add Vet"}</h2>}
      {modal}
      <div className="auth-form-container">
        <Form onSubmit={handleSubmit}>
          <div className="custom-form-input">
            <Label for="firstName" className="custom-form-input-label">
              First Name
            </Label>
            <Input
              type="text"
              name="firstName"
              id="firstName"
              value={vet.firstName || ""}
              onChange={handleChange}
              className="custom-input"
            />
          </div>
          <div className="custom-form-input">
            <Label for="lastName" className="custom-form-input-label">
              Last Name
            </Label>
            <Input
              type="text"
              name="lastName"
              id="lastName"
              value={vet.lastName || ""}
              onChange={handleChange}
              className="custom-input"
            />
          </div>
          <div className="custom-form-input">
            <Label for="city" className="custom-form-input-label">
              City
            </Label>
            <Input
              type="text"
              name="city"
              id="city"
              value={vet.city || ""}
              onChange={handleChange}
              className="custom-input"
            />
          </div>
          <Label for="specialties" className="custom-form-input-label">
            Specialties
          </Label>
          <Row className="row-cols-lg-auto g-3 align-items-center" style={{display: "flex", justifyContent: "space-evenly", alignItems: "center"}}>
            {specialtiesBoxes}
          </Row>
          <div className="custom-form-input">
            {vet.id ? (
              <Input
                type="select"
                disabled
                name="user"
                id="user"
                value={vet.user?.id || ""}
                onChange={handleChange}
                className="custom-input"
              >
                <option value="">None</option>
                {userOptions}
              </Input>
            ) : (
              <Input
                type="select"
                required
                name="user"
                id="user"
                value={vet.user?.id || ""}
                onChange={handleChange}
                className="custom-input"
              >
                <option value="">None</option>
                {userOptions}
              </Input>
            )}
          </div>
          <div className="custom-button-row">
            <button className="auth-button">Save</button>
            <Link
              to={`/vets`}
              className="auth-button"
              style={{ textDecoration: "none" }}
            >
              Cancel
            </Link>
          </div>
        </Form>
      </div>
    </div>
  );
}
