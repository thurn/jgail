package ca.thurn.uct.ingenious;

/**
 * A piece in Ingenious, consisting of two colored hexes.
 */
public class IngeniousPiece {
  private final IngeniousHex hex1;
  private final IngeniousHex hex2;
  
  public IngeniousPiece(IngeniousHex hex1, IngeniousHex hex2) {
    this.hex1 = hex1;
    this.hex2 = hex2;
  }

  public IngeniousHex getHex1() {
    return hex1;
  }

  public IngeniousHex getHex2() {
    return hex2;
  }
  
  public String toString() {
    return "[" + hex1 + " " + hex2 + "]";
  }
}
