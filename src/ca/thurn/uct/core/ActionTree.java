package ca.thurn.uct.core;

import java.util.HashMap;
import java.util.Map;

/**
 * A class designed to associate positions in the game tree with values.
 *
 * @param <A> The action type used in this game tree.
 * @param <V> The value type to associate with a position in the tree.
 */
public class ActionTree<A extends Action, V> {
  
  /**
   * Interface to allow mutating values in the tree. 
   */
  public static interface Mutator<V> {
    /**
     * Mutates the provided tree as desired, writing the new value to the tree.
     *
     * @param tree The current value in the tree.
     * @return The new value to store in this position in the tree.
     */
    public V mutate(V value);
  }
  
  private final Map<A, ActionTree<A, V>> children;
  private final V defaultValue;
  private V value;
  
  /**
   * Constructs a new ActionTree root node.
   *
   * @param defaultValue The initial value to associate with all newly created
   *     tree nodes.
   */
  public ActionTree(V defaultValue) {
    this.children = new HashMap<A, ActionTree<A, V>>();
    this.defaultValue = defaultValue;
    this.value = defaultValue;
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
  public ActionTree<A, V> child(A action) {
    ActionTree<A, V> result = children.get(action);
    if (result == null) {
      result = new ActionTree<A, V>(defaultValue);
      children.put(action, result);
    }
    return result;
  }
  
  /**
   * Mutate the value at the current position in the tree.
   *
   * @param mutator Mutator to employ.
   */
  public void mutate(Mutator<V> mutator) {
    this.value = mutator.mutate(value);
  }

  /**
   * @return The value currently stored at this position in the tree. 
   */
  public V getValue() {
    return value;
  }
}
