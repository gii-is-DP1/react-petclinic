import { useState } from "react";
import tokenService from "../services/token.service";
import useFetchState from "../util/useFetchState";
import getErrorModal from "../util/getErrorModal";
import { Button, ButtonGroup, Table } from "reactstrap";
import { Link } from "react-router-dom";

const jwt = tokenService.getLocalAccessToken();

export default function GameList() {
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [games, setGames] = useFetchState(
      [],
      `/api/v1/game`,
      jwt,
      setMessage,
      setVisible
    );

    const gamesList =
    games.map((game) => {
        return (
          <tr key={game.id}>
            <td className="text-center">{game.name}</td>
            <td className="text-center">{game.code?"private":"public"}</td>
            <td className="text-center">{game.start?'waiting':game.start}</td>
            <td className="text-center">{game.finish?game.finish:''}</td>
            <td className="text-center">
              <ButtonGroup>
                <Button
                  size="sm"
                  color="primary"
                  aria-label={"edit-" + game.name}
                  tag={Link}
                  to={"/games/" + game.id}
                >
                  Edit
                </Button>
                <Button
                  size="sm"
                  color="danger"
                  aria-label={"delete-" + game.name}
                  onClick={() => {
                    let confirmMessage = window.confirm("Are you sure you want to delete it?");

                    if(!confirmMessage) return;

                    fetch(`/api/v1/game/${game.id}`, {
                      method: "DELETE",
                      headers: {
                        "Content-Type": "application/json",
                        Authorization: `Bearer ${jwt}`,
                      },
                    })
                      .then((res) => {
                        if (res.status === 204) {
                          setMessage("Deleted successfully");
                          setVisible(true);
                          setGames(games.filter((g) => g.id!=game.id));
                        }
                      })
                      .catch((err) => {
                        setMessage(err.message);
                        setVisible(true);
                      });
                  }}
                >
                  Delete
                </Button>
              </ButtonGroup>
            </td>
          </tr>
        );
      });

      
  const modal = getErrorModal(setVisible, visible, message);

  return (
    <div>
      <div className="admin-page-container">
        <h1 className="text-center">Games</h1>        
        {modal}
        <div className="float-right">
          <Button color="success" tag={Link} to="/games/new">
            Add Game
          </Button>
        </div>
        <div>
          <Table aria-label="gamess" className="mt-4">
            <thead>
              <tr>
                <th width="15%" className="text-center">Name</th>
                <th width="15%" className="text-center">Type</th>
                <th width="15%" className="text-center">Start</th>
                <th width="15%" className="text-center">Finish</th>
                <th width="30%" className="text-center">Actions</th>
              </tr>
            </thead>
            <tbody>{gamesList}</tbody>
          </Table>
        </div>
      </div>
    </div>
  );

    
}
