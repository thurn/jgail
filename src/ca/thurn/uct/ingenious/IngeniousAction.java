package ca.thurn.uct.ingenious;

import ca.thurn.uct.core.Action;

public class IngeniousAction implements Action {
  private final IngeniousPiece piece;

  // Hex #1 x and y
  private final int x1;
  private final int y1;
  
  // Hex #2 x and y
  private final int x2;
  private final int y2;

  
  public IngeniousAction(IngeniousPiece piece, int x1, int y1, int x2, int y2) {
    this.piece = piece;
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
  }

  public int getX1() {
    return x1;
  }

  public int getY1() {
    return y1;
  }

  public int getX2() {
    return x2;
  }

  public int getY2() {
    return y2;
  }

  public IngeniousPiece getPiece() {
    return piece;
  }
  
}
