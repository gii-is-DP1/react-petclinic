import { formValidators } from "../../../../validators/formValidators";
import moment from "moment";

export const visitEditFormInputs = [
  {
    tag: "Date and Time",
    name: "datetime",
    type: "datetime-local",
    defaultValue: moment().format("YYYY-MM-DDTHH:mm"),
    isRequired: true,
    validators: [formValidators.notEmptyValidator],
  },
  {
    tag: "Description",
    name: "description",
    type: "textarea",
    defaultValue: "",
    isRequired: false,
    validators: [],
  },
  {
    tag: "Select City for the Visit",
    name: "city",
    type: "select",
    values: ["None"],
    defaultValue: "None",
    isRequired: true,
    validators: [formValidators.notEmptyValidator, formValidators.notNoneTypeValidator],
    onChange: null,
  },
];
