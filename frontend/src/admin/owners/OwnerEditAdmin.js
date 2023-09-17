import { useState } from "react";
import { Link } from "react-router-dom";
import { Form, Input, Label } from "reactstrap";
import tokenService from "../../services/token.service";
import getErrorModal from "../../util/getErrorModal";
import getIdFromUrl from "../../util/getIdFromUrl";
import useFetchState from "../../util/useFetchState";

const jwt = tokenService.getLocalAccessToken();

export default function OwnerEditAdmin() {
  const emptyItem = {
    id: "",
    firstName: "",
    lastName: "",
    address: "",
    city: "",
    telephone: "",
    plan: null,
  };
  const id = getIdFromUrl(2);
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const [owner, setOwner] = useFetchState(
    emptyItem,
    `/api/v1/owners/${id}`,
    jwt,
    setMessage,
    setVisible,
    id
  );
  const [clinics, setClinics] = useFetchState(
    [],
    `/api/v1/clinics`,
    jwt,
    setMessage,
    setVisible
  );

  function handleChange(event) {
    const target = event.target;
    const value = target.value;
    const name = target.name;
    if(name === "clinic") {
      setOwner({ ...owner, clinic: clinics.filter((clinic) => clinic.id === parseInt(value))[0] });
    }else{
      setOwner({ ...owner, [name]: value });
    }
  }

  function handleSubmit(event) {
    event.preventDefault();

    fetch("/api/v1/owners" + (owner.id ? "/" + owner.id : ""), {
      method: owner.id ? "PUT" : "POST",
      headers: {
        Authorization: `Bearer ${jwt}`,
        Accept: "application/json",
        "Content-Type": "application/json",
      },
      body: JSON.stringify(owner),
    })
      .then((response) => response.json())
      .then((json) => {
        if (json.message) {
          setMessage(json.message);
          setVisible(true);
        } else window.location.href = "/owners";
      })
      .catch((message) => alert(message));
  }

  const modal = getErrorModal(setVisible, visible, message);

  return (
    <div className="auth-page-container">
      {<h2>{id !== "new" ? "Edit Owner" : "Add Owner"}</h2>}
      {modal}
      <div className="auth-form-container">
        <Form onSubmit={handleSubmit}>
          <div className="custom-form-input">
            <Label for="firstName" className="custom-form-input-label">
              First Name
            </Label>
            <Input
              type="text"
              required
              name="firstName"
              id="firstName"
              value={owner.firstName || ""}
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
              required
              name="lastName"
              id="lastName"
              value={owner.lastName || ""}
              onChange={handleChange}
              className="custom-input"
            />
          </div>
          <div className="custom-form-input">
            <Label for="address" className="custom-form-input-label">
              Address
            </Label>
            <Input
              type="text"
              required
              name="address"
              id="address"
              value={owner.address || ""}
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
              required
              name="city"
              id="city"
              value={owner.city || ""}
              onChange={handleChange}
              className="custom-input"
            />
          </div>
          <div className="custom-form-input">
            <Label for="telephone" className="custom-form-input-label">
              Telephone
            </Label>
            <Input
              type="tel"
              required
              pattern="[0-9]{9}"
              name="telephone"
              id="telephone"
              value={owner.telephone || ""}
              onChange={handleChange}
              className="custom-input"
            />
          </div>
          <div className="custom-form-input">
            <Label for="clinic" className="custom-form-input-label">
              Clinic
            </Label>
            <Input
              id="clinic"
              name="clinic"
              required
              type="select"
              value={owner.clinic ? owner.clinic.id : ""}
              onChange={handleChange}
              className="custom-input"
            >
              <option value="">None</option>
              {
                clinics && clinics.map((clinic) => {
                  console.log(clinic);
                  return <option value={clinic.id}>{clinic.name}</option>
                })
              }
            </Input>
          </div>
          <div className="custom-button-row">
            <button className="auth-button">Save</button>
            <Link
              to={`/owners`}
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
