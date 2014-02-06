package ca.thurn.uct.connect4;

import java.util.Scanner;

import ca.thurn.uct.algorithm.ActionPicker;
import ca.thurn.uct.algorithm.State;
import ca.thurn.uct.core.Player;

public class Connect4HumanActionPicker implements ActionPicker<C4Action> {

  private Scanner in = new Scanner(System.in);
  
  @Override
  public C4Action pickAction(Player player, State<C4Action> rootNode) {
    System.out.println("Select a column [0,6]");
    int column = in.nextInt();
    return new C4Action(player, column);
  }

}
