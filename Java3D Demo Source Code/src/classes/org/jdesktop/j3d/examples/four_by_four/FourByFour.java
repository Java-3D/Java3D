/*
 * $RCSfile: FourByFour.java,v $
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
 * $Revision: 1.4 $
 * $Date: 2007/02/09 17:21:37 $
 * $State: Exp $
 */

package org.jdesktop.j3d.examples.four_by_four;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.applet.MainFrame;
import org.jdesktop.j3d.examples.Resources;

/**
 * Class        FourByFour
 *
 * Description: High level class for the game FourByFour
 *
 * Version:     1.2
 *
 */
public class FourByFour extends Applet implements ActionListener {

    // To write scores to scores file
    private static final boolean  writeScoresFile = false;
    
   String host;                    // Host from which this applet came from
   int port;                       // Port number for writing high scores
   Image backbuffer2D;             // Backbuffer image used for 2D double buffering
   int width, height;              // Size of the graphics window in pixels
   int score;                      // Final game score
   int level_weight;               // Weighting factor for skill level 
   int move_weight;                // Weighting factor for number of moves to win
   int time_weight;                // Weighting factor for amount of time it took to win
   int skill_level;                // Skill level, 0 - 4
   Canvas2D canvas2D;              // 2D rendering canvas
   Canvas3D canvas3D;              // 3D rendering canvas
   Board board;                    // Game board object
   Panel b_container;              // Container to hold the buttons
   Panel c_container;              // Container to hold the canvas
   Panel l_container;              // Container to hold the labels
   Panel skill_panel;              // Panel to hold skill levels
   Panel instruct_panel;           // Panel to hold instructions
   Panel winner_panel;             // Panel to hold winner announcement
   Panel high_panel;               // Panel to hold high scores
   Button instruct_button;         // Instructions button
   Button new_button;              // New Game button
   Button skill_button;            // Skill Level button
   Button high_button;             // High Scores button
   Button undo_button;             // Undo Move button
   Label skill_label;              // Label on skill panel
   Label winner_label;             // Label on winner panel
   Label winner_score_label;       // Score label on winner panel
   Label winner_name_label;        // Name label on winner panel
   Label winner_top_label;         // Top 20 label on winner panel
   Label high_label;               // High score label
   Label high_places[];            // Labels to hold places
   Label high_names[];             // Labels to hold names
   Label high_scores[];            // Labels to hold scores 
   TextArea instruct_text;         // TextArea object that holds instructions
   TextArea high_text;             // TextArea object that holds top 20 scores 
   TextField winner_name;          // TextField object that holds winner's name
   Button instruct_return_button;  // Return button for instruction panel
   Button skill_return_button;     // Return button for skill level panel
   Button winner_return_button;    // Return button for winner panel
   Button high_return_button;      // Return button for high scores panel 
   CheckboxGroup group;            // CheckboxGroup object for skill level panel
   InputStream inStream;           // Input stream for reading instructions and high scores 
   static boolean appletFlag = true;      // Applet flag
   boolean winner_flag = false;    // Winner flag
   byte text[];                    // Temporary storage area for reading instructions file
   byte outText[];                 // Temporary storage area for writing high scores file
   String textString;              // Storage area for instructions
   String scoresString;            // String used for writing high scores file
   int places[];                   // Storage area for high score places 
   int scores[];                   // Storage area for high score scores
   String names[];                 // Storage area for high score names
   Positions positions;            // Positions object, used to render player positions

    private SimpleUniverse universe = null;

