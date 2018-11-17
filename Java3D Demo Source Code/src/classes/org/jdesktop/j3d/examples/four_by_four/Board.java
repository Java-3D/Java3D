/*
 * $RCSfile: Board.java,v $
 *
 * Copyright (c) 2007 Sun Microsystems, Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistribution of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in
 *   the documentation and/or other materials provided with the
 *   distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF
 * USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR
 * ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed, licensed or
 * intended for use in the design, construction, operation or
 * maintenance of any nuclear facility.
 *
 * $Revision: 1.2 $
 * $Date: 2007/02/09 17:21:37 $
 * $State: Exp $
 */

package org.jdesktop.j3d.examples.four_by_four;

import java.awt.*;

/**
 *  Class:       Board
 *
 *  Description: Handles all logic with respect to play. Also renders
 *               the 2D window.
 *
 *  Version:     1.1
 *
 */
class Board {

   final static int UNOCCUPIED = 0;
   final static int HUMAN      = 1;
   final static int MACHINE    = 2;
   final static int END        = 3;

   private int[]      moves;
   private int[]      occupied;
   private int[][]    combinations;
   private int[][]    outside_four;
   private int[][]    inside_four;
   private int[][]    faces;
   private int[][]    pos_to_comb;
   private int[][]    best_picks;
   private int        num_points;
   private int        num_balls;
   private int        num_polygons;
   private int        num_pt_indexes;
   private int        num_normal_indexes;
   private int        pt_start;
   private int        color_index;
   private int        width;
   private int        height;
   private int        center_x;
   private int        center_y;
   private int        player;
   private int        skill_level;
   private int        outside_four_index;
   private int        inside_four_index;
   private int        face_index;
   private int        nmoves;
   private int        current_face;
   private int        min = 1000;
   private int        max = 0;
   private long[]     sort_array;
   private long       time;
   private long       beg_time;
   private long       end_time;
   private Color[]    color_ramp;
   private Color      background;
   private Color      label_color;
   private Color      red;
   private Color      blue;
   private Color      white;
   private Color      gray;
   private Color      yellow;
   private double     max_dist;
   private FourByFour panel;
   private boolean    debug;
   private boolean    outside_four_flag;
   private boolean    inside_four_flag;
   private boolean    face_flag;
   private boolean    label_flag;
   private boolean    block_chair_flag;
   private boolean    undoFlag;
   private boolean[]  highlight;
   private int        block_chair_next_move;
   private int        block_chair_face;
   private Positions  positions;
   private Canvas2D   canvas;

   Board (FourByFour panel, Positions positions, int width, int height) {

      // Set the debug state.
      debug = false;

      // Store arguments
      this.width = width;
      this.height = height;
      this.panel = panel;
      this.positions = positions;

      // Initialize flags
      label_flag = false;
      outside_four_flag = false;
      inside_four_flag = false;
      block_chair_flag = false;
      undoFlag = false;

      // Total number of board positions.
      num_points = 64;

      // Allocate the logic arrays.
      moves = new int[64];
      occupied = new int[64];
      combinations = new int[76][7];
      outside_four = new int[18][6];
      inside_four = new int[18][6];
      faces = new int[18][18];
      pos_to_comb = new int[64][8];
      best_picks = new int[64][8];
      highlight = new boolean[18];

      // Initialize the logic arrays.
      init_combinations();
      init_faces();
      init_outside_four();
      init_inside_four();

      // Set the player with the first move. 
      player = HUMAN;

      // Set the default skill level.
      skill_level = 4;

      // Initialize the number of moves.
      nmoves = 0;

      // Define colors
      background = new Color(13, 13, 51);
      red = new Color(230, 26, 51);
      blue = new Color(51, 51, 230);
      white = new Color(255, 255, 255);
      gray = new Color(240, 240, 240);
      yellow = new Color(240, 240, 0);

      // Record the start time
      beg_time = System.currentTimeMillis();
   }

   public void setCanvas(Canvas2D canvas) {
      this.canvas = canvas;
   }

