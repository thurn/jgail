package ca.thurn.uct.core;

import java.util.HashMap;
import java.util.Map;

/**
 * A class to track the number of visits to and rewards associated with given
 * game tree nodes.
 */
public class ActionTree {
  
  private final Map<Long, ActionTree> children;
  private int numVisits;
  private double totalReward;
  
  /**
   * Constructs a new ActionTree root node.
   */
  public ActionTree() {
    this.children = new HashMap<Long, ActionTree>();
  }
  
  /**
   * Return the child node associated with this action, creating one if it does
   * not already exist.
   * 
   * @param action Action to retrieve the corresponding child node for.
   * @return The child ActionTree associated with this value.
   */
  public ActionTree child(long action) {
    ActionTree result = children.get(action);
    if (result == null) {
      result = new ActionTree();
      children.put(action, result);
    }
    return result;
  }
  
  /**
   * Increases numVisits by 1.
   */
  public void incrementNumVisits() {
    numVisits++;
  }
  
  /**
   * Adds the provided reward to the stored totalReward.
   * 
   * @param reward Reward to add.
   */
  public void addReward(double reward) {
    totalReward += reward;
  }
  
  /**
   * @return Total number of visits recorded to this game tree node.
   */
  public int getNumVisits() {
    return numVisits;
  }
  
  /**
   * @return Total reward associated with this game tree node.
   */
  public double getTotalReward() {
    return totalReward;
  }
}
