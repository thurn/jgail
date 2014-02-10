package ca.thurn.uct.core;

/**
 * An Agent is any entity capable of analyzing a given game state and selecting
 * an action.
 *
 * @param <A> Action type for this game
 */
public interface FastAgent {
  /**
   * Picks an action for the provided player to take from the provided root
   * node. 
   *
   * @param player The player whose turn it is.
   * @param rootNode The current state of the game, using the state
   *     representation this agent returned from
   *     {@link FastAgent#getStateRepresentation()}.
   * @return An ActionScore pair consisting of the action this player should
   *     take in the current game state and an optional corresponding heuristic
   *     score for this action, where a higher number indicates a better action
   *     for the player.
   */
  public long pickAction(int player, FastState rootNode);
  
  public float getScoreForLastAction();
  
  /**
   * @return A null-initialized state object
   */
  public FastState getStateRepresentation();
}