   public void init_combinations () {

      // The combination array contains all possible winning combinations.
      //
      // Each combination has the following format:
      //
      // combinations[x][0] =  status: 0      = no player has selected positons in this row
      //                              -1      = both players have men in this row
      //                               1 to 4 = number of positions occupied by player
      //
      // combinations[x][1] =  player who owns this row (valid only if status = 1-4)
      // combinations[x][2] =  postion that define the row 
      // combinations[x][3] =  postion that define the row 
      // combinations[x][4] =  postion that define the row 
      // combinations[x][5] =  postion that define the row 

      // Horizontal, Z

      combinations[ 0][0] =  0;  combinations[ 1][0] =  0;  combinations[ 2][0] =  0;  combinations[ 3][0] =  0;
      combinations[ 0][1] =  0;  combinations[ 1][1] =  0;  combinations[ 2][1] =  0;  combinations[ 3][1] =  0;
      combinations[ 0][2] =  0;  combinations[ 1][2] =  4;  combinations[ 2][2] =  8;  combinations[ 3][2] = 12;
      combinations[ 0][3] =  1;  combinations[ 1][3] =  5;  combinations[ 2][3] =  9;  combinations[ 3][3] = 13;
      combinations[ 0][4] =  2;  combinations[ 1][4] =  6;  combinations[ 2][4] = 10;  combinations[ 3][4] = 14;
      combinations[ 0][5] =  3;  combinations[ 1][5] =  7;  combinations[ 2][5] = 11;  combinations[ 3][5] = 15;

      combinations[ 4][0] =  0;  combinations[ 5][0] =  0;  combinations[ 6][0] =  0;  combinations[ 7][0] =  0;
      combinations[ 4][1] =  0;  combinations[ 5][1] =  0;  combinations[ 6][1] =  0;  combinations[ 7][1] =  0;
      combinations[ 4][2] = 16;  combinations[ 5][2] = 20;  combinations[ 6][2] = 24;  combinations[ 7][2] = 28;
      combinations[ 4][3] = 17;  combinations[ 5][3] = 21;  combinations[ 6][3] = 25;  combinations[ 7][3] = 29;
      combinations[ 4][4] = 18;  combinations[ 5][4] = 22;  combinations[ 6][4] = 26;  combinations[ 7][4] = 30;
      combinations[ 4][5] = 19;  combinations[ 5][5] = 23;  combinations[ 6][5] = 27;  combinations[ 7][5] = 31;

      combinations[ 8][0] =  0;  combinations[ 9][0] =  0;  combinations[10][0] =  0;  combinations[11][0] =  0;
      combinations[ 8][1] =  0;  combinations[ 9][1] =  0;  combinations[10][1] =  0;  combinations[11][1] =  0;
      combinations[ 8][2] = 32;  combinations[ 9][2] = 36;  combinations[10][2] = 40;  combinations[11][2] = 44;
      combinations[ 8][3] = 33;  combinations[ 9][3] = 37;  combinations[10][3] = 41;  combinations[11][3] = 45;
      combinations[ 8][4] = 34;  combinations[ 9][4] = 38;  combinations[10][4] = 42;  combinations[11][4] = 46;
      combinations[ 8][5] = 35;  combinations[ 9][5] = 39;  combinations[10][5] = 43;  combinations[11][5] = 47;

      combinations[12][0] =  0;  combinations[13][0] =  0;  combinations[14][0] =  0;  combinations[15][0] =  0;
      combinations[12][1] =  0;  combinations[13][1] =  0;  combinations[14][1] =  0;  combinations[15][1] =  0;
      combinations[12][2] = 48;  combinations[13][2] = 52;  combinations[14][2] = 56;  combinations[15][2] = 60;
      combinations[12][3] = 49;  combinations[13][3] = 53;  combinations[14][3] = 57;  combinations[15][3] = 61;
      combinations[12][4] = 50;  combinations[13][4] = 54;  combinations[14][4] = 58;  combinations[15][4] = 62;
      combinations[12][5] = 51;  combinations[13][5] = 55;  combinations[14][5] = 59;  combinations[15][5] = 63;

      // Vertical, Z

      combinations[16][0] =  0;  combinations[17][0] =  0;  combinations[18][0] =  0;  combinations[19][0] =  0;
      combinations[16][1] =  0;  combinations[17][1] =  0;  combinations[18][1] =  0;  combinations[19][1] =  0;
      combinations[16][2] =  0;  combinations[17][2] =  1;  combinations[18][2] =  2;  combinations[19][2] =  3;
      combinations[16][3] =  4;  combinations[17][3] =  5;  combinations[18][3] =  6;  combinations[19][3] =  7;
      combinations[16][4] =  8;  combinations[17][4] =  9;  combinations[18][4] = 10;  combinations[19][4] = 11;
      combinations[16][5] = 12;  combinations[17][5] = 13;  combinations[18][5] = 14;  combinations[19][5] = 15;
 
      combinations[20][0] =  0;  combinations[21][0] =  0;  combinations[22][0] =  0;  combinations[23][0] =  0;
      combinations[20][1] =  0;  combinations[21][1] =  0;  combinations[22][1] =  0;  combinations[23][1] =  0;
      combinations[20][2] = 16;  combinations[21][2] = 17;  combinations[22][2] = 18;  combinations[23][2] = 19;
      combinations[20][3] = 20;  combinations[21][3] = 21;  combinations[22][3] = 22;  combinations[23][3] = 23;
      combinations[20][4] = 24;  combinations[21][4] = 25;  combinations[22][4] = 26;  combinations[23][4] = 27;
      combinations[20][5] = 28;  combinations[21][5] = 29;  combinations[22][5] = 30;  combinations[23][5] = 31;
 
      combinations[24][0] =  0;  combinations[25][0] =  0;  combinations[26][0] =  0;  combinations[27][0] =  0;
      combinations[24][1] =  0;  combinations[25][1] =  0;  combinations[26][1] =  0;  combinations[27][1] =  0;
      combinations[24][2] = 32;  combinations[25][2] = 33;  combinations[26][2] = 34;  combinations[27][2] = 35;
      combinations[24][3] = 36;  combinations[25][3] = 37;  combinations[26][3] = 38;  combinations[27][3] = 39;
      combinations[24][4] = 40;  combinations[25][4] = 41;  combinations[26][4] = 42;  combinations[27][4] = 43;
      combinations[24][5] = 44;  combinations[25][5] = 45;  combinations[26][5] = 46;  combinations[27][5] = 47;
 
      combinations[28][0] =  0;  combinations[29][0] =  0;  combinations[30][0] =  0;  combinations[31][0] =  0;
      combinations[28][1] =  0;  combinations[29][1] =  0;  combinations[30][1] =  0;  combinations[31][1] =  0;
      combinations[28][2] = 48;  combinations[29][2] = 49;  combinations[30][2] = 50;  combinations[31][2] = 51;
      combinations[28][3] = 52;  combinations[29][3] = 53;  combinations[30][3] = 54;  combinations[31][3] = 55;
      combinations[28][4] = 56;  combinations[29][4] = 57;  combinations[30][4] = 58;  combinations[31][4] = 59;
      combinations[28][5] = 60;  combinations[29][5] = 61;  combinations[30][5] = 62;  combinations[31][5] = 63;
 
      // Diagonal, Z

      combinations[32][0] =  0;  combinations[33][0] =  0;  combinations[34][0] =  0;  combinations[35][0] =  0;
      combinations[32][1] =  0;  combinations[33][1] =  0;  combinations[34][1] =  0;  combinations[35][1] =  0;
      combinations[32][2] =  0;  combinations[33][2] = 16;  combinations[34][2] = 32;  combinations[35][2] = 48;
      combinations[32][3] =  5;  combinations[33][3] = 21;  combinations[34][3] = 37;  combinations[35][3] = 53;
      combinations[32][4] = 10;  combinations[33][4] = 26;  combinations[34][4] = 42;  combinations[35][4] = 58;
      combinations[32][5] = 15;  combinations[33][5] = 31;  combinations[34][5] = 47;  combinations[35][5] = 63;
 
      combinations[36][0] =  0;  combinations[37][0] =  0;  combinations[38][0] =  0;  combinations[39][0] =  0;
      combinations[36][1] =  0;  combinations[37][1] =  0;  combinations[38][1] =  0;  combinations[39][1] =  0;
      combinations[36][2] =  3;  combinations[37][2] = 19;  combinations[38][2] = 35;  combinations[39][2] = 51;
      combinations[36][3] =  6;  combinations[37][3] = 22;  combinations[38][3] = 38;  combinations[39][3] = 54;
      combinations[36][4] =  9;  combinations[37][4] = 25;  combinations[38][4] = 41;  combinations[39][4] = 57;
      combinations[36][5] = 12;  combinations[37][5] = 28;  combinations[38][5] = 44;  combinations[39][5] = 60;
 
      // Horizontal, X

      combinations[40][0] =  0;  combinations[41][0] =  0;  combinations[42][0] =  0;  combinations[43][0] =  0;
      combinations[40][1] =  0;  combinations[41][1] =  0;  combinations[42][1] =  0;  combinations[43][1] =  0;
      combinations[40][2] = 51;  combinations[41][2] = 55;  combinations[42][2] = 59;  combinations[43][2] = 63;
      combinations[40][3] = 35;  combinations[41][3] = 39;  combinations[42][3] = 43;  combinations[43][3] = 47;
      combinations[40][4] = 19;  combinations[41][4] = 23;  combinations[42][4] = 27;  combinations[43][4] = 31;
      combinations[40][5] =  3;  combinations[41][5] =  7;  combinations[42][5] = 11;  combinations[43][5] = 15;
 
      combinations[44][0] =  0;  combinations[45][0] =  0;  combinations[46][0] =  0;  combinations[47][0] =  0;
      combinations[44][1] =  0;  combinations[45][1] =  0;  combinations[46][1] =  0;  combinations[47][1] =  0;
      combinations[44][2] = 50;  combinations[45][2] = 54;  combinations[46][2] = 58;  combinations[47][2] = 62;
      combinations[44][3] = 34;  combinations[45][3] = 38;  combinations[46][3] = 42;  combinations[47][3] = 46;
      combinations[44][4] = 18;  combinations[45][4] = 22;  combinations[46][4] = 26;  combinations[47][4] = 30;
      combinations[44][5] =  2;  combinations[45][5] =  6;  combinations[46][5] = 10;  combinations[47][5] = 14;
 
      combinations[48][0] =  0;  combinations[49][0] =  0;  combinations[50][0] =  0;  combinations[51][0] =  0;
      combinations[48][1] =  0;  combinations[49][1] =  0;  combinations[50][1] =  0;  combinations[51][1] =  0;
      combinations[48][2] = 49;  combinations[49][2] = 53;  combinations[50][2] = 57;  combinations[51][2] = 61;
      combinations[48][3] = 33;  combinations[49][3] = 37;  combinations[50][3] = 41;  combinations[51][3] = 45;
      combinations[48][4] = 17;  combinations[49][4] = 21;  combinations[50][4] = 25;  combinations[51][4] = 29;
      combinations[48][5] =  1;  combinations[49][5] =  5;  combinations[50][5] =  9;  combinations[51][5] = 13;
 
      combinations[52][0] =  0;  combinations[53][0] =  0;  combinations[54][0] =  0;  combinations[55][0] =  0;
      combinations[52][1] =  0;  combinations[53][1] =  0;  combinations[54][1] =  0;  combinations[55][1] =  0;
      combinations[52][2] = 48;  combinations[53][2] = 52;  combinations[54][2] = 56;  combinations[55][2] = 60;
      combinations[52][3] = 32;  combinations[53][3] = 36;  combinations[54][3] = 40;  combinations[55][3] = 44;
      combinations[52][4] = 16;  combinations[53][4] = 20;  combinations[54][4] = 24;  combinations[55][4] = 28;
      combinations[52][5] =  0;  combinations[53][5] =  4;  combinations[54][5] =  8;  combinations[55][5] = 12;
 
      // Diagonal, X

      combinations[56][0] =  0;  combinations[57][0] =  0;  combinations[58][0] =  0;  combinations[59][0] =  0;
      combinations[56][1] =  0;  combinations[57][1] =  0;  combinations[58][1] =  0;  combinations[59][1] =  0;
      combinations[56][2] = 51;  combinations[57][2] = 50;  combinations[58][2] = 49;  combinations[59][2] = 48;
      combinations[56][3] = 39;  combinations[57][3] = 38;  combinations[58][3] = 37;  combinations[59][3] = 36;
      combinations[56][4] = 27;  combinations[57][4] = 26;  combinations[58][4] = 25;  combinations[59][4] = 24;
      combinations[56][5] = 15;  combinations[57][5] = 14;  combinations[58][5] = 13;  combinations[59][5] = 12;
 
      combinations[60][0] =  0;  combinations[61][0] =  0;  combinations[62][0] =  0;  combinations[63][0] =  0;
      combinations[60][1] =  0;  combinations[61][1] =  0;  combinations[62][1] =  0;  combinations[63][1] =  0;
      combinations[60][2] =  3;  combinations[61][2] =  2;  combinations[62][2] =  1;  combinations[63][2] =  0;
      combinations[60][3] = 23;  combinations[61][3] = 22;  combinations[62][3] = 21;  combinations[63][3] = 20;
      combinations[60][4] = 43;  combinations[61][4] = 42;  combinations[62][4] = 41;  combinations[63][4] = 40;
      combinations[60][5] = 63;  combinations[61][5] = 62;  combinations[62][5] = 61;  combinations[63][5] = 60;
 
      // Diagonal, Y

      combinations[64][0] =  0;  combinations[65][0] =  0;  combinations[66][0] =  0;  combinations[67][0] =  0;
      combinations[64][1] =  0;  combinations[65][1] =  0;  combinations[66][1] =  0;  combinations[67][1] =  0;
      combinations[64][2] = 63;  combinations[65][2] = 59;  combinations[66][2] = 55;  combinations[67][2] = 51;
      combinations[64][3] = 46;  combinations[65][3] = 42;  combinations[66][3] = 38;  combinations[67][3] = 34;
      combinations[64][4] = 29;  combinations[65][4] = 25;  combinations[66][4] = 21;  combinations[67][4] = 17;
      combinations[64][5] = 12;  combinations[65][5] =  8;  combinations[66][5] =  4;  combinations[67][5] =  0;
 
      combinations[68][0] =  0;  combinations[69][0] =  0;  combinations[70][0] =  0;  combinations[71][0] =  0;
      combinations[68][1] =  0;  combinations[69][1] =  0;  combinations[70][1] =  0;  combinations[71][1] =  0;
      combinations[68][2] = 15;  combinations[69][2] = 11;  combinations[70][2] =  7;  combinations[71][2] =  3;
      combinations[68][3] = 30;  combinations[69][3] = 26;  combinations[70][3] = 22;  combinations[71][3] = 18;
      combinations[68][4] = 45;  combinations[69][4] = 41;  combinations[70][4] = 37;  combinations[71][4] = 33;
      combinations[68][5] = 60;  combinations[69][5] = 56;  combinations[70][5] = 52;  combinations[71][5] = 48;
 
      // Corner to Corner

      combinations[72][0] =  0;  combinations[73][0] =  0;  combinations[74][0] =  0;  combinations[75][0] =  0;
      combinations[72][1] =  0;  combinations[73][1] =  0;  combinations[74][1] =  0;  combinations[75][1] =  0;
      combinations[72][2] =  0;  combinations[73][2] =  3;  combinations[74][2] = 12;  combinations[75][2] = 15;
      combinations[72][3] = 21;  combinations[73][3] = 22;  combinations[74][3] = 25;  combinations[75][3] = 26;
      combinations[72][4] = 42;  combinations[73][4] = 41;  combinations[74][4] = 38;  combinations[75][4] = 37;
      combinations[72][5] = 63;  combinations[73][5] = 60;  combinations[74][5] = 51;  combinations[75][5] = 48;

      // Initialize the combination flags to zero.
      for (int i=0; i<76; i++) 
         combinations[i][6] = 0;

      // Set up the pos_to_comb array to point to every winning combination that a given 
      // position may have.
      setup_pos_to_comb();

      // Set up the best_picks array.
      update_best_picks();
   }


  /**
   *  Initialize the "outside four" array. 
   */
   public void init_outside_four() {
      for (int i=0; i<18; i++) {
         outside_four[i][0] = 0;
         outside_four[i][1] = 0;
         outside_four[i][2] = faces[i][ 2];
         outside_four[i][3] = faces[i][ 5];
         outside_four[i][4] = faces[i][14];
         outside_four[i][5] = faces[i][17];
      }
   }


  /**
   *  Initialize the "inside four" array.
   */
   public void init_inside_four() {
      for (int i=0; i<18; i++) {
         inside_four[i][0] = 0;
         inside_four[i][1] = 0;
         inside_four[i][2] = faces[i][ 7];
         inside_four[i][3] = faces[i][ 8];
         inside_four[i][4] = faces[i][11];
         inside_four[i][5] = faces[i][12];
      }
   }

