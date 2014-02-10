package ca.thurn.uct.core;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;

/**
 * A class designed to associate positions in the game tree with values.
 *
 * @param <A> The action type used in this game tree.
 * @param <V> The value type to associate with a position in the tree.
 */
public class FastActionTree {
  
  /**
   * Interface to allow mutating values in the tree. 
   */
  public static interface Mutator {
    /**
     * Mutates the provided tree as desired, writing the new value to the tree.
     *
     * @param tree The current value in the tree.
     * @return The new value to store in this position in the tree.
     */
    public long mutate(long value);
  }
  
  private final TLongObjectMap<FastActionTree> children;
  private long value;
  
  /**
   * Constructs a new ActionTree root node.
   *
   * @param defaultValue The initial value to associate with all newly created
   *     tree nodes.
   */
  public FastActionTree() {
    this.children = new TLongObjectHashMap<FastActionTree>();
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
  
  /**
   * Mutate the value at the current position in the tree.
   *
   * @param mutator Mutator to employ.
   */
  public void mutate(Mutator mutator) {
    this.value = mutator.mutate(value);
  }

  /**
   * @return The value currently stored at this position in the tree. 
   */
  public long getValue() {
    return value;
  }
}
