
//
// dimple.vert: Vertex shader for bump mapping dimples (bumps)
//
// author: John Kessenich
//
// Copyright (c) 2002: 3Dlabs, Inc.
//

varying vec3 LightDir;
varying vec3 EyeDir;
varying vec3 Normal;

uniform vec3 LightPosition;
// uniform float Scale;
// vec3 LightPosition = vec3(0.0, 0.0, 5.0);
float Scale = 1.0;

void main(void) 
{
	vec4 pos = gl_ModelViewMatrix * gl_Vertex;
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    vec3 eyeDir = vec3(pos);
//    gl_TexCoord[0] = gl_MultiTexCoord0;
    gl_TexCoord[0] = gl_Vertex;
    gl_FrontColor = gl_Color;
	
    vec3 n = normalize(gl_NormalMatrix * gl_Normal);
    vec3 t = normalize(cross(vec3(1.141, 2.78, 3.14), n));
    vec3 b = cross(n, t);

    vec3 v;
    v.x = dot(LightPosition, t);
    v.y = dot(LightPosition, b);
    v.z = dot(LightPosition, n);
    LightDir = normalize(v);

    v.x = dot(eyeDir, t);
    v.y = dot(eyeDir, b);
    v.z = dot(eyeDir, n);
    EyeDir = normalize(v);
}
