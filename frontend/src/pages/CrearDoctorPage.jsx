import { useState } from 'react';
import api from '../api';

export default function CrearDoctorPage() {
  const [nombre, setNombre] = useState('');
  const [especialidad, setEspecialidad] = useState('');
  const [email, setEmail] = useState('');
  const [userId, setUserId] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await api.post('/doctores', {
        nombre,
        especialidad,
        email,
        user: { id: parseInt(userId) }
      });
      alert('Doctor creado con éxito');
      setNombre('');
      setEspecialidad('');
      setEmail('');
      setUserId('');
    } catch (err) {
      alert(err.response?.data?.message || 'Error creando doctor');
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <h2>Crear Doctor</h2>
      <input placeholder="Nombre" value={nombre} onChange={e => setNombre(e.target.value)} required />
      <input placeholder="Especialidad" value={especialidad} onChange={e => setEspecialidad(e.target.value)} required />
      <input placeholder="Email" type="email" value={email} onChange={e => setEmail(e.target.value)} required />
      <input placeholder="ID de usuario" value={userId} onChange={e => setUserId(e.target.value)} required />
      <button type="submit">Crear Doctor</button>
    </form>
  );
}
