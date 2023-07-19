import {formValidators} from "../../../../validators/formValidators";

export const consultationEditFormInputs = [
    {
        tag: "Title",
        name: "title",
        type: "text",
        defaultValue: "",
        isRequired: true,
        validators: [formValidators.notEmptyValidator],
      },
];