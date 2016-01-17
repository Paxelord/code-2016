package com.lynbrookrobotics.sixteen.config;

import com.lynbrookrobotics.sixteen.sensors.GyroL3GD20H;
import edu.wpi.first.wpilibj.Jaguar;

public class DrivetrainHardware {
    private Jaguar frontLeftMotor;
    private Jaguar frontRightMotor;
    private Jaguar backLeftMotor;
    private Jaguar backRightMotor;

    private GyroL3GD20H gyro;

    public DrivetrainHardware(VariableConfiguration config) {
        frontLeftMotor = new Jaguar(config.drivetrainPorts().portFrontLeft());
        frontRightMotor = new Jaguar(config.drivetrainPorts().portFrontRight());
        backLeftMotor = new Jaguar(config.drivetrainPorts().portBackLeft());
        backRightMotor = new Jaguar(config.drivetrainPorts().portBackRight());

        gyro = new GyroL3GD20H();
    }

    public Jaguar frontLeftMotor() {
        return frontLeftMotor;
    }

    public Jaguar frontRightMotor() {
        return frontRightMotor;
    }

    public Jaguar backLeftMotor() {
        return backLeftMotor;
    }

    public Jaguar backRightMotor() {
        return backRightMotor;
    }

    public GyroL3GD20H gyro() {
        return gyro;
    }
}
