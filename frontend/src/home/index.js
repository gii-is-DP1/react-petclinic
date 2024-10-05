import React from 'react';
import '../App.css';
import '../static/css/home/home.css'; 

import { useLocalState } from '../util/useLocalStorage';

export default function Home(){

    const [jwt,] = useLocalState("jwt", "");

    return(
        <div className="home-page-container">
            <div className="hero-div">
                <h1>!Bienvenido a Dixit!</h1>
                {!jwt? <h3 className='mt-3'>Inicie sesión para jugar</h3> : <h3 className='mt-3'>!Ya estás listo para jugar!</h3>}

                {!jwt ? 
                    <div style={{ display: 'flex', justifyContent: 'space-between', width: 420 , marginTop: 15 }}>
                        <a href="/register" style={{borderRadius: 8, padding: 10, textDecoration: 'none',
                        fontSize: 22, color: 'lightgray', width: 200, textAlign: 'center',
                        backgroundColor: '#2f324f', marginTop: 20}}>
                        Registrarse</a> 

                        <a href="/login" style={{borderRadius: 8, padding: 10, textDecoration: 'none',
                        fontSize: 22, color: 'lightgray', width: 200, textAlign: 'center',
                        backgroundColor: '#2f324f', marginTop: 20}}>
                        Iniciar Sesión</a> 
                    </div> 
                : 
                    <div style={{ display: "flex", alignItems: 'center', justifyContent:"space-between", width: 420, marginTop: 20 }}>                        
                        <a href="/createGame" style={{borderRadius: 8, padding: 10, textDecoration: 'none',
                        fontSize: 22, color: 'lightgray', width: 200, textAlign: 'center',
                        backgroundColor: '#2f324f', marginTop: 20}}>
                        {`Crear`}<br />{`Partida`}</a> 

                        <a href="/joinGame" style={{borderRadius: 8, padding: 10, textDecoration: 'none',
                        fontSize: 22, color: 'lightgray', width: 200, textAlign: 'center',
                        backgroundColor: '#2f324f', marginTop: 20}}>
                        Unirse a Partida</a> 
                    </div>
                }

            </div>
        </div>
    );
}