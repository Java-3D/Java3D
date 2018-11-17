/*
 * $RCSfile: AlternateAppearanceBoundsTest.java,v $
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
 * $Date: 2007/02/09 17:21:31 $
 * $State: Exp $
 */

package org.jdesktop.j3d.examples.alternate_appearance;

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
import com.sun.j3d.utils.behaviors.mouse.*;

public class AlternateAppearanceBoundsTest extends JApplet 
implements ActionListener {


    Material mat1, altMat;			   
    Appearance app, otherApp;			   
    JComboBox altAppMaterialColor;
    JComboBox appMaterialColor;
    JCheckBox useBoundingLeaf;
    JCheckBox override;
    JComboBox boundsType;
    private Group content1 = null;
    AlternateAppearance altApp;
    Shape3D[] shapes1;
    boolean boundingLeafOn = false;
    // Globally used colors
   Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
   Color3f red = new Color3f(1.0f, 0.0f, 0.0f);
   Color3f green = new Color3f(0.0f, 1.0f, 0.0f);
   Color3f blue = new Color3f(0.0f, 0.0f, 1.0f);
   Color3f[] colors = {white, red, green, blue};
    
    private Bounds worldBounds = new BoundingSphere(
		new Point3d( 0.0, 0.0, 0.0 ),  // Center
		1000.0 );                      // Extent
    private Bounds smallBounds = new BoundingSphere(
		new Point3d( 0.0, 0.0, 0.0 ),  // Center
		0.25 );                         // Extent
    private Bounds tinyBounds = new BoundingSphere(
		new Point3d( 0.0, 0.0, 0.0 ),  // Center
		0.05 );                         // Extent
    private BoundingLeaf leafBounds = null;
    private int currentBounds = 2;

    private Bounds[] allBounds = {tinyBounds, smallBounds, worldBounds};

    DirectionalLight light1 = null;

    // Get the current bounding leaf position
    private int currentPosition = 0;
    //    Point3f pos = (Point3f)positions[currentPosition].value;

    private SimpleUniverse u = null;

    public AlternateAppearanceBoundsTest() {
    }

    public void init() {
	Container contentPane = getContentPane();
	
        Canvas3D c = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        contentPane.add("Center", c);

        BranchGroup scene = createSceneGraph();
        // SimpleUniverse is a Convenience Utility class
        u = new SimpleUniverse(c);

        // This will move the ViewPlatform back a bit so the
        // objects in the scene can be viewed.
        u.getViewingPlatform().setNominalViewingTransform();
        u.addBranchGraph(scene);


	// Create GUI
	JPanel p = new JPanel();
	BoxLayout boxlayout = new BoxLayout(p, 
					    BoxLayout.Y_AXIS);
	p.add(createBoundsPanel());
	p.add(createMaterialPanel());
	p.setLayout(boxlayout);
	
	contentPane.add("South", p);
    }

    public void destroy() {
	u.cleanup();
    }
    
    BranchGroup createSceneGraph() {
	BranchGroup objRoot = new BranchGroup();

	// Create an alternate appearance
	otherApp = new Appearance();
	altMat = new Material();
	altMat.setCapability(Material.ALLOW_COMPONENT_WRITE);
	altMat.setDiffuseColor( new Color3f( 0.0f, 1.0f, 0.0f ) );
	otherApp.setMaterial(altMat);

	altApp = new AlternateAppearance();
	altApp.setAppearance(otherApp);
	altApp.setCapability(AlternateAppearance.ALLOW_BOUNDS_WRITE);
	altApp.setCapability(AlternateAppearance.ALLOW_INFLUENCING_BOUNDS_WRITE);
	altApp.setInfluencingBounds( worldBounds );
	objRoot.addChild(altApp);
	
	// Build foreground geometry
	Appearance app1 = new Appearance();
	mat1 = new Material();
	mat1.setCapability(Material.ALLOW_COMPONENT_WRITE);
	mat1.setDiffuseColor( new Color3f( 1.0f, 0.0f, 0.0f ) );
	app1.setMaterial(mat1);
	content1 = new SphereGroup(
				   0.05f,   // radius of spheres
				   0.15f,    // x spacing
				   0.15f,   // y spacing
				   5,       // number of spheres in X
				   5,       // number of spheres in Y
				   app1, // appearance
				   true);  // alt app override = true
	objRoot.addChild( content1 );
	shapes1 = ((SphereGroup)content1).getShapes();
	


	// Add lights
	light1 = new DirectionalLight( );
	light1.setEnable( true );
	light1.setColor( new Color3f(0.2f, 0.2f, 0.2f) );
	light1.setDirection( new Vector3f( 1.0f, 0.0f, -1.0f ) );
	light1.setInfluencingBounds( worldBounds );
	light1.setCapability(
			    DirectionalLight.ALLOW_INFLUENCING_BOUNDS_WRITE );
	light1.setCapability(
			    DirectionalLight.ALLOW_BOUNDS_WRITE );
	objRoot.addChild( light1 );

	// Add an ambient light to dimly illuminate the rest of
	// the shapes in the scene to help illustrate that the
	// directional lights are being scoped... otherwise it looks
	// like we're just removing shapes from the scene
	AmbientLight ambient = new AmbientLight( );
	ambient.setEnable( true );
	ambient.setColor( new Color3f(1.0f, 1.0f, 1.0f) );
	ambient.setInfluencingBounds( worldBounds );
	objRoot.addChild( ambient );
	

	// Define a bounding leaf
	leafBounds = new BoundingLeaf( allBounds[currentBounds] );
	leafBounds.setCapability( BoundingLeaf.ALLOW_REGION_WRITE );
	objRoot.addChild( leafBounds );
	if (boundingLeafOn) {
	    altApp.setInfluencingBoundingLeaf(leafBounds);
	}
	else {
	    altApp.setInfluencingBounds(allBounds[currentBounds]);
	}
	    
	

	return objRoot;
    }
    JPanel createBoundsPanel() {
	JPanel panel = new JPanel();
	panel.setBorder(new TitledBorder("Scopes"));


	String boundsValues[] = { "Tiny Bounds", "Small Bounds", "Big Bounds"};

	boundsType = new JComboBox(boundsValues);
	boundsType.addActionListener(this);
	boundsType.setSelectedIndex(2);
	panel.add(new JLabel("Bounds")); 	
	panel.add(boundsType);

	useBoundingLeaf = new JCheckBox("Enable BoundingLeaf", 
				      boundingLeafOn);
	useBoundingLeaf.addActionListener(this);
	panel.add(useBoundingLeaf);

	override = new JCheckBox("Enable App Override", 
				      false);
	override.addActionListener(this);
	panel.add(override);

	return panel;

    }

    JPanel createMaterialPanel() {
	JPanel panel = new JPanel();
	panel.setBorder(new TitledBorder("Appearance Attributes"));

	String colorVals[] = { "WHITE", "RED", "GREEN", "BLUE"};

	altAppMaterialColor = new JComboBox(colorVals);
	altAppMaterialColor.addActionListener(this);
	altAppMaterialColor.setSelectedIndex(2);
	panel.add(new JLabel("Alternate Appearance MaterialColor")); 	
	panel.add(altAppMaterialColor);
	


	appMaterialColor = new JComboBox(colorVals);
	appMaterialColor.addActionListener(this);
	appMaterialColor.setSelectedIndex(1);
	panel.add(new JLabel("Normal Appearance MaterialColor")); 	
	panel.add(appMaterialColor);
	
	return panel;


    }

    public void actionPerformed(ActionEvent e) {
	int i;
	
	Object target = e.getSource();
	if (target == altAppMaterialColor) {
	    altMat.setDiffuseColor(colors[altAppMaterialColor.getSelectedIndex()]);
	}
	else if (target == useBoundingLeaf) {
	    boundingLeafOn = useBoundingLeaf.isSelected();
	    if (boundingLeafOn) {
		leafBounds.setRegion(allBounds[currentBounds]);
		altApp.setInfluencingBoundingLeaf( leafBounds );
	    }
	    else {
		altApp.setInfluencingBoundingLeaf( null );
		altApp.setInfluencingBounds(allBounds[currentBounds]);
	    }

	}
	else if (target == boundsType) {
	    currentBounds = boundsType.getSelectedIndex();
	    if (boundingLeafOn) {
		leafBounds.setRegion(allBounds[currentBounds]);
		altApp.setInfluencingBoundingLeaf( leafBounds );
	    }
	    else {
		altApp.setInfluencingBoundingLeaf( null );
		altApp.setInfluencingBounds(allBounds[currentBounds]);
	    }
		
	}
	else if (target == override) {
	    for (i = 0; i < shapes1.length; i++)
		shapes1[i].setAppearanceOverrideEnable(override.isSelected());
	}
	else if (target == appMaterialColor) {
	    mat1.setDiffuseColor(colors[appMaterialColor.getSelectedIndex()]);
	}

    }
			   
			   
    public static void main(String[] args) {
	Frame frame = new MainFrame(new AlternateAppearanceBoundsTest(), 800, 800);
    }

}			   
