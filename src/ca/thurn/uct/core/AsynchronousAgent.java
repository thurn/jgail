package ca.thurn.uct.core;

public interface AsynchronousAgent extends Agent {
  /**
   * Instructs the Agent to kick off an asynchronous action search and then
   * return. This method should not block on completion of the search -- the
   * result will be requested later via getAsynchronousSearchResult.
   * 
   * @param player The player who this Agent is trying to optimize for.
   * @param rootNode The current state of the game.
   */
  public void beginAsynchronousSearch(int player, State rootNode);
  
  /**
   * Halts the current asynchronous action search and returns the result.
   * Returning "null" is allowed to indicate the agent needs more time to find
   * a meaningful result.
   *
   * @return The current best-known action for this agent to take, along with
   *     an associated heuristic score, or null if insufficient time has
   *     elapsed to find a useful result.
   */
  public ActionScore getAsynchronousSearchResult();
}
