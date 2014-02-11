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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((hex1 == null) ? 0 : hex1.hashCode());
    result = prime * result + ((hex2 == null) ? 0 : hex2.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    IngeniousPiece other = (IngeniousPiece) obj;
    if (hex1 != other.hex1)
      return false;
    if (hex2 != other.hex2)
      return false;
    return true;
  }
}
