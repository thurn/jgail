package ca.thurn.uct.algorithm;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
public class Main<A extends Action> {

  private State<A> gameState;
  
  private Random random = new Random();
  
  private static final int TOURNAMENT_SIZE = 10;
  
  public static enum RunMode {
    VERSUS,
    TOURNAMENT
  }
  
  /**
   * Change this constant to run the program in different modes.
   */
  private long startTime;
  private final InitialStateGenerator<A> initialStateGenerator;
  private final List<ActionPicker<A>> actionPickers;

  public Main(RunMode runMode, InitialStateGenerator<A> initialStateGenerator,
      List<ActionPicker<A>> actionPickers,
      Map<ActionPicker<A>, StateInitializer<A>> initializers) {
    this.initialStateGenerator = initialStateGenerator;
    this.actionPickers = actionPickers;
  
    switch(runMode) {
      case VERSUS:
        Output.getInstance().setIsInteractive(true);
        Map<Player, ActionPicker<A>> pickerMap =
            new HashMap<Player, ActionPicker<A>>();
        pickerMap.put(Player.PLAYER_ONE, actionPickers.get(0));
        pickerMap.put(Player.PLAYER_TWO, actionPickers.get(1));
        playGame(pickerMap, initializers);
        break;
      case TOURNAMENT:
        runTournament(initializers);
        break;
    }
  }
  
  private void runTournament(
      Map<ActionPicker<A>, StateInitializer<A>> initializers) {
    startTime = System.currentTimeMillis();
    int[] wins = new int[actionPickers.size()];
    int draws = 0;

    for (int i = 0; i < TOURNAMENT_SIZE; ++i) {
      Map<Player, ActionPicker<A>> pickerMap =
          new HashMap<Player, ActionPicker<A>>();
      int black = random.nextInt(actionPickers.size());
      int red = random.nextInt(actionPickers.size());
      while (red == black) {
        red = random.nextInt(actionPickers.size());
      }
      pickerMap.put(Player.PLAYER_ONE, actionPickers.get(black));
      pickerMap.put(Player.PLAYER_TWO, actionPickers.get(red));
      Player winner = playGame(pickerMap, initializers);
      System.out.print(".");
      if (winner == Player.PLAYER_ONE) {
        wins[black]++;
      } else if (winner == Player.PLAYER_TWO) {
        wins[red]++;
      } else if (winner == null) {
        draws++;
      }
      
      if (i >= 10 && i % (TOURNAMENT_SIZE / 10) == 0) {
        // Print intermediate results
        printTournamentResults(wins, draws);        
      }
    }
    
    printTournamentResults(wins, draws);
    
    long duration = System.currentTimeMillis() - startTime;
    String elapsed = new SimpleDateFormat("mm:ss").format(new Date(duration));
    String perTournament = new SimpleDateFormat("mm:ss").format(new Date(duration / TOURNAMENT_SIZE));
    System.out.println("Tournament finished in " + elapsed + " (" + perTournament + 
        " per tournament)");    
  }

  private void printTournamentResults(int[] wins, int draws) {
    System.out.println("===== Tournament Results ======");
    for (int i = 0; i < wins.length; ++i) {
      System.out.println("Player #" + i + ": " + wins[i] + " wins");      
    }
    System.out.println(draws + " draws");
  }

  private Player playGame(Map<Player, ActionPicker<A>> pickerMap,
      Map<ActionPicker<A>, StateInitializer<A>> initializers) {
    gameState = initialStateGenerator.initialState();
    while (!gameState.isTerminal()) {
      if (Output.getInstance().isInteractive()) System.out.println(gameState);
      ActionPicker<A> actionPicker = pickerMap.get(gameState.getCurrentPlayer());
      StateInitializer<A> initializer = initializers.get(actionPicker);
      A action =
          actionPicker.pickAction(gameState.getCurrentPlayer(),
              initializer.initializeFromState(gameState.copy()));
      if (Output.getInstance().isInteractive()) {
        System.out.println(actionPicker + " Picked action " + action);
      }
      gameState = gameState.perform(action);
    }
    if (Output.getInstance().isInteractive()) System.out.println(gameState);
    Player winner = gameState.getWinner();
    if (Output.getInstance().isInteractive()) {
      if (winner == null) {
        System.out.println("Game drawn.");
      } else {
        System.out.println("Player " + (winner == Player.PLAYER_TWO ? "X" : "O") + " wins!");
      }
    }
    return winner;
  }

}
