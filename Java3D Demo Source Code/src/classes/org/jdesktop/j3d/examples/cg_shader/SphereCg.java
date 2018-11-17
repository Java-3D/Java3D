/*
 * $RCSfile: SphereCg.java,v $
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
 * $Revision: 1.4 $
 * $Date: 2007/02/09 17:21:33 $
 * $State: Exp $
 */

package org.jdesktop.j3d.examples.cg_shader;

import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.shader.StringIO;
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.awt.GraphicsConfiguration;
import java.io.*;
import javax.swing.JOptionPane;
import org.jdesktop.j3d.examples.Resources;

/**
 * Simple Java 3D example program with programmable shader.
 */
public class SphereCg extends javax.swing.JFrame {

    // Constants for type of light to use
    private static final int DIRECTIONAL_LIGHT = 0;
    private static final int POINT_LIGHT = 1;
    private static final int SPOT_LIGHT = 2;

    // Flag indicates type of lights: directional, point, or spot
    // lights.  This flag is set based on command line argument
    private static int lightType = DIRECTIONAL_LIGHT;
    private SimpleUniverse univ = null;
    private BranchGroup scene = null;

    public BranchGroup createSceneGraph() {
	Color3f eColor    = new Color3f(0.0f, 0.0f, 0.0f);
	Color3f sColor    = new Color3f(1.0f, 1.0f, 1.0f);
	Color3f objColor  = new Color3f(0.6f, 0.6f, 0.6f);
	Color3f lColor1   = new Color3f(1.0f, 0.0f, 0.0f);
	Color3f lColor2   = new Color3f(0.0f, 1.0f, 0.0f);
	Color3f alColor   = new Color3f(0.2f, 0.2f, 0.2f);
	Color3f bgColor   = new Color3f(0.05f, 0.05f, 0.2f);

	Transform3D t;

	// Create the root of the branch graph
	BranchGroup objRoot = new BranchGroup();

        // Create a Transformgroup to scale all objects so they
        // appear in the scene.
        TransformGroup objScale = new TransformGroup();
        Transform3D t3d = new Transform3D();
        t3d.setScale(0.4);
        objScale.setTransform(t3d);
        objRoot.addChild(objScale);

	// Create a bounds for the background and lights
	BoundingSphere bounds =
	    new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);

	// Set up the background
	Background bg = new Background(bgColor);
	bg.setApplicationBounds(bounds);
	objScale.addChild(bg);

	// Create a Sphere object, generate one copy of the sphere,
	// and add it into the scene graph.
	ShaderAppearance a = new ShaderAppearance();
	Material m = new Material(objColor, eColor, objColor, sColor, 100.0f);
	m.setLightingEnable(true);
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

	a.setShaderProgram(shaderProgram);
	a.setMaterial(m);
	Sphere sph = new Sphere(1.0f, Sphere.GENERATE_NORMALS, 200, a);
	objScale.addChild(sph);

	// Create the transform group node for the each light and initialize
	// it to the identity.  Enable the TRANSFORM_WRITE capability so that
	// our behavior code can modify it at runtime.  Add them to the root
	// of the subgraph.
	TransformGroup l1RotTrans = new TransformGroup();
	l1RotTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	objScale.addChild(l1RotTrans);

	TransformGroup l2RotTrans = new TransformGroup();
	l2RotTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	objScale.addChild(l2RotTrans);

	// Create transformations for the positional lights
	t = new Transform3D();
	Vector3d lPos1 =  new Vector3d(0.0, 0.0, 2.0);
	t.set(lPos1);
	TransformGroup l1Trans = new TransformGroup(t);
	l1RotTrans.addChild(l1Trans);

	t = new Transform3D();
	Vector3d lPos2 = new Vector3d(0.5, 0.8, 2.0);
	t.set(lPos2);
	TransformGroup l2Trans = new TransformGroup(t);
	l2RotTrans.addChild(l2Trans);

	// Create Geometry for point lights
	ColoringAttributes caL1 = new ColoringAttributes();
	ColoringAttributes caL2 = new ColoringAttributes();
	caL1.setColor(lColor1);
	caL2.setColor(lColor2);
	Appearance appL1 = new Appearance();
	Appearance appL2 = new Appearance();
	appL1.setColoringAttributes(caL1);
	appL2.setColoringAttributes(caL2);
	l1Trans.addChild(new Sphere(0.05f, appL1));
	l2Trans.addChild(new Sphere(0.05f, appL2));

	// Create lights
	AmbientLight aLgt = new AmbientLight(alColor);

	Light lgt1 = null;
	Light lgt2 = null;

