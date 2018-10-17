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
package theleo.sevensky.stars.hyg;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Juraj Papp
 */
public class HygCatalogReader {
	public HygRecord sun;
	public ArrayList<HygRecord> records = new ArrayList<HygRecord>();

	public void loadAll() throws IOException {
		load(new BufferedInputStream(HygCatalogReader.class.getResourceAsStream("hygdata_v3.csv")));
	}
	public void load9k() throws IOException {
		load(new BufferedInputStream(HygCatalogReader.class.getResourceAsStream("hygdata_v3_6.5.csv")));
	}
	public void load(InputStream io) throws IOException {
		try {
			Scanner in = new Scanner(io);
			
			String header = in.nextLine();
			
			while(in.hasNextLine()) {
				String line = in.nextLine();
				if(line.isEmpty()) continue;

				HygRecord rec = new HygRecord(line.split(",",-1));
				if(rec.id == 0) sun = rec;
				else records.add(rec);
			}
		}
		catch(Exception e) {e.printStackTrace();}
		finally {io.close();}
	}
	
	
	public static void main(String[] args) {
		HygCatalogReader y = new HygCatalogReader();
		try {
			y.load9k();
			System.out.println("Num " + y.records.size());
			//YaleRecord alpAnd = y.records.get(14);
			
			for(HygRecord yr : y.records) {
				double vMag = yr.mag;
				System.out.println(vMag);
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
