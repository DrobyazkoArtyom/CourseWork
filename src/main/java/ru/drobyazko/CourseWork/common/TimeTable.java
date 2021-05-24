package ru.drobyazko.CourseWork.common;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TimeTable {

    private final CopyOnWriteArrayList<ShipSlot> shipSlotList;
    private int optimalBulkCraneAmount = 0;
    private int optimalLiquidCraneAmount = 0;
    private int optimalContainerCraneAmount = 0;

    public TimeTable() {
        shipSlotList = new CopyOnWriteArrayList<>();
    }

    public List<ShipSlot> getShipSlotList() {
        return shipSlotList;
    }

    public int getOptimalBulkCraneAmount() {
        return optimalBulkCraneAmount;
    }

    public int getOptimalLiquidCraneAmount() {
        return optimalLiquidCraneAmount;
    }

    public int getOptimalContainerCraneAmount() {
        return optimalContainerCraneAmount;
    }

    public void setOptimalBulkCraneAmount(int optimalBulkCraneAmount) {
        this.optimalBulkCraneAmount = optimalBulkCraneAmount;
    }

    public void setOptimalLiquidCraneAmount(int optimalLiquidCraneAmount) {
        this.optimalLiquidCraneAmount = optimalLiquidCraneAmount;
    }

    public void setOptimalContainerCraneAmount(int optimalContainerCraneAmount) {
        this.optimalContainerCraneAmount = optimalContainerCraneAmount;
    }

    public void addShipSlot(ShipSlot shipSlot) {
        shipSlotList.add(shipSlot);
    }

}
