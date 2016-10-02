package com.example.ivanaclairine.thinningimage;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Ivana Clairine on 9/23/2016.
 */
public class ZhangSuen {

    public static final int black = -16711423;
    public static final int white = -1;
    int[][] pixels;
    int width, height;
    int changedPixels;

    //Buat jadi black and White pake rata rata RGB
    public ZhangSuen(Bitmap bitmap){
        preparation(bitmap);
    }

    //mengisi pixels[][], width, dan height (convert menjadi B&W)
    public void preparation(Bitmap bitmap){
        Bitmap BWbitmap =  toBlackAndWhite(bitmap);

        width = bitmap.getWidth();
        height = bitmap.getHeight();
        pixels = new int[width][height];

        for(int x=0; x<width; x++){
            for(int y = 0; y<height; y++){
                pixels[x][y] = BWbitmap.getPixel(x,y);
            }
        }
    }

    public void doStep1(){
        List<Integer> xChanged = new ArrayList<>();
        List<Integer> yChanged = new ArrayList<>();
        int indexList = 0;

        for(int x=1; x<width-1; x++){
            for(int y=1; y<height-1; y++){

                if(terms0(x,y) && terms1(x,y) && terms2(x,y) && terms3(x,y) && terms4(x,y))
                {
                    xChanged.add(indexList, x);
                    yChanged.add(indexList, y);
                    indexList++;
                }
            }
        }

        for(int i = 0; i<xChanged.size(); i++){
            int x = xChanged.get(i);
            int y = yChanged.get(i);

            pixels[x][y] = setColor(255);
            changedPixels++;
        }

    }

    public void doStep2(){
        List<Integer> xChanged = new ArrayList<>();
        List<Integer> yChanged = new ArrayList<>();
        int indexList = 0;

        for(int x=1; x<width-1; x++){
            for(int y=1; y<height-1; y++){

                if(terms0(x,y) && terms1(x,y) && terms2(x,y) && terms3Step2(x, y) && terms4Step2(x, y))
                {
                    xChanged.add(indexList, x);
                    yChanged.add(indexList, y);
                    indexList++;
                }
            }
        }

        for(int i = 0; i<xChanged.size(); i++){
            int x = xChanged.get(i);
            int y = yChanged.get(i);

            pixels[x][y] = setColor(255);
            changedPixels++;
        }
    }

    public Bitmap doSkeleton(){
        do{
            changedPixels = 0;
            doStep1();
            doStep2();
        }while(changedPixels != 0);

        int[] bitmapOutlined = convert2Dto1D(pixels);
        Bitmap retBitmap = Bitmap.createBitmap(bitmapOutlined, width, height, Bitmap.Config.ARGB_8888);

        return retBitmap;
    }

    //Step 1&2 - (0) --> The pixel is black and has eight neighbours
    public boolean terms0(int x, int y){
        if(pixels[x][y] == black && !isEdge(x, y))
            return true;

        return false;
    }

    //Step 1&2 - (1) --> 2 <= B(P1> <= 6
    public boolean terms1(int x, int y){
        int blackneighbor = countBlackNeighbor(x, y);
        if(blackneighbor >= 2 && blackneighbor <= 6){
            return true;
        }
        return false;
    }

    //Step 1&2 - (2) --> A(P1) = 1
    public boolean terms2(int x, int y){
        List<Integer> neighbors = getNeighbor(x, y);
        int count = 0;
        for(int i=2; i<10; i++){
            if(neighbors.get(i) == white && neighbors.get(i+1) == black){
                count++;
            }
        }

        if(count == 1)
            return true;

        return false;
    }

    //Step 1 - (3) --> At least one of P2 and P4 and P6 is white
    public boolean terms3(int x, int y){
        List<Integer> neighbors = getNeighbor(x, y);
        if(neighbors.get(2) == white || neighbors.get(4) == white || neighbors.get(6) == white)
            return true;

        return false;
    }

