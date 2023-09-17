import React from "react";
import { useEffect, useState, useRef } from "react";
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
import useFetchState from "../../../util/useFetchState";

export default function OwnerPetEdit(){
  let pathArray = window.location.pathname.split("/");
  const emptyItem = {
    id: null,
    name: "",
    birthDate: "",
    type: {},
    owner: {},
  };  
  const jwt = JSON.parse(window.localStorage.getItem("jwt"));
  const [message,setMessage] = useState(null);
  const [modalShow,setModalShow] = useState(false);
  const [types, setTypes] = useState([])
  const [pet,setPet] = useState(emptyItem);  
  const [petId,setPetId] = useState(pathArray[2]);
  const editPetFormRef=useRef();
  
  useEffect( () => setupPet(),[]);  
  
  function setupPet(){
      if (petId !== "new" && pet.id==null) { 
        const pet = fetch(
            `/api/v1/pets/${petId}`, 
            {
              headers: {
              Authorization: `Bearer ${jwt}`,
            },
          })
          .then((p) => p.json())
          .then((p) => {
            if(p.message){ 
              setMessage(pet.message);
              setModalShow( true );
            }else {
              setPet(p);
              setPetId(p.id);                
            }
          }).catch(m =>{
            setMessage(m);
            setModalShow( true );
          });          
    }    
    if(types.length===0){
      fetch(`/api/v1/pets/types`, {
          headers: {
            Authorization: `Bearer ${jwt}`,
          },
        }
      ).then(data => data.json()).then((data) => {
        if(!data.message)
          setTypes(data);
        else{
          setMessage(data.message);
          setModalShow(true);
        }
      }).catch(error => {setMessage(error);setModalShow(true);});
      }
  }

  function handleChange(event) {
    const target = event.target;
    const value = target.value;
    const name = target.name;
    let newPet = { ...pet };
    if (name === "type")
      newPet.type= types.filter(
                (type) => type.id === Number(value))[0];
    else 
      newPet.name = value;
    setPet(newPet);
  }  

  async function handleSubmit({ values }) {

    if (!editPetFormRef.current.validate()) return;

    const mypet = {
      id: pet.id,
      name: values["name"],
      birthDate: values["birthDate"],
      type: types.filter((type) => type.name === values["type"])[0],
      owner: pet.owner,
    };

    const submit = await (await fetch("/api/v1/pets" + (pet.id ? "/" + petId : ""), 
      {
        method: mypet.id ? "PUT" : "POST",
        headers: {
          Authorization: `Bearer ${jwt}`,
          Accept: "application/json",
          "Content-Type": "application/json",
        },
        body: JSON.stringify(mypet),
      }
    )).json();

    if (submit.message){
      setMessage(submit.message);
      setModalShow(true);
    }
    else window.location.href = `/myPets`;
  }
  
    const title = (
      <h2 className="text-center">{pet.id ? "Edit Pet" : "Add Pet"}</h2>
    );

    petEditFormInputs.forEach(i => i.handleChange=handleChange);
    

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

    function handleShow() {
      setModalShow(false);
      setMessage(null);
    }

    let modal = <></>;
    if (message) {
      const show = modalShow;
      const closeBtn = (
        <button className="close" onClick={handleShow} type="button">
          &times;
        </button>
      );
      const cond = message.includes("limit");
      modal = (
        <div>
          <Modal isOpen={show} toggle={handleShow} keyboard={false}>
            {cond ? (
              <ModalHeader>Warning!</ModalHeader>
            ) : (
              <ModalHeader toggle={handleShow} close={closeBtn}>
                Error!
              </ModalHeader>
            )}
            <ModalBody>{message || ""}</ModalBody>
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
            ref={editPetFormRef}
            inputs={petEditFormInputs}
            onSubmit={handleSubmit}
            buttonText="Save"
            buttonClassName="auth-button"
          />
        </div>
        {modal}
      </div>
    );
  }

