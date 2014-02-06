package ca.thurn.uct.algorithm;

import java.util.Random;

import ca.thurn.uct.core.Action;
import ca.thurn.uct.core.ActionScore;
import ca.thurn.uct.core.ActionTree;
import ca.thurn.uct.core.ActionTree.Mutator;
import ca.thurn.uct.core.Agent;
import ca.thurn.uct.core.Evaluator;
import ca.thurn.uct.core.Player;
import ca.thurn.uct.core.State;

/**
 * An agent which selects actions based on the UCT algorithm described in the
 * 2006 paper "Bandit based Monte-Carlo Planning" by Kocsis and Szepesvari.
 *
 * @param <A> Action type to use.
 */
public class UctSearch<A extends Action> implements Agent<A> {
  
  /**
    * This exploration bias value, 1/sqrt(2), was shown by Kocsis and
    * Szepesvari to work well if rewards are in the range [0,1]. 
   */
  public static double UNIT_EXPLORATION_BIAS = 0.70710678;
  
  /**
   * Builder for UctSearch agents.
   *
   * @param <A> Action type to use.
   */
  public static class Builder<A extends Action> {
    private final State<A> stateRepresentation;
    
    private int numSimulations = 100000;
  
    private double explorationBias = 2.0;

    private double discountRate = 1.0;
    
    private int maxDepth = 50;
    
    private Evaluator<A> evaluator = new Evaluator<A>() {
      @Override
      public double evaluate(Player player, State<A> state) {
        return state.getWinner() == player ? -1.0 : 1.0;
      }
    };
    
    /**
     * Constructor for UctSearch Builders.
     * 
     * @param stateRepresentation State representation to employ for this agent.
     */
    public Builder(State<A> stateRepresentation) {
      this.stateRepresentation = stateRepresentation;
    }
    
    /**
     * @return A new UctSearch agent based on this builder.
     */
    public UctSearch<A> build() {
      return new UctSearch<A>(stateRepresentation, numSimulations, explorationBias,
          discountRate, maxDepth, evaluator);
    }

    /**
     * Number of simulations to run before picking the best action from the
     * root node. Default value: 100000.
     * @return this.
     */
    public Builder<A> setNumSimulations(int numSimulations) {
      this.numSimulations = numSimulations;
      return this;
    }

    /**
     * The multiplier named C_p in the original UCT paper. A higher number
     * places more emphasis on exploration, a lower number places more emphasis
     * on exploitation. Default value: 2.0.
     * @return this.
     */
    public Builder<A> setExplorationBias(double explorationBias) {
      this.explorationBias = explorationBias;
      return this;
    }

    /**
     * The rate at which rewards should be discounted in the future, used to
     * compute the present value of future rewards. This way, rewards further
     * in the future are worth less. This captures our uncertainty about the
     * future, as well as helping avoid infinite reward cycles, etc.
     * Default value: 1.0.
     * @return this.
     */
    public Builder<A> setDiscountRate(double discountRate) {
      this.discountRate = discountRate;
      return this;
    }

    /**
     * The maximum depth the search to in the simulation. Default value: 50.
     * @return this.
     */
    public Builder<A> setMaxDepth(int maxDepth) {
      this.maxDepth = maxDepth;
      return this;
    }

    /**
     * Function to use to evaluate the heuristic value of a terminal search
     * node. Default value returns 0 for losses, 1 for wins, and 0 for all
     * other states.
     * @return this.
     */
    public Builder<A> setEvaluator(Evaluator<A> evaluator) {
      this.evaluator = evaluator;
      return this;
    }
  }
  
  /**
   * @param stateRepresentation State representation to employ.
   * @return A new builder for UctSearch agents.
   */
  public static <A extends Action> Builder<A> builder(State<A> stateRepresentation) {
    return new Builder<A>(stateRepresentation);
  }
  
  /**
   * Associates positions in the game tree with how many times they have been
   * visited and the total rewards obtained from being in that state in
   * simulations.
   */
  private static class ActionData {
    private final int numVisits;
    private final double totalRewards;
    
    private ActionData() {
      this(0, 0.0);
    }
    
    private ActionData(int numVisits, double totalRewards) {
      this.numVisits = numVisits;
      this.totalRewards = totalRewards;
    }

    private int getNumVisits() {
      return numVisits;
    }

    private double getTotalRewards() {
      return totalRewards;
    }
  }

  private final State<A> stateRepresentation;
  private final int numSimulations;
  private final double explorationBias;
  private final double discountRate;
  private final int maxDepth;
  private final Evaluator<A> evaluator;  
  private final Random random = new Random();
  
  private UctSearch(State<A> stateRepresentation, int numSimulations, double explorationBias,
      double discountRate, int maxDepth, Evaluator<A> evaluator) {
    this.stateRepresentation = stateRepresentation;
    this.numSimulations = numSimulations;
    this.explorationBias = explorationBias;
    this.discountRate = discountRate;
    this.maxDepth = maxDepth;
    this.evaluator = evaluator;
  }

