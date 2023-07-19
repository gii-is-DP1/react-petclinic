# Form Generator

## Dependencies

- filepond
- filepond-plugin-file-encode
- filepond-plugin-image-exif-orientation
- filepond-plugin-image-preview
- react-filepond
- react-simple-timefield

To install them all, run:

```
yarn add filepond@4.30.4 filepond-plugin-file-encode@2.1.10 filepond-plugin-image-exif-orientation@1.0.11 filepond-plugin-image-preview@4.6.11 react-filepond@7.1.2
```

Form Generator is a react component that symplifies the forms' creation task.

I will receive a data file where the form's inputs are modeled, and render them into a nice looking form.
All the colors can be customizable inside the `:root` block at the `css/formGenerator.css` file.

The FormGenerator component has the following attributes:

|   **Attribute**  	|                                                               **Description**                                                              	|      **Type**     	| **Default** 	|
|----------------	|------------------------------------------------------------------------------------------------------------------------------------------	    |:-----------------:	|:-----------:	|
|      inputs      	| Array of input objects to be rendered by the generator                                                                                     	| Array<br><Object> 	|      []     	|
|     onSubmit     	| Function to execute when the form is submitted. All the introduced values can be retrieved as the function parameter by casting `{values}` 	|      function     	|    ()=>{}   	|
|  numberOfColumns 	| Determines on how many columns will the inputs of the form organize                                                                        	|       number      	|      1      	|
| childrenPosition 	| If FormGenerator has children inputs, this attribute determines on which position will they be rendered                                    	|       number      	|      0      	|
|    buttonText    	| The text to be rendered on the submit button                                                                                               	|       String      	|   "Enviar"  	|
|  buttonClassname 	| Classes to add to the submit button. Use this attribute to give styles                                                                     	|       String      	|      ""     	|
|  listenEnterKey  	| Indicates wheter the form must be sent when pressing `Enter` on the keyboard or not                                                        	|        bool       	|    false    	|
|    scrollable  	| If the form's height exceeds the box on which it's contained, set this attribute to true in order to render a scrollable form                 |        bool       	|    false    	|

# Inputs modeling

As you might have seen, the form generator receives as the `inputs` attribute an array of inputs objects. The syntax of those objects is, in general, as follows:

```

{
    tag: String,
    name: String,
    type: String,
    defaultValue: String,
    isRequired: bool,
    validators: Array<Object>
},

```

Each attribute represents:

- **tag:** The placeholder and label that the field will show
- **name:** The name of the variable that contains the data of the input when retrieving the `{values}` object on the submitting function
- **type:** The type of the input to be rendered
- **defaultValue:** The initial value of the field
- **isRequired:** If set to true, the input rendered will show the '*' that symbolizes a required field and the form validator will force to complete it
- **validators:** The list of validations to apply to the input data when the form is validated

## Inputs types

One of the attributes of an input is it's type. This value represent which type of input will be rendered, such as text or a password. The possible values that this field can receive are:

- text
- password
- number
- email
- textarea
- select
- files
- interval
- date
- time

Depending on which you need, the input object structure may change. This will happen if you are using: `select` or `interval`.

#### select

```

{
    tag: String,
    name: String,
    type: String,
    values: Array<String>
    defaultValue: String,
    isRequired: bool,
    validators: Array<Object>
},

```

If you need it, have in mind that the given `defaultValue` must be inside `values`.

#### interval

```

{
    tag: String,
    name: String,
    type: String,
    min: number,
    max: number,
    isRequired: bool,
    validators: Array<Object>
},

```

## Validators

As you might notice, `validators` atribute is also an array of objects. The schema for creating each validator is the following:

```

{
    validate: function,
    message: String
},

```

- **validate:** The function that will be executed to the input data. It must return `true` if the validation is correct; and `false` if you want the message to be rendered and avoid the form submit. The function must have the following structure:

```

function (value){
    ...
}

```

or

```

(value) => {
    ...
}

```

- **message:** The text that will be rendered if the validation fails

As a good practise, we recommend you to save your validators in a different file, so you can reuse them between your inputs!