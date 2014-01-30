package ca.thurn.uct.connect4;

public class Output {
  private boolean interactive = false;
  
  private static final Output INSTANCE = new Output();
  
  public static final Output getInstance() {
    return INSTANCE;
  }
  
  public boolean isInteractive() {
    return interactive;
  }
  
  public void setIsInteractive(boolean isInteractive) {
    this.interactive = isInteractive;
  }
}
