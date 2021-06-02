//Coursework(Finding Squares)
//James Megenis (c1729929)
import java.io.*;
import java.util.*;
public class FindingSquares {
  static LinkedList<Integer> roeSquares = new LinkedList<Integer>();
  static LinkedList<Integer> thetaSquares = new LinkedList<Integer>();
  static LinkedList<Integer> roeHighlights = new LinkedList<Integer>();
  static LinkedList<Integer> thetaHighlights= new LinkedList<Integer>();

    public static void main(String[] args) throws FileNotFoundException, IOException{

      Image originalimage = null;
      int targetlength =0;
      double f1 = 0;
      double f2 = 0;
      
      
      int numberofsizes=0;
      LinkedList<Integer> sizes= new LinkedList<Integer>();
      //catches missing args
      //parses args into variables
      try{

          originalimage = new Image(args[0]);
          targetlength = Integer.parseInt(args[1]);
          f1 = Double.parseDouble(args[2]);
          f2 = Double.parseDouble(args[3]);
          numberofsizes = Integer.parseInt(args[4]);

      }
      catch(ArrayIndexOutOfBoundsException exception) {
        System.out.println("Missing Arguements(This will pop up if you havent chosen to identify more than 1 square size, adding 1 as a last arg will remove it)");
      }

      sizes.add(targetlength);
      //numberofsizes is the amount of different sized squares the program will identify
      if (numberofsizes >1){
        System.out.println("second command arguement will be counted");
        for (int i = 0;i < numberofsizes-1;i++){
          System.out.println("please enter next square size");
          Scanner in = new Scanner(System.in);
          sizes.add(Integer.parseInt(in.nextLine()));

        }
      }


      double[][] kernels1 = createGaussianKernel(1);
      double[][] kernels2 = createGaussianKernel(2);
      Image SobelImage = sobel(originalimage);
      //Apply 2 guassian blurs through image convolution with a guassian
      Image Guassians1 = new Image(convoltion(originalimage,kernels1),originalimage.xsize,originalimage.ysize,"guassians1.pgm",originalimage.format,originalimage.sizeStr,originalimage.maxvalue);
      Image Guassians2 = new Image(convoltion(originalimage,kernels2),originalimage.xsize,originalimage.ysize,"guassians2.pgm",originalimage.format,originalimage.sizeStr,originalimage.maxvalue);
      //Difference of Guassian is the difference between the two blurs and will highlight edges
      Image DoG = new Image(differenceOfGuassian(Guassians1,Guassians2),originalimage.xsize,originalimage.ysize,"DoG.pgm",originalimage.format,originalimage.sizeStr,originalimage.maxvalue);
      //Apply 2 sobel filters to image before applying the guassian blur, this should highlight the edges more
      Image SobelGuassians1 = new Image(convoltion(SobelImage,kernels1),originalimage.xsize,originalimage.ysize,"guassians1.pgm",originalimage.format,originalimage.sizeStr,originalimage.maxvalue);
      Image SobelGuassians2 = new Image(convoltion(SobelImage,kernels2),originalimage.xsize,originalimage.ysize,"guassians2.pgm",originalimage.format,originalimage.sizeStr,originalimage.maxvalue);
      Image SobelDoG  = new Image(differenceOfGuassian(SobelGuassians1,SobelGuassians2),originalimage.xsize,originalimage.ysize,"SobelDoG.pgm",originalimage.format,originalimage.sizeStr,originalimage.maxvalue);

      //Writes the images to files, this is the operation that takes the longest in the running of this program.
      DoG.writePGM();
      SobelDoG.writePGM();
      HoughTransform(DoG,180,f1,originalimage,targetlength,f2,sizes).writePGM();

    }



    //takes in an image and a kernel (2D array)
    //returns the convoltion in 2D array form
    static int[][] convoltion(Image start, double[][]kernel){
      int[][] cv = new int[start.xsize][start.ysize];
      int sum;

      for(int y = 0; y < start.ysize -2;y++){
        for (int x = 0; x < start.xsize-2; x++) {
          sum = 0;
          for (int yy = 0; yy < 3; yy++)
              for (int xx = 0; xx < 3; xx++)
                  sum += kernel[xx][yy] * start.getPixels()[x+xx][y+yy];
                  cv[x+1][y+1] = (int)sum;
        }
      }
      return cv;
    }

