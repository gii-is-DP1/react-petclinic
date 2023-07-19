import { formValidators } from "../../../validators/formValidators";
import { registerFormClinicOwnerInputs } from "./registerFormClinicOwnerInputs";

export const registerFormVetInputs = [
  ...registerFormClinicOwnerInputs,
  {
    tag: "City",
    name: "city",
    type: "text",
    defaultValue: "",
    isRequired: true,
    validators: [formValidators.notEmptyValidator],
  },
  {
    tag: "Clinic",
    name: "clinic",
    type: "select",
    values: ["None"],
    defaultValue: "",
    isRequired: true,
    validators: [formValidators.notEmptyValidator, formValidators.notNoneTypeValidator],
  },
];
