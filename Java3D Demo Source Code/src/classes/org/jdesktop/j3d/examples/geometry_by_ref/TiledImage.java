/*
 * $RCSfile: TiledImage.java,v $
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
 * $Date: 2007/02/09 17:21:40 $
 * $State: Exp $
 */

package org.jdesktop.j3d.examples.geometry_by_ref;

import java.awt.*;
import java.awt.image.*;
import java.util.Vector;
import java.awt.color.ColorSpace;

public class TiledImage extends Object implements RenderedImage {
    
 
    WritableRaster[][] tile = new WritableRaster[3][3];
    
    WritableRaster bigTile;
    ComponentColorModel colorModel;
    BufferedImage checkBoard;
    int minX = -2;
    int minY = -1;

    TiledImage() {
	ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB); 
	int[] nBits = {8, 8, 8, 8};
	int i, j, k, cc = 255;
	int[] bandOffset = new int[4];
	colorModel =
	    new ComponentColorModel(cs, nBits, true, false, Transparency.OPAQUE, 0);
	// Create 9 tiles
	bandOffset[0] = 3;
	bandOffset[1] = 2;
	bandOffset[2] = 1;
	bandOffset[3] = 0;
	for (i = 0; i < 3; i++) {
	    for (j = 0; j < 3; j++) {
		tile[i][j] = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, 8, 8 , 32, 4, bandOffset, null);
	    }
	}

	// tile {-2, -1}
	byte[] byteData = ((DataBufferByte)tile[0][0].getDataBuffer()).getData();
	for (i=4, k = 8 * 4 * 4+4 * 4;i < 8;i++, k+= 16){
	    for (j=4;j < 8;j++, k+=4){
		byteData[k] = (byte)0;
		byteData[k+1] = (byte)cc; 
		byteData[k+2] = (byte)0;
		byteData[k+3] = (byte)cc ;
	    }
	}

	// tile {-1, -1}
	byteData = ((DataBufferByte)tile[1][0].getDataBuffer()).getData();
	for (i=4, k = 8 * 4 * 4;i < 8;i++){
	    for (j=4;j < 8;j++, k+=4){
		byteData[k] = (byte)0;
		byteData[k+1] = (byte)cc; 
		byteData[k+2] = (byte)0;
		byteData[k+3] = (byte)cc ;
	    }
	    for (j=4;j < 8;j++, k+=4){
		byteData[k] = (byte)0;
		byteData[k+1] = (byte)0; 
		byteData[k+2] = (byte)0;
		byteData[k+3] = (byte)cc ;
	    }
	}

	// tile {1, -1}
	byteData = ((DataBufferByte)tile[2][0].getDataBuffer()).getData();
	for (i=4, k = 8 * 4 * 4;i < 8;i++, k+= 16){
	    for (j=0;j < 4;j++, k+=4){
		byteData[k] = (byte)0;
		byteData[k+1] = (byte)0; 
		byteData[k+2] = (byte)0;
		byteData[k+3] = (byte)cc ;
	    }
	}

	// tile {-2, 0}
	byteData = ((DataBufferByte)tile[0][1].getDataBuffer()).getData();
	for (i=0, k = 16;i < 4;i++, k+=16){
	    for (j=4;j < 8;j++, k+=4){
		byteData[k] = (byte)0;
		byteData[k+1] = (byte)cc; 
		byteData[k+2] = (byte)0;
		byteData[k+3] = (byte)cc ;
	    }
	}
	for (i=4, k = 8*4*4+16;i < 8;i++, k+=16){
	    for (j=4;j < 8;j++, k+=4){
		byteData[k] = (byte)0;
		byteData[k+1] = (byte)0; 
		byteData[k+2] = (byte)cc;
		byteData[k+3] = (byte)0 ;
	    }
	}
	// tile {-1, 0}
	byteData = ((DataBufferByte)tile[1][1].getDataBuffer()).getData();
	for (i=0, k = 0;i < 4;i++){
	    for (j=4;j < 8;j++, k+=4){
		byteData[k] = (byte)0;
		byteData[k+1] = (byte)cc; 
		byteData[k+2] = (byte)0;
		byteData[k+3] = (byte)cc ;
	    }
	    for (j=4;j < 8;j++, k+=4){
		byteData[k] = (byte)0;
		byteData[k+1] = (byte)0; 
		byteData[k+2] = (byte)0;
		byteData[k+3] = (byte)cc ;
	    }
	}
	for (i=0, k = 8 * 4 * 4;i < 4;i++){
	    for (j=4;j < 8;j++, k+=4){
		byteData[k] = (byte)0;
		byteData[k+1] = (byte)0; 
		byteData[k+2] = (byte)cc;
		byteData[k+3] = (byte)0 ;
	    }

	    for (j=4;j < 8;j++, k+=4){
		byteData[k] = (byte)0;
		byteData[k+1] = (byte)0; 
		byteData[k+2] = (byte)cc;
		byteData[k+3] = (byte)cc ;
	    }	    

	}


	// tile {0, 0}
	byteData = ((DataBufferByte)tile[2][1].getDataBuffer()).getData();
	for (i=0, k = 0;i < 4;i++, k+= 16) {
	    for (j=4;j < 8;j++, k+=4){
		byteData[k] = (byte)0;
		byteData[k+1] = (byte)0; 
		byteData[k+2] = (byte)0;
		byteData[k+3] = (byte)cc ;
	    }
	}
	for (i=4, k = 8 * 4* 4;i < 8;i++, k+= 16) {
	    for (j=4;j < 8;j++, k+=4){
		byteData[k] = (byte)0;
		byteData[k+1] = (byte)0; 
		byteData[k+2] = (byte)cc;
		byteData[k+3] = (byte)cc ;
	    }
	}
	
	    
	// tile {-2, 1}
	byteData = ((DataBufferByte)tile[0][2].getDataBuffer()).getData();
	for (i=4, k = 16;i < 8;i++, k+= 16) {
	    for (j=4;j < 8;j++, k+=4){
		byteData[k] = (byte)0;
		byteData[k+1] = (byte)0; 
		byteData[k+2] = (byte)cc;
		byteData[k+3] = (byte)0 ;
	    }
	}


	// tile {-1, 1}
	byteData = ((DataBufferByte)tile[1][2].getDataBuffer()).getData();
	for (i=0, k = 0;i < 8;i++) {
	    for (j=4;j < 8;j++, k+=4){
		byteData[k] = (byte)0;
		byteData[k+1] = (byte)0; 
		byteData[k+2] = (byte)cc;
		byteData[k+3] = (byte)0 ;
	    }
	    for (j=4;j < 8;j++, k+=4){
		byteData[k] = (byte)0;
		byteData[k+1] = (byte)0; 
		byteData[k+2] = (byte)cc;
		byteData[k+3] = (byte)cc ;
	    }
	}



	// tile {0, 1}
	byteData = ((DataBufferByte)tile[2][2].getDataBuffer()).getData();
	for (i=4, k = 0;i < 8;i++, k+= 16) {
	    for (j=4;j < 8;j++, k+=4){
		byteData[k] = (byte)0;
		byteData[k+1] = (byte)0; 
		byteData[k+2] = (byte)cc;
		byteData[k+3] = (byte)cc ;
	    }
	}
	
	bigTile =  Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, 16, 16 , 64, 4, bandOffset, null);;
        byteData = ((DataBufferByte)bigTile.getDataBuffer()).getData(); 
        for (i=0, k = 0;i < 8;i++){
            for (j=0;j < 8;j++, k+=4){
		byteData[k] = (byte)0;
                byteData[k+1] = (byte)cc; 
                byteData[k+2] = (byte)0;
                byteData[k+3] = (byte)cc ;
            } 
            for (;j < 16;j++, k+=4){ 
		byteData[k] = (byte)0;
                byteData[k+1] = (byte)0; 
                byteData[k+2] = (byte)0;
                byteData[k+3] = (byte)cc ;
            } 
        } 
        for (;i < 16;i++){
            for (j=0;j < 8;j++, k+=4){ 
		byteData[k] = (byte)0;
                byteData[k+1] = (byte)0; 
                byteData[k+2] = (byte)cc;
                byteData[k+3] = (byte)0;
            } 
            for (;j < 16;j++, k+=4){ 
		byteData[k] = (byte)0;
                byteData[k+1] = (byte)0; 
                byteData[k+2] = (byte)cc;
                byteData[k+3] = (byte)cc ;
            } 
        }
	checkBoard = new BufferedImage(colorModel, bigTile, false, null);
    }


    
    // create  four tiles {r, g, b, y}
    public WritableRaster copyData(WritableRaster raster) {
	return checkBoard.copyData(raster);
    }

    public ColorModel getColorModel() {
	return checkBoard.getColorModel();
    }

    public Raster getData() {
	return checkBoard.getData();
    }

    public Raster getData(Rectangle rect) {
	return checkBoard.getData(rect);
    }

    public int getHeight() {
	return 16;
    }

    public int getMinTileX() {
	return minX;
    }

    public int getMinTileY() {
	return minY;
    }

    public int getMinX () {
	return -8;
    }

    public int getMinY () {
	return -8;
    }

    public int getNumXTiles() {
	return 3;
    }

    public int getNumYTiles() {
	return 3;
    }

    public Object getProperty(String name) {
	return checkBoard.getProperty(name);
    }

    public String[] getPropertyNames() {
	return checkBoard.getPropertyNames();
    }


    public SampleModel getSampleModel() {
	return checkBoard.getSampleModel();
    }

    public Vector getSources() {
	return null;
    }

    public Raster getTile(int tileX, int tileY) {
	return tile[tileX- minX][tileY - minY];
    }

    public int getTileGridXOffset() {
	return 4;
    }

    public int getTileGridYOffset() {
	return -4;
    }


    public int getTileHeight() {
	return 8;
    }


    public int getTileWidth() {
	return 8;
    }

    public int getWidth() {
	return 16;
    }
}
    

