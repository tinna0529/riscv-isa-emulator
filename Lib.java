
public class Lib {
	   public static short halfWord(byte b1, byte b2)
	   {
		   
		   int res= (b1&0xff) + ((b2&0xff)<<8); 
		   return (short)res;
	   }

	   public static int word(byte b1, byte b2, byte b3, byte b4)
	   {
		   int res= (b1&0xff) + ((b2&0xff)<<8) + ((b3&0xff)<<16 ) + ((b4&0xff)<<24 );
		   return res;
	   }
	   public static long LongWord(byte b1, byte b2, byte b3, byte b4, byte b5, byte b6, byte b7, byte b8 )
	   {
		   long res=(b1&0xffL) + ((b2&0xffL)<<8) + ((b3&0xffL)<<16 ) + ((b4&0xffL)<<24) + ((b5&0xffL)<<32)+ ((b6&0xffL)<<40)+ ((b7&0xffL)<<48)+ ((b8&0xffL)<<56);
		   return res;
	   }
	   public static byte[] HarfToByte(short value)
	   {
		   byte[] result=new byte[2];
		   result[0]=(byte)value;
		   result[1]=(byte)(value >> 8);
		   return result;
	   }
	   public static byte[] WordToByte(int value)
	   {
		   byte[] result=new byte[4];
		   result[0]=(byte)value;
		   result[1]=(byte)(value >> 8);
		   result[2]=(byte)(value >> 16);
		   result[3]=(byte)(value >> 24);
		   return result;
	   }
	   public static byte[] LongWordToByte(long value)
	   {
		   byte[] result=new byte[8];
		   result[0]=(byte)value;
		   result[1]=(byte)(value >> 8);
		   result[2]=(byte)(value >> 16);
		   result[3]=(byte)(value >> 24);
		   result[4]=(byte)(value >> 32);
		   result[5]=(byte)(value >> 40);
		   result[6]=(byte)(value >> 48);
		   result[7]=(byte)(value >> 56);
		   return result;
	   }
		public static long toUnsignedInt(int x)
		{
			return x&0xFFFFFFFFL;
		}
}
