import React, { Component } from "react";
import { Link } from "react-router-dom";
import {
  Button,
  Modal,
  ModalBody,
  ModalFooter,
  ModalHeader,
} from "reactstrap";
import FormGenerator from "../../../components/formGenerator/formGenerator";
import { petEditFormInputs } from "./form/petEditFormInputs";
import "../../../static/css/owner/editPet.css";
import "../../../static/css/auth/authButton.css"

class OwnerPetEdit extends Component {
  emptyItem = {
    id: null,
    name: "",
    birthDate: "",
    type: {},
    owner: {},
  };

  constructor(props) {
    super(props);
    this.state = {
      pet: this.emptyItem,
      types: [],
      message: null,
      modalShow: false,
    };
    this.editPetFormRef = React.createRef();
    this.handleChange = this.handleChange.bind(this);
    this.handleShow = this.handleShow.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
    this.jwt = JSON.parse(window.localStorage.getItem("jwt"));

    let pathArray = window.location.pathname.split("/");
    this.petId = pathArray[2];
  }

  async componentDidMount() {
    if (this.petId !== "new") {
      const pet = await (
        await fetch(`/api/v1/pets/${this.petId}`, {
          headers: {
            Authorization: `Bearer ${this.jwt}`,
          },
        })
      ).json();
      if (pet.message) this.setState({ message: pet.message, modalShow: true });
      else {
        this.setState({
          pet: pet,
          selectedType: pet.type.name,
        });
      }
    }
    if (!this.state.message) {
      const types = await (
        await fetch(`/api/v1/pets/types`, {
          headers: {
            Authorization: `Bearer ${this.jwt}`,
          },
        })
      ).json();
      if (types.message)
        this.setState({ message: types.message, modalShow: true });
      else this.setState({ types: types });
    }
  }

  handleChange(event) {
    const target = event.target;
    const value = target.value;
    const name = target.name;
    let pet = { ...this.state.pet };
    if (name === "type")
      pet.type = this.state.types.filter(
        (type) => type.id === Number(value)
      )[0];
    else pet[name] = value;
    this.setState({ pet });
  }

  handleShow() {
    let modalShow = this.state.modalShow;
    this.setState({ modalShow: !modalShow });
  }

  async handleSubmit({ values }) {

    if (!this.editPetFormRef.current.validate()) return;

    const pet = {
      id: this.state.pet.id,
      name: values["name"],
      birthDate: values["birthDate"],
      type: this.state.types.filter((type) => type.name === values["type"])[0],
      owner: this.state.pet.owner,
    };

    const submit = await (
      await fetch("/api/v1/pets" + (pet.id ? "/" + this.petId : ""), {
        method: pet.id ? "PUT" : "POST",
        headers: {
          Authorization: `Bearer ${this.jwt}`,
          Accept: "application/json",
          "Content-Type": "application/json",
        },
        body: JSON.stringify(pet),
      })
    ).json();

    if (submit.message){
      this.setState({ message: submit.message, modalShow: true });
    }
    else window.location.href = `/myPets`;
  }

  render() {
    const { pet, types, message } = this.state;
    const title = (
      <h2 className="text-center">{pet.id ? "Edit Pet" : "Add Pet"}</h2>
    );

    if (petEditFormInputs[2].values.length < 2) {
      petEditFormInputs[2].values = [
        ...petEditFormInputs[2].values,
        ...types.map((type) => type.name),
      ];
    }

    if (pet && petEditFormInputs[2].values.length >= 2) {
      petEditFormInputs[0].defaultValue = pet.name || "";
      petEditFormInputs[1].defaultValue = pet.birthDate || "";
      petEditFormInputs[2].defaultValue = pet.type.name || "None";
    }

    let modal = <></>;
    if (message) {
      const show = this.state.modalShow;
      const closeBtn = (
        <button className="close" onClick={this.handleShow} type="button">
          &times;
        </button>
      );
      const cond = message.includes("limit");
      modal = (
        <div>
          <Modal isOpen={show} toggle={this.handleShow} keyboard={false}>
            {cond ? (
              <ModalHeader>Warning!</ModalHeader>
            ) : (
              <ModalHeader toggle={this.handleShow} close={closeBtn}>
                Error!
              </ModalHeader>
            )}
            <ModalBody>{this.state.message || ""}</ModalBody>
            <ModalFooter>
              <Button color="primary" tag={Link} to={`/myPets`}>
                Back
              </Button>
            </ModalFooter>
          </Modal>
        </div>
      );
    }

    return (
      <div className="edit-pet-page-container">
        <div className="edit-pet-form-container">
          {title}
          <FormGenerator
            ref={this.editPetFormRef}
            inputs={petEditFormInputs}
            onSubmit={this.handleSubmit}
            buttonText="Save"
            buttonClassName="auth-button"
          />
        </div>
        {modal}
      </div>
    );
  }
}
export default OwnerPetEdit;
