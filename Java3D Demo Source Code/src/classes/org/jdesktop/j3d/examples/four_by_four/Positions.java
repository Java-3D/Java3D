/*
 * $RCSfile: Positions.java,v $
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

import java.applet.Applet;
import java.awt.event.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.util.BitSet;
import com.sun.j3d.utils.geometry.Sphere;

/**
 * Class:       Positions
 *
 * Description: Creates the position markers.
 *
 * Version:     1.0
 *
 */
public class Positions extends Object {

   final static int UNOCCUPIED = 0;
   final static int HUMAN      = 1;
   final static int MACHINE    = 2;
   final static int END        = 3;

   private Vector3f point[];
   private Switch posSwitch;
   private Switch humanSwitch;
   private Switch machineSwitch;
   private BitSet posMask;
   private BitSet humanMask;
   private BitSet machineMask;
   private Group group;
   private Material redMat;
   private Material blueMat;
   private Material yellowMat;
   private Material whiteMat;
   private Appearance redApp;
   private Appearance blueApp;
   private Appearance yellowApp;
   private Appearance whiteApp;
   private Board board;
   private Sphere posSphere[];
   private BigCube cube[];
   private TransformGroup tgroup;
   private boolean winnerFlag = false;

   public Positions() {

      // Define colors for lighting
      Color3f white     = new Color3f(1.0f, 1.0f, 1.0f);
      Color3f black     = new Color3f(0.0f, 0.0f, 0.0f);
      Color3f red       = new Color3f(0.9f, 0.1f, 0.2f);
      Color3f blue      = new Color3f(0.3f, 0.3f, 0.8f);
      Color3f yellow    = new Color3f(1.0f, 1.0f, 0.0f);
      Color3f ambRed    = new Color3f(0.3f, 0.03f, 0.03f);
      Color3f ambBlue   = new Color3f(0.03f, 0.03f, 0.3f);
      Color3f ambYellow = new Color3f(0.3f, 0.3f, 0.03f);
      Color3f ambWhite  = new Color3f(0.3f, 0.3f, 0.3f);
      Color3f specular  = new Color3f(1.0f, 1.0f, 1.0f);

      // Create the red appearance node
      redMat= new Material(ambRed, black, red, specular, 100.f);
      redMat.setLightingEnable(true);
      redApp = new Appearance();
      redApp.setMaterial(redMat);

      // Create the blue appearance node
      blueMat= new Material(ambBlue, black, blue, specular, 100.f);
      blueMat.setLightingEnable(true);
      blueApp = new Appearance();
      blueApp.setMaterial(blueMat);

      // Create the yellow appearance node
      yellowMat= new Material(ambYellow, black, yellow, specular, 100.f);
      yellowMat.setLightingEnable(true);
      yellowApp = new Appearance();
      yellowApp.setMaterial(yellowMat);

      // Create the white appearance node
      whiteMat= new Material(ambWhite, black, white, specular, 100.f);
      whiteMat.setLightingEnable(true);
      whiteApp = new Appearance();
      whiteApp.setMaterial(whiteMat);

      // Load the point array with the offset (coordinates) for each of 
      // the 64 positions.
      point = new Vector3f[64];
      int count = 0;
      for (int i=-30; i<40; i+=20) {
         for (int j=-30; j<40; j+=20) {
            for (int k=-30; k<40; k+=20) {
               point[count] = new Vector3f((float) k, (float) j, (float) i);
               count++;
            }
         }
      }

      // Create the switch nodes
      posSwitch = new Switch(Switch.CHILD_MASK);
      humanSwitch = new Switch(Switch.CHILD_MASK);
      machineSwitch = new Switch(Switch.CHILD_MASK);

      // Set the capability bits
      posSwitch.setCapability(Switch.ALLOW_SWITCH_READ);
      posSwitch.setCapability(Switch.ALLOW_SWITCH_WRITE);

      humanSwitch.setCapability(Switch.ALLOW_SWITCH_READ);
      humanSwitch.setCapability(Switch.ALLOW_SWITCH_WRITE);
      
      machineSwitch.setCapability(Switch.ALLOW_SWITCH_READ);
      machineSwitch.setCapability(Switch.ALLOW_SWITCH_WRITE);
      
      // Create the bit masks
      posMask = new BitSet();  
      humanMask = new BitSet();  
      machineMask = new BitSet();  

      // Create the small white spheres that mark unoccupied
      // positions.
      posSphere = new Sphere[64];
      for (int i=0; i<64; i++) {
         Transform3D transform3D = new Transform3D();
         transform3D.set(point[i]);
         TransformGroup transformGroup = new TransformGroup(transform3D);
         posSphere[i] = new Sphere(2.0f, Sphere.GENERATE_NORMALS |
				   Sphere.ENABLE_APPEARANCE_MODIFY,
				   12, whiteApp);
         Shape3D shape = posSphere[i].getShape(); 
         ID id = new ID(i);
         shape.setUserData(id); 
         transformGroup.addChild(posSphere[i]);
         posSwitch.addChild(transformGroup);
         posMask.set(i);
      }

      // Create the red spheres that mark the user's positions.
      for (int i=0; i<64; i++) {
         Transform3D transform3D = new Transform3D();
         transform3D.set(point[i]);
         TransformGroup transformGroup = new TransformGroup(transform3D);
         transformGroup.addChild(new Sphere(7.0f, redApp));
         humanSwitch.addChild(transformGroup);
         humanMask.clear(i);
      }

      // Create the blue cubes that mark the computer's positions.
      for (int i=0; i<64; i++) {
         Transform3D transform3D = new Transform3D();
         transform3D.set(point[i]);
         TransformGroup transformGroup = new TransformGroup(transform3D);
         BigCube cube = new BigCube(blueApp);
         transformGroup.addChild(cube.getChild());
         machineSwitch.addChild(transformGroup);
         machineMask.clear(i);
      }

      // Set the positions mask
      posSwitch.setChildMask(posMask);
      humanSwitch.setChildMask(humanMask);
      machineSwitch.setChildMask(machineMask);

      // Throw everything into a single group
      group = new Group();
      group.addChild(posSwitch);
      group.addChild(humanSwitch);
      group.addChild(machineSwitch);
   }

