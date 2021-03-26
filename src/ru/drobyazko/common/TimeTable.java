package ru.drobyazko.common;

import java.util.ArrayList;
import java.util.List;

public class TimeTable {

    private List<Crane> craneList;
    private List<ShipSlot> shipSlotList;

    public TimeTable() {
        craneList = new ArrayList<>();
        shipSlotList = new ArrayList<>();
    }

    public List<Crane> getCraneList() {
        return craneList;
    }

    public List<ShipSlot> getShipSlotList() {
        return shipSlotList;
    }

    public void addCrane(Crane crane) {
        craneList.add(crane);
    }

    public void addShipSlot(ShipSlot shipSlot) {
        shipSlotList.add(shipSlot);
    }

}
