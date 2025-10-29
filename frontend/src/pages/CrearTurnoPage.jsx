import { useState, useEffect } from 'react';
import api from '../api';
import { useNavigate } from 'react-router-dom';

export default function CrearTurnoPage() {
  const [doctores, setDoctores] = useState([]);
  const [doctorId, setDoctorId] = useState('');
  const [fechaHora, setFechaHora] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const fetchDoctores = async () => {
      const res = await api.get('/doctores');
      setDoctores(res.data);
    };
    fetchDoctores();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await api.post('/turnos', {
        doctor: { id: parseInt(doctorId) },
        fechaHora
      });
      alert('Turno creado con éxito');
      navigate('/turnos');
    } catch (err) {
      alert(err.response?.data?.message || 'Error creando el turno');
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <h2>Crear Turno</h2>
      <label>Doctor:</label>
      <select value={doctorId} onChange={e => setDoctorId(e.target.value)} required>
        <option value="">Seleccionar</option>
        {doctores.map(d => (
          <option key={d.id} value={d.id}>{d.nombre} - {d.especialidad}</option>
        ))}
      </select>
      <label>Fecha y Hora:</label>
      <input type="datetime-local" value={fechaHora} onChange={e => setFechaHora(e.target.value)} required />
      <button type="submit">Crear Turno</button>
    </form>
  );
}
