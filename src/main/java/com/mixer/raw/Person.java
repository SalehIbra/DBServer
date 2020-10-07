package com.mixer.raw;

public class Person {
    private String name;
    private int age;
    private String address;
    private String carPlateNumber;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCarPlateNumber() {
        return carPlateNumber;
    }

    public void setCarPlateNumber(String carPlateNumber) {
        this.carPlateNumber = carPlateNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("name: %s, age: %d, address: %S, car plate: %S, description: %S",
                this.name,
                this.age,
                this.address,
                this.carPlateNumber,
                this.description);
    }
}