  /**
   *  Initialize the "faces" array. 
   */
   public void init_faces () {

      faces[ 0][ 0] =  0;
      faces[ 0][ 1] =  0;
      faces[ 0][ 2] = 12;  faces[ 0][ 6] = 13;  faces[ 0][10] = 14;  faces[ 0][14] = 15;
      faces[ 0][ 3] =  8;  faces[ 0][ 7] =  9;  faces[ 0][11] = 10;  faces[ 0][15] = 11;
      faces[ 0][ 4] =  4;  faces[ 0][ 8] =  5;  faces[ 0][12] =  6;  faces[ 0][16] =  7;
      faces[ 0][ 5] =  0;  faces[ 0][ 9] =  1;  faces[ 0][13] =  2;  faces[ 0][17] =  3;

      faces[ 1][ 0] =  0;
      faces[ 1][ 1] =  0;
      faces[ 1][ 2] = 28;  faces[ 1][ 6] = 29;  faces[ 1][10] = 30;  faces[ 1][14] = 31;
      faces[ 1][ 3] = 24;  faces[ 1][ 7] = 25;  faces[ 1][11] = 26;  faces[ 1][15] = 27;
      faces[ 1][ 4] = 20;  faces[ 1][ 8] = 21;  faces[ 1][12] = 22;  faces[ 1][16] = 23;
      faces[ 1][ 5] = 16;  faces[ 1][ 9] = 17;  faces[ 1][13] = 18;  faces[ 1][17] = 19;

      faces[ 2][ 0] =  0;
      faces[ 2][ 1] =  0;
      faces[ 2][ 2] = 44;  faces[ 2][ 6] = 45;  faces[ 2][10] = 46;  faces[ 2][14] = 47;
      faces[ 2][ 3] = 40;  faces[ 2][ 7] = 41;  faces[ 2][11] = 42;  faces[ 2][15] = 43;
      faces[ 2][ 4] = 36;  faces[ 2][ 8] = 37;  faces[ 2][12] = 38;  faces[ 2][16] = 39;
      faces[ 2][ 5] = 32;  faces[ 2][ 9] = 33;  faces[ 2][13] = 34;  faces[ 2][17] = 35;

      faces[ 3][ 0] =  0;
      faces[ 3][ 1] =  0;
      faces[ 3][ 2] = 60;  faces[ 3][ 6] = 61;  faces[ 3][10] = 62;  faces[ 3][14] = 63;
      faces[ 3][ 3] = 56;  faces[ 3][ 7] = 57;  faces[ 3][11] = 58;  faces[ 3][15] = 59;
      faces[ 3][ 4] = 52;  faces[ 3][ 8] = 53;  faces[ 3][12] = 54;  faces[ 3][16] = 55;
      faces[ 3][ 5] = 48;  faces[ 3][ 9] = 49;  faces[ 3][13] = 50;  faces[ 3][17] = 51;

      faces[ 4][ 0] =  0;
      faces[ 4][ 1] =  0;
      faces[ 4][ 2] = 12;  faces[ 4][ 6] = 28;  faces[ 4][10] = 44;  faces[ 4][14] = 60;
      faces[ 4][ 3] =  8;  faces[ 4][ 7] = 24;  faces[ 4][11] = 40;  faces[ 4][15] = 56;
      faces[ 4][ 4] =  4;  faces[ 4][ 8] = 20;  faces[ 4][12] = 36;  faces[ 4][16] = 52;
      faces[ 4][ 5] =  0;  faces[ 4][ 9] = 16;  faces[ 4][13] = 32;  faces[ 4][17] = 48;

      faces[ 5][ 0] =  0;
      faces[ 5][ 1] =  0;
      faces[ 5][ 2] = 13;  faces[ 5][ 6] = 29;  faces[ 5][10] = 45;  faces[ 5][14] = 61;
      faces[ 5][ 3] =  9;  faces[ 5][ 7] = 25;  faces[ 5][11] = 41;  faces[ 5][15] = 57;
      faces[ 5][ 4] =  5;  faces[ 5][ 8] = 21;  faces[ 5][12] = 37;  faces[ 5][16] = 53;
      faces[ 5][ 5] =  1;  faces[ 5][ 9] = 17;  faces[ 5][13] = 33;  faces[ 5][17] = 49;

      faces[ 6][ 0] =  0;
      faces[ 6][ 1] =  0;
      faces[ 6][ 2] = 14;  faces[ 6][ 6] = 30;  faces[ 6][10] = 46;  faces[ 6][14] = 62;
      faces[ 6][ 3] = 10;  faces[ 6][ 7] = 26;  faces[ 6][11] = 42;  faces[ 6][15] = 58;
      faces[ 6][ 4] =  6;  faces[ 6][ 8] = 22;  faces[ 6][12] = 38;  faces[ 6][16] = 54;
      faces[ 6][ 5] =  2;  faces[ 6][ 9] = 18;  faces[ 6][13] = 34;  faces[ 6][17] = 50;

      faces[ 7][ 0] =  0;
      faces[ 7][ 1] =  0;
      faces[ 7][ 2] = 15;  faces[ 7][ 6] = 31;  faces[ 7][10] = 47;  faces[ 7][14] = 63;
      faces[ 7][ 3] = 11;  faces[ 7][ 7] = 27;  faces[ 7][11] = 43;  faces[ 7][15] = 59;
      faces[ 7][ 4] =  7;  faces[ 7][ 8] = 23;  faces[ 7][12] = 39;  faces[ 7][16] = 55;
      faces[ 7][ 5] =  3;  faces[ 7][ 9] = 19;  faces[ 7][13] = 35;  faces[ 7][17] = 51;

      faces[ 8][ 0] =  0;
      faces[ 8][ 1] =  0;
      faces[ 8][ 2] = 12;  faces[ 8][ 6] = 28;  faces[ 8][10] = 44;  faces[ 8][14] = 60;
      faces[ 8][ 3] = 13;  faces[ 8][ 7] = 29;  faces[ 8][11] = 45;  faces[ 8][15] = 61;
      faces[ 8][ 4] = 14;  faces[ 8][ 8] = 30;  faces[ 8][12] = 46;  faces[ 8][16] = 62;
      faces[ 8][ 5] = 15;  faces[ 8][ 9] = 31;  faces[ 8][13] = 47;  faces[ 8][17] = 63;

      faces[ 9][ 0] =  0;
      faces[ 9][ 1] =  0;
      faces[ 9][ 2] =  8;  faces[ 9][ 6] = 24;  faces[ 9][10] = 40;  faces[ 9][14] = 56;
      faces[ 9][ 3] =  9;  faces[ 9][ 7] = 25;  faces[ 9][11] = 41;  faces[ 9][15] = 57;
      faces[ 9][ 4] = 10;  faces[ 9][ 8] = 26;  faces[ 9][12] = 42;  faces[ 9][16] = 58;
      faces[ 9][ 5] = 11;  faces[ 9][ 9] = 27;  faces[ 9][13] = 43;  faces[ 9][17] = 59;

      faces[10][ 0] =  0;
      faces[10][ 1] =  0;
      faces[10][ 2] =  4;  faces[10][ 6] = 20;  faces[10][10] = 36;  faces[10][14] = 52;
      faces[10][ 3] =  5;  faces[10][ 7] = 21;  faces[10][11] = 37;  faces[10][15] = 53;
      faces[10][ 4] =  6;  faces[10][ 8] = 22;  faces[10][12] = 38;  faces[10][16] = 54;
      faces[10][ 5] =  7;  faces[10][ 9] = 23;  faces[10][13] = 39;  faces[10][17] = 55;

      faces[11][ 0] =  0;
      faces[11][ 1] =  0;
      faces[11][ 2] =  0;  faces[11][ 6] = 16;  faces[11][10] = 32;  faces[11][14] = 48;
      faces[11][ 3] =  1;  faces[11][ 7] = 17;  faces[11][11] = 33;  faces[11][15] = 49;
      faces[11][ 4] =  2;  faces[11][ 8] = 18;  faces[11][12] = 34;  faces[11][16] = 50;
      faces[11][ 5] =  3;  faces[11][ 9] = 19;  faces[11][13] = 35;  faces[11][17] = 51;

      faces[12][ 0] =  0;
      faces[12][ 1] =  0;
      faces[12][ 2] = 12;  faces[12][ 6] = 13;  faces[12][10] = 14;  faces[12][14] = 15;
      faces[12][ 3] = 24;  faces[12][ 7] = 25;  faces[12][11] = 26;  faces[12][15] = 27;
      faces[12][ 4] = 36;  faces[12][ 8] = 37;  faces[12][12] = 38;  faces[12][16] = 39;
      faces[12][ 5] = 48;  faces[12][ 9] = 49;  faces[12][13] = 50;  faces[12][17] = 51;

      faces[13][ 0] =  0;
      faces[13][ 1] =  0;
      faces[13][ 2] =  0;  faces[13][ 6] =  1;  faces[13][10] =  2;  faces[13][14] =  3;
      faces[13][ 3] = 20;  faces[13][ 7] = 21;  faces[13][11] = 22;  faces[13][15] = 23;
      faces[13][ 4] = 40;  faces[13][ 8] = 41;  faces[13][12] = 42;  faces[13][16] = 43;
      faces[13][ 5] = 60;  faces[13][ 9] = 61;  faces[13][13] = 62;  faces[13][17] = 63;

      faces[14][ 0] =  0;
      faces[14][ 1] =  0;
      faces[14][ 2] = 12;  faces[14][ 6] = 28;  faces[14][10] = 44;  faces[14][14] = 60;
      faces[14][ 3] =  9;  faces[14][ 7] = 25;  faces[14][11] = 41;  faces[14][15] = 57;
      faces[14][ 4] =  6;  faces[14][ 8] = 22;  faces[14][12] = 38;  faces[14][16] = 54;
      faces[14][ 5] =  3;  faces[14][ 9] = 19;  faces[14][13] = 35;  faces[14][17] = 51;

      faces[15][ 0] =  0;
      faces[15][ 1] =  0;
      faces[15][ 2] = 15;  faces[15][ 6] = 31;  faces[15][10] = 47;  faces[15][14] = 63;
      faces[15][ 3] = 10;  faces[15][ 7] = 26;  faces[15][11] = 42;  faces[15][15] = 58;
      faces[15][ 4] =  5;  faces[15][ 8] = 21;  faces[15][12] = 37;  faces[15][16] = 53;
      faces[15][ 5] =  0;  faces[15][ 9] = 16;  faces[15][13] = 32;  faces[15][17] = 48;

      faces[16][ 0] =  0;
      faces[16][ 1] =  0;
      faces[16][ 2] = 12;  faces[16][ 6] = 29;  faces[16][10] = 46;  faces[16][14] = 63;
      faces[16][ 3] =  8;  faces[16][ 7] = 25;  faces[16][11] = 42;  faces[16][15] = 59;
      faces[16][ 4] =  4;  faces[16][ 8] = 21;  faces[16][12] = 38;  faces[16][16] = 55;
      faces[16][ 5] =  0;  faces[16][ 9] = 17;  faces[16][13] = 34;  faces[16][17] = 51;

      faces[17][ 0] =  0;
      faces[17][ 1] =  0;
      faces[17][ 2] = 15;  faces[17][ 6] = 30;  faces[17][10] = 45;  faces[17][14] = 60;
      faces[17][ 3] = 11;  faces[17][ 7] = 26;  faces[17][11] = 41;  faces[17][15] = 56;
      faces[17][ 4] =  7;  faces[17][ 8] = 22;  faces[17][12] = 37;  faces[17][16] = 52;
      faces[17][ 5] =  3;  faces[17][ 9] = 18;  faces[17][13] = 33;  faces[17][17] = 48;
   }

