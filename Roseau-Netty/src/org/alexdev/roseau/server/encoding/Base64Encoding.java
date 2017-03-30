package org.alexdev.roseau.server.encoding;

public class Base64Encoding {
	
	public final static byte NEGATIVE = 64; // '@'
	public final static byte POSITIVE = 65; // 'A'

	public static byte[] EncodeInt32(int i, int numBytes) {
		byte[] bzRes = new byte[numBytes];
		for (int j = 1; j <= numBytes; j++)
		{
			int k = ((numBytes - j) * 6);
			bzRes[j - 1] = (byte)(0x40 + ((i >> k) & 0x3f));
		}

		return bzRes;
	}

	public static int DecodeInt32(byte[] bzData) {
		int i = 0;
		int j = 0;
		
		for (int k = bzData.length - 1; k >= 0; k--) {
			int x = bzData[k] - 0x40;
			if (j > 0)
				x *= (int)Math.pow(64.0, (double)j);

			i += x;
			j++;
		}

		return i;
	}
	
	public static int DecodeUInt32(byte[] bzData) {
		return (int)DecodeInt32(bzData);
	}
}

