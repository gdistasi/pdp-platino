package it.comics.unina;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;


public class NetconfBufferedReader extends BufferedReader {

	
	public NetconfBufferedReader(Reader in){
		super(in);
	}
	
	
	/* Return what is read if end-of-line ('\n') or the NETCONF message termination is found */
	public String readLine()
            throws IOException{
		
		String NC_V10_END_MSG = "]]>]]>";
		
		final int bufLen = 3000;
		char[] buf=new char[bufLen];
		int offset=0;
		int len=500;
		
		int num = read(buf,  offset, len);
	
		while ( num != -1 && 
				buf[offset+num-1]!='\n' && 
				buf.toString().startsWith(NC_V10_END_MSG, offset+num-NC_V10_END_MSG.length())){
			
			offset+=num;
			read(buf, offset, len);
		}
		
		if (buf[offset-1]!='\n'){
			offset++;
		}
		
		buf[++offset]='\0';
		
		return String.valueOf(buf);
	}
	
	
}


