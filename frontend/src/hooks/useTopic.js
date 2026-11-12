import { useEffect, useRef } from "react";
import { Client } from "@stomp/stompjs";

/** Se suscribe a un topic STOMP mientras el componente esté montado. */
export default function useTopic(destination, onMessage) {
  const handlerRef = useRef(onMessage);
  handlerRef.current = onMessage; // siempre el callback más reciente

  useEffect(() => {
    if (!destination) return;
    const protocol = window.location.protocol === "https:" ? "wss" : "ws";
    const client = new Client({
      brokerURL: `${protocol}://${window.location.host}/ws`,
      reconnectDelay: 5000,
      onConnect: () => {
        client.subscribe(destination, (frame) =>
          handlerRef.current(JSON.parse(frame.body))
        );
      },
    });
    client.activate();
    return () => client.deactivate(); // cerramos al desmontar
  }, [destination]);
}
