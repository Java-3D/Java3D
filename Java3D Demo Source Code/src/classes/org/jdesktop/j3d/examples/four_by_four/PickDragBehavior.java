/*
 * $RCSfile: PickDragBehavior.java,v $
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
import java.awt.event.*;
import java.util.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.geometry.Sphere;

/**
 * Class:       PickDragBehavior
 * 
 * Description: Used to respond to mouse pick and drag events
 *              in the 3D window.
 *
 * Version:     1.0
 *
 */
public class PickDragBehavior extends Behavior {

   WakeupCriterion[] mouseEvents;
   WakeupOr mouseCriterion;
   int x, y;
   int x_last, y_last;
   double x_angle, y_angle;
   double x_factor, y_factor;
   Transform3D modelTrans;
   Transform3D transformX;
   Transform3D transformY;
   TransformGroup transformGroup;
   BranchGroup branchGroup;
   Canvas2D canvas2D;
   Canvas3D canvas3D;
   Positions positions;
   PickRay pickRay = new PickRay();
   SceneGraphPath sceneGraphPath[];
   Appearance highlight;
   boolean parallel;

   PickDragBehavior(Canvas2D canvas2D, Canvas3D canvas3D, Positions positions, 
                    BranchGroup branchGroup, TransformGroup transformGroup) {

      this.canvas2D = canvas2D;
      this.canvas3D = canvas3D;
      this.positions = positions;
      this.branchGroup = branchGroup;
      this.transformGroup = transformGroup;

      modelTrans = new Transform3D();
      transformX = new Transform3D();
      transformY = new Transform3D();

      Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
      Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
      Color3f green = new Color3f(0.0f, 1.0f, 0.0f);

      highlight = new Appearance();
      highlight.setMaterial(new Material(green, black, green, white, 80.f));

      parallel = true;
   }

   public void initialize() {
      x = 0;
      y = 0;
      x_last = 0;
      y_last = 0;
      x_angle = 0;
      y_angle = 0;
      x_factor = .02;
      y_factor = .02;

      mouseEvents = new WakeupCriterion[2];
      mouseEvents[0] = new WakeupOnAWTEvent(MouseEvent.MOUSE_DRAGGED);
      mouseEvents[1] = new WakeupOnAWTEvent(MouseEvent.MOUSE_PRESSED);
      mouseCriterion = new WakeupOr(mouseEvents);
      wakeupOn (mouseCriterion);
   }

   public void processStimulus (Enumeration criteria) {
      WakeupCriterion wakeup;
      AWTEvent[] event;
      int id;
      int dx, dy;

      while (criteria.hasMoreElements()) {
         wakeup = (WakeupCriterion) criteria.nextElement();
         if (wakeup instanceof WakeupOnAWTEvent) {
            event = ((WakeupOnAWTEvent)wakeup).getAWTEvent();
            for (int i=0; i<event.length; i++) { 
               id = event[i].getID();
               if (id == MouseEvent.MOUSE_DRAGGED) {

                  x = ((MouseEvent)event[i]).getX();
                  y = ((MouseEvent)event[i]).getY();

                  dx = x - x_last;
                  dy = y - y_last;

                  x_angle = dy * y_factor;
                  y_angle = dx * x_factor;

                  transformX.rotX(x_angle);
                  transformY.rotY(y_angle);

                  modelTrans.mul(transformX, modelTrans);
                  modelTrans.mul(transformY, modelTrans);
                 
                  transformGroup.setTransform(modelTrans);

                  x_last = x;
                  y_last = y;
               }
               else if (id == MouseEvent.MOUSE_PRESSED) {

                  x = x_last = ((MouseEvent)event[i]).getX();
                  y = y_last = ((MouseEvent)event[i]).getY();

                  Point3d eyePos = new Point3d();
                  canvas3D.getCenterEyeInImagePlate(eyePos);

                  Point3d mousePos = new Point3d();
                  canvas3D.getPixelLocationInImagePlate(x, y, mousePos);

                  Transform3D transform3D = new Transform3D();
                  canvas3D.getImagePlateToVworld(transform3D);

                  transform3D.transform(eyePos);
                  transform3D.transform(mousePos);

                  Vector3d mouseVec;
                  if (parallel) {
                     mouseVec = new Vector3d(0.f, 0.f, -1.f);
                  }
                  else {
                     mouseVec = new Vector3d();
                     mouseVec.sub(mousePos, eyePos);
                     mouseVec.normalize();
                  }
                
                  pickRay.set(mousePos, mouseVec);
                  sceneGraphPath = branchGroup.pickAllSorted(pickRay);
 
                  if (sceneGraphPath != null) {
                     for (int j=0; j<sceneGraphPath.length; j++) {
                        if (sceneGraphPath[j] != null) {
                           Node node = sceneGraphPath[j].getObject();
                           if (node instanceof Shape3D) {
                              try {
                                 ID posID = (ID) node.getUserData();
                                 if (posID != null) {
                                    int pos = posID.get();
                                    positions.set(pos, Positions.HUMAN);
                                    canvas2D.repaint();
                                    break;
                                 }
                              }
                              catch (CapabilityNotSetException e) {
                                 // Catch all CapabilityNotSet exceptions and
                                 // throw them away, prevents renderer from
                                 // locking up when encountering "non-selectable"
                                 // objects.
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
      wakeupOn (mouseCriterion);
   }
}
