export default function getIdFromUrl(index) {
    return window.location.pathname.split('/')[index];
}