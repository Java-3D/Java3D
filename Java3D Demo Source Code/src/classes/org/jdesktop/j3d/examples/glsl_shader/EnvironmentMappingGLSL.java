/*
 * $RCSfile: EnvironmentMappingGLSL.java,v $
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

package org.jdesktop.j3d.examples.glsl_shader;

import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.shader.StringIO;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.awt.GraphicsConfiguration;
import java.io.IOException;
import java.net.URL;
import javax.swing.JOptionPane;
import org.jdesktop.j3d.examples.Resources;

public class EnvironmentMappingGLSL extends javax.swing.JFrame {

    private URL textureURL = null;
    private static final int NUM_TEX_UNITS = 1;
    private static final int TEX_UNIT = 0;

    SimpleUniverse univ = null;

    public BranchGroup createSceneGraph() {
	// Create the root of the branch graph
	BranchGroup objRoot = new BranchGroup();

	// Create the TransformGroup node and initialize it to the
	// identity. Enable the TRANSFORM_WRITE capability so that
	// our behavior code can modify it at run time. Add it to
	// the root of the subgraph.
	TransformGroup objTrans = new TransformGroup();
	objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	objRoot.addChild(objTrans);

        // Create texture object
        textureURL = Resources.getResource("resources/images/duke-gears.jpg");
        Texture tex = new TextureLoader(textureURL, this).getTexture();
        // Create the shader program
	String vertexProgram = null;
	String fragmentProgram = null;
	try {
	    vertexProgram = StringIO.readFully(Resources.getResource("glsl_shader/envmap.vert"));
	    fragmentProgram = StringIO.readFully(Resources.getResource("glsl_shader/envmap.frag"));
	}
	catch (IOException e) {
	    System.err.println(e);
	}
	Shader[] shaders = new Shader[2];
	shaders[0] = new SourceCodeShader(Shader.SHADING_LANGUAGE_GLSL,
					  Shader.SHADER_TYPE_VERTEX,
					  vertexProgram);
	shaders[1] = new SourceCodeShader(Shader.SHADING_LANGUAGE_GLSL,
					  Shader.SHADER_TYPE_FRAGMENT,
					  fragmentProgram);
        final String[] shaderAttrNames = {
            "LightPos",
            "BaseColor",
            "MixRatio",
            "EnvMap",
        };
        final Object[] shaderAttrValues = {
            new Point3f(1.0f, -1.0f, 2.0f),
            new Color3f(0.2f, 0.9f, 0.5f),
            new Float(0.4f),
            new Integer(TEX_UNIT),
        };
	ShaderProgram shaderProgram = new GLSLShaderProgram();
	shaderProgram.setShaders(shaders);
        shaderProgram.setShaderAttrNames(shaderAttrNames);
        
        // Create the shader attribute set
        ShaderAttributeSet shaderAttributeSet = new ShaderAttributeSet();
        for (int i = 0; i < shaderAttrNames.length; i++) {
            ShaderAttribute shaderAttribute =
                    new ShaderAttributeValue(shaderAttrNames[i],  shaderAttrValues[i]);
            shaderAttributeSet.put(shaderAttribute);
        }

        // Create shader appearance to hold the shader program and
        // shader attributes
	ShaderAppearance app = new ShaderAppearance();
	app.setShaderProgram(shaderProgram);
	app.setShaderAttributeSet(shaderAttributeSet);

        // Put the texture in specified texture unit
        TextureUnitState[] tus = new TextureUnitState[NUM_TEX_UNITS];
        tus[TEX_UNIT] = new TextureUnitState();
        tus[TEX_UNIT].setTexture(tex);
        app.setTextureUnitState(tus);

	// Create a Sphere object using the shader appearance,
	// and add it into the scene graph.
        Sphere sph = new Sphere(0.4f, Sphere.GENERATE_NORMALS, 20, app);
	objTrans.addChild(sph);

	// Create a new Behavior object that will perform the
	// desired operation on the specified transform and add
	// it into the scene graph.
	Transform3D yAxis = new Transform3D();
	Alpha rotationAlpha = new Alpha(-1, 4000);

	RotationInterpolator rotator =
	    new RotationInterpolator(rotationAlpha, objTrans, yAxis,
				     0.0f, (float) Math.PI*2.0f);
	BoundingSphere bounds =
	    new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);
	rotator.setSchedulingBounds(bounds);
	objRoot.addChild(rotator);

        // Have Java 3D perform optimizations on this scene graph.
        //objRoot.compile();

	return objRoot;
    }

    private Canvas3D initScene() {
	GraphicsConfiguration config =
	    SimpleUniverse.getPreferredConfiguration();

	Canvas3D c = new Canvas3D(config);

	BranchGroup scene = createSceneGraph();
	univ = new SimpleUniverse(c);

        // Add a ShaderErrorListener
        univ.addShaderErrorListener(new ShaderErrorListener() {
            public void errorOccurred(ShaderError error) {
                error.printVerbose();
                JOptionPane.showMessageDialog(EnvironmentMappingGLSL.this,
                              error.toString(),
                              "ShaderError",
                              JOptionPane.ERROR_MESSAGE);
            }
        });

	ViewingPlatform viewingPlatform = univ.getViewingPlatform();
	// This will move the ViewPlatform back a bit so the
	// objects in the scene can be viewed.
	viewingPlatform.setNominalViewingTransform();

	univ.addBranchGraph(scene);

	return c;
    }

    /**
     * Creates new form EnvironmentMappingGLSL
     */
    public EnvironmentMappingGLSL() {
	// Initialize the GUI components
        initComponents();

	// Create the scene and add the Canvas3D to the drawing panel
	Canvas3D c = initScene();
	drawingPanel.add(c, java.awt.BorderLayout.CENTER);
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
        setTitle("EnvironmentMappingGLSL");
        drawingPanel.setLayout(new java.awt.BorderLayout());

        drawingPanel.setPreferredSize(new java.awt.Dimension(500, 500));
        getContentPane().add(drawingPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new EnvironmentMappingGLSL().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel drawingPanel;
    // End of variables declaration//GEN-END:variables
    
}
