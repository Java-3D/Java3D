/*
 * $RCSfile: Canvas2D.java,v $
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
 * $Date: 2007/02/09 17:21:37 $
 * $State: Exp $
 */

package org.jdesktop.j3d.examples.four_by_four;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

/**
 * Class:       Canvas2D
 *
 * Description: Used to respond to mouse events in the 2D window.
 *
 * Version:     1.0
 *
 */
class Canvas2D extends Canvas implements MouseListener {

   Image backbuffer;          // Backbuffer image
   Graphics gc;               // Graphics context of backbuffer
   Board board;               // Game board

   Canvas2D(Board board) {
      this.board = board;
   }

   public void setBuffer(Image backbuffer) {
      this.backbuffer = backbuffer;
      gc = backbuffer.getGraphics();
   }

   public void update(Graphics g) {
      paint(g);
   }

   public void paint(Graphics g) {
      if (board != null) {
         board.render2D(gc);
         g.drawImage(backbuffer, 0, 0, this);
      }
   }

   public void mousePressed(MouseEvent e) {
      board.checkSelection2D(e.getX(), e.getY(), 1);
      repaint();
   }

   public void mouseClicked(MouseEvent  e) {}
   public void mouseReleased(MouseEvent e) {}
   public void mouseEntered(MouseEvent  e) {}
   public void mouseExited(MouseEvent   e) {}
}
