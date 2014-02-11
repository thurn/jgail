package ca.thurn.uct.core;

public class FastAgentAdapter<A extends Action> implements Agent<A> {
  
  private final FastAgent agent;
  private final FastConverter<A> converter;
  
  public FastAgentAdapter(FastAgent agent, FastConverter<A> converter) {
    this.agent = agent;
    this.converter = converter;
  }

  @Override
  public ActionScore<A> pickAction(Player player, State<A> rootNode) {
    A action = converter.fromFastAction(agent.pickAction(converter.toFastPlayer(player), converter.toFastState(rootNode)));
    return new ActionScore<A>(agent.getScoreForLastAction(), action);
  }

  @Override
  public State<A> getStateRepresentation() {
    return new FastStateAdapter<A>(agent.getStateRepresentation(), converter);
  }

}
