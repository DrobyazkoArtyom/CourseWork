package ru.drobyazko.CourseWork.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
        timeTableTransferService.cleanDirectory();
    }

    @GetMapping("/transferTimeTable")
    public ResponseEntity<TimeTable> transferTimeTable() {
        RestTemplate restTemplate = new RestTemplate();
        String resourceUrl = "http://localhost:8080/generateTimeTable";
        ResponseEntity<TimeTable> response = restTemplate.getForEntity(resourceUrl, TimeTable.class);
        TimeTable timeTable = response.getBody();

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
                timeTable.addShipSlot(newShipSlot);
            }
        }

        return ResponseEntity.ok(timeTable);
    }

    @GetMapping("/timeTable/{fileName}")
    public ResponseEntity<TimeTable> getTimeTableByFileName(@PathVariable String fileName) {
        TimeTable timeTable = timeTableTransferService.getJsonFile(fileName);
        timeTableTransferService.printTimeTable(timeTable);
        return ResponseEntity.ok(timeTable);
    }

    @PostMapping("/timeTable/{fileName}")
    public ResponseEntity saveTimeTable(@PathVariable String fileName, @RequestBody TimeTable timeTable) {
        timeTableTransferService.saveJsonFile(fileName, timeTable);
        timeTableTransferService.printTimeTable(timeTable);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

}
