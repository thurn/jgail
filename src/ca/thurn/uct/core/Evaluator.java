package ca.thurn.uct.core;

import ca.thurn.uct.core.Action;
import ca.thurn.uct.core.Player;

/**
 * An evaluation function.
 * 
 * @param <A> Action type for this game.
 */
public interface Evaluator<A extends Action> {
  /**
   * @param player A player.
   * @param state A given game state.
   * @return A number which should be higher or lower if the provided state is
   *     correspondingly better or worse for the provided player.
   */
  public double evaluate(Player player, State<A> state);
}