  /**
   * Identifies the best action to take.
   *
   * @param player The player to pick an action for.
   * @param root The current game state.
   * @return An ActionScore containing the suggested action along with a
   *     heuristic score indicating how good the action is.
   */
  ActionScore<A> getBestAction(Player player, State<A> root) {
    ActionTree<A, ActionData> actionTree = new ActionTree<A, ActionData>(new ActionData());
    for (int i = 0; i < numSimulations; ++i) {
      runSimulation(actionTree, player, root.copy(), 0);
    }
    double bestPayoff = Double.NEGATIVE_INFINITY;
    A bestAction = null;
    for (A action : root.getActions()) {
      ActionTree<A, ActionData> child = actionTree.child(action);
      double estimatedPayoff = averageReward(child);
      if (estimatedPayoff > bestPayoff) {
        bestPayoff = estimatedPayoff;
        bestAction = action;
      }
    }
    return new ActionScore<A>(bestPayoff, bestAction);
  }
  
  /**
   * {@inheritDoc} 
   */
  @Override
  public A pickAction(Player player, State<A> root) {
    return getBestAction(player, root).getAction();
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public State<A> getStateRepresentation() {
    return stateRepresentation;
  }
  
  /**
   * Runs a simulation to determine the total payoff associated with being at
   * the provided state.
   *
   * @param actionTree An ActionTree tracking the rewards at each game tree
   *     position.
   * @param player The player we are trying to optimize for.
   * @param state The current state.
   * @param depth The current depth in the search tree.
   * @return The heuristic value of being in this state. 
   */
  private double runSimulation(ActionTree<A, ActionData> actionTree, Player player, State<A> state,
      int depth) {
    if (depth > maxDepth || state.isTerminal()) {
      double reward = evaluator.evaluate(player, state);
      updateTree(actionTree, reward);
      return reward;
    }
    A action = uctSelectAction(actionTree, state);
    state.perform(action);
    final double reward = discountRate *
        -runSimulation(actionTree.child(action), state.getCurrentPlayer(), state, depth + 1);
    updateTree(actionTree, reward);
    return reward;
  }

  /**
   * Updates the tree at the given position, adding the given reward and
   * marking this node as visited
   *
   * @param actionTree An ActionTree tracking the rewards at each game tree
   *     position.
   * @param reward The reward associated with this position.
   */
  private void updateTree(ActionTree<A, ActionData> actionTree, final double reward) {
    actionTree.mutate(new Mutator<ActionData>() {
      @Override
      public ActionData mutate(ActionData value) {
        return new ActionData(value.getNumVisits() + 1, value.getTotalRewards() + reward);
      }
    });
  }

  /**
   * Selects an action to take from the provided state via the UCT algorithm.
   *
   * @param actionTree An ActionTree tracking the rewards at each game tree
   *     position.
   * @param state The current state.
   * @return The action to take.
   */
  private A uctSelectAction(ActionTree<A, ActionData> actionTree, State<A> state) {
    // We iterate through each action and return the one that maximizes
    // uctValue.
    double maximum = Double.NEGATIVE_INFINITY;
    A result = null;
    for (A action : state.getActions()) {
      ActionTree<A, ActionData> child = actionTree.child(action);
      double uctValue = averageReward(child) +
          explorationBias(actionTree.getValue().getNumVisits(),
              child.getValue().getNumVisits());
      // We multiply the result by 1000000 and then add a random double from
      // the interval [0,1] in order to break ties.
      uctValue = (uctValue * 1000000) + random.nextDouble();
      if (uctValue > maximum) {
        maximum = uctValue;
        result = action;
      }
    }
    return result;
  }
  
  /**
   * @param actionTree An ActionTree tracking the rewards at each game tree
   *     position.
   * @return The average reward of visiting this state, or 0 if this state has
   *     never been visited before;
   */
  private double averageReward(ActionTree<A, ActionData> actionTree) {
    int numVisits = actionTree.getValue().getNumVisits();
    if (numVisits == 0) {
      return 0;
    } else {
      return actionTree.getValue().getTotalRewards() / numVisits;
    }
  }

  /**
   * The bias to add to the estimated payoff of a given action when deciding
   * which action to take. This is called c_{t,s} in the original UCT paper.
   * This is the "exploration term" of the UCT equation, designed to
   * encourage exploration instead of always just picking the best node.
   *
   * @param visitsToState How many times the current state has been visited.
   * @param visitsToAction How many times the state resulting from the proposed
   *     action has been visited.
   * @return The exploration bias.
   */
  private double explorationBias(int visitsToState, int visitsToAction) {
    // We return 1000000000 if we've never visited this state or action before
    // in order to prioritize nodes we have not yet explored.
    if (visitsToState == 0 || visitsToAction == 0) {
      return 1000000000.0;
    }
    return 2.0 * explorationBias *
        Math.sqrt(Math.log(visitsToState) / visitsToAction);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("UctSearch [numSimulations=");
    builder.append(numSimulations);
    builder.append(", explorationBias=");
    builder.append(explorationBias);
    builder.append(", discountRate=");
    builder.append(discountRate);
    builder.append(", maxDepth=");
    builder.append(maxDepth);
    builder.append("]");
    return builder.toString();
  }
}
