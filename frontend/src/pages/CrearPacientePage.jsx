import { useState } from 'react';
import api from '../api';

export default function CrearPacientePage() {
  const [nombre, setNombre] = useState('');
  const [email, setEmail] = useState('');
  const [telefono, setTelefono] = useState('');
  const [userId, setUserId] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await api.post('/pacientes', {
        nombre,
        email,
        telefono,
        user: { id: parseInt(userId) }
      });
      alert('Paciente creado con éxito');
      setNombre('');
      setEmail('');
      setTelefono('');
      setUserId('');
    } catch (err) {
      alert(err.response?.data?.message || 'Error creando paciente');
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <h2>Crear Paciente</h2>
      <input placeholder="Nombre" value={nombre} onChange={e => setNombre(e.target.value)} required />
      <input placeholder="Email" type="email" value={email} onChange={e => setEmail(e.target.value)} required />
      <input placeholder="Teléfono" value={telefono} onChange={e => setTelefono(e.target.value)} required />
      <input placeholder="ID de usuario" value={userId} onChange={e => setUserId(e.target.value)} required />
      <button type="submit">Crear Paciente</button>
    </form>
  );
}
