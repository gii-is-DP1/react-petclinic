import { useEffect, useState } from "react";

export default function useIntervalFetchState(initial, url, jwt, setMessage, setVisible, id = null,delay = 1000) {
    const [data, setData] = useState(initial);
    useEffect(() => {
        let intervalID=setInterval(()=>{
          
        

        if (url) {
            if (!id || id !== "new") {
                let ignore = false;
                fetch(url, jwt?{
                    headers: {
                        "Authorization": `Bearer ${jwt}`,
                    },
                }:{})
                    .then(response => response.json())
                    .then(json => {
                        if (!ignore) {
                            if (json.message) {
                                if(setMessage!==null){
                                    setMessage(json.message);
                                    setVisible(true);
                                }else
                                    window.alert(json.message);
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
                
            }

        }
    },delay);
    return () => {
        clearInterval(intervalID);
    };
    }, [url, id, jwt, setMessage, setVisible]);
    return [data, setData];
}
