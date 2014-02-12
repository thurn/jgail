package ca.thurn.uct.core;


/**
 * An evaluation function.
 */
public interface Evaluator {
  /**
   * @param player A player.
   * @param state A given game state.
   * @return A number which should be higher or lower if the provided state is
   *     correspondingly better or worse for the provided player.
   */
  public double evaluate(int player, State state);
}
