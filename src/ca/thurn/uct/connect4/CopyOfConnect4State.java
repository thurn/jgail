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
public class CopyOfConnect4State extends State<Connect4Action> {

  private static List<Connect4Action> redActions;
  private static List<Connect4Action> blackActions;

  static {
    redActions = new ArrayList<Connect4Action>();
    for (int i = 0; i < 7; ++i) {
      redActions.add(new Connect4Action(Player.PLAYER_TWO, i));            
    }

    blackActions = new ArrayList<Connect4Action>();
    for (int i = 0; i < 7; ++i) {
      blackActions.add(new Connect4Action(Player.PLAYER_TWO, i));
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
  Player[][] board;
  
  Player[][] originalBoard;
  
  // The player whose turn it is
  final Player currentPlayer;
  
  // The player who has won the game at this state, or null if the game is
  // still ongoing.
  final Player winner;

  public CopyOfConnect4State() {
    setActions(blackActions);
    this.board = new Player[7][6];
    this.currentPlayer = Player.PLAYER_ONE;
    this.winner = null;
  }
  
  CopyOfConnect4State(Connect4State other) {
    this.board = copyBoard(other.board);
    this.currentPlayer = other.currentPlayer;
    this.winner = other.winner;
    setActions(actionsForPlayer(board, currentPlayer));    
  }
  
  private CopyOfConnect4State(Player currentPlayer, Player[][] board, Player winner) {
    this.board = board;
    this.currentPlayer = currentPlayer;
    this.winner = winner;
    setActions(actionsForPlayer(board, currentPlayer));
  }
  
  protected CopyOfConnect4State copyInternal() {
    return new CopyOfConnect4State(currentPlayer, copyBoard(board), winner);
  }

  static List<Connect4Action> actionsForPlayer(Player[][] board, Player player) {
    List<Connect4Action> actions = player == Player.PLAYER_TWO ? redActions : blackActions;
    List<Connect4Action> result = new ArrayList<Connect4Action>();
    for (Connect4Action action : actions) {
      if (board[action.getColumnNumber()][5] == null) {
        result.add(action);
      }
    }
    return result;
  }

  @Override
  public CopyOfConnect4State performInternal(Connect4Action action) {
//    Player[][] board = copyBoard(this.board);
    int freeCell = 0;
    while (board[action.getColumnNumber()][freeCell] != null) {
      freeCell++;
    }
    Player nextStateWinner = computeWinner(currentPlayer, action.getColumnNumber(), freeCell);
    board[action.getColumnNumber()][freeCell] = currentPlayer;    
    CopyOfConnect4State nextState = new CopyOfConnect4State(
        playerAfter(currentPlayer),
        board,
        nextStateWinner);
    return nextState;
  }
  
  @Override
  public boolean performOnCachedStates() {
    return true;
  }

  public State<Connect4Action> unperform(Connect4Action action) {
    int freeCell = 5;
    while (board[action.getColumnNumber()][freeCell] == null) {
      freeCell--;
    }
    board[action.getColumnNumber()][freeCell] = null;
    return new CopyOfConnect4State(playerAfter(currentPlayer), board, null);
  } 

  @Override
  public boolean isTerminal() {
    if (getActions().size() == 0) return true; // Draw
    return winner != null;
  }
  
  @Override
  public void reset() {
    for (int i = 0; i < 7; ++i) {
      board[i] = Arrays.copyOf(originalBoard[i], 6);
    }
  }
  
  @Override
  public void prepareForSimulation() {
    this.originalBoard = copyBoard(board);
  }
  
  private static Player[][] copyBoard(Player[][] board) {
    Player[][] result = new Player[7][6];
    for (int i = 0; i < 7; ++i) {
      result[i] = Arrays.copyOf(board[i], 6);
    }
    return result;
  }  
  
  @Override  
  public String toString() {
    StringBuilder result = new StringBuilder();
    for (int row = 5; row >= 0; --row) {
      for (int column = 0; column < 7; ++column) {
        Player p = board[column][row];
        if (p == null) {
          result.append("-");
        } else {
          result.append(p ==  Player.PLAYER_TWO ? "X" : "O");
        }
      }
      result.append("\n");
    }
    return result.toString();
  }

  @Override  
  public Player getWinner() {
    return winner;
  }
  
  public Player getCurrentPlayer() {
    return currentPlayer;
  }
  
  /**
   * Checks whether the provided player has won by making the provided move.
   */
  public Player computeWinner(Player player, int moveColumn, int moveRow) {
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

}