    //Takes in an image type
    //returns an image type with the sobel applied.
    static Image sobel(Image im){
      double[][] sobelxkernal = new double[][] {{-1,0,1},{-1,0,1},{-1,0,1}};
      double[][] sobelykernal = new double[][] {{1,1,1},{0,0,0},{-1,-1,-1}};
      //applying a sobel
      int sobelpixels[][] = new int[im.xsize][im.ysize];
      int[][]sobelx = convoltion(im,sobelxkernal);
      int[][]sobely = convoltion(im,sobelykernal);

      for(int y = 0;y <im.ysize;y++){
        for (int x = 0; x < im.xsize; x++) {
          sobelpixels[x][y] = sobelx[x][y] + sobely[x][y];
        }
      }
      Image SobelImage = new Image(sobelpixels,im.xsize,im.ysize,"sobel.pgm",im.format,im.sizeStr,im.maxvalue);
      return SobelImage;
      }


    //takes in a double value for the sigma of the guassian
    //returns the 3x3 array of the kernel given the sigma value
    //all values in the kernel add to 1
    static double[][] createGaussianKernel(double sigma){
      double[][] kernel = new double[3][3];
      double r,s = 2* sigma * sigma;
      double sum =0;
      double val= 0;
      for (int x = -1;x <=1;x++ ){
        for (int y = -1;y <=1;y++ ){
          r = Math.sqrt(x*x + y*y);
          val = (Math.exp(-(r*r) / s)/Math.PI * s);
          kernel[x+1][y+1] = val;
          sum+=val;
        }
      }

      //rescaling the kernel
      for(int x =0;x <3;x++){
        for(int y =0;y <3;y++){
           kernel[x][y] /= sum;
         }
      }
       return kernel;
    }
    //Takes in two images
    //returns the difference betweent the two images rescaled in the 0-255 range
    static int[][] differenceOfGuassian(Image g1, Image g2){

      int[][] DoGpixels = new int[g1.xsize][g1.ysize];

      int a = 0;
      int maxvalue = 0;
      for(int y = 0;y <g1.ysize;y++){
        for (int x = 0; x < g1.xsize; x++) {
          a = g2.getPixels()[x][y]-g1.getPixels()[x][y];
          if (a> maxvalue){
            maxvalue = a;
          }
          if (a < 0){
            DoGpixels[x][y] = 0;

          }else{
            DoGpixels[x][y] = a;
          }
        }
      }
      //rescaling to 0-255
      double f = 255 / maxvalue ;
      for(int y = 0;y <g1.ysize;y++){
        for (int x = 0; x < g1.xsize; x++) {
          double p = DoGpixels[x][y]  * f;
          DoGpixels[x][y] =(int) p;
        }
      }
      return DoGpixels;
    }

    //t
    //function is not made using good practices as it peforms multiple operatio
    //returns the hough transform in image type
    static Image HoughTransform(Image im, int mt , Double f1, Image Original,int targetlength,Double f2,LinkedList<Integer> sizes){
      //initialising
      int maxTheta = mt;
      int maxvalue = 0;
      int maxRoe= (int)(Math.sqrt(2) * Math.max(im.ysize, im.xsize)) ;

      int[][] houghSpace = new int[maxRoe][maxTheta]; // if sizes are changed need to change the rescaling below and image creation
      int centerX = im.xsize / 2;
      int centerY = im.ysize / 2;

      //for finding how many points were cast into the hough space
      int numPoints = 0; //mainly used for testing how ways of reducing votes cast into the hough space
      //caching values of sin and cos to improve effeciency
      double[] sinCache = new double[maxTheta];
      double[] cosCache = new double[maxTheta];
      for (int t = 0;t<maxTheta;t++){
        sinCache[t] = Math.sin(Math.toRadians(t));
        cosCache[t] = Math.cos(Math.toRadians(t));
      }

      //find points to convert to hough space
      for (int x= 0;x<im.xsize;x++){
        for (int y = 0;y<im.ysize;y++){
          if (im.getPixels()[x][y] != 0){
            //adding a pixel to the hough space
            for (int t =0;t < maxTheta;t++){ //for all values of theta we can find the corresponding roe
              //Find the r values for each theta step
              int r = (int)(((x - centerX) * cosCache[t]) + ((y - centerY) * sinCache[t]));
              r += maxRoe/2;
              if (r > 0 && r<maxRoe){
                houghSpace[r][t]+= im.getPixels()[x][y]; //incrememnt the hough space by the value of the pixel in the image space. (Stronger borders will get more increment)

                if(houghSpace[r][t] > maxvalue){ //checking if there is a new max value from the hough space
                  maxvalue = houghSpace[r][t];
                }

              }
            }
            numPoints++; //increment the number of points from the image space that have been added to the hough space
          }
        }
      }

      //rescaling the hough space. And finding candidate peaks
      int rescaleVal =(int)(maxvalue / 255);
      LinkedList<Integer> candidatetheta = new LinkedList<Integer>();
      LinkedList<Integer> candidateroe = new LinkedList<Integer>();
      double minimumcandidatevalue = f1 * maxvalue;
      for (int r= 0;r<houghSpace.length;r++){
        for (int t = 0;t<houghSpace[0].length;t++){

          //finding candidates. Significantly reduces amount of time spend finding highlights in the hough space
          if (houghSpace[r][t] >= minimumcandidatevalue){
            candidatetheta.add(t);
            candidateroe.add(r);
          }
          //rescaling the hough space
          houghSpace[r][t] =(int) houghSpace[r][t] / rescaleVal;
        }
      }
      //prints out the number of candidate peaks with the current roe values

      //Finding / drawing lines(For visual purposes only)
      findHighlights(houghSpace,candidatetheta,candidateroe);
      writePPM(Original,thetaHighlights,roeHighlights,"lines.ppm","000 255 000");

      //Find squares
      FindSquares(thetaHighlights,roeHighlights,sizes,im.xsize,im.ysize,f2);
      writePPM(Original,thetaSquares,roeSquares,"squares.ppm","255 000 000");

      //returng the pixels as an image
      String sizeString =  maxRoe + " " + maxTheta;
      Image houghImage = new Image(houghSpace,maxRoe,maxTheta,"accumulator.pgm","P5",sizeString,"255");
      return houghImage;
      }


