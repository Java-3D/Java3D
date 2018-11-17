/*
 * $RCSfile: TextureControlPanel.java,v $
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
 * $Date: 2007/02/09 17:21:36 $
 * $State: Exp $
 */

package org.jdesktop.j3d.examples.dot3;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import java.net.URL;

import javax.imageio.ImageIO;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jdesktop.j3d.examples.Resources;

/**
 * A control panel for Dot3Demo.
 * It enables user change LightMap, enables/disables textures units states 
 * and toggles geometry wireframes on/off
 */
public class TextureControlPanel extends JDialog implements ChangeListener,
        ActionListener {
    /** renderer for lightMap, with support for mouse interaction **/
    private MyCanvas canvas = null;
    /** file name for light mask */
    private String maskFileName = "resources/images/mask.png";
    /** a slider to change Z light direction, i.e, blue channel */
    private JSlider sliderZ = new JSlider(JSlider.HORIZONTAL, 1, 255, 142);
    /** target demo instance to be controled **/
    private Dot3Demo dot3DemoFrame;
    
    // some checkboxes for user interaction
    private JCheckBox cbWireframe = new JCheckBox("Show as Wireframe", false);
    private JCheckBox cbDot3 = new JCheckBox("Show Dot3 texture", true);
    private JCheckBox cbShowLightMap =  new JCheckBox("Show LightMap texture", true);
    private JCheckBox cbShowColor = new JCheckBox("Show Color texture", true);
    private JCheckBox cbDragLightMask = new JCheckBox("Drag light mask");
    
    private JLabel lbSliderZ = new JLabel();
    private JLabel lbMessage = new JLabel();
    
    public TextureControlPanel(Dot3Demo owner) {
        super(owner);
        dot3DemoFrame = owner;
        try {
            URL url = Resources.getResource(maskFileName);
            BufferedImage mask = ImageIO.read(url);
            canvas = new MyCanvas(mask);
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public TextureControlPanel() {
        this(null);
    }
    
    /**
     * Creates Graphical User Interface
     * @throws Exception
     */
    private void init() throws Exception {
        Dimension dim = new Dimension(540, 350);
        this.setSize(dim);
        this.setPreferredSize(dim);
        this.setTitle("DOT3Demo Texture Control Panel");
        this.setLayout(new BorderLayout());
        
        JPanel panel = new JPanel();
        this.getContentPane().add(panel, BorderLayout.CENTER);
        canvas.setSize(new Dimension(256, 256));
        canvas.setBounds(new Rectangle(40, 40, 256, 256));
        
        sliderZ.setBounds(new Rectangle(310, 190, 205, 45));
        sliderZ.setPaintTicks(true);
        sliderZ.setMajorTickSpacing(63);
        
        cbWireframe.setBounds(new Rectangle(310, 50, 200, 20));
        cbWireframe.setToolTipText("Toggles Wireframe");
        cbDot3.setBounds(new Rectangle(310, 70, 150, 20));
        cbShowLightMap.setBounds(new Rectangle(310, 90, 200, 20));
        cbShowLightMap.setToolTipText("Toggles DOT3 texture");
        cbShowColor.setBounds(new Rectangle(310, 110, 200, 20));
        cbShowColor.setToolTipText("Toggles Color texture");
        
        panel.setLayout(null);
        
        cbDragLightMask.setBounds(new Rectangle(310, 135, 200, 20));
        lbMessage.setText("<html>Left-click and drag to change Light Direction." +
                " Right-click and drag to move spotlight.</html>");
        lbMessage.setBounds(new Rectangle(305, 245, 210, 60));
        
        lbSliderZ.setText("Blue Light (Dot3 Z axis)");
        lbSliderZ.setBounds(new Rectangle(310, 170, 210, 15));
        lbSliderZ.setToolTipText("changes light intensity from Z axis");
        
        panel.add(cbDragLightMask, null);
        panel.add(lbMessage, null);
        panel.add(lbSliderZ, null);
        panel.add(sliderZ, null);
        panel.add(canvas, null);
        panel.add(cbShowColor, null);
        panel.add(cbShowLightMap, null);
        panel.add(cbWireframe, null);
        panel.add(cbDot3, null);
        
        sliderZ.addChangeListener(this);
        
        cbDot3.addActionListener(this);
        cbShowColor.addActionListener(this);
        cbShowLightMap.addActionListener(this);
        cbWireframe.addActionListener(this);
        cbDragLightMask.addActionListener(this);
    }
    
    public void stateChanged(ChangeEvent ev) {
        JComponent source = (JComponent)ev.getSource();
        if (sliderZ.equals(source)) {
            int xVal = canvas.getBgColor().getRed();
            int yVal = canvas.getBgColor().getGreen();
            int zVal = sliderZ.getValue();
            Color ligtDir = new Color(xVal, yVal, zVal);
            updateLightMap(ligtDir) ;
        }
    }
    
    private void updateLightMap(Color ligtDir) {
        canvas.setBgColor(ligtDir);
        canvas.repaint();
        dot3DemoFrame.updateLighMap(canvas.getTextureImage());
    }
    
    
    public void actionPerformed(ActionEvent ev) {
        JComponent source = (JComponent)ev.getSource();
        if (cbWireframe.equals(source)) {
            dot3DemoFrame.setWireframeMode(cbWireframe.isSelected());
        } else
            if (cbDot3.equals(source)
            || cbShowColor.equals(source)
            || cbShowLightMap.equals(source)) {
            dot3DemoFrame.showTextures(cbShowLightMap.isSelected(),
                    cbDot3.isSelected(),
                    cbShowColor.isSelected());
            } else if (cbDragLightMask.equals(source)) {
            canvas.dragMask = cbDragLightMask.isSelected();
            }
        
    }
    
    /**
     *  Wrapper method call for MyCanvas. hasTextureImageReady()
     * @return true if exists a new texture image available
     */
    public boolean hasTextureImageReady() {
        return canvas.hasTextureImageReady();
    }
    
    /**
     * Wrapper method call for MyCanvas.getTextureImage()
     * Returns a texture image.<br>
     * Avoid calling the same image several times
     * by cheking  hasTextureImageReady() first.
     * @return latest texture image available
     */
    public BufferedImage getTextureImage() {
        return canvas.getTextureImage();
    }
    
    /**
     * Wrapper method to MyCanvas.setLightMask(mask)
     * @param mask a new light mask
     */
    public void setLightMask(BufferedImage mask) {
        canvas.setLightMask(mask);
    }
}


