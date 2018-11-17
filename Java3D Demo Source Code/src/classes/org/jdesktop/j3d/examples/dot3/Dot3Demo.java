/*
 * $RCSfile: Dot3Demo.java,v $
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

package org.jdesktop.j3d.examples.dot3;

import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.image.BufferedImage;

import java.util.Enumeration;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.Behavior;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TexCoordGeneration;
import javax.media.j3d.Texture;
import javax.media.j3d.Texture2D;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.TextureUnitState;
import javax.media.j3d.WakeupOnElapsedFrames;

import javax.swing.JFrame;
import javax.swing.JPanel;

import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;
import org.jdesktop.j3d.examples.Resources;

/**
 * This example program is contributed by Alessandro Borges
 */

/**
 * <pre>
 *  DOT3 per-pixel lighting demo.
 * It uses a Normal map and a Light map, both coded as independent textures.
 * Each pixel color is a vector coded, where color range [0,255] is mapped
 * as vector in range [-1.0,+1.0].
 *
 * A math operation called DOT3 applied to Light vector and Normal vector results
 * a scalar value, interpreted as light intensity. This operation is made for each
 * pixel on texture.  
 * Light Intensity = DOT3(light, normal);
 *
 * This technique allows complex lighting effects, as bumps, on low polygon count
 * geometries.
 * </pre>
 *
 */

public class Dot3Demo extends JFrame {
    // a external control panel  for this demo
    private TextureControlPanel ctrlPanel = null;
    // default bounds used in this application
    private BoundingSphere bounds =  new BoundingSphere(new Point3d(0.0, 0.0, 0.0),
            100.0);
    // TextureUnitStates used in this application
    TextureUnitState tuLightMap;
    TextureUnitState tuDOT3NormalMap;
    TextureUnitState tuColor;
    
    /** Where the TUs are applied **/
    TextureUnitState[] tusArr;
    /** appearance will be changed at runtime **/
    Appearance appearance;
    /** polygonAttributes will be changed at runtime **/
    PolygonAttributes polygonAttributes;
    
    // textures used
    Texture textureColor;
    Texture textureDOT3NormalMap;
    Texture2D textureLightMap;
    // needs for runtime updates on lightMap
    ImageComponent2D imageLightMap;
    
    // default texture names used
    String  textureColorName= "resources/images/wood.jpg";
    String textureDOT3NormalMapName = "resources/images/Java3Ddot3.jpg";
    
    /**
     * Constructor.
     */
    public Dot3Demo() {
        super("Java3D DOT3 demo");
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }    
    
    private void init() throws Exception {
        this.setSize(new Dimension(400, 400));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel mainPanel = new JPanel();
        this.getContentPane().add(mainPanel, null);
        mainPanel.setLayout(new BorderLayout());
        // get default configuration for 3D
        GraphicsConfiguration conf =   SimpleUniverse.getPreferredConfiguration();
        Canvas3D canvas = new Canvas3D(conf);
        // create simpleUniverse
        SimpleUniverse su = new SimpleUniverse(canvas);
        // create sceneGraph and add it to universe
        BranchGroup sceneGraph = createSceneGraph();
        su.addBranchGraph(sceneGraph);
        
        // This will move the ViewPlatform back a bit so the
        // objects in the scene can be viewed.
        su.getViewingPlatform().setNominalViewingTransform();
        
        // Ensure at least 5 msec per frame (i.e., < 200Hz)
        su.getViewer().getView().setMinimumFrameCycleTime(5);
        
        // add the behaviors to the ViewingPlatform
        ViewingPlatform viewingPlatform = su.getViewingPlatform();
        viewingPlatform.setNominalViewingTransform();
        
        // add orbit behavior to ViewingPlatform
        OrbitBehavior   orbit = new OrbitBehavior(canvas, OrbitBehavior.REVERSE_ALL |
                OrbitBehavior.STOP_ZOOM);
        orbit.setSchedulingBounds(bounds);
        viewingPlatform.setViewPlatformBehavior(orbit);
        
        mainPanel.add(canvas, BorderLayout.CENTER);
        this.setVisible(true);
        //create a control panel to user interaction
        ctrlPanel = new TextureControlPanel(this);
        ctrlPanel.setVisible(true);
        ctrlPanel.setLocation(410,10);
    }
    
