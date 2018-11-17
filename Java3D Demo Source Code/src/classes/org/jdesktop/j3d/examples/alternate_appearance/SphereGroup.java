/*
 * $RCSfile: SphereGroup.java,v $
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

package org.jdesktop.j3d.examples.alternate_appearance;

import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.geometry.*;

public class SphereGroup
	extends Group
{
    Shape3D[] shapes;
    int numShapes = 0;
	//  Constructors
	public SphereGroup( )
	{
		//    radius   x,y spacing   x,y count  appearance
		this( 0.25f,   0.75f, 0.75f,   5, 5,      null, false );
	}

	public SphereGroup( Appearance app )
	{
		//    radius   x,y spacing   x,y count  appearance
		this( 0.25f,   0.75f, 0.75f,   5, 5,      app, false );
	}

	public SphereGroup( float radius, float xSpacing, float ySpacing,
		int xCount, int yCount, boolean overrideflag )
	{
		this( radius,  xSpacing, ySpacing, xCount, yCount, null, overrideflag );
	}

    public SphereGroup( float radius, float xSpacing, float ySpacing,
			int xCount, int yCount, Appearance app, boolean overrideflag )
    {
	if ( app == null )
	    {
		app = new Appearance( );
		Material material = new Material( );
		material.setDiffuseColor( new Color3f( 0.8f, 0.8f, 0.8f ) );
		material.setSpecularColor( new Color3f( 0.0f, 0.0f, 0.0f ) );
		material.setShininess( 0.0f );
		app.setMaterial( material );
	    }

	double xStart = -xSpacing * (double)(xCount-1) / 2.0;
	double yStart = -ySpacing * (double)(yCount-1) / 2.0;

	Sphere sphere = null;
	TransformGroup trans = null;
	Transform3D t3d = new Transform3D( );
	Vector3d vec = new Vector3d( );
	double x, y = yStart, z = 0.0;
	shapes = new Shape3D[xCount * yCount];
	for ( int i = 0; i < yCount; i++ )
	    {
		x = xStart;
		for ( int j = 0; j < xCount; j++ ) {
		    vec.set( x, y, z );
		    t3d.setTranslation( vec );
		    trans = new TransformGroup( t3d );
		    addChild( trans );

		    sphere = new Sphere(
					radius,     // sphere radius
					Primitive.GENERATE_NORMALS,  // generate normals
					16,         // 16 divisions radially
					app );      // it's appearance
		    trans.addChild( sphere );
		    x += xSpacing;
		    shapes[numShapes] = sphere.getShape();
		    if (overrideflag) 
			shapes[numShapes].setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);
		    numShapes++;
		}
		y += ySpacing;
	    }
    }
    Shape3D[] getShapes() {
	return shapes;
    }
    
}
