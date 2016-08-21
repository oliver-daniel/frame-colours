import org.bytedeco.javacv.*;
import java.io.*;
import java.net.URL;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.*;
import javax.swing.*;

public class App
{
  static String url;
  static int frames;
  static String urlfile;
  static boolean fileExists;
  
  static void getVid() throws Exception {
    if ((url = JOptionPane.showInputDialog("URL/ID:")) == null)
      System.exit(0);
    
    url = url.replaceAll(
                         "http(s)?:\\/\\/www\\.youtube\\.com\\/watch\\?v=","");
    
    urlfile = "../res/" + url + "/" + url + ".txt";
    
    fileExists=(new File(urlfile)).exists();
    
    if (fileExists)
      return;
    
    Process p = Runtime.getRuntime().exec(
                                          "/usr/local/bin/youtube-dl --id -f worstvideo[ext=mp4]  " + url);
    
    p.waitFor();
    System.out.println("Complete!");
  }
  
  static void writeFrameAverages() throws Exception {
    
    if (fileExists)
      return;
    
    FFmpegFrameGrabber f = new FFmpegFrameGrabber(url + ".mp4");
    Java2DFrameConverter c = new Java2DFrameConverter();
    File file = new File(urlfile);
    file.getParentFile().mkdirs();
    file.createNewFile();
    PrintWriter pr = new PrintWriter(new FileWriter(urlfile));
    f.start();
    
    frames = f.getLengthInFrames();
    pr.println(frames);
    
    for (int i = 0; i < frames; i++) {
      BufferedImage frame = c.getBufferedImage(f.grab());
      
      int red = 0;
      int green = 0;
      int blue = 0;
      
      for (int y = 0; y < frame.getHeight(); y++) {
        for (int x = 0; x < frame.getWidth(); x++) {
          int clr   = frame.getRGB(x, y);
          red   += (clr & 0x00ff0000) >> 16;
          green += (clr & 0x0000ff00) >> 8;
          blue  +=  clr & 0x000000ff;
        }
      }
      
      int totalFrames = frame.getHeight() * frame.getWidth();
      
      red /= totalFrames;
      green /= totalFrames;
      blue /= totalFrames;
      
      pr.println(red + " " + green + " " + blue);
    }
    
    f.stop();
    new File(url + ".mp4").delete();
    pr.close();
  }
  
  
  static void illustrate() throws Exception {
    
    ColorPanel p = new ColorPanel();
    p.setVisible(true);
    
    if(JOptionPane.showConfirmDialog(null,p,null,JOptionPane.DEFAULT_OPTION,
                                     JOptionPane.PLAIN_MESSAGE)
         == JOptionPane.CLOSED_OPTION)
      System.exit(0);
    
    BufferedImage img = new BufferedImage(
                                          p.getWidth(),p.getHeight(),BufferedImage.TYPE_INT_RGB);
    Graphics2D g = img.createGraphics();
    p.printAll(g);
    g.dispose();
    
    ImageIO.write(img,"png",new File("../res/" + url + "/" + "render.png"));
  }
  
  public static void main( String[] args ){
    while(true) {
      try{
        System.out.println("Retrieving video...");
        getVid();
        System.out.println("Video retrieved. Getting average frame colours...");
        writeFrameAverages();
        System.out.println("Averages retrieved. Rendering barcode...");
        illustrate();
      }catch(Exception e) {e.printStackTrace(); }
    }
  }
}