    /**
     * loads all needed textures, and creates light map texture
     */
    private void loadTextures() {
        try {
            //java.net.URL urlColor = new java.net.URL("file:" + textureColorName);
            //java.net.URL urlDot3  = new java.net.URL("file:" + textureDOT3NormalMapName);
            java.net.URL urlColor = Resources.getResource(textureColorName);
            java.net.URL urlDot3  = Resources.getResource(textureDOT3NormalMapName);
            
            // loading textures
            textureColor = new TextureLoader(urlColor,this).getTexture();
            textureDOT3NormalMap = new TextureLoader(urlDot3,this) .getTexture();
            
            // create Image for textureLightMap
            BufferedImage image = new BufferedImage(256,256,BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = image.createGraphics();
            graphics.setPaint(new Color(130,130,250));
            graphics.fillRect(0,0,image.getWidth(),image.getHeight());
            graphics.dispose();
            
            imageLightMap = new ImageComponent2D(ImageComponent2D.FORMAT_RGB,image,false,false);
            imageLightMap.setCapability(ImageComponent2D.ALLOW_IMAGE_WRITE);
            imageLightMap.setCapability(ImageComponent2D.ALLOW_IMAGE_READ);
            
            //create textureLightMap with above imageLightMap
            textureLightMap = new Texture2D(Texture2D.BASE_LEVEL,Texture2D.RGB,256,256);
            textureLightMap.setImage(0,imageLightMap);
            textureLightMap.setMagFilter(Texture2D.NICEST);
            textureLightMap.setMinFilter(Texture2D.NICEST);
            
            // application with update textureLightMap at runtime, so lets enable some caps
            textureLightMap.setCapability(Texture2D.ALLOW_ENABLE_WRITE);
            textureLightMap.setCapability(Texture2D.ALLOW_ENABLE_READ);
            textureLightMap.setCapability(Texture2D.ALLOW_IMAGE_WRITE);
            textureLightMap.setCapability(Texture2D.ALLOW_IMAGE_READ);
            
        } catch(Exception e) {
            System.err.println("Failed to load textures");
            e.printStackTrace();
        }
    }
    
    /**
     * setup TextureUnitStates used in this demo.     *
     * @return
     */
    private TextureUnitState[] setupTextureUnitState() {
        //texture Attributes for DOT3 normal map
        TextureAttributes textAttDot3 = new TextureAttributes();
        
        
        // lightMap uses  TextureAttributes with default REPLACE mode
        TextureAttributes textAttLightMap = new TextureAttributes();
        
        TextureAttributes texAttColor = new TextureAttributes();
        texAttColor.setTextureMode(TextureAttributes.COMBINE);
        
        //CombineRgbMode could be also COMBINE_ADD or COMBINE_ADD_SIGNED, with
        //different results
        texAttColor.setCombineRgbMode(TextureAttributes.COMBINE_MODULATE);
        // increase light depth effect
        texAttColor.setCombineRgbScale(2);
        
        textAttDot3.setTextureMode(TextureAttributes.COMBINE);
        textAttDot3.setCombineRgbMode(TextureAttributes.COMBINE_DOT3);
        textAttDot3.setCombineAlphaMode(TextureAttributes.COMBINE_DOT3);
        textAttDot3.setTextureBlendColor(1.f,1.0f,1.0f,0.0f);
        //  increase light intesity
        textAttDot3.setCombineRgbScale(2);
        // setup functions
        textAttDot3.setCombineRgbFunction(0,TextureAttributes.COMBINE_SRC_COLOR);
        textAttDot3.setCombineRgbFunction(1,TextureAttributes.COMBINE_SRC_COLOR);
        textAttDot3.setCombineRgbFunction(2,TextureAttributes.COMBINE_SRC_COLOR);
        //combine with previous TUS, lightMap
        textAttDot3.setCombineRgbSource(0,TextureAttributes.COMBINE_PREVIOUS_TEXTURE_UNIT_STATE);
        textAttDot3.setCombineRgbSource(1,TextureAttributes.COMBINE_TEXTURE_COLOR );
        textAttDot3.setCombineRgbSource(2,TextureAttributes.COMBINE_OBJECT_COLOR);
        
        TexCoordGeneration tcg1=null;
        // SphereMap tcg can add nice dynamic effects for curved surfaces, because it
        // distributes texture like a light bean over geometry.
        // It os not used in this demo, but you can try yourself at home
        // with *complex* lightmaps, i.e., spherical light distributions,
        // multi light sorces, degradee, waves,etc
         /*
            tcg1 = new TexCoordGeneration(TexCoordGeneration.SPHERE_MAP,
                                          TexCoordGeneration.TEXTURE_COORDINATE_3);
          */
        
        // create TUS
        tuLightMap = new TextureUnitState(textureLightMap,textAttLightMap,tcg1);
        tuDOT3NormalMap     = new TextureUnitState(textureDOT3NormalMap,textAttDot3,null);
        tuColor    = new TextureUnitState(textureColor,texAttColor,null);
        
        // this TUS array is used by geometry at runtime
        TextureUnitState[] tus = new TextureUnitState[3];
        tus[0] = tuLightMap;
        tus[1] = tuDOT3NormalMap;
        tus[2] = tuColor;
        // enable texture units for read/write at runtime
        for (int i = 0; i < tus.length; i++) {
            tus[i].setCapability(TextureUnitState.ALLOW_STATE_WRITE);
            tus[i].setCapability(TextureUnitState.ALLOW_STATE_READ);
        }
        
        return tus;
    }
    
    /**
     * creates a single Quad geometry with 4 TextureCoordinateMaps, for multitexture use.<br>
     * Dimension is scale*(2m , 1m)
     * @param scale a scale for this quad
     * @return quad geometry for multitexture use
     */
    private GeometryArray createGeometry(float scale) {
        // vertex coordinates
        float[] verts = {  2.0f, -1.0f,  0.0f,
        2.0f,  1.0f,  0.0f,
        -2.0f,  1.0f,  0.0f,
        -2.0f, -1.0f,  0.0f };
        // 2D texture Coords - each texture unit will use one set of this
        float[] texCoords = { 1.0f, 0.0f,
        1.0f, 1.0f,
        0.0f, 1.0f,
        0.0f, 0.0f};
        // all texture units will use texCoords from unit 0
        int[] texCoordSetMap = {0,0,0,0};
        // normals
        Vector3f normal = new Vector3f( 0.0f,  0.0f,  1.0f);
        Vector3f[] normals = { normal,  normal,  normal,  normal} ;
        // resize quad dimension
        for(int i = 0;i<verts.length;i++) {
            verts[i] *= scale;
        }
        // create geometry  using GeometryInfo
        GeometryInfo gi = new GeometryInfo(GeometryInfo.QUAD_ARRAY);
        
        gi.setCoordinates(verts);
        gi.setNormals(normals);
        // preparing for multitexture
        // To get up to 4 TUS, it needs 4 sets of 2D texture
        gi.setTextureCoordinateParams(4, 2);
        gi.setTexCoordSetMap(texCoordSetMap);
        
        // this demo needs just 3 TUS, but geometry
        // is prepared for up to 4 TUS stages
        gi.setTextureCoordinates(0,texCoords);
        gi.setTextureCoordinates(1,texCoords);
        gi.setTextureCoordinates(2,texCoords);
        gi.setTextureCoordinates(3,texCoords);
        
        return gi.getGeometryArray();
    }
    
    /**
     * Creates scenegraphs
     * @return a BranchGroup with all needed objects in scene
     */
    private BranchGroup createSceneGraph() {
        BranchGroup bgRoot = new BranchGroup();
        CheckNewLightMapBehavior checkNewLightMapBehavior = new CheckNewLightMapBehavior();
        
        bgRoot.addChild(checkNewLightMapBehavior);
        
        // a blue background
        Background background = new Background(0.4f,0.4f,0.8f);
        background.setApplicationBounds(bounds);
        bgRoot.addChild(background);
        
        AmbientLight alit = new AmbientLight(true,new Color3f(0.4f,0.4f,0.4f));
        bgRoot.addChild(alit);
        
        // Set up some directional lights
        // DOT3 doesnot need light, because it is a perpixel lighting technique
        //but we add this lights to show
        // geometry when using non-DOT3 lighting, as color texture only and
        // light map texture mode
        Color3f light1Color = new Color3f(1.0f, 1.0f, 0.9f);
        Vector3f light1Direction  = new Vector3f(1.0f, 1.0f, 1.0f);
        Color3f light2Color = new Color3f(1.0f, 1.0f, 0.9f);
        Vector3f light2Direction  = new Vector3f(-1.0f, -1.0f, -1.0f);
        
        DirectionalLight light1
                = new DirectionalLight(light1Color, light1Direction);
        light1.setInfluencingBounds(bounds);
        bgRoot.addChild(light1);
        
        DirectionalLight light2
                = new DirectionalLight(light2Color, light2Direction);
        light2.setInfluencingBounds(bounds);
        bgRoot.addChild(light2);
        
        //loading color and DOT3 normal map textures from disk,
        //and creating light map at runtime
        loadTextures();
        //our single Quad geometry, enabled for multitexture
        GeometryArray geo = createGeometry(0.4f);
        // a appearance for our geometry
        appearance = new Appearance();
        // polygon and texture unit will be updated at runtime
        // so we must enabled read/write operations for then
        appearance.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_READ);
        appearance.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_WRITE);
        appearance.setCapability(Appearance.ALLOW_TEXTURE_UNIT_STATE_READ);
        appearance.setCapability(Appearance.ALLOW_TEXTURE_UNIT_STATE_WRITE);
        
