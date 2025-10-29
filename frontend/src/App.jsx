import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Navbar from './components/Navbar';
import LoginPage from './pages/LoginPage';
import TurnosPage from './pages/TurnosPage';
import CrearTurnoPage from './pages/CrearTurnoPage';
import CrearPacientePage from './pages/CrearPacientePage';
import CrearDoctorPage from './pages/CrearDoctorPage';

function App() {
  const token = localStorage.getItem('token');

  return (
    <Router>
      <Navbar />
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/turnos" element={token ? <TurnosPage /> : <Navigate to="/login" />} />
        <Route path="/crear-turno" element={token ? <CrearTurnoPage /> : <Navigate to="/login" />} />
        <Route path="/crear-paciente" element={token ? <CrearPacientePage /> : <Navigate to="/login" />} />
        <Route path="/crear-doctor" element={token ? <CrearDoctorPage /> : <Navigate to="/login" />} />
      </Routes>
    </Router>
  );
}

export default App;