   /**
    * Initialization
    */
   public void init() {

      // Set the port number.
      port = 4111;

      // Set the graphics window size.
      width  = 350;
      height = 350;

      // Set the weighting factors used for scoring.
      level_weight = 1311;
      move_weight  =  111;
      time_weight  = 1000;

      // Create the "base" color for the AWT components.
      setBackground(new Color(200, 200, 200));

      // Read the instructions file.
       if (appletFlag) {

         // Get the host from which this applet came.
         host = getCodeBase().getHost();

         try {
            URL instrURL = Resources.getResource("four_by_four/instructions.txt");
            inStream = new BufferedInputStream((instrURL).openStream(), 8192);
            text = new byte[5000];
            int character = inStream.read();
            int count = 0;
            while (character != -1) {
               text[count++] = (byte) character;
               character = inStream.read();
            }
            textString = new String(text);
            inStream.close();
         }
         catch(Exception e) {
            System.out.println("Error: " + e.toString());
         }
      }
      else {

         try {
            URL instrURL = Resources.getResource("four_by_four/instructions.txt");
            inStream = new BufferedInputStream((instrURL).openStream(), 8192);
            text = new byte[5000];
            int character = inStream.read();
            int count = 0;
            while (character != -1) {
               text[count++] = (byte) character;
               character = inStream.read();
            }
            textString = new String(text);
            inStream.close();
         }
         catch(Exception e) {
            System.out.println("Error: " + e.toString());
         }
      }

      // Read the high-scores file.
      places = new int[20];
      scores = new int[20];
      names  = new String[20];
      if (appletFlag) {
         try {
            URL scoreURL = Resources.getResource("four_by_four/scores.txt");
            inStream = new BufferedInputStream((scoreURL).openStream(), 8192);
            Reader read = new BufferedReader(new InputStreamReader(inStream));
            StreamTokenizer st = new StreamTokenizer(read);
            st.whitespaceChars(32,44);
            st.eolIsSignificant(false);

            int count = 0;
            int token = st.nextToken();
            boolean scoreFlag = true;
            String string;
            while (count<20) {
               places[count] = (int) st.nval;
               string = new String("");
               token = st.nextToken();
               while (token == StreamTokenizer.TT_WORD) {
                  string += st.sval; 
                  string += " ";
                  token = st.nextToken();
               }
               names[count] = string; 
               scores[count] = (int) st.nval;
               token = st.nextToken();
               count++;
            } 
            inStream.close();
         }
         catch(Exception e) {
            System.out.println("Error: " + e.toString());
         }
      }
      else {
         try {
            URL scoreURL = Resources.getResource("four_by_four/scores.txt");
            inStream = new BufferedInputStream((scoreURL).openStream(), 8192);
            Reader read = new BufferedReader(new InputStreamReader(inStream));
            StreamTokenizer st = new StreamTokenizer(read);
            st.whitespaceChars(32,44);
            st.eolIsSignificant(false);

            int count = 0;
            int token = st.nextToken();
            boolean scoreFlag = true;
            String string;
            while (count<20) {
               places[count] = (int) st.nval;
               string = new String("");
               token = st.nextToken();
               while (token == StreamTokenizer.TT_WORD) {
                  string += st.sval;
                  string += " ";
                  token = st.nextToken();
               }
               names[count] = string;
               scores[count] = (int) st.nval;
               token = st.nextToken();
               count++;
            }
            inStream.close();
         }
         catch(Exception e) {
            System.out.println("Error: " + e.toString());
         }
      }

      // The positions object sets up the switch nodes which
      // control the rendering of the player's positions.
      positions = new Positions();

      // Create the game board object which is responsible
      // for keeping track of the moves on the game board
      // and determining what move the computer should make.
      board = new Board(this, positions, width, height);
      positions.setBoard(board);

      // Create a 2D graphics canvas.
      canvas2D = new Canvas2D(board);
      canvas2D.setSize(width, height);
      canvas2D.setLocation(width+10, 5);
      canvas2D.addMouseListener(canvas2D);
      board.setCanvas(canvas2D);

      // Create the 2D backbuffer
      backbuffer2D = createImage(width, height);
      canvas2D.setBuffer(backbuffer2D);

      // Create a 3D graphics canvas.
      canvas3D = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
      canvas3D.setSize(width, height);
      canvas3D.setLocation(5, 5);

      // Create the scene branchgroup.
      BranchGroup scene3D = createScene3D();

      // Create a universe with the Java3D universe utility.
      universe = new SimpleUniverse(canvas3D);
      universe.addBranchGraph(scene3D);
  
      // Use parallel projection.
      View view = universe.getViewer().getView();
      view.setProjectionPolicy(View.PARALLEL_PROJECTION);

      // Set the universe Transform3D object.
      TransformGroup tg = 
         universe.getViewingPlatform().getViewPlatformTransform();
      Transform3D transform = new Transform3D();
      transform.set(65.f, new Vector3f(0.0f, 0.0f, 400.0f));
      tg.setTransform(transform);

      // Create the canvas container.
      c_container = new Panel();
      c_container.setSize(720, 360);
      c_container.setLocation(0, 0);
      c_container.setVisible(true);
      c_container.setLayout(null);
      add(c_container);

      // Add the 2D and 3D canvases to the container.
      c_container.add(canvas2D);
      c_container.add(canvas3D);

      // Turn off the layout manager, widgets will be sized 
      // and positioned explicitly.
      setLayout(null);

      // Create the button container.
      b_container = new Panel();
      b_container.setSize(720, 70);
      b_container.setLocation(0, 360);
      b_container.setVisible(true);
      b_container.setLayout(null);

      // Create the buttons.
      instruct_button = new Button("Instructions");
      instruct_button.setSize(135, 25);
      instruct_button.setLocation(10, 10);
      instruct_button.setVisible(true);
      instruct_button.addActionListener(this);

      new_button = new Button("New Game");
      new_button.setSize(135, 25);
      new_button.setLocation(150, 10);
      new_button.setVisible(true);
      new_button.addActionListener(this);

      undo_button = new Button("Undo Move");
      undo_button.setSize(135, 25);
      undo_button.setLocation(290, 10);
      undo_button.setVisible(true);
      undo_button.addActionListener(this);

      skill_button = new Button("Skill Level");
      skill_button.setSize(135, 25);
      skill_button.setLocation(430, 10);
      skill_button.setVisible(true);
      skill_button.addActionListener(this);

      high_button = new Button("High Scores");
      high_button.setSize(135, 25);
      high_button.setLocation(570, 10);
      high_button.setVisible(true);
      high_button.addActionListener(this);

      b_container.add(new_button);
      b_container.add(undo_button);
      b_container.add(skill_button);
      b_container.add(high_button);
      b_container.add(instruct_button);

      // Add the button container to the applet.
      add(b_container);

      // Create the "Skill Level" dialog box.
      skill_panel = new Panel();
      skill_panel.setSize(400, 300);
      skill_panel.setLocation(200, 20);
      skill_panel.setLayout(null);

      skill_label = new Label("Pick your skill level:");
      skill_label.setSize(200, 25);
      skill_label.setLocation(25, 20);
      skill_label.setVisible(true);
      skill_panel.add(skill_label);

      group = new CheckboxGroup();
      Checkbox skill_1 = new Checkbox("Babe in the Woods        ", group, false);
      Checkbox skill_2 = new Checkbox("Walk and Chew Gum        ", group, false);
      Checkbox skill_3 = new Checkbox("Jeopardy Contestant      ", group, false);
      Checkbox skill_4 = new Checkbox("Rocket Scientist         ", group, false);
      Checkbox skill_5 = new Checkbox("Be afraid, be very afraid", group, true);
      skill_1.setSize(170, 25);
      skill_1.setLocation(80, 60);
      skill_1.setVisible(true);
      skill_2.setSize(170, 25);
      skill_2.setLocation(80, 100);
      skill_2.setVisible(true);
      skill_3.setSize(170, 25);
      skill_3.setLocation(80, 140);
      skill_3.setVisible(true);
      skill_4.setSize(170, 25);
      skill_4.setLocation(80, 180);
      skill_4.setVisible(true);
      skill_5.setSize(170, 25);
      skill_5.setLocation(80, 220);
      skill_5.setVisible(true);
      skill_return_button = new Button("Return");
      skill_return_button.setSize(120, 25);
      skill_return_button.setLocation(300, 370);
      skill_return_button.setVisible(false);
      skill_return_button.addActionListener(this);
      skill_panel.add(skill_1);
      skill_panel.add(skill_2);
      skill_panel.add(skill_3);
      skill_panel.add(skill_4);
      skill_panel.add(skill_5);
      skill_panel.setVisible(false);
      add(skill_return_button);
      add(skill_panel);

      // Create the "Instructions" panel.
      instruct_return_button = new Button("Return");
      instruct_return_button.setLocation(300, 370);
      instruct_return_button.setSize(120, 25);
      instruct_return_button.setVisible(false);
      instruct_return_button.addActionListener(this);
      instruct_text = 
         new TextArea(textString, 100, 200, TextArea.SCROLLBARS_VERTICAL_ONLY);
      instruct_text.setSize(715, 350);
      instruct_text.setLocation(0, 0);
      instruct_text.setVisible(false);
      add(instruct_text);

      add(instruct_return_button);

      high_panel = new Panel();
      high_panel.setSize(715, 350);
      high_panel.setLocation(0, 0);
      high_panel.setVisible(false);
      high_panel.setLayout(null);

      high_label = new Label("High Scores");
      high_label.setLocation(330, 5);
      high_label.setSize(200, 30);
      high_label.setVisible(true);
      high_panel.add(high_label);

      high_places = new Label[20];
      high_names  = new Label[20];
      high_scores = new Label[20];
      for (int i=0; i<20; i++) {
         high_places[i] = new Label(Integer.toString(i+1));
         high_places[i].setSize(20, 30);
         high_places[i].setVisible(true);
         high_names[i] = new Label(names[i]);
         high_names[i].setSize(150, 30);
         high_names[i].setVisible(true);
         high_scores[i] = new Label(Integer.toString(scores[i]));
         high_scores[i].setSize(150, 30);
         high_scores[i].setVisible(true);
         if (i<10) {
            high_places[i].setLocation(70, i*30+40);
            high_names[i].setLocation(100, i*30+40);
            high_scores[i].setLocation(260, i*30+40);
         }
         else {
            high_places[i].setLocation(425, (i-10)*30+40);
            high_names[i].setLocation(455, (i-10)*30+40);
            high_scores[i].setLocation(615, (i-10)*30+40);
         }
         high_panel.add(high_places[i]);
         high_panel.add(high_names[i]);
         high_panel.add(high_scores[i]);
      }
      high_return_button = new Button("Return");
      high_return_button.setSize(120, 25);
      high_return_button.setLocation(300, 370);
      high_return_button.setVisible(false);
      high_return_button.addActionListener(this);
      add(high_return_button);
      add(high_panel);

      // Create the "Winner" dialog box
      winner_panel = new Panel();
      winner_panel.setLayout(null);
      winner_panel.setSize(600, 500);
      winner_panel.setLocation(0, 0);
      winner_return_button = new Button("Return");
      winner_return_button.setSize(120, 25);
      winner_return_button.setLocation(300, 360);
      winner_return_button.addActionListener(this);
      winner_panel.add(winner_return_button);
      winner_label = new Label("");
      winner_label.setSize(200, 30);
      winner_label.setLocation(270, 110);
      winner_score_label = new Label("");
      winner_score_label.setSize(200, 30);
      winner_top_label = new Label("You have a score in the top 20.");
      winner_top_label.setSize(200, 25);
      winner_top_label.setLocation(260, 185);
      winner_top_label.setVisible(false); winner_name_label = new Label("Enter your name here:");
      winner_name_label.setSize(150, 25);
      winner_name_label.setLocation(260, 210);
      winner_name_label.setVisible(false);
      winner_name = new TextField("");
      winner_name.setSize(200, 30);
      winner_name.setLocation(260, 240);
      winner_name.setVisible(false);
      winner_panel.add(winner_label);
      winner_panel.add(winner_score_label);
      winner_panel.add(winner_top_label);
      winner_panel.add(winner_name_label);
      winner_panel.add(winner_name);
      winner_panel.setVisible(false);
      add(winner_panel);
   }

