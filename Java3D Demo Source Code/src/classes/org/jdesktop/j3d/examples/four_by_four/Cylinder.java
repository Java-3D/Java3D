/*
 * $RCSfile: Cylinder.java,v $
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

import javax.media.j3d.*;
import javax.vecmath.*;

public class Cylinder {
   
   float verts[];
   float normals[];
   QuadArray quad = null;
   float div = 3.0f;
   Shape3D shape;

   public Cylinder(float x, float z, float radius, float length, int quality, Appearance a) {

      if (quality < 3) quality = 3;

      div = (float) quality;
      
      verts = new float[quality*12];
      normals = new float[quality*12];

      double inc = 2.0*Math.PI/(double)div;
      for (int i=0; i< quality; i++){
	 float z1 = radius * (float)Math.sin((double)i*inc) + z;
	 float x1 = radius * (float)Math.cos((double)i*inc) + x;
	 float z2 = radius * (float)Math.sin((double)(i+1)*inc) + z;
	 float x2 = radius * (float)Math.cos((double)(i+1)*inc) + x;

	 verts[12*i]    = x1;
	 verts[12*i+1]  = -length/2.f;
	 verts[12*i+2]  = z1;
	 verts[12*i+3]  = x1;
	 verts[12*i+4]  = length/2.f;
	 verts[12*i+5]  = z1;
	 verts[12*i+6]  = x2;
	 verts[12*i+7]  = length/2.f;
	 verts[12*i+8]  = z2;
	 verts[12*i+9]  = x2;
	 verts[12*i+10] = -length/2.f;
	 verts[12*i+11] = z2;
	 
	 float nz1 = (float)Math.sin((double)i*inc);
	 float nx1 = (float)Math.cos((double)i*inc);
	 float nz2 = (float)Math.sin((double)(i+1)*inc);
	 float nx2 = (float)Math.cos((double)(i+1)*inc);

	 normals[12*i] = nx1;
	 normals[12*i+1] = 0.0f;
	 normals[12*i+2] = nz1;
	 normals[12*i+3] = nx1;
	 normals[12*i+4] = 0.0f;
	 normals[12*i+5] = nz1;
	 normals[12*i+6] = nx2;
	 normals[12*i+7] = 0.0f;
	 normals[12*i+8] = nz2;
	 normals[12*i+9] = nx2;
	 normals[12*i+10] = 0.0f;
	 normals[12*i+11] = nz2;
      }
      
      quad = new QuadArray(quality*4, QuadArray.COORDINATES |
                                      QuadArray.NORMALS );
      quad.setCoordinates(0, verts);
      quad.setNormals(0, normals);
      shape = new Shape3D(quad, a);
   }

   Shape3D getShape(){
      return shape;
   }
}
