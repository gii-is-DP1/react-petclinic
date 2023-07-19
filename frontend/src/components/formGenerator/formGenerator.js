import "./css/formGenerator.css";
import "filepond/dist/filepond.min.css";
import "filepond-plugin-image-preview/dist/filepond-plugin-image-preview.css";

import {
  useState,
  useImperativeHandle,
  forwardRef,
  useEffect,
  useRef,
} from "react";

import PropTypes from "prop-types";
import FormInput from "./formInput";

const FormGenerator = forwardRef((props, ref) => {
  const [formValues, setFormValues] = useState({});
  const [submitForm, setSubmitForm] = useState(false);

  let formElement = useRef(null);
  let formInputs = useRef([]);

  useImperativeHandle(ref, () => {
    return {
      validate: () => {
        let isValid = true;
        for (let i = 0; i < props.inputs.length; i++) {
          let input = props.inputs[i];
          for (let validator of input.validators) {
            if (!validator.validate(formValues[input.name])) {
              console.log(input);
              console.log(formValues[input.name])
              formInputs.current[i].setErrors([validator.message]);
              isValid = false;
            }
          }
        }

        return isValid;
      },
      updateForm: () => {
        if (Object.keys(formValues).length === 0) {
          let newFormValues = {};
          for (let input of props.inputs) {
            if (input.type === "interval") {
              newFormValues[`min_${input.name}`] = input.min;
              newFormValues[`max_${input.name}`] = input.max;
            } else {
              newFormValues[input.name] = input.defaultValue
                ? input.defaultValue
                : "";
            }
          }
          setFormValues(newFormValues);
        }
      }
    };
  });

  function handleSubmit(e) {
    e.preventDefault();
    let formValuesCopy = {};

    for (let i = 0; i < props.inputs.length; i++) {
      let input = props.inputs[i];
      if (input.type === "files") {
        formValuesCopy[input.name] = formInputs.current[i].files.map((file) =>
          file.getFileEncodeBase64String()
        );
      } else if (input.type === "interval") {
        formValuesCopy[`min_${input.name}`] = formInputs.current[i].min;
        formValuesCopy[`max_${input.name}`] = formInputs.current[i].max;
      } else {
        formValuesCopy[input.name] = formInputs.current[i].value;
      }
    }
    setFormValues(formValuesCopy);
    setSubmitForm(true);
  }

  useEffect(() => {
    if (Object.keys(formValues).length === 0) {
      let newFormValues = {};
      for (let input of props.inputs) {
        if (input.type === "interval") {
          newFormValues[`min_${input.name}`] = input.min;
          newFormValues[`max_${input.name}`] = input.max;
        } else {
          newFormValues[input.name] = input.defaultValue
            ? input.defaultValue
            : "";
        }
      }
      setFormValues(newFormValues);
    }

    if (props.scrollable) {
      formElement.current.style.overflow = "scroll";
    }

    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [formValues, props.inputs]);

  useEffect(() => {
    if (submitForm) {
      props.onSubmit({ values: formValues });
      setSubmitForm(false);
    }
  }, [submitForm]);

  useEffect(() => {
    document.addEventListener("keyup", (e) => {
      if (e.key === "Enter" && props.listenEnterKey) {
        handleSubmit(e);
      }
    });
  }, []);

  return (
    <div className="class-profile-form">
      <form
        className="class-form"
        ref={formElement}
        style={
          props.numberOfColumns > 1
            ? {
                flexDirection: "row",
                flexWrap: "wrap",
                justifyContent: "center",
              }
            : {}
        }
      >
        {Object.keys(formValues).length > 0 &&
          props.inputs.map((input, index) => {
            return (
              <>
                {props.childrenPosition !== -1 &&
                  index === props.childrenPosition &&
                  props.children}
                <FormInput
                  key={index}
                  tag={input.tag}
                  name={input.name}
                  type={input.type}
                  values={input.values}
                  defaultValue={input.defaultValue}
                  isRequired={input.isRequired}
                  minValue={input.min}
                  maxValue={input.max}
                  numberOfColumns={props.numberOfColumns}
                  validators={input.validators}
                  formValues={formValues}
                  setFormValues={setFormValues}
                  onChange={input?.onChange}
                  disabled={input.disabled}
                  ref={(input) => (formInputs.current[index] = input)}
                />
              </>
            );
          })}
        {props.childrenPosition === -1 && props.children}
      </form>

      <button onClick={handleSubmit} className={`${props.buttonClassName}`}>
        {props.buttonText}
      </button>
    </div>
  );
});

FormGenerator.propTypes = {
  inputs: PropTypes.array,
  onSubmit: PropTypes.func,
  buttonText: PropTypes.string,
  buttonClassName: PropTypes.string,
  numberOfColumns: PropTypes.number,
  childrenPosition: PropTypes.number,
  listenEnterKey: PropTypes.bool,
};

FormGenerator.defaultProps = {
  inputs: [],
  onSubmit: () => {},
  buttonText: "Enviar",
  buttonClassName: "",
  numberOfColumns: 1,
  childrenPosition: 0,
listenEnterKey: false,
};

export default FormGenerator;
