/*
 * $RCSfile: ConfigObjLoad.java,v $
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
 * $Revision: 1.5 $
 * $Date: 2007/02/09 17:21:34 $
 * $State: Exp $
 */

package org.jdesktop.j3d.examples.configured_universe;

import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.Scene;
import java.awt.event.*;
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.io.*;
import com.sun.j3d.utils.behaviors.vp.*;
import com.sun.j3d.utils.behaviors.sensor.Mouse6DPointerBehavior;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.Map;
import org.jdesktop.j3d.examples.Resources;

public class ConfigObjLoad {

    private boolean spin = false;
    private boolean noTriangulate = false;
    private boolean noStripify = false;
    private double creaseAngle = 60.0;
    private URL filename = null;

    private ConfiguredUniverse u;

    public BranchGroup createSceneGraph() {
	// Create the root of the branch graph
	BranchGroup objRoot = new BranchGroup();

        // Create a Transformgroup to scale all objects so they
        // appear in the scene.
        TransformGroup objScale = new TransformGroup();
        Transform3D t3d = new Transform3D();
        t3d.setScale(0.7);
        objScale.setTransform(t3d);
        objRoot.addChild(objScale);

	// Create the transform group node and initialize it to the
	// identity.  Enable the TRANSFORM_WRITE capability so that
	// our behavior code can modify it at runtime.  Add it to the
	// root of the subgraph.
	TransformGroup objTrans = new TransformGroup();
	objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
	objScale.addChild(objTrans);

	int flags = ObjectFile.RESIZE;
	if (!noTriangulate) flags |= ObjectFile.TRIANGULATE;
	if (!noStripify) flags |= ObjectFile.STRIPIFY;
	ObjectFile f = new ObjectFile(flags, 
	  (float)(creaseAngle * Math.PI / 180.0));
	Scene s = null;
	try {
	  s = f.load(filename);
	}
	catch (FileNotFoundException e) {
	  System.err.println(e);
	  System.exit(1);
	}
	catch (ParsingErrorException e) {
	  System.err.println(e);
	  System.exit(1);
	}
	catch (IncorrectFormatException e) {
	  System.err.println(e);
	  System.exit(1);
	}
	  
	objTrans.addChild(s.getSceneGroup());

	BoundingSphere bounds =
	  new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);

        if (spin) {
	  Transform3D yAxis = new Transform3D();
	  Alpha rotationAlpha = new Alpha(-1, Alpha.INCREASING_ENABLE,
					  0, 0,
					  4000, 0, 0,
					  0, 0, 0);

	  RotationInterpolator rotator =
	      new RotationInterpolator(rotationAlpha, objTrans, yAxis,
				       0.0f, (float) Math.PI*2.0f);
	  rotator.setSchedulingBounds(bounds);
	  objTrans.addChild(rotator);
	} 

        // Set up the background
        Color3f bgColor = new Color3f(0.05f, 0.05f, 0.5f);
        Background bgNode = new Background(bgColor);
        bgNode.setApplicationBounds(bounds);
        objRoot.addChild(bgNode);

        // Set up the ambient light
        Color3f ambientColor = new Color3f(0.1f, 0.1f, 0.1f);
        AmbientLight ambientLightNode = new AmbientLight(ambientColor);
        ambientLightNode.setInfluencingBounds(bounds);
        objRoot.addChild(ambientLightNode);

        // Set up the directional lights
        Color3f light1Color = new Color3f(1.0f, 1.0f, 0.9f);
        Vector3f light1Direction  = new Vector3f(1.0f, 1.0f, 1.0f);
        Color3f light2Color = new Color3f(1.0f, 1.0f, 1.0f);
        Vector3f light2Direction  = new Vector3f(-1.0f, -1.0f, -1.0f);

        DirectionalLight light1
            = new DirectionalLight(light1Color, light1Direction);
        light1.setInfluencingBounds(bounds);
        objRoot.addChild(light1);

        DirectionalLight light2
            = new DirectionalLight(light2Color, light2Direction);
        light2.setInfluencingBounds(bounds);
        objRoot.addChild(light2);

