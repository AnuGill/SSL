package ssl;

import java.math.BigInteger;
import java.util.Arrays;

public class Hash {
	
	static int k;
	static int pattern ;
	static int ncheckbytes;
	static boolean verified = false;
	static int ndatabytes;
	static int n; //actual data bytes used in the packet
	
	public static void main(String[] args) {
		ndatabytes = Integer.parseInt(args[0]);
		ncheckbytes = Integer.parseInt(args[1]);
		pattern = Integer.parseInt(args[2]);
		k = Integer.parseInt(args[3]);
		String text = args[4];
		n = text.length();
		
		String[] data = convertToBinary(text, ndatabytes);
		
		System.out.println("packed Bytes");
		System.out.println(text);
		
		String[] sendPacket = assemblePackets(data, ndatabytes, n, k, pattern, ncheckbytes);
		
		//String[] encryptedPacket = applyOneTimeKey(sendPacket);
		
		//String[] decryptedPacket = applyOneTimeKey(encryptedPacket);
		
		System.out.println("unpacked Bytes");
		
		String[] receivedPacket = disassemblePacket(sendPacket, k, pattern, ncheckbytes);
		
		StringBuilder sb = new StringBuilder(); int index = 1;
		
		if(receivedPacket != null) {
			while(index <= n) {
				sb.append((char) Integer.parseInt(receivedPacket[index], 2));
				index++;
			}
			System.out.println(sb.toString());
		}
		else System.out.println("Packet dropped");
		
	}
	
	public static String[] pack(String text, int ndatabytes, int n, int k, int pattern, int ncheckbytes) {
		String[] data = convertToBinary(text, ndatabytes);
		String[] sendPacket = assemblePackets(data, ndatabytes, n, k, pattern, ncheckbytes);
		return sendPacket;
	}
	
	public static String[] convertToBinary(String text, int ndatabytes) {
		int c;
		String[] data = new String[ndatabytes];
		Arrays.fill(data, "00000000");
		for(int i = 0 ;i < text.length(); i++) {
			c = text.charAt(i);
			data[i] = Integer.toBinaryString(c);
		}
		return data;
	}
	
	public static String[] assemblePackets(String[] data, int ndatabytes, int n, int k, int pattern, int ncheckbytes) {
		String[] packet = new String[data.length+1];
		packet[0] = Integer.toBinaryString(n);
		int i = 1; int j = 0;
		while(j < data.length) {
			packet[i] = data[j];
			i++;
			j++;
		}
		String checkSum = calculateCheckSum(packet, ndatabytes, k, pattern, ncheckbytes);
		
		String[] assembledPacket = generateNewPacket(packet, checkSum);
		
		return assembledPacket;
	}
	
	public static String[] applyOneTimeKey(String[] packet) {
		String randomStream = "1010011000101110011101010101011010001110";
		String[] K = generateOneTimeKey(randomStream, packet.length);
		String[] encryptedPacket = xorBits(packet, K);
		return encryptedPacket;
	}
	
//	public static String[] unPack(String[] packet) {
//		String randomStream = "1010011000101110011101010101011010001110";
//		String[] K = generateOneTimeKey(randomStream, packet.length);
//		String[] decryptedPacket = xorBits(packet, K);
//		return decryptedPacket;
//	}
	
	public static String[] generateNewPacket(String[] data, String checkSum) {
		String[] packet = new String[data.length + 1];
		int i = 0;
		while(i < packet.length-1) {
			packet[i] = data[i];
			i++;
		}
		packet[i] = checkSum;
		return packet;
	}
	
	private static String[] generateOneTimeKey(String randomStream, int requiredLength) {
		String[] K = new String[requiredLength];
		Arrays.fill(K, "00000000");
		StringBuilder sb = new StringBuilder(randomStream+randomStream);
		int start = 0; int end = 0;
		for (int i = 0; i < K.length; i++) {
			start = end;
			end = start + 8;
			if(end < sb.length()) {K[i] = sb.substring(start, end);}
			else break;
		}
		return K;
	}
	
	private static String[] xorBits(String[] packet, String[] K) {
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
	
	public static String calculateCheckSum(String[] data, int ndatabytes, int k, int pattern, int ncheckbytes) {
		int checkSum = getCheckSum(data, ndatabytes, pattern);
		checkSum *= k;
		String s = Integer.toBinaryString(checkSum);
		if (s.length() > 8 * ncheckbytes) {
			s = s.substring(s.length() - 8 * ncheckbytes);
		}
		return s;
	}
	
	public static int getCheckSum(String[] data, int ndatabytes, int pattern) {
		int checkSum = 0; int a,b, c;
		for(int i = 1; i <= ndatabytes; i++) {
			a = Integer.parseInt(data[i], 2);
			b = pattern;
			c = a & b;
			checkSum += c;
		}
		return checkSum;
	}
	public static String[] disassemblePacket(String[] packet, int k, int pattern, int ncheckbytes) {
		int ndatabytes = Integer.parseInt(packet[0], 2);
		String checkSum = calculateCheckSum(packet, ndatabytes, k, pattern, ncheckbytes);
		if(Integer.parseInt(checkSum, 2) == Integer.parseInt(packet[packet.length-1], 2)) {
			return packet;
		}
		return null;
	}
	

}
