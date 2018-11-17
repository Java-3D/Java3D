/*
 * $RCSfile: Gear.java,v $
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
 * $Date: 2007/02/09 17:21:38 $
 * $State: Exp $
 */

package org.jdesktop.j3d.examples.gears;

import java.lang.Math.*;
import javax.media.j3d.*;
import javax.vecmath.*;

public class Gear extends javax.media.j3d.TransformGroup {
    
    // Specifiers determining whether to generate outward facing normals or
    // inward facing normals.
    static final int OutwardNormals = 1;
    static final int InwardNormals = -1;

    // The number of teeth in the gear
    int toothCount;

    // Gear start differential angle. All gears are constructed with the
    // center of a tooth at Z-axis angle = 0.
    double gearStartAngle;
    // The Z-rotation angle to place the tooth center at theta = 0
    float toothTopCenterAngle;
    // The Z-rotation angle to place the valley center at theta = 0
    float valleyCenterAngle;
    // The angle about Z subtended by one tooth and its associated valley
    float circularPitchAngle;
    
    // Increment angles
    float toothValleyAngleIncrement;

    // Front and rear facing normals for the gear's body
    final Vector3f frontNormal = new Vector3f(0.0f, 0.0f, -1.0f);
    final Vector3f rearNormal = new Vector3f(0.0f, 0.0f, 1.0f);


    Gear(int toothCount) {
	this.toothCount = toothCount;
    }

