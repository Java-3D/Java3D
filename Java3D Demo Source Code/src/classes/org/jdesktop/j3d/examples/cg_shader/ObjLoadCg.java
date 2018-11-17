/*
 * $RCSfile: ObjLoadCg.java,v $
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
 * $Revision: 1.6 $
 * $Date: 2007/02/09 17:21:32 $
 * $State: Exp $
 */

package org.jdesktop.j3d.examples.cg_shader;

import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.utils.shader.StringIO;
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.awt.GraphicsConfiguration;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.io.*;
import java.net.URL;
import com.sun.j3d.utils.behaviors.vp.*;
import java.io.FileNotFoundException;
import javax.swing.JOptionPane;
import org.jdesktop.j3d.examples.Resources;

/**
 * Simple Java 3D example program to display an .obj object with shader programs.
 */
public class ObjLoadCg extends javax.swing.JFrame {

    private boolean spin = false;
    private boolean noTriangulate = false;
    private boolean noStripify = false;
    private double creaseAngle = 60.0;
    private URL filename = null;

    private SimpleUniverse univ = null;
    private BranchGroup scene = null;

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
	    e.printStackTrace();
	    System.exit(1);
	}
	catch (ParsingErrorException e) {
	    e.printStackTrace();
	    System.exit(1);
	}
	catch (IncorrectFormatException e) {
	    e.printStackTrace();
	    System.exit(1);
	}
	  
	// Set vertex and fragment shader program for all Shape3D nodes in scene
	String vertexProgram = null;
	String fragmentProgram = null;
	try {
	    vertexProgram = StringIO.readFully(Resources.getResource("cg_shader/simple_vp.cg"));
	    fragmentProgram = StringIO.readFully(Resources.getResource("cg_shader/simple_fp.cg"));
	}
	catch (IOException e) {
	    e.printStackTrace();
	    System.exit(1);
	}
	Shader[] shaders = new Shader[2];
	shaders[0] = new SourceCodeShader(Shader.SHADING_LANGUAGE_CG,
					  Shader.SHADER_TYPE_VERTEX,
					  vertexProgram);
	shaders[1] = new SourceCodeShader(Shader.SHADING_LANGUAGE_CG,
					  Shader.SHADER_TYPE_FRAGMENT,
					  fragmentProgram);
	ShaderProgram shaderProgram = new CgShaderProgram();
	shaderProgram.setShaders(shaders);
	setShaderProgram(s.getSceneGroup(), shaderProgram);

	objTrans.addChild(s.getSceneGroup());

	BoundingSphere bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);

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

	return objRoot;
    }
    
    private Canvas3D createUniverse() {
	// Get the preferred graphics configuration for the default screen
	GraphicsConfiguration config =
	    SimpleUniverse.getPreferredConfiguration();

	// Create a Canvas3D using the preferred configuration
	Canvas3D canvas3d = new Canvas3D(config);

	// Create simple universe with view branch
	univ = new SimpleUniverse(canvas3d);
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);

        // Add a ShaderErrorListener
        univ.addShaderErrorListener(new ShaderErrorListener() {
            public void errorOccurred(ShaderError error) {
                error.printVerbose();
                JOptionPane.showMessageDialog(ObjLoadCg.this,
                              error.toString(),
                              "ShaderError",
                              JOptionPane.ERROR_MESSAGE);
            }
        });

	// add mouse behaviors to the ViewingPlatform
	ViewingPlatform viewingPlatform = univ.getViewingPlatform();

	PlatformGeometry pg = new PlatformGeometry();

	// Set up the ambient light
	Color3f ambientColor = new Color3f(0.1f, 0.1f, 0.1f);
	AmbientLight ambientLightNode = new AmbientLight(ambientColor);
	ambientLightNode.setInfluencingBounds(bounds);
	pg.addChild(ambientLightNode);

	// Set up the directional lights
	Color3f light1Color = new Color3f(1.0f, 1.0f, 0.9f);
	Vector3f light1Direction  = new Vector3f(1.0f, 1.0f, 1.0f);
	Color3f light2Color = new Color3f(1.0f, 1.0f, 1.0f);
	Vector3f light2Direction  = new Vector3f(-1.0f, -1.0f, -1.0f);

	DirectionalLight light1
	    = new DirectionalLight(light1Color, light1Direction);
	light1.setInfluencingBounds(bounds);
	pg.addChild(light1);

	DirectionalLight light2
	    = new DirectionalLight(light2Color, light2Direction);
	light2.setInfluencingBounds(bounds);
	pg.addChild(light2);

	viewingPlatform.setPlatformGeometry( pg );
      
	// This will move the ViewPlatform back a bit so the
	// objects in the scene can be viewed.
	viewingPlatform.setNominalViewingTransform();

	if (!spin) {
            OrbitBehavior orbit = new OrbitBehavior(canvas3d,
						    OrbitBehavior.REVERSE_ALL);
            orbit.setSchedulingBounds(bounds);
            viewingPlatform.setViewPlatformBehavior(orbit);	    
	}        
        
        // Ensure at least 5 msec per frame (i.e., < 200Hz)
	univ.getViewer().getView().setMinimumFrameCycleTime(5);

	return canvas3d;
    }

    private void usage() {
      System.out.println(
	"Usage: java ObjLoadCg [-s] [-n] [-t] [-c degrees] <.obj file>");
      System.out.println("  -s Spin (no user interaction)");
      System.out.println("  -n No triangulation");
      System.out.println("  -t No stripification");
      System.out.println(
	"  -c Set crease angle for normal generation (default is 60 without");
      System.out.println(
	"     smoothing group info, otherwise 180 within smoothing groups)");
      System.exit(0);
    } // End of usage

    // Set shader program for all nodes in specified branch graph
    private void setShaderProgram(BranchGroup g, ShaderProgram shaderProgram) {
	ShaderAppearance myApp = new ShaderAppearance();
	Material mat = new Material();
	Texture2D tex2d = new Texture2D();
	myApp.setCapability(Appearance.ALLOW_TEXTURE_WRITE);
	myApp.setShaderProgram(shaderProgram);
	myApp.setMaterial(mat);
	myApp.setTexture(tex2d);
	setShaderProgram(g, myApp);
    }

    // Recursively set shader program for all children of specified group
    private void setShaderProgram(Group g,
				  ShaderAppearance myApp) {

	Enumeration e = g.getAllChildren();
	while (e.hasMoreElements()) {
	    Node n = (Node)(e.nextElement());
	    if (n instanceof Group) {
		setShaderProgram((Group)n, myApp);
	    }
	    else if (n instanceof Shape3D) {
		Shape3D s = (Shape3D)n;
		s.setAppearance(myApp);
	    }
	}
    }

    /**
     * Creates new form ObjLoadCg
     */
    public ObjLoadCg(String args[]) {
        if (args.length != 0) {
            for (int i = 0 ; i < args.length ; i++) {
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
                        } else if (args[i].charAt(0) != '/') {
                            filename = new URL("file:./" + args[i]);
                        } else {
                            filename = new URL("file:" + args[i]);
                        }
                    } catch (MalformedURLException e) {
                        System.err.println(e);
                        System.exit(1);
                    }
                }
            }
        }
        
        if (filename == null) {
            filename = Resources.getResource("resources/geometry/galleon.obj");
            if (filename == null) {
                System.err.println("resources/geometry/galleon.obj not found");
                System.exit(1);
            }
        }
        
        // Initialize the GUI components
        initComponents();
        
        // Create Canvas3D and SimpleUniverse; add canvas to drawing panel
        Canvas3D c = createUniverse();
        drawingPanel.add(c, java.awt.BorderLayout.CENTER);
        
        // Create the content branch and add it to the universe
        scene = createSceneGraph();
        univ.addBranchGraph(scene);
    }

    // ----------------------------------------------------------------
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        drawingPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("ObjLoadCg");
        drawingPanel.setLayout(new java.awt.BorderLayout());

        drawingPanel.setPreferredSize(new java.awt.Dimension(700, 700));
        getContentPane().add(drawingPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    /**
     * @param args the command line arguments
     */
    public static void main(final String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ObjLoadCg objLoadCg = new ObjLoadCg(args);
                objLoadCg.setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel drawingPanel;
    // End of variables declaration//GEN-END:variables
    
}
