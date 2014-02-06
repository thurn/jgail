package ca.thurn.uct.ingenious;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.thurn.uct.algorithm.State;
import ca.thurn.uct.core.Player;

public class IngeniousState extends State<IngeniousAction> {
  
  // 11x11 board using the axial coordinate system
  private final IngeniousHex[][] board;
  
  private final Player currentPlayer;
  
  private final Map<Player, List<IngeniousPiece>> hands;
  
  private static final int BOARD_SIZE = 11;
  
  private static final int HAND_SIZE = 6;
  
  private static final int STARTING_FREE_HEXES = 91;
  
  private final int emptyHexesRemaining;
  
  private final Map<Player, Map<IngeniousHex, Integer>> scores;

  private static enum Direction {
    NE,
    E,
    SE,
    SW,
    W,
    NW
  }
  
  public IngeniousState() {
    this.board = new IngeniousHex[BOARD_SIZE][BOARD_SIZE];
    addOffBoardHexes(board);
    this.currentPlayer = Player.PLAYER_ONE;
    this.hands = new HashMap<Player, List<IngeniousPiece>>();
    List<IngeniousPiece> currentHand = randomHand();
    this.hands.put(Player.PLAYER_ONE, currentHand);
    this.hands.put(Player.PLAYER_TWO, randomHand());
    this.emptyHexesRemaining = STARTING_FREE_HEXES;
    this.scores = new HashMap<Player, Map<IngeniousHex, Integer>>();
    this.scores.put(Player.PLAYER_ONE, new HashMap<IngeniousHex, Integer>());
    this.scores.put(Player.PLAYER_TWO, new HashMap<IngeniousHex, Integer>());
    setActions(allActions(board, currentHand));
  }

  private IngeniousState(IngeniousHex[][] board, Player player,
      Map<Player, List<IngeniousPiece>> hands, int emptyHexesRemaining,
      Map<Player, Map<IngeniousHex, Integer>> scores) {
    this.board = board;
    this.currentPlayer = player;
    this.hands = hands;
    this.emptyHexesRemaining = emptyHexesRemaining;
    this.scores = scores;
    setActions(allActions(board, hands.get(player)));
  }  
  
  private IngeniousState(List<IngeniousAction> actions, IngeniousHex[][] board,
      Player player, Map<Player, List<IngeniousPiece>> hands, int emptyHexesRemaining,
      Map<Player, Map<IngeniousHex, Integer>> scores) {
    this.board = board;
    this.currentPlayer = player;
    this.hands = hands;
    this.emptyHexesRemaining = emptyHexesRemaining;
    this.scores = scores;
    setActions(actions);
  }
  
  private static List<IngeniousAction> allActions(IngeniousHex[][] board,
      List<IngeniousPiece> hand) {
    List<IngeniousAction> result = new ArrayList<IngeniousAction>();
    for (int x = 0; x < BOARD_SIZE; ++x) {
      for (int y = 0; y < BOARD_SIZE; ++y) {
        if (board[x][y] == null) {
          addActionsForPosition(board, hand, result, x, y);
        }
      }
    }
    return result;
  }
  
  private static void addActionsForPosition(IngeniousHex[][] board, List<IngeniousPiece> hand, 
      List<IngeniousAction> list, int x, int y) {
    if (y - 1 > 0 && y - 1 < BOARD_SIZE && board[x][y - 1] == null) {
      addActionsForMove(hand, list, x, y, x, y - 1);
    } else if (x + 1 > 0 && x + 1 < BOARD_SIZE && y - 1 > 0 && y - 1 < BOARD_SIZE &&
        board[x + 1][y - 1] == null) {
      addActionsForMove(hand, list, x, y, x + 1, y - 1);
    } else if (x + 1 > 0 && x + 1 < BOARD_SIZE && board[x + 1][y] == null) {
      addActionsForMove(hand, list, x, y, x + 1, y);
    } else if (y + 1 > 0 && y + 1 < BOARD_SIZE && board[x][y + 1] == null) {
      addActionsForMove(hand, list, x, y, x, y + 1);
    } else if (x - 1 > 0 && x - 1 < BOARD_SIZE && y + 1 > 0 && y + 1 < BOARD_SIZE &&
        board[x - 1][y + 1] == null) {
      addActionsForMove(hand, list, x, y, x - 1 , y + 1);
    } else if (x - 1 > 0 && x - 1 < BOARD_SIZE && board[x - 1][y] == null) {
      addActionsForMove(hand, list, x, y, x - 1, y);
    }
  }
  
