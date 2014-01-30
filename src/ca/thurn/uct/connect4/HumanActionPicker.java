package ca.thurn.uct.connect4;

import java.util.Scanner;

import ca.thurn.uct.algorithm.ActionPicker;
import ca.thurn.uct.algorithm.Player;
import ca.thurn.uct.algorithm.State;

public class HumanActionPicker implements ActionPicker<Connect4Action> {

  private Scanner in = new Scanner(System.in);
  
  @Override
  public Connect4Action pickAction(Player player, State<Connect4Action> rootNode) {
    System.out.println("Select a column [0,6]");
    int column = in.nextInt();
    return new Connect4Action(player, column);
  }

}
