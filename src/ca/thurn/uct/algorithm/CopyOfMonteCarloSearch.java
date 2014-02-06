package ca.thurn.uct.algorithm;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import ca.thurn.uct.algorithm.State.PerformMode;
import ca.thurn.uct.core.Action;
import ca.thurn.uct.core.Player;

public class CopyOfMonteCarloSearch<A extends Action> implements ActionPicker<A> {
  
  public static class Builder<A extends Action> {
    private int numSimulations = 100000;
    
    private int maxDepth = 50;
    
    private Evaluator<A> evaluator = new Evaluator<A>() {
      @Override
      public double evaluate(Player player, State<A> state) {
        return state.getWinner() == player ? 1.0 : -1.0;
      }
    };
    
    public CopyOfMonteCarloSearch<A> build() {
      return new CopyOfMonteCarloSearch<A>(numSimulations, maxDepth, evaluator);
    }

    /**
     * Number of simulations to run before picking the best action from the
     * root node.
     */
    public Builder<A> setNumSimulations(int numSimulations) {
      this.numSimulations = numSimulations;
      return this;
    }

    /**
     * The maximum depth the search to in the simulation.
     */
    public Builder<A> setMaxDepth(int maxDepth) {
      this.maxDepth = maxDepth;
      return this;
    }

    /**
     * Function to use to evaluate the heuristic value of a terminal search
     * node.
     */
    public Builder<A> setEvaluator(Evaluator<A> evaluator) {
      this.evaluator = evaluator;
      return this;
    }
  }
  
  public static <A extends Action> Builder<A> builder() {
    return new Builder<A>();
  }

  private final int numSimulations;  
  private final int maxDepth;
  private final Evaluator<A> evaluator;  
  private final Random random = new Random();
  private Map<A, Double> actionRewards = new HashMap<A, Double>(); 
  
  private CopyOfMonteCarloSearch(int numSimulations, int maxDepth, Evaluator<A> evaluator) {
    this.numSimulations = numSimulations;
    this.maxDepth = maxDepth;
    this.evaluator = evaluator;
  }

  /**
   * Picks an action to take from the provided root node. 
   */
  public A pickAction(Player player, State<A> root) {
    root.prepareForSimulation();
    actionRewards = new HashMap<A, Double>(); 
    for (int i = 0; i < numSimulations; ++i) {
      runSimulation(player, root, 0);
      root.reset();
    }
    double bestReward = Double.NEGATIVE_INFINITY;
    A bestAction = null;
    for (Map.Entry<A, Double> entry : actionRewards.entrySet()) {
      if (entry.getValue() > bestReward) {
        bestReward = entry.getValue();
        bestAction = entry.getKey();
      }
    }
    return bestAction;
  }
  
  /**
   * Runs a simulation to determine the total payoff associated with being at
   * the provided state, tracking the depth in the search tree in "depth".  
   */
  double runSimulation(Player player, State<A> state, int depth) {
    if (depth > maxDepth || state.isTerminal()) {
      return evaluator.evaluate(player, state);
    }
    A action = randomAction(state);
    State<A> nextState = state.perform(action, PerformMode.RETURN_CACHED);
    double reward = runSimulation(player, nextState, depth + 1);
    if (depth == 0) {
      Double current = actionRewards.get(action);
      actionRewards.put(action, current == null ? 0 : current + reward);
    }
    return reward;
  }

  /**
   * Selects an action to take from the provided state randomly
   */
  A randomAction(State<A> state) {
    return state.getActions().get(random.nextInt(state.getActions().size()));
  }
}
