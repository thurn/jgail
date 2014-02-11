package ca.thurn.uct.connect4;

import java.util.ArrayList;
import java.util.List;

import ca.thurn.uct.algorithm.FastUctSearch;
import ca.thurn.uct.algorithm.UctSearch;
import ca.thurn.uct.core.Agent;
import ca.thurn.uct.core.FastAgent;
import ca.thurn.uct.core.FastMain;
import ca.thurn.uct.core.Main;

public class C4Main {
  public static void main(String[] args) {
    List<Agent<C4Action>> agents = new ArrayList<Agent<C4Action>>();
    agents.add(UctSearch.builder(new C4State()).build());
    agents.add(UctSearch.builder(new C4State()).build());
    Main<C4Action> main = new Main<C4Action>(agents, new C4State().setToStartingConditions());
    main.runTournament(10);
    
//    List<FastAgent> fastAgents = new ArrayList<FastAgent>();
//    fastAgents.add(FastUctSearch.builder(new FastC4State()).build());
//    fastAgents.add(FastUctSearch.builder(new FastC4State()).build());
//    FastMain fastMain = new FastMain(fastAgents, new FastC4State().setToStartingConditions());
//    fastMain.runTournament(10);
    
  }
}
