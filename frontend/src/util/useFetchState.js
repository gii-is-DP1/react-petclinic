import { useEffect, useState } from "react";

export default function useFetchState(initial, url, jwt, setMessage, setVisible, id = null) {
    const [data, setData] = useState(initial);
    useEffect(() => {
        if (url) {
            if (!id || id !== "new") {
                let ignore = false;
                fetch(url, {
                    headers: {
                        "Authorization": `Bearer ${jwt}`,
                    },
                })
                    .then(response => response.json())
                    .then(json => {
                        if (!ignore) {
                            if (json.message) {
                                setMessage(json.message);
                                setVisible(true);
                            }
                            else {
                                setData(json);
                            }
                        }
                    }).catch((message) => {
                        console.log(message);
                        setMessage('Failed to fetch data');
                        setVisible(true);
                    });
                return () => {
                    ignore = true;
                };
            }

        }
    }, [url, id, jwt, setMessage, setVisible]);
    return [data, setData];
}