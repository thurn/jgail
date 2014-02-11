package ca.thurn.uct.ingenious;

/**
 * A piece in Ingenious, consisting of two colored hexes.
 */
public class IngeniousPiece {
  public static int create(int hex1, int hex2) {
    return (hex2 << 4) | hex1;
  }

  public static int getHex1(int piece) {
    return piece & 0xF;
  }

  public static int getHex2(int piece) {
    return (piece >> 4) & 0xF;
  }
  
  public static String toString(int piece) {
    return "[" + IngeniousHex.toString(getHex1(piece)) + " " + 
        IngeniousHex.toString(getHex2(piece)) + "]";
  }
}
