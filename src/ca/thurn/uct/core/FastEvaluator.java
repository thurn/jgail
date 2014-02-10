package ca.thurn.uct.core;


/**
 * An evaluation function.
 * 
 * @param <A> Action type for this game.
 */
public interface FastEvaluator {
  /**
   * @param player A player.
   * @param state A given game state.
   * @return A number which should be higher or lower if the provided state is
   *     correspondingly better or worse for the provided player.
   */
  public float evaluate(int player, FastState state);
}
