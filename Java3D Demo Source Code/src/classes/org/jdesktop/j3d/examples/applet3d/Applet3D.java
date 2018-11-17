/*
 * $RCSfile: Applet3D.java,v $
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
 * $Date: 2007/08/01 21:37:58 $
 * $State: Exp $
 */

package org.jdesktop.j3d.examples.applet3d;

import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import javax.media.j3d.Alpha;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.WindowConstants;
import javax.vecmath.Point3d;

/**
 * Simple Java 3D program that can be run as an application or as an applet.
 */
public class Applet3D extends javax.swing.JPanel {
    
    private SimpleUniverse univ = null;
    private BranchGroup scene = null;

    private Alpha rotationAlpha1;
    private Alpha rotationAlpha2;

    public BranchGroup createSceneGraph() {
	// Create the root of the branch graph
	BranchGroup objRoot = new BranchGroup();

	// Create two TransformGroup nodes in series
	TransformGroup objTrans1 = new TransformGroup();
	objTrans1.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	objRoot.addChild(objTrans1);

	// Create two TransformGroup nodes in series
	TransformGroup objTrans2 = new TransformGroup();
	objTrans2.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	objTrans1.addChild(objTrans2);

	// Create a simple Shape3D node; add it to the scene graph.
	objTrans2.addChild(new ColorCube(0.4));

	// Create 2 new Behavior objects that will perform the
	// desired operations on the specified transforms and add
	// them into the scene graph.
	BoundingSphere bounds =
	    new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);

        Transform3D yAxis1 = new Transform3D();
        rotationAlpha1 = new Alpha(-1, 4000);

	RotationInterpolator rotator1 =
	    new RotationInterpolator(rotationAlpha1, objTrans1, yAxis1,
				     0.0f, (float) Math.PI*2.0f);
	rotator1.setSchedulingBounds(bounds);
	objRoot.addChild(rotator1);

        Transform3D yAxis2 = new Transform3D();
        yAxis2.rotX(Math.PI/4.0);
        rotationAlpha2 = new Alpha(-1, 13000);

	RotationInterpolator rotator2 =
	    new RotationInterpolator(rotationAlpha2, objTrans2, yAxis2,
				     0.0f, (float) Math.PI*2.0f);
	rotator2.setSchedulingBounds(bounds);
	objRoot.addChild(rotator2);

	return objRoot;
    }

    private Canvas3D createUniverse(Container container) {
        GraphicsDevice graphicsDevice;
        if (container.getGraphicsConfiguration() != null) {
            graphicsDevice = container.getGraphicsConfiguration().getDevice();
        } else {
            graphicsDevice =
                    GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        }
        GraphicsConfigTemplate3D template = new GraphicsConfigTemplate3D();
	GraphicsConfiguration config = graphicsDevice.getBestConfiguration(template);

	Canvas3D c = new Canvas3D(config);

	univ = new SimpleUniverse(c);

	// This will move the ViewPlatform back a bit so the
	// objects in the scene can be viewed.
        univ.getViewingPlatform().setNominalViewingTransform();

	// Ensure at least 5 msec per frame (i.e., < 200Hz)
	univ.getViewer().getView().setMinimumFrameCycleTime(5);

	return c;
    }
    
    private void destroy() {
        univ.cleanup();
    }

    /**
     * Creates new form Applet3D
     */
    public Applet3D(Container container) {
        // Initialize the GUI components
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        initComponents();

        // Create Canvas3D and SimpleUniverse; add canvas to drawing panel
        Canvas3D c = createUniverse(container);
        drawingPanel.add(c, BorderLayout.CENTER);

        // Create the content branch and add it to the universe
        scene = createSceneGraph();
        univ.addBranchGraph(scene);
    }

    // ----------------------------------------------------------------
    
    // Applet framework

    public static class MyApplet extends JApplet {
        Applet3D mainPanel;

        public void init() {
            setLayout(new BorderLayout());
            mainPanel = new Applet3D(this);
            add(mainPanel, BorderLayout.CENTER);
        }

        public void destroy() {
            mainPanel.destroy();
        }
    }

    // Application framework

    private static class MyFrame extends JFrame {
        MyFrame() {
            setLayout(new BorderLayout());
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setTitle("Simple 3D Applet");
            getContentPane().add(new Applet3D(this), BorderLayout.CENTER);
            pack();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MyFrame().setVisible(true);
            }
        });
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        guiPanel = new javax.swing.JPanel();
        pauseButton = new javax.swing.JButton();
        resumeButton = new javax.swing.JButton();
        drawingPanel = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        guiPanel.setLayout(new java.awt.GridBagLayout());

        pauseButton.setText("Pause");
        pauseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pauseButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        guiPanel.add(pauseButton, gridBagConstraints);

        resumeButton.setText("Resume");
        resumeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resumeButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        guiPanel.add(resumeButton, gridBagConstraints);

        add(guiPanel, java.awt.BorderLayout.NORTH);

        drawingPanel.setPreferredSize(new java.awt.Dimension(500, 500));
        drawingPanel.setLayout(new java.awt.BorderLayout());
        add(drawingPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void resumeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resumeButtonActionPerformed
        rotationAlpha1.resume();
        rotationAlpha2.resume();
    }//GEN-LAST:event_resumeButtonActionPerformed

    private void pauseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pauseButtonActionPerformed
        rotationAlpha1.pause();
        rotationAlpha2.pause();
    }//GEN-LAST:event_pauseButtonActionPerformed
            
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel drawingPanel;
    private javax.swing.JPanel guiPanel;
    private javax.swing.JButton pauseButton;
    private javax.swing.JButton resumeButton;
    // End of variables declaration//GEN-END:variables
    
}
