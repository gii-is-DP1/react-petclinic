import React, { useState } from 'react';
import tokenService from '../services/token.service';
import Login from '../auth/login';

const PrivateRoute = ({ children }) => {
    const jwt = tokenService.getLocalAccessToken();
    const [isLoading, setIsLoading] = useState(true);
    const [isValid, setIsValid] = useState(null);
    const [message, setMessage] = useState(null);
    if (jwt) {
        fetch(`/api/v1/auth/validate?token=${jwt}`, {
            method: 'GET',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
        }).then(response => {
            return response.json();
        }).then(isValid => {
            setMessage("Your token has expired. Please, sign in again.")
            setIsValid(isValid);
            setIsLoading(false);
        });
    } else return <Login message={message} navigation={false} />;

    if (isLoading === true) {
        return <div>Loading...</div>;
    } else return isValid === true ? children : <Login message={message} navigation={true} />
};

export default PrivateRoute;