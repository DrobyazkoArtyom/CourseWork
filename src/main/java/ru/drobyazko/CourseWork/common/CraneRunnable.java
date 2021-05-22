package ru.drobyazko.CourseWork.common;

import ru.drobyazko.CourseWork.services.TimeTableGeneratorService;
import ru.drobyazko.CourseWork.services.TimeTableSolverService;

public class CraneRunnable implements Runnable {

    private final Thread thread;
    private final Crane crane;
    private final ShipService ownerService;
    private ShipSlot currentShipSlot;
    private int currentTick;

    public CraneRunnable(ShipService ownerService, Crane crane) {
        this.ownerService = ownerService;
        this.crane = crane;
        thread = new Thread(this);
    }

    @Override
    public void run() {
        if (ownerService instanceof TimeTableGeneratorService) {
            generate();
        } else if (ownerService instanceof TimeTableSolverService){
            solve();
        }
    }

    private void generate() {
        for (currentTick = 0; currentTick < ownerService.tickAmount; ++currentTick) {

            if (currentShipSlot == null) {
                currentShipSlot = ownerService.requestShip(this);

                if (currentShipSlot == null) {
                    ownerService.barrierAwait();
                    continue;
                }

                if (currentShipSlot.getArrivalTime() == -1) {
                    currentShipSlot.setArrivalTime(currentTick);
                }

            }

            proceedOneTickGenerate();

            if (currentShipSlot == null) {
                ownerService.barrierAwait();
                continue;
            }

            if (currentShipSlot.getShip().getWorkingWeight() <= 0) {
                currentShipSlot.setDispatchTime(currentTick + 1);
                currentShipSlot = null;
            }

            ownerService.barrierAwait();

        }
    }

    private void solve() {
        for (currentTick = 0; currentTick < ownerService.tickAmount; ++currentTick) {

            if (currentShipSlot == null) {
                currentShipSlot = ownerService.requestShip(this);

                if (currentShipSlot == null) {
                    ownerService.barrierAwait();
                    continue;
                }

                if (currentShipSlot.getStartTime() == -1) {
                    currentShipSlot.setStartTime(currentTick);
                    currentShipSlot.setPenalty( ( currentTick - currentShipSlot.getArrivalTime() ) / 60 * 100);
                }
            }

            if (currentShipSlot.getDispatchTimeOffset() > 0) {
                currentShipSlot.setDispatchTimeOffset(currentShipSlot.getDispatchTimeOffset() - 1);
                ownerService.barrierAwait();
                continue;
            }

            proceedOneTickSolve();

            if (currentShipSlot == null) {
                ownerService.barrierAwait();
                continue;
            }



            if (currentShipSlot.getShip().getWorkingWeight() <= 0) {
                currentShipSlot.setDispatchTime(currentTick + 1);
                currentShipSlot = null;
            }

            ownerService.barrierAwait();
        }
    }

    private void proceedOneTickGenerate() {
        synchronized (currentShipSlot.getShip()) {
            if (currentShipSlot.getShip().getWorkingWeight() <= 0) {
                currentShipSlot = ownerService.requestShip(this);

                if (currentShipSlot == null) {
                    return;
                }

                if (currentShipSlot.getArrivalTime() == -1) {
                    currentShipSlot.setArrivalTime(currentTick);
                }

            }
            int newWeight = currentShipSlot.getShip().getWorkingWeight() - crane.getCraneEfficiency();
            currentShipSlot.getShip().setWorkingWeight(newWeight);
        }
    }

    private void proceedOneTickSolve() {
        synchronized (currentShipSlot.getShip()) {
            if (currentShipSlot.getShip().getWorkingWeight() <= 0) {
                currentShipSlot = ownerService.requestShip(this);

                if (currentShipSlot == null) {
                    return;
                }

                if (currentShipSlot.getStartTime() == -1) {
                    currentShipSlot.setStartTime(currentTick);
                    currentShipSlot.setPenalty( ( ( currentTick - currentShipSlot.getArrivalTime() ) / 60) * 100);
                }
            }
            int newWeight = currentShipSlot.getShip().getWorkingWeight() - crane.getCraneEfficiency();
            currentShipSlot.getShip().setWorkingWeight(newWeight);
        }
    }

    public Crane getCrane() {
        return crane;
    }

    public Thread getThread() {
        return thread;
    }

    public int getCurrentTick() {
        return currentTick;
    }

}