    public void destroy() {
	universe.cleanup();
    }

   /**
    *  Create the scenegraph for the 3D view.
    */
   public BranchGroup createScene3D() {

      // Define colors
      Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
      Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
      Color3f red   = new Color3f(0.80f, 0.20f, 0.2f);
      Color3f ambient = new Color3f(0.25f, 0.25f, 0.25f);
      Color3f diffuse = new Color3f(0.7f, 0.7f, 0.7f);
      Color3f specular = new Color3f(0.9f, 0.9f, 0.9f);
      Color3f ambientRed = new Color3f(0.2f, 0.05f, 0.0f);
      Color3f bgColor = new Color3f(0.05f, 0.05f, 0.2f);

      // Create the branch group
      BranchGroup branchGroup = new BranchGroup();

      // Create the bounding leaf node
      BoundingSphere bounds =
         new BoundingSphere(new Point3d(0.0,0.0,0.0), 1000.0);
      BoundingLeaf boundingLeaf = new BoundingLeaf(bounds);
      branchGroup.addChild(boundingLeaf);

      // Create the background
      Background bg = new Background(bgColor);
      bg.setApplicationBounds(bounds);
      branchGroup.addChild(bg);

      // Create the ambient light
      AmbientLight ambLight = new AmbientLight(white);
      ambLight.setInfluencingBounds(bounds);
      branchGroup.addChild(ambLight);

      // Create the directional light
      Vector3f dir = new Vector3f(-1.0f, -1.0f, -1.0f);
      DirectionalLight dirLight = new DirectionalLight(white, dir);
      dirLight.setInfluencingBounds(bounds);
      branchGroup.addChild(dirLight);

      // Create the pole appearance
      Material poleMaterial =
         new Material(ambient, black, diffuse, specular, 110.f);
      poleMaterial.setLightingEnable(true);
      Appearance poleAppearance = new Appearance();
      poleAppearance.setMaterial(poleMaterial);

      // Create the transform group node
      TransformGroup transformGroup = new TransformGroup();
      transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
      transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
      branchGroup.addChild(transformGroup);

      // Create the poles
      Poles poles = new Poles(poleAppearance);
      transformGroup.addChild(poles.getChild());

      // Add the position markers to the transform group
      transformGroup.addChild(positions.getChild());

      // Let the positions object know about the transform group
      positions.setTransformGroup(transformGroup);

      // Create the mouse pick and drag behavior node
      PickDragBehavior behavior = new PickDragBehavior(canvas2D, canvas3D, positions, 
                                                       branchGroup, transformGroup);
      behavior.setSchedulingBounds(bounds);
      transformGroup.addChild(behavior);

      return branchGroup;
   }

