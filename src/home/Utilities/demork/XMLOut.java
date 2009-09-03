/**
 * Copyright (c) Gunnar Aastrand Grimnes, DFKI GmbH, 2008. 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in 
 *    the documentation and/or other materials provided with the distribution.
 *  * Neither the name of the DFKI GmbH nor the names of its contributors may be used to endorse or promote products derived from 
 *    this software without specific prior written permission.
 *    
 *    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 *    BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 *    SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 *    DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS  
 *    INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE  
 *    OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package dimm.home.Utilities.demork;

import dimm.home.Utilities.demork.database.MorkCell;
import dimm.home.Utilities.demork.database.MorkDatabase;
import dimm.home.Utilities.demork.database.MorkRow;
import dimm.home.Utilities.demork.database.MorkTable;

/**
 * @author grimnes
 *
 */
public class XMLOut {
	
	public static String outPut(MorkDatabase db) {
		StringBuffer xml=new StringBuffer();
		
		xml.append("<?xml version='1.0'?>\n");
		
		
		for (String tk: db.tables.keySet()) {
			MorkTable t = db.tables.get(tk);
			xml.append("<table key='"+tk+"'>\n");
			
			for (String rk: t.rows.keySet()) {
				MorkRow r=t.rows.get(rk);
				xml.append("\t<row key='"+rk+"'>\n");
					for (MorkCell c: r.cells) {
						if (c.atom.length()>0) 
							xml.append("\t\t<"+c.column+">"+c.atom+"</"+c.column+">\n");
					}
				
				xml.append("\t</row>\n");
			}
			
			xml.append("</table>\n");
		}
		
		
		return xml.toString();
	}

}
