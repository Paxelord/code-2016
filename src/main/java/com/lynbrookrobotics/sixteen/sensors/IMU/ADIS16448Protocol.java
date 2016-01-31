package com.lynbrookrobotics.sixteen.sensors.imu;

import com.lynbrookrobotics.sixteen.sensors.ConstantBufferSPI;
import com.lynbrookrobotics.sixteen.sensors.Value3D;
import edu.wpi.first.wpilibj.SPI;

import java.nio.ByteBuffer;

/**
 * Implements the SPI protocol for the ADIS16448 IMU
 */
class ADIS16448Protocol {
    private static class Registers {
        static final IMURegister SMPL_PRD = new IMURegister(0x36);
        static final IMURegister SENS_AVG = new IMURegister(0x38);
        static final IMURegister MSC_CTRL = new IMURegister(0x34);
        static final IMURegister PROD_ID = new IMURegister(0x56);
        static final IMURegister XGYRO_OFF = new IMURegister(0x1A);
        static final IMURegister YGYRO_OFF = new IMURegister(0x1C);
        static final IMURegister ZGYRO_OFF = new IMURegister(0x1E);

    }

    private static class Constants {
        // TODO: test with 0.01 as per http://www.analog.com/media/en/technical-documentation/data-sheets/ADIS16448.pdf#page=23
        static final double DegreePerSecondPerLSB = 1.0 / 25.0;
        static final double GPerLSB = 1.0 / 1200.0;
        static final double MilligaussPerLSB = 1.0 / 7.0;
    }

    // TODO: what is the global command doing?
    private static final byte[] globalCommand = {0x08, 0};
    private ConstantBufferSPI spi;

    public double angularDriftX = 0;
    public double angularDriftY = 0;
    public double angularDriftZ = 0;

    public ADIS16448Protocol() {
        spi = new ConstantBufferSPI(SPI.Port.kMXP, 26);
        spi.setClockRate(100000); // TODO: check if this is a random number
        spi.setMSBFirst();
        spi.setSampleDataOnFalling();
        spi.setClockActiveLow();
        spi.setChipSelectActiveLow();

        Registers.PROD_ID.read(spi); // TODO: check if dummy read is needed
        if (Registers.PROD_ID.read(spi) != 16448) {
            throw new IllegalStateException("The device in the MXP port is not an ADIS16448 IMU");
        }

        Registers.SMPL_PRD.write(769, spi); // TODO: Magic Number
        Registers.MSC_CTRL.write(4, spi); // TODO: Magic Number
        Registers.SENS_AVG.write(1030, spi); // TODO: Magic Number

        //angularDriftX = getGyroRegister(new byte[]{ , 0 }) * Constants.DegreePerSecondPerLSB;
        //angularDriftY = getGyroRegister(new byte[]{ , 0 }) * Constants.DegreePerSecondPerLSB;
        //angularDriftZ = getGyroRegister(new byte[]{ , 0 }) * Constants.DegreePerSecondPerLSB;
    }

    private short getGyroRegister(byte[] outData) {
        byte[] gyroData = new byte[2];
        spi.transaction(new byte[]{ 0x08, 0 }, gyroData, 2);
        ByteBuffer gyroBuffer = ByteBuffer.wrap(gyroData);

        return gyroBuffer.getShort();
    }

    /**
     * @return the current gyro, accel, and magneto data from the IMU
     */
    public IMUValue currentData() {
        Value3D gyro = new Value3D(
                getGyroRegister(new byte[]{ 0x04, 0 }) * Constants.DegreePerSecondPerLSB,
                getGyroRegister(new byte[]{ 0x06, 0 }) * Constants.DegreePerSecondPerLSB,
                getGyroRegister(new byte[]{ 0x08, 0 }) * Constants.DegreePerSecondPerLSB
        );

        // TODO: kalman calculation?
        return new IMUValue(gyro, null, null);
    }
}
