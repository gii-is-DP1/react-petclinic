import { Alert } from "reactstrap";


/**
 * Helper function to dismiss an alert by its ID.
 *
 * @param {Array} alerts - The current list of alerts being displayed.
 * @param {string} id - The unique identifier of the alert to be dismissed.
 * @param {function} setAlerts - A function to update the list of alerts by removing the dismissed alert.
 */
function dismiss(alerts, id, setAlerts) {
    setAlerts(alerts.filter(i => i.id !== id))
}

/**
 * Function to handle the display of alerts or modals based on a server response.
 * If the response contains a status code, it displays a modal with a message.
 * Otherwise, it adds a dismissible alert to the list of alerts.
 *
 * @param {object} json - The JSON response object from the server. It may contain a status code and message.
 * @param {string} id - A unique identifier for the alert.
 * @param {Array} alerts - The current list of alerts being displayed.
 * @param {function} setAlerts - A function to update the list of alerts.
 * @param {function} setMessage - A function to set the message for the modal.
 * @param {function} setVisible - A function to control the visibility of the modal.
 *
 * @example
 * getDeleteAlertsOrModal(responseJson, "123", currentAlerts, setCurrentAlerts, setModalMessage, setModalVisible);
 */

export default function getDeleteAlertsOrModal(json, id, alerts, setAlerts, setMessage, setVisible) {
    if (json.statusCode) {
        setMessage(json.message);
        setVisible(true);
    }
    else {
        const alertId = `alert-${id}`
        setAlerts([
            ...alerts,
            {
                alert: <Alert toggle={() => dismiss(alerts, alertId, setAlerts)} key={"alert-" + id} id={alertId} color="info">
                    {json.message}
                </Alert>,
                id: alertId
            }
        ]);
    }
}

