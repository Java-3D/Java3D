/*
 * $RCSfile: InterleavedTest.java,v $
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

package org.jdesktop.j3d.examples.geometry_by_ref;

import java.awt.*;
import java.awt.event.*;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.image.TextureLoader;
import javax.media.j3d.*;
import javax.vecmath.*;
import javax.swing.*;
import javax.swing.border.*;
import com.sun.j3d.utils.behaviors.vp.*;
import org.jdesktop.j3d.examples.Resources;

public class InterleavedTest extends JApplet implements ActionListener {

    RenderingAttributes ra;
    ColoringAttributes ca;
    Material mat;			   
    Appearance app;			   
    JComboBox geomType;
    JCheckBox transparency;
    JCheckBox textureBox;
    Shape3D shape;			   
    TransparencyAttributes transp;

    GeometryArray tetraRegular, tetraStrip, tetraIndexed, tetraIndexedStrip;
    GeometryArray[] geoArrays = new GeometryArray[4];

   // Globally used colors
   Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
   Color3f red = new Color3f(1.0f, 0.0f, 0.0f);
   Color3f green = new Color3f(0.0f, 1.0f, 0.0f);
   Color3f blue = new Color3f(0.0f, 0.0f, 1.0f);
   Color3f[] colors = {white, red, green, blue};			   

    private static final float sqrt3 = (float) Math.sqrt(3.0);
    private static final float sqrt3_3 = sqrt3 / 3.0f;
    private static final float sqrt24_3 = (float) Math.sqrt(24.0) / 3.0f;

    private static final float ycenter = 0.5f * sqrt24_3;
    private static final float zcenter = -sqrt3_3;

    private static final Point3f p1 = 
	new Point3f(-1.0f, -ycenter, -zcenter);
    private static final Point3f p2 = 
	new Point3f(1.0f, -ycenter, -zcenter);
    private static final Point3f p3 =
	new Point3f(0.0f, -ycenter, -sqrt3 - zcenter);
    private static final Point3f p4 =
	new Point3f(0.0f, sqrt24_3 - ycenter, 0.0f);

    private static final Point2f t1 = new Point2f(0.0f, 0.0f);
    private static final Point2f t2 = new Point2f(0.5f, 1.0f);
    private static final Point2f t3 = new Point2f(1.0f, 0.0f);
    private static final Point2f t4 = new Point2f(1.0f, 1.0f);

    private static final Color3f c1 = new Color3f(1.0f, 0.0f, 0.0f);
    private static final Color3f c2 = new Color3f(0.0f, 1.0f, 0.0f);
    private static final Color3f c3 = new Color3f(0.0f, 1.0f, 1.0f);
    private static final Color3f c4 = new Color3f(1.0f, 1.0f, 0.0f);
    

    private static final float[] interleaved = {
	t1.x, t1.y,
	t1.x, t1.y,
	c1.x, c1.y, c1.z, // front face
	p1.x, p1.y, p1.z, // front face
	t2.x, t2.y,
	t2.x, t2.y,
	c2.x, c2.y, c2.z,
	p2.x, p2.y, p2.z,
	t4.x, t4.y,
	t4.x, t4.y,
	c4.x, c4.y, c4.z,
	p4.x, p4.y, p4.z,
	
	t1.x, t1.y,
	t1.x, t1.y,
	c1.x, c1.y, c1.z,// left, back face
	p1.x, p1.y, p1.z,// left, back face
	t4.x, t4.y,
	t4.x, t4.y,
	c4.x, c4.y, c4.z,
	p4.x, p4.y, p4.z,
	t3.x, t3.y,
	t3.x, t3.y,
	c3.x, c3.y, c3.z,
	p3.x, p3.y, p3.z,
	
	t2.x, t2.y,
	t2.x, t2.y,
	c2.x, c2.y, c2.z,// right, back face
	p2.x, p2.y, p2.z,// right, back face
	t3.x, t3.y,
	t3.x, t3.y,
	c3.x, c3.y, c3.z,
	p3.x, p3.y, p3.z,
	t4.x, t4.y,
	t4.x, t4.y,
	c4.x, c4.y, c4.z,
	p4.x, p4.y, p4.z,
	
	t1.x, t1.y,
	t1.x, t1.y,
	c1.x, c1.y, c1.z,// bottom face
	p1.x, p1.y, p1.z,// bottom face
	t3.x, t3.y,
	t3.x, t3.y,
	c3.x, c3.y, c3.z,
	p3.x, p3.y, p3.z,
	t2.x, t2.y,
	t2.x, t2.y,
	c2.x, c2.y, c2.z,
	p2.x, p2.y, p2.z,
    };
 
    private static final float[] indexedInterleaved = {
	t1.x,t1.y,
	t1.x,t1.y,
	c1.x,c1.y,c1.z,
	p1.x,p1.y,p1.z,
	t2.x,t2.y,
	t2.x,t2.y,
	c2.x,c2.y,c2.z,
	p2.x,p2.y,p2.z,
	t3.x,t3.y,
	t3.x,t3.y,
	c3.x,c3.y,c3.z,
	p3.x,p3.y,p3.z,
	t4.x,t4.y,
	t4.x,t4.y,
	c4.x,c4.y,c4.z,
	p4.x,p4.y,p4.z,
    };
    

    private static final int[] indices = {0,1,3,0,3,2,1,2,3,0,2,1};
    private int[] stripVertexCounts = {3,3,3,3};

    TextureUnitState textureUnitState[] = new TextureUnitState[2];
    Texture tex1;
    Texture tex2;

    private java.net.URL texImage1 = null;
    private java.net.URL texImage2 = null;

    private SimpleUniverse u;

   BranchGroup createSceneGraph() {
	BranchGroup objRoot = new BranchGroup();

	// Set up attributes to render lines
        app = new Appearance();
	app.setCapability(Appearance.ALLOW_TEXTURE_UNIT_STATE_WRITE);
	
	transp = new TransparencyAttributes();
	transp.setTransparency(0.5f);
	transp.setCapability(TransparencyAttributes.ALLOW_MODE_WRITE);
	transp.setTransparencyMode(TransparencyAttributes.NONE);
	app.setTransparencyAttributes(transp);

        // load textures
        TextureAttributes texAttr1 = new TextureAttributes();
        texAttr1.setTextureMode(TextureAttributes.DECAL);
        TextureAttributes texAttr2 = new TextureAttributes();
        texAttr2.setTextureMode(TextureAttributes.MODULATE);

        TextureLoader tex = new TextureLoader(texImage1, new String("RGB"), this);
        if (tex == null)
            return null;
        tex1 = tex.getTexture();

        tex = new TextureLoader(texImage2, new String("RGB"), this);
        if (tex == null)
            return null;
        tex2 = tex.getTexture();

        textureUnitState[0] = new TextureUnitState(tex1, texAttr1, null);
        textureUnitState[1] = new TextureUnitState(tex2, texAttr2, null);

	tetraRegular = createGeometry(1);
	tetraStrip =createGeometry(2);
	tetraIndexed = createGeometry(3);
	tetraIndexedStrip = createGeometry(4);

	geoArrays[0] = tetraRegular;
	geoArrays[1] = tetraStrip;
	geoArrays[2] = tetraIndexed;
	geoArrays[3] = tetraIndexedStrip;
	
	shape = new Shape3D(tetraRegular, app);
	shape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);

	Transform3D t = new Transform3D();
	// move the object upwards
	t.set(new Vector3f(0.0f, 0.3f, 0.0f));

	// rotate the shape
	Transform3D temp = new Transform3D();
        temp.rotX(Math.PI/4.0d);
	t.mul(temp);
        temp.rotY(Math.PI/4.0d);
        t.mul(temp);
	
	// Shrink the object 
	t.setScale(0.6);

	TransformGroup trans = new TransformGroup(t);
	trans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	trans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

	objRoot.addChild(trans);
	trans.addChild(shape);

	BoundingSphere bounds =
	    new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);

	// Set up the global lights
	Color3f lColor1 = new Color3f(0.7f, 0.7f, 0.7f);
	Vector3f lDir1  = new Vector3f(-1.0f, -1.0f, -1.0f);
	Color3f alColor = new Color3f(0.2f, 0.2f, 0.2f);

	AmbientLight aLgt = new AmbientLight(alColor);
	aLgt.setInfluencingBounds(bounds);
	DirectionalLight lgt1 = new DirectionalLight(lColor1, lDir1);
	lgt1.setInfluencingBounds(bounds);
	objRoot.addChild(aLgt);
	objRoot.addChild(lgt1);
	
	// Let Java 3D perform optimizations on this scene graph.
        objRoot.compile();

	return objRoot;
    }

    JPanel createGeometryByReferencePanel() {
	JPanel panel = new JPanel();
	panel.setBorder(new TitledBorder("Geometry Type"));

	String values[] = {"Array", "Strip", "Indexed", "IndexedStrip"};
	geomType = new JComboBox(values);
	geomType.setLightWeightPopupEnabled(false);
	geomType.addActionListener(this);
	geomType.setSelectedIndex(0);
	panel.add(new JLabel("Geometry Type")); 	
	panel.add(geomType);

	transparency = new JCheckBox("EnableTransparency", 
				      false);
	transparency.addActionListener(this);
	panel.add(transparency);

	textureBox = new JCheckBox("EnableTexture", false);
	textureBox.addActionListener(this);
	panel.add(textureBox);

	return panel;
    }

    public InterleavedTest() {
    }

    public InterleavedTest(java.net.URL texURL1, java.net.URL texURL2) {
	texImage1 = texURL1;
	texImage2 = texURL2;
    }

    public void init() {

	// create textures
        texImage1 = Resources.getResource("resources/images/bg.jpg");
        if (texImage1 == null) {
            System.err.println("resources/images/bg.jpg not found");
            System.exit(1);
        }

        texImage2 = Resources.getResource("resources/images/one.jpg");
        if (texImage2 == null) {
            System.err.println("resources/images/one.jpg not found");
            System.exit(1);
        }

	Container contentPane = getContentPane();
	
        Canvas3D c = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        contentPane.add("Center", c);

        BranchGroup scene = createSceneGraph();
        // SimpleUniverse is a Convenience Utility class
        u = new SimpleUniverse(c);

	// add mouse behaviors to the viewingPlatform
	ViewingPlatform viewingPlatform = u.getViewingPlatform();

        // This will move the ViewPlatform back a bit so the
        // objects in the scene can be viewed.
        viewingPlatform.setNominalViewingTransform();

	// add Orbit behavior to the viewing platform
	OrbitBehavior orbit = new OrbitBehavior(c, OrbitBehavior.REVERSE_ALL);
	BoundingSphere bounds =
	    new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
	orbit.setSchedulingBounds(bounds);
	viewingPlatform.setViewPlatformBehavior(orbit);
	
        u.addBranchGraph(scene);


	// Create GUI
	JPanel p = new JPanel();
	BoxLayout boxlayout = new BoxLayout(p, 
					    BoxLayout.Y_AXIS);
	p.add(createGeometryByReferencePanel());
	p.setLayout(boxlayout);

	contentPane.add("South", p);
    }

    public void destroy() {
	u.cleanup();
    }

    public void actionPerformed(ActionEvent e) {
	Object target = e.getSource();
	if (target == geomType) {
	    shape.setGeometry(geoArrays[geomType.getSelectedIndex()]);

	} 
	else if (target == transparency) {
	    if (transparency.isSelected()) {
		transp.setTransparencyMode(TransparencyAttributes.BLENDED);
	    }
	    else {
		transp.setTransparencyMode(TransparencyAttributes.NONE);
	    }
	} 
	else if (target == textureBox) {
	    if (textureBox.isSelected()) {
		app.setTextureUnitState(textureUnitState);
	    }
	    else {
		app.setTextureUnitState(null);
	    }
	}
    }


			   
    public static void main(String[] args) {
        java.net.URL texURL1 = null;
        java.net.URL texURL2 = null;
        // the path to the image for an application
        texURL1 = Resources.getResource("resources/images/bg.jpg");
        if (texURL1 == null) {
            System.err.println("resources/images/bg.jpg not found");
            System.exit(1);
        }

        texURL2 = Resources.getResource("resources/images/one.jpg");
        if (texURL2 == null) {
            System.err.println("resources/images/one.jpg not found");
            System.exit(1);
        }

	Frame frame = new MainFrame(new InterleavedTest(texURL1, texURL2), 
					800, 800);
    }
			   
    public GeometryArray createGeometry (int type) {
	GeometryArray tetra = null;
        int texCoordSetMap[] = {0, 0};

	if (type == 1) {
	    tetra =new TriangleArray(12, 
				     TriangleArray.COORDINATES|
				     TriangleArray.COLOR_3|
				     /*
				       TriangleArray.NORMAL_3|
				       */
				     TriangleArray.TEXTURE_COORDINATE_2 |
				     TriangleArray.INTERLEAVED|
				     TriangleArray.BY_REFERENCE,
				     2, texCoordSetMap);
	    
	    tetra.setInterleavedVertices(interleaved);

	}
	else if (type == 2) {
	    tetra = new TriangleStripArray(12,
					   TriangleStripArray.COORDINATES|
					   TriangleStripArray.COLOR_3|
					   /*
					     TriangleArray.NORMAL_3|
					     */
					   TriangleArray.TEXTURE_COORDINATE_2 |
					   TriangleStripArray.INTERLEAVED|
					   TriangleStripArray.BY_REFERENCE,
					   2, texCoordSetMap,
					   stripVertexCounts);
	    tetra.setInterleavedVertices(interleaved);

	    
	}
	else if (type == 3) { // Indexed Geometry
	    tetra = new IndexedTriangleArray(4,
					     IndexedTriangleArray.COORDINATES|
					     IndexedTriangleArray.COLOR_3|
					     /*
					       IndexedTriangleArray.NORMAL_3|
					       */
					     IndexedTriangleArray.TEXTURE_COORDINATE_2 |
					     IndexedTriangleArray.INTERLEAVED|
					     IndexedTriangleArray.BY_REFERENCE,
					     2, texCoordSetMap,
					     12);
	    tetra.setInterleavedVertices(indexedInterleaved);
	    ((IndexedTriangleArray)tetra).setCoordinateIndices(0, indices);
	    ((IndexedTriangleArray)tetra).setColorIndices(0, indices);
	    ((IndexedTriangleArray)tetra).setTextureCoordinateIndices(
					0, 0, indices);
	    ((IndexedTriangleArray)tetra).setTextureCoordinateIndices(
					1, 0, indices);
	}
	else if (type == 4) { // Indexed strip geometry
	    tetra = new IndexedTriangleStripArray(4,
						  IndexedTriangleStripArray.COORDINATES|
						  IndexedTriangleStripArray.COLOR_3|
						  /*
						    IndexedTriangleArray.NORMAL_3|
						    */
						  IndexedTriangleArray.TEXTURE_COORDINATE_2 |
						  IndexedTriangleStripArray.INTERLEAVED|
						  IndexedTriangleStripArray.BY_REFERENCE,
					          2, texCoordSetMap,
						  12,
						  stripVertexCounts);
	    tetra.setInterleavedVertices(indexedInterleaved);
	    ((IndexedTriangleStripArray)tetra).setCoordinateIndices(0, indices);
	    ((IndexedTriangleStripArray)tetra).setColorIndices(0, indices);
	    ((IndexedTriangleStripArray)tetra).setTextureCoordinateIndices(
					0, 0, indices);
	    ((IndexedTriangleStripArray)tetra).setTextureCoordinateIndices(
					1, 0, indices);
	}
	else if (type == 5) { // Interleaved array
	}
	return tetra;
    }
}