  private static void addActionsForMove(List<IngeniousPiece> hand, List<IngeniousAction> list,
      int x1, int y1, int x2, int y2) {
    for (IngeniousPiece piece : hand) {
      list.add(new IngeniousAction(piece, x1, y1, x2, y2));
    }
  }
  
  private static List<IngeniousPiece> randomHand() {
    List<IngeniousPiece> hand = new ArrayList<IngeniousPiece>(HAND_SIZE);
    for (int i = 0; i < HAND_SIZE; ++i) {
      hand.add(randomPiece());
    }
    return hand;
  }
  
  private static IngeniousPiece randomPiece() {
    return new IngeniousPiece(IngeniousHex.randomHex(), IngeniousHex.randomHex());
  }
  
  private static void addOffBoardHexes(IngeniousHex[][] board) {
    board[0][0] = IngeniousHex.OFF_BOARD;
    board[0][1] = IngeniousHex.OFF_BOARD;
    board[0][2] = IngeniousHex.OFF_BOARD;
    board[0][3] = IngeniousHex.OFF_BOARD;
    board[0][4] = IngeniousHex.OFF_BOARD;
    board[1][0] = IngeniousHex.OFF_BOARD;
    board[1][1] = IngeniousHex.OFF_BOARD;
    board[1][2] = IngeniousHex.OFF_BOARD;
    board[1][3] = IngeniousHex.OFF_BOARD;
    board[2][0] = IngeniousHex.OFF_BOARD;
    board[2][1] = IngeniousHex.OFF_BOARD;
    board[2][2] = IngeniousHex.OFF_BOARD;
    board[3][0] = IngeniousHex.OFF_BOARD;
    board[3][1] = IngeniousHex.OFF_BOARD;
    board[4][0] = IngeniousHex.OFF_BOARD;
    
    board[10][6] = IngeniousHex.OFF_BOARD;
    board[10][7] = IngeniousHex.OFF_BOARD;
    board[10][8] = IngeniousHex.OFF_BOARD;
    board[10][9] = IngeniousHex.OFF_BOARD;
    board[10][10] = IngeniousHex.OFF_BOARD;
    board[9][7] = IngeniousHex.OFF_BOARD;
    board[9][8] = IngeniousHex.OFF_BOARD;
    board[9][9] = IngeniousHex.OFF_BOARD;
    board[9][10] = IngeniousHex.OFF_BOARD;
    board[8][8] = IngeniousHex.OFF_BOARD;
    board[8][9] = IngeniousHex.OFF_BOARD;
    board[8][10] = IngeniousHex.OFF_BOARD;
    board[7][9] = IngeniousHex.OFF_BOARD;
    board[7][10] = IngeniousHex.OFF_BOARD;
    board[6][10] = IngeniousHex.OFF_BOARD;       
  }

  @Override
  protected IngeniousState copyInternal() {
    return new IngeniousState(getActions(), copyBoard(), currentPlayer, copyHands(),
        emptyHexesRemaining, copyScores());
  }
  
  private IngeniousHex[][] copyBoard() {
    IngeniousHex[][] result = new IngeniousHex[BOARD_SIZE][BOARD_SIZE];
    for (int i = 0; i < BOARD_SIZE; ++i) {
      result[i] = Arrays.copyOf(board[i], BOARD_SIZE);
    }
    return result;
  }
  
