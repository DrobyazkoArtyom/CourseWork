package ru.drobyazko.common;

import java.util.ArrayList;
import java.util.List;

public class TimeTable {

    private final List<ShipSlot> shipSlotList;

    public TimeTable() {
        shipSlotList = new ArrayList<>();
    }


    public List<ShipSlot> getShipSlotList() {
        return shipSlotList;
    }


    public void addShipSlot(ShipSlot shipSlot) {
        shipSlotList.add(shipSlot);
    }

}