      //TODO What if 2 values in a 19x19 have the same value
      //takes in the hough space and generated candidatetheta and roe values and finds the 19x19 highlights
      //xsize and ysize are to draw the lines back onto the original image
      static void findHighlights(int[][]houghSpace,LinkedList<Integer>candidatetheta,LinkedList<Integer>candidateroe){
        int currentRoe = 0;
        int currentTheta = 0;
        int candidatesremoved =0;

        thetaloop:
        //for each roe and theta pair
        for(int i = 0; i < candidatetheta.size();i++){

          xloop:
          //looping through a 19x19 area around the candidate hardcoded 19x19 but not difficult to change
          for (int x = -9;x<9;x++){
            currentRoe = candidateroe.get(i) + x;
            if (currentRoe < 0 || currentRoe >=houghSpace.length){ //skips x if it can
              continue xloop;
            }

            yloop:
            for (int y = -9;y<9;y++){
              currentTheta = candidatetheta.get(i) + y;
              if (currentTheta < 0 || currentTheta >=houghSpace[0].length){ //skips y if it can
                continue yloop;
              }
              //If theres a value in a 19x19 area higher then go to next theta and dont add this candidate to the highlights
              if(houghSpace[currentRoe][currentTheta] > houghSpace[candidateroe.get(i)][candidatetheta.get(i)]){
                candidatesremoved++;
                continue thetaloop;
              }

            }
          }
          //roe and theta that make it through all of the loops added to the highlights
          roeHighlights.add(candidateroe.get(i));
          thetaHighlights.add(candidatetheta.get(i));
        }
      }

      //function to create an image object for the lines found in the hough space
      //takes in lists of candidate roe and theta values, and the x and y lenghts of the image space
      //Used for creating an image with found lines on before being used by writePPM
      static Image drawLines(LinkedList<Integer> thetaList, LinkedList<Integer>roeList,int xsize,int ysize,String filename ){
        int[][] lines = new int [xsize][ysize];
        int centerX = xsize / 2;
        int centerY = ysize / 2;

        int maxRoe= (int)(Math.sqrt(2) * Math.max(ysize, xsize));
        int theta =0;
        int roe = 0;
        int y = 0;
        for(int i = 0; i < thetaList.size();i++){
          for(int x = 0;x<xsize;x++){
            theta = thetaList.get(i);
            roe = roeList.get(i)-maxRoe/2;

            y = (int) ((roe / Math.sin(Math.toRadians(theta)) - ((x - centerX) * Math.cos(Math.toRadians(theta))) / Math.sin(Math.toRadians(theta))) + centerY);
            if (y<0 || y >= ysize){
              continue;
            }
              lines[x][y] = 255;
          }
        }
        //Have to do for both x and y to make sure lines have no gaps
        int x = 0;
        for(int i = 0; i < thetaList.size();i++){
          for(int yy = 0;yy<ysize;yy++){
            theta = thetaList.get(i);
            roe = roeList.get(i)-maxRoe/2;
            x = (int) ((roe / Math.cos(Math.toRadians(theta)) - ((yy - centerY) * Math.sin(Math.toRadians(theta))) / Math.cos(Math.toRadians(theta))) + centerX);
            if (x<0 || x >= xsize){
              continue;
            }
              lines[x][yy] = 255;
          }
        }
        String sizeStr = xsize + " " + ysize;
        Image linesImage = new Image(lines,xsize,ysize,filename,"P5",sizeStr,"255");
        return linesImage;
      }

