package com.lynbrookrobotics.sixteen.config.constants;

public class ShootingPositionConstants {
  public static final double DISTANCE_WHEEL_BOTTOM = 29; // inches
  public static final double UP_BATTER_SHOOT = 0; // inches

  public static final double LEFT_BATTER_TURN = 180 - 114;

  public static final double ONE_FORWARD = (94 + (DISTANCE_WHEEL_BOTTOM / 2)) / 12;
  public static final double ONE_TURN = LEFT_BATTER_TURN;
  public static final double ONE_FORWARD_SECOND = (98 + UP_BATTER_SHOOT) / 12;

  public static final double TWO_FORWARD = (150 + (DISTANCE_WHEEL_BOTTOM / 2)) / 12;
  public static final double TWO_TURN = LEFT_BATTER_TURN;
  public static final double TWO_FORWARD_SECOND = 29;

  public static final double THREE_FORWARD_FIRST = (37 + (DISTANCE_WHEEL_BOTTOM / 2)) / 12;
  public static final double THREE_TURN_FIRST = 45;
  public static final double THREE_FORWARD_SECOND = (36 * Math.sqrt(2)) / 12;
  public static final double THREE_TURN_SECOND = -45;
  public static final double THREE_FORWARD_THIRD = (30 - (DISTANCE_WHEEL_BOTTOM / 2)) / 12;

  public static final double FOUR_FORWARD_FIRST = (37 + (DISTANCE_WHEEL_BOTTOM / 2)) / 12;
  public static final double FOUR_TURN_FIRST = -45;
  public static final double FOUR_FORWARD_SECOND = (12 * Math.sqrt(2)) / 12;
  public static final double FOUR_TURN_SECOND = 45;
  public static final double FOUR_FORWARD_THIRD = (54 - (DISTANCE_WHEEL_BOTTOM / 2)) / 12;

  public static final double FIVE_FORWARD = (123.8 + (DISTANCE_WHEEL_BOTTOM / 2)) / 12;
  public static final double FIVE_TURN = -60; // 120 left
  public static final double FIVE_FORWARD_SECOND = 0;
  // ^ 9.4 inches without centered, might have to back up
}
