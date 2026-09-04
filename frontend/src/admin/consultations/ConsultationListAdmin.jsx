import { useState } from 'react';
import { Link } from 'react-router-dom';
import { Button, Col, Container, Input, Row, Table } from 'reactstrap';
import tokenService from '../../services/token.service';
import consultationService from '../../services/consultation.service';
import useFetchState from '../../util/useFetchState';
import getErrorModal from '../../util/getErrorModal';

const jwt = tokenService.getLocalAccessToken();

export default function ConsultationListAdmin() {
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [consultations, setConsultations] = useFetchState([], `/api/v1/consultations`, jwt, setMessage, setVisible);
    const [filtered, setFiltered] = useState([]);
    const [search, setSearch] = useState("");
    const [filter, setFilter] = useState("");
    const [alerts, setAlerts] = useState([]);

    function handleSearch(event) {
        const value = event.target.value;
        let filteredConsultations;
        if (value === "") {
            if (filter !== "")
                filteredConsultations = consultations.filter((i) => i.status === filter);
            else
                filteredConsultations = consultations;
        } else {
            if (filter !== "")
                filteredConsultations = consultations.filter((i) => i.status === filter && i.owner.user.username.includes(value));
            else
                filteredConsultations = consultations.filter((i) => i.owner.user.username.includes(value));
        }
        setFiltered(filteredConsultations);
        setSearch(value);
    }

    function handleFilter(event) {
        const value = event.target.value;
        let filteredConsultations;
        if (value === "") {
            if (search !== "")
                filteredConsultations = consultations.filter((i) => i.owner.user.username.toLowerCase().includes(search));
            else
                filteredConsultations = consultations;
        } else {
            if (search !== "")
                filteredConsultations = consultations.filter((i) => i.status === value && i.owner.user.username.toLowerCase().includes(search));
            else
                filteredConsultations = consultations.filter((i) => i.status === value);
        }
        setFiltered(filteredConsultations);
        setFilter(value);
    }

    function handleClear() {
        setFiltered(consultations);
        setSearch("");
        setFilter("");
    }

    let consultationList;
    if (filtered.length === 0 && (filter !== "" || search !== "")) consultationList =
        <tr>
            <td>There are no consultations with those filter and search parameters.</td>
        </tr>
    else consultationList = consultationService.getConsultationList([consultations, setConsultations],
        [filtered, setFiltered], [alerts, setAlerts], setMessage, setVisible);
    const modal = getErrorModal(setVisible, visible, message);

    return (
        <div>
            <Container fluid style={{ marginTop: "15px" }}>
                <h1 className="text-center">Consultations</h1>
                {alerts.map((a) => a.alert)}
                {modal}
                <Row className="row-cols-auto g-3 align-items-center">
                    <Col>
                        <Button color="success" tag={Link} to="/consultations/new">
                            Add Consultation
                        </Button>
                        <Button aria-label='pending-filter' color="link" onClick={handleFilter} value="PENDING">Pending</Button>
                        <Button aria-label='answered-filter' color="link" onClick={handleFilter} value="ANSWERED">Answered</Button>
                        <Button aria-label='closed-filter' color="link" onClick={handleFilter} value="CLOSED">Closed</Button>
                        <Button aria-label='all-filter' color="link" onClick={handleFilter} value="">All</Button>
                    </Col>
                    <Col className="col-sm-3">
                        <Input type="search" aria-label='search' placeholder="Introduce an owner name to search by it" value={search || ''}
                            onChange={handleSearch} />
                    </Col>
                    <Col className="col-sm-3">
                        <Button aria-label='clear-all' color="link" onClick={handleClear} >Clear All</Button>
                    </Col>
                </Row>
                <Table aria-label='consultations' className="mt-4">
                    <thead>
                        <tr>
                            <th>Title</th>
                            <th>Status</th>
                            <th>Owner</th>
                            <th>Pet</th>
                            <th>Clinic</th>
                            <th>Sent To</th>
                            <th>Creation Date</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>{consultationList}</tbody>
                </Table>
            </Container>
        </div>
    );
}
