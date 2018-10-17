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

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import java.io.IOException;

public class QuadXZ extends Mesh {

    private float width;
    private float height;

    /**
     * Serialization only. Do not use.
     */
    public QuadXZ(){
    }

    /**
     * Create a quad with the given width and height. The quad
     * is always created in the XY plane.
     * 
     * @param width The X extent or width
     * @param height The Y extent or width
     */
    public QuadXZ(float width, float height){
        updateGeometry(width, height, false, 1, 1, false);
    }
	
	public QuadXZ(float width, float height, float texW, float texH){
        updateGeometry(width, height, false, texW, texH, false);
    }

    /**
     * Create a quad with the given width and height. The quad
     * is always created in the XY plane.
     * 
     * @param width The X extent or width
     * @param height The Y extent or width
     * @param flipCoords If true, the texture coordinates will be flipped
     * along the Y axis.
	 * @param texW
	 * @param texH
     */
    public QuadXZ(float width, float height, boolean flipCoords, float texW, float texH){
        updateGeometry(width, height, flipCoords, texW, texH, false);
    }
	public QuadXZ(float width, float height, boolean flipCoords, float texW, float texH, boolean flipNorm){
        updateGeometry(width, height, flipCoords, texW, texH, flipNorm);
    }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }

    

    public void updateGeometry(float width, float height, boolean flipCoords,  float texW, float texH, boolean flipNorm) {
        this.width = width;
        this.height = height;
		setBuffer(VertexBuffer.Type.Position, 3, new float[]{0,      0,      0,
                                                0,  0, height,
                                                width,  0, height,
                                                width,  0, 0
                                                });
		
        if (flipCoords){
			setBuffer(VertexBuffer.Type.TexCoord, 2, new float[]{0, 0,
                                                    texW, 0,
                                                    texW, texH,
                                                    0, texH});
			
        }else{
            setBuffer(VertexBuffer.Type.TexCoord, 2, new float[]{0, 0,
                                                    0, texH,
                                                    texW, texH,
                                                    texW, 0});
        }
		if(flipNorm) {
			setBuffer(VertexBuffer.Type.Normal, 3, new float[]{0, -1, 0,
                                              0, -1, 0,
                                              0, -1, 0,
                                              0, -1, 0});
		}
		else setBuffer(VertexBuffer.Type.Normal, 3, new float[]{0, 1, 0,
                                              0, 1, 0,
                                              0, 1, 0,
                                              0, 1, 0});
        if (flipNorm){
            setBuffer(VertexBuffer.Type.Index, 3, new short[]{0, 2, 1,
                                                 0, 3, 2});
        }else{
            setBuffer(VertexBuffer.Type.Index, 3, new short[]{0, 1, 2,
                                                 0, 2, 3});
        }
        
        updateBound();
        setStatic();
    }
    
    @Override
    public void read(JmeImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        width = capsule.readFloat("width", 0);
        height = capsule.readFloat("height", 0);
    }

    @Override
    public void write(JmeExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(width, "width", 0);
        capsule.write(height, "height", 0);
    }
}