	Point3f lPoint  = new Point3f(0.0f, 0.0f, 0.0f);
	Point3f atten = new Point3f(1.0f, 0.0f, 0.0f);
	Vector3f lDirect1 = new Vector3f(lPos1);
	Vector3f lDirect2 = new Vector3f(lPos2);
	lDirect1.negate();
	lDirect2.negate();

	switch (lightType) {
	case DIRECTIONAL_LIGHT:
	    lgt1 = new DirectionalLight(lColor1, lDirect1);
	    lgt2 = new DirectionalLight(lColor2, lDirect2);
	    break;
	case POINT_LIGHT:
	    lgt1 = new PointLight(lColor1, lPoint, atten);
	    lgt2 = new PointLight(lColor2, lPoint, atten);
	    break;
	case SPOT_LIGHT:
	    lgt1 = new SpotLight(lColor1, lPoint, atten, lDirect1,
				 25.0f * (float)Math.PI / 180.0f, 10.0f);
	    lgt2 = new SpotLight(lColor2, lPoint, atten, lDirect2,
				 25.0f * (float)Math.PI / 180.0f, 10.0f);
	    break;
	}

	// Set the influencing bounds
	aLgt.setInfluencingBounds(bounds);
	lgt1.setInfluencingBounds(bounds);
	lgt2.setInfluencingBounds(bounds);

	// Add the lights into the scene graph
	objScale.addChild(aLgt);
	l1Trans.addChild(lgt1);
	l2Trans.addChild(lgt2);

	// Create a new Behavior object that will perform the desired
	// operation on the specified transform object and add it into the
	// scene graph.
	Transform3D yAxis = new Transform3D();
	Alpha rotor1Alpha = new Alpha(-1, Alpha.INCREASING_ENABLE,
				     0, 0,
				     4000, 0, 0,
				     0, 0, 0);
	RotationInterpolator rotator1 =
	    new RotationInterpolator(rotor1Alpha,
				     l1RotTrans,
				     yAxis,
				     0.0f, (float) Math.PI*2.0f);
	rotator1.setSchedulingBounds(bounds);
	l1RotTrans.addChild(rotator1);

	// Create a new Behavior object that will perform the desired
	// operation on the specified transform object and add it into the
	// scene graph.
	Alpha rotor2Alpha = new Alpha(-1, Alpha.INCREASING_ENABLE,
				     0, 0,
				     1000, 0, 0,
				     0, 0, 0);
	RotationInterpolator rotator2 =
	    new RotationInterpolator(rotor2Alpha,
				     l2RotTrans,
				     yAxis,
				     0.0f, 0.0f);
	bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);
	rotator2.setSchedulingBounds(bounds);
	l2RotTrans.addChild(rotator2);

	// Create a position interpolator and attach it to the view
	// platform
	TransformGroup vpTrans =
	    univ.getViewingPlatform().getViewPlatformTransform();
	Transform3D axisOfTranslation = new Transform3D();
	Alpha transAlpha = new Alpha(-1,
				      Alpha.INCREASING_ENABLE |
				      Alpha.DECREASING_ENABLE,
				      0, 0,
				      5000, 0, 0,
				      5000, 0, 0);
	axisOfTranslation.rotY(-Math.PI/2.0);
	PositionInterpolator translator =
	    new PositionInterpolator(transAlpha,
				     vpTrans,
				     axisOfTranslation,
				     2.0f, 3.5f);
	translator.setSchedulingBounds(bounds);
	objScale.addChild(translator);

        // Let Java 3D perform optimizations on this scene graph.
        objRoot.compile();

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
                JOptionPane.showMessageDialog(SphereCg.this,
                              error.toString(),
                              "ShaderError",
                              JOptionPane.ERROR_MESSAGE);
            }
        });

        // This will move the ViewPlatform back a bit so the
	// objects in the scene can be viewed.
	univ.getViewingPlatform().setNominalViewingTransform();
        
        // Ensure at least 5 msec per frame (i.e., < 200Hz)
	univ.getViewer().getView().setMinimumFrameCycleTime(5);

	return canvas3d;
    }


    /**
     * Creates new form SphereCg
     */
    public SphereCg() {
        
        // Initialize the GUI components
        initComponents();
        
        // Create Canvas3D and SimpleUniverse; add canvas to drawing panel
        Canvas3D c = createUniverse();
        drawingPanel.add(c, java.awt.BorderLayout.CENTER);
        
	// Create the content branch and add it to the universe
	scene = createSceneGraph();
	univ.addBranchGraph(scene);    }

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
        setTitle("SphereCg");
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
                SphereCg sphereCg = new SphereCg();
                sphereCg.setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel drawingPanel;
    // End of variables declaration//GEN-END:variables
    
}
