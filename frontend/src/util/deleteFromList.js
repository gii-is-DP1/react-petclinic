import tokenService from "../services/token.service";
import getDeleteAlertsOrModal from "./getDeleteAlertsOrModal";


/**
 * Function to delete an item from a list via a DELETE request and update the state accordingly.
 * This function also handles alerts and modals based on the server's response.
 *
 * @param {string} url - The URL to send the DELETE request to.
 * @param {string} id - The unique identifier of the item to be deleted.
 * @param {[Array, function]} state - An array containing the current state and the state setter function.
 * @param {[Array, function]} alerts - An array containing the current list of alerts and the state setter function for alerts.
 * @param {function} setMessage - A function to set the message for the modal in case of an error or success.
 * @param {function} setVisible - A function to control the visibility of the modal.
 * @param {object} [options={}] - Optional parameters:
 *   - `date` {Date}: If provided, only items created before this date will be deleted from the state.
 *   - `filtered` {Array}: An optional filtered list that needs to be updated in addition to the main state.
 *   - `setFiltered` {function}: A function to update the `filtered` state if applicable.
 *
 * @example
 * deleteFromList('/api/items/123', '123', [items, setItems], [alerts, setAlerts], setMessage, setVisible);
 */

export default function deleteFromList(url, id, [state, setState], [alerts, setAlerts], setMessage, setVisible, options = {}) {
    const jwt = tokenService.getLocalAccessToken();
    let confirmMessage = window.confirm("Are you sure you want to delete it?");
    if (confirmMessage) {
        fetch(url, {
            method: "DELETE",
            headers: {
                "Authorization": `Bearer ${jwt}`,
                Accept: "application/json",
                "Content-Type": "application/json",
            },
        })
            .then((response) => {
                if (response.status === 200 || response.status==204) {
                    if (options.date)
                        setState(state.filter((i) => i.id !== id && i.creationDate < options.date));
                    else if (options.filtered && options.setFiltered) {
                        setState(state.filter((i) => i.id !== id));
                        options.setFiltered(options.filtered.filter((i) => i.id !== id));
                    }
                    else
                        setState(state.filter((i) => i.id !== id));
                }
                return response.text();
            })
            .then(text => {if(text!=='')
                        getDeleteAlertsOrModal(JSON.parse(text), id, alerts, setAlerts, setMessage, setVisible);
            })
            .catch((err) => {
                console.log(err);
                alert("Error deleting entity")
            });
    }
}
