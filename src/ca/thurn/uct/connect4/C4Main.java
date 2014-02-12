package ca.thurn.uct.connect4;

import java.util.ArrayList;
import java.util.List;

import ca.thurn.uct.algorithm.NegamaxSearch;
import ca.thurn.uct.algorithm.UctSearch;
import ca.thurn.uct.core.Agent;
import ca.thurn.uct.core.Main;

public class C4Main {
  public static void main(String[] args) {
    List<Agent> agents = new ArrayList<Agent>();
    agents.add(NegamaxSearch.builder(new C4State()).build());
    agents.add(UctSearch.builder(new C4State()).setNumSimulations(100000).build());
    Main main = new Main(agents, new C4State().setToStartingConditions());
    main.runTournament(250);
  }
}