   /**
    *  Render the current face set in the 2D window.
    */
   public void render2D(Graphics gc) {

      gc.setColor(background);
      gc.fillRect(0, 0, width, height);

      int id;
      int x, y;

      float begX;
      float begY;

      for (int l=0; l<3; l++) {
         begY =  28.0f + l*(5.f*23.3f);
         for (int k=0; k<6; k++) {
            begX =  11.65f + k*(5.f*11.65f);
            int count = 0;
            int face = l*6+k;
            for (int i=0; i<4; i++) {
               for (int j=0; j<4; j++) {
                  x = (int)begX + i*12;
                  y = (int)begY + j*12;
                  id = faces[face][count+2];
                  if (occupied[id] == HUMAN) {
                     x -= 2;
                     y -= 2;
                     gc.setColor(red);
                     gc.fillRect(x, y, 5, 5);
                  }
                  else if (occupied[id] == MACHINE) {
                     x -= 2;
                     y -= 2;
                     gc.setColor(blue);
                     gc.fillRect(x, y, 5, 5);
                  }
                  else {
                     x -= 1;
                     y -= 1;
                     gc.setColor(gray);
                     gc.fillRect(x, y, 2, 2);
                  }
                  if (highlight[face]) {
                     gc.setColor(yellow);
                     positions.setHighlight(faces[face][count+2]);
                  }
                  count++;
               }
            }
            if (highlight[face])
               gc.setColor(yellow);
            else
               gc.setColor(white);
            if ((face+1)<10)
               gc.drawString("Face "+(face+1), (int)begX-2, (int)begY+60);
            else
               gc.drawString("Face "+(face+1), (int)begX-4, (int)begY+60);
         }
      }
   }

   /**
    *  Determine what position has been selected in the 2D window.
    */
   public void checkSelection2D(int x, int y, int player) {

      int id;
      int posX, posY;

      float begX;
      float begY;

      for (int l=0; l<3; l++) {
         begY =  28.0f + l*(5.f*23.3f);
         for (int k=0; k<6; k++) {
            begX =  11.65f + k*(5.f*11.65f);
            int count = 0;
            int face = l*6+k;
            for (int i=0; i<4; i++) {
               for (int j=0; j<4; j++) {
                  posX = (int)begX + i*12;
                  posY = (int)begY + j*12;
                  if (x > posX-4 && x < posX+4 &&
                      y > posY-4 && y < posY+4) {

                     id = faces[face][count+2];

                     if (occupied[id] == UNOCCUPIED) {
                        positions.set(id, player);
                        selection(id, player);
                        canvas.repaint();
                     }
                     return;
                  }
                  count++;
               }
            }
            if ((x > begX-4  && x < begX+40) && 
                (y > begY+45 && y < begY+60)   ) {

               count = 0;
               for (int i=0; i<4; i++) {
                  for (int j=0; j<4; j++) {
                     if (highlight[face])
                        positions.clearHighlight(faces[face][count+2]);
                     count++;
                  }
               }
               if (highlight[face])
                  highlight[face] = false;
               else
                  highlight[face] = true;
               canvas.repaint();
            }
         }
      }

   }


   /**
    *  Record the player's move.
    */
   public void selection(int pos, int player) {
 
      int num_combinations;
      int comb;

      this.player = player;

      if (player == HUMAN) {

         // If position is already occupied, return.
         if (occupied[pos] != 0) return;

         // Mark the position as HUMAN.
         occupied[pos] = HUMAN;

         // Update the logic arrays.
         this.player = update_logic_arrays(pos);

         // Have the computer determine its move.
         choose_move();
      }
   }


