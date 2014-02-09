package ca.thurn.uct.connect4;

import java.util.ArrayList;
import java.util.List;

import ca.thurn.uct.algorithm.UctSearch;
import ca.thurn.uct.core.Agent;
import ca.thurn.uct.core.Main;

public class C4Main {
  public static void main(String[] args) {
    List<Agent<C4Action>> agents = new ArrayList<Agent<C4Action>>();
    agents.add(UctSearch.builder(new C4State()).setNumInitialVisits(10).build());
    agents.add(UctSearch.builder(new C4State()).setNumSimulations(10000).build());
        Main<C4Action> main = new Main<C4Action>(agents, new C4State().setToStartingConditions());
//    main.runMatch();
    main.runTournament(10);
  }
}
