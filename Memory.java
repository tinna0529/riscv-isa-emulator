import java.util.*;

public class Memory {

	byte[] mem;
	//byte[] mem1;
	public static final int MAX_NUM = 2009000001;
	String debug="";
	
	public Memory()
	{
		mem = new byte[MAX_NUM];
		//mem1 = new byte[1000000000];
	}
	public void initializePEntry(int Entry,Register register)
	{
		//jalr main
		register.register[10]=Entry;
		register.pc=4;
		putWord(4, 0x500e7);
		putWord(8, 0x7F);
	}
	
	public byte getByte(int address)
	{
		
		//return address > 2000000000 ? mem1[address - 2000000001] : mem[address];
		return mem[address];		
	}
	
	public short getHarfWord(int address)
	{
		    short result=0;
			byte b1=getByte(address);
			byte b2=getByte(address+1);
			result=Lib.halfWord(b1, b2);			
		    return result;		
	}
	
	public int getWord(int address)
	{
		int result=0;
			byte b1=getByte(address);
			byte b2=getByte(address+1);
			byte b3=getByte(address+2);
			byte b4=getByte(address+3);
			result=Lib.word(b1, b2, b3, b4);			
		return result;
	}
	public long getLongWord(int address)
	{
		long result=0;
			byte b1=getByte(address);
			byte b2=getByte(address+1);
			byte b3=getByte(address+2);
			byte b4=getByte(address+3);
			byte b5=getByte(address+4);
			byte b6=getByte(address+5);
			byte b7=getByte(address+6);
			byte b8=getByte(address+7);
			result=Lib.LongWord(b1, b2, b3, b4, b5, b6, b7, b8);			
		return result;
	}
	public float getFloatWord(int address)
	{
		float result=0;
		int temp=0;
			temp=getWord(address);
		result=Float.intBitsToFloat(temp);
		return result;
	}
	public double getDoubleWord(int address)
	{
		double result=0;
		long temp=0;
			temp=getLongWord(address);
		result=Double.longBitsToDouble(temp);
		return result;
	}
	
	public void putByte(int address,byte value)
	{
		//if(address > 2000000000)
		//{
		//	mem[address - 2000000001] = value;
		//}
		//else
		//{
			mem[address]=value;
		//}
	}
	public void putHarfWord(int address,short value)
	{
		byte[] result=Lib.HarfToByte(value);
		putByte(address, result[0]);
		putByte(address+1, result[1]);
	}
	public void putWord(int address,int value)
	{
		byte[] result=Lib.WordToByte(value);
		putByte(address, result[0]);
		putByte(address+1, result[1]);
		putByte(address+2, result[2]);
		putByte(address+3, result[3]);
	}
	public void putFloatWord(int address,float value)
	{
		int result=Float.floatToIntBits(value);
		putWord(address,result);
	}
	public void putLongWord(int address,long value)
	{
		byte[] result=Lib.LongWordToByte(value);
		putByte(address, result[0]);
		putByte(address+1, result[1]);
		putByte(address+2, result[2]);
		putByte(address+3, result[3]);
		putByte(address+4, result[4]);
		putByte(address+5, result[5]);
		putByte(address+6, result[6]);
		putByte(address+7, result[7]);
	}
	public void putDoubleWord(int address,double value)
	{
		long result=Double.doubleToLongBits(value);
		putLongWord(address,result);
	}
	public void printlnDebug()
	{
		System.out.println(debug);
	}
	
	
}
