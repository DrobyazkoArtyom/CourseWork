package ru.drobyazko.common;

import static ru.drobyazko.util.TimeFormatter.formatTime;

public class ShipSlot {

    private Ship ship;
    private int dispatchTimeOffsetNominal;
    private int arrivalTimeOffset;
    private int arrivalTime = -1;
    private int startTime;
    private int dispatchTimeOffset;
    private int dispatchTime;
    volatile private int cranesWorkingOn = 0;
    volatile private int penalty = 0;

    public ShipSlot(Ship ship) {
        this.ship = ship;
    }

    synchronized public int getCranesWorkingOn() {
        return cranesWorkingOn;
    }

    synchronized public void setCranesWorkingOn(int cranesWorkingOn) {
        this.cranesWorkingOn = cranesWorkingOn;
    }

    public int getPenalty() {
        return penalty;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public void setPenalty(int penalty) {
        this.penalty = penalty;
    }

    public int getArrivalTimeOffset() {
        return arrivalTimeOffset;
    }

    public void setArrivalTimeOffset(int arrivalTimeOffset) {
        this.arrivalTimeOffset = arrivalTimeOffset;
    }

    synchronized public int getDispatchTimeOffset() {
        return dispatchTimeOffset;
    }

    public int getDispatchTimeOffsetNominal() {
        return dispatchTimeOffsetNominal;
    }

    public void setDispatchTimeOffsetNominal(int dispatchTimeOffsetNominal) {
        this.dispatchTimeOffsetNominal = dispatchTimeOffsetNominal;
        this.dispatchTimeOffset = dispatchTimeOffsetNominal;
    }

    synchronized public void setDispatchTimeOffset(int dispatchTimeOffset) {
        this.dispatchTimeOffset = dispatchTimeOffset;
    }

    synchronized public Ship getShip() {
        return ship;
    }

    synchronized public int getArrivalTime() {
        return arrivalTime;
    }

    synchronized public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    synchronized public int getDispatchTime() {
        return dispatchTime;
    }

    synchronized public void setDispatchTime(int dispatchTime) {
        this.dispatchTime = dispatchTime;
    }

    @Override
    public String toString() {
        return "ShipSlot{" +
                "ship=" + ship.getName() +
                ", arrivalTime=" + formatTime(arrivalTime) +
                ", queueTime=" + formatTime(startTime - arrivalTime) +
                ", startTime=" + formatTime(startTime) +
                ", workTime=" + formatTime(dispatchTime - startTime) +
                '}';
    }
}
