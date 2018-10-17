/*
 * Copyright (c) 2017, Juraj Papp
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the copyright holder nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package templates.geom;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.mesh.IndexBuffer;
import java.nio.FloatBuffer;

/**
 *
 * @author Juraj Papp
 */
public class Rock implements BMesh {
    public int axisSamples;
    public int radialSamples;
    public float radius;
    public float radius2;
    public float height;
    public boolean closed;
    public float jitter = 0f;
    public Vector3f pos = new Vector3f();
    
    public Rock() {
        
    }
    public Rock(int axisSamples, int radialSamples,
            float radius, float height, boolean closed) {
        set(axisSamples, radialSamples, radius, radius, height, closed);
    }
    public Rock(int axisSamples, int radialSamples,
            float radius, float radius2, float height, boolean closed) {
        set(axisSamples, radialSamples, radius, radius2, height, closed);
    }
    public void set(int axisSamples, int radialSamples,
            float radius, float radius2, float height, boolean closed) {
        this.axisSamples = axisSamples;
        this.radialSamples = radialSamples;
        this.radius = radius;
        this.radius2 = radius2;
        this.height = height;
        this.closed = closed;
    }
//    public void build() {
//        float inverseRadial = 1.0f / radialSamples;
//        float hStep = height/(axisSamples-1);
//        
//        float[] sin = new float[radialSamples];
//        float[] cos = new float[radialSamples];
//        for (int r = 0; r < radialSamples; r++) {
//            float angle = FastMath.TWO_PI * inverseRadial * r;
//            cos[r] = FastMath.cos(angle);
//            sin[r] = FastMath.sin(angle);
//        }
//        int vertCount = radialSamples*axisSamples;
//        int triCount = 2 * (axisSamples - 1) * radialSamples;
//        
//        if(closed) {
//            vertCount += 2;
//            triCount += radialSamples+radialSamples;
//        }
//        
//        setBuffer(VertexBuffer.Type.Position, 3, createVector3Buffer(getFloatBuffer(VertexBuffer.Type.Position), vertCount));
//        setBuffer(VertexBuffer.Type.Index, 3, createShortBuffer(getShortBuffer(VertexBuffer.Type.Index), 3 * triCount));
//
//        FloatBuffer p = getFloatBuffer(VertexBuffer.Type.Position);
//        ShortBuffer i = getShortBuffer(VertexBuffer.Type.Index);
//               
//        p.rewind();
//        i.rewind();
//        
//        float j2 = jitter+jitter;
//        for(int a = 0; a < axisSamples; a++)
//            for (int r = 0; r < radialSamples; r++) {
//                p.put(cos[r] + ((float)Math.random()*j2-jitter)).put(a*hStep)
//                        .put(sin[r] + ((float)Math.random()*j2-jitter));
//            }
//        
//        if(closed) {
//            p.put(0).put(0).put(0);
//            p.put(0).put(height).put(0);
//        }
//        short aa = 0;
//        short r;
//        for(short a = 0; a < axisSamples-1; a++) {
//            for (r = 0; r < radialSamples-1; r++) {
//                i.put((short)(aa+r));
//                i.put((short)(aa+r+radialSamples));
//                i.put((short)(aa+r+1));
//                
//                i.put((short)(aa+r+radialSamples));
//                i.put((short)(aa+r+radialSamples+1));
//                i.put((short)(aa+r+1));
//            }
//            i.put((short)(aa+r));
//            i.put((short)(aa+r+radialSamples));
//            i.put((short)(aa));
//
//            i.put((short)(aa+r+radialSamples));
//            i.put((short)(aa+radialSamples));
//            i.put((short)(aa));
//            
//            aa += radialSamples;
//        }
//        
//        if(closed) {
//            for (r = 0; r < radialSamples-1; r++) {
//                i.put((short)(r));
//                i.put((short)(r+1));
//                i.put((short)(vertCount-2));
//            }
//            i.put((short)(r));
//            i.put((short)(0));
//            i.put((short)(vertCount-2));
//            
//            aa = (short) (radialSamples*(axisSamples-1));
//            for (r = 0; r < radialSamples-1; r++) {
//                i.put((short)(aa+r));
//                i.put((short)(vertCount-1));
//                i.put((short)(aa+r+1));
//            }
//            i.put((short)(aa+r));
//            i.put((short)(vertCount-1));
//            i.put((short)(aa));
//        }
//        
//        updateBound();
//        setStatic();
//    }
//    
//    public void buildNormals() {
//            setBuffer(VertexBuffer.Type.Normal, 3, ShaderUtils.computeNormals(this));
//    }
    
//    public void _set(int axisSamples, int radialSamples,
//            float radius, float radius2, float height, boolean closed, boolean inverted) {
////        this.axisSamples = axisSamples;
////        this.radialSamples = radialSamples;
////        this.radius = radius;
////        this.radius2 = radius2;
////        this.height = height;
////        this.closed = closed;
////        this.inverted = inverted;
//
////        VertexBuffer pvb = getBuffer(Type.Position);
////        VertexBuffer nvb = getBuffer(Type.Normal);
////        VertexBuffer tvb = getBuffer(Type.TexCoord);        
//
//
//        int triCount = ((closed ? 2 : 0) + 2 * (axisSamples - 1)) * radialSamples;
//
//        axisSamples += (closed ? 2 : 0);
//
//        // Vertices
//        int vertCount = axisSamples * (radialSamples  ) + (closed ? 2 : 0);
//
//        setBuffer(VertexBuffer.Type.Position, 3, createVector3Buffer(getFloatBuffer(VertexBuffer.Type.Position), vertCount));
//
//        // Normals
////        setBuffer(VertexBuffer.Type.Normal, 3, createVector3Buffer(getFloatBuffer(VertexBuffer.Type.Normal), vertCount));
//
//        // Texture co-ordinates
//        setBuffer(VertexBuffer.Type.TexCoord, 2, createVector2Buffer(vertCount));
//
//        
//        setBuffer(VertexBuffer.Type.Index, 3, createShortBuffer(getShortBuffer(VertexBuffer.Type.Index), 3 * triCount));
//
//        // generate geometry
//        float inverseRadial = 1.0f / radialSamples;
//        float inverseAxisLess = 1.0f / (closed ? axisSamples - 3 : axisSamples - 1);
//        float inverseAxisLessTexture = 1.0f / (axisSamples - 1);
//        float halfHeight = 0.5f * height;
//
//        // Generate points on the unit circle to be used in computing the mesh
//        // points on a cylinder slice.
//        float[] sin = new float[radialSamples];
//        float[] cos = new float[radialSamples];
//
//        for (int radialCount = 0; radialCount < radialSamples; radialCount++) {
//            float angle = FastMath.TWO_PI * inverseRadial * radialCount;
//            cos[radialCount] = FastMath.cos(angle);
//            sin[radialCount] = FastMath.sin(angle);
//        }
////        sin[radialSamples] = sin[0];
////        cos[radialSamples] = cos[0];
//
//        // calculate normals
//        Vector3f[] vNormals = null;
//        Vector3f vNormal = Vector3f.UNIT_Z;
//
//        if ((height != 0.0f) && (radius != radius2)) {
//            vNormals = new Vector3f[radialSamples];
//            Vector3f vHeight = Vector3f.UNIT_Z.mult(height);
//            Vector3f vRadial = new Vector3f();
//
//            for (int radialCount = 0; radialCount < radialSamples; radialCount++) {
//                vRadial.set(cos[radialCount], sin[radialCount], 0.0f);
//                Vector3f vRadius = vRadial.mult(radius);
//                Vector3f vRadius2 = vRadial.mult(radius2);
//                Vector3f vMantle = vHeight.subtract(vRadius2.subtract(vRadius));
//                Vector3f vTangent = vRadial.cross(Vector3f.UNIT_Z);
//                vNormals[radialCount] = vMantle.cross(vTangent).normalize();
//            }
//        }
//
////        FloatBuffer nb = getFloatBuffer(VertexBuffer.Type.Normal);
//        FloatBuffer pb = getFloatBuffer(VertexBuffer.Type.Position);
//        FloatBuffer tb = getFloatBuffer(VertexBuffer.Type.TexCoord);
//
//        // generate the cylinder itself
//        Vector3f tempNormal = new Vector3f();
//        for (int axisCount = 0, i = 0; axisCount < axisSamples; axisCount++, i++) {
//            float axisFraction;
//            float axisFractionTexture;
//            int topBottom = 0;
//            if (!closed) {
//                axisFraction = axisCount * inverseAxisLess; // in [0,1]
//                axisFractionTexture = axisFraction;
//            } else {
//                if (axisCount == 0) {
//                    topBottom = -1; // bottom
//                    axisFraction = 0;
//                    axisFractionTexture = inverseAxisLessTexture;
//                } else if (axisCount == axisSamples - 1) {
//                    topBottom = 1; // top
//                    axisFraction = 1;
//                    axisFractionTexture = 1 - inverseAxisLessTexture;
//                } else {
//                    axisFraction = (axisCount - 1) * inverseAxisLess;
//                    axisFractionTexture = axisCount * inverseAxisLessTexture;
//                }
//            }
//
//            // compute center of slice
//            float z = -halfHeight + height * axisFraction;
//            Vector3f sliceCenter = new Vector3f(0, 0, z);
//
//            float random = 0;//0.10f;
//            
//            System.err.println("pb " + pb.capacity() + ", " + radialSamples);
//            // compute slice vertices with duplication at end point
//            int save = i;
//            for (int radialCount = 0; radialCount < radialSamples; radialCount++, i++) {
//                float radialFraction = radialCount * inverseRadial; // in [0,1)
//                tempNormal.set(cos[radialCount], sin[radialCount], 0.0f);
//
//                tempNormal.multLocal((float)(1f-random-Math.random()*(random+random)));
//                
//                if (vNormals != null) {
//                    vNormal = vNormals[radialCount];
//                } else if (radius == radius2) {
//                    vNormal = tempNormal;
//                }
//
////                if (topBottom == 0) {
////                    if (!inverted)
////                        nb.put(vNormal.x).put(vNormal.y).put(vNormal.z);
////                    else
////                        nb.put(-vNormal.x).put(-vNormal.y).put(-vNormal.z);
////                } else {
////                    nb.put(0).put(0).put(topBottom * (inverted ? -1 : 1));
////                }
//
//                tempNormal.multLocal((radius - radius2) * axisFraction + radius2)
//                        .addLocal(sliceCenter);
//                pb.put(tempNormal.x).put(tempNormal.y).put(tempNormal.z);
//
//                tb.put((inverted ? 1 - radialFraction : radialFraction))
//                        .put(axisFractionTexture);
//            }
//
//            BufferUtils.copyInternalVector3(pb, save, i);
////            BufferUtils.copyInternalVector3(nb, save, i);
//
//            tb.put((inverted ? 0.0f : 1.0f))
//                    .put(axisFractionTexture);
//        }
//
//        if (closed) {
//            pb.put(0).put(0).put(-halfHeight); // bottom center
////            nb.put(0).put(0).put(-1 * (inverted ? -1 : 1));
//            tb.put(0.5f).put(0);
//            pb.put(0).put(0).put(halfHeight); // top center
////            nb.put(0).put(0).put(1 * (inverted ? -1 : 1));
//            tb.put(0.5f).put(1);
//        }
//
//        IndexBuffer ib = getIndexBuffer();
//        int index = 0;
//        // Connectivity
//        for (int axisCount = 0, axisStart = 0; axisCount < axisSamples - 1; axisCount++) {
//            int i0 = axisStart;
//            int i1 = i0 + 1;
//            axisStart += radialSamples + 1;
//            int i2 = axisStart;
//            int i3 = i2 + 1;
//            for (int i = 0; i < radialSamples; i++) {
//                if (closed && axisCount == 0) {
//                    if (!inverted) {
//                        ib.put(index++, i0++);
//                        ib.put(index++, vertCount - 2);
//                        ib.put(index++, i1++);
//                    } else {
//                        ib.put(index++, i0++);
//                        ib.put(index++, i1++);
//                        ib.put(index++, vertCount - 2);
//                    }
//                } else if (closed && axisCount == axisSamples - 2) {
//                    ib.put(index++, i2++);
//                    ib.put(index++, inverted ? vertCount - 1 : i3++);
//                    ib.put(index++, inverted ? i3++ : vertCount - 1);
//                } else {
//                    ib.put(index++, i0++);
//                    ib.put(index++, inverted ? i2 : i1);
//                    ib.put(index++, inverted ? i1 : i2);
//                    ib.put(index++, i1++);
//                    ib.put(index++, inverted ? i2++ : i3++);
//                    ib.put(index++, inverted ? i3++ : i2++);
//                }
//            }
//        }
//        System.out.println("index " + index + " / " + ib.size());
//        System.out.println("vert count " + vertCount);
//        System.out.println("tri " + triCount);
//        setBuffer(VertexBuffer.Type.Normal, 3, ShaderUtils.computeNormals(this));
//
//        updateBound();
//        setStatic();
//    }

