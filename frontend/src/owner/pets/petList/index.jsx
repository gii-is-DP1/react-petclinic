import "../../../static/css/owner/petList.css";
import "../../../static/css/auth/authButton.css";
import { Button, Modal, ModalBody, ModalFooter, ModalHeader } from "reactstrap";
import { Link } from "react-router-dom";
import tokenService from "../../../services/token.service";
import { Splide, SplideSlide } from "@splidejs/react-splide";
import moment from "moment";
import { useState, useEffect } from "react";

export default function OwnerPetList() {
  let [pets, setPets] = useState([]);
  let [message, setMessage] = useState(null);
  let [modalShow, setModalShow] = useState(false);

  const user = tokenService.getUser();
  const jwt = tokenService.getLocalAccessToken();

  function removePet(id) {
    fetch(`/api/v1/pets/${id}`, {
      method: "DELETE",
      headers: {
        Authorization: `Bearer ${jwt}`,
        Accept: "application/json",
        "Content-Type": "application/json",
      },
    })
      .then((response) => {
        if (response.status === 200) {
          let updatedPets = [...pets].filter((i) => i.id !== id);
          setPets(updatedPets);
        }
        return response.json();
      })
      .then((data) => {
        setMessage(data.message);
        setModalShow(true);
      });
  }

  async function removeVisit(petId, visitId) {
    let status = "";
    await fetch(`/api/v1/pets/${petId}/visits/${visitId}`, {
      method: "DELETE",
      headers: {
        Authorization: `Bearer ${jwt}`,
        Accept: "application/json",
        "Content-Type": "application/json",
      },
    })
      .then((response) => {
        if (response.status === 200) status = "200";
        return response.json();
      })
      .then((data) => {
        setMessage(data.message);
        setModalShow(true);
      });

    if (status === "200") {
      let petsAux = pets;
      const index = petsAux.findIndex((i) => i.id === petId);
      let pet = [...petsAux].filter((i) => i.id === petId);
      let updatedVisits = pet[0].visits.filter((i) => i.id !== visitId);
      if (updatedVisits.length > 0) pet[0].visits = updatedVisits;
      else pet[0].visits = [];
      petsAux[index] = pet[0];

      setPets(petsAux);
    }
  }

  function handleShow() {
    setModalShow(!modalShow);
  }

  async function setUp() {
    let pets = await (
      await fetch(`/api/v1/pets?userId=${user.id}`, {
        headers: {
          Authorization: `Bearer ${jwt}`,
          "Content-Type": "application/json",
        },
      })
    ).json();
    if (pets.message) setMessage(pets.message);
    else {
      for (let pet of pets) {
        let index = pets.findIndex((obj) => obj.id === pet.id);
        const visits = await (
          await fetch(`/api/v1/pets/${pet.id}/visits`, {
            headers: {
              Authorization: `Bearer ${jwt}`,
              "Content-Type": "application/json",
            },
          })
        ).json();
        if (visits.message) setMessage(visits.message);
        else pets[index]["visits"] = visits;
      }
      setPets(pets);
    }
  }

  useEffect(() => {
    setUp();
  }, []);

  return (
    <div>
      {/* <AppNavbar /> */}
      <div className="pet-list-page-container">
        <div className="title-and-add">
          <h1 className="pet-list-title">Pets</h1>
          <Link
            to="/myPets/new"
            className="auth-button"
            style={{ textDecoration: "none", marginBottom: "2rem" }}
          >
            Add Pet
          </Link>
        </div>
        {pets.length > 0 ? (
          pets.map((pet) => {
            return (
              <div key={pet.id} className="pet-row">
                <div className="pet-data">
                  <h4 className="pet-name">{pet.name}</h4>
                  <span>
                    <strong>Date of birth:</strong> {pet.birthDate}
                  </span>
                  <span>
                    <strong>Type:</strong> {pet.type.name}
                  </span>
                </div>
                <div className="pet-options">
                  <Link
                    to={"/myPets/" + pet.id}
                    className="auth-button blue"
                    style={{ textDecoration: "none" }}
                  >
                    Edit
                  </Link>
                  <button
                    onClick={() => removePet(pet.id)}
                    className="auth-button danger"
                  >
                    Delete
                  </button>
                </div>
                <div className="pet-visits">
                  {pet.visits && pet.visits.length > 0 ? (
                    <Splide
                      aria-label="My Favorite Images"
                      className="visits-carousel"
                    >
                      {pet.visits.map((visit, index) => {
                        return (
                          <SplideSlide className="carousel-slide" key={index}>
                            <span>
                              <strong>Date and Time:</strong>{" "}
                              {moment(visit.datetime).format(
                                "DD/MM/YYYY HH:mm"
                              )}
                            </span>
                            <span>
                              <strong>Vet:</strong> {visit.vet.firstName}{" "}
                              {visit.vet.lastName}
                            </span>
                            <div className="options-row">
                              {new Date(visit.datetime) > Date.now() && (
                                <button
                                  className="cancel-visit-button"
                                  onClick={() => removeVisit(pet.id, visit.id)}
                                >
                                  Cancel
                                </button>
                              )}
                              <Link
                                to={`/myPets/${pet.id}/visits/${visit.id}`}
                                className="edit-visit-button"
                                style={{ textDecoration: "none" }}
                              >
                                Edit
                              </Link>
                            </div>
                          </SplideSlide>
                        );
                      })}
                    </Splide>
                  ) : (
                    <></>
                  )}
                  <Link
                    to={`/myPets/${pet.id}/visits/new`}
                    className="auth-button blue"
                    style={{ textDecoration: "none", marginTop: "20px" }}
                  >
                    Add Visit
                  </Link>
                </div>
              </div>
            );
          })
        ) : (
          <p>Loading...</p>
        )}

        <Modal isOpen={modalShow} toggle={handleShow} keyboard={false}>
          <ModalHeader
            toggle={handleShow}
            close={
              <button className="close" onClick={handleShow} type="button">
                &times;
              </button>
            }
          >
            Alert!
          </ModalHeader>
          <ModalBody>{message || ""}</ModalBody>
          <ModalFooter>
            <Button color="primary" onClick={handleShow}>
              Close
            </Button>
          </ModalFooter>
        </Modal>
      </div>
    </div>
  );
}
