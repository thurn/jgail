package ca.thurn.jgail.core;

import java.util.Iterator;



/**
 * Represents the game state of a given game. A state is responsible for
 * tracking whose turn it is, what actions have been taken, and if anybody has
 * won. A good state will strive to implement the methods in its interface as
 * efficiently as possible, particularly ones like perform() and getWinner(),
 * since they will likely be called extremely frequently. States should usually
 * have only one public constructor: a zero-argument one which performs no
 * work and null-initializes the state.
 */
public interface State extends Copyable {
  
  /**
   * An iterator over the actions available from a state in an undefined order.
   */
  public static interface ActionIterator {
    /**
     * @return Any legal action from the underlying state which has not
     *     been returned on a previous call.
     */
    public long nextAction();
    
    /**
     * @return True if all legal actions from the underlying state have been
     *     returned by calls to {@link ActionIterator#nextAction()}.
     */
    public boolean hasNextAction();
  }
  
  /**
   * Adapter class to convert an Iterable<Long> into an ActionIterator.
   */
  public static class ActionIteratorFromIterable implements ActionIterator {
    
    private final Iterator<Long> iterator;
    
    /**
     * Create a new wrapper for the provider iterable.
     *
     * @param iterable The iterable to wrap.
     */
    public ActionIteratorFromIterable(Iterable<Long> iterable) {
      this.iterator = iterable.iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long nextAction() {
      return iterator.next();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNextAction() {
      return iterator.hasNext();
    }
  }
  
  /**
   * Gets an action iterator for this state.
   * 
   * @return An iterator over the set of all actions which are currently
   *     possible from this state. This iterator should be "perform-tolerant",
   *     meaning that a user can perform() and then undo() an arbitrary number
   *     of actions between calls to {@link ActionIterator#nextAction()}
   *     without breaking the iterator or causing it to lose its place.
   */
  public ActionIterator getActionIterator();
  
  /**
   * @return A random action which is legal from this state.
   */
  public long getRandomAction();
  
  /**
   * Performs the provided action by mutating the state. You should assume that
   * the provided action will be a legal one, it is the responsibility of
   * {@link State#getActionIterator()} to return only legal actions.
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
   * Initialize this state from the information in the provided object.
   * Typically, this method should make a copy of the provided object and then
   * cast it to a known representation to initialize its fields. In general,
   * this method should handle initialization from another instance of this
   * State as well as initialization from some canonical state object.
   * 
   * @param state The object to initialize this state from.
   * @return this.
   */
  public State initializeFrom(Copyable state);
 
  /**
   * @return True if there are no more actions possible from this state (the
   *     game has ended).
   */
  public boolean isTerminal();
  
  /**
   * @return The Player who won the game in this state. If there is no
   *     winner (the game is a draw, not yet over, etc), returns 0.
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
  
  /**
   * @param action One of this state's actions.
   * @return A String representation of this action.
   */
  public String actionToString(long action);
}
