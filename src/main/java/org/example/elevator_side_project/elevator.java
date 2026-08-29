package org.example.elevator_side_project;

import java.util.Comparator;
import java.util.TreeSet;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class elevator {
  private int currentFloor;
  private Direction currentDirection;
  private TreeSet<Integer> upRequests = new TreeSet<>() ;
  private TreeSet<Integer> downRequests = new TreeSet<>() ;

  public elevator() {
    this.currentFloor = 1;
    this.currentDirection = Direction.STOP;
    this.upRequests = new TreeSet<>() ;
    this.downRequests = new TreeSet<>(Comparator.reverseOrder()) ;
  }

  public Direction addRequest(int floor, Direction direction) {
      if (direction == Direction.UP) {
        upRequests.add(floor);
        return direction;
      } else {
        downRequests.add(floor);
        return direction;
      }
  }

  public void addRequest(int floor) {
    if (floor > currentFloor) {
      upRequests.add(floor);
    } else {
      downRequests.add(floor);
    }

    if (currentDirection == Direction.STOP) {
      currentDirection = (floor > currentFloor) ? Direction.UP : Direction.DOWN;
    }
  }

  private void processRequests(TreeSet<Integer> requests, Direction nextDirection) {
    while (!requests.isEmpty()) {
      int nextFloor = requests.pollFirst();
      log.info("CurrentFloor = {} to NextFloor = {}", currentFloor, nextFloor);
      currentFloor = nextFloor;
    }
    if (!upRequests.isEmpty() || !downRequests.isEmpty()) {
      currentDirection = nextDirection;
    }
  }

  public void move() {
    while (!upRequests.isEmpty() || !downRequests.isEmpty()) {
      if (currentDirection == Direction.UP) {
        processRequests(upRequests, Direction.UP);
      } else if (currentDirection == Direction.DOWN) {
        processRequests(downRequests, Direction.DOWN);
      }
    }
    currentDirection = Direction.STOP;
    log.info("Mission finished, status of elevator is stop, CurrentFloor = {}", currentFloor);
  }
}
