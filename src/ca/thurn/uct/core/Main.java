package ca.thurn.uct.core;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * A helper class for running games & sets of games between multiple Agents.
 */
public class Main {
  private final List<Agent> agents;
  private final State initialState;
  private final Random random = new Random();
  private State canonicalState;  

  /**
   * Constructs a new Main instance.
   *
   * @param agents A list of agents who will participate in the game(s).
   * @param canonicalState The state to use as the canonical game state. It is
   *     responsible for keeping track of actual actions in the game and is not
   *     directly given to any agent to make their action determination. It is
   *     the responsibility of the caller to ensure that this state is in the
   *     appropriate initial state for this game.
   */
  public Main(List<Agent> agents, State canonicalState) {
    this.agents = agents;
    this.initialState = canonicalState;
  }
  
  /**
   * Run a series of matches between the agents, selected at random, and then
   * report the results.
   *
   * @param tournamentSize The number of matches to run.
   */
  public void runTournament(int tournamentSize) {
    long startTime = System.currentTimeMillis();
    int[] wins = new int[agents.size()];
    int draws = 0;

    for (int i = 0; i < tournamentSize; ++i) {
      Map<Integer, Agent> agentMap = new HashMap<Integer, Agent>();
      int black = random.nextInt(agents.size());
      int red = random.nextInt(agents.size());
      while (red == black) {
        red = random.nextInt(agents.size());
      }
      agentMap.put(Player.PLAYER_ONE, agents.get(black));
      agentMap.put(Player.PLAYER_TWO, agents.get(red));
      int winner = playGame(agentMap, false /* isInteractive */);
      System.out.print(".");
      if (winner == Player.PLAYER_ONE) {
        wins[black]++;
      } else if (winner == Player.PLAYER_TWO) {
        wins[red]++;
      } else if (winner == 0) {
        draws++;
      }
      
      if (i >= 10 && i % (tournamentSize / 10) == 0) {
        // Print intermediate results
        printTournamentResults(wins, draws);        
      }
    }
    
    printTournamentResults(wins, draws);
    
    long duration = System.currentTimeMillis() - startTime;
    String elapsed = new SimpleDateFormat("mm:ss").format(new Date(duration));
    String perTournament = new SimpleDateFormat("mm:ss").format(new Date(duration / tournamentSize));
    System.out.println("Tournament finished in " + elapsed + " (" + perTournament + 
        " per tournament)");    
  }
  
  /**
   * Run a single match between the first two provided agents, printing out the
   * current game state between each move.
   */
  public void runMatch() {
    Map<Integer, Agent> agentMap = new HashMap<Integer, Agent>();
    agentMap.put(Player.PLAYER_ONE, agents.get(0));
    agentMap.put(Player.PLAYER_TWO, agents.get(1));
    int winner = playGame(agentMap, true /* isInteractive */);
    if (winner != 0) {
      System.out.println(agentMap.get(winner) + " wins!");
    } else {
      System.out.println("Game drawn.");
    }
  }
  
  /**
   * Play a match between the supplied agents.
   *
   * @param agentMap A mapping from players in the game to the agents who will
   *     represent them.
   * @param isInteractive If true, print out intermediate game state
   *     information.
   * @return The winner of the game as defined by the canonical state's
   *     {@link State#getWinner()} method.
   */
  private int playGame(Map<Integer, Agent> agentMap, boolean isInteractive) {
    canonicalState = initialState.copy();
    while (!canonicalState.isTerminal()) {
      if (isInteractive) {
        System.out.println(canonicalState);
      }
      Agent agent = agentMap.get(canonicalState.getCurrentPlayer());
      long action = agent.pickAction(canonicalState.getCurrentPlayer(),
          agent.getStateRepresentation().initialize(canonicalState),
          0L).getAction();
      if (isInteractive) {
        System.out.println(agent + " picked action " + canonicalState.actionToString(action));
      }
      canonicalState.perform(action);
    }
    if (isInteractive) {
      System.out.println(canonicalState);
    }
    return canonicalState.getWinner();
  }
  
  /**
   * Prints out the results of a tournament.
   *
   * @param wins Array counting wins for each player number.
   * @param draws Number of draws in the tournament.
   */
  private void printTournamentResults(int[] wins, int draws) {
    System.out.println("===== Tournament Results ======");
    for (int i = 0; i < wins.length; ++i) {
      System.out.println("Player #" + i + ": " + wins[i] + " wins");      
    }
    System.out.println(draws + " draws");
  }
  
}
