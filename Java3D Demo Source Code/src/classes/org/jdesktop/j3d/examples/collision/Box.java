/*
 * $RCSfile: Box.java,v $
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
 * $Date: 2007/02/09 17:21:33 $
 * $State: Exp $
 */

package org.jdesktop.j3d.examples.collision;

import javax.media.j3d.*;
import javax.vecmath.*;

public class Box extends Shape3D {

    public Box(double xsize, double ysize, double zsize) {
	super();
	double xmin = -xsize/2.0;
	double xmax =  xsize/2.0;
	double ymin = -ysize/2.0;
	double ymax =  ysize/2.0;
	double zmin = -zsize/2.0;
	double zmax =  zsize/2.0;

	QuadArray box = new QuadArray(24, QuadArray.COORDINATES);

	Point3d verts[] = new Point3d[24];

	// front face
	verts[0] = new Point3d(xmax, ymin, zmax);
	verts[1] = new Point3d(xmax, ymax, zmax);
	verts[2] = new Point3d(xmin, ymax, zmax);
	verts[3] = new Point3d(xmin, ymin, zmax);
	// back face
	verts[4] = new Point3d(xmin, ymin, zmin);
	verts[5] = new Point3d(xmin, ymax, zmin);
	verts[6] = new Point3d(xmax, ymax, zmin);
	verts[7] = new Point3d(xmax, ymin, zmin);
	// right face
	verts[8] = new Point3d(xmax, ymin, zmin);
	verts[9] = new Point3d(xmax, ymax, zmin);
	verts[10] = new Point3d(xmax, ymax, zmax);
	verts[11] = new Point3d(xmax, ymin, zmax);
	// left face
	verts[12] = new Point3d(xmin, ymin, zmax);
	verts[13] = new Point3d(xmin, ymax, zmax);
	verts[14] = new Point3d(xmin, ymax, zmin);
	verts[15] = new Point3d(xmin, ymin, zmin);
	// top face
	verts[16] = new Point3d(xmax, ymax, zmax);
	verts[17] = new Point3d(xmax, ymax, zmin);
	verts[18] = new Point3d(xmin, ymax, zmin);
	verts[19] = new Point3d(xmin, ymax, zmax);
	// bottom face
	verts[20] = new Point3d(xmin, ymin, zmax);
	verts[21] = new Point3d(xmin, ymin, zmin);
	verts[22] = new Point3d(xmax, ymin, zmin);
	verts[23] = new Point3d(xmax, ymin, zmax);

	box.setCoordinates(0, verts);
        setGeometry(box);
	setAppearance(new Appearance());
    }
}
