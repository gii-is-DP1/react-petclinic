import React from "react";

/**
 * Custom function to manage state that is synchronized with `localStorage`.
 * This function allows you to persist state across sessions by saving it to `localStorage`.
 *
 * @param {string} key - The key under which the state value will be stored in `localStorage`.
 * @param {any} defaultValue - The default value to be used if no value is found in `localStorage` for the given key.
 * 
 * @returns {[any, function]} - Returns an array with two elements:
 *   - `value`: The current state value, initialized either from `localStorage` or with the `defaultValue`.
 *   - `setValue`: A function to update the state value. When called, it also updates the value in `localStorage`.
 *
 * @example
 * const [name, setName] = useLocalState('name', 'Guest');
 */

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