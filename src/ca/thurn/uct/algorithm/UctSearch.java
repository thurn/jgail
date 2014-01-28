package ca.thurn.uct.algorithm;

import java.util.Random;

public class UctSearch<A extends Action, S extends State<A,S>> implements ActionPicker<A, S> {

  /**
   * The multiplier applied to the uct bias, named C_p in the original UCT
   * paper. A higher number places more emphasis on exploration, a lower
   * number places more emphasis on exploitation. This value, 1/sqrt(2), was
   * shown by Kocsis and Szepesvari to work well if rewards are in the range
   * [0,1].
   */
  private static final double BIAS_MULTIPLER = 0.70710678;

  /**
   * The rate at which rewards should be discounted in the future, used to
   * compute the present value of future rewards. This way, rewards further
   * in the future are worth less. This captures our uncertainty about the
   * future, as well as helping avoid infinite reward cycles, etc.
   */
  private static final double DISCOUNT_RATE = 0.99;

  /**
   * The maximum depth the search to in the simulation.
   */
  private static final int MAX_DEPTH = 50;

  /**
   * Number of simulations to run before picking the best action from the
   * root node.
   */
  private static final int NUM_SIMULATIONS = 100000;

  private final Random random = new Random();
  
  /**
   * Picks an action to take from the provided root node. 
   */
  public A pickAction(S root) {
    for (int i = 0; i < NUM_SIMULATIONS; ++i) {
      runSimulation(root, 0);
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
    System.out.println(">>> UctSearch plays action " + bestAction);
    return bestAction;
  }

  
  /**
   * Runs a simulation to determine the total payoff associated with being at
   * the provided state, tracking the depth in the search tree in "depth".  
   */
  double runSimulation(S state, int depth) {
    if (depth > MAX_DEPTH || state.isTerminal()) {
      return state.evaluate();
    }
    A action = uctSelectAction(state);
    State<A,S>.ActionResult result = state.perform(action);
    double reward = result.getReward() + (DISCOUNT_RATE *
        runSimulation(result.getNextState(), depth + 1));
    state.addReward(action, reward);
    return reward;
  }

  /**
   * Selects an action to take from the provided state via the UCT
   * algorithm.
   */
  A uctSelectAction(S state) {
    // We iterate through each action and return the one that maximizes
    // uctValue.
    double maximum = 0.0;
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
    return 2.0 * BIAS_MULTIPLER *
        Math.sqrt(Math.log(visitsToState) / visitsToAction);
  }

}
