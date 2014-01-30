package ca.thurn.uct.algorithm;
import java.util.List;


public abstract class State<A extends Action> {

  protected List<A> actions;
  protected int stateVisits;
  protected int[] actionVisits;
  protected double[] actionRewards;

  public static class ActionResult<A extends Action> {
    private final State<A> nextState;
    private final double reward;

    public ActionResult(State<A> nextState, double reward) {
      this.nextState = nextState;
      this.reward = reward;
    }

    public State<A> getNextState() {
      return nextState;
    }

    public double getReward() {
      return reward;
    }
  }

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
   * Returns a fast evaluation of the value of being in this state if you are
   * the provided player, to be used to evaluate the situation when the tree
   * search reaches the depth limit or in a terminal state. This value should
   * ideally be in the range [0,1].
   */
  public abstract double evaluate(Player player);

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
   * Returns the result of performing the supplied action. There may be an
   * associated reward for the provided player. You should assume that
   * the provided action will be a legal one. The reward should ideally be in
   * the range [0,1].
   */
  public abstract ActionResult<A> perform(Player player, A action);

  /**
   * Returns whether or not this state is terminal. 
   */
  public abstract boolean isTerminal();
  
  public abstract Player getCurrentPlayer();
  
  public Player getWinner() {
    return null;
  }
}
