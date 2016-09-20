package com.lynbrookrobotics.sixteen.sensors.imu;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import edu.wpi.first.wpilibj.SPI;

/**
 * Represents a register on the ADIS16448 IMU.
 */
class IMURegister {
  int register;
  private ByteBuffer readBuffer;

  private byte[] readMessage;
  private byte writeMessage1;
  private byte writeMessage2;

  public IMURegister(int register) {
    this.register = register;
    readBuffer = ByteBuffer.allocateDirect(2);
    readBuffer.order(ByteOrder.BIG_ENDIAN);

    readMessage = new byte[]{(byte) (register & 0x7f), 0};
    writeMessage1 = (byte) (0x80 | register);
    writeMessage2 = (byte) (0x81 | register);
  }

  /**
   * Reads a value from the register.
   * @param spi the interface to use for communication
   * @return a single value from the register
   */
  public int read(SPI spi) {
    readBuffer.clear();
    spi.write(readMessage, 2);
    spi.read(false, readBuffer, 2);

    return ((int) readBuffer.getShort(0)) & 0xffff;
  }

  /**
   * Writes a single value to the register.
   *
   * @param value the value to write
   * @param spi   the interface to use for communication
   */
  public void write(int value, SPI spi) {
    spi.write(new byte[]{writeMessage1, (byte) value}, 2);
    spi.write(new byte[]{writeMessage2, (byte) (value >> 8)}, 2);
  }
}