    void addBodyDisks(float shaftRadius, float bodyOuterRadius,
		      float thickness, Appearance look) {
	int gearBodySegmentVertexCount;		// #(segments) per tooth-unit
	int gearBodyTotalVertexCount;		// #(vertices) in a gear face
	int gearBodyStripCount[] = new int[1];	// per strip (1) vertex count

	// A ray from the gear center, used in normal calculations
	float xDirection, yDirection;

	// The x and y coordinates at each point of a facet and at each
	// point on the gear: at the shaft, the root of the teeth, and
	// the outer point of the teeth
	float xRoot0, yRoot0, xShaft0, yShaft0;
	float xRoot3, yRoot3, xShaft3, yShaft3;
	float xRoot4, yRoot4, xShaft4, yShaft4;

	// Temporary variables for storing coordinates and vectors 
	Point3f coordinate = new Point3f(0.0f, 0.0f, 0.0f);

	// Gear start differential angle. All gears are constructed with the
	// center of a tooth at Z-axis angle = 0.
	double gearStartAngle = -1.0 * toothTopCenterAngle;

	// Temporaries that store start angle for each portion of tooth facet
	double toothStartAngle, toothTopStartAngle,
	    toothDeclineStartAngle, toothValleyStartAngle,
	    nextToothStartAngle;

	Shape3D newShape;
	int index;

	// The z coordinates for the body disks
	final float frontZ = -0.5f * thickness;
	final float rearZ = 0.5f * thickness;

	/* Construct the gear's front body (front facing torus disk)
	 *                   __2__
	 *                -    |    -  4
	 *             -       /|     /-
	 *           /        / |    /| \
	 *          0\       /  |   / /  >
	 *            \     /   |  /  |   >
	 *             \   /    | /  /     |
	 *              \ / ____|/   |      >
	 *               \--    --__/       | 
	 *                1     3   5
	 *
	 */
	gearBodySegmentVertexCount = 4;
	gearBodyTotalVertexCount = 2 + gearBodySegmentVertexCount * toothCount;
	gearBodyStripCount[0] = gearBodyTotalVertexCount;

	TriangleStripArray frontGearBody
	    = new TriangleStripArray(gearBodyTotalVertexCount,
				     GeometryArray.COORDINATES
				     | GeometryArray.NORMALS,
				     gearBodyStripCount);

	xDirection = (float)Math.cos(gearStartAngle);
	yDirection = (float)Math.sin(gearStartAngle);
	xShaft0 = shaftRadius * xDirection;
	yShaft0 = shaftRadius * yDirection;
	xRoot0 = bodyOuterRadius * xDirection;
	yRoot0 = bodyOuterRadius * yDirection;

	coordinate.set(xRoot0, yRoot0, frontZ);
	frontGearBody.setCoordinate(0, coordinate);
	frontGearBody.setNormal(0, frontNormal);

	coordinate.set(xShaft0, yShaft0, frontZ);
	frontGearBody.setCoordinate(1, coordinate);
	frontGearBody.setNormal(1, frontNormal);

	for(int count = 0; count < toothCount; count++) {
	    index = 2 + count * 4;
	    toothStartAngle
		= gearStartAngle + circularPitchAngle * (double)count;
	    toothValleyStartAngle
		= toothStartAngle + toothValleyAngleIncrement;
	    nextToothStartAngle = toothStartAngle + circularPitchAngle;

	    xDirection = (float)Math.cos(toothValleyStartAngle);
	    yDirection = (float)Math.sin(toothValleyStartAngle);
	    xShaft3 = shaftRadius * xDirection;
	    yShaft3 = shaftRadius * yDirection;
	    xRoot3 = bodyOuterRadius * xDirection;
	    yRoot3 = bodyOuterRadius * yDirection;

	    xDirection = (float)Math.cos(nextToothStartAngle);
	    yDirection = (float)Math.sin(nextToothStartAngle);
	    xShaft4 = shaftRadius * xDirection;
	    yShaft4 = shaftRadius * yDirection;
	    xRoot4 = bodyOuterRadius * xDirection;
	    yRoot4 = bodyOuterRadius * yDirection;

	    coordinate.set(xRoot3, yRoot3, frontZ);
	    frontGearBody.setCoordinate(index, coordinate);
	    frontGearBody.setNormal(index, frontNormal);

	    coordinate.set(xShaft3, yShaft3, frontZ);
	    frontGearBody.setCoordinate(index + 1, coordinate);
	    frontGearBody.setNormal(index + 1, frontNormal);

	    coordinate.set(xRoot4, yRoot4, frontZ);
	    frontGearBody.setCoordinate(index + 2, coordinate);
	    frontGearBody.setNormal(index + 2, frontNormal);

	    coordinate.set(xShaft4, yShaft4, frontZ);
	    frontGearBody.setCoordinate(index + 3, coordinate);
	    frontGearBody.setNormal(index + 3, frontNormal);
	}
	newShape = new Shape3D(frontGearBody, look);
	this.addChild(newShape);

	// Construct the gear's rear body (rear facing torus disc)
	TriangleStripArray rearGearBody
	    = new TriangleStripArray(gearBodyTotalVertexCount,
				     GeometryArray.COORDINATES
				     | GeometryArray.NORMALS,
				     gearBodyStripCount);
	xDirection = (float)Math.cos(gearStartAngle);
	yDirection = (float)Math.sin(gearStartAngle);
	xShaft0 = shaftRadius * xDirection;
	yShaft0 = shaftRadius * yDirection;
	xRoot0 = bodyOuterRadius * xDirection;
	yRoot0 = bodyOuterRadius * yDirection;

	coordinate.set(xShaft0, yShaft0, rearZ);
	rearGearBody.setCoordinate(0, coordinate);
	rearGearBody.setNormal(0, rearNormal);

	coordinate.set(xRoot0, yRoot0, rearZ);
	rearGearBody.setCoordinate(1, coordinate);
	rearGearBody.setNormal(1, rearNormal);

	for(int count = 0; count < toothCount; count++) {
	    index = 2 + count * 4;
	    toothStartAngle
		= gearStartAngle + circularPitchAngle * (double)count;
	    toothValleyStartAngle
		= toothStartAngle + toothValleyAngleIncrement;
	    nextToothStartAngle = toothStartAngle + circularPitchAngle;

	    xDirection = (float)Math.cos(toothValleyStartAngle);
	    yDirection = (float)Math.sin(toothValleyStartAngle);
	    xShaft3 = shaftRadius * xDirection;
	    yShaft3 = shaftRadius * yDirection;
	    xRoot3 = bodyOuterRadius * xDirection;
	    yRoot3 = bodyOuterRadius * yDirection;

	    xDirection = (float)Math.cos(nextToothStartAngle);
	    yDirection = (float)Math.sin(nextToothStartAngle);
	    xShaft4 = shaftRadius * xDirection;
	    yShaft4 = shaftRadius * yDirection;
	    xRoot4 = bodyOuterRadius * xDirection;
	    yRoot4 = bodyOuterRadius * yDirection;

	    coordinate.set(xShaft3, yShaft3, rearZ);
	    rearGearBody.setCoordinate(index, coordinate);
	    rearGearBody.setNormal(index, rearNormal);

	    coordinate.set(xRoot3, yRoot3, rearZ);
	    rearGearBody.setCoordinate(index + 1, coordinate);
	    rearGearBody.setNormal(index + 1, rearNormal);

	    coordinate.set(xShaft4, yShaft4, rearZ);
	    rearGearBody.setCoordinate(index + 2, coordinate);
	    rearGearBody.setNormal(index + 2, rearNormal);

	    coordinate.set(xRoot4, yRoot4, rearZ);
	    rearGearBody.setCoordinate(index + 3, coordinate);
	    rearGearBody.setNormal(index + 3, rearNormal);

	}
	newShape = new Shape3D(rearGearBody, look);
	this.addChild(newShape);
    }

