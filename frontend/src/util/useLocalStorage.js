import React from "react";

function useLocalState(key, defaultValue){
    const [value,setValue] = React.useState(()=> {
        const stickyValue = window.localStorage.getItem(key);

        return stickyValue !== null
            ? JSON.parse(stickyValue)
            : defaultValue;
    });

    React.useEffect(()=>{
        window.localStorage.setItem(key, JSON.stringify(value));
    },[key,value])

    return [value,setValue];
}

export {useLocalState}