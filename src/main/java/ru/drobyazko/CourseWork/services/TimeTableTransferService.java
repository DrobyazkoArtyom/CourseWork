package ru.drobyazko.CourseWork.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import ru.drobyazko.CourseWork.common.ShipSlot;
import ru.drobyazko.CourseWork.common.TimeTable;

import java.io.File;
import java.io.IOException;

import static ru.drobyazko.CourseWork.util.TimeFormatter.formatTime;

@Component
public class TimeTableTransferService {

    public void saveJsonFile(String fileName, TimeTable timeTable) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File("./timetables/"+ fileName), timeTable);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TimeTable getJsonFile(String fileName) {
        ObjectMapper objectMapper = new ObjectMapper();
        TimeTable timeTable = null;
        File file = new File("./timetables/"+ fileName);
        if(!file.exists()) {
            return null;
        }
        try {
            timeTable = objectMapper.readValue(file, TimeTable.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return timeTable;
    }

    public void printTimeTable(TimeTable timeTable) {
        System.out.println("Results:");
        int totalCraneAmount = timeTable.getOptimalBulkCraneAmount()
                + timeTable.getOptimalLiquidCraneAmount()
                + timeTable.getOptimalContainerCraneAmount();
        int optimalCost = 30000 * totalCraneAmount;
        int doneShipsNumber = 0;
        int queueTime = 0;
        int dispatchTimeOffset = 0;
        int maxDispatchTimeOffset = 0;
        for (ShipSlot shipSlot : timeTable.getShipSlotList()) {
            if(shipSlot.getStartTime() == -1) {
                continue;
            }

            ++doneShipsNumber;
            queueTime += shipSlot.getStartTime() - shipSlot.getArrivalTime();
            optimalCost += shipSlot.getPenalty();

            int dispatchTimeOffsetTemp = shipSlot.getDispatchTimeOffsetNominal();
            if(dispatchTimeOffsetTemp > maxDispatchTimeOffset) {
                maxDispatchTimeOffset = dispatchTimeOffsetTemp;
            }
            dispatchTimeOffset += dispatchTimeOffsetTemp;
        }

        System.out.println("Total Cost: " + optimalCost);
        System.out.println("Combination of cranes: " + timeTable.getOptimalBulkCraneAmount() + " bulk cranes, " +
                timeTable.getOptimalLiquidCraneAmount() + " liquid cranes, " + timeTable.getOptimalContainerCraneAmount() +
                " container cranes.");
        System.out.println("Ships done: " + doneShipsNumber);
        System.out.println("Average queue time: " + formatTime(queueTime / doneShipsNumber));
        System.out.println("Maximum dispatch time offset: " + formatTime(maxDispatchTimeOffset));
        System.out.println("Average dispatch time offset: " + formatTime(dispatchTimeOffset / doneShipsNumber));
    }

}
