package ru.drobyazko.CourseWork.common;

public interface ShipService {
    int tickAmount = 43200;
    ShipSlot requestShip(CraneRunnable craneRunnable);
    void barrierAwait();
}
