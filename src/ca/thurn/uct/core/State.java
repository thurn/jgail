package ca.thurn.uct.core;



/**
 * Represents the game state of a given game. A state is responsible for
 * tracking whose turn it is, what actions have been taken, and if anybody has
 * won. A good state will strive to implement the methods in its interface as
 * efficiently as possible, particularly ones like perform() and getWinner(),
 * since they will likely be called extremely frequently. States should usually
 * have only one public constructor: a zero-argument one which performs no
 * work and null-initializes the state.
 */
public interface State {
  
  /**
   * @return All of the actions which are currently possible from this state.
   */
  public Iterable<Long> getActions();
  
  /**
   * @return A random action which is legal from this state.
   */
  public long getRandomAction();
  
  /**
   * Performs the provided action by mutating the state. You should assume that
   * the provided action will be a legal one, it is the responsibility of
   * {@link State#getActions()} to return only legal actions.
   * 
   * @param action The action to perform.
   * @return An "undo token" which must be passed to
   *     {@link State#undo(long, long)} in order to undo this action.
   */
  public long perform(long action);
  
  /**
   * Undoes the provided action by mutating the state back to the way that it
   * was. It is the responsibility of callers to only supply the most recently
   * performed action as an argument.
   * 
   * @param action The action to undo.
   * @param undoToken The undo token returned from
   *     {@link State#undo(long, long)} when this action was performed.
   */
  public void undo(long action, long undoToken);
  
  /**
   * Put this state in the starting condition for the game, the state before
   * any actions have been taken.
   * @return this 
   */
  public State setToStartingConditions();
  
  /**
   * @return A complete copy of this state.
   */
  public State copy();
  
  /**
   * Mutates this state to be a copy of the provided state. This method should
   * make a copy of the provided state via {@link State#copy()} and then assign
   * the fields from the result to its fields. It will typically be necessary
   * to cast the provided state to the correct value. The method should at a
   * minimum handle initialization from another instance of this class and
   * initialization from the game's canonical state.
   * 
   * @param state The state to initialize this state from.
   * @return this.
   */
  public State initialize(State state);
  
  /**
   * @return True if there are no more actions possible from this state (the
   *     game has ended).
   */
  public boolean isTerminal();
  
  /**
   * @return The Player who won the game in this state. If there is no
   *     winner (the game is a draw, not yet over, etc), returns null.
   */
  public int getWinner();

  /**
   * @return The Player whose turn it is in this state.
   */
  public int getCurrentPlayer();
  
  /**
   * @param player A player.
   * @return The player who will follow the provided player in the game's turn
   *     sequence.
   */
  public int playerAfter(int player);
  
  /**
   * @param player A player.
   * @return The player who is before the provided player in the game's turn
   *     sequence.
   */
  public int playerBefore(int player);
}