	return objRoot;
    }

    private void usage()
    {
      System.out.println(
	"Usage: java ConfigObjLoad [-s] [-n] [-t] [-c degrees] <.obj file>");
      System.out.println("  -s Spin (no user interaction)");
      System.out.println("  -n No triangulation");
      System.out.println("  -t No stripification");
      System.out.println(
	"  -c Set crease angle for normal generation (default is 60 without");
      System.out.println(
	"     smoothing group info, otherwise 180 within smoothing groups)");
      System.exit(0);
    } // End of usage


    public void init() {
	if (filename == null) {
            filename = Resources.getResource("resources/geometry/galleon.obj");
            if (filename == null) {
                System.err.println("resources/geometry/galleon.obj not found");
                System.exit(1);
            }
        }

	// Get the config file URL from the j3d.configURL property or use the
	// default config file "j3d1x1-window" in the current directory.
        URL configURL = ConfiguredUniverse.getConfigURL(null);
        if(configURL == null) {
            configURL = Resources.getResource("configured_universe/j3d1x1-window.cfg");
        }
        
        // Create a simple scene and attach it to the virtual universe
	BranchGroup scene = createSceneGraph();

	u = new ConfiguredUniverse(configURL);
	
	// Get the ViewingPlatform.
	ViewingPlatform viewingPlatform = u.getViewingPlatform();

	// This will move the ViewPlatform back a bit so the objects in the
	// scene can be viewed.  This will only have an effect if the config
	// file sets the window eyepoint policy to something other than
	// RELATIVE_TO_COEXISTENCE, which is the default eyepoint policy
	// applied by ConfiguredUniverse.
	// 
	// The default view attach policy for ConfiguredUniverse applications
	// is NOMINAL_SCREEN.  This sets the view platform origin in the
	// physical world to the center of coexistence, which allows eye
	// positions expressed relative to coexistence to see the appropriate
	// field of view automatically.
        viewingPlatform.setNominalViewingTransform();
	
	// Add a ViewPlatformBehavior if not specified in the config file.
	if (!spin && viewingPlatform.getViewPlatformBehavior() == null) {
            OrbitBehavior orbit = new OrbitBehavior(u.getCanvas(),
						    OrbitBehavior.REVERSE_ALL);
            BoundingSphere bounds =
                new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);

            orbit.setSchedulingBounds(bounds);
            viewingPlatform.setViewPlatformBehavior(orbit);	    
	}
	
	// See if there's a 6 degree of freedom mouse in the environment.
	// We look for one named "mouse6d".
	Map sensorMap = null;
	sensorMap = u.getNamedSensors();
	if (sensorMap != null) {
	    Sensor mouse6d = (Sensor)sensorMap.get("mouse6d");
	    if (mouse6d != null) {
		Mouse6DPointerBehavior behavior =
		    new Mouse6DPointerBehavior(mouse6d, 1.0, true);

		BoundingSphere bounds =
		    new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);
		behavior.setSchedulingBounds(bounds);

		scene.addChild(behavior);
		scene.addChild(behavior.getEcho());
	    }
	}

	// Listen for a typed "q", "Q", or "Escape" key on each canvas to
	// allow a convenient exit from full screen configurations.
	Canvas3D[] canvases;
	canvases = u.getViewer().getCanvas3Ds();

	class QuitListener extends KeyAdapter {
	    public void keyTyped(KeyEvent e) {
		char c = e.getKeyChar();
		if (c == 'q' || c == 'Q' || c == 27)
		    System.exit(0);
	    }
	}

	QuitListener quitListener = new QuitListener();
	for (int i = 0; i < canvases.length; i++)
	    canvases[i].addKeyListener(quitListener);

	// Make the scenegraph live.
	u.addBranchGraph(scene);
    }

    public ConfigObjLoad(String[] args) {
      if (args.length != 0) {
	for (int i = 0; i < args.length; i++) {
	  if (args[i].startsWith("-")) {
	    if (args[i].equals("-s")) {
	      spin = true;
	    } else if (args[i].equals("-n")) {
	      noTriangulate = true;
	    } else if (args[i].equals("-t")) {
	      noStripify = true;
	    } else if (args[i].equals("-c")) {
	      if (i < args.length - 1) {
		creaseAngle = (new Double(args[++i])).doubleValue();
	      } else usage();
	    } else {
	      usage();
	    }
	  } else {
	    try {
	      if ((args[i].indexOf("file:") == 0) ||
		  (args[i].indexOf("http") == 0)) {
		  filename = new URL(args[i]);
	      }
	      else if (args[i].charAt(0) != '/') {
		  filename = new URL("file:./" + args[i]);
	      }
	      else {
		  filename = new URL("file:" + args[i]);
	      }
            }
	    catch (MalformedURLException e) {
	      System.err.println(e);
	      System.exit(1);
	    }
	  }
	}
      }
      init();
    }

    public static void main(String[] args) {
      new ConfigObjLoad(args);
    }
}
