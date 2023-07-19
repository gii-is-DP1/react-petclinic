import { formValidators } from "../../../validators/formValidators";

export const ownerEditInputs = [
    {
        tag: "First Name",
        name: "firstName",
        type: "text",
        defaultValue: "",
        isRequired: true,
        validators: [formValidators.notEmptyValidator],
    },
    {
        tag: "Last Name",
        name: "lastName",
        type: "text",
        defaultValue: "",
        isRequired: true,
        validators: [formValidators.notEmptyValidator],
    },
    {
        tag: "Address",
        name: "address",
        type: "text",
        defaultValue: "",
        isRequired: true,
        validators: [formValidators.notEmptyValidator],
    },
    {
        tag: "City",
        name: "city",
        type: "text",
        defaultValue: "",
        isRequired: true,
        validators: [formValidators.notEmptyValidator],
    },
    {
        tag: "Telephone",
        name: "telephone",
        type: "tel",
        defaultValue: "",
        isRequired: true,
        validators: [formValidators.notEmptyValidator, formValidators.telephoneValidator],
    },
    {
        tag: "Plan",
        name: "plan",
        type: "select",
        values: ["None", "BASIC", "GOLD", "PLATINUM"],
        defaultValue: "None",
        isRequired: true,
        validators: [formValidators.notEmptyValidator, formValidators.notNoneTypeValidator],
    },
];