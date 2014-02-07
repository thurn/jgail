package ca.thurn.uct.connect4;

import java.util.ArrayList;
import java.util.List;

import ca.thurn.uct.algorithm.MonteCarloSearch;
import ca.thurn.uct.core.Agent;
import ca.thurn.uct.core.Main;

public class C4Main {
  public static void main(String[] args) {
    List<Agent<C4Action>> agents = new ArrayList<Agent<C4Action>>();
//    agents.add(new C4HumanAgent(new C4State()));
    agents.add(MonteCarloSearch.builder(new C4State()).build());
    agents.add(MonteCarloSearch.builder(new C4State()).build());
    Main<C4Action> main = new Main<C4Action>(agents, new C4State());
    main.runTournament(10);
  }
}
