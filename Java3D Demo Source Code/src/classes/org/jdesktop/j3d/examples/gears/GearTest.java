/*
 * $RCSfile: GearTest.java,v $
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
 * $Date: 2007/02/09 17:21:38 $
 * $State: Exp $
 */

package org.jdesktop.j3d.examples.gears;

import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.ColorCube;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.awt.GraphicsConfiguration;

/**
 * Simple Java 3D example program to display a spinning cube.
 */
public class GearTest extends javax.swing.JFrame {

    private int toothCount = 24;
    private SimpleUniverse univ = null;
    private BranchGroup scene = null;

    public BranchGroup createSceneGraph() {
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
	Color3f bgColor = new Color3f(0.05f, 0.05f, 0.2f);
	Background bgNode = new Background(bgColor);
	bgNode.setApplicationBounds(bounds);
	objScale.addChild(bgNode);

	// Set up the global lights
	Color3f light1Color = new Color3f(1.0f, 1.0f, 0.9f);
	Vector3f light1Direction  = new Vector3f(4.0f, -7.0f, -12.0f);
	Color3f light2Color = new Color3f(0.3f, 0.3f, 0.4f);
	Vector3f light2Direction  = new Vector3f(-6.0f, -2.0f, -1.0f);
	Color3f ambientColor = new Color3f(0.1f, 0.1f, 0.1f);

	AmbientLight ambientLightNode = new AmbientLight(ambientColor);
	ambientLightNode.setInfluencingBounds(bounds);
	objScale.addChild(ambientLightNode);

	DirectionalLight light1
	    = new DirectionalLight(light1Color, light1Direction);
	light1.setInfluencingBounds(bounds);
	objScale.addChild(light1);

	DirectionalLight light2
	    = new DirectionalLight(light2Color, light2Direction);
	light2.setInfluencingBounds(bounds);
	objScale.addChild(light2);

	// Create the transform group node and initialize it to the
	// identity.  Enable the TRANSFORM_WRITE capability so that
	// our behavior code can modify it at runtime.  Add it to the
	// root of the subgraph.
	TransformGroup objTrans = new TransformGroup();
	objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	objScale.addChild(objTrans);

	// Create an Appearance.
	Appearance look = new Appearance();
	Color3f objColor = new Color3f(0.5f, 0.5f, 0.6f);
	Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
	Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
	look.setMaterial(new Material(objColor, black, objColor, white, 100.0f));

	// Create a gear, add it to the scene graph.
	//	SpurGear gear = new SpurGear(toothCount, 1.0f, 0.2f,
	SpurGear gear = new SpurGearThinBody(toothCount, 1.0f, 0.2f,
				     0.05f, 0.05f, 0.3f, 0.28f, look);
	objTrans.addChild(gear);

	// Create a new Behavior object that will rotate the object and
	// add it into the scene graph.
	Transform3D yAxis = new Transform3D();
	Alpha rotationAlpha = new Alpha(-1, Alpha.INCREASING_ENABLE,
					0, 0,
					8000, 0, 0,
					0, 0, 0);

	RotationInterpolator rotator =
	    new RotationInterpolator(rotationAlpha, objTrans, yAxis,
				     0.0f, (float) Math.PI*2.0f);
	rotator.setSchedulingBounds(bounds);
	objTrans.addChild(rotator);

        // Have Java 3D perform optimizations on this scene graph.
        objRoot.compile();

	return objRoot;
    }

    private Canvas3D createUniverse() {
	// Get the preferred graphics configuration for the default screen
	GraphicsConfiguration config =
	    SimpleUniverse.getPreferredConfiguration();

	// Create a Canvas3D using the preferred configuration
	Canvas3D c = new Canvas3D(config);

	// Create simple universe with view branch
	univ = new SimpleUniverse(c);

	// This will move the ViewPlatform back a bit so the
	// objects in the scene can be viewed.
	univ.getViewingPlatform().setNominalViewingTransform();

	// Ensure at least 5 msec per frame (i.e., < 200Hz)
	univ.getViewer().getView().setMinimumFrameCycleTime(5);

	return c;
    }

    /**
     * Creates new form GearTest
     */
    public GearTest(String args[]) {
	int value;
	
	if (args.length > 1) {
            System.out.println("Usage: java GearTest [#teeth]");
            System.exit(0);
        } else if (args.length == 1) {
            try {
                value = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Illegal integer specified");
                System.out.println("Usage: java GearTest [#teeth]");
                value = 0;
                System.exit(0);
            }
            if (value <= 0) {
                System.out.println("Integer must be positive (> 0)");
                System.out.println("Usage: java GearBox [#teeth]");
                System.exit(0);
            }
            toothCount = value;
            
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
        setTitle("GearTest");
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
                GearTest gt = new GearTest(args);
                gt.setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel drawingPanel;
    // End of variables declaration//GEN-END:variables
    
}
