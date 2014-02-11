package ca.thurn.uct.connect4;

import java.util.ArrayList;
import java.util.List;

import ca.thurn.uct.algorithm.FastNegamaxSearch;
import ca.thurn.uct.core.FastAgent;
import ca.thurn.uct.core.FastMain;

public class C4Main {
  public static void main(String[] args) {
//    List<Agent<C4Action>> agents = new ArrayList<Agent<C4Action>>();
//    agents.add(NegamaxSearch.builder(new C4State()).build());
//    agents.add(NegamaxSearch.builder(new C4State()).build());
//    FastNegamaxSearch fnm = FastNegamaxSearch.builder(new FastC4State()).build();
//    agents.add(new FastAgentAdapter<C4Action>(fnm, new C4Converter()));
//    Main<C4Action> main = new Main<C4Action>(agents, new C4State().setToStartingConditions());
//    main.runTournament(10);

    List<FastAgent> fastAgents = new ArrayList<FastAgent>();
    fastAgents.add(FastNegamaxSearch.builder(new FastC4State()).build());
    fastAgents.add(FastNegamaxSearch.builder(new FastC4State()).build());
    FastMain fastMain = new FastMain(fastAgents, new FastC4State().setToStartingConditions());
    fastMain.runTournament(10);
  }
}
