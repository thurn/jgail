package ca.thurn.uct.algorithm;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class State<A extends Action, S extends State<A, S>> {

  protected List<A> actions;
  protected int stateVisits;
  protected Map<A, Integer> actionVisits;
  protected Map<A, Double> actionRewards;

  public class ActionResult {
    private final S nextState;
    private final double reward;

    public ActionResult(S nextState, double reward) {
      this.nextState = nextState;
      this.reward = reward;
    }

    public S getNextState() {
      return nextState;
    }

    public double getReward() {
      return reward;
    }
  }

  public State(List<A> actions) {
    this.actions = actions;
    this.stateVisits = 0;
    this.actionVisits = new HashMap<A, Integer>();
    this.actionRewards = new HashMap<A, Double>();
  }

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
    addZerosForAction(action);
    // Estimate payoff of an action based on the win rate
    if (actionVisits.get(action) == 0) return 0.0;
    return actionRewards.get(action) / actionVisits.get(action);
  }

  /**
   * Marks this state and action as visited, and then stores the reward that
   * was obtained by selecting this action.
   */
  public void addReward(A action, double reward) {
    addZerosForAction(action);
    stateVisits++;
    actionVisits.put(action, actionVisits.get(action) + 1);
    actionRewards.put(action, actionRewards.get(action) + reward);
  }

  /**
   * Returns a fast evaluation of the value of this state, to be used to
   * evaluate the situation when the tree search reaches the depth limit.
   * This value should ideally be in the range [0,1].
   */
  public abstract double evaluate();

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
    addZerosForAction(action);
    return actionVisits.get(action);
  }

  /**
   * Returns the result of performing the supplied action, which may have an
   * associated reward and will have a resulting state. You should assume that
   * the provided action will be a legal one. The reward should ideally be in
   * the range [0,1].
   */
  public abstract ActionResult perform(A action);

  /**
   * Returns whether or not this state is terminal. 
   */
  public abstract boolean isTerminal();
  
  private void addZerosForAction(A action) {
    if (!actionVisits.containsKey(action)) {
      actionVisits.put(action, 0);
    }
    if (!actionRewards.containsKey(action)) {
      actionRewards.put(action, 0.0);
    }    
  }
}
