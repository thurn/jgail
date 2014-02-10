package ca.thurn.uct.ingenious;

import gnu.trove.list.TIntList;
import gnu.trove.list.TLongList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.list.array.TLongArrayList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

import java.util.Arrays;

import ca.thurn.uct.core.FastPlayer;
import ca.thurn.uct.core.FastState;

/**
 * State class for the game Ingenious.
 */
public class FastIngeniousState implements FastState {
  
  private static final int BOARD_SIZE = 11;
  private static final int HAND_SIZE = 6;
  
  private static enum Direction {
    NE, E, SE, SW, W, NW
  }
  
  private TLongList actions;
  // 11x11 board using the axial coordinate system
  private int[][] board;
  private int currentPlayer;
  private TIntList p1Hand;
  private TIntList p2Hand;
  private TIntIntMap p1Score;
  private TIntIntMap p2Score;
  
  /**
   * Null-initializing constructor.
   */
  public FastIngeniousState() {
  }

  private FastIngeniousState(TLongList actions, int[][] board,
      int currentPlayer, TIntList p1Hand, TIntList p2Hand,
      TIntIntMap p1Score, TIntIntMap p2Score) {
    this.actions = actions;
    this.board = board;
    this.currentPlayer = currentPlayer;
    this.p1Hand = p1Hand;
    this.p2Hand = p2Hand;
    this.p1Score = p1Score;
    this.p2Score = p2Score;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TLongList getActions() {
    return actions;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void perform(long action) {
    // Perform move
    board[FastIngeniousAction.getX1(action)][FastIngeniousAction.getY1(action)] = 
        FastIngeniousPiece.getHex1(FastIngeniousAction.getPiece(action));
    board[FastIngeniousAction.getX2(action)][FastIngeniousAction.getY2(action)] = 
        FastIngeniousPiece.getHex2(FastIngeniousAction.getPiece(action));
    
    // Score points
    TIntIntMap myScores = scoresForPlayer(currentPlayer);
    int hex1 = FastIngeniousPiece.getHex1(FastIngeniousAction.getPiece(action));
    int hex2 = FastIngeniousPiece.getHex2(FastIngeniousAction.getPiece(action));
    Direction exclude1 = 
        hexDirection(FastIngeniousAction.getX1(action), FastIngeniousAction.getY1(action),
            FastIngeniousAction.getX2(action), FastIngeniousAction.getY2(action));
    Direction exclude2 =
        hexDirection(FastIngeniousAction.getX2(action), FastIngeniousAction.getY2(action),
            FastIngeniousAction.getX1(action), FastIngeniousAction.getY1(action));
    myScores.put(hex1, myScores.get(hex1) + 
        scoreForHex(FastIngeniousAction.getX1(action), FastIngeniousAction.getY1(action), hex1, exclude1));
    myScores.put(hex2, myScores.get(hex2) + 
        scoreForHex(FastIngeniousAction.getX2(action), FastIngeniousAction.getY2(action), hex2, exclude2));    
    
    // Update hand
    TIntList hand = handForPlayer(currentPlayer);
    hand.remove(FastIngeniousAction.getPiece(action));
    hand.add(randomPiece());

    currentPlayer = playerAfter(currentPlayer);
    actions = allActions(handForPlayer(currentPlayer));
  }
  
  private TIntIntMap scoresForPlayer(int player) {
    return player == FastPlayer.PLAYER_ONE ? p1Score : p2Score;
  }
  
  private TIntList handForPlayer(int player) {
    return player == FastPlayer.PLAYER_ONE ? p1Hand : p2Hand;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void undo(long action) {
    throw new UnsupportedOperationException();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public FastState setToStartingConditions() {
    this.board = new int[BOARD_SIZE][BOARD_SIZE];
    addInitialHexes(board);
    this.currentPlayer = FastPlayer.PLAYER_ONE;
    TIntList currentHand = randomHand();
    this.p1Hand = currentHand;
    this.p2Hand = randomHand();
    p1Score = new TIntIntHashMap();
    p2Score = new TIntIntHashMap();    
    for (int hex : FastIngeniousHex.allColors()) {
      p1Score.put(hex, 0);
      p2Score.put(hex, 0);
    }
    actions = allActions(currentHand);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public FastState copy() {
    return new FastIngeniousState(new TLongArrayList(actions), copyBoard(),
        currentPlayer, new TIntArrayList(p1Hand), new TIntArrayList(p2Hand),
        new TIntIntHashMap(p1Score), new TIntIntHashMap(p2Score));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public FastState initialize(FastState state) {
    FastIngeniousState copy = (FastIngeniousState)state.copy();
    this.actions = copy.actions;
    this.board = copy.board;
    this.currentPlayer = copy.currentPlayer;
    this.p1Hand = copy.p1Hand;
    this.p2Hand = copy.p2Hand;
    this.p1Score = copy.p1Score;
    this.p2Score = copy.p2Score;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int playerAfter(int player) {
    return player == FastPlayer.PLAYER_ONE ? FastPlayer.PLAYER_TWO : FastPlayer.PLAYER_ONE;
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
  public boolean isTerminal() {
    return actions.size() == 0;
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
  public int getWinner() {
    int[] p1 = p1Score.values();
    Arrays.sort(p1);
    int[] p2 = p2Score.values();
    Arrays.sort(p2);
    
    for (int i = 0; i < 6; ++i) {
      if (p1[i] > p2[i]) {
        return FastPlayer.PLAYER_ONE;
      } else if (p2[i] > p1[i]) {
        return FastPlayer.PLAYER_TWO;
      }
    }
    
    return FastPlayer.NO_WINNER;
  }
  
  /**
   * @param player Player whose hand to look in.
   * @param index Index of desired piece.
   * @return The piece at the indicating index in this player's hand.
   */
  int getPiece(int player, int index) {
    return handForPlayer(player).get(index);
  }
  
  /**
   * @param x X coordinate.
   * @param y Y coordinate.
   * @return True if there is no hex currently placed on the specified board
   *     location.
   */
  boolean isOpen(int x, int y) {
    return board[x][y] == 0;
  }

  /**
   * @param hand The current player's hand.
   * @return A list of all actions possible from the current game state with
   *     the provided hand.
   */
  private TLongList allActions(TIntList hand) {
    TLongList result = new TLongArrayList(300);
    for (int x = 0; x < BOARD_SIZE; ++x) {
      for (int y = 0; y < BOARD_SIZE; ++y) {
        if (board[x][y] == 0) {
          addActionsForPosition(hand, result, x, y);
        }
      }
    }
    return result;
  }
  
  /**
   * Adds all possible actions at the provided board position to the action list.
   *
   * @param hand The current player's hand.
   * @param list The action list.
   * @param x X coordinate.
   * @param y Y coordinate.
   */
  private void addActionsForPosition(TIntList hand, TLongList list,
      int x, int y) {
    if (y - 1 > 0 && y - 1 < BOARD_SIZE && board[x][y - 1] == 0) {
      addActionsForMove(hand, list, x, y, x, y - 1);
    } else if (x + 1 > 0 && x + 1 < BOARD_SIZE && y - 1 > 0 && y - 1 < BOARD_SIZE &&
        board[x + 1][y - 1] == 0) {
      addActionsForMove(hand, list, x, y, x + 1, y - 1);
    } else if (x + 1 > 0 && x + 1 < BOARD_SIZE && board[x + 1][y] == 0) {
      addActionsForMove(hand, list, x, y, x + 1, y);
    } else if (y + 1 > 0 && y + 1 < BOARD_SIZE && board[x][y + 1] == 0) {
      addActionsForMove(hand, list, x, y, x, y + 1);
    } else if (x - 1 > 0 && x - 1 < BOARD_SIZE && y + 1 > 0 && y + 1 < BOARD_SIZE &&
        board[x - 1][y + 1] == 0) {
      addActionsForMove(hand, list, x, y, x - 1 , y + 1);
    } else if (x - 1 > 0 && x - 1 < BOARD_SIZE && board[x - 1][y] == 0) {
      addActionsForMove(hand, list, x, y, x - 1, y);
    }
  }
  
  /**
   * Add all possible actions involving providing a piece on the specified two
   * hexes onto the board.
   *
   * @param hand The current player's hand.
   * @param list The action list.
   * @param x1 First hex X coordinate.
   * @param y1 First hex Y coordinate.
   * @param x2 Second hex X coordinate.
   * @param y2 Second hex Y coordinate.
   */
  private void addActionsForMove(TIntList hand, TLongList list,
      int x1, int y1, int x2, int y2) {
    for (int i = 0; i < hand.size(); ++i) {
      list.add(FastIngeniousAction.create(hand.get(i), x1, y1, x2, y2));
    }
  }
  
  /**
   * @return A randomly generated hand of pieces.
   */
  private TIntList randomHand() {
    TIntList hand = new TIntArrayList(HAND_SIZE);
    for (int i = 0; i < HAND_SIZE; ++i) {
      hand.add(randomPiece());
    }
    return hand;
  }
  
  /**
   * @return A randomly generated piece.
   */
  private int randomPiece() {
    return FastIngeniousPiece.create(FastIngeniousHex.randomHex(), FastIngeniousHex.randomHex());
  }
  
  /**
   * Add OFF_BOARD dummy hexes to the parts of the board that are off-limits.
   * This is necessary because you can't store a hexagonal board in a square
   * array. Also add the starting colored hexes.
   *
   * @param board The board.
   */
  private void addInitialHexes(int[][] board) {
    board[0][0] = FastIngeniousHex.OFF_BOARD;
    board[0][1] = FastIngeniousHex.OFF_BOARD;
    board[0][2] = FastIngeniousHex.OFF_BOARD;
    board[0][3] = FastIngeniousHex.OFF_BOARD;
    board[0][4] = FastIngeniousHex.OFF_BOARD;
    board[1][0] = FastIngeniousHex.OFF_BOARD;
    board[1][1] = FastIngeniousHex.OFF_BOARD;
    board[1][2] = FastIngeniousHex.OFF_BOARD;
    board[1][3] = FastIngeniousHex.OFF_BOARD;
    board[2][0] = FastIngeniousHex.OFF_BOARD;
    board[2][1] = FastIngeniousHex.OFF_BOARD;
    board[2][2] = FastIngeniousHex.OFF_BOARD;
    board[3][0] = FastIngeniousHex.OFF_BOARD;
    board[3][1] = FastIngeniousHex.OFF_BOARD;
    board[4][0] = FastIngeniousHex.OFF_BOARD;
    
    board[10][6] = FastIngeniousHex.OFF_BOARD;
    board[10][7] = FastIngeniousHex.OFF_BOARD;
    board[10][8] = FastIngeniousHex.OFF_BOARD;
    board[10][9] = FastIngeniousHex.OFF_BOARD;
    board[10][10] = FastIngeniousHex.OFF_BOARD;
    board[9][7] = FastIngeniousHex.OFF_BOARD;
    board[9][8] = FastIngeniousHex.OFF_BOARD;
    board[9][9] = FastIngeniousHex.OFF_BOARD;
    board[9][10] = FastIngeniousHex.OFF_BOARD;
    board[8][8] = FastIngeniousHex.OFF_BOARD;
    board[8][9] = FastIngeniousHex.OFF_BOARD;
    board[8][10] = FastIngeniousHex.OFF_BOARD;
    board[7][9] = FastIngeniousHex.OFF_BOARD;
    board[7][10] = FastIngeniousHex.OFF_BOARD;
    board[6][10] = FastIngeniousHex.OFF_BOARD;
    
    board[0][10] = FastIngeniousHex.BLUE;
    board[5][10] = FastIngeniousHex.GREEN;
    board[10][5] = FastIngeniousHex.ORANGE;
    board[10][0] = FastIngeniousHex.PURPLE;
    board[5][0] = FastIngeniousHex.RED;
    board[0][5] = FastIngeniousHex.YELLOW;
  }
  
  /**
   * @return A copy of the current board.
   */
  private int[][] copyBoard() {
    int[][] result = new int[BOARD_SIZE][BOARD_SIZE];
    for (int i = 0; i < BOARD_SIZE; ++i) {
      result[i] = Arrays.copyOf(board[i], BOARD_SIZE);
    }
    return result;
  }
  
  /**
   * Calculates the score for placing a hex.
   * 
   * @param x X coordinate of hex.
   * @param y Y coordinate of hex.
   * @param hex The hex being placed.
   * @param excludeDirection A Direction in which score should NOT be counted.
   *     This is used to enforce the rule that you can't score points in the
   *     direction of a piece's sibling hex. 
   * @return Total score for placing this hex at these coordinates.
   */
  private int scoreForHex(int x, int y, int hex, Direction excludeDirection) {
    int total = 0;
    if (Direction.NE != excludeDirection) {
      total += countInDirection(x + 1, y - 1, hex, Direction.NE);
    }
    if (Direction.E != excludeDirection) {
      total += countInDirection(x + 1, y, hex, Direction.E);
    }
    if (Direction.SE != excludeDirection) {
      total += countInDirection(x, y + 1, hex, Direction.SE);
    }
    if (Direction.SW != excludeDirection) {
      total += countInDirection(x - 1, y + 1, hex, Direction.SW);
    }
    if (Direction.W != excludeDirection) {
      total += countInDirection(x - 1, y, hex, Direction.W);
    }
    if (Direction.NW != excludeDirection) {
      total += countInDirection(x, y - 1, hex, Direction.NW);
    }   
    return total;
  }
  
  /**
   * Counts hexes of the same color as the provided hex in the provided
   * direction from the given start coordinates.
   *
   * @param x Starting X coordinate.
   * @param y Starting Y coordinate.
   * @param hex Hex whose color we are counting.
   * @param direction Direction in which to count.
   * @return The number of hexes in a line in the provided direction from the
   *     given coordiantes.
   */
  private int countInDirection(int x, int y, int hex, Direction direction) {
    if (x < 0 || y < 0 || x >= BOARD_SIZE || y >= BOARD_SIZE || board[x][y] == 0 ||
        board[x][y] != hex) {
      return 0;
    }
    switch (direction) {
      case NE:
        return 1 + countInDirection(x + 1, y - 1, hex, direction);
      case E:
        return 1 + countInDirection(x + 1, y, hex, direction);
      case SE:
        return 1 + countInDirection(x, y + 1, hex, direction);
      case SW:
        return 1 + countInDirection(x - 1, y + 1, hex, direction);
      case W:
        return 1 + countInDirection(x - 1, y, hex, direction);
      case NW:
        return 1 + countInDirection(x, y - 1, hex, direction);
    }
    return 0;
  }
  
  /**
   * @param x1 First X coordinate.
   * @param y1 First Y coordinate.
   * @param x2 Second X coordinate.
   * @param y2 Second Y coordinate.
   * @return The direction you will travel if you go from (x1, y1) to
   *     (x2, y2), assuming they are one step apart. 
   */
  Direction hexDirection(int x1, int y1, int x2, int y2) {
    if (x2 - x1 == 0 && y2 - y1 == -1) {
      return Direction.NW;
    } else if (x2 - x1 == 1 && y2 - y1 == -1) {
      return Direction.NE;
    } else if (x2 - x1 == 1 && y2 - y1 == 0) {
      return Direction.E;
    } else if (x2 - x1 == 0 && y2 - y1 == 1) {
      return Direction.SE;
    } else if (x2 - x1 == -1 && y2 - y1 == 1) {
      return Direction.SW;
    } else if (x2 - x1 == -1 && y2 - y1 == 0) {
      return Direction.W;
    } else {
      throw new RuntimeException("Invalid arguments to hexDirection");
    }
  }
  
  /**
   * Append the given number of space characters to this StringBuilder.
   *
   * @param builder The StringBuilder.
   * @param count Number of spaces to append.
   */
  private void addSpaces(StringBuilder builder, int count) {
    for (int i = 0; i < count; ++i) {
      builder.append(" ");
    }
  }
  
  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    result.append("\nNum Actions: " + getActions().size() + "\n");
    scoresToString(result);
    boardToString(result);
    handsToString(result);
    return result.toString();
  }

  /**
   * Adds the current game scores to the provided StringBuilder.
   *
   * @param result The StringBuilder.
   */
  private void scoresToString(StringBuilder result) {
    result.append("Scores:\n");
      result.append("Player 1:");
      for (int hex : FastIngeniousHex.allColors()) {
        result.append(" " + FastIngeniousHex.toString(hex) + "=" + p1Score.get(hex));
      }
      result.append("\n");
      result.append("Player 2:");
      for (int hex : FastIngeniousHex.allColors()) {
        result.append(" " + FastIngeniousHex.toString(hex) + "=" + p2Score.get(hex));
      }
      result.append("\n");
  }

  /**
   * Adds a representation of the board to the provided StringBuilder.
   *
   * @param result The StringBuilder.
   */
  private void boardToString(StringBuilder result) {
    result.append("\nBoard:\n");
    for (int y = 0; y < BOARD_SIZE; ++y) {
      addSpaces(result, 2*Math.abs(5 - y));
      for (int x = 0; x < BOARD_SIZE; ++x) {
        if (board[x][y] == 0) {
          if (x == 10) {
            result.append("[T" + y % 10 + "]");            
          } else if (y == 10) {
            result.append("[" + x + "T]");            
          } else {
            result.append("[" + x + "" + y + "]");
          }
          
        } else {
          if (board[x][y] != FastIngeniousHex.OFF_BOARD) {
            result.append("[" + FastIngeniousHex.toString(board[x][y]) + "]");
          }
        }
      }
      result.append("\n");
    }
  }

  /**
   * Adds the current player's hand to the provided StringBuilder.  
   *
   * @param result The StringBuilder.
   */
  private void handsToString(StringBuilder result) {
    result.append("\nHand:\n");
    int pieceNumber = 0;
    TIntList hand = handForPlayer(currentPlayer);
    for (int i = 0; i < hand.size(); ++i) {
      result.append(" " + pieceNumber + ") " + FastIngeniousPiece.toString(hand.get(i)));
      pieceNumber++;
    }
  }
}
