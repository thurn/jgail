package ca.thurn.jgail.tictactoe;

import java.util.Scanner;

import ca.thurn.jgail.core.ActionScore;
import ca.thurn.jgail.core.Agent;
import ca.thurn.jgail.core.Player;
import ca.thurn.jgail.core.State;

/**
 * A human agent for Tic Tac Toe.
 */
public class TicTacToeHumanAgent implements Agent {

  private final State stateRepresentation;
  private Scanner in = new Scanner(System.in);
  
  /**
   * Creates a new Tic Tac Toe human agent.
   *  
   * @param stateRepresentation State representation, which will be ignored.
   */
  public TicTacToeHumanAgent(State stateRepresentation) {
    this.stateRepresentation = stateRepresentation;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public ActionScore pickActionBlocking(int player, State rootNode) {
    System.out.println("Pick an action [0-8]");
    int shift = in.nextInt();
    int base = player == Player.PLAYER_ONE ? 0x100 : 0x100000;
    return new ActionScore(base >> shift, 0.0);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public State getStateRepresentation() {
    return stateRepresentation;
  }

  @Override
  public String toString() {
    return "Human";
  }
  
}
