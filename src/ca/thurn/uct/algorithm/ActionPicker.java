package ca.thurn.uct.algorithm;

public interface ActionPicker<A extends Action> {
  /**
   * Picks an action for the provided player to take from the provided root
   * node. 
   */
  public A pickAction(Player player, State<A> rootNode);
}
