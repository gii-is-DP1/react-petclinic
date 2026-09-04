import { useEffect, useState } from "react";

/**
 * Custom React hook to fetch data from a specified URL and manage it in the component's state.
 * This hook supports JWT-based authentication.
 *
 * @param {string} url - The URL from which to fetch the data. The fetch request will not be initiated if the URL is not provided.
 * @param {string} jwt - The JSON Web Token (JWT) for authorization. The token will be included in the request headers as `Authorization: Bearer <jwt>`.
 * 
 * @returns {Array} - The state variable `data` containing the fetched data, initialized as an empty array.
 *
 * @example
 * const data = useFetchData('https://api.example.com/data', jwtToken);
 */

export default function useFetchData(url, jwt) {
    const [data, setData] = useState([]);
    useEffect(() => {
        if (url) {
            let ignore = false;
            fetch(url, {
                headers: {
                    "Authorization": `Bearer ${jwt}`,
                },
            })
                .then(response => response.json())
                .then(json => {
                    if (!ignore) {
                        setData(json);
                    }
                }).catch((message) => alert(message));
            return () => {
                ignore = true;
            };
        }
    }, [url, jwt]);
    return data;
}