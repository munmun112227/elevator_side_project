package org.example.elevator_side_project.controller;

import lombok.Data;
import org.example.elevator_side_project.Direction;
import org.example.elevator_side_project.config.ElevatorProperties;
import org.example.elevator_side_project.elevator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Controller
public class ElevatorWebSocketController {

    @Autowired
    private elevator elevatorInstance;

    @Autowired
    private ElevatorProperties properties;

    @SubscribeMapping("/state")
    public Map<String, Object> handleSubscription() {
        // Send initial state and config when client subscribes to /topic/state
        Map<String, Object> data = new HashMap<>();
        data.put("config", properties);
        data.put("state", elevatorInstance);
        return data;
    }

    @MessageMapping("/hallCall")
    public void hallCall(HallCallRequest request) {
        elevatorInstance.addHallCall(request.getFloor(), request.getDirection());
    }

    @MessageMapping("/carCall")
    public void carCall(CarCallRequest request) {
        elevatorInstance.addCarCall(request.getFloor());
    }

    @MessageMapping("/doorControl")
    public void doorControl(DoorControlRequest request) {
        if ("OPEN".equalsIgnoreCase(request.getAction())) {
            elevatorInstance.openDoor();
        } else if ("CLOSE".equalsIgnoreCase(request.getAction())) {
            elevatorInstance.closeDoor();
        }
    }

    @Data
    public static class DoorControlRequest {
        private String action; // "OPEN" or "CLOSE"
    }

    @Data
    public static class HallCallRequest {
        private int floor;
        private Direction direction;
    }

    @Data
    public static class CarCallRequest {
        private int floor;
    }
}
