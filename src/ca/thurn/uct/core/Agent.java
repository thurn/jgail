package ca.thurn.uct.core;

/**
 * An Agent is any entity capable of analyzing a given game state and selecting
 * an action.
 *
 * @param <A> Action type for this game
 */
public interface Agent<A extends Action> {
  /**
   * Picks an action for the provided player to take from the provided root
   * node. 
   *
   * @param player The player whose turn it is.
   * @param rootNode The current state of the game, using the state
   *     representation this agent returned from
   *     {@link Agent#getStateRepresentation()}.
   * @return An ActionScore pair consisting of the action this player should
   *     take in the current game state and an optional corresponding heuristic
   *     score for this action, where a higher number indicates a better action
   *     for the player.
   */
  public ActionScore<A> pickAction(Player player, State<A> rootNode);
  
  /**
   * @return A null-initialized state object
   */
  public State<A> getStateRepresentation();
}
