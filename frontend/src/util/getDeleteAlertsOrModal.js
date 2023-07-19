import { Alert } from "reactstrap";

function dismiss(alerts, id, setAlerts) {
    setAlerts(alerts.filter(i => i.id !== id))
}

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