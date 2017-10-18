package flair_solve;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Solve {
	
	
	public static String decrypt(String string2) throws UnsupportedEncodingException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        byte[] arrby = getKey(string2);
        byte[] arrby2 = "Initech_Security".getBytes("UTF-8");
        byte[] arrby3 = new byte[]{-38, 84, 11, -84, 45, -68, 94, -90, -125, 88, -83, -77, -12, -39, -57, 21, 107, -6, -22, 83, 96, 25, 15, 43, 40, -83, -76, -3, 49, 17, 60, 13, -35, 112, -58, -20, 53, -69, 34, -9, 60, 63, -127, 116, -19, 89, 63, -39};
        IvParameterSpec ivParameterSpec = new IvParameterSpec(arrby2);
        SecretKeySpec secretKeySpec = new SecretKeySpec(arrby, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(2, (Key)secretKeySpec, ivParameterSpec);
        return new String(cipher.doFinal(arrby3));
    }

	private static byte[] getKey(String string2) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeySpecException {
        PBEKeySpec pBEKeySpec = new PBEKeySpec(string2.toCharArray(), "NoSaltInTheMargarita".getBytes("UTF-8"), 1000, 192);
        return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(pBEKeySpec).getEncoded();
    }
	
	public static String getAllFlairPieces(String[] arrstring) throws IllegalAccessException {
        String string2 = "flair";
        for (String string3 : arrstring) {
            if (string3 == null) {
                throw new IllegalAccessException();
            }
            String string4 = string2 + "-";
            string2 = string4 + string3;
        }
        return string2;
    }

	private static String asdjfnhaxshcvhuw() {
        int n = 65535 & 0xffc0fefe;
        String string2 = "Fajitas"; //textView.getText().toString().split(" ")[4];
        String string3 = "hashtag"; //imageView.getTag().toString();
        
        Object[] arrobject = new Object[]{string3, "cov", n, string2};
        return String.format("%s_%s%x_%s!", arrobject);
    }

	static byte[] drdfg(String string2, int n) {
        byte[] arrby;
        try {
            byte[] arrby2;
            arrby = arrby2 = Base64.getDecoder().decode((byte[])string2.getBytes("UTF-8"));
        }
        catch (UnsupportedEncodingException var2_4) {
            var2_4.printStackTrace();
            arrby = null;
        }
        byte[] arrby3 = new byte[arrby.length];
        int n2 = 0;
        while (n2 < arrby.length) {
            arrby3[n2] = (byte)(n ^ arrby[n2]);
            ++n2;
        }
        return arrby3;
    }
	
	static byte[] poserw(byte[] arrby) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(new String(drdfg("JT43W0c=", 118), "UTF-8"));
        messageDigest.update(arrby);
        return messageDigest.digest();
    }
	
	static String vutfs(String string2, int n, String string3) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(string3.getBytes(), new String(drdfg("0cLTpA==", 144), "UTF-8"));
            Cipher cipher = Cipher.getInstance(new String(drdfg("BBcGcQ==", 69), "UTF-8"));
            cipher.init(2, secretKeySpec);
            String string4 = new String(cipher.doFinal(drdfg(string2, n)), "UTF-8");
            return string4;
        }
        catch (Exception var4_6) {
            var4_6.printStackTrace();
            return null;
        }
    }
	
	static byte[] neapucx(String string2) {
        int n = string2.length();
        if (n % 2 == 1) {
            return null;
        }
        byte[] arrby = new byte[n / 2];
        int n2 = 0;
        while (n2 < n) {
            arrby[n2 / 2] = (byte)((Character.digit(string2.charAt(n2), 16) << 4) + Character.digit(string2.charAt(n2 + 1), 16));
            n2 += 2;
        }
        return arrby;
    }

	private static byte[] loadData() throws Exception {
		DataInputStream in = new DataInputStream(new FileInputStream("..\\apktool\\assets\\tspe"));
		in.readLong();
		int size = in.readInt() / 3;
		byte[] dat = new byte[size];
		for (int i = 0; i < size; i++) {
			short pos = in.readShort();
			byte d = in.readByte();
			dat[pos] = d;
		}		
		
		in.close();
		return dat;
	}
	
	private static String toHex(byte[] arr) {
		String out = "";
		for (int i = 0; i < arr.length; i++) {
        	out += String.format("%02x", arr[i]);
        }
		return out;
	}
	
	public static byte[] getSha256Hash(String string2) {
        try {
            byte[] arrby = string2.getBytes("UTF-8");
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(arrby);
            return messageDigest.digest();
        }
        catch (UnsupportedEncodingException var2_4) {
            var2_4.printStackTrace();
            return null;
            
        }
        catch (NoSuchAlgorithmException var1_5) {
            var1_5.printStackTrace();
            return null;
        }
    }
	
	public static void main(String[] args) throws Exception {
		
		// Brian
		String brian = asdjfnhaxshcvhuw();
		System.out.println("Flair from Brian: " + brian);
		System.out.println();
		
		// Michael
		String two_letters = "";
		for (int i = 0; i < 256; i++) {
			two_letters = new String(new char[]{(char)i,(char)i});
			if (two_letters.hashCode() == 3040) {
				break; 
			}
		}
		String michael = "MYPRSHE" + two_letters + "FTW";
		System.out.println("Flair from Michael: " + michael);
		System.out.println();
				
		// Milton
		String milton = "";
			
		String hild = vutfs("JP+98sTB4Zt6q8g=", 56, "State") + vutfs("rh6HkuflHmw5Rw==", 96, "Chile")
					+ vutfs("+BNtTP/6", 118, "eagle") + vutfs("oLLoI7X/jIp2+w==", 33, "wind") 
					+ vutfs("w/MCnPD68xfjSCE=", 148, "river");
		
		System.out.println("Milton phrase: "+hild);

        milton = toHex(poserw(hild.getBytes("UTF-8")));
        System.out.println("Flair from Milton: " + milton);
		System.out.println();
        
		// Printer
		String printer = "";
		
		byte[] data = loadData();
		printer = toHex(poserw(data));
		
		System.out.println("Phrase from Printer: "+new String(data));
		System.out.println("Flair from Printer: "+printer);
		System.out.println();
		
		String[] flairs = new String[]{michael, brian, milton, printer};
		
		String allPieces = getAllFlairPieces(flairs);
		
		System.out.println("Combined flair pieces: " + allPieces); 
		System.out.println();
		System.out.println("Flair correct: "
				+ Arrays.equals(getSha256Hash(allPieces), 
						new byte[]{105, 30, -99, -14, 90, -80, 102, 125, -80, 66, -122, -18, 99, 73, 50, -28, -86, 32, -100, 26, 29, 85, 38, -113, 94, -110, -85, 67, -33, -108, -14, -34}));
		System.out.println();
		
		System.out.println("Solution: " + decrypt(allPieces));

	}

}
