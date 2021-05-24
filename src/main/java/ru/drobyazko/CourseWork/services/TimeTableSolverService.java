package ru.drobyazko.CourseWork.services;

import org.springframework.stereotype.Component;
import ru.drobyazko.CourseWork.common.*;
import ru.drobyazko.CourseWork.util.CraneInitializer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

@Component
public class TimeTableSolverService implements ShipService {

    private final int bulkCraneEfficiency = 2;
    private final int liquidCraneEfficiency = 2;
    private final int containerCraneEfficiency = 2;
    private final int bulkCraneAmount = 4;
    private final int liquidCraneAmount = 4;
    private final int containerCraneAmount = 4;
    private TimeTable timeTable;
    private CyclicBarrier cyclicBarrier;

    public TimeTableSolverService() {
    }

    public void solveTimeTable (TimeTable timeTable) {
        this.timeTable = timeTable;
        generateTimeOffsets();
        sortShipSlotsByArrivalTime();

        int optimalBulkCraneAmount;
        int optimalLiquidCraneAmount;
        int optimalContainerCraneAmount;

        Object[] bulkResults = solveCranes(CargoType.BULK, bulkCraneAmount);
        Object[] liquidResults = solveCranes(CargoType.LIQUID, liquidCraneAmount);
        Object[] containerResults = solveCranes(CargoType.CONTAINER, containerCraneAmount);

        optimalBulkCraneAmount = (int)bulkResults[1];
        optimalLiquidCraneAmount = (int)liquidResults[1];
        optimalContainerCraneAmount = (int)containerResults[1];

        timeTable.setOptimalBulkCraneAmount(optimalBulkCraneAmount);
        timeTable.setOptimalLiquidCraneAmount(optimalLiquidCraneAmount);
        timeTable.setOptimalContainerCraneAmount(optimalContainerCraneAmount);

        nullifyShipSlots();
        restoreByOptimal(optimalBulkCraneAmount, optimalLiquidCraneAmount, optimalContainerCraneAmount);
    }

    private void restoreByOptimal(int optimalBulkCraneAmount, int optimalLiquidCraneAmount
            , int optimalContainerCraneAmount) {
        List<Crane> craneList = CraneInitializer.createCraneList(optimalBulkCraneAmount, optimalLiquidCraneAmount
                , optimalContainerCraneAmount, bulkCraneEfficiency
                , liquidCraneEfficiency, containerCraneEfficiency);

        List<CraneRunnable> craneRunnableList = new ArrayList<>();

        int totalCraneAmount = optimalBulkCraneAmount + optimalLiquidCraneAmount + optimalContainerCraneAmount;

        cyclicBarrier = new CyclicBarrier(totalCraneAmount);

        for (int i = 0; i < totalCraneAmount; ++i) {
            craneRunnableList.add(new CraneRunnable(this, craneList.get(i)));
        }

        for (int i = 0; i < totalCraneAmount; ++i) {
            craneRunnableList.get(i).getThread().start();
        }

        for (int i = 0; i < totalCraneAmount; ++i) {
            try {
                craneRunnableList.get(i).getThread().join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private Object[] solveCranes(CargoType cargoType, int craneAmount) {

        int optimalCost = 0;
        int optimalCraneAmount = 0;

        for(int i = 1; i <= craneAmount; ++i) {

            nullifyShipSlots();
            List<Crane> craneList;

            switch (cargoType) {
                case BULK:
                    craneList = CraneInitializer.createCraneList(i, 0
                        , 0, bulkCraneEfficiency
                        , liquidCraneEfficiency, containerCraneEfficiency);
                    break;
                case LIQUID:
                    craneList = CraneInitializer.createCraneList(0, i
                            , 0, bulkCraneEfficiency
                            , liquidCraneEfficiency, containerCraneEfficiency);
                    break;
                case CONTAINER:
                    craneList = CraneInitializer.createCraneList(0, 0
                            , i, bulkCraneEfficiency
                            , liquidCraneEfficiency, containerCraneEfficiency);
                    break;
                default:
                    craneList = null;
            }
            List<CraneRunnable> craneRunnableList = new ArrayList<>();

            cyclicBarrier = new CyclicBarrier(i);

            for (int j = 0; j < i; ++j) {
                craneRunnableList.add(new CraneRunnable(this, craneList.get(j)));
            }

            for (int j = 0; j < i; ++j) {
                craneRunnableList.get(j).getThread().start();
            }

            for (int j = 0; j < i; ++j) {
                try {
                    craneRunnableList.get(j).getThread().join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            int tempCost = 30000 * i;;

            for (ShipSlot shipSlot : timeTable.getShipSlotList()) {
                tempCost += shipSlot.getPenalty();
            }

            if(tempCost < optimalCost || optimalCost == 0) {
                optimalCost = tempCost;
                optimalCraneAmount = i;
            }

        }

        return new Object[] {optimalCost, optimalCraneAmount};
    }

    private void generateTimeOffsets() {
        Random random = new Random();
        for (ShipSlot shipSlot : timeTable.getShipSlotList()) {
            shipSlot.setDispatchTimeOffsetNominal(random.nextInt(1));
            shipSlot.setArrivalTimeOffset(random.nextInt(21600)-10800);
            if(shipSlot.getArrivalTimeOffset() < 0) {
                shipSlot.setArrivalTimeOffset(0);
            }
            shipSlot.setArrivalTime(shipSlot.getArrivalTime() + shipSlot.getArrivalTimeOffset());
        }
    }

    private void sortShipSlotsByArrivalTime() {
        timeTable.getShipSlotList().sort(Comparator.comparing(ShipSlot::getArrivalTime));
    }

    private void nullifyShipSlots() {
        for (ShipSlot shipSlot : timeTable.getShipSlotList()) {
            shipSlot.setCranesWorkingOn(0);
            Ship ship = shipSlot.getShip();
            ship.setWorkingWeight(ship.getNominalWeight());
            shipSlot.setDispatchTimeOffset(shipSlot.getDispatchTimeOffsetNominal());
            shipSlot.setStartTime(-1);
            shipSlot.setDispatchTime(-1);
            shipSlot.setPenalty(0);
        }
    }

    @Override
    public ShipSlot requestShip(CraneRunnable craneRunnable) {
        for (ShipSlot shipSlot : timeTable.getShipSlotList()) {

            if(craneRunnable.getCrane().getCargoType() == shipSlot.getShip().getCargoType()
                    && shipSlot.getShip().getWorkingWeight() > 0
                    && shipSlot.getCranesWorkingOn() != 2
                    && shipSlot.getArrivalTime() <= craneRunnable.getCurrentTick()) {

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
