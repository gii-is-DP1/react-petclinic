import React from 'react';
import '../App.css';
import '../static/css/home/home.css'; 

export default function Home(){
    return(
        <div className="home-page-container">
            <div className="hero-div">
                <h1>Petclinic</h1>
                <h3>---</h3>
                <h3>Find the best vet for your pet</h3>                
            </div>
        </div>
    );
}