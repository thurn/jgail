package ca.thurn.uct.ingenious;

import java.util.Scanner;

import ca.thurn.uct.core.ActionScore;
import ca.thurn.uct.core.Agent;
import ca.thurn.uct.core.Player;
import ca.thurn.uct.core.State;

/**
 * Human agent for Ingenious.
 */
public class CopyOfIngeniousHumanAgent implements Agent<FastIngeniousAction> {

  private Scanner in = new Scanner(System.in);  
  
  private final State<FastIngeniousAction> stateRepresentation;
  
  /**
   * @param stateRepresentation A state representation which will be totally
   *     ignored.
   */
  public CopyOfIngeniousHumanAgent(State<FastIngeniousAction> stateRepresentation) {
    this.stateRepresentation = stateRepresentation;
  }
  
  public ActionScore<FastIngeniousAction> pickAction(Player player, State<FastIngeniousAction> rootNode) {
    CopyOfIngeniousState state = (CopyOfIngeniousState)rootNode;
    FastIngeniousPiece piece = null;
    byte index, x1, y1, x2, y2;
    while (true) {
      System.out.println("Select a piece [0,5]");
      index = in.nextByte();
      piece = state.getPiece(player, index);
      System.out.println("Enter x1");
      x1 = in.nextByte();
      System.out.println("Enter y1");
      y1 = in.nextByte();
      System.out.println("Enter x2");
      x2 = in.nextByte();
      System.out.println("Enter y2");
      y2 = in.nextByte();
      if (state.isOpen(x1, y1) && state.isOpen(x2, y2)) {
        try {
          state.hexDirection(x1, y1, x2, y2);
          break;
        } catch (RuntimeException rte) {
          // Retry
        }
      }
      System.out.println("Invalid action selection!");      
    }
    return new ActionScore<FastIngeniousAction>(FastIngeniousAction.create(piece, x1, y1, x2, y2));
  }

  @Override
  public State<FastIngeniousAction> getStateRepresentation() {
    return stateRepresentation;
  }


}
