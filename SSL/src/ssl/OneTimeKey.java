package ssl;

import java.math.BigInteger;
import java.util.Arrays;

public class OneTimeKey {
	
	public static void main(String[] args) {
		String randomStream = args[0];
		String text = args[1];
		System.out.println("original text is "+text);
		String[] data = convertToBinary(text);
		
		String[] K = generateOneTimeKey(randomStream, data.length);
		
		String[] encodedPacket = applyOneTimeKey(data, K);
		
		int j = 0;
		StringBuilder encoded = new StringBuilder();
		while(j < encodedPacket.length) {
			encoded.append((char) Integer.parseInt(encodedPacket[j], 2));
			j++;
		}
		System.out.println("encoded to "+ encoded.toString());
		String[] decodedPacket = applyOneTimeKey(encodedPacket, K);
		StringBuilder decoded = new StringBuilder();
		j = 0;
		while(j < decodedPacket.length) {
			decoded.append((char) Integer.parseInt(decodedPacket[j], 2));
			j++;
		}
		System.out.println("decoded to "+decoded.toString());
		
	}
	
//	private static String getBytesFromStream(String randomStream) {
//		StringBuilder sb = new StringBuilder();
//		int c;
//		for(int i = 0; i < randomStream.length(); i++) {
//			c = randomStream.charAt(i) - '0';
//			sb.append(Integer.toBinaryString(c));
//		}
//		return sb.toString();
//	}
	
	public static String[] applyOneTimeKey(String[] packet, String[] K) {
		String[] encryptedPacket = xorBits(packet, K);
		return encryptedPacket;
	}
	
	public static String[] xorBits(String[] packet, String[] K) {
		BigInteger b1;
		BigInteger b2;
		BigInteger c1;
		for(int i = 0; i<packet.length; i++) {
			b1 = new BigInteger(Integer.parseInt(packet[i], 2)+"");
			b2 = new BigInteger(Integer.parseInt(K[i], 2)+"");
			c1 = b1.xor(b2);
			packet[i] = Integer.toBinaryString(c1.intValue());
		}
		return packet;
	}
	
	private static String[] convertToBinary(String text) {
		int c;
		String[] data = new String[text.length()];
		for(int i = 0 ;i < text.length(); i++) {
			c = text.charAt(i);
			data[i] = Integer.toBinaryString(c);
		}
		return data;
	}
	
	private static String[] generateOneTimeKey(String randomStream, int requiredLength) {
		String[] K = new String[requiredLength];
		Arrays.fill(K, "00000000");
		StringBuilder sb = new StringBuilder(randomStream+randomStream);
		int c;
		for (int i = 0, j = 0; i < K.length; i++, j++) {
			if(j == sb.length()) j = 0;
			c = sb.charAt(j);
			K[i] = Integer.toBinaryString(c);
		}
		return K;
	}
	
}
