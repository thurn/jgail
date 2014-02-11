package ca.thurn.uct.connect4;

import java.util.ArrayList;
import java.util.List;

import ca.thurn.uct.algorithm.NegamaxSearch;
import ca.thurn.uct.core.Agent;
import ca.thurn.uct.core.Main;

public class C4Main {
  public static void main(String[] args) {
//    List<Agent<C4Action>> agents = new ArrayList<Agent<C4Action>>();
//    agents.add(NegamaxSearch.builder(new C4State()).build());
//    agents.add(NegamaxSearch.builder(new C4State()).build());
//    FastNegamaxSearch fnm = FastNegamaxSearch.builder(new FastC4State()).build();
//    agents.add(new FastAgentAdapter<C4Action>(fnm, new C4Converter()));
//    Main<C4Action> main = new Main<C4Action>(agents, new C4State().setToStartingConditions());
//    main.runTournament(10);

    List<Agent> fastAgents = new ArrayList<Agent>();
    fastAgents.add(NegamaxSearch.builder(new C4State()).build());
    fastAgents.add(NegamaxSearch.builder(new C4State()).build());
    Main fastMain = new Main(fastAgents, new C4State().setToStartingConditions());
    fastMain.runTournament(10);
  }
}
