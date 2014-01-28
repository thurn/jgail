package ca.thurn.uct.connect4;

import java.util.Scanner;

import ca.thurn.uct.algorithm.ActionPicker;

public class HumanActionPicker implements ActionPicker<Connect4Action, Connect4State> {

  private Scanner in = new Scanner(System.in);
  
  @Override
  public Connect4Action pickAction(Connect4State rootNode) {
    System.out.println("Select a column [0,6]");
    int column = in.nextInt();
    return new Connect4Action(rootNode.getCurrentPlayer(), column);    
  }

}
