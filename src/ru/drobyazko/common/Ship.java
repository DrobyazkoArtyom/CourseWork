package ru.drobyazko.common;

import java.util.Random;

public class Ship {

    private final int weightBound = 10;
    static private int count = 0;
    private String name;
    private CargoType cargoType;
    private int nominalWeight;
    volatile private int workingWeight;

    public Ship() {
        Random random = new Random();

        this.name = "ship" + (count++);
        this.nominalWeight = random.nextInt(weightBound) + 1;
        this.workingWeight = this.nominalWeight;
        this.cargoType = generateRandomCargoType();
    }

    private CargoType generateRandomCargoType() {
        Random random = new Random();
        CargoType cargoType = CargoType.BULK;

        switch(random.nextInt(3)) {
            case 0:
                cargoType = CargoType.BULK;
                break;
            case 1:
                cargoType = CargoType.LIQUID;
                break;
            case 2:
                cargoType = CargoType.CONTAINER;
                break;
        }

        return cargoType;
    }

    public String getName() {
        return name;
    }

    public CargoType getCargoType() {
        return cargoType;
    }

    public int getNominalWeight() {
        return nominalWeight;
    }

    synchronized public int getWorkingWeight() {
        return workingWeight;
    }

    synchronized public void setWorkingWeight(int workingWeight) {
        this.workingWeight = workingWeight;
    }

    @Override
    public String toString() {
        return "Ship{" +
                "name='" + name + '\'' +
                ", cargoType=" + cargoType +
                ", weight=" + nominalWeight +
                '}';
    }

}
