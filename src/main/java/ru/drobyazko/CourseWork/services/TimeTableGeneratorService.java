package ru.drobyazko.CourseWork.services;

import org.springframework.stereotype.Component;
import ru.drobyazko.CourseWork.common.*;
import ru.drobyazko.CourseWork.util.CraneInitializer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

@Component
public class TimeTableGeneratorService implements ShipService {

    private final int shipAmount = 50;
    private final int bulkCraneAmount = 2;
    private final int liquidCraneAmount = 2;
    private final int containerCraneAmount = 2;
    private final int bulkCraneEfficiency = 1;
    private final int liquidCraneEfficiency = 1;
    private final int containerCraneEfficiency = 1;
    private final int totalCraneAmount = bulkCraneAmount +
            liquidCraneAmount + containerCraneAmount;
    private final CyclicBarrier cyclicBarrier;
    private TimeTable timeTable;

    public TimeTableGeneratorService() {
        cyclicBarrier = new CyclicBarrier(totalCraneAmount);
    }

    public TimeTable generateTimeTable() {
        timeTable = new TimeTable();
        createShips();
        List<Crane> craneList = CraneInitializer.createCraneList(bulkCraneAmount, liquidCraneAmount, containerCraneAmount
                , bulkCraneEfficiency, liquidCraneEfficiency, containerCraneEfficiency);
        List<CraneRunnable> craneRunnableList = new ArrayList<>();

        for(int i = 0; i < totalCraneAmount; ++i) {
            craneRunnableList.add( new CraneRunnable(this, craneList.get(i) ) );
        }

        for(int i = 0; i < totalCraneAmount; ++i) {
            craneRunnableList.get(i).getThread().start();
        }

        for(int i = 0; i < totalCraneAmount; ++i) {
            try {
                craneRunnableList.get(i).getThread().join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (ShipSlot shipSlot : timeTable.getShipSlotList()) {
            System.out.println(shipSlot.getShip() + ", arrivalTime=" + shipSlot.getArrivalTime() +
                    ", dispatchTime=" + shipSlot.getDispatchTime() + ".");
        }

        return timeTable;
    }

    private void createShips() {
        for (int i = 0; i < shipAmount; ++i) {
            Ship newShip = new Ship();
            ShipSlot newShipSlot = new ShipSlot(newShip);
            timeTable.addShipSlot(newShipSlot);
        }
    }

    @Override
    public ShipSlot requestShip(CraneRunnable craneRunnable) {
        for (ShipSlot shipSlot : timeTable.getShipSlotList()) {

            if (craneRunnable.getCrane().getCargoType() == shipSlot.getShip().getCargoType()
                    && shipSlot.getShip().getWorkingWeight() > 0
                    && shipSlot.getCranesWorkingOn() != 2) {

                shipSlot.setCranesWorkingOn(shipSlot.getCranesWorkingOn() + 1);
                return shipSlot;
            }

        }
        return null;
    }

    @Override
    public void barrierAwait() {
        try {
            cyclicBarrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
    
}
