/*
 * $RCSfile: ImageComponentByReferenceTest.java,v $
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
 * $Date: 2007/02/09 17:21:39 $
 * $State: Exp $
 */

package org.jdesktop.j3d.examples.geometry_by_ref;

import java.awt.*;
import java.awt.event.*;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import javax.swing.*;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.geometry.Box;
import java.awt.image.*;
import org.jdesktop.j3d.examples.Resources;

public class ImageComponentByReferenceTest extends JApplet implements ActionListener {

    Shape3D s1,s2;
    TextureLoader t0, t1, t2;
    int count = 0;

    Appearance app = new Appearance();
    BranchGroup objRoot = new BranchGroup();
    TransformGroup objTrans = new TransformGroup();
    BufferedImage bImage1;
    TiledImage checkBoard;
    boolean yUp = false;
    boolean byRef = true;
    JComboBox rasterType, texType;
    ImageComponent2D[] image = new ImageComponent2D[8];
    Appearance dummyApp = new Appearance();
    Texture2D texOne, texCheckBoard;
    javax.media.j3d.Raster raster;
    Box textureCube;
    Shape3D boxShape;
    int w1 = 64, h1 = 32, checkw = 16 , checkh = 16;

    private java.net.URL texImage = null;

    private SimpleUniverse u = null;
    
    public BranchGroup createSceneGraph() {
        objRoot = new BranchGroup();
 
        objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objTrans.setCapability(Group.ALLOW_CHILDREN_WRITE);

        objRoot.addChild(objTrans);
 
        BoundingSphere bounds =
            new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);

	app.setCapability(Appearance.ALLOW_TEXTURE_WRITE);


        textureCube = new Box(0.4f, 0.4f, 0.4f,
                                  Box.GENERATE_TEXTURE_COORDS|
				  Box.GENERATE_NORMALS, app);
	boxShape = textureCube.getShape(Box.FRONT);
	boxShape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
	objTrans.addChild(textureCube);

	checkBoard = new TiledImage();
	TextureLoader texLoader = new TextureLoader( texImage, this);
	ImageComponent2D oneImage = texLoader.getImage();
	bImage1 = oneImage.getImage();

	int index = 0;
	image[index++] =  new ImageComponent2D(oneImage.getFormat(),
				      (RenderedImage)bImage1,
				      false,
				      true);

	image[index++] =  new ImageComponent2D(oneImage.getFormat(),
				      (RenderedImage)bImage1,
				      true,
				      true);


	image[index++] =  new ImageComponent2D(oneImage.getFormat(),
				      (RenderedImage)bImage1,
				      false,
				      false);
	
	
	image[index++] =  new ImageComponent2D(oneImage.getFormat(),
				      (RenderedImage)bImage1,
				      true,
				      false);

	createRaster(objRoot);

	image[index++] =  new ImageComponent2D(ImageComponent.FORMAT_RGBA,
				      checkBoard,
				      false,
				      true);

	image[index++] =  new ImageComponent2D(ImageComponent.FORMAT_RGBA,
				      checkBoard,
				      true,
				      true);


	image[index++] =  new ImageComponent2D(ImageComponent.FORMAT_RGBA,
				      checkBoard,
				      false,
				      false);
	
	
	image[index++] =  new ImageComponent2D(ImageComponent.FORMAT_RGBA,
				      checkBoard,
				      true,
				      false);



	texOne = new Texture2D(Texture.BASE_LEVEL,
				      Texture.RGBA,
				      image[2].getWidth(), image[2].getHeight());

	texOne.setCapability(Texture.ALLOW_IMAGE_WRITE);
	texOne.setImage(0, image[2]);
	
        app.setTexture(texOne);

	texCheckBoard = new Texture2D(Texture.BASE_LEVEL,
				      Texture.RGBA,
				      image[4].getWidth(), image[4].getHeight());
	
