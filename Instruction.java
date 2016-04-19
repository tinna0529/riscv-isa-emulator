
public class Instruction {
    int val;
	public Instruction()
	{
		val=0;
	}
	public void setVal(int value)
	{
		val=value;
	}
	public int getVal()
	{
		return val;
	}
	public int opcode()
	{
		return val&0x07F;
	}
	public int func3()
	{
		return (val>>12)&0x07;
	}
	public int rdNumber()
	{
		return (val>>7)&0x01F;
	}
	public int rs1Number()
	{
		return (val>>15)& 0x01F;
	}
	public int rs2Number()
	{
		return (val>>20) & 0x01F;
	}
	public int func7()
	{
		return (val >> 25) & 0x07F;
	}
	public int imm12()//signed-extended number of 12bit data
	{
		return val>>20;
	}
	public int imm7()//unsigned-extended number of upper 7bit data
	{
		return (val>>25)&0x07F;
	}
	public int u_imm20()//sign-extended number of upper 20bit data
	{
		return val>>12;
	}
	public int shamt5()//get shamt5
	{
		return (val>>20)&0x01F;
	}
	public int offset21()
	{
		int imm20=val >> 31;
		int imm19_12=(val >> 12) & 0x0FF;
		int imm20_12= (imm20 << 8) | imm19_12;
		int imm11=(val >> 20) & 0x1;
		int imm20_11= (imm20_12 << 1) | imm11;
		int imm10_1=(val >> 21) & 0x03FF;
		int imm20_1=(imm20_11 << 10) | imm10_1;
		int imm20_0= imm20_1 << 1;
		return imm20_0;
	}
	public int offset13()
	{
		int imm12 =  val >> 31;
		int imm11 =  (val >> 7) & 0x1;
		int imm12_11 = (imm12 << 1) | imm11;
		int imm10_5 = (val >> 25) & 0x03F;
		int imm12_5= (imm12_11 << 6) | imm10_5;
		int imm4_1=(val >> 8) & 0x0F;
		int imm12_1=(imm12_5 << 4) | imm4_1;
		int imm12_0=imm12_1 << 1;
		return imm12_0;
	}
	public int offset12()
	{
		int imm11_5= val >> 25;
		int imm4_0=(val >> 7)& 0x01F;
		int imm11_0= (imm11_5 << 5) | imm4_0;
		return imm11_0;
		
	}

}