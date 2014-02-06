package ca.thurn.uct.algorithm;

import ca.thurn.uct.core.Action;
import ca.thurn.uct.core.Player;

public interface Evaluator<A extends Action> {
  
  /**
   * Returns a number which shoulder be higher or lower if the provided game
   * state is better or worse for the provided player.
   */
  public double evaluate(Player player, State<A> state);
}