  private Map<Player, List<IngeniousPiece>> copyHands() {
    Map<Player, List<IngeniousPiece>> result =
        new HashMap<Player, List<IngeniousPiece>>();
    result.put(Player.PLAYER_ONE,
        new ArrayList<IngeniousPiece>(hands.get(Player.PLAYER_ONE)));
    result.put(Player.PLAYER_TWO,
        new ArrayList<IngeniousPiece>(hands.get(Player.PLAYER_TWO)));
    return result;
  }
  
  private Map<Player, Map<IngeniousHex, Integer>> copyScores() {
    Map<Player, Map<IngeniousHex, Integer>> result =
        new HashMap<Player, Map<IngeniousHex, Integer>>();
    result.put(Player.PLAYER_ONE,
        new HashMap<IngeniousHex, Integer>(scores.get(Player.PLAYER_ONE)));
    result.put(Player.PLAYER_TWO,
        new HashMap<IngeniousHex, Integer>(scores.get(Player.PLAYER_TWO)));    
    return result;
  }

  @Override
  public State<IngeniousAction> performInternal(IngeniousAction action) {
    // Perform move
    IngeniousHex[][] newBoard = copyBoard();
    newBoard[action.getX1()][action.getY1()] = action.getPiece().getHex1();
    newBoard[action.getX2()][action.getY2()] = action.getPiece().getHex2();
    
    // Score points
    Map<Player, Map<IngeniousHex, Integer>> newScores = copyScores();    
    Map<IngeniousHex, Integer> myScores = newScores.get(currentPlayer);
    // Hex 1
    IngeniousHex hex1 = action.getPiece().getHex1();
    IngeniousHex hex2 = action.getPiece().getHex2();
    Direction exclude1 = 
        hexDirection(action.getX1(), action.getY1(), action.getX2(), action.getY2());
    Direction exclude2 =
        hexDirection(action.getX2(), action.getY2(), action.getX1(), action.getY1());
    myScores.put(hex1, getWithDefault(myScores, hex1, 0) + 
        scoreForHex(newBoard, action.getX1(), action.getY1(), hex1, exclude1));
    myScores.put(hex2, getWithDefault(myScores, hex2, 0) + 
        scoreForHex(newBoard, action.getX2(), action.getY2(), hex2, exclude2));    
    
    // Update hand
    Map<Player, List<IngeniousPiece>> newHands = copyHands();
    List<IngeniousPiece> hand = newHands.get(currentPlayer);
    hand.remove(action.getPiece());
    hand.add(randomPiece());

    return new IngeniousState(newBoard, playerAfter(currentPlayer), newHands,
        emptyHexesRemaining - 2, newScores);
  }
  
  private int scoreForHex(IngeniousHex[][] board, int x, int y, IngeniousHex hex,
      Direction excludeDirection) {
    int total = 0;
    if (Direction.NE != excludeDirection) {
      total += countInDirection(board, x + 1, y - 1, hex, Direction.NE);
    }
    if (Direction.E != excludeDirection) {
      total += countInDirection(board, x + 1, y, hex, Direction.E);
    }
    if (Direction.SE != excludeDirection) {
      total += countInDirection(board, x, y + 1, hex, Direction.SE);
    }
    if (Direction.SW != excludeDirection) {
      total += countInDirection(board, x - 1, y + 1, hex, Direction.SW);
    }
    if (Direction.W != excludeDirection) {
      total += countInDirection(board, x - 1, y, hex, Direction.W);
    }
    if (Direction.NW != excludeDirection) {
      total += countInDirection(board, x, y - 1, hex, Direction.NW);
    }   
    return total;
  }
  