      //Finds sets of parallel lines with distance = square size.
      // if two sets of parallel lines are perpendicular then there is a square of that size
      static void FindSquares(LinkedList<Integer> thetaList, LinkedList<Integer>roeList,LinkedList<Integer>sizes,int xsize,int ysize,double f2){
        LinkedList<Integer> roePairs = new LinkedList<Integer>();
        LinkedList<Integer> thetaPairs = new LinkedList<Integer>();
        LinkedList<Integer> confidencevals = new LinkedList<Integer>();
        for(int squaresize:sizes){//go through all of the different sized squares
        //loops through the thetalist twice if matching theta and roe are separated by squaresize then a parrallel line set has been found with distance = squaresize
          for(int i = 0; i<thetaList.size();i++){
            for(int j = i; j<thetaList.size();j++){
            //confidence values for i and j. Lower is better
              int ci = 0;
              int cj = 0;
              if (thetaList.get(i)>thetaList.get(j) - 10 &&thetaList.get(i)<thetaList.get(j) + 10){ //if two lines are nearly parallel
                ci =Math.abs(thetaList.get(i)-thetaList.get(j));
                cj = Math.abs(thetaList.get(i)-thetaList.get(j));
                int distance =roeList.get(j)-roeList.get(i) - squaresize;

                if (distance > -10 && distance < 10){ // if two lines are approx squaresize distance apart (arbitrary values +-10) 
                  confidencevals.add(ci + Math.abs(distance));
                  confidencevals.add(cj + Math.abs(distance));
                  roePairs.add(roeList.get(i));
                  thetaPairs.add(thetaList.get(i));
                  roePairs.add(roeList.get(j));
                  thetaPairs.add(thetaList.get(j));
                }
              }
            }
          }
      }
	//loops through all the sets of parallel lines, if two sets are perpendicular then there is a square. 
      for(int i =0;i<roePairs.size();i +=2){
        for(int j =i; j < roePairs.size();j+=2){
          if (Math.abs(thetaPairs.get(i) - thetaPairs.get(j)) >85 && Math.abs(thetaPairs.get(i) - thetaPairs.get(j)) <95){ //if two lines are approx perpendicular 85 - 95 degrees
            int ct = Math.abs(thetaPairs.get(i) - thetaPairs.get(j)-90);
            int confidence = confidencevals.get(i)+ ct+ confidencevals.get(i+1)+ ct + confidencevals.get(j)+ ct + confidencevals.get(j+1) + ct ;
            Double d2=Double.valueOf(confidence); //useful for finding correct confidence level
            d2 /=12;
            System.out.println(" Square found, confidence = "  + d2);
            if(d2 < f2){//if the average confidence in a square < f2 then add it
              roeSquares.add(roePairs.get(i));
              roeSquares.add(roePairs.get(i+1));
              roeSquares.add(roePairs.get(j));
              roeSquares.add(roePairs.get(j+1));
              thetaSquares.add(thetaPairs.get(i));
              thetaSquares.add(thetaPairs.get(i+1));
              thetaSquares.add(thetaPairs.get(j));
              thetaSquares.add(thetaPairs.get(j+1));

            }
          }
        }
      }
    }

      //writes an image in ppm format
      static void writePPM(Image im, LinkedList<Integer> thetaLines,LinkedList<Integer>roeLines,String filename,String val){
        //convert gray values into ppm
        String [][] values = new String[im.xsize][im.ysize];
        int[][]grayPixels = im.getPixels();
        int xsize = im.xsize;
        int ysize = im.ysize;

        for(int x = 0; x<xsize;x++){
          for (int y=0;y<ysize;y++){
            values[x][y] = grayPixels[x][y] + " " +grayPixels[x][y]+ " " + grayPixels[x][y] + " ";
          }
        }

        Image squares = drawLines(thetaLines,roeLines,im.xsize,im.ysize," ");
        for(int x = 0; x<xsize;x++){
          for (int y=0;y<ysize;y++){
            if (squares.getPixels()[x][y] != 0){
              values[x][y] = val;
            }
          }
        }
        String format = "p3";
        String sizeStr = xsize + " "+ysize;
        String maxvalue = "255";


        try {
           PrintWriter outfile = new PrintWriter(filename);
           outfile.println("P3");
           outfile.println(xsize + " " + ysize);
           outfile.println(maxvalue);
           for (int y=0; y<ysize; y++) {
               for(int x=0; x<xsize; x++) {
                 String str = values[x][y];
                 String[] arrOfStr = str.split(" ", 5);
                 for (String a : arrOfStr){
                    outfile.println(a);
                 }

               }
           }
           outfile.close();
       }
       catch (Exception e) {
       	   System.out.println(e.toString() + filename +" not written Successfully");
       	   e.printStackTrace();
       }
       System.out.println("Successfully wrote to the file " + filename + ".");
      }
  }
