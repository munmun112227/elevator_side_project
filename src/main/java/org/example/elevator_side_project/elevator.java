package org.example.elevator_side_project;

import java.util.Comparator;
import java.util.TreeSet;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.example.elevator_side_project.config.ElevatorProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class elevator {
    @Getter
    private int currentFloor = 1;
    @Getter
    private Direction currentDirection = Direction.STOP;
    @Getter
    private TreeSet<Integer> upRequests = new TreeSet<>();
    @Getter
    private TreeSet<Integer> downRequests = new TreeSet<>(Comparator.reverseOrder());
    
    @Getter
    private TreeSet<Integer> carRequests = new TreeSet<>();
    @Getter
    private TreeSet<Integer> upHallRequests = new TreeSet<>();
    @Getter
    private TreeSet<Integer> downHallRequests = new TreeSet<>();

    private final SimpMessagingTemplate messagingTemplate;
    private final ElevatorProperties properties;

    @Autowired
    public elevator(SimpMessagingTemplate messagingTemplate, ElevatorProperties properties) {
        this.messagingTemplate = messagingTemplate;
        this.properties = properties;
    }

    public void addHallCall(int floor, Direction direction) {
        if (floor < 1 || floor > properties.getTotalFloors()) return;
        
        if (floor == currentFloor && currentDirection == Direction.STOP) {
            log.info("Already at floor {}, opening doors directly.", floor);
            if (direction == Direction.UP) {
                upRequests.remove(floor);
                upHallRequests.remove(floor);
            } else {
                downRequests.remove(floor);
                downHallRequests.remove(floor);
            }
            carRequests.remove(floor);
            openDoor();
            return;
        }

        if (direction == Direction.UP) {
            upRequests.add(floor);
            upHallRequests.add(floor);
        } else if (direction == Direction.DOWN) {
            downRequests.add(floor);
            downHallRequests.add(floor);
        }
        updateDirectionIfStopped(floor);
        broadcastState();
    }

    public void addCarCall(int floor) {
        if (floor < 1 || floor > properties.getTotalFloors()) return;
        
        if (floor == currentFloor && currentDirection == Direction.STOP) {
            log.info("Already at floor {}, opening doors directly.", floor);
            upRequests.remove(floor);
            downRequests.remove(floor);
            carRequests.remove(floor);
            openDoor();
            return;
        }
        
        carRequests.add(floor);

        if (floor > currentFloor) {
            upRequests.add(floor);
        } else if (floor < currentFloor) {
            downRequests.add(floor);
        }
        updateDirectionIfStopped(floor);
        broadcastState();
    }

    private void updateDirectionIfStopped(int targetFloor) {
        if (currentDirection == Direction.STOP) {
            if (targetFloor > currentFloor) {
                currentDirection = Direction.UP;
            } else if (targetFloor < currentFloor) {
                currentDirection = Direction.DOWN;
            }
        }
    }

    @Getter
    private DoorState doorState = DoorState.CLOSED;
    @Getter
    private int doorTimer = 0;

    public void openDoor() {
        if (currentDirection == Direction.STOP || doorState == DoorState.OPEN) {
            doorState = DoorState.OPEN;
            doorTimer = 3; // Keep open for 3 ticks
            log.info("Door opened at floor {}", currentFloor);
            broadcastState();
        }
    }

    public void closeDoor() {
        if (doorState == DoorState.OPEN) {
            doorTimer = 0; // Force close on next tick
            log.info("Door close button pressed at floor {}", currentFloor);
            broadcastState();
        }
    }

    // Tick every X milliseconds configured in properties
    @Scheduled(fixedRateString = "${elevator.config.move-delay-ms:1000}")
    public void tick() {
        if (doorState == DoorState.OPEN) {
            if (doorTimer > 0) {
                doorTimer--;
                if (doorTimer == 0) {
                    doorState = DoorState.CLOSED;
                    log.info("Doors closed at floor {}", currentFloor);
                    broadcastState();
                }
            }
            return; // Cannot move while doors are open
        }

        if (currentDirection == Direction.STOP) {
            return;
        }

        boolean stateChanged = false;

        if (currentDirection == Direction.UP) {
            if (upRequests.contains(currentFloor) || carRequests.contains(currentFloor)) {
                upRequests.remove(currentFloor);
                upHallRequests.remove(currentFloor);
                carRequests.remove(currentFloor);
                log.info("Stopped at floor {} to serve UP request", currentFloor);
                doorState = DoorState.OPEN;
                doorTimer = 3;
                stateChanged = true;
                broadcastState();
                return; // Stop processing movement this tick
            }
            
            boolean hasRequestsAbove = 
                (!upRequests.isEmpty() && upRequests.last() > currentFloor) ||
                (!downRequests.isEmpty() && downRequests.first() > currentFloor);

            if (hasRequestsAbove) {
                currentFloor++;
                stateChanged = true;
            } else {
                if (!downRequests.isEmpty() || !upRequests.isEmpty()) {
                    currentDirection = Direction.DOWN;
                } else {
                    currentDirection = Direction.STOP;
                }
                stateChanged = true;
            }
        } else if (currentDirection == Direction.DOWN) {
            if (downRequests.contains(currentFloor) || carRequests.contains(currentFloor)) {
                downRequests.remove(currentFloor);
                downHallRequests.remove(currentFloor);
                carRequests.remove(currentFloor);
                log.info("Stopped at floor {} to serve DOWN request", currentFloor);
                doorState = DoorState.OPEN;
                doorTimer = 3;
                stateChanged = true;
                broadcastState();
                return; // Stop processing movement this tick
            }

            boolean hasRequestsBelow = 
                (!downRequests.isEmpty() && downRequests.last() < currentFloor) ||
                (!upRequests.isEmpty() && upRequests.first() < currentFloor);

            if (hasRequestsBelow) {
                currentFloor--;
                stateChanged = true;
            } else {
                if (!upRequests.isEmpty() || !downRequests.isEmpty()) {
                    currentDirection = Direction.UP;
                } else {
                    currentDirection = Direction.STOP;
                }
                stateChanged = true;
            }
        }

        if (stateChanged) {
            log.info("Elevator State: Floor={}, Direction={}, UPQueue={}, DOWNQueue={}", 
                currentFloor, currentDirection, upRequests, downRequests);
            broadcastState();
        }
    }

    public void broadcastState() {
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("config", properties);
        data.put("state", this);
        messagingTemplate.convertAndSend("/topic/state", (Object) data);
    }
}
