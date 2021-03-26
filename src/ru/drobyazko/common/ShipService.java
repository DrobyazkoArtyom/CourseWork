package ru.drobyazko.common;

public interface ShipService {
    int tickAmount = 43200;
    ShipSlot requestShip(CraneRunnable craneRunnable);
    void barrierAwait();
}
