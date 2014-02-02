package ca.thurn.uct.algorithm;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class State<A extends Action> {

  List<A> actions;
  int stateVisits;
  Map<Action, Integer> actionVisits;
  Map<Action, Double> actionRewards;
  Map<A, State<A>> performCache;

  public State() {
    this.stateVisits = 0;
    this.actionVisits = new HashMap<Action, Integer>();
    this.actionRewards = new HashMap<Action, Double>();
    this.performCache = new HashMap<A, State<A>>();
  }
  
  public <K,V> V getWithDefault(Map<K, V> map, K key, V defaultValue) {
    V ret = map.get(key);
    if (ret == null) {
        return defaultValue;
    }
    return ret;
  }
  
  /**
   * Sets the legal actions from this state. This should be called early in a state's life cycle,
   * ideally from the constructor.
   */
  public void setActions(List<A> actions) {
    this.actions = actions;
  }

  /**
   * Returns actions available from this state.
   */
  public List<A> getActions() {
    return actions;
  }
  
  public State<A> copy() {
    State<A> result = copyInternal();
    result.actionRewards = new HashMap<Action, Double>(actionRewards);
    result.actionVisits = new HashMap<Action, Integer>(actionVisits);
    result.stateVisits = stateVisits;
    result.performCache = new HashMap<A, State<A>>(performCache);
    return result;
  }
  
  /**
   * Returns a copy of this state. It's OK to re-use immutable objects in the
   * resulting state, but mutable ones should be deep-copied.
   */
  protected abstract State<A> copyInternal();

  /**
   * Returns the average payoff of taking the provided action from this
   * state, potentially based the average historical payoff. This is called
   * Q in the original UCT paper.
   */
  public double averagePayoff(A action) {
    // Estimate payoff of an action based on the win rate
    if (getWithDefault(actionVisits, action, 0) == 0) return 0.0;
    return getWithDefault(actionRewards, action, 0.0) / getWithDefault(actionVisits, action, 0);
  }

  /**
   * Marks this state and action as visited, and then stores the reward that
   * was obtained by selecting this action.
   */
  public void addReward(A action, double reward) {
    stateVisits++;
    actionVisits.put(action, getWithDefault(actionVisits, action, 0) + 1);
    actionRewards.put(action, getWithDefault(actionRewards, action, 0.0) + reward);
  }
  
  /**
   * Set any shared mutate state back to how it was when prepareForSimulation
   * was called. Note that it is usually NOT sufficient to simply assign a new
   * value to a field here, because multiple State objects will have a
   * reference to that state. You instead need to mutate the shared reference
   * back to the original state.
   */
  public void reset() {
  }
  
  public void prepareForSimulation() {
  }

  /**
   * Returns the number of times this state has been visited in this
   * simulation. This is called N_{s,d} in the original UCT paper.
   */
  public int numberOfTimesVisited() {
    return stateVisits;
  }

  /**
   * Returns the number of times the provided action has been selected when
   * this state was visited. This is called N_{s,a,d} in the original UCT
   * paper.
   */
  public int numberOfTimesActionSelected(A action) {
    return getWithDefault(actionVisits, action, 0);
  }

  /**
   * Returns the resulting state after performing the supplied action. You
   * should assume that the provided action will be a legal one.
   */
  public State<A> perform(A action) {
    State<A> state = performCache.get(action);
    if (state == null) {
      State<A> result = performInternal(action);
      performCache.put(action, result);
      return result;
    } else {
      return state;
    }
  }
  
  protected abstract State<A> performInternal(A action);
  
  /**
   * Undoes the provided action, returning the resulting state.
   */
  public abstract State<A> unperform(A action);
  
  /**
   * Returns the player whose turn will be after the provided player.
   */
  public Player playerAfter(Player player) {
    return player == Player.PLAYER_ONE ? Player.PLAYER_TWO :
      Player.PLAYER_ONE;
  }

  /**
   * Returns whether or not this state is terminal. 
   */
  public abstract boolean isTerminal();
  
  public abstract Player getCurrentPlayer();
  
  public abstract Player getWinner();
}
