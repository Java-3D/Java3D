/*
 * $RCSfile: GeometryByReferenceTest.java,v $
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
 * $Date: 2007/02/09 17:21:39 $
 * $State: Exp $
 */

package org.jdesktop.j3d.examples.geometry_by_ref;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import com.sun.j3d.utils.behaviors.vp.*;

public class GeometryByReferenceTest extends JApplet implements ActionListener,
GeometryUpdater {

    RenderingAttributes ra;
    ColoringAttributes ca;
    Material mat;			   
    Appearance app;			   
    JComboBox geomType;
    JComboBox vertexType;
    JComboBox colorType;
    JCheckBox transparency;
    JComboBox updates;
    Shape3D shape;			   
    TransparencyAttributes transp;
    int updateIndex = 0;
    int colorCount = 0, vertexCount = 0;
    int vertexIndex = 0, colorIndex = 0;
    
    GeometryArray tetraRegular, tetraStrip, tetraIndexed, tetraIndexedStrip;
    GeometryArray[] geoArrays = new GeometryArray[4];		   

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


    private static final float[] floatVerts = {
	p1.x, p1.y, p1.z, // front face
	p2.x, p2.y, p2.z,
	p4.x, p4.y, p4.z,
	
	p1.x, p1.y, p1.z,// left, back face
	p4.x, p4.y, p4.z,
	p3.x, p3.y, p3.z,
	
	p2.x, p2.y, p2.z,// right, back face
	p3.x, p3.y, p3.z,
	p4.x, p4.y, p4.z,
	
	p1.x, p1.y, p1.z,// bottom face
	p3.x, p3.y, p3.z,
	p2.x, p2.y, p2.z,
    };
    
    private static final Color3f c1 = new Color3f(0.6f, 0.0f, 0.0f);
    private static final Color3f c2 = new Color3f(0.0f, 0.6f, 0.0f);
    private static final Color3f c3 = new Color3f(0.0f, 0.6f, 0.6f);
    private static final Color3f c4 = new Color3f(0.6f, 0.6f, 0.0f);


    private static final float[] floatClrs = {
	c1.x, c1.y, c1.z, // front face
	c2.x, c2.y, c2.z,
	c4.x, c4.y, c4.z,
	
	c1.x, c1.y, c1.z,// left, back face
	c4.x, c4.y, c4.z,
	c3.x, c3.y, c3.z,
	
	c2.x, c2.y, c2.z,// right, back face
	c3.x, c3.y, c3.z,
	c4.x, c4.y, c4.z,
	
	c1.x, c1.y, c1.z,// bottom face
	c3.x, c3.y, c3.z,
	c2.x, c2.y, c2.z,
    }    ;

    private static final float[] indexedFloatVerts = {
	p1.x,p1.y,p1.z,
	p2.x,p2.y,p2.z,
	p3.x,p3.y,p3.z,
	p4.x,p4.y,p4.z,

    };
    private static final float[] indexedFloatClrs = {
	c1.x,c1.y,c1.z,
	c2.x,c2.y,c2.z,
	c3.x,c3.y,c3.z,
	c4.x,c4.y,c4.z,
    };
   private static final Point3f[] p3fVerts = {
	p1, p2, p4, p1, p4, p3, p2, p3, p4, p1, p3, p2};

    private static final Point3f[] indexedP3fVerts = {p1, p2, p3, p4};
    
    private static final Color3f[] c3fClrs = {
	c1, c2, c4, c1, c4, c3, c2, c3, c4, c1, c3, c2};

    private static final Color3f[] indexedC3fClrs = {c1, c2, c3, c4};

    
    private static final int[] indices = {0,1,3,0,3,2,1,2,3,0,2,1};
    private int[] stripVertexCounts = {3,3,3,3};

    private SimpleUniverse u;

   BranchGroup createSceneGraph() {
	BranchGroup objRoot = new BranchGroup();

	// Set up attributes to render lines
        app = new Appearance();

	transp = new TransparencyAttributes();
	transp.setTransparency(0.5f);
	transp.setCapability(TransparencyAttributes.ALLOW_MODE_WRITE);
	transp.setTransparencyMode(TransparencyAttributes.NONE);
	app.setTransparencyAttributes(transp);
	
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
	shape.setCapability(Shape3D.ALLOW_GEOMETRY_READ);

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


	String vertex_types[] = { "Float","P3F"};

	vertexType = new JComboBox(vertex_types);
	vertexType.setLightWeightPopupEnabled(false);
	vertexType.addActionListener(this);
	vertexType.setSelectedIndex(0);
	panel.add(new JLabel("VertexType")); 	
	panel.add(vertexType);


	String color_types[] = { "Float","C3F"};

	colorType = new JComboBox(color_types);
	colorType.setLightWeightPopupEnabled(false);
	colorType.addActionListener(this);
	colorType.setSelectedIndex(0);
	panel.add(new JLabel("ColorType")); 	
	panel.add(colorType);
	



	return panel;
    }

    JPanel createUpdatePanel() {

	JPanel panel = new JPanel();
	panel.setBorder(new TitledBorder("Other Attributes"));

	String updateComp[] = { "None","Geometry", "Color"};
	
	transparency = new JCheckBox("EnableTransparency", 
				      false);
	transparency.addActionListener(this);
	panel.add(transparency);


	updates = new JComboBox(updateComp);
	updates.setLightWeightPopupEnabled(false);
	updates.addActionListener(this);
	updates.setSelectedIndex(0);
	panel.add(new JLabel("UpdateData")); 	
	panel.add(updates);

	return panel;
    }

    

    public GeometryByReferenceTest() {
    }

    public void init() {
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
        u.addBranchGraph(scene);

	// add Orbit behavior to the ViewingPlatform
	OrbitBehavior orbit = new OrbitBehavior(c, OrbitBehavior.REVERSE_ALL);
	BoundingSphere bounds =
	    new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
	orbit.setSchedulingBounds(bounds);
	viewingPlatform.setViewPlatformBehavior(orbit);

	// Create GUI
	JPanel p = new JPanel();
	BoxLayout boxlayout = new BoxLayout(p, 
					    BoxLayout.Y_AXIS);
	p.add(createGeometryByReferencePanel());
	p.add(createUpdatePanel());
	p.setLayout(boxlayout);

	contentPane.add("South", p);
    }

    public void destroy() {
	u.cleanup();
    }

    public void actionPerformed(ActionEvent e) {
	Object target = e.getSource();
	GeometryArray geo;
	boolean setColor = false, setVertex = false;
	if (target == geomType) {
	    geo = geoArrays[geomType.getSelectedIndex()];
	    // Set everything to null, and set it later ..
	    geo.setColorRefFloat(null);
	    geo.setColorRef3f(null);
	    geo.setCoordRefFloat(null);
	    geo.setCoordRef3f(null);
	    shape.setGeometry(geoArrays[geomType.getSelectedIndex()]);

	    setColor = true;
	    setVertex= true;

	    
	} 
	else if (target == transparency) {
	    if (transparency.isSelected()) {
		transp.setTransparencyMode(TransparencyAttributes.BLENDED);
	    }
	    else {
		transp.setTransparencyMode(TransparencyAttributes.NONE);
	    }


	}
	else if (target == updates) {
	    updateIndex = updates.getSelectedIndex();
	    if (updateIndex == 1) {
		System.out.println("Doing coordinate update");
		((GeometryArray)(shape.getGeometry())).updateData(this);
	    }
	    else if (updateIndex == 2) {
		System.out.println("Doing color update");
		((GeometryArray)(shape.getGeometry())).updateData(this);
	    }

	}
	else if (target == vertexType) {
	    geo = ((GeometryArray)shape.getGeometry());
	    if (vertexIndex == 0) {
		geo.setCoordRefFloat(null);
	    }
	    else if (vertexIndex == 1) {
		geo.setCoordRef3f(null);
	    }
	    vertexIndex = vertexType.getSelectedIndex();
	    setVertex = true;
	}
	else if (target == colorType) {
	    geo = (GeometryArray) shape.getGeometry();
	    if (colorIndex == 0) {
		geo.setColorRefFloat(null);
	    }
	    else if (colorIndex == 1) {
		geo.setColorRef3f(null);
	    }
	    colorIndex = colorType.getSelectedIndex();
	    setColor = true;
	}

	if (setVertex) {
	    geo = (GeometryArray) shape.getGeometry();
	    if (vertexIndex == 0) {
		if (geo instanceof IndexedGeometryArray)
		    geo.setCoordRefFloat(indexedFloatVerts);
		else
		    geo.setCoordRefFloat(floatVerts);
	    }
	    else if (vertexIndex == 1) {
		if (geo instanceof IndexedGeometryArray)
		    geo.setCoordRef3f(indexedP3fVerts);
		else
		    geo.setCoordRef3f(p3fVerts);
	    }

	}
	if (setColor) {
	    geo = (GeometryArray) shape.getGeometry();
	    if (colorIndex == 0) {
		if (geo instanceof IndexedGeometryArray)
		    geo.setColorRefFloat(indexedFloatClrs);
		else
		    geo.setColorRefFloat(floatClrs);
	    }
	    else if (colorIndex == 1) {
		if (geo instanceof IndexedGeometryArray)
		    geo.setColorRef3f(indexedC3fClrs);
		else
		    geo.setColorRef3f(c3fClrs);
	    }
	}

    }


			   
    public static void main(String[] args) {
	Frame frame = new MainFrame(new GeometryByReferenceTest(), 800, 800);
    }
			   
    public GeometryArray createGeometry (int type) {
	GeometryArray tetra = null;
	if (type == 1) {
	    tetra =new TriangleArray(12, 
				     TriangleArray.COORDINATES|
				     TriangleArray.COLOR_3|
				     TriangleArray.BY_REFERENCE);
	    
	    tetra.setCoordRefFloat(floatVerts);
	    tetra.setColorRefFloat(floatClrs);

	}
	else if (type == 2) {
	    tetra = new TriangleStripArray(12,
					   TriangleStripArray.COORDINATES|
					   TriangleStripArray.COLOR_3|
					   TriangleStripArray.BY_REFERENCE,
					   stripVertexCounts);
	    tetra.setCoordRefFloat(floatVerts);
	    tetra.setColorRefFloat(floatClrs);
	    
	}
	else if (type == 3) { // Indexed Geometry
	    tetra = new IndexedTriangleArray(4,
					     IndexedTriangleArray.COORDINATES|
					     IndexedTriangleArray.COLOR_3|
					     IndexedTriangleArray.BY_REFERENCE,
					     12);
	    tetra.setCoordRefFloat(indexedFloatVerts);
	    tetra.setColorRefFloat(indexedFloatClrs);
	    ((IndexedTriangleArray)tetra).setCoordinateIndices(0, indices);
	    ((IndexedTriangleArray)tetra).setColorIndices(0, indices);
	}
	else if (type == 4) { // Indexed strip geometry
	    tetra = new IndexedTriangleStripArray(4,
						  IndexedTriangleStripArray.COORDINATES|
						  IndexedTriangleStripArray.COLOR_3|
						  IndexedTriangleStripArray.BY_REFERENCE,
						  12,
						  stripVertexCounts);
	    tetra.setCoordRefFloat(indexedFloatVerts);
	    tetra.setColorRefFloat(indexedFloatClrs);
	    ((IndexedTriangleStripArray)tetra).setCoordinateIndices(0, indices);
	    ((IndexedTriangleStripArray)tetra).setColorIndices(0, indices);
	}

	if (tetra != null)
	    tetra.setCapability(GeometryArray.ALLOW_REF_DATA_WRITE);
	return tetra;
    }

    public void updateData(Geometry geometry) {
	int i;
	float val;

	
	if (updateIndex == 1) { // geometry
	    // Translate the geometry by a small amount in x
	    vertexCount++;
	    if ((vertexCount &1) == 1)
		val = 0.2f;
	    else
		val = -0.2f;

	    if (vertexIndex == 0) {
		// Do Indexed geometry
		for (i = 0; i < indexedFloatVerts.length; i+=3) {
		    indexedFloatVerts[i] += val;
		}
		// Do non-indexed float geometry
		for (i = 0; i < floatVerts.length; i+=3) {
		    floatVerts[i] += val;
		}
	    }
	    else {
		// If p3f do each point only once
		for (i = 0; i < indexedP3fVerts.length; i++) {
		    indexedP3fVerts[i].x += val;
		}
	    }

	}
	else if (updateIndex == 2) { // colors
	    colorCount++;
	    if ((colorCount & 1) == 1)
		val = 0.4f;
	    else
		val = -0.4f;
	    if (colorIndex == 0) {
		// Do Indexed geometry
		for (i = 0; i < indexedFloatClrs.length; i+=3) {
		    indexedFloatClrs[i] += val;
		}
		// Do non-indexed float geometry
		for (i = 0; i < floatClrs.length; i+=3) {
		    floatClrs[i] += val;
		}
	    }
	    else {
		// If c3f do each point only once		
		for (i = 0; i < indexedC3fClrs.length; i++) {
		    indexedC3fClrs[i].x += val;
		}
	    }

	}

    }
}
