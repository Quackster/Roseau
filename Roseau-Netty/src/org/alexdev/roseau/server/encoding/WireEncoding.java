package org.alexdev.roseau.server.encoding;

public class WireEncoding {

	public static byte NEGATIVE = 72; // 'H'
	public static byte POSITIVE = 73; // 'I'
	public static int MAX_INTEGER_BYTE_AMOUNT = 6;

	public static byte[] EncodeInt32(int i) {
		
		byte[] wf = new byte[WireEncoding.MAX_INTEGER_BYTE_AMOUNT];
		
		int pos = 0;
		int numBytes = 1;
		int startPos = pos;
		int negativeMask = i >= 0 ? 0 : 4;
		
		i = Math.abs(i);
		
		wf[pos++] = (byte)(64 + (i & 3));
		
		for (i >>= 2; i != 0; i >>= WireEncoding.MAX_INTEGER_BYTE_AMOUNT) {
			numBytes++;
			wf[pos++] = (byte)(64 + (i & 0x3f));
		}
		
		wf[startPos] = (byte)(wf[startPos] | numBytes << 3 | negativeMask);

		byte[] bzData = new byte[numBytes];
		
		for (int x = 0; x < numBytes; x++) {
			bzData[x] = wf[x];
		}

		return bzData;
	}

	public static int DecodeInt32(byte[] bzData) {
		int pos = 0;
		int v = 0;
		
		boolean negative = (bzData[pos] & 4) == 4;
		int numBytes = bzData[pos] >> 3 & 7;
		
		v = bzData[pos] & 3;
		pos++;
		
		int shiftAmount = 2;
		
		for (int b = 1; b < numBytes; b++) {
			v |= (bzData[pos] & 0x3f) << shiftAmount;
			shiftAmount = 2 + 6 * b;
			pos++;
		}

		if (negative == true)
			v *= -1;

		return v;
	}

}