   public void actionPerformed (ActionEvent event) {

      Object target = event.getSource();

      // Process the button events.
      if (target == skill_return_button) {
         skill_panel.setVisible(false);
         skill_return_button.setVisible(false);
         c_container.setVisible(true);
         b_container.setVisible(true);
         newGame();
      }
      else if (target == winner_return_button) {
         if (winner_flag) {
            String name = winner_name.getText();
            String tmp_name = new String("");
            int tmp_score = 0;
            boolean insert_flag = false;
            winner_flag = false;
            for (int i=0; i<20; i++) {
               if (insert_flag) {
                  name = names[i];
                  score = scores[i];
                  names[i] = tmp_name;
                  scores[i] = tmp_score;
                  tmp_name = name;
                  tmp_score = score;
               }
               if (!insert_flag && score > scores[i]) {
                  tmp_name = names[i];
                  tmp_score = scores[i];
                  scores[i] = score;
                  names[i] = name;
                  insert_flag = true;
               }
               high_names[i].setText(names[i]);
               high_scores[i].setText(Integer.toString(scores[i]));
            }
            scoresString = new String("");
            int place;
            for (int i=0; i<20; i++) {
               place = (int) places[i];
               scoresString += Integer.toString(place);
               scoresString += "\t";
               scoresString += names[i];
               scoresString += "   ";
               scoresString += Integer.toString(scores[i]);
               scoresString += "\n";
            }

            if(writeScoresFile) {
                if (appletFlag) {
                    try {
                        OutputStreamWriter outFile =
                                new OutputStreamWriter(new FileOutputStream("scores.txt"));
                        outFile.write(scoresString);
                        outFile.flush();
                        outFile.close();
                        outFile = null;
                    } catch (IOException ioe) {
                        System.out.println("Error: " + ioe.toString());
                    } catch (Exception e) {
                        System.out.println("Error: " + e.toString());
                    }
                    
                } else {
                    
                    try {
                        
                        OutputStreamWriter outFile =
                                new OutputStreamWriter(new FileOutputStream("scores.txt"));
                        outFile.write(scoresString);
                        outFile.flush();
                        outFile.close();
                        outFile = null;
                    } catch (IOException ioe) {
                        System.out.println("Error: " + ioe.toString());
                    }
                }
            }
         }
         winner_panel.setVisible(false);
         winner_return_button.setVisible(false);
         winner_label.setVisible(false);
         winner_score_label.setVisible(false);
         winner_name_label.setVisible(false);
         winner_top_label.setVisible(false);
         winner_name.setVisible(false);
         c_container.setVisible(true);
         b_container.setVisible(true);
      }
      else if (target == high_return_button) {
         high_return_button.setVisible(false);
         high_panel.setVisible(false);
         c_container.setVisible(true);
         b_container.setVisible(true);
      }
      else if (target == instruct_return_button) {
         instruct_text.setVisible(false);
         instruct_return_button.setVisible(false);
         instruct_text.repaint();
         c_container.setVisible(true);
         b_container.setVisible(true);
      }
      else if (target == undo_button) {
         board.undo_move();
         canvas2D.repaint();
      }
      else if (target == instruct_button) {
         c_container.setVisible(false);
         b_container.setVisible(false);
         instruct_text.setVisible(true);
         instruct_return_button.setVisible(true);
      }
      else if (target == new_button) {
         newGame();
      }
      else if (target == skill_button) {
         c_container.setVisible(false);
         b_container.setVisible(false);
         skill_panel.setVisible(true);
         skill_return_button.setVisible(true);
      }
      else if (target == high_button) {
         // Read the high scores file.
         if (appletFlag) {
            try {
               URL scoreURL = Resources.getResource("four_by_four/scores.txt");
               inStream = new BufferedInputStream(scoreURL.openStream(), 8192);
               Reader read = new BufferedReader(new InputStreamReader(inStream));
               StreamTokenizer st = new StreamTokenizer(read);
               st.whitespaceChars(32,44);
               st.eolIsSignificant(false);

               int count = 0;
               int token = st.nextToken();
               boolean scoreFlag = true;
               String string;
               while (count<20) {
                  places[count] = (int) st.nval;
                  string = new String("");
                  token = st.nextToken();
                  while (token == StreamTokenizer.TT_WORD) {
                     string += st.sval;
                     string += " ";
                     token = st.nextToken();
                  }
                  names[count] = string;
                  scores[count] = (int) st.nval;
                  token = st.nextToken();
                  count++;
               }
               inStream.close();
            }
            catch(Exception ioe) {
               System.out.println("Error: " + ioe.toString());
            }
         }
         else {
            try {
               URL scoreURL = Resources.getResource("four_by_four/scores.txt");
               inStream = new BufferedInputStream(scoreURL.openStream(), 8192);
               Reader read = new BufferedReader(new InputStreamReader(inStream));
               StreamTokenizer st = new StreamTokenizer(read);
               st.whitespaceChars(32,44);
               st.eolIsSignificant(false);

               int count = 0;
               int token = st.nextToken();
               boolean scoreFlag = true;
               String string;
               while (count<20) {
                  places[count] = (int) st.nval;
                  string = new String("");
                  token = st.nextToken();
                  while (token == StreamTokenizer.TT_WORD) {
                     string += st.sval;
                     string += " ";
                     token = st.nextToken();
                  }
                  names[count] = string;
                  scores[count] = (int) st.nval;
                  token = st.nextToken();
                  count++;
               }
               inStream.close();
            }
            catch(Exception ioe) {
               System.out.println("Error: " + ioe.toString());
            }
         }
         c_container.setVisible(false);
         b_container.setVisible(false);
         high_panel.setVisible(true);
         high_return_button.setVisible(true);
      }

      Checkbox box = group.getSelectedCheckbox(); 
      String label = box.getLabel();
      if (label.equals("Babe in the Woods        ")) {
         board.set_skill_level(0);
      }
      else if (label.equals("Walk and Chew Gum        ")) {
         board.set_skill_level(1);
      }
      else if (label.equals("Jeopardy Contestant      ")) {
         board.set_skill_level(2);
      }
      else if (label.equals("Rocket Scientist         ")) {
         board.set_skill_level(3);
      }
      else if (label.equals("Be afraid, be very afraid")) {
         board.set_skill_level(4);
      }
   }

