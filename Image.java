//Coursework(Finding Squares)
//Author James Megenis (c1729929)
import java.io.*;
import java.util.*;
public class Image{

  int[][] pixels;
  int xsize;
  int ysize;
  String filename;
  String format;
  //image size
  String sizeStr;

  String maxvalue;
  public Image(int[][]a,int b,int c,String d,String e,String f,String g){
    pixels = a;
    xsize=b;
    ysize = c;
    filename = d;
    format = e;
    sizeStr = f;
    maxvalue = g;
  }
  public int[][] getPixels(){
    return pixels;
  }

  public Image(String name) throws IOException{

    filename = name;
    try {
      FileInputStream f = new FileInputStream(filename);
      DataInputStream d = new DataInputStream(f);
      //read the pgm files ignoring comments
      int r =0;
      while(r<3){
        String x = d.readLine();

        if (x.contains("#")){
          continue;
        }
        if (r == 0){
          format = x;
          r++;
          continue;
        }
        if (r == 1){

          sizeStr = x;

          r++;
          continue;
        }
        if (r ==2){
          maxvalue = x;
          r++;
          continue;
        }

      }
      //gets the x and y from the string
      xsize = Integer.parseInt(sizeStr.split(" ")[0]);
      ysize = Integer.parseInt(sizeStr.split(" ")[1]);
      //reads all the pixel values into a 2d array data structure
      int[][] p = new int[xsize][ysize];
      for(int y = 0;y <ysize;y++)
        for (int x = 0; x < xsize; x++) {

          p[x][y] = d.read();

        }
      pixels = p;


    } catch (FileNotFoundException e) {
       System.out.println("File specified cannot be found");
       e.printStackTrace();
    }
  }

  public void writePGM() {
    try {
      FileOutputStream f = new FileOutputStream(filename);
      DataOutputStream writeFile= new DataOutputStream(f);
      writeFile.writeBytes(format +  "\n");
      writeFile.writeBytes(sizeStr+ "\n" );
      writeFile.writeBytes(maxvalue + "\n");
       for(int x = 0;x <ysize;x++)
          for (int i = 0; i < xsize; i++) {
            writeFile.writeByte(this.getPixels()[i][x]);
          }

      System.out.println("Successfully wrote to the file " + filename + ".");
    } catch (IOException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
  }
}
