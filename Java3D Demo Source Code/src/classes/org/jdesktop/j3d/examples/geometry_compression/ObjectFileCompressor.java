/*
 * $RCSfile: ObjectFileCompressor.java,v $
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

package org.jdesktop.j3d.examples.geometry_compression;

import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.geometry.compression.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.Hashtable;
import javax.media.j3d.Shape3D;

/**
 * This extension of ObjectFile provides the methods setQuantization() and
 * compress() to compress Wavefront .obj files into the format described by
 * appendix B of the Java 3D specification.
 */
public class ObjectFileCompressor extends ObjectFile {
    private GeometryCompressor compressor = null ;

    public ObjectFileCompressor() {
	super(STRIPIFY | TRIANGULATE) ;
	compressor = new GeometryCompressor() ;
    }

    public ObjectFileCompressor(int flags) {
	super(flags | STRIPIFY | TRIANGULATE) ;
	compressor = new GeometryCompressor() ;
    }

    public ObjectFileCompressor(int flags, float radians) {
	super(flags | STRIPIFY | TRIANGULATE, radians) ;
	compressor = new GeometryCompressor() ;
    }

    public void setFlags(int flags) {
	super.setFlags(flags | STRIPIFY | TRIANGULATE) ;
    }

    private int positionQuant = 10 ;
    private int colorQuant = 8 ;
    private int normalQuant = 3 ;

    /**
     * Set the position, normal, and color quantization values for compression.
     * @param positionQuant number of bits to quantize each position's X, Y,
     * and Z components, ranging from 1 to 16 with a default of 10
     * @param colorQuant number of bits to quantize each color's R, G, B, and
     * alpha components, ranging from 2 to 16 with a default of 8
     * @param normalQuant number of bits for quantizing each normal's U and V
     * components, ranging from 0 to 6 with a default of 3
     */
    public void setQuantization(int positionQuant,
				int colorQuant,
				int normalQuant) {

	this.positionQuant = positionQuant ;
	this.colorQuant = colorQuant ;
	this.normalQuant = normalQuant ;
    }

    /**
     * Compress the specified .obj file into a CompressedGeometryData node
     * component.  
     * @param objFileName String object representing the path to a .obj file
     * @return a CompressedGeometryData node component
     */
    public CompressedGeometryData compress(String objFileName) {
	return compressScene(getScene(objFileName)) ;
    }
	    
    /**
     * Compress the specified .obj file and add it to the end of an open
     * compressed geometry file.
     * @param objFileName String object representing the path to a .obj file
     * @param file a currently open CompressedGeometryFile object
     * @exception IOException - if write fails
     */
    public void compress(String objFileName, CompressedGeometryFile file)
	throws IOException {
	compressScene(getScene(objFileName), file) ;
    }

    /**
     * Compress the specified .obj file into a CompressedGeometryData node
     * component.
     * @param reader an open .obj file
     * @return a CompressedGeometryData node component
     */
    public CompressedGeometryData compress(Reader reader) {
	return compressScene(getScene(reader)) ;
    }

    /**
     * Compress the specified .obj file and add it to the end of an open
     * compressed geometry file.
     * @param reader an open .obj file
     * @param file an open CompressedGeometryFile object
     * @exception IOException - if write fails
     */
    public void compress(Reader reader, CompressedGeometryFile file)
	throws IOException {
	compressScene(getScene(reader), file) ;
    }

    /**
     * Compress the specified .obj file into a CompressedGeometryData node
     * component.
     * @param url Uniform Resource Locator for the .obj file
     * @return a CompressedGeometryData node component
     */
    public CompressedGeometryData compress(URL url) {
	return compressScene(getScene(url)) ;
    }

    /**
     * Compress the specified .obj file and add it to the end of an open
     * compressed geometry file.
     * @param url Uniform Resource Locator for the .obj file
     * @param file a currently open CompressedGeometryFile object
     * @exception IOException - if write fails
     */
    public void compress(URL url, CompressedGeometryFile file)
	throws IOException {
	compressScene(getScene(url), file) ;
    }

    private CompressedGeometryData compressScene(Scene scene) {
	return compressor.compress(getStream(scene)) ;
    }

    private void compressScene(Scene scene, CompressedGeometryFile file)
	throws IOException {
	compressor.compress(getStream(scene), file) ;
    }

    private CompressionStream getStream(Scene scene) {
	Hashtable objs = scene.getNamedObjects() ;
	Shape3D shapes[] = new Shape3D[objs.size()] ;

	objs.values().toArray(shapes) ;
	return new CompressionStream(positionQuant, colorQuant, normalQuant,
				     shapes) ;
    }

    private Scene getScene(String objFileName) {
	Scene scene = null ;
	try {
	  scene = load(objFileName) ;
	}
	catch (FileNotFoundException e) {
	  System.err.println(e) ;
	  System.exit(1) ;
	}
	catch (ParsingErrorException e) {
	  System.err.println(e) ;
	  System.exit(1) ;
	}
	catch (IncorrectFormatException e) {
	  System.err.println(e) ;
	  System.exit(1) ;
	}
	return scene ;
    }

    private Scene getScene(Reader reader) {
	Scene scene = null ;
	try {
	  scene = load(reader) ;
	}
	catch (FileNotFoundException e) {
	  System.err.println(e) ;
	  System.exit(1) ;
	}
	catch (ParsingErrorException e) {
	  System.err.println(e) ;
	  System.exit(1) ;
	}
	catch (IncorrectFormatException e) {
	  System.err.println(e) ;
	  System.exit(1) ;
	}
	return scene ;
    }

    private Scene getScene(URL url) {
	Scene scene = null ;
	try {
	  scene = load(url) ;
	}
	catch (FileNotFoundException e) {
	  System.err.println(e) ;
	  System.exit(1) ;
	}
	catch (ParsingErrorException e) {
	  System.err.println(e) ;
	  System.exit(1) ;
	}
	catch (IncorrectFormatException e) {
	  System.err.println(e) ;
	  System.exit(1) ;
	}
	return scene ;
    }
}
