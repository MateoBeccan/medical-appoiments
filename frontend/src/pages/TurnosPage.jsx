import { useEffect, useState } from "react";
import API from "../api/api";
import TurnoCard from "../components/TurnoCard";

export default function TurnosPage() {
  const [turnos, setTurnos] = useState([]);

  useEffect(() => {
    API.get("/turnos")
      .then((res) => setTurnos(res.data))
      .catch((err) => console.error(err));
  }, []);

  return (
    <div className="p-6">
      <h1 className="text-2xl mb-4">Turnos</h1>
      <div className="grid gap-4">
        {turnos.map((t) => (
          <TurnoCard key={t.id} turno={t} />
        ))}
      </div>
    </div>
  );
}
