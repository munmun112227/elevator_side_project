import { ref, onMounted, onUnmounted } from 'vue';
import { Client } from '@stomp/stompjs';

export function useElevatorWebSocket() {
  const config = ref({ totalFloors: 10, elevatorCount: 1 });
  const state = ref({
    currentFloor: 1,
    currentDirection: 'STOP',
    upRequests: [],
    downRequests: [],
    carRequests: [],
    upHallRequests: [],
    downHallRequests: []
  });
  
  const connected = ref(false);
  let stompClient = null;

  const connect = () => {
    stompClient = new Client({
      brokerURL: 'ws://localhost:8080/ws',
      debug: function (str) {
        console.log(str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    stompClient.onConnect = (frame) => {
      connected.value = true;
      stompClient.subscribe('/app/state', (message) => {
        const data = JSON.parse(message.body);
        config.value = data.config;
        state.value = data.state;
      });
      stompClient.subscribe('/topic/state', (message) => {
        const data = JSON.parse(message.body);
        config.value = data.config;
        state.value = data.state;
      });
    };

    stompClient.onWebSocketError = (error) => {
      console.error('Error with websocket', error);
    };

    stompClient.onStompError = (frame) => {
      console.error('Broker reported error: ' + frame.headers['message']);
      console.error('Additional details: ' + frame.body);
    };

    stompClient.activate();
  };

  const disconnect = () => {
    if (stompClient) {
      stompClient.deactivate();
    }
    connected.value = false;
  };

  const sendHallCall = (floor, direction) => {
    if (stompClient && stompClient.connected) {
      stompClient.publish({
        destination: '/app/hallCall',
        body: JSON.stringify({ floor, direction })
      });
    }
  };

  const sendCarCall = (floor) => {
    if (stompClient && stompClient.connected) {
      stompClient.publish({
        destination: '/app/carCall',
        body: JSON.stringify({ floor })
      });
    }
  };

  const sendDoorCommand = (action) => {
    if (stompClient && stompClient.connected) {
      stompClient.publish({
        destination: '/app/doorControl',
        body: JSON.stringify({ action })
      });
    }
  };

  onMounted(() => {
    connect();
  });

  onUnmounted(() => {
    disconnect();
  });

  return {
    config,
    state,
    connected,
    sendHallCall,
    sendCarCall,
    sendDoorCommand
  };
}
