"use client";
import { useEffect, useState } from "react";

export default function PingTest() {
    const [msg, setMsg] = useState("cargando...");

    useEffect(() => {
        fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/ping`)
            .then((res) => res.text())
            .then(setMsg);
    }, []);

    return <p>Respuesta del backend: {msg}</p>;
}
