/*
 * $RCSfile: MyCanvas.java,v $
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

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.MouseInputListener;

/**
 * A mouse interactive canvas for lightMap image
 */
public class MyCanvas extends JPanel implements MouseInputListener {
    BufferedImage lightMask = null;
    BufferedImage textureImage = null;
    Graphics2D gr = null;
    Point location = new Point();
    // default color light map
    Color bgColor = new Color(147, 147, 147);
    
    int x = 0;
    int y = 0;
    int z = 142;
    // texture image size
    private static final int textureSize = 256;
    boolean mouseOut = true;
    //flag about image is ready or not for use
    boolean imageReady = false;
    // allows mask be dragged with mouse
    boolean dragMask = false;
    boolean updateLightDir = false;
    boolean updateMaskPosition = false;
    
    /**
     * Creates a MyCanvas object with a image lightMask.
     * Also creates a default ImageLight map
     * @param mask light lightMask used
     */
    public MyCanvas(BufferedImage mask) {
        super();
        this.lightMask = mask;
        // create a light map
        setTextureImage(new BufferedImage(textureSize, textureSize,
                BufferedImage.TYPE_INT_RGB));
        // Graphics used to update lightmap
        gr = getTextureImage().createGraphics();
        
        Dimension dimSize = new Dimension(textureSize, textureSize);
        // lock size
        this.setSize(dimSize);
        this.setMaximumSize(dimSize);
        this.setMinimumSize(dimSize);
        
        this.setDoubleBuffered(true);
        this.setOpaque(true);
        this.addMouseMotionListener(this);
        this.addMouseListener(this);
    }
    
    /**
     * Handles mouse click event.
     * Get mouse coords call repaint for proper imageLight update
     * @param ev mouse event
     */
    public void mouseClicked(MouseEvent ev) {
        x = ev.getX();
        y = this.getHeight() -  ev.getY();
        updateLightDir = true;
        repaint();
    }
    
    public void mousePressed(MouseEvent e) {
    }
    
    public void mouseReleased(MouseEvent e) {
    }
    
    /**
     * Handles mouse drag event.
     * get current mouse position and calls repaint for proper imageLight update
     * @param ev
     */
    public void mouseDragged(MouseEvent ev) {
        if (!mouseOut) {
            x = ev.getX();
            y = this.getHeight() - ev.getY();
            
            //changes lightDir
            if ((ev.getModifiers()& MouseEvent.BUTTON1_MASK) ==
                    MouseEvent.BUTTON1_MASK) {
                updateLightDir = true;
                updateMaskPosition = false;
            }
            //updates light mask position
            if ((ev.getModifiers() & ev.BUTTON2_MASK) == ev.BUTTON2_MASK ||
                    (ev.getModifiers() & ev.BUTTON3_MASK) == ev.BUTTON3_MASK) {
                updateLightDir = false;
                updateMaskPosition = true;
            }
            repaint();
        }
    }
    
    public void mouseMoved(MouseEvent ev) {
        // disable updates on lightMap
        updateLightDir = false;
        updateMaskPosition = false;
    }
    
    public void mouseEntered(MouseEvent e) {
        mouseOut = false;
    }
    
    public void mouseExited(MouseEvent e) {
        mouseOut = true;
    }
    
    /**
     * updates imageLight using current setings
     * @param g
     */
    public void paintComponent(Graphics g) {
        imageReady = false;
        Graphics2D g2d = (Graphics2D)g;
        
        // ligthDir has changed, we must update bgColor li
        if(updateLightDir) {
            int blue = bgColor.getBlue();
            //clamp values to 255
            y = y>255?255:y;
            x = x>255?255:x;
            bgColor = new Color(y,x,blue);
        }
        // paint lightMap
        gr.setColor(bgColor);
        gr.fillRect(0, 0, textureSize, textureSize);
        
        // draw mask on mouse position
        if (dragMask || updateMaskPosition) {
            int maskWH = lightMask.getWidth()/2;
            int mx = x - maskWH ;
            int my = textureSize - y - maskWH ; // y value is inverted
            // clamp mouse position, to avoid drawing outside imageLigh bounds
            mx = mx > textureSize ? textureSize : mx;
            my = my > textureSize ? textureSize : my;
            // draw light mask
            gr.drawImage(lightMask, mx, my, this);
        }
        
        g2d.drawImage(getTextureImage(), 0, 0, this);
        imageReady = true;
    }
    
    /**
     *
     * @return true if exists a new texture image available
     */
    public boolean hasTextureImageReady() {
        return imageReady;
    }
    
    /**
     * Returns a texture image.<br>
     * You can avoid calling the same image several times
     *  by checking  hasTextureImageReady() first.
     * @return latest texture image available
     */
    public BufferedImage getTextureImage() {
        // sign as texture used for next call;
        imageReady = false;
        //return image
        return textureImage;
    }
    
    
    public Image getMask() {
        return lightMask;
    }
    
    public void setLightMask(BufferedImage mask) {
        this.lightMask = mask;
    }
    
    public Color getBgColor() {
        return bgColor;
    }
    
    public void setBgColor(Color bgColor) {
        this.bgColor = bgColor;
    }
    
    public void setTextureImage(BufferedImage textureImage) {
        this.textureImage = textureImage;
    }
    
    
}

