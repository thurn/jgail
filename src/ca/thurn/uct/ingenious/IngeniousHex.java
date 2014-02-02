package ca.thurn.uct.ingenious;

import java.util.Random;

import ca.thurn.uct.algorithm.Output;

public enum IngeniousHex {
  ORANGE,
  YELLOW,
  PURPLE,
  RED,
  GREEN,
  BLUE,
  OFF_BOARD;
  
  private static final String ANSI_RESET = "\u001B[0m";
  private static final String ANSI_RED = "\u001B[31m";
  private static final String ANSI_GREEN = "\u001B[32m";
  private static final String ANSI_YELLOW = "\u001B[33m";
  private static final String ANSI_BLUE = "\u001B[34m";
  private static final String ANSI_PURPLE = "\u001B[35m";
  private static final String ANSI_CYAN = "\u001B[36m";
  
  private static final IngeniousHex[] values = {ORANGE, YELLOW, PURPLE, RED, GREEN, BLUE};
    private static final Random random = new Random();
  
  public static IngeniousHex randomHex() {
    return values[random.nextInt(values.length)];
  }
  
  public static IngeniousHex[] allColors() {
    return values;
  }
  
  public String toString() {
    if (Output.getInstance().isColor()) {
      switch(this) {
        case BLUE:
          return ANSI_BLUE + "BB" + ANSI_RESET;
        case GREEN:
          return ANSI_GREEN + "GG" + ANSI_RESET;
        case ORANGE:
          return ANSI_CYAN + "OO" + ANSI_RESET;
        case PURPLE:
          return ANSI_PURPLE + "PP" + ANSI_RESET;
        case RED:
          return ANSI_RED + "RR" + ANSI_RESET;
        case YELLOW:
          return ANSI_YELLOW + "YY" + ANSI_RESET;
        case OFF_BOARD:
          return "";
        default:
          throw new RuntimeException();
      }       
    } else {
      switch(this) {
        case BLUE:
          return "BB";
        case GREEN:
          return "GG";
        case ORANGE:
          return "OO";
        case PURPLE:
          return "PP";
        case RED:
          return "RR";
        case YELLOW:
          return "YY";
        case OFF_BOARD:
          return "";
        default:
          throw new RuntimeException();
      }      
    }
  }
  

}
