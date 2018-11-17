/*
 * $RCSfile: cgview.java,v $
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
 * $Revision: 1.3 $
 * $Date: 2007/02/09 17:21:40 $
 * $State: Exp $
 */

package org.jdesktop.j3d.examples.geometry_compression;

import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.geometry.compression.*;
import com.sun.j3d.utils.universe.*;
import java.applet.Applet;
import java.awt.BorderLayout;
import java.io.IOException;
import javax.media.j3d.*;
import javax.vecmath.*;

public class cgview extends Applet {

    private SimpleUniverse u = null;

    public BranchGroup createSceneGraph(CompressedGeometryData cg) {
	// Create the root of the branch graph
	BranchGroup objRoot = new BranchGroup() ;

        // Create a Transformgroup to scale all objects so they
        // appear in the scene.
        TransformGroup objScale = new TransformGroup() ;
        Transform3D t3d = new Transform3D() ;
        t3d.setScale(0.7) ;
        objScale.setTransform(t3d) ;
        objRoot.addChild(objScale) ;

	// Create the transform group node and initialize it to the
	// identity.  Enable the TRANSFORM_WRITE capability so that
	// our behavior code can modify it at runtime.  Add it to the
	// root of the subgraph.
	TransformGroup objTrans = new TransformGroup() ;
	objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE) ;
	objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ) ;
	objScale.addChild(objTrans) ;

	// Add compressed geometry to the scene graph.
	CompressedGeometryData.Header hdr = new CompressedGeometryData.Header() ;
	cg.getCompressedGeometryHeader(hdr) ;

        Shape3D[] shapes = cg.decompress();
        if (shapes != null) {
            for (int i = 0; i < shapes.length; i++) {
                objTrans.addChild(shapes[i]);
            }
        }

	// Create mouse behavior scheduling bounds.
	BoundingSphere bounds =
	    new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0) ;

        // Set up the background
        Color3f bgColor = new Color3f(0.05f, 0.05f, 0.5f);
        Background bgNode = new Background(bgColor) ;
        bgNode.setApplicationBounds(bounds) ;
        objRoot.addChild(bgNode) ;

        // Set up the ambient light
        Color3f ambientColor = new Color3f(0.1f, 0.1f, 0.1f) ;
        AmbientLight ambientLightNode = new AmbientLight(ambientColor) ;
        ambientLightNode.setInfluencingBounds(bounds) ;
        objRoot.addChild(ambientLightNode) ;

        // Set up the directional lights
        Color3f light1Color = new Color3f(1.0f, 1.0f, 0.9f) ;
        Vector3f light1Direction  = new Vector3f(1.0f, 1.0f, 1.0f) ;
        Color3f light2Color = new Color3f(1.0f, 1.0f, 0.9f) ;
        Vector3f light2Direction  = new Vector3f(-1.0f, -1.0f, -0.9f) ;

        DirectionalLight light1
            = new DirectionalLight(light1Color, light1Direction) ;
        light1.setInfluencingBounds(bounds) ;
        objRoot.addChild(light1) ;

        DirectionalLight light2
            = new DirectionalLight(light2Color, light2Direction) ;
        light2.setInfluencingBounds(bounds) ;
        objRoot.addChild(light2) ;

	return objRoot ;
    }

    private void usage() {
	System.out.println("Usage: cgview <.cg file> <object index>")  ;
	System.exit(0)  ;
    }

    public cgview(String args[]) {
        if (args.length < 1)
	    usage() ;

	int index ;
	if (args.length < 2)
	    index = 0 ;
	else
	    index = Integer.parseInt(args[1]) ;

	String filename = args[0] ;
	if (filename == null)
	    usage() ;

	// Read the compressed geometry.
	CompressedGeometryData cg = null ;
	try {
	    CompressedGeometryFile cgf ;
	    cgf = new CompressedGeometryFile(filename, false) ;

	    if (cgf.getObjectCount() == 0) {
		System.out.println("no objects were found in " + filename) ;
		System.exit(0) ;
	    }

	    cg = cgf.read(index) ;
	    cgf.close() ;

	} catch (IOException e) {
	    System.out.println(e) ;
	    System.exit(0) ;
	}

	setLayout(new BorderLayout()) ;
	Canvas3D c = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
	add("Center", c) ;

	// Create a simple scene and attach it to the virtual universe
	BranchGroup scene = createSceneGraph(cg) ;
	u = new SimpleUniverse(c) ;

	// add mouse behaviors to the ViewingPlatform
	ViewingPlatform viewingPlatform = u.getViewingPlatform();

        // This will move the ViewPlatform back a bit so the
        // objects in the scene can be viewed.
	viewingPlatform.setNominalViewingTransform();

	OrbitBehavior orbit = new OrbitBehavior(c, OrbitBehavior.REVERSE_ALL);
	BoundingSphere bounds = 
	  new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
	orbit.setSchedulingBounds(bounds);
	viewingPlatform.setViewPlatformBehavior(orbit);

	u.addBranchGraph(scene) ;
    }

    public void destroy() {
	u.cleanup();
    }

    //
    // The following allows cgview to be run as an application
    // as well as an applet.
    //
    public static void main(String[] args) {
	new MainFrame(new cgview(args), 700, 700) ;
    }
}
