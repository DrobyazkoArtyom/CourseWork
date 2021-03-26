package ru.drobyazko;

import ru.drobyazko.common.Ship;
import ru.drobyazko.common.ShipSlot;
import ru.drobyazko.common.TimeTable;
import ru.drobyazko.services.TimeTableGeneratorService;
import ru.drobyazko.services.TimeTableSolverService;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        TimeTableGeneratorService timeTableGeneratorService = new TimeTableGeneratorService();
        TimeTable timeTable = timeTableGeneratorService.generateTimeTable();
        System.out.println("Do you want to add ships manually? : y/n");
        Scanner scanner = new Scanner(System.in);
        if(scanner.next().equals("y")) {

            System.out.println("How many ships do you want to add? : ");
            int num = 0;
            num = scanner.nextInt();

            for(int i = 0; i < num; ++i) {
                System.out.println("ArrivalTime and DispatchTime? : ");
                ShipSlot newShipSlot = buildShipSlot(scanner.nextInt(), scanner.nextInt());
                timeTable.addShipSlot(newShipSlot);
            }

            System.out.println("Adding ships is done.");

        }
        scanner.close();
        TimeTableSolverService timeTableSolverService = new TimeTableSolverService(timeTable);
        timeTableSolverService.solveTimeTable();
    }

    private static ShipSlot buildShipSlot(int arrivalTime, int dispatchTime) {
        Ship newShip = new Ship();
        ShipSlot newShipSlot = new ShipSlot(newShip);
        newShipSlot.setArrivalTime(arrivalTime);
        newShipSlot.setDispatchTime(dispatchTime);
        return newShipSlot;
    }
}
