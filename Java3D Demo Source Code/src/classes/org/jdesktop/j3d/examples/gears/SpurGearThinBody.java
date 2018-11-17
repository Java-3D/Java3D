/*
 * $RCSfile: SpurGearThinBody.java,v $
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
 * $Date: 2007/02/09 17:21:39 $
 * $State: Exp $
 */

package org.jdesktop.j3d.examples.gears;

import java.lang.Math.*;
import javax.media.j3d.*;
import javax.vecmath.*;

public class SpurGearThinBody extends SpurGear {
    
    /**
     * Construct a SpurGearThinBody;
     * @return a new spur gear that conforms to the input paramters
     * @param toothCount number of teeth
     * @param pitchCircleRadius radius at center of teeth
     * @param shaftRadius radius of hole at center
     * @param addendum distance from pitch circle to top of teeth
     * @param dedendum distance from pitch circle to root of teeth
     * @param gearThickness  thickness of the gear
     */
    public SpurGearThinBody(int toothCount, float pitchCircleRadius,
			    float shaftRadius, float addendum, float dedendum,
			    float gearThickness) {
	this(toothCount, pitchCircleRadius, shaftRadius,
	     addendum, dedendum, gearThickness, gearThickness, 0.25f, null);
    }

    /**
     * Construct a SpurGearThinBody;
     * @return a new spur gear that conforms to the input paramters
     * @param toothCount number of teeth
     * @param pitchCircleRadius radius at center of teeth
     * @param shaftRadius radius of hole at center
     * @param addendum distance from pitch circle to top of teeth
     * @param dedendum distance from pitch circle to root of teeth
     * @param gearThickness  thickness of the gear
     * @param look the gear's appearance
     */
    public SpurGearThinBody(int toothCount, float pitchCircleRadius,
			    float shaftRadius, float addendum, float dedendum,
			    float gearThickness,
		    Appearance look) {
	this(toothCount, pitchCircleRadius, shaftRadius,
	     addendum, dedendum, gearThickness, gearThickness, 0.25f, look);
    }

    /**
     * Construct a SpurGearThinBody;
     * @return a new spur gear that conforms to the input paramters
     * @param toothCount number of teeth
     * @param pitchCircleRadius radius at center of teeth
     * @param shaftRadius radius of hole at center
     * @param addendum distance from pitch circle to top of teeth
     * @param dedendum distance from pitch circle to root of teeth
     * @param gearThickness thickness of the gear
     * @param toothTipThickness thickness of the tip of the tooth
     * @param look the gear's appearance
     */
    public SpurGearThinBody(int toothCount, float pitchCircleRadius,
			    float shaftRadius, float addendum, float dedendum,
			    float gearThickness, float toothTipThickness,
			    Appearance look) {
	this(toothCount, pitchCircleRadius, shaftRadius, addendum,
	     dedendum, gearThickness, toothTipThickness, 0.25f, look);
	}

    /**
     * Construct a SpurGearThinBody;
     * @return a new spur gear that conforms to the input paramters
     * @param toothCount number of teeth
     * @param pitchCircleRadius radius at center of teeth
     * @param shaftRadius radius of hole at center
     * @param addendum distance from pitch circle to top of teeth
     * @param dedendum distance from pitch circle to root of teeth
     * @param gearThickness thickness of the gear
     * @param toothTipThickness thickness of the tip of the tooth
     * @param toothToValleyRatio ratio of tooth valley to circular pitch
     * (must be <= .25) 
     * @param look the gear's appearance object
     */
    public SpurGearThinBody(int toothCount, float pitchCircleRadius,
			    float shaftRadius, float addendum, float dedendum,
			    float gearThickness, float toothTipThickness,
			    float toothToValleyAngleRatio, Appearance look) { 

	this(toothCount, pitchCircleRadius, shaftRadius, addendum,
	     dedendum, gearThickness, toothTipThickness, 0.25f, look,
	     0.6f * gearThickness, 0.75f * (pitchCircleRadius - shaftRadius));
    }

    /**
     * Construct a SpurGearThinBody;
     * @return a new spur gear that conforms to the input paramters
     * @param toothCount number of teeth
     * @param pitchCircleRadius radius at center of teeth
     * @param shaftRadius radius of hole at center
     * @param addendum distance from pitch circle to top of teeth
     * @param dedendum distance from pitch circle to root of teeth
     * @param gearThickness thickness of the gear
     * @param toothTipThickness thickness of the tip of the tooth
     * @param toothToValleyRatio ratio of tooth valley to circular pitch
     * (must be <= .25)
     * @param look the gear's appearance object
     * @param bodyThickness the thickness of the gear body
     * @param crossSectionWidth the width of the depressed portion of the
     * gear's body
     */
    public SpurGearThinBody(int toothCount, float pitchCircleRadius,
			    float shaftRadius, float addendum, float dedendum,
			    float gearThickness, float toothTipThickness,
			    float toothToValleyAngleRatio, Appearance look,
			    float bodyThickness, float crossSectionWidth) {

	super(toothCount, pitchCircleRadius, addendum, dedendum,
	      toothToValleyAngleRatio);

	float diskCrossSectionWidth =
	    (rootRadius - shaftRadius - crossSectionWidth)/ 2.0f;
	float outerShaftRadius = shaftRadius + diskCrossSectionWidth;
	float innerToothRadius = rootRadius - diskCrossSectionWidth;

	// Generate the gear's body disks, first by the shaft, then in
	// the body and, lastly, by the teeth
	addBodyDisks(shaftRadius, outerShaftRadius,
		     gearThickness, look);
	addBodyDisks(innerToothRadius, rootRadius,
		     gearThickness, look);
	addBodyDisks(outerShaftRadius, innerToothRadius,
		     bodyThickness, look);

	// Generate the gear's "shaft" equivalents the two at the teeth
	// and the two at the shaft
	addCylinderSkins(innerToothRadius, gearThickness, InwardNormals, look);
	addCylinderSkins(outerShaftRadius, gearThickness, OutwardNormals, look);
	
	// Generate the gear's interior shaft
	addCylinderSkins(shaftRadius, gearThickness, InwardNormals, look);

	// Generate the gear's teeth
	addTeeth(pitchCircleRadius, rootRadius,
		 outsideRadius, gearThickness, toothTipThickness,
		 toothToValleyAngleRatio, look);
    }

}
