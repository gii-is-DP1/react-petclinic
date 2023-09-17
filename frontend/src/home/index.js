import React from 'react';
import '../App.css';
import '../static/css/home/home.css';
import logo from '../static/images/logonuevo.png'

export default function Home(){
    return(
        <div className="home-page-container">
            <div className="hero-div">
                <h1>Petclinic</h1>
                <h3>---</h3>
                <img src={logo}/> 
                <h3>Find the best vet for your pet</h3>
            </div>
        </div>
    );
}