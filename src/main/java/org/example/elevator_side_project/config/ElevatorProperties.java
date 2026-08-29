package org.example.elevator_side_project.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "elevator.config")
public class ElevatorProperties {
    private int totalFloors = 10;
    private int elevatorCount = 1;
    private int moveDelayMs = 1000;
}
