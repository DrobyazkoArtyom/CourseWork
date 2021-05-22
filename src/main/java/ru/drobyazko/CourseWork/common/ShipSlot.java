package ru.drobyazko.CourseWork.common;

import static ru.drobyazko.CourseWork.util.TimeFormatter.formatTime;

public class ShipSlot {

    private Ship ship;
    private int dispatchTimeOffsetNominal;
    private int arrivalTimeOffset;
    private int arrivalTime = -1;
    private int startTime = -1;
    private int dispatchTimeOffset;
    private int dispatchTime = -1;
    private int cranesWorkingOn = 0;
    private int penalty = 0;

    private ShipSlot() {
    }

    public ShipSlot(Ship ship) {
        this.ship = ship;
    }

    public Ship getShip() {
        return ship;
    }

    public synchronized int getCranesWorkingOn() {
        return cranesWorkingOn;
    }

    public synchronized void setCranesWorkingOn(int cranesWorkingOn) {
        this.cranesWorkingOn = cranesWorkingOn;
    }

    public synchronized int getPenalty() {
        return penalty;
    }

    public synchronized void setPenalty(int penalty) {
        this.penalty = penalty;
    }

    public synchronized int getStartTime() {
        return startTime;
    }

    public synchronized void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public synchronized int getArrivalTimeOffset() {
        return arrivalTimeOffset;
    }

    public synchronized void setArrivalTimeOffset(int arrivalTimeOffset) {
        this.arrivalTimeOffset = arrivalTimeOffset;
    }

    public synchronized int getDispatchTimeOffset() {
        return dispatchTimeOffset;
    }

    public synchronized void setDispatchTimeOffset(int dispatchTimeOffset) {
        this.dispatchTimeOffset = dispatchTimeOffset;
    }

    public int getDispatchTimeOffsetNominal() {
        return dispatchTimeOffsetNominal;
    }

    public void setDispatchTimeOffsetNominal(int dispatchTimeOffsetNominal) {
        this.dispatchTimeOffsetNominal = dispatchTimeOffsetNominal;
        this.dispatchTimeOffset = dispatchTimeOffsetNominal;
    }

    public synchronized int getArrivalTime() {
        return arrivalTime;
    }

    public synchronized void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public synchronized int getDispatchTime() {
        return dispatchTime;
    }

    public synchronized void setDispatchTime(int dispatchTime) {
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
