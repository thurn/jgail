package ca.thurn.uct.core;

import java.util.HashMap;
import java.util.Map;

/**
 * A class designed to associate positions in the game tree with values.
 *
 * @param <A> The action type used in this game tree.
 * @param <V> The value type to associate with a position in the tree.
 */
public class FastActionTree {
  
  private final Map<Long, FastActionTree> children;
  private int numVisits;
  private double totalReward;
  
  /**
   * Constructs a new ActionTree root node.
   *
   * @param defaultValue The initial value to associate with all newly created
   *     tree nodes.
   */
  public FastActionTree() {
    this.children = new HashMap<Long, FastActionTree>();
  }
  
  /**
   * Return the child node associated with this action, creating one if it does
   * not already exist.
   * 
   * @param action Action to retrieve the corresponding child node for.
   * @param defaultValue If the desired node doesn't exist yet, a new
   *     ActionTree will be created and initialized with this value.
   * @return The child ActionTree associated with this value.
   */
  public FastActionTree child(long action) {
    FastActionTree result = children.get(action);
    if (result == null) {
      result = new FastActionTree();
      children.put(action, result);
    }
    return result;
  }
  
  public void incrementNumVisits() {
    numVisits++;
  }
  
  public void addReward(double reward) {
    totalReward += reward;
  }
  
  public int getNumVisits() {
    return numVisits;
  }
  
  public double getTotalReward() {
    return totalReward;
  }
}
