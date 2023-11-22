import { useState } from "react";
import { useParams } from "react-router-dom";
import tokenService from "../services/token.service";
import getErrorModal from "../util/getErrorModal";
import useIntervalFetchState from "../util/useIntervalFetchState";
import galaxian from './../static/images/galaxian.png';
import background from './../static/images/galaxian-fondo.png';


const jwt = tokenService.getLocalAccessToken();

export default function GameOfLife( ) {
    const {planetName}= useParams();
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [planet, setPlanet] = useIntervalFetchState(
        {planet:[[]],population:0},
        `/api/v1/universe/planet/${planetName}`,
        jwt,
        setMessage,
        setVisible,
        null,
        5000
    );
    const planetSurface=planet.planet.map((country) => {
        let cities =country.map((city)=> {
            return(<td><img src={city?galaxian:background} alt='Planet surface image'></img></td>);
        });
        return (
            <tr>{cities}</tr>
        );
    });
    
    const modal = getErrorModal(setVisible, visible, message);

    return (
        <div>
            {modal}
            <h1>{planetName}</h1>
            <h2>Population: {planet.population}</h2>
            <table cellspacing="0" cellpadding="0">
                    <tbody>
                        {planetSurface}
                    </tbody>
            </table>
        </div>
    );
}