    void addCylinderSkins(float shaftRadius, float length,
			  int normalDirection, Appearance look) {
	int insideShaftVertexCount;		  // #(vertices) for shaft
	int insideShaftStripCount[] = new int[1]; // #(vertices) in strip/strip
	double toothStartAngle, nextToothStartAngle, toothValleyStartAngle;

	// A ray from the gear center, used in normal calculations
	float xDirection, yDirection;

	// The z coordinates for the body disks
	final float frontZ = -0.5f * length;
	final float rearZ = 0.5f * length;

	// Temporary variables for storing coordinates, points, and vectors 
	float xShaft3, yShaft3, xShaft4, yShaft4;
	Point3f coordinate = new Point3f(0.0f, 0.0f, 0.0f);
	Vector3f surfaceNormal = new Vector3f();

	Shape3D newShape;
	int index;
	int firstIndex;
	int secondIndex;


	/*
	 * Construct gear's inside shaft cylinder
	 * First the tooth's up, flat outer, and down distances
	 * Second the tooth's flat inner distance
	 *
	 * Outward facing vertex order:
	 *      0_______2____4
	 *      |      /|   /|
	 *      |    /  |  / |
	 *      |  /    | /  |
	 *      |/______|/___|
	 *      1       3    5
	 *
	 * Inward facing vertex order:
	 *	1_______3____5
	 *      |\      |\   |
	 *      |  \    | \  |
	 *      |    \  |  \ |
	 *      |______\|___\|
	 *      0       2    4
	 */
	insideShaftVertexCount = 4 * toothCount + 2;
	insideShaftStripCount[0] = insideShaftVertexCount;

	TriangleStripArray insideShaft
	    = new TriangleStripArray(insideShaftVertexCount,
				     GeometryArray.COORDINATES
				     | GeometryArray.NORMALS,
				     insideShaftStripCount);
	xShaft3 = shaftRadius * (float)Math.cos(gearStartAngle);
	yShaft3 = shaftRadius * (float)Math.sin(gearStartAngle);

	if (normalDirection == OutwardNormals) {
	    surfaceNormal.set(1.0f, 0.0f, 0.0f);
	    firstIndex = 1;
	    secondIndex = 0;
	} else {
	    surfaceNormal.set(-1.0f, 0.0f, 0.0f);
	    firstIndex = 0;
	    secondIndex = 1;
	}

	// Coordinate labeled 0 in the strip
	coordinate.set(shaftRadius, 0.0f, frontZ);
	insideShaft.setCoordinate(firstIndex, coordinate);
	insideShaft.setNormal(firstIndex, surfaceNormal);

	// Coordinate labeled 1 in the strip
	coordinate.set(shaftRadius, 0.0f, rearZ);
	insideShaft.setCoordinate(secondIndex, coordinate);
	insideShaft.setNormal(secondIndex, surfaceNormal);

	for(int count = 0; count < toothCount; count++) {
	    index = 2 + count * 4;

	    toothStartAngle = circularPitchAngle * (double)count;
	    toothValleyStartAngle
		= toothStartAngle + toothValleyAngleIncrement;
	    nextToothStartAngle = toothStartAngle + circularPitchAngle;

	    xDirection = (float)Math.cos(toothValleyStartAngle);
	    yDirection = (float)Math.sin(toothValleyStartAngle);
	    xShaft3 = shaftRadius * xDirection;
	    yShaft3 = shaftRadius * yDirection;
	    if (normalDirection == OutwardNormals)
		surfaceNormal.set(xDirection, yDirection, 0.0f);
	    else
		surfaceNormal.set(-xDirection, -yDirection, 0.0f);

	    // Coordinate labeled 2 in the strip
	    coordinate.set(xShaft3, yShaft3, frontZ);
	    insideShaft.setCoordinate(index + firstIndex, coordinate);
	    insideShaft.setNormal(index + firstIndex, surfaceNormal);

	    // Coordinate labeled 3 in the strip
	    coordinate.set(xShaft3, yShaft3, rearZ);
	    insideShaft.setCoordinate(index + secondIndex, coordinate);
	    insideShaft.setNormal(index + secondIndex, surfaceNormal);

	    xDirection = (float)Math.cos(nextToothStartAngle);
	    yDirection = (float)Math.sin(nextToothStartAngle);
	    xShaft4 = shaftRadius * xDirection;
	    yShaft4 = shaftRadius * yDirection;
	    if (normalDirection == OutwardNormals)
		surfaceNormal.set(xDirection, yDirection, 0.0f);
	    else
		surfaceNormal.set(-xDirection, -yDirection, 0.0f);

	    // Coordinate labeled 4 in the strip
	    coordinate.set(xShaft4, yShaft4, frontZ);
	    insideShaft.setCoordinate(index + 2 + firstIndex, coordinate);
	    insideShaft.setNormal(index + 2 + firstIndex, surfaceNormal);

	    // Coordinate labeled 5 in the strip
	    coordinate.set(xShaft4, yShaft4, rearZ);
	    insideShaft.setCoordinate(index + 2 + secondIndex, coordinate);
	    insideShaft.setNormal(index + 2 + secondIndex, surfaceNormal);

	}
	newShape = new Shape3D(insideShaft, look);
	this.addChild(newShape);
    }

    public float getToothTopCenterAngle() {
	return toothTopCenterAngle;
    }
    
    public float getValleyCenterAngle() {
	return valleyCenterAngle;
    }

    public float getCircularPitchAngle() {
	return circularPitchAngle;
    }
}
