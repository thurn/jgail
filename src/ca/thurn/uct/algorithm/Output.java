package ca.thurn.uct.algorithm;

public class Output {
  private boolean interactive = false;
  
  private boolean color = false;
  
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
  
  public boolean isColor() {
    return color;
  }
  
  public void setIsColor(boolean color) {
    this.color = color;
  }
}
