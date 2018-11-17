/*
 * $RCSfile: DistortGlyphTest.java,v $
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
 * $Date: 2007/02/09 17:21:36 $
 * $State: Exp $
 */

package org.jdesktop.j3d.examples.distort_glyph;

import com.sun.j3d.utils.behaviors.mouse.*;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.*;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import javax.media.j3d.*;
import javax.vecmath.*;
import org.jdesktop.j3d.examples.Resources;

public class DistortGlyphTest extends javax.swing.JFrame {

    private SimpleUniverse univ = null;
    private BranchGroup scene = null;

    // get a nice graphics config
    private static GraphicsConfiguration getGraphicsConfig() {
        GraphicsConfigTemplate3D template = new GraphicsConfigTemplate3D();
        template.setSceneAntialiasing(GraphicsConfigTemplate3D.PREFERRED);
        GraphicsConfiguration gcfg = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getBestConfiguration(template);
        return gcfg;
    }
    
    private void setupLights(BranchGroup root) {
        // set up the BoundingSphere for all the lights
        BoundingSphere bounds = new BoundingSphere(new Point3d(), 100.0);

        // Set up the ambient light
        AmbientLight lightAmbient = new AmbientLight(new Color3f(0.37f, 0.37f, 0.37f));
        lightAmbient.setInfluencingBounds(bounds);
        root.addChild(lightAmbient);

        // Set up the directional light
        Vector3f lightDirection1 = new Vector3f(0.0f, 0.0f, -1.0f);
        DirectionalLight lightDirectional1 = new DirectionalLight(new Color3f(1.00f, 0.10f, 0.00f), lightDirection1);
        lightDirectional1.setInfluencingBounds(bounds);
        lightDirectional1.setCapability(Light.ALLOW_STATE_WRITE);
        root.addChild(lightDirectional1);

        Point3f lightPos1 = new Point3f(-4.0f, 8.0f, 16.0f);
        Point3f lightAttenuation1 = new Point3f(1.0f, 0.0f, 0.0f);
        PointLight pointLight1 = new PointLight(new Color3f(0.37f, 1.00f, 0.37f), lightPos1, lightAttenuation1);
        pointLight1.setInfluencingBounds(bounds);
        root.addChild(pointLight1);

        Point3f lightPos2 = new Point3f(-16.0f, 8.0f, 4.0f);
        Point3f lightAttenuation2 = new Point3f(1.0f, 0.0f, 0.0f);
        PointLight pointLight2 = new PointLight(new Color3f(0.37f, 0.37f, 1.00f), lightPos2, lightAttenuation2);
        pointLight2.setInfluencingBounds(bounds);
        root.addChild(pointLight2);
    }
        
    public BranchGroup createSceneGraph() {
        // Create the root of the branch graph
        BranchGroup objRoot = new BranchGroup();

        setupLights(objRoot);

        TransformGroup objTransform = new TransformGroup();
        objTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

        objRoot.addChild(objTransform);

        // setup a nice textured appearance
        Appearance app = new Appearance();
        Color3f objColor = new Color3f(1.0f, 0.7f, 0.8f);
        Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
        app.setMaterial(new Material(objColor, black, objColor, black, 80.0f));
        Texture txtr = new TextureLoader(Resources.getResource("resources/images/gold.jpg"),this).getTexture();
        app.setTexture(txtr);
        TexCoordGeneration tcg = new TexCoordGeneration(TexCoordGeneration.SPHERE_MAP,TexCoordGeneration.TEXTURE_COORDINATE_2);
        app.setTexCoordGeneration(tcg);

        // use a customized FontExtrusion object to control the depth of the text
        java.awt.geom.GeneralPath gp = new java.awt.geom.GeneralPath();
        gp.moveTo(0, 0);
        gp.lineTo(.01f, .01f);
        gp.lineTo(.2f, .01f);
        gp.lineTo(.21f, 0f);
        FontExtrusion fontEx = new FontExtrusion(gp);

        // our glyph
        Font fnt = new Font("dialog", Font.BOLD, 1);
        Font3D f3d = new Font3D(fnt, .001, fontEx);
        GeometryArray geom = f3d.getGlyphGeometry('A');
        Shape3D shape = new Shape3D(geom, app);
        objTransform.addChild(shape);

        // the DistortBehavior
        DistortBehavior eb = new DistortBehavior(shape, 1000, 1000);
        eb.setSchedulingBounds(new BoundingSphere());
        objTransform.addChild(eb);

        MouseRotate myMouseRotate = new MouseRotate();
        myMouseRotate.setTransformGroup(objTransform);
        myMouseRotate.setSchedulingBounds(new BoundingSphere());
        objRoot.addChild(myMouseRotate);

        MouseTranslate myMouseTranslate = new MouseTranslate();
        myMouseTranslate.setTransformGroup(objTransform);
        myMouseTranslate.setSchedulingBounds(new BoundingSphere());
        objRoot.addChild(myMouseTranslate);

        MouseZoom myMouseZoom = new MouseZoom();
        myMouseZoom.setTransformGroup(objTransform);
        myMouseZoom.setSchedulingBounds(new BoundingSphere());
        objRoot.addChild(myMouseZoom);

        // Let Java 3D perform optimizations on this scene graph.
        objRoot.compile();

        return objRoot;
    }
    
    private Canvas3D createUniverse() {
        
	// Create a Canvas3D using a nice configuration
	Canvas3D c = new Canvas3D(getGraphicsConfig());

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
     * Creates new form DistortGlyphTest2
     */
    public DistortGlyphTest() {
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
        setTitle("DistortGlyphTest");
        drawingPanel.setLayout(new java.awt.BorderLayout());

        drawingPanel.setPreferredSize(new java.awt.Dimension(700, 700));
        getContentPane().add(drawingPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DistortGlyphTest().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel drawingPanel;
    // End of variables declaration//GEN-END:variables
    
}
