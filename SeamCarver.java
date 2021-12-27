/* *****************************************************************************
 *  Name:    Mahmudul Rapi
 *  NetID:   mrapi
 *  Precept: P08
 *
 *  Description: Seam-carving is a content-aware image resizing technique where
 *  the image is reduced in size by one pixel of height (or width) at a time.
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;

public class SeamCarver {
    // maintain reference to picture operating on
    private Picture picture;
    // stores the width of the picture
    private int width;
    // stores the height of the picture
    private int height;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null)
            throw new IllegalArgumentException("Null argument");
        this.picture = new Picture(picture);
        width = picture.width();
        height = picture.height();
    }


    // current picture
    public Picture picture() {
        return new Picture(picture);
    }

    // width of current picture
    public int width() {
        return width;
    }

    // height of current picture
    public int height() {
        return height;
    }

    // computes the x-oriented color gradient for energy
    private double gradientSquareX(int x, int y) {
        int left;
        int right;
        if (x - 1 == -1)
            left = width - 1;
        else
            left = x - 1;
        if (x + 1 == width)
            right = 0;
        else
            right = x + 1;

        int rgbLeft = picture.getRGB(left, y);
        int rgbRight = picture.getRGB(right, y);

        return computeGradient(rgbLeft, rgbRight);
    }

    // computes the y-oriented color gradient for energy
    private double gradientSquareY(int x, int y) {
        int up;
        int down;
        if (y - 1 == -1)
            down = height - 1;
        else
            down = y - 1;
        if (y + 1 == height)
            up = 0;
        else
            up = y + 1;
        int rgbUp = picture.getRGB(x, up);
        int rgbDown = picture.getRGB(x, down);

        return computeGradient(rgbUp, rgbDown);
    }

    // computes gradient given RGB values of adjacent pixels
    private double computeGradient(int pixOne, int pixTwo) {

        int redY = ((pixOne >> 16) & 0xFF) - ((pixTwo >> 16) & 0xFF);
        int greenY = ((pixOne >> 8) & 0xFF) - ((pixTwo >> 8) & 0xFF);
        int blueY = ((pixOne >> 0) & 0xFF) - ((pixTwo >> 0) & 0xFF);

        double gradientSq = redY * redY + greenY * greenY + blueY * blueY;

        return gradientSq;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || x > width || y < 0 || y > height)
            throw new IllegalArgumentException("outside prescribed range");
        double energySquared = gradientSquareX(x, y) + gradientSquareY(x, y);
        return Math.sqrt(energySquared);
    }

    // relaxes a vertex if finds a closer path to it
    private void relax(int xFrom, int yFrom, int xTo, int yTo,
                       double[][] distTo, int[][] edgeTo, double[][] energyMatrix) {
        if (xTo == width || xTo == -1)
            return;
        double vWeight = energyMatrix[xTo][yTo];
        if (distTo[xTo][yTo] > distTo[xFrom][yFrom] + vWeight) {
            distTo[xTo][yTo] = distTo[xFrom][yFrom] + vWeight;
            edgeTo[xTo][yTo] = xFrom;
        }
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {

        double[][] energyMatrix = new double[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                energyMatrix[i][j] = energy(i, j);
            }
        }
        int[] path = new int[height];
        double[][] distTo = new double[width][height];
        int[][] edgeTo = new int[width][height];

        // initialize top to energy values, else to infinity, and then relax
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (row == 0) distTo[col][0] = energyMatrix[col][0];
                else distTo[col][row] = Double.POSITIVE_INFINITY;
            }
        }
        for (int vy = 0; vy < height - 1; vy++) {
            for (int vx = 0; vx < width; vx++) {
                relax(vx, vy, vx - 1, vy + 1, distTo, edgeTo, energyMatrix);
                relax(vx, vy, vx, vy + 1, distTo, edgeTo, energyMatrix);
                relax(vx, vy, vx + 1, vy + 1, distTo, edgeTo, energyMatrix);
            }
        }

        // find minimum distTo value in last row, then traverse backwards
        double min = distTo[0][height - 1];
        int y = 0;
        for (int i = 1; i < width; i++) {
            if (distTo[i][height - 1] < min) {
                min = distTo[i][height - 1];
                y = i;
            }
        }

        int rowNumber = height - 1;
        path[rowNumber] = y;
        while (rowNumber > 0) {
            path[rowNumber - 1] = edgeTo[path[rowNumber]][rowNumber];
            rowNumber--;
        }

        return path;
    }

    // transposes picture and associated energy function in order to simplify code
    private void transpose() {
        Picture transposedPic = new Picture(height, width);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                transposedPic.setRGB(j, i, picture.getRGB(i, j));
            }
        }

        int temp = width;
        width = height;
        height = temp;
        picture = transposedPic;
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        transpose();
        int[] path = findVerticalSeam();
        transpose(); // transpose of transpose reverts back matrix
        return path;
    }

    // checks if conditions for removing seam are met
    private void seamChecker(int[] seam, boolean horizontal) {
        if (seam == null)
            throw new IllegalArgumentException("Null argument");
        if (horizontal && seam.length != width)
            throw new IllegalArgumentException("Seam of length different from width");
        if (!horizontal && seam.length != height)
            throw new IllegalArgumentException("Seam of length different from height");
        if (picture.height() == 1 && horizontal)
            throw new IllegalArgumentException("Only 1 pixel in dimension");
        if (picture.width() == 1 && !horizontal)
            throw new IllegalArgumentException("Only 1 pixel in dimension");
        for (int i = 0; i < seam.length - 1; i++) {
            if (Math.abs(seam[i + 1] - seam[i]) > 1)
                throw new IllegalArgumentException(
                        "Successive entries in seam[] must differ by -1, 0, or +1");
        }
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        seamChecker(seam, true);
        transpose();
        removeVerticalSeam(seam);
        transpose();
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        seamChecker(seam, false);
        Picture removedSeam = new Picture(width - 1, height);
        for (int j = 0; j < height; j++) {
            int skip = seam[j];
            int skipIncrement = 0;
            for (int i = 0; i + skipIncrement < width; i++) {
                if (i == skip) {
                    skipIncrement = 1;
                    if (i == width - 1)
                        break;
                }
                removedSeam.setRGB(i, j, picture.getRGB(i + skipIncrement, j));
            }
        }

        picture = removedSeam;
        width--;
    }


    //  unit testing (required)
    public static void main(String[] args) {
        // Test energy function and seam operations on 3x4.png
        SeamCarver seam1 = new SeamCarver(new Picture("3x4.png"));
        StdOut.println("Energy at each pixel of 3x4.png: ");
        for (int j = 0; j < seam1.height(); j++) {
            for (int i = 0; i < seam1.width(); i++) {
                StdOut.printf("%7.2f ", seam1.energy(i, j));
            }
            StdOut.println();
        }
        int[] findVerSeam1 = seam1.findVerticalSeam();
        int[] findHorSeam1 = seam1.findHorizontalSeam();
        StdOut.println("Find vertical seam: ");
        for (int i : findVerSeam1) {
            StdOut.print(i + " ");
        }
        StdOut.println();
        StdOut.println("Find horizontal seam: ");
        for (int i : findHorSeam1) {
            StdOut.print(i + " ");
        }
        StdOut.println();
        int[] verSeamRandom = { 0, 1, 2, 2 };
        seam1.removeVerticalSeam(verSeamRandom);
        StdOut.println("Energy at each pixel after removing vertical seam: ");
        for (int j = 0; j < seam1.height(); j++) {
            for (int i = 0; i < seam1.width(); i++) {
                StdOut.printf("%7.2f ", seam1.energy(i, j));
            }
            StdOut.println();
        }
        int[] horSeamRandom = { 1, 2 };
        seam1.removeHorizontalSeam(horSeamRandom);
        StdOut.println("Energy at each pixel after removing horizontal seam: ");
        for (int j = 0; j < seam1.height(); j++) {
            for (int i = 0; i < seam1.width(); i++) {
                StdOut.printf("%7.2f ", seam1.energy(i, j));
            }
            StdOut.println();
        }

        // Test energy function and seam operations on 12x10.png
        SeamCarver seam2 = new SeamCarver(new Picture("12x10.png"));
        StdOut.println("Energy at each pixel of 12x10.png: ");
        for (int j = 0; j < seam2.height(); j++) {
            for (int i = 0; i < seam2.width(); i++) {
                StdOut.printf("%7.2f ", seam2.energy(i, j));
            }
            StdOut.println();
        }
        int[] findVerSeam2 = seam2.findVerticalSeam();
        int[] findHorSeam2 = seam2.findHorizontalSeam();
        StdOut.println("Find vertical seam: ");
        for (int i : findVerSeam2) {
            StdOut.print(i + " ");
        }
        StdOut.println();
        StdOut.println("Find horizontal seam: ");
        for (int i : findHorSeam2) {
            StdOut.print(i + " ");
        }
        StdOut.println();

        // Test effect on a picture of a chameleon (given in folder)
        SeamCarver chameleon = new SeamCarver(new Picture("chameleon.png"));
        for (int i = 0; i < 75; i++) {
            int[] hSeam = chameleon.findHorizontalSeam();
            chameleon.removeHorizontalSeam(hSeam);
            int[] vSeam = chameleon.findVerticalSeam();
            chameleon.removeVerticalSeam(vSeam);
        }
        Picture chameleonSeamed = chameleon.picture();
        chameleonSeamed.show();
    }


}

