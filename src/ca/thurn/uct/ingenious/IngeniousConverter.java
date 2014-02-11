package ca.thurn.uct.ingenious;

import gnu.trove.list.TIntList;
import gnu.trove.list.TLongList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.list.array.TLongArrayList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

import java.util.List;
import java.util.Map;

import ca.thurn.uct.core.FastConverter;
import ca.thurn.uct.core.FastPlayer;
import ca.thurn.uct.core.FastState;
import ca.thurn.uct.core.FastStateAdapter;
import ca.thurn.uct.core.Player;
import ca.thurn.uct.core.State;

public class IngeniousConverter implements FastConverter<IngeniousAction> {

  @Override
  public long toFastAction(IngeniousAction action) {
    int piece = toFastPiece(action.getPiece());
    return FastIngeniousAction.create(piece, action.getX1(), action.getY1(), action.getX2(), action.getY2());
  }

  private int toFastPiece(IngeniousPiece piece) {
    return FastIngeniousPiece.create(toFastHex(piece.getHex1()), toFastHex(piece.getHex2()));
  }

  @Override
  public IngeniousAction fromFastAction(long action) {
    int fastPiece = FastIngeniousAction.getPiece(action);
    IngeniousPiece piece = new IngeniousPiece(fromFastHex(FastIngeniousPiece.getHex1(fastPiece)), fromFastHex(FastIngeniousPiece.getHex2(fastPiece)));
    return new IngeniousAction(piece, FastIngeniousAction.getX1(action), FastIngeniousAction.getY1(action), FastIngeniousAction.getX2(action), FastIngeniousAction.getY2(action));
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
  public FastState toFastState(State<IngeniousAction> otherState) {
    if (otherState instanceof FastStateAdapter) {
      return ((FastStateAdapter<IngeniousAction>)otherState).getUnderlyingState();
    }
    IngeniousState state = (IngeniousState)otherState;
    return new FastIngeniousState(
        adaptActions(state.actions),
        adaptBoard(state.board),
        toFastPlayer(state.currentPlayer),
        getP1Hand(state.hands),
        getP2Hand(state.hands),
        getP1Scores(state.scores),
        getP2Scores(state.scores));
  }
  
  private TLongList adaptActions(List<IngeniousAction> actions) {
    TLongList result = new TLongArrayList();
    for (IngeniousAction action : actions) {
      result.add(toFastAction(action));
    }
    return result;
  }
  
  private int[][] adaptBoard(IngeniousHex[][] board) {
    int[][] result = new int[board.length][board[0].length];
    for (int i = 0; i < board.length; ++i) {
      for (int j = 0; j < board[i].length; ++j) {
        result[i][j] = toFastHex(board[i][j]);
      }
    }
    return result;
  }
  
  private TIntList getP1Hand(Map<Player, List<IngeniousPiece>> hands) {
    TIntList result = new TIntArrayList();
    for (IngeniousPiece piece : hands.get(Player.PLAYER_ONE)) {
      result.add(toFastPiece(piece));
    }
    return result;
  }
  
  private TIntList getP2Hand(Map<Player, List<IngeniousPiece>> hands) {
    TIntList result = new TIntArrayList();
    for (IngeniousPiece piece : hands.get(Player.PLAYER_TWO)) {
      result.add(toFastPiece(piece));
    }    
    return result;
  }
  
  private TIntIntMap getP1Scores(Map<Player, Map<IngeniousHex, Integer>> scores) {
    TIntIntMap result = new TIntIntHashMap();
    for (Map.Entry<IngeniousHex, Integer> entry : scores.get(Player.PLAYER_ONE).entrySet()) {
      result.put(toFastHex(entry.getKey()), entry.getValue());
    }
    return result;
  }
  
  private TIntIntMap getP2Scores(Map<Player, Map<IngeniousHex, Integer>> scores) {
    TIntIntMap result = new TIntIntHashMap();
    for (Map.Entry<IngeniousHex, Integer> entry : scores.get(Player.PLAYER_TWO).entrySet()) {
      result.put(toFastHex(entry.getKey()), entry.getValue());
    }    
    return result;
  }
  
  private IngeniousHex fromFastHex(int hex) {
    if (hex == 0) {
      return null;
    }
    switch (hex) {
      case FastIngeniousHex.BLUE:
        return IngeniousHex.BLUE;
      case FastIngeniousHex.GREEN:
        return IngeniousHex.GREEN;
      case FastIngeniousHex.OFF_BOARD:
        return IngeniousHex.OFF_BOARD;
      case FastIngeniousHex.ORANGE:
        return IngeniousHex.ORANGE;
      case FastIngeniousHex.PURPLE:
        return IngeniousHex.PURPLE;
      case FastIngeniousHex.RED:
        return IngeniousHex.RED;
      case FastIngeniousHex.YELLOW:
        return IngeniousHex.YELLOW;
    }
    throw new RuntimeException();
  }
  
  private int toFastHex(IngeniousHex hex) {
    if (hex == null) return 0;
    switch (hex) {
      case BLUE:
        return FastIngeniousHex.BLUE;
      case GREEN:
        return FastIngeniousHex.GREEN;
      case OFF_BOARD:
        return FastIngeniousHex.OFF_BOARD;
      case ORANGE:
        return FastIngeniousHex.ORANGE;
      case PURPLE:
        return FastIngeniousHex.PURPLE;
      case RED:
        return FastIngeniousHex.RED;
      case YELLOW:
        return FastIngeniousHex.YELLOW;
    }
    throw new RuntimeException();
  }

}