  private int countInDirection(IngeniousHex[][] board, int x, int y, IngeniousHex hex,
      Direction direction) {
    if (x < 0 || y < 0 || x >= BOARD_SIZE || y >= BOARD_SIZE || board[x][y] == null ||
        board[x][y] != hex) {
      return 0;
    }
    switch (direction) {
      case NE:
        return 1 + countInDirection(board, x + 1, y - 1, hex, direction);
      case E:
        return 1 + countInDirection(board, x + 1, y, hex, direction);
      case SE:
        return 1 + countInDirection(board, x, y + 1, hex, direction);
      case SW:
        return 1 + countInDirection(board, x - 1, y + 1, hex, direction);
      case W:
        return 1 + countInDirection(board, x - 1, y, hex, direction);
      case NW:
        return 1 + countInDirection(board, x, y - 1, hex, direction);
    }
    return 0;
  }
  
  /**
   * Returns the direction you will travel if you go from (x1, y1) to
   * (x2, y2), assuming they are one step apart. 
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

  @Override
  public State<IngeniousAction> unperform(IngeniousAction action) {
    throw new RuntimeException();
  }

  @Override
  public boolean isTerminal() {
    return emptyHexesRemaining == 0;
  }

  @Override
  public Player getCurrentPlayer() {
    return currentPlayer;
  }
  
  IngeniousPiece getPiece(Player player, int index) {
    return hands.get(player).get(index);
  }
  
  boolean isOpen(int x, int y) {
    return board[x][y] == null;
  }

  @Override
  public Player getWinner() {
    // TODO: handle tiebreakers

    Player winner = null;
    int bestScore = -1;
    for (Player player : scores.keySet()) {
      int total = Integer.MAX_VALUE;
      Map<IngeniousHex, Integer> map = scores.get(player);
      for (IngeniousHex hex : IngeniousHex.allColors()) {
       int score = getWithDefault(map, hex, 0);
       if (score < total) {
         total = score;
       }
      }
      if (total > bestScore) {
        winner = player;
      }
    }
    return winner;
  }
  
  private void addSpaces(StringBuilder builder, int count) {
    for (int i = 0; i < count; ++i) {
      builder.append(" ");
    }
  }
  
  public String toString() {
    StringBuilder result = new StringBuilder();
    result.append("\nNum Actions: " + getActions().size() + "\n");
    scoresToString(result, scores);
    boardToString(result, board);
    handsToString(result, hands);
    return result.toString();
  }

  private void scoresToString(StringBuilder result,
      Map<Player, Map<IngeniousHex, Integer>> scores) {
    result.append("Scores:\n");
    for (Player player : scores.keySet()) {
      result.append(player + ":");
      for (IngeniousHex hex : IngeniousHex.allColors()) {
        result.append(" " + hex + "=" + getWithDefault(scores.get(player), hex, 0));
      }
      result.append("\n");
    }
  }

  private void boardToString(StringBuilder result, IngeniousHex[][] board) {
    result.append("\nBoard:\n");
    for (int y = 0; y < BOARD_SIZE; ++y) {
      addSpaces(result, 2*Math.abs(5 - y));
      for (int x = 0; x < BOARD_SIZE; ++x) {
        if (board[x][y] == null) {
          if (x == 10) {
            result.append("[T" + y % 10 + "]");            
          } else if (y == 10) {
            result.append("[" + x + "T]");            
          } else {
            result.append("[" + x + "" + y + "]");
          }
          
        } else {
          if (board[x][y] != IngeniousHex.OFF_BOARD) {
            result.append("[" + board[x][y].toString() + "]");
          }
        }
      }
      result.append("\n");
    }
  }

  private void handsToString(StringBuilder result, Map<Player, List<IngeniousPiece>> hands) {
    result.append("\nHand:\n");
    int pieceNumber = 0;
    for (IngeniousPiece piece : hands.get(currentPlayer)) {
      result.append(" " + pieceNumber + ") " + piece);
      pieceNumber++;
    }
  }
}
