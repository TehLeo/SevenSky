package templates.geom;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Prism extends Mesh {
    public Prism(float radius, float halfHeight, int sides) {
        this(Vector3f.ZERO, radius, halfHeight, sides);
    }
    public Prism(Vector3f center, float radius, float halfHeight, int sides) {
        FloatBuffer fb = BufferUtils.createFloatBuffer(sides*18);
        ShortBuffer idx = BufferUtils.createShortBuffer((sides+sides-2)*6);
        FloatBuffer normal = BufferUtils.createFloatBuffer(sides*18);
        normal.position(6);
        float angle = FastMath.TWO_PI/sides,x,z;
        float lastX = 0,//FastMath.sin(0),
              lastZ = 1;//FastMath.cos(0);
        short c = 2;
        x = FastMath.sin(angle);
        z = FastMath.cos(angle)+1;
        float normVal = 1f/FastMath.sqrt(x*x+z*z);
        for (int i = 0; i < sides-1; i++, c+= 6) {
            x = center.x+radius * lastX;
            z = center.z+radius * lastZ;
            for(int j = 0; j < 3; j++) {
               fb.put(x).put(center.y+halfHeight).put(z);
               fb.put(x).put(center.y-halfHeight).put(z);
            }
            x = FastMath.sin((i+1)*angle);
            z = FastMath.cos((i+1)*angle);
            
            lastX = (lastX+x)*normVal;
            lastZ = (lastZ+z)*normVal;
            
            normal.put(lastX).put(0).put(lastZ);
            normal.put(lastX).put(0).put(lastZ);
            normal.put(0).put(1).put(0);
            normal.put(0).put(-1).put(0);
            normal.put(lastX).put(0).put(lastZ);
            normal.put(lastX).put(0).put(lastZ);
                        
            lastX = x;
            lastZ = z;
            
            //index for sides
            idx.put(c).put((short) (c+1)).put((short) (c+4));
            idx.put((short) (c+5)).put((short) (c+4)).put((short) (c+1));
        }
        x = center.x+radius * lastX;
        z = center.z+radius * lastZ;
        for(int j = 0; j < 3; j++) {
           fb.put(x).put(center.y+halfHeight).put(z);
           fb.put(x).put(center.y-halfHeight).put(z);
        }
        x = 0;//FastMath.sin(0);
        z = 1;//FastMath.cos(0);
            
        lastX = (lastX+x)*normVal;
        lastZ = (lastZ+z)*normVal;
            
        normal.put(lastX).put(0).put(lastZ);
        normal.put(lastX).put(0).put(lastZ);
        normal.put(0).put(1).put(0);
        normal.put(0).put(-1).put(0);
        normal.flip();
        normal.put(lastX).put(0).put(lastZ);
        normal.put(lastX).put(0).put(lastZ);
            
        idx.put(c).put((short) (c+1)).put((short)0);
        idx.put((short) 1).put((short) 0).put((short) (c+1));
        
        c = 10; //index for caps
        for (int i = 0; i < sides-2; i++, c += 6) {
            idx.put((short)4).put((short) (c)).put((short) (c+6));
            idx.put((short)5).put((short) (c+7)).put((short) (c+1));
        }        
        normal.rewind();
        fb.flip();
        idx.flip();
               
        setBuffer(VertexBuffer.Type.Position, 3, fb);
        setBuffer(VertexBuffer.Type.Index, 3, idx);
        setBuffer(VertexBuffer.Type.Normal, 3, normal);
        
        updateBound();
    }
}
