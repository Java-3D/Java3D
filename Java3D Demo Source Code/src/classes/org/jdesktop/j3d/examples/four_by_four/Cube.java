/*
 * $RCSfile: Cube.java,v $
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

public class Cube extends Object {

   private Shape3D shape3D;

   private static final float[] verts = {
   // Front Face
      1.0f, -1.0f,  1.0f,     1.0f,  1.0f,  1.0f,
     -1.0f,  1.0f,  1.0f,    -1.0f, -1.0f,  1.0f,
   // Back Face
     -1.0f, -1.0f, -1.0f,    -1.0f,  1.0f, -1.0f,
      1.0f,  1.0f, -1.0f,     1.0f, -1.0f, -1.0f,
   // Right Face
      1.0f, -1.0f, -1.0f,     1.0f,  1.0f, -1.0f,
      1.0f,  1.0f,  1.0f,     1.0f, -1.0f,  1.0f,
   // Left Face
     -1.0f, -1.0f,  1.0f,    -1.0f,  1.0f,  1.0f,
     -1.0f,  1.0f, -1.0f,    -1.0f, -1.0f, -1.0f,
   // Top Face
      1.0f,  1.0f,  1.0f,     1.0f,  1.0f, -1.0f,
     -1.0f,  1.0f, -1.0f,    -1.0f,  1.0f,  1.0f,
   // Bottom Face
     -1.0f, -1.0f,  1.0f,    -1.0f, -1.0f, -1.0f,
      1.0f, -1.0f, -1.0f,     1.0f, -1.0f,  1.0f,
   };

   private static final float[] normals = {
   // Front Face
      0.0f,  0.0f,  1.0f,     0.0f,  0.0f,  1.0f,
      0.0f,  0.0f,  1.0f,     0.0f,  0.0f,  1.0f,
   // Back Face
      0.0f,  0.0f, -1.0f,     0.0f,  0.0f, -1.0f,
      0.0f,  0.0f, -1.0f,     0.0f,  0.0f, -1.0f,
   // Right Face
      1.0f,  0.0f,  0.0f,     1.0f,  0.0f,  0.0f,
      1.0f,  0.0f,  0.0f,     1.0f,  0.0f,  0.0f,
   // Left Face
     -1.0f,  0.0f,  0.0f,    -1.0f,  0.0f,  0.0f,
     -1.0f,  0.0f,  0.0f,    -1.0f,  0.0f,  0.0f,
   // Top Face
      0.0f,  1.0f,  0.0f,     0.0f,  1.0f,  0.0f,
      0.0f,  1.0f,  0.0f,     0.0f,  1.0f,  0.0f,
   // Bottom Face
      0.0f, -1.0f,  0.0f,     0.0f, -1.0f,  0.0f,
      0.0f, -1.0f,  0.0f,     0.0f, -1.0f,  0.0f,
   };

   public Cube(Appearance appearance) {

      QuadArray quadArray = new QuadArray(24, QuadArray.COORDINATES |
                                              QuadArray.NORMALS |
                                              QuadArray.TEXTURE_COORDINATE_2);
      quadArray.setCoordinates(0, verts);
      quadArray.setNormals(0, normals);

      shape3D = new Shape3D(quadArray, appearance);
      shape3D.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
      shape3D.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
      shape3D.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
      shape3D.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
   }

   public Cube(Appearance appearance, float size) {

      QuadArray quadArray = new QuadArray(24, QuadArray.COORDINATES | 
                                              QuadArray.NORMALS);
      for (int i=0; i<72; i++) 
         verts[i] *= size;

      quadArray.setCoordinates(0, verts);
      quadArray.setNormals(0, normals);

      shape3D = new Shape3D(quadArray, appearance);
      shape3D.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
      shape3D.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
      shape3D.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
      shape3D.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
   }

   public Shape3D getChild() {
      return shape3D;
   }
}
