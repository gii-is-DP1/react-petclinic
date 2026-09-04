import { Button, Modal, ModalBody, ModalFooter, ModalHeader } from "reactstrap";
/**
 * Toggles the visibility state of a modal.
 *
 * @param {function} setVisible - Function to update the visibility state.
 * @param {boolean} visible - The current visibility state of the modal.
 */
function handleVisible(setVisible, visible) {
    setVisible(!visible);
}
/**
 * Function to render an error modal with a given message.
 * The modal includes a close button and a footer button to dismiss the modal.
 *
 * @param {function} setVisible - Function to update the visibility state of the modal.
 * @param {boolean} [visible=false] - The initial visibility state of the modal. Defaults to `false`.
 * @param {string|null} [message=null] - The message to display inside the modal. If no message is provided, the modal is not rendered.
 * 
 * @returns {JSX.Element} - Returns the modal component if a message is provided; otherwise, returns an empty fragment.
 *
 * @example
 * const [visible, setVisible] = useState(false);
 * const modal = getErrorModal(setVisible, visible, "An error occurred!");
 */
export default function getErrorModal(setVisible, visible = false, message = null) {
    if (message) {
        const closeBtn = (
            <button className="close" onClick={() => handleVisible(setVisible, visible)} type="button">
                &times;
            </button>
        );
        return (
            <div>
                <Modal isOpen={visible} toggle={() => handleVisible(setVisible, visible)}
                    keyboard={false}>
                    <ModalHeader toggle={() => handleVisible(setVisible, visible)} close={closeBtn}>Alert!</ModalHeader>
                    <ModalBody>
                        {message}
                    </ModalBody>
                    <ModalFooter>
                        <Button color="primary" onClick={() => handleVisible(setVisible, visible)}>Close</Button>
                    </ModalFooter>
                </Modal>
            </div>
        )
    } else
        return <></>;
}