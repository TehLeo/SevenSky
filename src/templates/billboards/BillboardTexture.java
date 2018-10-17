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
package templates.billboards;

import com.jme3.texture.Image;
import com.jme3.texture.Texture2D;
import java.awt.Rectangle;
import java.util.ArrayList;

/**
 *
 * @author Juraj Papp
 */
public class BillboardTexture {
		public Texture2D tex;
		public ArrayList<MappedRegion> regions = new ArrayList<>();
		public BillboardTexture(Texture2D tex) {
			this.tex = tex;
		}
		public BillboardTexture(int w, int h, Image.Format f) {
			this.tex = new Texture2D(w, h, f);
		}
		public static class MappedRegion {
			public Rectangle rect;
			public MappedRegion(int x, int y, int w, int h) {
				rect = new Rectangle(x,y,w,h);
			}
		}
		public MappedRegion aquire(int w, int h) {
			int iw = tex.getImage().getWidth();
			int ih = tex.getImage().getHeight();
			
			int gw = iw/w;
			int gh = ih/h;
			
			//gw*gh allocation slots
			int x = 0, y = 0;
			for(int j = 0; j < gh; j++) {
				x = 0;
				for(int i = 0; i < gw; i++) {
					if(isEmpty(x, y, w, h)) {
						MappedRegion mp = new MappedRegion(x, y, w, h);
						regions.add(mp);
						return mp;
					}
					x += w; 
				}
				y += h;
			}
			return null;
		}
		public boolean isEmpty(int x, int y, int w, int h) {
			for(MappedRegion m : regions) 
				if(m.rect.intersects(x, y, w, h)) return false;
			return true;
		}
		public int getWidth() { return tex.getImage().getWidth();}
		public int getHeight() { return tex.getImage().getHeight();}
	}
