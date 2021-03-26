package ru.drobyazko.services;

import ru.drobyazko.common.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class TimeTableGeneratorService implements ShipService {

    private final int shipAmount = 50;
    private final int bulkCraneAmount = 2;
    private final int liquidCraneAmount = 2;
    private final int containerCraneAmount = 2;
    private final int totalCraneAmount = bulkCraneAmount +
            liquidCraneAmount + containerCraneAmount;
    private CyclicBarrier cyclicBarrier;
    private TimeTable timeTable;

    public TimeTableGeneratorService() {
    }

    public TimeTable generateTimeTable() {
        List<CraneRunnable> craneRunnableList = new ArrayList<>();
        initEntities();

        for(int i = 0; i < totalCraneAmount; ++i) {
            craneRunnableList.add( new CraneRunnable(this, timeTable.getCraneList().get(i) ) );
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

    @Override
    public ShipSlot requestShip(CraneRunnable craneRunnable) {
        for (ShipSlot shipSlot : timeTable.getShipSlotList()) {

            if(craneRunnable.getCrane().getCargoType() == shipSlot.getShip().getCargoType()
                    && shipSlot.getShip().getWorkingWeight() > 0) {

                if(shipSlot.getCranesWorkingOn() != 2) {
                    shipSlot.setCranesWorkingOn(shipSlot.getCranesWorkingOn() + 1);
                    return shipSlot;
                }

            }

        }

        return null;
    }

    private void initEntities() {
        timeTable = new TimeTable();
        cyclicBarrier = new CyclicBarrier(totalCraneAmount);

        for(int i = 0; i < bulkCraneAmount; ++i) {
            Crane newCraneBulk = new Crane(CargoType.BULK, 100);
            timeTable.addCrane(newCraneBulk);
        }

        for(int i = 0; i < liquidCraneAmount; ++i) {
            Crane newCraneLiquid = new Crane(CargoType.LIQUID, 100);
            timeTable.addCrane(newCraneLiquid);
        }

        for(int i = 0; i < containerCraneAmount; ++i) {
            Crane newCraneContainer = new Crane(CargoType.CONTAINER, 100);
            timeTable.addCrane(newCraneContainer);
        }

        for(int i = 0; i < shipAmount; ++i) {
            Ship newShip = new Ship();
            ShipSlot newShipSlot = new ShipSlot(newShip);
            timeTable.addShipSlot(newShipSlot);
        }
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
