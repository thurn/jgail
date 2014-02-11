package ca.thurn.uct.connect4;

import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;

import java.util.List;

import ca.thurn.uct.core.FastConverter;
import ca.thurn.uct.core.FastPlayer;
import ca.thurn.uct.core.FastState;
import ca.thurn.uct.core.FastStateAdapter;
import ca.thurn.uct.core.Player;
import ca.thurn.uct.core.State;

public class C4Converter implements FastConverter<C4Action> {

  @Override
  public long toFastAction(C4Action action) {
    return FastC4Action.create(toFastPlayer(action.getPlayer()), action.getColumnNumber());
  }

  @Override
  public C4Action fromFastAction(long action) {
    return new C4Action(fromFastPlayer(FastC4Action.getPlayer(action)), FastC4Action.getColumnNumber(action));
  }

  @Override
  public int toFastPlayer(Player player) {
    if (player == null) return 0;
    switch (player) {
      case PLAYER_ONE:
        return FastPlayer.PLAYER_ONE;
      case PLAYER_TWO:
        return FastPlayer.PLAYER_TWO;
    }
    throw new RuntimeException();
  }

  @Override
  public Player fromFastPlayer(int player) {
    switch (player) {
      case FastPlayer.PLAYER_ONE:
        return Player.PLAYER_ONE;
      case FastPlayer.PLAYER_TWO:
        return Player.PLAYER_TWO;
      case 0:
        return null;
    }
    throw new RuntimeException();
  }
  @Override
  public FastState toFastState(State<C4Action> otherState) {
    if (otherState instanceof FastStateAdapter) {
      return ((FastStateAdapter<C4Action>)otherState).getUnderlyingState();
    }
    C4State state = (C4State)otherState;
    return new FastC4State(
        adaptBoard(state.board),
        adaptActions(state.actions),
        toFastPlayer(state.currentPlayer),
        toFastPlayer(state.winner));
  }
  
  private int[][] adaptBoard(Player[][] board) {
    int[][] result = new int[board.length][board[0].length];
    for (int i = 0; i < board.length; ++i) {
      for (int j = 0; j < board[i].length; ++j) {
        result[i][j] = toFastPlayer(board[i][j]);
      }
    }
    return result;
  }
  
  private TLongList adaptActions(List<C4Action> actions) {
    TLongList result = new TLongArrayList();
    for (C4Action action : actions) {
      result.add(toFastAction(action));
    }
    return result;
  }

}
