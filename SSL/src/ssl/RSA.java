package ssl;

import java.math.BigInteger;

public class RSA {
	
	static BigInteger[] publicKey = new BigInteger[2];
	static BigInteger[] privateKey = new BigInteger[2];
	static BigInteger n;

	public static void main(String[] args) {
		
		BigInteger[] keys = generateKeys();
		publicKey[0] = keys[0]; //e
		privateKey[0] = keys[1]; //d
		publicKey[1] = privateKey[1]= keys[2]; //n
		
		
		System.out.println("KR={"+privateKey[0]+"}");
		System.out.println("KU={"+publicKey[0]+"}");

		String message = args[0]; 
		
		System.out.println("KU(KR(M))="+message);
		
		byte[] m = message.getBytes();
		BigInteger t; BigInteger encrypted; BigInteger decrypted;
		StringBuilder originalMessage = new StringBuilder();
		
		for(int i = 0; i < m.length; i++) {
			t = new BigInteger(m[i]+"");
			encrypted = (t).modPow(publicKey[0], publicKey[1]);
			decrypted = (encrypted).modPow(privateKey[0], privateKey[1]);
			originalMessage.append((char) (decrypted.intValue()));
		}
		System.out.println("KR(KU(M))="+originalMessage);
	}
	
	public static BigInteger[] generateKeys() {
		BigInteger p = new BigInteger("573259433"); //49979687
		BigInteger q = new BigInteger("15485867"); //15485867
		BigInteger one = new BigInteger("1");
		BigInteger n = p.multiply(q);
		BigInteger t = (p.subtract(one)).multiply(q.subtract(one));
		BigInteger e = new BigInteger("1");
		for (int i = 2; i <= t.doubleValue(); i++) { 
			if(bigIntegerRelativelyPrime(i, t.intValue())){
				e = new BigInteger(i+"");
				break;
			}
	    } 
		BigInteger d = e.modInverse(t);
		BigInteger[] keys = {e, d, n};
		
		return keys;
		
	}
	
	static boolean bigIntegerRelativelyPrime(int a, int b) {
	    return BigInteger.valueOf(a).gcd(BigInteger.valueOf(b)).equals(BigInteger.ONE);
	}

}
