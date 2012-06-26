package com.saraandshmuel.anddaaven;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

public class CharRangeFilter extends FilterReader {
	
	char filterLow;
	char filterHigh;

	public CharRangeFilter(Reader in, char low) {
		super(in);
		this.filterLow=low;
		this.filterHigh=low;
	}

	public CharRangeFilter(Reader in, char low, char high) {
		super(in);
		this.filterLow=low;
		this.filterHigh=high;
	}

	@Override
	public int read() throws IOException {
		int result;
		do {
			result=super.read();
		} while (result >= filterLow && result <= filterHigh);
			
		return result;
	}
	
	@Override
	public int read(char[] buf) throws IOException {
		return read(buf, 0, buf.length);
	}
	
	@Override
	public int read(char[] buffer, int offset, int count) throws IOException {
		char[] localBuf = new char[buffer.length];
		int read = super.read(localBuf, offset, count);
		int result=0;
		for (int i=offset; i<offset+read; ++i) {
			if (localBuf[i] < filterLow || localBuf[i] > filterHigh) {
				buffer[result++] = localBuf[i];
			}
		}
		return result;
	}
}