    @Override
    public int estimateVertexCount() {
        int vertCount = radialSamples*axisSamples;
        if(closed) 
            vertCount += 2;
        return vertCount;
    }

    @Override
    public int estimateTriangleCount() {
        int triCount = 2 * (axisSamples - 1) * radialSamples;
        if(closed) triCount += radialSamples+radialSamples;
        return triCount;
    }

    @Override
    public void build(FloatBuffer p, IndexBuffer i, int pOff, int ibOff) {
        float inverseRadial = 1.0f / radialSamples;
        float hStep = height/(axisSamples-1);
        float invAxis = 1f/(axisSamples-1);
        
        float[] sin = new float[radialSamples];
        float[] cos = new float[radialSamples];
        for (int r = 0; r < radialSamples; r++) {
            float angle = FastMath.TWO_PI * inverseRadial * r;
            cos[r] = FastMath.cos(angle);
            sin[r] = FastMath.sin(angle);
        }
        int vertCount = radialSamples*axisSamples;
        int triCount = 2 * (axisSamples - 1) * radialSamples;
        
        if(closed) {
            vertCount += 2;
            triCount += radialSamples+radialSamples;
        }
          
        p.position(pOff);
        pOff = pOff /3;
        float x = pos.x; float y = pos.y; float z = pos.z;
        
        float j2 = jitter+jitter;
        float rad;
        for(int a = 0; a < axisSamples; a++) {
            rad = FastMath.interpolateLinear(a*invAxis, radius, radius2);
            for (int r = 0; r < radialSamples; r++) {
                p.put(x+cos[r]*rad + ((float)Math.random()*j2-jitter)).put(y+a*hStep)
                        .put(z+sin[r]*rad + ((float)Math.random()*j2-jitter));
            }
        }
        
        if(closed) {
            p.put(x).put(y).put(z);
            p.put(x).put(y+height).put(z);
        }
        short aa = 0;
        short r;
        for(short a = 0; a < axisSamples-1; a++) {
            for (r = 0; r < radialSamples-1; r++) {
                i.put(ibOff++, pOff+(aa+r));
                i.put(ibOff++, pOff+(aa+r+radialSamples));
                i.put(ibOff++, pOff+(aa+r+1));
                
                i.put(ibOff++, pOff+(aa+r+radialSamples));
                i.put(ibOff++, pOff+(aa+r+radialSamples+1));
                i.put(ibOff++, pOff+(aa+r+1));
            }
            i.put(ibOff++, pOff+(aa+r));
            i.put(ibOff++, pOff+(aa+r+radialSamples));
            i.put(ibOff++, pOff+(aa));

            i.put(ibOff++, pOff+(aa+r+radialSamples));
            i.put(ibOff++, pOff+(aa+radialSamples));
            i.put(ibOff++, pOff+(aa));
            
            aa += radialSamples;
        }
        
        if(closed) {
            for (r = 0; r < radialSamples-1; r++) {
                i.put(ibOff++, pOff+(short)(r));
                i.put(ibOff++, pOff+(short)(r+1));
                i.put(ibOff++, pOff+(short)(vertCount-2));
            }
            i.put(ibOff++, pOff+(short)(r));
            i.put(ibOff++, pOff+(short)(0));
            i.put(ibOff++, pOff+(short)(vertCount-2));
            
            aa = (short) (radialSamples*(axisSamples-1));
            for (r = 0; r < radialSamples-1; r++) {
                i.put(ibOff++, pOff+(short)(aa+r));
                i.put(ibOff++, pOff+(short)(vertCount-1));
                i.put(ibOff++, pOff+(short)(aa+r+1));
            }
            i.put(ibOff++, pOff+(short)(aa+r));
            i.put(ibOff++, pOff+(short)(vertCount-1));
            i.put(ibOff++, pOff+(short)(aa));
        }
        
    }
}
