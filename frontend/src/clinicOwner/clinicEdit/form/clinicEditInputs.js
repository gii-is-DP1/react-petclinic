import { formValidators } from "../../../validators/formValidators";

export const clinicEditInputs = [
    {
        tag: "Name",
        name: "name",
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
        tag: "Telephone",
        name: "telephone",
        type: "tel",
        defaultValue: "",
        isRequired: true,
        validators: [formValidators.notEmptyValidator, formValidators.validPhoneNumberValidator],
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