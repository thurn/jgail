package ca.thurn.uct.connect4;

import java.util.Scanner;

import ca.thurn.uct.core.ActionScore;
import ca.thurn.uct.core.Agent;
import ca.thurn.uct.core.Player;
import ca.thurn.uct.core.State;

/**
 * A human player of Connect 4.
 */
public class C4HumanAgent implements Agent<C4Action> {

  private final Scanner in = new Scanner(System.in);
  private final State<C4Action> stateRepresentation;
  
  /**
   * @param stateRepresentation A state representation which will be totally
   *     ignored.
   */
  public C4HumanAgent(State<C4Action> stateRepresentation) {
    this.stateRepresentation = stateRepresentation;
  }
  
  @Override
  public ActionScore<C4Action> pickAction(Player player, State<C4Action> rootNode) {
    System.out.println("Select a column [0,6]");
    int column = in.nextInt();
    return new ActionScore<C4Action>(new C4Action(player, column));
  }

  @Override
  public State<C4Action> getStateRepresentation() {
    return stateRepresentation;
  }
  
  @Override
  public String toString() {
    return "Human";
  }

}
