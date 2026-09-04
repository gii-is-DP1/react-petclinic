import { useState, useEffect } from "react";
import SwaggerUI from "swagger-ui-react";
import "swagger-ui-react/swagger-ui.css"

export default function SwaggerDocs(){
    const [docs,setDocs]=useState({});
    useEffect(() =>{loadDocs();},[]);

    async function loadDocs() {
        const mydocs = await (await fetch(`/v3/api-docs`, {
            headers: {
                "Content-Type": "application/json",
            },
        })).json();
        setDocs(mydocs);
    }

    
    
    return (
        <SwaggerUI spec={docs} url="" />
    );
    
}