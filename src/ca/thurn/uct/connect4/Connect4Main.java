package ca.thurn.uct.connect4;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ca.thurn.uct.algorithm.ActionPicker;
import ca.thurn.uct.algorithm.NegamaxSearch;
import ca.thurn.uct.algorithm.Player;
import ca.thurn.uct.algorithm.State;
import ca.thurn.uct.algorithm.UctSearch;

/**
 * 
 * Experimental results for the Connect4 domain:
 * - Discounting appears to be of little benefit for this domain. Large
 *   discounts (rates < 0.99) adversely affect performance.
 * - The UCT exploration bias also appears to have little impact as long as
 *   it falls roughly in the range [0.1, 1.0].
 * - For negamax, a search depth of 4 with about 7^3 simulations in the UCT
 *   evaluation function was the most successful.
 */
public class Connect4Main {

  private State<Connect4Action> gameState;
  
  private Random random = new Random();
  
  private static final int TOURNAMENT_SIZE = 10;
  
  private static enum RunMode {
    AI_VS_AI,
    HUMAN_VS_AI,
    TOURNAMENT
  }
  
  /**
   * Change this constant to run the program in different modes.
   */
  private static final RunMode runMode = RunMode.TOURNAMENT;
  private static long startTime;

  public static void main(String[] args) {
    System.out.println("Welcome to Connect 4");
    Connect4Main main = new Connect4Main();
    switch(runMode) {
      case AI_VS_AI:
        main.playGame(aiVsAi());
        break;
      case HUMAN_VS_AI:
        Output.getInstance().setIsInteractive(true);
        main.playGame(humanVsAi());
        break;
      case TOURNAMENT:
        main.runTournament();
        break;
    }
  }
  
  private static Map<Connect4Player, ActionPicker<Connect4Action>> aiVsAi() {
    Map<Connect4Player, ActionPicker<Connect4Action>> actionPickers =
        new HashMap<Connect4Player, ActionPicker<Connect4Action>>();
    actionPickers.put(Connect4Player.BLACK, new UctSearch<Connect4Action>());
    actionPickers.put(Connect4Player.RED, new UctSearch<Connect4Action>());
    return actionPickers;
  }  
  
  private static Map<Connect4Player, ActionPicker<Connect4Action>> humanVsAi() {
    Map<Connect4Player, ActionPicker<Connect4Action>> actionPickers =
        new HashMap<Connect4Player, ActionPicker<Connect4Action>>();
    actionPickers.put(Connect4Player.BLACK, new HumanActionPicker());
    actionPickers.put(Connect4Player.RED, new NegamaxSearch<Connect4Action>());
    return actionPickers;
  }
  
  private void runTournament() {
    startTime = System.currentTimeMillis();    
    List<ActionPicker<Connect4Action>> pickerList =
        new ArrayList<ActionPicker<Connect4Action>>();
    pickerList.add(new UctSearch<Connect4Action>());
    pickerList.add(new NegamaxSearch<Connect4Action>());    
    int[] wins = new int[pickerList.size()];

    for (int i = 0; i < TOURNAMENT_SIZE; ++i) {
      Map<Connect4Player, ActionPicker<Connect4Action>> actionPickers =
          new HashMap<Connect4Player, ActionPicker<Connect4Action>>();
      int black = random.nextInt(pickerList.size());
      int red = random.nextInt(pickerList.size());
      while (red == black) {
        red = random.nextInt(pickerList.size());
      }
      actionPickers.put(Connect4Player.BLACK, pickerList.get(black));
      actionPickers.put(Connect4Player.RED, pickerList.get(red));
      Player winner = playGame(actionPickers);
      System.out.print(".");
      if (winner == Connect4Player.BLACK) {
        wins[black]++;
      } else if (winner == Connect4Player.RED){
        wins[red]++;
      }
      
      if (i >= 10 && i % (TOURNAMENT_SIZE / 10) == 0) {
        // Print intermediate results
        printTournamentResults(wins);        
      }
    }
    
    printTournamentResults(wins);
    
    long duration = System.currentTimeMillis() - startTime;
    String elapsed = new SimpleDateFormat("hh:mm:ss").format(new Date(duration));
    String perTournament = new SimpleDateFormat("mm:ss").format(new Date(duration / TOURNAMENT_SIZE));
    System.out.println("Tournament finished in " + elapsed + " (" + perTournament + 
        " per tournament)");    
  }

  private void printTournamentResults(int[] wins) {
    System.out.println("===== Tournament Results ======");
    for (int i = 0; i < wins.length; ++i) {
      System.out.println("Player #" + i + ": " + wins[i] + " wins");      
    }
  }

  private Player playGame(
      Map<Connect4Player, ActionPicker<Connect4Action>> actionPickers) {
    gameState = new Connect4State();
    while (!gameState.isTerminal()) {
      if (Output.getInstance().isInteractive()) System.out.println(gameState);
      ActionPicker<Connect4Action> actionPicker = actionPickers.get(gameState.getCurrentPlayer());
      Connect4Action action =
          actionPicker.pickAction(gameState.getCurrentPlayer(), gameState.copy());
      gameState = gameState.perform(action);
    }
    if (Output.getInstance().isInteractive()) System.out.println(gameState);
    Player winner = gameState.getWinner();
    if (Output.getInstance().isInteractive()) {
      if (winner == null) {
        System.out.println("Game drawn.");
      } else {
        System.out.println("Player " + (winner == Connect4Player.RED ? "X" : "O") + " wins!");
      }
    }
    return winner;
  }

}
