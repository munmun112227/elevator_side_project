package org.example.elevator_side_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ElevatorSideProjectApplication {

  public static void main(String[] args) {
    SpringApplication.run(ElevatorSideProjectApplication.class, args);
  }

}
