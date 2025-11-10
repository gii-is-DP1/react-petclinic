import {
    Table
    } from "reactstrap";
    export default function DeveloperList() {
    const developers = [
    {name: "Giorno", email:"Giovanna", url:"https://jojo.fandom.com/es/wiki/Giorno_Giovanna",
    picUrl:"https://cdn-icons-png.flaticon.com/512/1956/1956683.png"},
    {name: "Alan", email:"Turing", url:"https://es.wikipedia.org/wiki/Alan_Turing",
    picUrl:"https://cdn-icons-png.flaticon.com/512/827/827364.png"}
    ]
    const developerList =
    developers.map((d) => {
        return (
            <tr key={d.id}>
                <td className="text-center">{d.name}</td>
                <td className="text-center"> {d.email} </td>
                <td className="text-center"> <a href={d.url}>{d.url}</a> </td>
                <td className="text-center"> <img src={d.picUrl} alt={d.name} width="50px"/>
                </td>
            </tr>
            );
            });
            return (
            <div>
                <div className="admin-page-container">
                    <h1 className="text-center">Developers</h1>
                    <div>
                        <Table aria-label="developers" className="mt-4">
                            <thead>
                                <tr>
                                    <th className="text-center">Name</th>
                                    <th className="text-center">e-mail</th>
                                    <th className="text-center">URL</th>
                                    <th className="text-center">Picture</th>
                                </tr>
                            </thead>
                            <tbody>{developerList}</tbody>
                        </Table>
                    </div>
                </div>
            </div>
            );
            }
            