   /**
    *  Determine the computer's move.
    */
   public void choose_move () {
      
      if (player == MACHINE) {

         // Babe in the woods.
         if (skill_level == 0) {
            if (!block_winning_move()) {
               if (!pick_7()) {
                  if (!check_outside_four()) {
                     pick_best_position();
                  }
               }
            }
         }

         // Walk and chew gum.
         else if (skill_level == 1) {
            if (!block_winning_move()) {
               if (!block_intersecting_rows()) {
                  if (!block_inside_four()) {
                     if (!block_outside_four()) {
                           pick_best_position();
                     }
                  }
               }
            }
         }

         // Jeopordy contestant.
         else if (skill_level == 2) {
            if (!block_winning_move()) {
               if (!block_intersecting_rows()) {
                  if (!block_inside_four()) {
                     if (!block_outside_four()) {
                        if (!pick_7()) {
                           pick_best_position();
                        }
                     }
                  }
               }
            }
         }

         // Rocket scientist.
         else if (skill_level == 3) {
            if (!block_winning_move()) {
               if (!block_intersecting_rows()) {
                  if (!block_chair_move()) {
                     if (!check_face_three()) {
                        if (!block_central_four()) {
                           if (!block_inside_four()) {
                              if (!block_outside_four()) {
                                 if (!take_inside_four()) {
                                    if (!take_outside_four()) {
                                       if (!pick_7()) {
                                          if (!check_outside_four()) {
                                             pick_best_position();
                                          }
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         // Be afraid, be very afraid.
         else if (skill_level == 4) {
            if (!block_winning_move()) {
               if (!block_intersecting_rows()) {
                  if (!block_chair_move()) {
                     if (!block_walk_move()) {
                        if (!block_central_four()) {
                           if (!block_inside_four()) {
                              if (!block_outside_four()) {
                                 if (!check_face_three()) {
                                    if (!check_intersecting_rows2()) {
                                       if (!take_inside_four()) {
                                          if (!take_outside_four()) {
                                             if (!pick_7()) {
                                                if (!check_outside_four()) {
                                                   pick_best_position();
                                                }
                                             }
                                          }
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }


   /**
    *  Check for a winning move.
    */
   public boolean block_winning_move() {

      // Loop through each combination and see if any player occupies 
      // three positions. If so, take the last remaining position.
      int pos;
      for (int i=0; i<76; i++) {
         if (combinations[i][0] == 3) {
            for (int j=2; j<6; j++) {
               pos = combinations[i][j];
               if (occupied[pos] == 0) {
                  occupied[pos] = MACHINE;
                  positions.set(pos, MACHINE);
                  player = update_logic_arrays(pos);
                  if (debug) System.out.println("block_winning_move:  true");
                  return true;  
               }
            }
         }
      }
      if (debug) System.out.println("check_winning_move:  false");
      return false;
   }


   /**
    *  Block outside four
    */
   public boolean block_outside_four() {

      int pos;
      int index = 0;
      int max = 0;

      // Block the opponent, if necessary.
      for (int i=0; i<18; i++) {
         if (outside_four[i][0] > 0 &&
             outside_four[i][1] == HUMAN) {
            if(outside_four[i][0] > max) {
               index = i;
               max = outside_four[i][0];
            }
         }
      }

      if (max > 0) {
         for (int j=2; j<6; j++) {
            pos = outside_four[index][j];
            if (occupied[pos] == 0) {
               occupied[pos] = MACHINE;
               positions.set(pos, MACHINE);
               player = update_logic_arrays(pos);
               if (debug) System.out.println("block_outside_four:  true");
               return true;
            }
         }
      }

      if (debug) System.out.println("block_outside_four:  false");
      return false;
   }


   /**
    *  Block central four
    */
   public boolean block_central_four() {
 
      int pos;   
      int index = 0;
      int max = 0;
 
      // Block the opponent, if necessary.
      for (int i=1; i<3; i++) {
         if (inside_four[i][0] > 0 &&
             inside_four[i][1] == HUMAN) {
            if(inside_four[i][0] > max) {
               index = i;
               max = inside_four[i][0];
            }
         }
      }   
 
      if (max > 0) {
         for (int j=2; j<6; j++) {
            pos = inside_four[index][j];
            if (occupied[pos] == 0) {
               occupied[pos] = MACHINE;
               positions.set(pos, MACHINE);
               player = update_logic_arrays(pos);
               if (debug) System.out.println("block_central_four:  true");
               return true;
            }
         }
      }   
 
      if (debug) System.out.println("block_central_four:  false");
      return false;
   }
 
   /**   
    *  Check each face for a forced win.
    */   
   public boolean check_face_three() {
     
      int pos;   
      int index = 0;
      int human = 0;
      int machine = 0;
 
      // Block the opponent from a forced win.
      for (int i=0; i<18; i++) {
         if (outside_four[i][0] == -1) {
            human = 0;
            machine = 0;
            for (int j=2; j<6; j++) {
               if (occupied[outside_four[i][j]] == MACHINE)
                  machine++;
               else if (occupied[outside_four[i][j]] == HUMAN)
                  human++;
            }
            if (debug) System.out.println("machine = " + machine);
            if (debug) System.out.println("human   = " + human);
            if (human == 3 && machine == 1) {
               if (debug) System.out.println("human == 3 && machine == 1");
               for (int j=2; j<18; j++) {
                  pos  = faces[i][j];
                  if (occupied[pos] == 0) { 
                     for (int k=0; k<76; k++) {
                        if (combinations[i][0] == 2 &
                            combinations[i][1] == HUMAN) {
                           for (int l=0; l<4; l++) {
                              if (combinations[i][l] == pos) {
                                 occupied[pos] = MACHINE;
                                 positions.set(pos, MACHINE);
                                 player = update_logic_arrays(pos);
                                 if (debug) System.out.println("check_face_three:  true");
                                 return true;
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }     
         
      if (debug) System.out.println("check_face_three:  false");
      return false;
   }
 


   /**
    *  Block inside four
    */
   public boolean block_inside_four() {
 
      int pos;
      int index = 0;
      int max = 0;
 
      // Block the opponent, if necessary.
      for (int i=0; i<18; i++) {
         if (inside_four[i][0] > 0 &&
             inside_four[i][1] == HUMAN) {
            if(inside_four[i][0] > max) {
               index = i;
               max = inside_four[i][0];
            }
         }
      }   

      if (max > 0) {
         for (int j=2; j<6; j++) {
            pos = inside_four[index][j];
            if (occupied[pos] == 0) {
               occupied[pos] = MACHINE;
               positions.set(pos, MACHINE);
               player = update_logic_arrays(pos);
               if (debug) System.out.println("block_inside_four:  true");
               return true;
            }
         }
      }   

      if (debug) System.out.println("block_inside_four:  false");
      return false;
   }


   public boolean block_chair_move() {

      int pos;

      int ncorners = 0;    // Number of corners owned by human
      int corner   = 0;    // Corner owned by machine

      if (debug) System.out.println("inside block_chair_move");

      // Loop through all of the faces.
      for(int i=0; i<18; i++) {

         // Determine which corners the human owns.
         if (occupied[faces[i][2]] == HUMAN)
            ncorners++;   
         else if (occupied[faces[i][2]] == MACHINE)
            corner = 2;
         if (occupied[faces[i][5]] == HUMAN) 
            ncorners++;   
         else if (occupied[faces[i][5]] == MACHINE)
            corner = 5;
         if (occupied[faces[i][14]] == HUMAN)
            ncorners++;   
         else if (occupied[faces[i][14]] == MACHINE)
            corner = 14;
         if (occupied[faces[i][17]] == HUMAN)
            ncorners++;   
         else if (occupied[faces[i][17]] == MACHINE)
            corner = 17;

         // If the human owns three corners, continue with the search.
         if (ncorners == 3) {
            if (corner == 2) {
               if (occupied[faces[i][ 3]] == HUMAN && occupied[faces[i][ 7]] == 0 &&
                   occupied[faces[i][ 8]] == 0     && occupied[faces[i][11]] == 0 &&
                   occupied[faces[i][15]] == 0     && occupied[faces[i][16]] == 0) {
                  pos = faces[i][11];
                  occupied[pos] = MACHINE;
                  positions.set(pos, MACHINE);
                  player = update_logic_arrays(pos);
                  if (debug) System.out.println("block_chair_move: found");
                  return true;
               }
               if (occupied[faces[i][ 4]] == HUMAN && occupied[faces[i][ 8]] == 0 &&
                   occupied[faces[i][11]] == 0     && occupied[faces[i][12]] == 0 &&
                   occupied[faces[i][15]] == 0     && occupied[faces[i][16]] == 0) {
                  pos = faces[i][12];
                  occupied[pos] = MACHINE;
                  positions.set(pos, MACHINE);
                  player = update_logic_arrays(pos);
                  if (debug) System.out.println("block_chair_move: found");
                  return true;
               }
               if (occupied[faces[i][ 6]] == HUMAN && occupied[faces[i][ 7]] == 0 &&
                   occupied[faces[i][ 8]] == 0     && occupied[faces[i][ 9]] == 0 &&
                   occupied[faces[i][11]] == 0     && occupied[faces[i][13]] == 0) {
                  pos = faces[i][8];
                  occupied[pos] = MACHINE;
                  positions.set(pos, MACHINE);
                  player = update_logic_arrays(pos);
                  if (debug) System.out.println("block_chair_move: found");
                  return true;
               }
               if (occupied[faces[i][10]] == HUMAN && occupied[faces[i][ 8]] == 0 &&
                   occupied[faces[i][ 9]] == 0     && occupied[faces[i][11]] == 0 &&
                   occupied[faces[i][12]] == 0     && occupied[faces[i][13]] == 0) {
                  pos = faces[i][11];
                  occupied[pos] = MACHINE;
                  positions.set(pos, MACHINE);
                  player = update_logic_arrays(pos);
                  if (debug) System.out.println("block_chair_move: found");
                  return true;
               }
               if (occupied[faces[i][ 7]] == HUMAN && occupied[faces[i][ 3]] == 0 &&
                   occupied[faces[i][ 8]] == 0     && occupied[faces[i][11]] == 0 &&
                   occupied[faces[i][15]] == 0     && occupied[faces[i][16]] == 0) {
                  pos = faces[i][11];
                  occupied[pos] = MACHINE;
                  positions.set(pos, MACHINE);
                  player = update_logic_arrays(pos);
                  if (debug) System.out.println("block_chair_move: found");
                  return true;
               }
               if (occupied[faces[i][12]] == HUMAN && occupied[faces[i][ 4]] == 0 &&
                   occupied[faces[i][ 8]] == 0     && occupied[faces[i][11]] == 0 &&
                   occupied[faces[i][15]] == 0     && occupied[faces[i][16]] == 0) {
                  pos = faces[i][16];
                  occupied[pos] = MACHINE;
                  positions.set(pos, MACHINE);
                  player = update_logic_arrays(pos);
                  if (debug) System.out.println("block_chair_move: found");
                  return true;
               }
            }
            else if (corner == 5) {
               if (occupied[faces[i][ 9]] == HUMAN && occupied[faces[i][ 6]] == 0 &&
                   occupied[faces[i][ 7]] == 0     && occupied[faces[i][ 8]] == 0 &&
                   occupied[faces[i][10]] == 0     && occupied[faces[i][12]] == 0) {
                  pos = faces[i][7];
                  occupied[pos] = MACHINE;
                  positions.set(pos, MACHINE);
                  player = update_logic_arrays(pos);
                  if (debug) System.out.println("block_chair_move: found");
                  return true;
               }
               if (occupied[faces[i][13]] == HUMAN && occupied[faces[i][ 7]] == 0 &&
                   occupied[faces[i][ 7]] == 0     && occupied[faces[i][10]] == 0 &&
                   occupied[faces[i][11]] == 0     && occupied[faces[i][12]] == 0) {
                  pos = faces[i][12];
                  occupied[pos] = MACHINE;
                  positions.set(pos, MACHINE);
                  player = update_logic_arrays(pos);
                  if (debug) System.out.println("block_chair_move: found");
                  return true;
               }
               if (occupied[faces[i][ 4]] == HUMAN && occupied[faces[i][ 8]] == 0 &&
                   occupied[faces[i][11]] == 0     && occupied[faces[i][12]] == 0 &&
                   occupied[faces[i][15]] == 0     && occupied[faces[i][16]] == 0) {
                  pos = faces[i][12];
                  occupied[pos] = MACHINE;
                  positions.set(pos, MACHINE);
                  player = update_logic_arrays(pos);
                  if (debug) System.out.println("block_chair_move: found");
                  return true;
               }
               if (occupied[faces[i][ 3]] == HUMAN && occupied[faces[i][ 7]] == 0 &&
                   occupied[faces[i][11]] == 0     && occupied[faces[i][12]] == 0 &&
                   occupied[faces[i][15]] == 0     && occupied[faces[i][16]] == 0) {
                  pos = faces[i][7];
                  occupied[pos] = MACHINE;
                  positions.set(pos, MACHINE);
                  player = update_logic_arrays(pos);
                  if (debug) System.out.println("block_chair_move: found");
                  return true;
               }
               if (occupied[faces[i][ 8]] == HUMAN && occupied[faces[i][ 4]] == 0 &&
                   occupied[faces[i][11]] == 0     && occupied[faces[i][12]] == 0 &&
                   occupied[faces[i][15]] == 0     && occupied[faces[i][16]] == 0) {
                  pos = faces[i][12];
                  occupied[pos] = MACHINE;
                  positions.set(pos, MACHINE);
                  player = update_logic_arrays(pos);
                  if (debug) System.out.println("block_chair_move: found");
                  return true;
               }
               if (occupied[faces[i][11]] == HUMAN && occupied[faces[i][ 3]] == 0 &&
                   occupied[faces[i][ 7]] == 0     && occupied[faces[i][12]] == 0 &&
                   occupied[faces[i][15]] == 0     && occupied[faces[i][16]] == 0) {
                  pos = faces[i][ 7];
                  occupied[pos] = MACHINE;
                  positions.set(pos, MACHINE);
                  player = update_logic_arrays(pos);
                  if (debug) System.out.println("block_chair_move: found");
                  return true;
               }
            }
            else if (corner == 14) {
               if (occupied[faces[i][ 6]] == HUMAN && occupied[faces[i][ 7]] == 0 &&
                   occupied[faces[i][ 8]] == 0     && occupied[faces[i][ 9]] == 0 &&
                   occupied[faces[i][11]] == 0     && occupied[faces[i][13]] == 0) {
                  pos = faces[i][7];
                  occupied[pos] = MACHINE;
                  positions.set(pos, MACHINE);
                  player = update_logic_arrays(pos);
                  if (debug) System.out.println("block_chair_move: found");
                  return true;
               }
               if (occupied[faces[i][10]] == HUMAN && occupied[faces[i][ 8]] == 0 &&
                   occupied[faces[i][ 9]] == 0     && occupied[faces[i][11]] == 0 &&
                   occupied[faces[i][12]] == 0     && occupied[faces[i][13]] == 0) {
                  pos = faces[i][12];
                  occupied[pos] = MACHINE;
                  positions.set(pos, MACHINE);
                  player = update_logic_arrays(pos);
                  if (debug) System.out.println("block_chair_move: found");
                  return true;
               }
               if (occupied[faces[i][15]] == HUMAN && occupied[faces[i][ 3]] == 0 &&
                   occupied[faces[i][ 4]] == 0     && occupied[faces[i][ 7]] == 0 &&
                   occupied[faces[i][11]] == 0     && occupied[faces[i][12]] == 0) {
                  pos = faces[i][3];
                  occupied[pos] = MACHINE;
                  positions.set(pos, MACHINE);
                  player = update_logic_arrays(pos);
                  if (debug) System.out.println("block_chair_move: found");
                  return true;
               }
               if (occupied[faces[i][16]] == HUMAN && occupied[faces[i][ 3]] == 0 &&
                   occupied[faces[i][ 4]] == 0     && occupied[faces[i][ 7]] == 0 &&
                   occupied[faces[i][ 8]] == 0     && occupied[faces[i][12]] == 0) {
                  pos = faces[i][12];
                  occupied[pos] = MACHINE;
                  positions.set(pos, MACHINE);
                  player = update_logic_arrays(pos);
                  if (debug) System.out.println("block_chair_move: found");
                  return true;
               }
               if (occupied[faces[i][11]] == HUMAN && occupied[faces[i][ 3]] == 0 &&
                   occupied[faces[i][ 4]] == 0     && occupied[faces[i][ 7]] == 0 &&
                   occupied[faces[i][12]] == 0     && occupied[faces[i][15]] == 0) {
                  pos = faces[i][7];
                  occupied[pos] = MACHINE;
                  positions.set(pos, MACHINE);
                  player = update_logic_arrays(pos);
                  if (debug) System.out.println("block_chair_move: found");
                  return true;
               }
               if (occupied[faces[i][ 8]] == HUMAN && occupied[faces[i][ 6]] == 0 &&
                   occupied[faces[i][ 7]] == 0     && occupied[faces[i][ 9]] == 0 &&
                   occupied[faces[i][12]] == 0     && occupied[faces[i][13]] == 0) {
                  pos = faces[i][7];
                  occupied[pos] = MACHINE;
                  positions.set(pos, MACHINE);
                  player = update_logic_arrays(pos);
                  if (debug) System.out.println("block_chair_move: found");
                  return true;
               }
            }
            else if (corner == 17) {
               if (occupied[faces[i][ 9]] == HUMAN && occupied[faces[i][ 6]] == 0 &&
                   occupied[faces[i][ 7]] == 0     && occupied[faces[i][ 8]] == 0 &&
                   occupied[faces[i][10]] == 0     && occupied[faces[i][11]] == 0) {
                  pos = faces[i][8];
                  occupied[pos] = MACHINE;
                  positions.set(pos, MACHINE);
                  player = update_logic_arrays(pos);
                  if (debug) System.out.println("block_chair_move: found");
                  return true;
               }
               if (occupied[faces[i][13]] == HUMAN && occupied[faces[i][ 6]] == 0 &&
                   occupied[faces[i][ 8]] == 0     && occupied[faces[i][10]] == 0 &&
                   occupied[faces[i][11]] == 0     && occupied[faces[i][12]] == 0) {
                  pos = faces[i][11];
                  occupied[pos] = MACHINE;
                  positions.set(pos, MACHINE);
                  player = update_logic_arrays(pos);
                  if (debug) System.out.println("block_chair_move: found");
                  return true;
               }
               if (occupied[faces[i][15]] == HUMAN && occupied[faces[i][ 3]] == 0 &&
                   occupied[faces[i][ 4]] == 0     && occupied[faces[i][ 7]] == 0 &&
                   occupied[faces[i][ 8]] == 0     && occupied[faces[i][11]] == 0) {
                  pos = faces[i][11];
                  occupied[pos] = MACHINE;
                  positions.set(pos, MACHINE);
                  player = update_logic_arrays(pos);
                  if (debug) System.out.println("block_chair_move: found");
                  return true;
               }
               if (occupied[faces[i][16]] == HUMAN && occupied[faces[i][ 3]] == 0 &&
                   occupied[faces[i][ 4]] == 0     && occupied[faces[i][ 8]] == 0 &&
                   occupied[faces[i][11]] == 0     && occupied[faces[i][12]] == 0) {
                  pos = faces[i][8];
                  occupied[pos] = MACHINE;
                  positions.set(pos, MACHINE);
                  player = update_logic_arrays(pos);
                  if (debug) System.out.println("block_chair_move: found");
                  return true;
               }
               if (occupied[faces[i][12]] == HUMAN && occupied[faces[i][ 3]] == 0 &&
                   occupied[faces[i][ 4]] == 0     && occupied[faces[i][ 8]] == 0 &&
                   occupied[faces[i][11]] == 0     && occupied[faces[i][16]] == 0) {
                  pos = faces[i][8];
                  occupied[pos] = MACHINE;
                  positions.set(pos, MACHINE);
                  player = update_logic_arrays(pos);
                  if (debug) System.out.println("block_chair_move: found");
                  return true;
               }
               if (occupied[faces[i][ 7]] == HUMAN && occupied[faces[i][ 3]] == 0 &&
                   occupied[faces[i][ 4]] == 0     && occupied[faces[i][ 8]] == 0 &&
                   occupied[faces[i][11]] == 0     && occupied[faces[i][15]] == 0) {
                  pos = faces[i][11];
                  occupied[pos] = MACHINE;
                  positions.set(pos, MACHINE);
                  player = update_logic_arrays(pos);
                  if (debug) System.out.println("block_chair_move: found");
                  return true;
               }
            }
         }
         ncorners = 0;
         corner   = -1;
      }
      if (debug) System.out.println("block_chair_move: false");
      return false;
   }

   public boolean block_walk_move() {

      int pos;

      if (debug) System.out.println("inside block_walk_move");

      // Loop through all of the faces.
      for(int i=0; i<18; i++) {

         // Look for a matching pattern.
         if (occupied[faces[i][ 2]] == HUMAN && occupied[faces[i][14]] == HUMAN &&
             occupied[faces[i][ 3]] == HUMAN && occupied[faces[i][15]] == HUMAN &&
             occupied[faces[i][ 6]] == 0     && occupied[faces[i][10]] == 0     &&
             occupied[faces[i][ 7]] == 0     && occupied[faces[i][11]] == 0) {

            if (occupied[faces[i][ 8]] == HUMAN && occupied[faces[i][ 9]] == 0) {
               pos = faces[i][6];
               occupied[pos] = MACHINE;
               positions.set(pos, MACHINE);
               player = update_logic_arrays(pos);
               return true;
            }
            else if (occupied[faces[i][12]] == HUMAN && occupied[faces[i][13]] == 0) {
               pos = faces[i][10];
               occupied[pos] = MACHINE;
               positions.set(pos, MACHINE);
               player = update_logic_arrays(pos);
               return true;
            }
         }

         // Look for a matching pattern.
         if (occupied[faces[i][14]] == HUMAN && occupied[faces[i][17]] == HUMAN &&
             occupied[faces[i][10]] == HUMAN && occupied[faces[i][13]] == HUMAN &&
             occupied[faces[i][15]] == 0     && occupied[faces[i][16]] == 0     &&
             occupied[faces[i][11]] == 0     && occupied[faces[i][12]] == 0) {

            if (occupied[faces[i][7]] == HUMAN && occupied[faces[i][3]] == 0) {
               pos = faces[i][15];
               occupied[pos] = MACHINE;
               positions.set(pos, MACHINE);
               player = update_logic_arrays(pos);
               return true;
            }
            else if (occupied[faces[i][8]] == HUMAN && occupied[faces[i][4]] == 0) {
               pos = faces[i][16];
               occupied[pos] = MACHINE;
               positions.set(pos, MACHINE);
               player = update_logic_arrays(pos);
               return true;
            }
         }

         // Look for a matching pattern.
         if (occupied[faces[i][ 4]] == HUMAN && occupied[faces[i][16]] == HUMAN &&
             occupied[faces[i][ 5]] == HUMAN && occupied[faces[i][17]] == HUMAN &&
             occupied[faces[i][ 8]] == 0     && occupied[faces[i][12]] == 0     &&
             occupied[faces[i][ 9]] == 0     && occupied[faces[i][13]] == 0) {

            if (occupied[faces[i][11]] == HUMAN && occupied[faces[i][10]] == 0) {
               pos = faces[i][18];
               occupied[pos] = MACHINE;
               positions.set(pos, MACHINE);
               player = update_logic_arrays(pos);
               return true;
            }
            else if (occupied[faces[i][7]] == HUMAN && occupied[faces[i][6]] == 0) {
               pos = faces[i][9];
               occupied[pos] = MACHINE;
               positions.set(pos, MACHINE);
               player = update_logic_arrays(pos);
               return true;
            }
         }

         // Look for a matching pattern.
         if (occupied[faces[i][ 6]] == HUMAN && occupied[faces[i][ 9]] == HUMAN &&
             occupied[faces[i][ 2]] == HUMAN && occupied[faces[i][ 5]] == HUMAN &&
             occupied[faces[i][ 7]] == 0     && occupied[faces[i][ 8]] == 0     &&
             occupied[faces[i][ 3]] == 0     && occupied[faces[i][ 4]] == 0) {

            if (occupied[faces[i][11]] == HUMAN && occupied[faces[i][15]] == 0) {
               pos = faces[i][3];
               occupied[pos] = MACHINE;
               positions.set(pos, MACHINE);
               player = update_logic_arrays(pos);
               return true;
            }
            else if (occupied[faces[i][12]] == HUMAN && occupied[faces[i][16]] == 0) {
               pos = faces[i][4];
               occupied[pos] = MACHINE;
               positions.set(pos, MACHINE);
               player = update_logic_arrays(pos);
               return true;
            }
         }

         // Look for a matching pattern.
         if (occupied[faces[i][ 2]] == HUMAN && occupied[faces[i][14]] == HUMAN &&
             occupied[faces[i][ 4]] == HUMAN && occupied[faces[i][16]] == HUMAN &&
             occupied[faces[i][ 6]] == 0     && occupied[faces[i][10]] == 0     &&
             occupied[faces[i][ 8]] == 0     && occupied[faces[i][12]] == 0) {

            if ((occupied[faces[i][7]] == HUMAN && occupied[faces[i][9]] == 0) ||
                (occupied[faces[i][9]] == HUMAN && occupied[faces[i][7]] == 0)   ) {
               pos = faces[i][6];
               occupied[pos] = MACHINE;
               positions.set(pos, MACHINE);
               player = update_logic_arrays(pos);
               return true;
            }
            else if ((occupied[faces[i][11]] == HUMAN && occupied[faces[i][13]] == 0) ||
                     (occupied[faces[i][13]] == HUMAN && occupied[faces[i][11]] == 0)   ) {
               pos = faces[i][10];
               occupied[pos] = MACHINE;
               positions.set(pos, MACHINE);
               player = update_logic_arrays(pos);
               return true;
            }
         }

         // Look for a matching pattern.
         if (occupied[faces[i][14]] == HUMAN && occupied[faces[i][17]] == HUMAN &&
             occupied[faces[i][ 6]] == HUMAN && occupied[faces[i][ 9]] == HUMAN &&
             occupied[faces[i][15]] == 0     && occupied[faces[i][16]] == 0     &&
             occupied[faces[i][ 7]] == 0     && occupied[faces[i][ 8]] == 0) {

            if ((occupied[faces[i][11]] == HUMAN && occupied[faces[i][ 3]] == 0) ||
                (occupied[faces[i][ 3]] == HUMAN && occupied[faces[i][11]] == 0)   ) {
               pos = faces[i][15];
               occupied[pos] = MACHINE;
               positions.set(pos, MACHINE);
               player = update_logic_arrays(pos);
               return true;
            }
            else if ((occupied[faces[i][12]] == HUMAN && occupied[faces[i][ 4]] == 0) ||
                     (occupied[faces[i][ 4]] == HUMAN && occupied[faces[i][12]] == 0)   ) {
               pos = faces[i][16];
               occupied[pos] = MACHINE;
               positions.set(pos, MACHINE);
               player = update_logic_arrays(pos);
               return true;
            }
         }

         // Look for a matching pattern.
         if (occupied[faces[i][ 3]] == HUMAN && occupied[faces[i][15]] == HUMAN &&
             occupied[faces[i][ 5]] == HUMAN && occupied[faces[i][17]] == HUMAN &&
             occupied[faces[i][ 7]] == 0     && occupied[faces[i][11]] == 0     &&
             occupied[faces[i][ 9]] == 0     && occupied[faces[i][13]] == 0) {

            if ((occupied[faces[i][ 6]] == HUMAN && occupied[faces[i][ 8]] == 0) ||
                (occupied[faces[i][ 8]] == HUMAN && occupied[faces[i][ 6]] == 0)   ) {
               pos = faces[i][9];
               occupied[pos] = MACHINE;
               positions.set(pos, MACHINE);
               player = update_logic_arrays(pos);
               return true;
            }
            else if ((occupied[faces[i][10]] == HUMAN && occupied[faces[i][12]] == 0) ||
                     (occupied[faces[i][12]] == HUMAN && occupied[faces[i][10]] == 0)   ) {
               pos = faces[i][13];
               occupied[pos] = MACHINE;
               positions.set(pos, MACHINE);
               player = update_logic_arrays(pos);
               return true;
            }
         }

         // Look for a matching pattern.
         if (occupied[faces[i][10]] == HUMAN && occupied[faces[i][13]] == HUMAN &&
             occupied[faces[i][ 2]] == HUMAN && occupied[faces[i][ 5]] == HUMAN &&
             occupied[faces[i][11]] == 0     && occupied[faces[i][12]] == 0     &&
             occupied[faces[i][ 3]] == 0     && occupied[faces[i][ 4]] == 0) {

            if ((occupied[faces[i][ 7]] == HUMAN && occupied[faces[i][15]] == 0) ||
                (occupied[faces[i][15]] == HUMAN && occupied[faces[i][ 7]] == 0)   ) {
               pos = faces[i][3];
               occupied[pos] = MACHINE;
               positions.set(pos, MACHINE);
               player = update_logic_arrays(pos);
               return true;
            }
            else if ((occupied[faces[i][ 8]] == HUMAN && occupied[faces[i][16]] == 0) ||
                     (occupied[faces[i][16]] == HUMAN && occupied[faces[i][ 8]] == 0)   ) {
               pos = faces[i][4];
               occupied[pos] = MACHINE;
               positions.set(pos, MACHINE);
               player = update_logic_arrays(pos);
               return true;
            }
         }

      }

      if (debug) System.out.println("block_walk_move: false");
      return false;
   }

   public boolean check_chair_move() {

      int pos;

      // If the "block chair flag" is set, all we need to do is
      // block the winning path...
      if (block_chair_flag) {
         pos = faces[block_chair_face][block_chair_next_move];
         occupied[pos] = MACHINE;
         positions.set(pos, MACHINE);
         player = update_logic_arrays(pos);
         if (debug) System.out.println("block_chair_move: march");
         return true;
      }

      int ncorners = 0;    // Number of corners owned by human
      int corner   = 0;    // Corner owned by machine

      // Loop through all of the faces.
      for(int i=0; i<18; i++) {

         // Determine which corners the human owns.
         if (faces[i][ 2] == HUMAN)
            ncorners++;   
         else
            corner = 2;
         if (faces[i][ 5] == HUMAN) 
            ncorners++;   
         else
            corner = 5;
         if (faces[i][14] == HUMAN)
            ncorners++;   
         else
            corner = 14;
         if (faces[i][17] == HUMAN)
            ncorners++;   
         else
            corner = 17;

         // If the human owns three corners, continue with the search.
         if (ncorners == 3) {
            if (corner == 2) {
               if (faces[i][ 3] == HUMAN && faces[i][ 7] == 0 &&
                   faces[i][ 8] == 0     && faces[i][11] == 0 &&
                   faces[i][15] == 0     && faces[i][16] == 0) {
                  block_chair_flag = true;
                  block_chair_next_move = 11;
                  block_chair_face = i;
                  pos = faces[i][15];
                  occupied[pos] = MACHINE;
                  positions.set(pos, MACHINE);
                  player = update_logic_arrays(pos);
                  if (debug) System.out.println("block_chair_move: found");
                  return true;
               }
               if (faces[i][ 4] == HUMAN && faces[i][ 8] == 0 &&
                   faces[i][11] == 0     && faces[i][12] == 0 &&
                   faces[i][15] == 0     && faces[i][16] == 0) {
                  block_chair_flag = true;
                  block_chair_next_move = 16;
                  block_chair_face = i;
                  pos = faces[i][8];
                  occupied[pos] = MACHINE;
                  positions.set(pos, MACHINE);
                  player = update_logic_arrays(pos);
                  if (debug) System.out.println("block_chair_move: found");
                  return true;
               }
            }
            else if (corner == 5) {
                  block_chair_flag = true;
                  block_chair_next_move = 11;
                  block_chair_face = i;
                  pos = faces[i][15];
                  occupied[pos] = MACHINE;
                  positions.set(pos, MACHINE);
                  player = update_logic_arrays(pos);
                  if (debug) System.out.println("check_face_three:  true");
                  return true;
            }
            else if (corner == 14) {
                  block_chair_flag = true;
                  block_chair_next_move = 11;
                  block_chair_face = i;
                  pos = faces[i][15];
                  occupied[pos] = MACHINE;
                  positions.set(pos, MACHINE);
                  player = update_logic_arrays(pos);
                  if (debug) System.out.println("check_face_three:  true");
                  return true;
            }
            else if (corner == 17) {
                  block_chair_flag = true;
                  block_chair_next_move = 11;
                  block_chair_face = i;
                  pos = faces[i][15];
                  occupied[pos] = MACHINE;
                  positions.set(pos, MACHINE);
                  player = update_logic_arrays(pos);
                  if (debug) System.out.println("check_face_three:  true");
                  return true;
            }
         }
      }
      return false;
   }

   /**
    *  Take inside four
    */
   public boolean take_inside_four() {

      int pos = 0;
      boolean found = false;

      if (occupied[21] == 0) {
         found = true;
         pos = 21;
      }
      else if (occupied[22] == 0) {
         found = true;
         pos = 22;
      }
      else if (occupied[25] == 0) {
         found = true;
         pos = 25;
      }
      else if (occupied[26] == 0) {
         found = true;
         pos = 26;
      }
      else if (occupied[37] == 0) {
         found = true;
         pos = 37;
      }
      else if (occupied[38] == 0) {
         found = true;
         pos = 38;
      }
      else if (occupied[41] == 0) {
         found = true;
         pos = 41;
      }
      else if (occupied[42] == 0) {
         found = true;
         pos = 42;
      }
 
      if (found) {
         occupied[pos] = MACHINE;
         positions.set(pos, MACHINE);
         player = update_logic_arrays(pos);
         if (debug) System.out.println("take_inside_four:  true");
         return true;
      }

      if (debug) System.out.println("take_inside_four:  false");
      return false;
   }


   /**
    *  Check occupancy of outside four.
    */
   public boolean check_outside_four() {

      int pos = 0;

      // Finish off the four corner combination.
      if (outside_four_flag) {
         if (occupied[faces[face_index][7]] == 0) {
            pos = faces[face_index][7];
         }
         else if (occupied[faces[face_index][6]] == 0) {
            pos = faces[face_index][6];
         }
       
         if (occupied[pos] == 0) {
            occupied[pos] = MACHINE;
            positions.set(pos, MACHINE);
            player = update_logic_arrays(pos);
            return true;  
         }
      }

      // Look for a four corner combination.
      for (int i=0; i<18; i++) {
         if (outside_four[i][0] == 4 &&
             outside_four[i][1] == MACHINE) {
            if (faces[i][0] > 0 &&
                faces[i][1] == MACHINE) {
               if (occupied[faces[i][8]] == 0) {
                  pos = faces[i][8];
                  outside_four_flag = true;
                  face_index = i; 
               }
               if (occupied[pos] == 0) {
                  occupied[pos] = MACHINE;
                  positions.set(pos, MACHINE);
                  player = update_logic_arrays(pos);
                  if (debug) System.out.println("check_outside_four:  true");
                  return true;  
               }
            }
         }
      }

      // Take the corners, if available.
      for (int i=0; i<18; i++) {
         if (outside_four[i][0] > 0 &&
             outside_four[i][1] == MACHINE) {
            if (faces[i][0] > 0 &&
                faces[i][1] == MACHINE) {
               for (int j=2; j<6; j++) {
                  pos = outside_four[i][j];
                  if (occupied[pos] == 0) {
                     occupied[pos] = MACHINE;
                     positions.set(pos, MACHINE);
                     player = update_logic_arrays(pos);
                     if (debug) System.out.println("check_outside_four:  true");
                     return true;  
                  }
               }
            }
         }
      }

      // Look for an "outside four" combination in a face in which the 
      // opponent holds no positions.
      for (int i=0; i<18; i++) {
         if (outside_four[i][0] == 0 || (outside_four[i][0] > 0 &&
             outside_four[i][1] == MACHINE)) {

            if (outside_four[i][1] == MACHINE)
                outside_four_flag = true;
            for (int j=2; j<6; j++) {
               pos = outside_four[i][j];
               if (occupied[pos] == 0) {
                  occupied[pos] = MACHINE;
                  positions.set(pos, MACHINE);
                  player = update_logic_arrays(pos);
                  if (debug) System.out.println("check_outside_four:  true");
                  return true;  
               }
            }
         }
      }

      if (debug) System.out.println("check_outside_four:  false");
      return false;
   }


   /**
    *  Take outside four
    */
   public boolean take_outside_four() {
 
      int pos = 0;
      boolean found = false; 
 
      if (occupied[0] == 0) {
         found = true; 
         pos = 0; 
      }  
      else if (occupied[3] == 0) {
         found = true;   
         pos = 3;   
      }  
      else if (occupied[12] == 0) {
         found = true;   
         pos = 12;   
      }  
      else if (occupied[15] == 0) {
         found = true;   
         pos = 15;   
      }  
      else if (occupied[48] == 0) {
         found = true;   
         pos = 48;  
      }  
      else if (occupied[51] == 0) {
         found = true;   
         pos = 51;   
      }  
      else if (occupied[60] == 0) {
         found = true;   
         pos = 60;   
      }  
      else if (occupied[63] == 0) {
         found = true;   
         pos = 63;
      }  
  
      if (found) { 
         occupied[pos] = MACHINE;
         positions.set(pos, MACHINE);
         player = update_logic_arrays(pos);
         if (debug) System.out.println("take_outside_four:  true");
         return true;
      }
 
      if (debug) System.out.println("take_outside_four:  false");
      return false;
   }
 

   /**
    *  Check for a forced win by intersecting rows. Block
    *  if necessary.
    */
   public boolean block_intersecting_rows() {

      int pos;

      // Loop through each row and check for rows that have two
      // positions occupied by the human and two positions which are empty.
      // Make sure that none of the empty positions in this row intersect 
      // with another row that also contains two positions held by the human. 
      // If so, block the row by taking the position at the intersection
      // of these two row.

      // Loop through each row.
      for (int i=0; i<76; i++) {

         // Look for a row that has two positions held by the human. 
         if (combinations[i][0] == 2  && combinations[i][1] == HUMAN) {

            if (debug)
               System.out.println("   row " + i + "has 2 positions occupied by the human");

            // Mark this row with a flag.
            combinations[i][6] = 1;

            // Check each position in the row.
            for (int j=2; j<6; j++) {

               // Look for the empty positions in the row.
               pos = combinations[i][j];
               if (occupied[pos] == 0) {

                  // Loop through the rows again.
                  for (int k=0; k<76; k++) {

                     if (debug) System.out.println("   row " + k);

                     // Look for another row that has two positions held
                     // by the human (and which is unmarked.) modified
                     if (combinations[k][0] == 2     &&
                         combinations[k][1] == HUMAN &&
                         combinations[k][6] == 0)       {

                        if (debug)
                           System.out.println("found an intersecting row:   row " + k);

                        // Check the positions in this row and see if
                        // any match the position we're looking for. If
                        // we find a match, grab the position and return.
                        for (int l=2; l<6; l++) {
                           if (pos == combinations[k][l]) {
                              combinations[i][6] = 0;
                              occupied[pos] = MACHINE;
                              positions.set(pos, MACHINE);
                              player = update_logic_arrays(pos);
                              if (debug) System.out.println("block_intersecting_rows:  true");
                              return true;
                           }
                        }
                     }
                  }
               }
            }

            // Unmark the combination before moving on.
            combinations[i][6] = 0;
         }

      }
      if (debug) System.out.println("block_intersecting_rows:  false");
      return false;
   }

   /**
    *  Check for a forced win by intersecting rows. Block
    *  if necessary.
    */
   public boolean check_intersecting_rows2() {

      int pos;

      // Loop through each row and check for rows that have two
      // positions occupied by the human and two positions which are empty.
      // Make sure that none of the empty positions in this row intersect 
      // with another row that also contains two positions held by the human. 
      // If so, block the row by taking the position at the intersection
      // of these two row.

      // Loop through each row.
      for (int i=0; i<76; i++) {

         // Look for a row that has two positions held by the human. 
         if (combinations[i][0] == 2  && combinations[i][1] == HUMAN) {

            if (debug) {
               System.out.println("   row " + i + "has 2 positions occupied by the human");
            }

            // Mark this row with a flag.
            combinations[i][6] = 1;

            // Check each position in the row.
            for (int j=2; j<6; j++) {

               // Look for the empty positions in the row.
               pos = combinations[i][j];
               if (occupied[pos] == 0) {

                  // Loop through the rows again.
                  for (int k=0; k<76; k++) {

                     if (debug) System.out.println("   row " + k);

                     // Look for another row that has two positions held
                     // by the human (and which is unmarked.) modified
                     if (combinations[k][0] == 1     &&
                         combinations[k][1] == HUMAN &&
                         combinations[k][6] == 0)       {

                        if (debug)
                           System.out.println("found an intersecting row:   row " + k);

                        // Check the positions in this row and see if
                        // any match the position we're looking for. If
                        // we find a match, grab the position and return.
                        for (int l=2; l<6; l++) {
                           if (pos == combinations[k][l]) {
                              combinations[i][6] = 0;
                              occupied[pos] = MACHINE;
                              positions.set(pos, MACHINE);
                              player = update_logic_arrays(pos);
                              if (debug) System.out.println("check_intersecting_rows:  true");
                              return true;
                           }
                        }
                     }
                  }
               }
            }

            // Unmark the combination before moving on.
            combinations[i][6] = 0;
         }

      }
      if (debug) System.out.println("check_intersecting_rows:  false");
      return false;
   }


   /**
    *  Check for a forced win by intersecting rows. Block
    *  if necessary.
    */
   public boolean check_for_two() {

      int pos;

      // Loop through the rows.
      for (int i=0; i<76; i++) {

         // Look for a row that has two positions held
         // by the human (and which is unmarked.)
         if (combinations[i][0] == 2     &&  
             combinations[i][1] == HUMAN &&
             combinations[i][6] == 0)       {
 
            // Take the first available spot.
            for (int j=2; j<6; j++) {
               pos = combinations[i][j];
               if (occupied[pos] == 0) {
                  occupied[pos] = MACHINE;
                  positions.set(pos, MACHINE);
                  player = update_logic_arrays(pos);
                  if (debug) System.out.println("check_for_two:  true");
                  return true;  
               }
            }   

         }   
      }   
      if (debug) System.out.println("check_for_two:  false");
      return false;
   }

   public void undo_move() {

      // Return if no moves are recorded
      if (nmoves == 0) return;

      // Set the undo flag
      undoFlag = true;

      // Undo the last two moves
      positions.clear(moves[--nmoves]);
      positions.clear(moves[--nmoves]);

      // Undo the winner flag in the positions object
      positions.noWinner();

      // Repaint the 2D canvas.
      canvas.repaint();

      // Reset the inside/outside flags
      inside_four_flag = false;
      outside_four_flag = false;
      block_chair_flag = false;

      // Reset the board
      for (int i=0; i<64; i++) {
         occupied[i] = 0;
      }

      // Reset the inside/outside arrays
      for (int i=0; i<18; i++) {
          inside_four[i][0] = 0;
          inside_four[i][1] = 0;
          outside_four[i][0] = 0;
          outside_four[i][1] = 0;
      }

      // Reset the faces array
      for (int i=0; i<18; i++) {
          faces[i][0] = 0;
          faces[i][1] = 0;
      }

      // Reset the combinations array
      for (int i=0; i<76; i++) {
         combinations[i][0] = 0;
         combinations[i][1] = 0;
      }

      if (nmoves == 0) {
         undoFlag = false;
         player = HUMAN;
         return;
      }

      // Update the logic arrays
      int pos;
      player = HUMAN;
      for (int i=0; i<nmoves; i++) {
         pos = moves[i]; 
         occupied[pos] = player;
         player = update_logic_arrays(pos);
      }

      // Reset the "best picks" array
      update_best_picks();

      // Reset the player and undo flag
      player = HUMAN;
      undoFlag = false;
   }

   /**
    *  Update the logic arrays that keep track of positions and status.
    *  If we have a winner, stop the game.
    */
   public int update_logic_arrays(int pos) {

      // Record the move.
      if (!undoFlag) {
         moves[nmoves++] = pos;
      }

      // Get the number of combinations that this position has.
      int num_combinations = pos_to_comb[pos][0];

      // Go through each combination associated with this position 
      // and update the status. If we have a winner, stop the game.
      int comb;
      for (int j=0; j<num_combinations; j++) {
         comb = pos_to_comb[pos][j+1];
         if (combinations[comb][1] != player &&
             combinations[comb][1] != 0) {
            combinations[comb][0] = -1;
         }
         else {
            combinations[comb][0]++;
            if (combinations[comb][0] == 4) {
               end_time = System.currentTimeMillis();
               time = (end_time - beg_time)/1000;
               panel.winner(player, skill_level, nmoves, time);
               panel.repaint();
               canvas.repaint();
               positions.winner();
               return END;
            }
            else {
               combinations[comb][1] = player;
            }
         }      
      }  

      // Update the best_picks array.
      update_best_picks();

      // Update the inside_four array.
      for (int i=0; i<18; i++) {
         for (int j=2; j<6; j++) {
            if (pos == inside_four[i][j]) {
                if (inside_four[i][0] == 0) {
                   inside_four[i][0] = 1;
                   inside_four[i][1] = player;
                }
                else if (inside_four[i][1] == player) {
                   inside_four[i][0]++;
                   inside_four[i][1] = player;
                }
                else {
                   inside_four[i][0] = -1;
                }
            }
         }
      }

      // Update the outside_four array.
      for (int i=0; i<18; i++) {
         for (int j=2; j<6; j++) {
            if (pos == outside_four[i][j]) {
               if (outside_four[i][0] == 0) {
                  outside_four[i][0] = 1;
                  outside_four[i][1] = player;
               }
               else if (outside_four[i][1] == player) {
                  outside_four[i][0]++;
                  outside_four[i][1] = player;
               }
               else {
                  outside_four[i][0] = -1;
               }
            }
         }
      }
      
      // Update the faces array.
      for (int i=0; i<18; i++) {
         for (int j=2; j<18; j++) {
            if (pos == faces[i][j]) {
               if (faces[i][0] == 0) {
                  faces[i][0] = 1;
                  faces[i][1] = player;
               }
               else if (faces[i][1] == player) {
                  faces[i][0]++;
               }
               else {
                  faces[i][0] = -1;
               }
            }
         }
          
      }
 
      // Switch players.
      if (player == HUMAN)
         return MACHINE;
      else
         return HUMAN;
   }


   /**
    *  Start a new game.
    */
   public void newGame() {

      // Initialize the inside/outside flags.
      inside_four_flag = false;
      outside_four_flag = false;
      block_chair_flag = false;

      // Initialize the inside/outside arrays.
      for (int i=0; i<18; i++) {
          inside_four[i][0] = 0;
          inside_four[i][1] = 0;
          outside_four[i][0] = 0;
          outside_four[i][1] = 0;
      }

      // Initialize the faces array.
      for (int i=0; i<18; i++) {
          faces[i][0] = 0;
          faces[i][1] = 0;
      }

      // Initialize the board.
      for (int i=0; i<64; i++) {
         occupied[i] = 0;
      }
      for (int i=0; i<76; i++) {
         combinations[i][0] = 0;
         combinations[i][1] = 0;
      }

      // Reset the best_picks array.
      update_best_picks();

      // Set the player with the first move.
      player = HUMAN;

      // Initialize the number of moves.
      nmoves = 0;

      // Reset the playing positions.
      positions.newGame();
   }


   /**
    *  Set the skill level.
    */
   public void set_skill_level(int level) {
      skill_level = level;
   }


   /**
    *  Set up the pos_to_comb array.
    */
   public void setup_pos_to_comb() {

      // Set up the pos_to_comb array to point to every winning
      // combination a given position may have.
      int count;
      for (int i=0; i<64; i++) {
          count = 1;
          pos_to_comb[i][0] = 0;
          for (int j=0; j<76; j++) {
                for (int k=2; k<6; k++) {
                   if (combinations[j][k] == i) {
                       pos_to_comb[i][0]++;
                       pos_to_comb[i][count++] = j;
                   }
                }
          }
      }

      if (debug) {
         for (int i=0; i<64; i++) {
             System.out.println("");
             for (int j=0; j<8; j++) {
                System.out.println("pos_to_comb[" + i + "][" + j + "] = " + pos_to_comb[i][j]);
             }
         }
      }

   }


   /**
    *  Update the best_picks array.
    */
   public void update_best_picks() {

      // Re-calculate the best_picks array to point to every (current) winning 
      // combination a given position may have.
      int count;
      for (int i=0; i<64; i++) {

          count = 1;
          best_picks[i][0] = 0;
          if (occupied[i] == 0) {
             for (int j=0; j<76; j++) {

                if (combinations[j][0] == 0 ||
                    combinations[j][1] == MACHINE) {

                   for (int k=2; k<6; k++) {
                      if (combinations[j][k] == i) {
                         best_picks[i][0]++;
                         best_picks[i][count++] = j;
                      }
                   }
                }
             }
          }
      }

      if (debug) {
         for (int i=0; i<64; i++) {
             System.out.println("");
             for (int j=0; j<8; j++) {
                System.out.println("best_picks[" + i + "][" + j + "] = " + best_picks[i][j]); 
             }
         }
      }
   }


   /**
    *  Pick the computer's best possible move based on the number
    *  of combinations per position. Choose the position with the
    *  most combinations.
    */
   public void pick_best_position() {

      int pos = 0;
      int max_num = 0;
      for (int i=0; i<64; i++) {
         if (best_picks[i][0] > max_num &&
             occupied[i] == 0) {
            pos = i;
            max_num = best_picks[i][0];
         }
      }

      // Mark the position as MACHINE.
      occupied[pos] = MACHINE;

      positions.set(pos, MACHINE);

      // Udate the logic arrays and reset the player.
      player = update_logic_arrays(pos);
   }


   public boolean pick_7() {

      for (int i=0; i<64; i++) {
         if (best_picks[i][0] == 7) { 
            occupied[i] = MACHINE;
            positions.set(i, MACHINE);
            player = update_logic_arrays(i);
            return true;
         }
      }
      return false;

   }
  
   public void change_face() {
      current_face = ++current_face%18;
   }
  
   public void label() {
      label_flag ^= true;
   }

   public boolean unoccupied(int pos) {
      if (occupied[pos] == UNOCCUPIED)
         return true;
      else
         return false;
   } 
}
