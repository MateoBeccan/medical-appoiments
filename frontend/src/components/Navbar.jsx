import { Link } from 'react-router-dom';

export default function Navbar() {
  return (
    <nav>
      <Link to="/turnos">Turnos</Link> |
      <Link to="/crear-turno">Crear Turno</Link> |
      <Link to="/crear-paciente">Crear Paciente</Link> |
      <Link to="/crear-doctor">Crear Doctor</Link> |
      <Link to="/login">Login</Link>
    </nav>
  );
}
