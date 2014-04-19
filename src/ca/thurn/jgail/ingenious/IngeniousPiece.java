package ca.thurn.jgail.ingenious;

/**
 * A piece in Ingenious, consisting of two colored hexes.
 */
public class IngeniousPiece {
  // Bit pattern:
  // AAAA BBBB
  // BBBB = hex 1
  // AAAA = hex 2
  
  /**
   * Create an integer representing an ingenious piece.
   *
   * @param hex1 Integer representing the color of the first hex.
   * @param hex2 Integer representing the color of the second hex.
   * @return Integer representing the combined piece.
   */
  public static int create(int hex1, int hex2) {
    return (hex2 << 4) | hex1;
  }

  /**
   * @param piece Integer representing an ingenious piece.
   * @return Integer representing first hex's color.
   */
  public static int getHex1(int piece) {
    return piece & 0xF;
  }

  /**
   * @param piece Integer representing an ingenious piece.
   * @return Integer representing second hex's color.
   */
  public static int getHex2(int piece) {
    return (piece >> 4) & 0xF;
  }
  
  /**
   * @param piece Integer representing an ingenious piece.
   * @return String representation of the piece.
   */
  public static String toString(int piece) {
    return "[" + IngeniousHex.toString(getHex1(piece)) + " " + 
        IngeniousHex.toString(getHex2(piece)) + "]";
  }
}
