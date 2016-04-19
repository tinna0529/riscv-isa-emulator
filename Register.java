
public class Register {
    
	int[] register;
	String[] regname={"zero","ra","sp","gp","tp","t0","t1","t2",
						"s0","s1","a0","a1","a2","a3","a4","a5",
						"a6","a7","s2","s3","s4","s5","s6","s7",
						"s8","s9","s10","s11","t3","t4","t5","t6"};
	int pc;
    
	public Register()
	{
		register = new int[32];
		register[2] = Memory.MAX_NUM-5;
	}
	
	public int get(int number)
	{
		return register[number];
	}
	
	public void set(int number,int value)
	{
		if(number != 0)
		{
			register[number]=value;
		}
	}
	public int getPC()
	{
		return pc;
	}
	public void setGP(int value)
	{
		register[3]=value;
	}
	public void print()
	{
		
		System.out.print("Register: "+"nextPc->");
		System.out.format("%x\n", pc);
		//System.out.format("#0:0x%x  ra:0x%x  sp:0x%x  gp:0x%x \n",register[0],register[1],register[2],register[3]);
		for(int i=0;i<32;i++)
		{
			System.out.format(regname[i]+":0x%x  ",register[i]);
			if(i==3)
				System.out.println();
			if(i==10)
				System.out.println();
			if(i==17)
				System.out.println();
			if(i==24)
				System.out.println();
		}
		System.out.println();
		
	}
}
