package com.lynbrookrobotics.sixteen;

import com.lynbrookrobotics.funkydashboard.TimeSeriesNumeric;
import com.lynbrookrobotics.potassium.defaults.events.InGameState;
import com.lynbrookrobotics.potassium.events.SteadyEventHandler;
import com.lynbrookrobotics.potassium.tasks.FiniteTask;
import com.lynbrookrobotics.potassium.tasks.Task;
import com.lynbrookrobotics.sixteen.components.drivetrain.Drivetrain;
import com.lynbrookrobotics.sixteen.components.drivetrain.TankDriveController;
import com.lynbrookrobotics.sixteen.config.DriverControls;
import com.lynbrookrobotics.sixteen.config.RobotConstants;
import com.lynbrookrobotics.sixteen.config.RobotHardware;
import com.lynbrookrobotics.sixteen.tasks.drivetrain.TurnByAngle;
import edu.wpi.first.wpilibj.Joystick;

public class CoreEvents {
    DriverControls controls;
    RobotHardware hardware;
    Drivetrain drivetrain;

    boolean initialCalibrationDone = false;

    // Game State
    InGameState disabledStateEvent;
    InGameState autonomousStateEvent;
    InGameState enabledStateEvent;

    // Drivetrain
    TankDriveController enabledDrive = new TankDriveController(
        () -> controls.driverStick().getAxis(Joystick.AxisType.kY),
        () -> controls.driverWheel().getAxis(Joystick.AxisType.kX)
    );

    FiniteTask auto;

    public CoreEvents(DriverControls controls, RobotHardware hardware, Drivetrain drivetrain) {
        this.controls = controls;
        this.drivetrain = drivetrain;
        this.hardware = hardware;

        this.disabledStateEvent = new InGameState(controls.driverStation(), InGameState.GameState.DISABLED);
        this.autonomousStateEvent = new InGameState(controls.driverStation(), InGameState.GameState.AUTONOMOUS);
        this.enabledStateEvent = new InGameState(controls.driverStation(), InGameState.GameState.ENABLED);

        this.auto =
                new TurnByAngle(360, hardware, drivetrain).
                        then(new TurnByAngle(360, hardware, drivetrain)).
                        then(new TurnByAngle(360, hardware, drivetrain)).
                        then(new TurnByAngle(360, hardware, drivetrain));
//                        then(new TurnByAngle(90, hardware, drivetrain)).
//                        then(new TurnByAngle(90, hardware, drivetrain)).
//                        then(new TurnByAngle(90, hardware, drivetrain)).
//                        then(new TurnByAngle(90, hardware, drivetrain)).
//                        then(new TurnByAngle(90, hardware, drivetrain)).
//                        then(new TurnByAngle(90, hardware, drivetrain)).
//                        then(new TurnByAngle(90, hardware, drivetrain)).
//                        then(new TurnByAngle(90, hardware, drivetrain));

        initEventMappings();
    }

    public void initEventMappings() {
        // Drivetrain
        // Drivetrain - Gyro
        disabledStateEvent.forEach(new SteadyEventHandler() {
            @Override
            public void onStart() {}

            @Override
            public void onRunning() {
                if (!initialCalibrationDone) {
                    hardware.drivetrainHardware().gyro().calibrateUpdate();
                } else {
                    hardware.drivetrainHardware().gyro().angleUpdate();
                }
            }

            @Override
            public void onEnd() {}
        });

//        enabledStateEvent.forEach(new SteadyEventHandler() {
//            @Override
//            public void onStart() {
//                Task.executeTask(auto);
//            }
//
//            @Override
//            public void onRunning() {}
//
//            @Override
//            public void onEnd() {
//                Task.abortTask(auto);
//            }
//        });


        enabledStateEvent.forEach(new SteadyEventHandler() {
            @Override
            public void onStart() {}

            @Override
            public void onRunning() {
                initialCalibrationDone = true;
                hardware.drivetrainHardware().gyro().angleUpdate();
            }

            @Override
            public void onEnd() {}
        });

        RobotConstants.dashboard().datasetGroup("drivetrain").
                addDataset(new TimeSeriesNumeric<>(
                        "Gyro Velocity",
                        hardware.drivetrainHardware().gyro()::getZVel));

        RobotConstants.dashboard().datasetGroup("drivetrain").
                addDataset(new TimeSeriesNumeric<>(
                        "Gyro Position",
                        hardware.drivetrainHardware().gyro()::getZAngle));

        // Drivetrain - Joystick
        enabledStateEvent.forEach(new SteadyEventHandler() {
            @Override
            public void onStart() {
                drivetrain.setController(enabledDrive);
            }

            @Override
            public void onRunning() {}

            @Override
            public void onEnd() {
                drivetrain.resetToDefault();
            }
        });
    }
}
