package ru.drobyazko.common;

public class Crane {

    private CargoType cargoType;
    private int craneEfficiency;

    public Crane(CargoType cargoType, int craneEfficiency) {
        this.cargoType = cargoType;
        this.craneEfficiency = craneEfficiency;
    }

    public CargoType getCargoType() {
        return cargoType;
    }

    public int getCraneEfficiency() {
        return craneEfficiency;
    }

}