        //use a default material. It is necessary when running
        //on non per-pixel lighting mod, i.e., using non DOT3 textures
        appearance.setMaterial(new Material());
        
        polygonAttributes = new PolygonAttributes();
        polygonAttributes.setCapability(PolygonAttributes.ALLOW_MODE_WRITE);
        polygonAttributes.setCullFace(PolygonAttributes.CULL_NONE);
        
        appearance.setPolygonAttributes(polygonAttributes);
        
        // uses a TUS dot3 enabled
        tusArr = setupTextureUnitState();
        appearance.setTextureUnitState(tusArr);
        
        // joining geometry and appearance in a shape3D
        Shape3D shape3D = new Shape3D(geo,appearance);
        shape3D.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
        shape3D.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
        
        bgRoot.addChild(shape3D);
        
        bgRoot.compile();
        
        return bgRoot;
    }
    
    /**
     * Toggles wireframe mode
     * @param mode true for  wireframe, false for fill polygon
     */
    public void setWireframeMode(boolean mode) {
        if(mode)
            polygonAttributes.setPolygonMode(PolygonAttributes.POLYGON_LINE);
        else
            polygonAttributes.setPolygonMode(PolygonAttributes.POLYGON_FILL);
    }
    
    
    /**
     * This method togles on/off textures and updates TextureUnitState in correct Order.
     * Some video drivers does not accept TextureUnitState arrays with null values among
     * non-null values
     * @param showLightMap togles LightMap texture
     * @param showDot3 togles DOT3 Normal texture
     * @param showColor togles Color texture
     */
    public void showTextures(boolean showLightMap, boolean showDot3, boolean showColor) {
        int bitSet = 0;
        bitSet |= showLightMap ? 4 : 0;
        bitSet |= showDot3     ? 2 : 0;
        bitSet |= showColor    ? 1 : 0;
        
        tusArr[0] = null;
        tusArr[1] = null;
        tusArr[2] = null;
        
        switch (bitSet)  {
            case 7: { //all bit == all tus
                tusArr[0] = tuLightMap;
                tusArr[1] = tuDOT3NormalMap;
                tusArr[2] = tuColor;
            }
            break;
            case 6: { //no Color
                tusArr[0] = tuLightMap;
                tusArr[1] = tuDOT3NormalMap;
            }
            break;
            case 5: { //no Dot3
                tusArr[0] = tuLightMap;
                tusArr[1] = tuColor;
            }
            break;
            case 4: { //lightMap only
                tusArr[0] = tuLightMap;
            }
            break;
            case 3: { //no LightMap
                tusArr[0] = tuDOT3NormalMap;
                tusArr[1] = tuColor;
            }
            break;
            case 2: { //Dot3 Only
                tusArr[0] = tuDOT3NormalMap;
            }
            break;
            case 1: { // Color Only
                tusArr[0] = tuColor;
            }
            break;
            default: { // case 0, no textures shows at all
            }
            break;
        }
        appearance.setTextureUnitState(tusArr);
    }
    
    /**
     * updates LightMap texture.
     * This method is called from checkNewLightMapBehavior
     * @param image new image to be applied
     */
    public void updateLighMap(BufferedImage image) {
        imageLightMap.setSubImage(image,image.getWidth(),image.getHeight(),0,0,0,0);
    }
    
    private BufferedImage tempImage;
    private boolean lockTempImage = false;
    
    
    /**
     * main method
     * @param args
     */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Dot3Demo();
            }
        });
        
    }
    
    /**
     * A internal class to check if there is a new Light Map to be applied
     */
    class CheckNewLightMapBehavior extends Behavior {
        WakeupOnElapsedFrames wakeup = new WakeupOnElapsedFrames(0);
        
        public CheckNewLightMapBehavior() {// auto enable and set schedulling bounds
            setEnable(true);
            setSchedulingBounds(bounds);
        }
        
        public void initialize() {
            wakeupOn(wakeup);
        }
        
        public void processStimulus(Enumeration e) {
            // check if there are a new light map ready to use
            if (ctrlPanel.hasTextureImageReady()) {
                updateLighMap(ctrlPanel.getTextureImage());
            }
            //wake up on next frame
            wakeupOn(wakeup);
        }
    }
    
}


