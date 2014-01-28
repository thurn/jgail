package ca.thurn.uct.connect4;

import java.util.HashMap;
import java.util.Map;

import ca.thurn.uct.algorithm.ActionPicker;
import ca.thurn.uct.algorithm.State;
import ca.thurn.uct.algorithm.UctSearch;
import ca.thurn.uct.connect4.Connect4Action.Player;

public class Connect4Main {

    private static Connect4State gameState;
    private static Map<Player, ActionPicker<Connect4Action, Connect4State>> actionPickers;
    private static boolean DEBUG = false; 

	public static void main(String[] args) {
	  System.out.println("Welcome to Connect 4");
	  gameState = new Connect4State();
	  actionPickers = new HashMap<Player, ActionPicker<Connect4Action, Connect4State>>();
	  actionPickers.put(Player.BLACK, new HumanActionPicker());
	  actionPickers.put(Player.RED, new UctSearch<Connect4Action, Connect4State>());
	  while (!gameState.isTerminal()) {
	    gameState.printState();
	    ActionPicker<Connect4Action, Connect4State> actionPicker = actionPickers.get(gameState.getCurrentPlayer());
        Connect4Action action = actionPicker.pickAction(gameState);
	    if (DEBUG && actionPicker instanceof UctSearch) {
	      for (Connect4Action child : gameState.getActions()) {
	        double payoff = gameState.averagePayoff(child);
	        System.out.print(child.getColumnNumber() + "=" + String.format("%.4g", payoff) + " ");
	      }
	      System.out.print("\n");
	    }
	    State<Connect4Action, Connect4State>.ActionResult actionResult = gameState.perform(action);
	    gameState = actionResult.getNextState();
	  }
	  gameState.printState();
	  Player winner = gameState.getWinner();
	  System.out.println("Player " + (winner == Player.RED ? "X" : "O") + " wins!");
	}

}
