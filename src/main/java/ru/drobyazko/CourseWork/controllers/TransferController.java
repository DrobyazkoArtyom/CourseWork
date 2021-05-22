package ru.drobyazko.CourseWork.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.drobyazko.CourseWork.common.Ship;
import ru.drobyazko.CourseWork.common.ShipSlot;
import ru.drobyazko.CourseWork.common.TimeTable;
import ru.drobyazko.CourseWork.services.TimeTableTransferService;

import java.util.Scanner;

@RestController
public class TransferController {

    private final TimeTableTransferService timeTableTransferService;

    @Autowired
    public TransferController(TimeTableTransferService timeTableTransferService) {
        this.timeTableTransferService = timeTableTransferService;
    }

    @GetMapping("/newTimeTable")
    public ResponseEntity<TimeTable> getNewTimeTable() {
        RestTemplate restTemplate = new RestTemplate();
        String resourceUrl = "http://localhost:8080/generate";
        ResponseEntity<TimeTable> response = restTemplate.getForEntity(resourceUrl, TimeTable.class);

        Scanner scanner = new Scanner(System.in);
        System.out.println("Write \"y\" if you want to add ships manually or anything else if you don't want to.");
        String temp = scanner.next();
        if(temp.equals("y")) {
            System.out.println("How many ships do you want to add?");
            int shipAmount = scanner.nextInt();
            for(int i = 0; i < shipAmount; ++i) {
                Ship newShip = new Ship();
                ShipSlot newShipSlot = new ShipSlot(newShip);
                System.out.println("Arrival time and dispatch time?");
                int arrivalTime = scanner.nextInt();
                int dispatchTime = scanner.nextInt();
                newShipSlot.setArrivalTime(arrivalTime);
                newShipSlot.setDispatchTime(dispatchTime);
                response.getBody().addShipSlot(newShipSlot);
            }
        }

        return response;
    }

    @GetMapping("/timeTable/{fileName}")
    public ResponseEntity<TimeTable> getTimeTableByFileName(@PathVariable String fileName) {
        TimeTable timeTable = timeTableTransferService.getJsonFile(fileName);
        timeTableTransferService.printTimeTable(timeTable);
        return ResponseEntity.ok(timeTable);
    }

    @PostMapping("/timeTable/{fileName}")
    public void saveTimeTable(@PathVariable String fileName, @RequestBody TimeTable timeTable) {
        timeTableTransferService.saveJsonFile(fileName, timeTable);
        timeTableTransferService.printTimeTable(timeTable);
    }

}
