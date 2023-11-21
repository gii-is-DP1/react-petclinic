import { useState, useEffect } from "react";
import { Table } from "reactstrap";

export default function Statement(){
    const [customer, setCustomer] = useState([]);
    const [totalAmount, setTotalAmount] = useState([]);
    const [frequentRenterPoints, setFrequentRenterPoints] = useState([]);
    const [rentalsInfo, setRentalsInfo] = useState([]);



   
    useEffect(setUp, []);

    function setUp(){  
      fetch(`/api/v1/customers/1/statement`).then(response => response.json())
                                            .then(response => {
                                              setCustomer(response.customer);
                                              setFrequentRenterPoints(response.frequentRenterPoints);
                                              setTotalAmount(response.totalAmount);
                                              setRentalsInfo(response.customer.rentals);
                                            })
      }
 

  const rentalsInfoList = rentalsInfo.map( rentalInfo => {
    return (
      <tr key={rentalInfo.id}>
        <td>{rentalInfo.movie.title}</td>
        <td>{rentalInfo.amount}</td>
      </tr>
    );
  });

  return (

              <div>
                <div>
                  <h1 className="text-center">Rental record for {customer.name}</h1>
                </div>
                <div>
                    <Table aria-label="pets" className="mt-4">
                                  <thead>
                                    <tr>
                                      <th>Movie Title</th>
                                      <th>Amount owed</th>
                                    </tr>
                                  </thead>
                                  <tbody>{rentalsInfoList}</tbody>
                      </Table>
                </div>
                <div>
                  <h2 className="text-center">Amount owed is {totalAmount}</h2>
                  <h2 className="text-center">You earned {frequentRenterPoints} frequent renter points</h2>
                </div>
              </div>

  );
  
}

