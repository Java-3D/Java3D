This directory contains example code for using compressed geometry in
Java 3D through the com.sun.j3d.utils.geometry.compression package.

Applications:

  obj2cg -- takes the names of .obj files to compress followed by the name
            of a .cg compressed geometry resource file.  If the .cg file
            doesn't exist, then an attempt is made to create it; otherwise,
            new compressed geometry objects are appended to the end.

	    The .obj files are compressed and stored into the .cg file in
            the order in which they appear in the command line, and can be
            accessed through indices [0 .. fileCount-1]

  cgview -- takes the name of a .cg file and the index of the object to
            display, which can range from [0 .. objectCount-1].  The object
            may rotated, scaled, and translated in response to mouse drags.


Utility classes:

  ObjectFileCompressor.java -- 
  Extends ObjectFile with compression methods.