	texCheckBoard.setCapability(Texture.ALLOW_IMAGE_WRITE);
        objRoot.compile();
	return objRoot;
    }

    public void actionPerformed(ActionEvent e ) {
	Object target = e.getSource();

	if (target == rasterType) {
	    if (rasterType.getSelectedIndex() < 4) {
		raster.setSize(w1, h1);
	    }
	    else {
		raster.setSize(checkw, checkh);
	    }
	    raster.setImage(image[rasterType.getSelectedIndex()]);
	}
	else if (target == texType) {
	    boxShape.setAppearance(dummyApp);
	    if (texType.getSelectedIndex() < 4) {
		texOne.setImage(0, image[texType.getSelectedIndex()]);
		app.setTexture(texOne);
	    }
	    else {
		texCheckBoard.setImage(0, image[texType.getSelectedIndex()]);
		app.setTexture(texCheckBoard);
	    }
		
	    boxShape.setAppearance(app);
	}


    }

    JPanel createImagePanel() {
	JPanel panel = new JPanel();
	String texVals[] = { "One_Yup_ByCopy",
			     "One_Yup_ByReference",
			     "One_Ydown_ByCopy",
			     "One_Ydown_ByReference",
			     "Checkered_Yup_ByCopy",
			     "Checkered_Yup_ByReference",
			     "Checkered_Ydown_ByCopy",
			     "Checkered_Ydown_ByReference"};
	
	rasterType = new JComboBox(texVals);
	rasterType.setLightWeightPopupEnabled(false);
	rasterType.addActionListener(this);
	rasterType.setSelectedIndex(2);
	panel.add(new JLabel("Raster Image")); 	
	panel.add(rasterType);

	texType = new JComboBox(texVals);
	texType.setLightWeightPopupEnabled(false);
	texType.addActionListener(this);
	texType.setSelectedIndex(2);
	panel.add(new JLabel("Texture Image")); 	
	panel.add(texType);
	return panel;

    }



    public ImageComponentByReferenceTest() 
    {
    }

    public ImageComponentByReferenceTest(java.net.URL url) {
        texImage = url;
    }

    public void init() {
        
        texImage = Resources.getResource("resources/images/one.jpg");
        if (texImage == null) {
            System.err.println("resources/images/one.jpg not found");
            System.exit(1);
        }
        
        Canvas3D c = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        BranchGroup scene = createSceneGraph();u = new SimpleUniverse(c);
        u.getViewingPlatform().setNominalViewingTransform();
        u.addBranchGraph(scene);
        Container contentPane = getContentPane();
        JPanel p = new JPanel();
        BoxLayout boxlayout = new BoxLayout(p,
                BoxLayout.Y_AXIS);
        p.setLayout(boxlayout);
        contentPane.add("Center", c);
        
        contentPane.add("South", p);
        
        p.add(createImagePanel());
        
    }

    public void destroy() {
	u.cleanup();
    }
 
    public static void main(String[] args) {
        java.net.URL url = null;
        // the path to the image file for an application
        url = Resources.getResource("resources/images/one.jpg");
        if (url == null) {
            System.err.println("resources/images/one.jpg not found");
            System.exit(1);
        }
        
        new MainFrame(new ImageComponentByReferenceTest(url), 800, 700);
    }

   void createRaster( BranchGroup scene) {
	// Create raster geometries and shapes
	Vector3f trans = new Vector3f( );
	Transform3D tr = new Transform3D( );
	TransformGroup tg;

	// Left
	raster = new javax.media.j3d.Raster( );
	raster.setCapability(javax.media.j3d.Raster.ALLOW_IMAGE_WRITE);
	raster.setCapability(javax.media.j3d.Raster.ALLOW_SIZE_WRITE);
	raster.setPosition( new Point3f( -0.9f, 0.75f, 0.0f ) );
	raster.setType( javax.media.j3d.Raster.RASTER_COLOR );
	raster.setOffset( 0, 0 );

	raster.setSize( image[2].getWidth(), image[2].getHeight() );
	raster.setImage( image[2] );
	Shape3D sh = new Shape3D( raster, new Appearance( ) );
	scene.addChild( sh );
    }
}




