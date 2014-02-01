package ca.thurn.uct.algorithm;
import java.util.List;


public abstract class State<A extends Action> {

  protected List<A> actions;
  protected int stateVisits;
  protected int[] actionVisits;
  protected double[] actionRewards;

  public State(List<A> actions) {
    this.actions = actions;
    this.stateVisits = 0;
    this.actionVisits = new int[numActions()];
    this.actionRewards = new double[numActions()];
  }
  
  /**
   * Returns the number of possible actions from this state (more precisely,
   * this should be the highest  possible return value for an action's
   * getActionNumber() method).
   */
  public abstract int numActions();

  /**
   * Returns actions available from this state.
   */
  public List<A> getActions() {
    return actions;
  }
  
  public abstract State<A> copy();

  /**
   * Returns the average payoff of taking the provided action from this
   * state, potentially based the average historical payoff. This is called
   * Q in the original UCT paper.
   */
  public double averagePayoff(A action) {
    // Estimate payoff of an action based on the win rate
    int actionNumber = action.getActionNumber();
    if (actionVisits[actionNumber] == 0) return 0.0;
    return actionRewards[actionNumber] / actionVisits[actionNumber];
  }

  /**
   * Marks this state and action as visited, and then stores the reward that
   * was obtained by selecting this action.
   */
  public void addReward(A action, double reward) {
    stateVisits++;
    actionVisits[action.getActionNumber()]++;
    actionRewards[action.getActionNumber()] += reward;
  }
  
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
    return actionVisits[action.getActionNumber()];
  }

  /**
   * Returns the result of performing the supplied action. You should assume
   * that the provided action will be a legal one.
   */
  public abstract State<A> perform(A action);
  
  /**
   * Undoes the provided action, returning the resulting state.
   */
  public abstract State<A> unperform(A action);
  
  /**
   * Returns the player whose turn will be after the provided player.
   */
  public abstract Player playerAfter(Player player);

  /**
   * Returns whether or not this state is terminal. 
   */
  public abstract boolean isTerminal();
  
  public abstract Player getCurrentPlayer();
  
  public Player getWinner() {
    return null;
  }
}
