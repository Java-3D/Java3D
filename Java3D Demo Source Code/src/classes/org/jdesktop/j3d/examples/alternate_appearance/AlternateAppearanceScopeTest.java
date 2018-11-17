/*
 * $RCSfile: AlternateAppearanceScopeTest.java,v $
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
import com.sun.j3d.utils.behaviors.vp.*;

public class AlternateAppearanceScopeTest extends JApplet 
implements ActionListener {


    Material mat1, altMat;			   
    Appearance app, otherApp;			   
    JComboBox altAppMaterialColor;
    JComboBox appMaterialColor;
    JComboBox altAppScoping;
    JComboBox override;
    private Group content1 = null;
    private Group content2 = null;
    BoundingSphere worldBounds;
    AlternateAppearance altApp;
    Shape3D[] shapes1, shapes2;
    boolean shape1Enabled = false, shape2Enabled = false;
    // Globally used colors
   Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
   Color3f red = new Color3f(1.0f, 0.0f, 0.0f);
   Color3f green = new Color3f(0.0f, 1.0f, 0.0f);
   Color3f blue = new Color3f(0.0f, 0.0f, 1.0f);
   Color3f[] colors = {white, red, green, blue};

    private SimpleUniverse u;
    
    public AlternateAppearanceScopeTest() {
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

	OrbitBehavior orbit = new OrbitBehavior(c,
						OrbitBehavior.REVERSE_ALL);
	BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0),
						   100.0);
	orbit.setSchedulingBounds(bounds);
	viewingPlatform.setViewPlatformBehavior(orbit);
	
        u.addBranchGraph(scene);


	// Create GUI
	JPanel p = new JPanel();
	BoxLayout boxlayout = new BoxLayout(p, 
					    BoxLayout.Y_AXIS);
	p.add(createScopingPanel());
	p.add(createMaterialPanel());
	p.setLayout(boxlayout);
	
	contentPane.add("South", p);
    }

    public void destroy() {
	u.cleanup();
    }
    
    BranchGroup createSceneGraph() {
	BranchGroup objRoot = new BranchGroup();

	// Create influencing bounds
	worldBounds = new BoundingSphere(
						new Point3d( 0.0, 0.0, 0.0 ),  // Center
						1000.0 );                      // Extent

	Transform3D t = new Transform3D();
	// move the object upwards
	t.set(new Vector3f(0.0f, 0.1f, 0.0f));
	// Shrink the object 
	t.setScale(0.8);

	TransformGroup trans = new TransformGroup(t);
	trans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	trans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);


	otherApp = new Appearance();
	altMat = new Material();
	altMat.setCapability(Material.ALLOW_COMPONENT_WRITE);
	altMat.setDiffuseColor( new Color3f( 0.0f, 1.0f, 0.0f ) );
	otherApp.setMaterial(altMat);

	altApp = new AlternateAppearance();
	altApp.setAppearance(otherApp);
	altApp.setCapability(AlternateAppearance.ALLOW_SCOPE_WRITE);
	altApp.setCapability(AlternateAppearance.ALLOW_SCOPE_READ);
	altApp.setInfluencingBounds( worldBounds );
	objRoot.addChild(altApp);
	
	// Build foreground geometry into two groups.  We'll
	// create three directional lights below, one each with
	// scope to cover the first geometry group only, the
	// second geometry group only, or both geometry groups.
	Appearance app1 = new Appearance();
	mat1 = new Material();
	mat1.setCapability(Material.ALLOW_COMPONENT_WRITE);
	mat1.setDiffuseColor( new Color3f( 1.0f, 0.0f, 0.0f ) );
	app1.setMaterial(mat1);
	content1 = new SphereGroup(
				   0.05f,   // radius of spheres
				   0.4f,    // x spacing
				   0.2f,   // y spacing
				   3,       // number of spheres in X
				   5,       // number of spheres in Y
				   app1, // appearance
				   true);  // alt app override = true
	trans.addChild( content1 );
	shapes1 = ((SphereGroup)content1).getShapes();
	
	content2 = new SphereGroup(
				   0.05f,   // radius of spheres
				   .4f,    // x spacing
				   0.2f,   // y spacing
				   2,       // number of spheres in X
				   5,       // number of spheres in Y
				   app1,   // appearance
				   true); // alt app override = true
	trans.addChild( content2 );	
	shapes2 = ((SphereGroup)content2).getShapes();


	// Add lights
	DirectionalLight light1 = null;
	light1 = new DirectionalLight( );
	light1.setEnable( true );
	light1.setColor( new Color3f(0.2f, 0.2f, 0.2f) );
	light1.setDirection( new Vector3f( 1.0f, 0.0f, -1.0f ) );
	light1.setInfluencingBounds( worldBounds );
	objRoot.addChild( light1 );

 	DirectionalLight light2 = new DirectionalLight();
 	light2.setEnable(true);
 	light2.setColor(new Color3f(0.2f, 0.2f, 0.2f));
 	light2.setDirection(new Vector3f(-1.0f, 0.0f, 1.0f));
 	light2.setInfluencingBounds(worldBounds);
 	objRoot.addChild(light2);

	// Add an ambient light to dimly illuminate the rest of
	// the shapes in the scene to help illustrate that the
	// directional lights are being scoped... otherwise it looks
	// like we're just removing shapes from the scene
	AmbientLight ambient = new AmbientLight( );
	ambient.setEnable( true );
	ambient.setColor( new Color3f(1.0f, 1.0f, 1.0f) );
	ambient.setInfluencingBounds( worldBounds );
	objRoot.addChild( ambient );
		

	objRoot.addChild(trans);

	return objRoot;
    }
    JPanel createScopingPanel() {
	JPanel panel = new JPanel();
	panel.setBorder(new TitledBorder("Scopes"));

	String values[] = {"Scoped Set1", "Scoped Set2", "Universal Scope"};
	altAppScoping = new JComboBox(values);
	altAppScoping.addActionListener(this);
	altAppScoping.setSelectedIndex(2);
	panel.add(new JLabel("Scoping")); 	
	panel.add(altAppScoping);


	String enables[] = { "Enabled Set1", "Enabled Set2", "Enabled set1&2", "Disabled set1&2"};

	override = new JComboBox(enables);
	override.addActionListener(this);
	override.setSelectedIndex(3);
	panel.add(new JLabel("Alternate Appearance Override")); 	
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
	Object target = e.getSource();
	if (target == altAppMaterialColor) {
	    altMat.setDiffuseColor(colors[altAppMaterialColor.getSelectedIndex()]);
	}
	else if (target == altAppScoping) {
	    for (int i = 0; i < altApp.numScopes(); i++) {
		altApp.removeScope(0);
	    }
	    if (altAppScoping.getSelectedIndex() == 0) {
		altApp.addScope(content1);
	    }
	    else if (altAppScoping.getSelectedIndex() == 1) {
		altApp.addScope(content2);
	    }
	}
	else if (target == override) {
	    int i;
	    if (override.getSelectedIndex()== 0) {
		if (!shape1Enabled) {
		    for (i = 0; i < shapes1.length; i++)
			shapes1[i].setAppearanceOverrideEnable(true);
		    shape1Enabled = true;
		}

		if (shape2Enabled) {
		    for (i = 0; i < shapes2.length; i++)
			shapes2[i].setAppearanceOverrideEnable(false);
		    shape2Enabled = false;
		}
	    }
	    else if (override.getSelectedIndex() == 1) {
		if (!shape2Enabled) {
		    for (i = 0; i < shapes2.length; i++)
			shapes2[i].setAppearanceOverrideEnable(true);
		    shape2Enabled = true;
		}

		if (shape1Enabled) {
		    for (i = 0; i < shapes1.length; i++)
			shapes1[i].setAppearanceOverrideEnable(false);
		    shape1Enabled = false;
		}
	    }
	    else if (override.getSelectedIndex() == 2) {
		if (!shape1Enabled) {
		    for (i = 0; i < shapes1.length; i++)
			shapes1[i].setAppearanceOverrideEnable(true);
		    shape1Enabled = true;
		}
		if (!shape2Enabled) {
		    for (i = 0; i < shapes2.length; i++)
			shapes2[i].setAppearanceOverrideEnable(true);
		    shape2Enabled = true;
		}
	    }
	    else {
		if (shape1Enabled) {
		    for (i = 0; i < shapes1.length; i++)
			shapes1[i].setAppearanceOverrideEnable(false);
		    shape1Enabled = false;
		}
			
		if (shape2Enabled) {
		    for (i = 0; i < shapes2.length; i++)
			shapes2[i].setAppearanceOverrideEnable(false);
		    shape2Enabled = false;
		}
	    }
	    
	}
	else if (target == appMaterialColor) {
	    mat1.setDiffuseColor(colors[appMaterialColor.getSelectedIndex()]);
	}

    }
			   
			   
    public static void main(String[] args) {
	Frame frame = new MainFrame(new AlternateAppearanceScopeTest(), 800, 800);
    }

}			   