    //Step 1 - (4) -->  At least one of P4 and P6 and P8 is white
    public boolean terms4(int x, int y){
        List<Integer> neighbors = getNeighbor(x, y);
        if(neighbors.get(4) == white || neighbors.get(6) == white || neighbors.get(8) == white)
            return true;

        return false;
    }

    //Step 2 - (3) --> At least one of P2 and P4 and P6 is white
    public boolean terms3Step2(int x, int y){
        List<Integer> neighbors = getNeighbor(x, y);
        if(neighbors.get(2) == white || neighbors.get(4) == white || neighbors.get(8) == white)
            return true;

        return false;
    }

    //Step 2 - (4) -->  At least one of P4 and P6 and P8 is white
    public boolean terms4Step2(int x, int y){
        List<Integer> neighbors = getNeighbor(x, y);
        if(neighbors.get(2) == white || neighbors.get(6) == white || neighbors.get(8) == white)
            return true;

        return false;
    }

    //ambil tetangga sebuah pixel, indeks sesuai dengan arah; LIst[0] dan List[1] = warna pixels[x][y]
    public List<Integer> getNeighbor (int x, int y){
        List<Integer> neighbors = new ArrayList<>();

        //P2
        neighbors.add(0, pixels[x][y]);
        neighbors.add(1, pixels[x][y]);

        neighbors.add(2, pixels[x][y-1]);
        neighbors.add(3, pixels[x+1][y-1]);
        neighbors.add(4, pixels[x+1][y]);
        neighbors.add(5, pixels[x+1][y+1]);
        neighbors.add(6, pixels[x][y+1]);
        neighbors.add(7, pixels[x-1][y+1]);
        neighbors.add(8, pixels[x-1][y]);
        neighbors.add(9, pixels[x-1][y-1]);
        neighbors.add(10,pixels[x][y-1]);

        return neighbors;
    }

    public int countBlackNeighbor(int x, int y){
        int counter = 0;

        //cek P2
        if(pixels[x][y-1] == black){
            counter++;
        }

        //cek P3
        if(pixels[x+1][y-1] == black){
            counter++;
        }

        //cek P4
        if(pixels[x+1][y] == black){
            counter++;
        }

        //cek P5
        if(pixels[x+1][y+1] == black){
            counter++;
        }

        //cek P6
        if(pixels[x][y+1] == black){
            counter++;
        }

        //cek P7
        if(pixels[x-1][y+1] == black){
            counter++;
        }

        //cek P8
        if(pixels[x-1][y] == black){
            counter++;
        }

        //cek P9
        if(pixels[x-1][y-1] == black){
            counter++;
        }

        return counter;
    }

    public boolean isEdge(int x, int y){
        if(x == 0 || x == width-1){
            return true;
        }
        if(y == 0 || y == height-1){
            return true;
        }
        return false;
    }

    public Bitmap toBlackAndWhite(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        int[] pix = new int[width * height];
        bmpOriginal.getPixels(pix, 0, width, 0, 0, width, height);

        for (int y = 0; y < height; y++){
            for (int x = 0; x < width; x++)
            {
                int index = y * width + x;
                int Red = (pix[index] >> 16) & 0xff;     //bitwise shifting
                int Green = (pix[index] >> 8) & 0xff;
                int Blue = pix[index] & 0xff;

                int average = (Red + Green + Blue) / 3;
                int color = 255;
                if(average < 128)
                {
                    color = 1;
                }

                pix[index] = setColor(color);
//                Log.d("pixels: ", String.valueOf(pix[index]));
            }}

        Bitmap bmpGrayscale = Bitmap.createBitmap(pix, width, height, Bitmap.Config.ARGB_8888);
        return bmpGrayscale;
    }

    public int setColor(int color){
        return 0xff000000 | (color << 16) | (color << 8) | color;
    }

    public int[] convert2Dto1D (int[][] input){
        int[] retArray = new int[width*height];
        int indeks = 0;

        for(int i=0; i<height; i++){
            for(int j=0; j< width; j++){
                retArray[indeks] = pixels[j][i];
                indeks++;
            }
        }
        return retArray;
    }
}
