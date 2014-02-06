package ca.thurn.uct.connect4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.thurn.uct.algorithm.ActionPicker;
import ca.thurn.uct.algorithm.CopyOfMonteCarloSearch;
import ca.thurn.uct.algorithm.InitialStateGenerator;
import ca.thurn.uct.algorithm.Main;
import ca.thurn.uct.algorithm.MonteCarloSearch;
import ca.thurn.uct.algorithm.State;
import ca.thurn.uct.algorithm.StateInitializer;
import ca.thurn.uct.algorithm.UctSearch;

public class Connect4Main {
  private static class Connect4InitialStateGenerator
      implements InitialStateGenerator<C4Action> {
    @Override
    public State<C4Action> initialState() {
      State<C4Action> state = new Connect4State();
      return state;
    }
  }
  
  private static class Connect4StateInitializer implements StateInitializer<C4Action> {
    @Override
    public State<C4Action> initializeFromState(State<C4Action> state) {
      return new Connect4State((Connect4State) state);
    }
  }
  
  public static void main(String[] args) {
    List<ActionPicker<C4Action>> actionPickers =
        new ArrayList<ActionPicker<C4Action>>();
    Connect4HumanActionPicker hap = new Connect4HumanActionPicker();
//    UctSearch<Connect4Action> search1 = UctSearch.<Connect4Action>builder()
//        .build();
    CopyOfMonteCarloSearch<C4Action> search2 = CopyOfMonteCarloSearch.<C4Action>builder()
        .build();
    actionPickers.add(hap);
//    actionPickers.add(search1);
    actionPickers.add(search2);
    Map<ActionPicker<C4Action>, StateInitializer<C4Action>> initializers =
        new HashMap<ActionPicker<C4Action>, StateInitializer<C4Action>>();
    initializers.put(hap, new Connect4StateInitializer());
//    initializers.put(search1, new Connect4StateInitializer());
    initializers.put(search2, new Connect4StateInitializer());
    new Main<C4Action>(new Connect4InitialStateGenerator(),
        actionPickers,
        initializers).run();
  }
        
}
