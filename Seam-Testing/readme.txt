/* *****************************************************************************
 *  Name: Mahmudul Rapi
 *  NetID: mrapi
 *  Precept: P08
 *
 *  Partner Name:    N/A
 *  Partner NetID:   N/A
 *  Partner Precept: N/A
 *
 *  Hours to complete assignment (optional): 14
 *
 **************************************************************************** */

Programming Assignment 7: Seam Carving


/* *****************************************************************************
 *  Describe concisely your algorithm to find a horizontal (or vertical)
 *  seam.
 **************************************************************************** */
I used more of a dynamic programming approach to this assignment. To find a
vertical seam, I initialized the first row of distTo[][]  to contain their energy
matrix values (since they point indirectly from a virtual top source pixel of
distance 0), and then initialize all other vertices distTo as infinity. For for
each vertex, using a helper relax function, I wanted to relax each edge incident
to it by seeing if the distance to that vertex (by adding the previous pixel
energy) is smaller than its current distTo value. To apply the operations in the
horizontal direction, I used a cool trick discussed in EdLesson of taking the
transpose of the energy matrix and applying the vertical operation.

/* *****************************************************************************
 *  Describe what makes an image suitable to the seam-carving approach
 *  (in terms of preserving the content and structure of the original
 *  image, without introducing visual artifacts). Describe an image that
 *  would not work well.
 **************************************************************************** */
Images of textures or a background scene seem to work best with seam-carving,
most likely because these elements are typically always prevalent in a picture so
reducing the "amount of background" we see will still preserves the content
of the images.
Images that contain shapes or faces would probably not work out too well, since
these elements typically need to be preserved but seaming them would distort the
shapes and faces (unless we implemented some sort of preservation function that
doesn't just look at the energy functions).

/* *****************************************************************************
 *  Perform computational experiments to estimate the running time to reduce
 *  a W-by-H image by one column and one row (i.e., one call each to
 *  findVerticalSeam(), removeVerticalSeam(), findHorizontalSeam(), and
 *  removeHorizontalSeam()). Use a "doubling" hypothesis, where you
 *  successively increase either W or H by a constant multiplicative
 *  factor (not necessarily 2).
 *
 *  To do so, fill in the two tables below. Each table must have 5-10
 *  data points, ranging in time from around 0.25 seconds for the smallest
 *  data point to around 30 seconds for the largest one.
 **************************************************************************** */

(keep W constant)
 W = 2000
 multiplicative factor (for H) = 2

 H           time (seconds)      ratio       log ratio
------------------------------------------------------
5            0.26
10           0.575              2.211        1.145
20           1.18               2.052        1.037
40           2.475              2.097        1.068
80           4.348              1.756        0.813
160          8.8                2.024        1.017
320          20.756             2.359        1.228

(keep H constant)
 H = 2000
 multiplicative factor (for W) = 2

 W           time (seconds)      ratio       log ratio
------------------------------------------------------
5             0.273
10            0.597              2.186       1.128
20            1.154              1.932       0.95
40            2.104              1.823       0.866
80            4.224              2.008       1.01
160           8.744              2.070       1.05
320           20.04              2.29        0.882



/* *****************************************************************************
 *  Using the empirical data from the above two tables, give a formula
 *  (using tilde notation) for the running time (in seconds) as a function
 *  of both W and H, such as
 *
 *       ~ 5.3*10^-8 * W^5.1 * H^1.5
 *
 *  Briefly explain how you determined the formula for the running time.
 *  Recall that with tilde notation, you include both the coefficient
 *  and exponents of the leading term (but not lower-order terms).
 *  Round each coefficient and exponent to two significant digits.
 **************************************************************************** */
Based on the log ratios of the width and the height variables, both of them
seem to grow linearly (exponent of 1.0) based on my timing results. Therefore,
to solve for the leading coefficient, I can simply take various times and divide
it by the product of W^1.0 * H^1.0.

Using time = 8.777 seconds for H = 2000, W = 160, I get my constant to be
8.777 / (2000 * 160) = 2.74 * 10^-5
For time = 8.8 for H = 160, W = 2000, I get my constant to be:
8.8 / (40 * 2000) = 2.75 * 10^-5

Therefore, the constant seems to be about 2.7 * 10^-5.

Running time (in seconds) to find and remove one horizontal seam and one
vertical seam, as a function of both W and H:


    ~ 2.7*10^-5 * W^1.0 * H^1.0
       _______________________________________


/* *****************************************************************************
 *  Known bugs / limitations.
 **************************************************************************** */


/* *****************************************************************************
 *  Describe whatever help (if any) that you received.
 *  Don't include readings, lectures, and precepts, but do
 *  include any help from people (including course staff, lab TAs,
 *  classmates, and friends) and attribute them by name.
 **************************************************************************** */


/* *****************************************************************************
 *  Describe any serious problems you encountered.
 **************************************************************************** */


/* *****************************************************************************
 *  If you worked with a partner, assert below that you followed
 *  the protocol as described on the assignment page. Give one
 *  sentence explaining what each of you contributed.
 **************************************************************************** */


/* *****************************************************************************
 *  List any other comments here. Feel free to provide any feedback
 *  on how much you learned from doing the assignment, and whether
 *  you enjoyed doing it.
 **************************************************************************** */
Overall such a cool assignment, seeing the application of a shortest path
algorithm to picture manipulation.
