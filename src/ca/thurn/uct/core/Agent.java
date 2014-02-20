package ca.thurn.uct.core;

/**
 * An Agent is any entity capable of analyzing a given game state and selecting
 * an action.
 */
public interface Agent {
  /**
   * Picks an action for the provided player to take from the provided root
   * node. 
   *
   * @param player The player who this Agent is trying to optimize for.
   * @param rootNode The current state of the game, using the state
   *     representation this agent returned from
   *     {@link Agent#getStateRepresentation()}.
   * @return An ActionScore pair consisting of the action this player should
   *     take in the current game state and an optional corresponding heuristic
   *     score for this action, where a higher number indicates a better action
   *     for the player.
   */
  public ActionScore pickActionSynchronously(int player, State rootNode);
  
  /**
   * @return A null-initialized state object of the class this agent wishes
   *     to use for its internal state representation.
   */
  public State getStateRepresentation();
}