   public void setTransformGroup(TransformGroup transformGroup) {
      tgroup = transformGroup;
   }

   public Group getChild() {
      return group;
   }

   public void setBoard(Board board) {
      this.board = board;
   }

   public void winner() {
      winnerFlag = true;
   }

   public void noWinner() {
      winnerFlag = false;
   }

   public void setHighlight(int pos) {
      posSphere[pos].setAppearance(yellowApp);
   }

   public void clearHighlight(int pos) {
      posSphere[pos].setAppearance(whiteApp);
   }

   public void newGame() {

      // Clear the board
      for (int i=0; i<64; i++) {
         posMask.set(i);
         humanMask.clear(i);
         machineMask.clear(i);
      }
      posSwitch.setChildMask(posMask);
      humanSwitch.setChildMask(humanMask);
      machineSwitch.setChildMask(machineMask);

      // The following three lines fix a bug in J3D
      Transform3D t = new Transform3D();
      tgroup.getTransform(t);
      tgroup.setTransform(t);

      // Reset the winner flag
      winnerFlag = false;
   }

   public void set(int pos, int player) {

      // Stop accepting selections when the game
      // is over.
      if (winnerFlag) return;

      // Make sure the position is not occupied.
      if (player == HUMAN)
         if (!board.unoccupied(pos)) return;

      // Turn off the position marker for the given position
      posMask.clear(pos);
      posSwitch.setChildMask(posMask);

      // Turn on the player marker
      if (player == Positions.HUMAN) {
         humanMask.set(pos);
         humanSwitch.setChildMask(humanMask);
         board.selection(pos, Positions.HUMAN);
      }
      else {
         machineMask.set(pos);
         machineSwitch.setChildMask(machineMask);
      }

      // The following three lines fix a bug in J3D
      Transform3D t = new Transform3D();
      tgroup.getTransform(t);
      tgroup.setTransform(t);
   }

   public void clear(int pos) {

      // Turn on the position marker
      posMask.set(pos);
      posSwitch.setChildMask(posMask);

      // Turn off the player marker
      humanMask.clear(pos);
      humanSwitch.setChildMask(humanMask);
      machineMask.clear(pos);
      machineSwitch.setChildMask(machineMask);

      // The following three lines are a workaround for a bug 
      // in dev09 in which the transform3D of certain items are
      // not updated properly. Scheduled to be fixed in dev10
      Transform3D t = new Transform3D();
      tgroup.getTransform(t);
      tgroup.setTransform(t);
   }

}
