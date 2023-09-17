import { useState } from "react";
import { Link } from "react-router-dom";
import { Form, Input, Label } from "reactstrap";
import tokenService from "../../services/token.service";
import getErrorModal from "../../util/getErrorModal";
import getIdFromUrl from "../../util/getIdFromUrl";
import useFetchState from "../../util/useFetchState";

const jwt = tokenService.getLocalAccessToken();

export default function ClinicEditAdmin() {
  const emptyItem = {
    id: "",
    name: "",
    address: "",
    telephone: "",
  };
  const id = getIdFromUrl(2);
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const [clinic, setClinic] = useFetchState(
    emptyItem,
    `/api/v1/clinics/${id}`,
    jwt,
    setMessage,
    setVisible,
    id
  );

  const [clinicOwners, setClinicOwners] = useFetchState(
    [],
    `/api/v1/clinicOwners/all`,
    jwt,
    setMessage,
    setVisible
  );

  function handleChange(event) {
    const target = event.target;
    const value = target.value;
    const name = target.name;
    setClinic({ ...clinic, [name]: value });
  }

  function handleSubmit(event) {
    event.preventDefault();

    clinic.clinicOwner = clinicOwners.filter((clinicOwner) => clinicOwner.id === parseInt(clinic.clinicOwner))[0];

    console.log(clinic);

    fetch("/api/v1/clinics" + (clinic.id ? "/" + clinic.id : ""), {
      method: clinic.id ? "PUT" : "POST",
      headers: {
        Authorization: `Bearer ${jwt}`,
        Accept: "application/json",
        "Content-Type": "application/json",
      },
      body: JSON.stringify(clinic),
    })
      .then((response) => response.json())
      .then((json) => {
        if (json.message) {
          setMessage(json.message);
          setVisible(true);
        } else window.location.href = "/clinics";
      })
      .catch((message) => alert(message));
  }

  const modal = getErrorModal(setVisible, visible, message);

  return (
    <div className="auth-page-container">
      {<h2>{id !== "new" ? "Edit Clinic" : "Add Clinic"}</h2>}
      {modal}
      <div className="auth-form-container">
        <Form onSubmit={handleSubmit}>
          <div className="custom-form-input">
            <Label for="name" className="custom-form-input-label">
              Name
            </Label>
            <Input
              type="text"
              required
              name="name"
              id="name"
              value={clinic.name || ""}
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
              value={clinic.address || ""}
              onChange={handleChange}
              className="custom-input"
            />
          </div>
          <div className="custom-form-input">
            <Label for="telephone" className="custom-form-input-label">
                Telephone
            </Label>
            <Input
              type="text"
              required
              name="telephone"
              id="telephone"
              value={clinic.telephone || ""}
              onChange={handleChange}
              className="custom-input"
            />
          </div>
          <div className="custom-form-input">
            <Label for="plan" className="custom-form-input-label">
              Plan
            </Label>
            <Input
              id="plan"
              name="plan"
              required
              type="select"
              value={clinic.plan || ""}
              onChange={handleChange}
              className="custom-input"
            >
              <option value="">None</option>
              <option value="BASIC">BASIC</option>
              <option value="GOLD">GOLD</option>
              <option value="PLATINUM">PLATINUM</option>
              
            </Input>
          </div>
          <div className="custom-form-input">
            <Label for="clinicOwner" className="custom-form-input-label">
              Clinic Owner
            </Label>
            <Input
              id="clinicOwner"
              name="clinicOwner"
              required
              type="select"
              value={clinic.clinicOwner ? clinic.clinicOwner.id : ""}
              onChange={handleChange}
              className="custom-input"
            >
              <option value="">None</option>
              {clinicOwners.map((clinicOwner) => {
                return(
                    <option value={clinicOwner.id}>{clinicOwner.firstName} {clinicOwner.lastName}</option>
                );
              })}
              
            </Input>
          </div>
          <div className="custom-button-row">
            <button className="auth-button">Save</button>
            <Link
              to={`/clinicOwners`}
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
