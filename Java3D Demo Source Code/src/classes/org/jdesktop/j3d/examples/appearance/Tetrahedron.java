/*
 * $RCSfile: Tetrahedron.java,v $
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
 * $Date: 2007/02/09 17:21:31 $
 * $State: Exp $
 */

package org.jdesktop.j3d.examples.appearance;

import javax.media.j3d.*;
import javax.vecmath.*;

public class Tetrahedron extends Shape3D {
    private static final float sqrt3 = (float) Math.sqrt(3.0);
    private static final float sqrt3_3 = sqrt3 / 3.0f;
    private static final float sqrt24_3 = (float) Math.sqrt(24.0) / 3.0f;

    private static final float ycenter = 0.5f * sqrt24_3;
    private static final float zcenter = -sqrt3_3;

    private static final Point3f p1 = new Point3f(-1.0f, -ycenter, -zcenter);
    private static final Point3f p2 = new Point3f(1.0f, -ycenter, -zcenter);
    private static final Point3f p3 =
	new Point3f(0.0f, -ycenter, -sqrt3 - zcenter);
    private static final Point3f p4 =
	new Point3f(0.0f, sqrt24_3 - ycenter, 0.0f);

    private static final Point3f[] verts = {
	p1, p2, p4,	// front face
	p1, p4, p3,	// left, back face
	p2, p3, p4,	// right, back face
	p1, p3, p2,	// bottom face
    };

    private TexCoord2f texCoord[] = {
        new TexCoord2f(0.0f, 0.0f),
	new TexCoord2f(1.0f, 0.0f),
        new TexCoord2f(0.5f, sqrt3 / 2.0f),
    };

    public Tetrahedron() {
	int i;

	TriangleArray tetra = new TriangleArray(12, TriangleArray.COORDINATES |
		TriangleArray.NORMALS | TriangleArray.TEXTURE_COORDINATE_2);

	tetra.setCoordinates(0, verts);
        for (i = 0; i < 12; i++) {
            tetra.setTextureCoordinate(0, i, texCoord[i%3]);
        }

	int face;
	Vector3f normal = new Vector3f();
	Vector3f v1 = new Vector3f();
	Vector3f v2 = new Vector3f();
	Point3f [] pts = new Point3f[3];
	for (i = 0; i < 3; i++) pts[i] = new Point3f();

	for (face = 0; face < 4; face++) {
	    tetra.getCoordinates(face*3, pts);
	    v1.sub(pts[1], pts[0]);
	    v2.sub(pts[2], pts[0]);
	    normal.cross(v1, v2);
	    normal.normalize();
	    for (i = 0; i < 3; i++) {
		tetra.setNormal((face * 3 + i), normal);
	    }
	}
	this.setGeometry(tetra);
	this.setAppearance(new Appearance());
    }
}
