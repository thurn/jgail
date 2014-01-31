package ca.thurn.uct.connect4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.thurn.uct.algorithm.Player;
import ca.thurn.uct.algorithm.State;

/**
 * This is a simple implementation of Connect4State that does not attempt
 * any optimization. It's largely immutable.
 */
public class Connect4State extends State<Connect4Action> {

  private static List<Connect4Action> redActions;
  private static List<Connect4Action> blackActions;

  static {
    redActions = new ArrayList<Connect4Action>();
    for (int i = 0; i < 7; ++i) {
      redActions.add(new Connect4Action(Connect4Player.RED, i));            
    }

    blackActions = new ArrayList<Connect4Action>();
    for (int i = 0; i < 7; ++i) {
      blackActions.add(new Connect4Action(Connect4Player.BLACK, i));
    }
  }
  
  static enum Direction {
    N,
    NE,
    E,
    SE,
    S,
    SW,
    W,
    NW
  }

  // Indexed as board[column][row] with the origin being in the bottom left.
  Connect4Player[][] board;
  
  Connect4Player[][] originalBoard;
  
  // The player whose turn it is
  final Connect4Player currentPlayer;
  
  // The player who has won the game at this state, or null if the game is
  // still ongoing.
  final Connect4Player winner;

  public Connect4State() {
    super(blackActions);
    this.board = new Connect4Player[7][6];
    this.currentPlayer = Connect4Player.BLACK; // BLACK moves first
    this.winner = null;
  }
  
  Connect4State(Connect4Player currentPlayer, Connect4Player[][] board, Connect4Player winner) {
    // TODO: are legal actions correctly updated as the board changes?
    super(actionsForPlayer(board, currentPlayer));
    this.board = board;
    this.currentPlayer = currentPlayer;
    this.winner = winner;
  }
  
  public Connect4State(Connect4State state) {
    this(state.currentPlayer, copyBoard(state.board), state.winner);    
  }

  static List<Connect4Action> actionsForPlayer(Connect4Player[][] board, Connect4Player player) {
    List<Connect4Action> actions = player == Connect4Player.RED ? redActions : blackActions;
    List<Connect4Action> result = new ArrayList<Connect4Action>();
    for (Connect4Action action : actions) {
      if (board[action.getColumnNumber()][5] == null) {
        result.add(action);
      }
    }
    return result;
  }

  @Override
  public ActionResult<Connect4Action> perform(Player player, Connect4Action action) {
    int freeCell = 0;
    Connect4Player[][] board = copyBoard(this.board);
    while (board[action.getColumnNumber()][freeCell] != null) {
      freeCell++;
    }
    Connect4Player nextStateWinner = computeWinner(currentPlayer, action.getColumnNumber(), freeCell);
    board[action.getColumnNumber()][freeCell] = currentPlayer;    
    Connect4State nextState = new Connect4State(
        currentPlayer == Connect4Player.RED ? Connect4Player.BLACK : Connect4Player.RED,
        board,
        nextStateWinner);
    return new ActionResult<Connect4Action>(nextState, 0.0);
  }

  public State<Connect4Action> unperform(Player player, Connect4Action action) {
    int freeCell = 5;
    while (board[action.getColumnNumber()][freeCell] == null) {
      freeCell--;
    }
    board[action.getColumnNumber()][freeCell] = null;
    return new Connect4State(currentPlayer == Connect4Player.RED ? Connect4Player.BLACK : Connect4Player.RED,
        board,
        null);
  } 

  @Override
  public boolean isTerminal() {
    if (actions.size() == 0) return true; // Draw
    return winner != null;
  }
  
  @Override
  public void reset() {
    this.board = copyBoard(this.originalBoard);
  }
  
  @Override
  public void prepareForSimulation() {
    this.originalBoard = copyBoard(board);
  }
  
  protected static Connect4Player[][] copyBoard(Connect4Player[][] board) {
    Connect4Player[][] result = new Connect4Player[7][6];
    for (int i = 0; i < 7; ++i) {
      result[i] = Arrays.copyOf(board[i], 7);
    }
    return result;
  }  
  
  @Override  
  public String toString() {
    StringBuilder result = new StringBuilder();
    for (int row = 5; row >= 0; --row) {
      for (int column = 0; column < 7; ++column) {
        Connect4Player p = board[column][row];
        if (p == null) {
          result.append("-");
        } else {
          result.append(p == Connect4Player.RED ? "X" : "O");
        }
      }
      result.append("\n");
    }
    return result.toString();
  }

  @Override  
  public Connect4Player getWinner() {
    return winner;
  }
  
  public Player getCurrentPlayer() {
    return currentPlayer;
  }
  
  /**
   * Checks whether the provided player has won by making the provided move.
   */
  public Connect4Player computeWinner(Connect4Player player, int moveColumn, int moveRow) {
    // Vertical win?
    if (countGroupSize(moveColumn, moveRow - 1, Direction.S, player) >= 3) {
      return player;
    }
    
    // Horizontal win?
    if (countGroupSize(moveColumn + 1, moveRow, Direction.E, player) +
        countGroupSize(moveColumn - 1, moveRow, Direction.W, player) >= 3) {
      return player;
    }
    
    // Diagonal win?
    if (countGroupSize(moveColumn + 1, moveRow + 1, Direction.NE, player) +
        countGroupSize(moveColumn - 1, moveRow - 1, Direction.SW, player) >= 3) {
      return player;
    }
    if (countGroupSize(moveColumn - 1, moveRow + 1, Direction.NW, player) +
        countGroupSize(moveColumn + 1, moveRow - 1, Direction.SE, player) >=3) {
      return player;
    }
    
    // No win
    return null;
  }

  public int countGroupSize(int col, int row, Direction dir, Player player) {
    if (row < 6 && row >= 0 && col < 7 && col >= 0
        && board[col][row] == player) {
      switch (dir) {
        case N:
          return 1 + countGroupSize(col, row + 1, dir, player);
        case S:
          return 1 + countGroupSize(col, row - 1, dir, player);
        case E:
          return 1 + countGroupSize(col + 1, row, dir, player);
        case W:
          return 1 + countGroupSize(col - 1, row, dir, player);
        case NE:
          return 1 + countGroupSize(col + 1, row + 1, dir, player);
        case NW:
          return 1 + countGroupSize(col - 1, row + 1, dir, player);
        case SE:
          return 1 + countGroupSize(col + 1, row - 1, dir, player);
        case SW:
          return 1 + countGroupSize(col - 1, row - 1, dir, player);
        default:
          return 0;
      }
    } else {
      return 0;
    }
  }

  @Override
  public int numActions() {
    return 7;
  }

}