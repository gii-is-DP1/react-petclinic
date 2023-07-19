import { Component } from "react";
import SwaggerUI from "swagger-ui-react";
import "swagger-ui-react/swagger-ui.css"

class SwaggerDocs extends Component {
    constructor(props) {
        super(props);
        this.state = {
            docs: {},
        };
    }

    async componentDidMount() {
        const docs = await (await fetch(`/v2/api-docs`, {
            headers: {
                "Content-Type": "application/json",
            },
        })).json();
        this.setState({ docs: docs });
    }

    render() {
        const docs = this.state.docs;
        return (
            <SwaggerUI spec={docs} url="" />
        )
    }
}
export default SwaggerDocs;