package ru.drobyazko.services;

import ru.drobyazko.common.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.stream.Collectors;
import static ru.drobyazko.util.TimeFormatter.formatTime;

public class TimeTableSolverService implements ShipService {

    private CyclicBarrier cyclicBarrier;
    private TimeTable timeTable;

    public TimeTableSolverService(TimeTable timeTable) {
        this.timeTable = timeTable;
    }

    public void solveTimeTable () {
        generateTimeOffsets();
        sortShipSlotsByArrivalTime();

        List<ShipSlot> optimalShipSlotList = new ArrayList<>();
        int optimalPenalty = -1;
        int optimalBulkCraneAmount = -1;
        int optimalLiquidCraneAmount = -1;
        int optimalContainerCraneAmount = -1;

        for(int bulkCraneAmount = 1; bulkCraneAmount < 3; ++bulkCraneAmount) {

            for(int liquidCraneAmount = 1; liquidCraneAmount < 3; ++liquidCraneAmount) {

                for(int containerCraneAmount = 1; containerCraneAmount < 3; ++containerCraneAmount) {

                    nullifyShipSlots();

                    List<Crane> craneList = initCraneList(bulkCraneAmount, liquidCraneAmount, containerCraneAmount);
                    List<CraneRunnable> craneRunnableList = new ArrayList<>();
                    int totalCraneAmount = bulkCraneAmount + liquidCraneAmount + containerCraneAmount;

                    cyclicBarrier = new CyclicBarrier(totalCraneAmount);

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

                    int totalPenalty = 0;
                    for (ShipSlot shipSlot : timeTable.getShipSlotList()) {
                        totalPenalty += shipSlot.getPenalty();
                    }

                    totalPenalty += 30000 * totalCraneAmount;

                    if(totalPenalty < optimalPenalty || optimalPenalty == -1) {
                        optimalPenalty = totalPenalty;
                        optimalBulkCraneAmount = bulkCraneAmount;
                        optimalLiquidCraneAmount = liquidCraneAmount;
                        optimalContainerCraneAmount = containerCraneAmount;
                        optimalShipSlotList.clear();
                        optimalShipSlotList = timeTable.getShipSlotList().stream()
                                .collect(Collectors.toList());
                    }

                }

            }

        }

        System.out.println("- - - - - - - - - - -");

        int doneShipsNumber = 0;
        int queueTime = 0;
        int dispatchTimeOffset = 0;
        int maxDispatchTimeOffset = 0;
        for (ShipSlot shipSlot : optimalShipSlotList) {
            if(shipSlot.getStartTime() == -1) {
                break;
            }
            System.out.println(shipSlot);
            ++doneShipsNumber;
            queueTime += shipSlot.getStartTime() - shipSlot.getArrivalTime();
            int dispatchTimeOffsetTemp = shipSlot.getDispatchTimeOffsetNominal();
            if(dispatchTimeOffsetTemp > maxDispatchTimeOffset) {
                maxDispatchTimeOffset = dispatchTimeOffsetTemp;
            }
            dispatchTimeOffset += dispatchTimeOffsetTemp;
        }

        System.out.println("Penalty: " + optimalPenalty);
        System.out.println("Combination of cranes: " + optimalBulkCraneAmount + " bulk cranes, " +
                optimalLiquidCraneAmount + " liquid cranes, " + optimalContainerCraneAmount +
                " container cranes.");
        System.out.println("Ships done: " + doneShipsNumber);
        System.out.println("Average queue time: " + formatTime(queueTime / doneShipsNumber));
        System.out.println("Maximum dispatch time offset: " + formatTime(maxDispatchTimeOffset));
        System.out.println("Average dispatch time offset: " + formatTime(dispatchTimeOffset / doneShipsNumber));

    }

    private void nullifyShipSlots() {
        for (ShipSlot shipSlot : timeTable.getShipSlotList()) {
            shipSlot.setCranesWorkingOn(0);
            Ship ship = shipSlot.getShip();
            ship.setWorkingWeight(ship.getNominalWeight());
            shipSlot.setDispatchTimeOffset(shipSlot.getDispatchTimeOffsetNominal());
        }
    }

    private void generateTimeOffsets() {
        Random random = new Random();
        for (ShipSlot shipSlot : timeTable.getShipSlotList()) {
            shipSlot.setDispatchTimeOffsetNominal(random.nextInt(1441));
            shipSlot.setArrivalTimeOffset(random.nextInt(21600)-10080);
            if(shipSlot.getArrivalTimeOffset() < 0) {
                shipSlot.setArrivalTimeOffset(0);
            }
            shipSlot.setArrivalTime(shipSlot.getArrivalTime() + shipSlot.getArrivalTimeOffset());
        }
    }

    private void sortShipSlotsByArrivalTime() {
        timeTable.getShipSlotList().sort(Comparator.comparing(ShipSlot::getArrivalTime));
    }

    private List<Crane> initCraneList(int bulkCraneAmount, int liquidCraneAmount, int containerCraneAmount) {
        List<Crane> craneList = new ArrayList<>();

        for(int i = 0; i < bulkCraneAmount; ++i) {
            Crane newCraneBulk = new Crane(CargoType.BULK, 1);
            craneList.add(newCraneBulk);
        }

        for(int i = 0; i < liquidCraneAmount; ++i) {
            Crane newCraneLiquid = new Crane(CargoType.LIQUID, 1);
            craneList.add(newCraneLiquid);
        }

        for(int i = 0; i < containerCraneAmount; ++i) {
            Crane newCraneContainer = new Crane(CargoType.CONTAINER, 1);
            craneList.add(newCraneContainer);
        }

        return craneList;
    }

    @Override
    public ShipSlot requestShip(CraneRunnable craneRunnable) {
        for (ShipSlot shipSlot : timeTable.getShipSlotList()) {

            if(craneRunnable.getCrane().getCargoType() == shipSlot.getShip().getCargoType()
                    && shipSlot.getShip().getWorkingWeight() > 0) {

                if(shipSlot.getArrivalTime() <= craneRunnable.getCurrentTick()) {
                    if (shipSlot.getCranesWorkingOn() != 2) {
                        shipSlot.setCranesWorkingOn(shipSlot.getCranesWorkingOn() + 1);
                        return shipSlot;
                    }

                }

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