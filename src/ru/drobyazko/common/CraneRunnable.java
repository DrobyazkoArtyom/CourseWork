package ru.drobyazko.common;

import ru.drobyazko.services.TimeTableGeneratorService;

public class CraneRunnable implements Runnable {

    private Thread thread;
    private Crane crane;
    private ShipSlot currentShipSlot;
    private ShipService ownerService;
    private int currentTick;

    public CraneRunnable(ShipService ownerService, Crane crane) {
        this.ownerService = ownerService;
        thread = new Thread(this);
        this.crane = crane;
    }

    private void proceedOneTickGenerate() {
        synchronized (currentShipSlot) {
            if(currentShipSlot.getShip().getWorkingWeight() <= 0) {
                currentShipSlot = ownerService.requestShip(this);

                if (currentShipSlot == null) {
                    return;
                }

                if(currentShipSlot.getArrivalTime() == -1) {
                    currentShipSlot.setArrivalTime(currentTick);
                }

            }
            int newWeight = currentShipSlot.getShip().getWorkingWeight() - crane.getCraneEfficiency();
            currentShipSlot.getShip().setWorkingWeight(newWeight);
        }
    }

    @Override
    public void run() {
        if(ownerService instanceof TimeTableGeneratorService) {
            generate();
        }
        else {
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

                if(currentShipSlot.getArrivalTime() == -1) {
                    currentShipSlot.setArrivalTime(currentTick);
                }

            }

            proceedOneTickGenerate();

            if (currentShipSlot == null) {
                ownerService.barrierAwait();
                continue;
            }

            synchronized (currentShipSlot) {
                if (currentShipSlot.getShip().getWorkingWeight() <= 0) {
                    currentShipSlot.setDispatchTime(currentTick + 1);
                    currentShipSlot = null;
                }
            }

            ownerService.barrierAwait();

        }
    }

    private void proceedOneTickSolve() {
        synchronized (currentShipSlot) {
            if(currentShipSlot.getShip().getWorkingWeight() <= 0) {
                currentShipSlot = ownerService.requestShip(this);

                if (currentShipSlot == null) {
                    return;
                }

                if(currentShipSlot.getCranesWorkingOn() == 1) {
                    currentShipSlot.setStartTime(currentTick);
                    currentShipSlot.setPenalty( ( ( currentTick - currentShipSlot.getArrivalTime() ) / 60) * 100);
                }
            }
            int newWeight = currentShipSlot.getShip().getWorkingWeight() - crane.getCraneEfficiency();
            currentShipSlot.getShip().setWorkingWeight(newWeight);
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
                if(currentShipSlot.getCranesWorkingOn() == 1) {
                    currentShipSlot.setStartTime(currentTick);
                    currentShipSlot.setPenalty( ( ( currentTick - currentShipSlot.getArrivalTime() ) / 60) * 100);
                }
            }

            if(currentShipSlot.getDispatchTimeOffset() > 0) {
                currentShipSlot.setDispatchTimeOffset(currentShipSlot.getDispatchTimeOffset() - 1);
                ownerService.barrierAwait();
                continue;
            }

            proceedOneTickSolve();

            if (currentShipSlot == null) {
                ownerService.barrierAwait();
                continue;
            }

            synchronized (currentShipSlot) {
                if (currentShipSlot.getShip().getWorkingWeight() <= 0) {
                    currentShipSlot.setDispatchTime(currentTick + 1);
                    currentShipSlot = null;
                }
            }

            ownerService.barrierAwait();
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
