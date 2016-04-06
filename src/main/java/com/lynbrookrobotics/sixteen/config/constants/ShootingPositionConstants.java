package com.lynbrookrobotics.sixteen.config.constants;

public class ShootingPositionConstants {
  public static final double DISTANCE_WHEEL_BOTTOM = 29; // inches
  public static final double UP_BATTER_SHOOT = 0; // inches

  public static final double LEFT_BATTER_TURN = 180 - 119;

  // tower 170 fd 120 side
  public static final double ONE_FORWARD = (85 + (DISTANCE_WHEEL_BOTTOM / 2)) / 12;
  public static final double ONE_TURN = LEFT_BATTER_TURN;
  public static final double ONE_FORWARD_SECOND = (79 + UP_BATTER_SHOOT) / 12;

  public static final double TWO_FORWARD = (104 + (DISTANCE_WHEEL_BOTTOM / 2)) / 12;
  public static final double TWO_TURN = LEFT_BATTER_TURN;
  public static final double TWO_FORWARD_SECOND = 30 / 12D;

  public static final double THREE_FORWARD_FIRST = (3 + (DISTANCE_WHEEL_BOTTOM / 2)) / 12;
  public static final double THREE_TURN_FIRST = 45;
  public static final double THREE_FORWARD_SECOND = (52 * Math.sqrt(2)) / 12;
  public static final double THREE_TURN_SECOND = -45;
  public static final double THREE_FORWARD_THIRD = 6D / 12;

  public static final double FOUR_FORWARD_FIRST = (15 + (DISTANCE_WHEEL_BOTTOM / 2)) / 12;
  public static final double FOUR_TURN_FIRST = -45;
  public static final double FOUR_FORWARD_SECOND = (6 * Math.sqrt(2)) / 12;
  public static final double FOUR_TURN_SECOND = 45;
  public static final double FOUR_FORWARD_THIRD = 45D / 12;

  public static final double FIVE_FORWARD_FIRST = (9 + (DISTANCE_WHEEL_BOTTOM / 2)) / 12;
  public static final double FIVE_TURN_FIRST = -45;
  public static final double FIVE_FORWARD_SECOND = (60 * Math.sqrt(2)) / 12;
  public static final double FIVE_TURN_SECOND = 45;
  public static final double FIVE_FORWARD_THIRD = 45D / 12;
  // ^ 9.4 inches without centered, might have to back up
}
