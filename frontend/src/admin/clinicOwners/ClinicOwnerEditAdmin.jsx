import { useState } from "react";
import { Link } from "react-router-dom";
import { Form, Input, Label } from "reactstrap";
import tokenService from "../../services/token.service";
import getErrorModal from "../../util/getErrorModal";
import getIdFromUrl from "../../util/getIdFromUrl";
import useFetchState from "../../util/useFetchState";

const user = tokenService.getUser();
const jwt = tokenService.getLocalAccessToken();

export default function ClinicOwnerEditAdmin() {
  const emptyItem = {
    id: "",
    firstName: "",
    lastName: "",
  };
  const id = getIdFromUrl(2);
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const [clinicOwner, setClinicOwner] = useFetchState(
    emptyItem,
    `/api/v1/clinicOwners/${id}`,
    jwt,
    setMessage,
    setVisible,
    id
  );

  function handleChange(event) {
    const target = event.target;
    const value = target.value;
    const name = target.name;
    setClinicOwner({ ...clinicOwner, [name]: value });
  }

  function handleSubmit(event) {
    event.preventDefault();

    fetch("/api/v1/clinicOwners" + (clinicOwner.id ? "/" + clinicOwner.id : "" + `?userId=${user.id}`), {
      method: clinicOwner.id ? "PUT" : "POST",
      headers: {
        Authorization: `Bearer ${jwt}`,
        Accept: "application/json",
        "Content-Type": "application/json",
      },
      body: JSON.stringify(clinicOwner),
    })
      .then((response) => response.json())
      .then((json) => {
        if (json.message) {
          setMessage(json.message);
          setVisible(true);
        } else window.location.href = "/clinicOwners";
      })
      .catch((message) => alert(message));
  }

  const modal = getErrorModal(setVisible, visible, message);

  return (
    <div className="auth-page-container">
      {<h2>{id !== "new" ? "Edit Clinic Owner" : "Add Clinic Owner"}</h2>}
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
              value={clinicOwner.firstName || ""}
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
              value={clinicOwner.lastName || ""}
              onChange={handleChange}
              className="custom-input"
            />
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
