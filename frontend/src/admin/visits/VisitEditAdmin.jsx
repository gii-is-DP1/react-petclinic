import { useState } from "react";
import { Link } from "react-router-dom";
import { Form, Input, Label } from "reactstrap";
import tokenService from "../../services/token.service";
import "../../static/css/admin/adminPage.css";
import getErrorModal from "../../util/getErrorModal";
import getIdFromUrl from "../../util/getIdFromUrl";
import useFetchData from "../../util/useFetchData";
import useFetchState from "../../util/useFetchState";

const jwt = tokenService.getLocalAccessToken();

export default function VisitEditAdmin() {
  const emptyItem = {
    id: "",
    datetime: "",
    description: "",
    vet: {},
    pet: {},
  };
  const petId = getIdFromUrl(2);
  const visitId = getIdFromUrl(4);
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const [visit, setVisit] = useFetchState(
    emptyItem,
    `/api/v1/pets/${petId}/visits/${visitId}`,
    jwt,
    setMessage,
    setVisible,
    null,
    visitId
  );
  const pet = useFetchData(`/api/v1/pets/${petId}`, jwt);
  const vets = useFetchData(`/api/v1/vets`, jwt);

  function handleChange(event) {
    const target = event.target;
    const value = target.value;
    const name = target.name;
    if (name === "vet") {
      const vet = vets.find((v) => v.id === Number(value));
      setVisit({ ...visit, vet: vet });
    } else setVisit({ ...visit, [name]: value });
  }

  function handleSubmit(event) {
    event.preventDefault();
    setVisit({ ...visit, pet: pet });

    fetch(`/api/v1/pets/${petId}/visits` + (visit.id ? "/" + visit.id : ""), {
      method: visit.id ? "PUT" : "POST",
      headers: {
        Authorization: `Bearer ${jwt}`,
        Accept: "application/json",
        "Content-Type": "application/json",
      },
      body: JSON.stringify(visit),
    })
      .then((response) => response.json())
      .then((json) => {
        if (json.message) {
          setMessage(json.message);
          setVisible(true);
        } else window.location.href = `/pets/${petId}/visits`;
      })
      .catch((message) => alert(message));
  }

  const modal = getErrorModal(setVisible, visible, message);
  const vetOptions = vets.map((vet) => (
    <option key={vet.id} value={vet.id}>
      {vet.firstName} {vet.lastName}
    </option>
  ));

  return (
      <div className="auth-page-container">
        {<h2>{visit.id ? "Edit Visit" : "Add Visit"}</h2>}
        {modal}
        <div className="auth-form-container">
          <Form onSubmit={handleSubmit}>
            <div className="custom-form-input">
              <Label for="datetime" className="custom-form-input-label">
                Date and Time
              </Label>
              <Input
                type="datetime-local"
                required
                name="datetime"
                id="datetime"
                value={visit.datetime || ""}
                onChange={handleChange}
                className="custom-input"
              />
            </div>
            <div className="custom-form-input">
              <Label for="description" className="custom-form-input-label">
                Description
              </Label>
              <Input
                type="textarea"
                name="description"
                id="description"
                value={visit.description || ""}
                onChange={handleChange}
                className="custom-textarea"
              />
            </div>
            <div className="custom-form-input">
              <Label for="vet" className="custom-form-input-label">
                Vet
              </Label>
              <Input
                type="select"
                required
                name="vet"
                id="vet"
                value={visit.vet.id}
                onChange={handleChange}
                className="custom-input"
              >
                <option value="">None</option>
                {vetOptions}
              </Input>
            </div>
            <div className="custom-form-input">
              <Label for="pet" className="custom-form-input-label">
                Pet
              </Label>
              <Input
                type="text"
                disabled
                name="pet"
                id="pet"
                value={pet.name || ""}
                onChange={handleChange}
                className="custom-input"
              />
            </div>
            <div className="custom-button-row">
              <button className="auth-button">
                Save
              </button>
              <Link to={`/pets/${petId}/visits`} className="auth-button" style={{textDecoration: "none"}}>
                Cancel
              </Link>
            </div>
          </Form>
        </div>
      </div>
  );
}
