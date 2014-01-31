package ca.thurn.uct.algorithm;

import java.util.Random;

import ca.thurn.uct.algorithm.State.ActionResult;

public class UctSearch<A extends Action> implements ActionPicker<A>, Evaluator<A> {

  /**
   * Number of simulations to run before picking the best action from the
   * root node.
   */
  private final int numSimulations;  
  
  /**
   * The multiplier applied to the uct bias, named C_p in the original UCT
   * paper. A higher number places more emphasis on exploration, a lower
   * number places more emphasis on exploitation.
   */
  private final double biasMultiplier;
  
  /**
   * The rate at which rewards should be discounted in the future, used to
   * compute the present value of future rewards. This way, rewards further
   * in the future are worth less. This captures our uncertainty about the
   * future, as well as helping avoid infinite reward cycles, etc.
   */
  private final double discountRate;

  /**
   * The maximum depth the search to in the simulation.
   */
  private final int maxDepth;

  private final Random random = new Random();
  
  public UctSearch() {
    // The bias value here, 1/sqrt(2), was shown by Kocsis and Szepesvari to
    // work well if rewards are in the range [0,1].
    this(100000, 0.70710678, 0.999, 50);
  }
  
  public UctSearch(int numSimulations) {
    this(numSimulations, 0.70710678, 0.999, 50);
  }
  
  public UctSearch(int numSimulations, double biasMultiplier) {
    this(numSimulations, biasMultiplier, 0.999, 50);
  }
  
  public UctSearch(int numSimulations, double biasMultiplier, double discountRate) {
    this(numSimulations, biasMultiplier, discountRate, 50);
  }  
  
  public UctSearch(int numSimulations, double biasMultiplier, double discountRate, int maxDepth) {
    this.biasMultiplier = biasMultiplier;
    this.numSimulations = numSimulations;
    this.discountRate = discountRate;
    this.maxDepth = maxDepth;
  }
  
  /**
   * Picks an action to take from the provided root node. 
   */
  public A pickAction(Player player, State<A> root) {
    root.prepareForSimulation();
    for (int i = 0; i < numSimulations; ++i) {
      runSimulation(player, root, 0);
      root.reset();
    }
    // Return action with highest estimated payoff
    double bestPayoff = Double.NEGATIVE_INFINITY;
    A bestAction = null;
    for (A action : root.getActions()) {
      double estimatedPayoff = root.averagePayoff(action);
      if (estimatedPayoff > bestPayoff) {
        bestPayoff = estimatedPayoff;
        bestAction = action;
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
      return evaluate(player, state);
    }
    A action = uctSelectAction(state);
    ActionResult<A> result = state.perform(player, action);
    double reward = result.getReward() + (discountRate *
        runSimulation(player, result.getNextState(), depth + 1));
    state.addReward(action, reward);
    return reward;
  }

  /**
   * Selects an action to take from the provided state via the UCT
   * algorithm.
   */
  A uctSelectAction(State<A> state) {
    // We iterate through each action and return the one that maximizes
    // uctValue.
    double maximum = Double.NEGATIVE_INFINITY;
    A result = null;
    for (A action : state.getActions()) {
      double uctValue = state.averagePayoff(action) +
          uctBias(state.numberOfTimesVisited(),
              state.numberOfTimesActionSelected(action));
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
   * The bias to add to the estimated payoff of a given action when deciding
   * which action to take. This is called c_{t,s} in the original UCT paper.
   * This is the "exploration term" of the UCT equation, designed to
   * encourage exploration instead of always just picking the best node.
   */
  double uctBias(int visitsToState, int visitsToAction) {
    // We return 1000000000 if we've never visited this state or action before
    // in order to prioritize nodes we have not yet explored.
    if (visitsToState == 0 || visitsToAction == 0) return 1000000000.0;
    return 2.0 * biasMultiplier *
        Math.sqrt(Math.log(visitsToState) / visitsToAction);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("UctSearch [biasMultiplier=");
    builder.append(biasMultiplier);
    builder.append(", numSimulations=");
    builder.append(numSimulations);
    builder.append(", discountRate=");
    builder.append(discountRate);
    builder.append(", maxDepth=");
    builder.append(maxDepth);
    builder.append("]");
    return builder.toString();
  }

  public double evaluate(Player player, State<A> state) {
    return state.getWinner() == player ? 1.0 : 0.0;
  }

}