   public void newGame() {
      board.newGame();         
      canvas2D.repaint();
   }

   public void start() {
      if (appletFlag) showStatus("FourByFour");
   }

   public void winner(int player, int level, int nmoves, long time) {

      if (player == 1) { 
         score = level *  level_weight + 
                 (66 - nmoves) * move_weight -
                 (int) Math.min(time * time_weight, 5000);
         winner_label.setText("Game over, you win!");
         winner_label.setLocation(290, 90);
         winner_score_label.setText("Score = " + score);
         winner_score_label.setVisible(true);
         winner_score_label.setLocation(315, 120);
         if (score > scores[19]) {
            winner_name_label.setVisible(true);
            winner_top_label.setVisible(true);
            winner_name.setVisible(true);
            winner_flag = true;
         }
      }
      else {
         winner_label.setText("Game over, the computer wins!");
         winner_label.setLocation(250, 150);
      }
      c_container.setVisible(false);
      b_container.setVisible(false);
      winner_panel.setVisible(true);
      winner_label.setVisible(true);
      winner_return_button.setVisible(true);
      repaint();
   }

   /**
    *  Inner class used to "kill" the window when running as
    *  an application.
    */
   static class killAdapter extends WindowAdapter {
      public void windowClosing(WindowEvent event) {
         System.exit(0);
      }
   }

  /**
   *  Main method, only used when running as an application.
   */
  public static void main(String[] args) {
    FourByFour.appletFlag = false;
    new MainFrame(new FourByFour(), 730, 450);
  }
  
}
