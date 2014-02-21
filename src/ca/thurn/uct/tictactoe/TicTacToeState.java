package ca.thurn.uct.tictactoe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ca.thurn.uct.core.Player;
import ca.thurn.uct.core.State;

/**
 * State for a game of Tic Tac Toe.
 */
public class TicTacToeState implements State {
  // All possible winning lines for the players:
  private static final int[] WINNING_X_LINES = {
    0x1C0, 0x038, 0x007, // Horizontal
    0x124, 0x092, 0x049, // Vertical
    0x111, 0x054 // Diagonal
  };
  private static final int[] WINNING_O_LINES = new int[8];
  static {
    for (int i = 0; i < WINNING_X_LINES.length; ++i) {
      WINNING_O_LINES[i] = WINNING_X_LINES[i] << 12;
    }
  }
  
  // Bit structure:
  // 0000A AAAA AAAA 000B BBBB BBBB
  // 00001 2345 6789 0001 2345 6789
  // A is set if there is an X in board positions 1 through 9
  // B is set if there is an O in board positions 1 through 9
  // 1  2  3
  // 4  5  6
  // 7  8  9  
  private int board;
  // X is always PLAYER_ONE, O is always PLAYER_TWO.
  private int currentPlayer;
  private List<Long> actions;
  private Random random = new Random();
  
  /**
   * Null-initializing constructor.
   */
  public TicTacToeState() {
  }
  
  private TicTacToeState(int board, int currentPlayer, List<Long> actions) {
    this.board = board;
    this.currentPlayer = currentPlayer;
    this.actions = actions;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public State.ActionIterator getActionIterator() {
    return new State.ActionIteratorFromIterable(actions);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public long getRandomAction() {
    return actions.get(random.nextInt(actions.size()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public long perform(long action) {
    board |= action;
    currentPlayer = playerAfter(currentPlayer);
    actions = allActions();
    return 0;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void undo(long action, long undoToken) {
    board &= ~action;
    currentPlayer = playerBefore(currentPlayer);
    actions = allActions();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public State setToStartingConditions() {
    board = 0;
    currentPlayer = Player.PLAYER_ONE;
    actions = allActions();
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public State copy() {
    if (actions == null) {
      return new TicTacToeState();
    }
    return new TicTacToeState(board, currentPlayer, new ArrayList<Long>(actions));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public State initialize(State state) {
    TicTacToeState copy = (TicTacToeState)state.copy();
    this.board = copy.board;
    this.currentPlayer = copy.currentPlayer;
    this.actions = copy.actions;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isTerminal() {
    if (Long.bitCount(board) >= 9) {
      return true;
    } else {
      return getWinner() != 0;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getWinner() {
    for (int i : WINNING_X_LINES) {
      if ((i & board) == i) {
        return Player.PLAYER_ONE;
      }
    }
    for (int j : WINNING_O_LINES) {
      if ((j & board) == j) {
        return Player.PLAYER_TWO;
      }
    }
    return 0;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getCurrentPlayer() {
    return currentPlayer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int playerAfter(int player) {
    return player == Player.PLAYER_ONE ? Player.PLAYER_TWO : Player.PLAYER_ONE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int playerBefore(int player) {
    return playerAfter(player);
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String actionToString(long action) {
    int result = 0;
    while (action != 0) {
      action >>= 1;
      result++;
    }
    result = 9 - result;
    if (result < 0) {
      result += 12;
    }
    return "[" + result + "]";
  }
  
  /**
   * @return All actions currently possible in the game.
   */
  private List<Long> allActions() {
    List<Long> result = new ArrayList<Long>(9);
    int action = getCurrentPlayer() == Player.PLAYER_ONE ? 0x0001 : 0x1000;
    int other = getCurrentPlayer() == Player.PLAYER_ONE ? 0x1000 : 0x0001;
    for (int i = 0; i < 9; ++i) {
      if (((action & board) == 0) && ((other & board) == 0)) {
        result.add((long)action);
      }
      action <<= 1;
      other <<= 1;
    }    
    return result;
  }
  
  public String toString() {
    int xIterator = 0x100;
    int oIterator = 0x100000;
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < 3; ++i) {
      for (int j = 0; j < 3; ++j) {
        if ((xIterator & board) == xIterator) {
          result.append("X");
        } else if ((oIterator & board) == oIterator) {
          result.append("O");
        } else {
          result.append("_");
        }
        xIterator >>= 1;
        oIterator >>= 1;
      }
      result.append("\n");
    }
    return result.toString();
  }

}
