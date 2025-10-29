import { useEffect, useState } from 'react';
import api from '../api';

export default function TurnosPage() {
  const [turnos, setTurnos] = useState([]);

  useEffect(() => {
    const fetchTurnos = async () => {
      const res = await api.get('/turnos');
      setTurnos(res.data);
    };
    fetchTurnos();
  }, []);

  return (
    <div>
      <h2>Turnos</h2>
      <ul>
        {turnos.map(t => (
          <li key={t.id}>
            {t.fechaHora} - {t.doctor?.nombre} - {t.paciente?.nombre} - {t.estado}
          </li>
        ))}
      </ul>
    </div>
  );
}
