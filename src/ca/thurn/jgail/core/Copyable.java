package ca.thurn.jgail.core;

/**
 * Represents anything that you can make a copy of.
 */
public interface Copyable {
  /**
   * @return A complete deep copy of this object. 
   */
  public Copyable copy();
}
