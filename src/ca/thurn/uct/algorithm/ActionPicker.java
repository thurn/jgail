package ca.thurn.uct.algorithm;

public interface ActionPicker<A extends Action, S extends State<A, S>> {
  /**
   * Picks an action to take from the provided root node. 
   */
  public A pickAction(S rootNode);
}
