import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.ListIterator;

public class ColorPanel extends JPanel {
  
  static int SIZE = 2;
  static ArrayList<Integer> rgb;
  
  public ColorPanel(){
    rgb = new ArrayList<Integer>();
    try{
      String line;
      BufferedReader f = new BufferedReader(new FileReader(App.urlfile));
      App.frames = Integer.parseInt(f.readLine());
      System.out.println("Complete!");
      System.out.println(App.frames + " frames.");
      SIZE = Math.max(1,1000 / (App.frames / 24));
      
      while ((line = f.readLine()) != null) {
        for (String x: line.split(" "))
          rgb.add(Integer.valueOf(x));
      }
    }catch(IOException i) {
      i.printStackTrace();
    }
  }
  
  @Override
  protected void paintComponent(Graphics gr){
    super.paintComponent(gr);
    
    int linePt = 0;
    int r = 0;
    int g = 0;
    int b = 0;
    ListIterator<Integer>it = rgb.listIterator();
    
    for (int i = 0; i < App.frames / 24; i++) {
      for (int ii = 0; ii < 24; ii++) {
        r += it.next();
        g += it.next();
        b += it.next();
      }
      r /= 24;
      g /= 24;
      b /= 24;
      
      r = Math.min(255,r);
      g = Math.min(255,g);
      b = Math.min(255,b);
      
      
      gr.setColor(new Color(r,g,b));
      gr.fillRect(linePt,0,linePt + SIZE,400);
      linePt += SIZE;
    }
  }
  
  @Override
  public Dimension getPreferredSize(){
    return new Dimension((App.frames / 24) * SIZE,400);
  }
}
