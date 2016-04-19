import java.util.Scanner;

public class Run {

	public static final String Debug = "-d";
	public static final String Log="-l";

	public static int InstructionCounter=0;
	public static void main(String[] args) throws Exception {
//#####################variable
		String filename="";
		String option="";
		boolean debug=false;
		boolean onlyrun=false;
		boolean runlog=false;
		
		
//#####################class defination
		Memory memory = new Memory();	
		Register register =  new Register();
		FpRegister fpregister = new FpRegister();
		CPU cpu = new CPU(register,fpregister,memory);
//#####################handle args
		if(args.length==1)
		{
			filename=args[0];
			onlyrun=true;
		}
		else if(args.length==2)
		{
			option =  args[0];
			filename = args[1];
		}
		else
		{
			option =  args[0];
			filename = args[1];
		}		
//###################initialize		
		ReadElf readelf = new ReadElf(filename, memory);
		cpu.register.pc=readelf.getEntry();
	//	memory.initializePEntry(readelf.getEntry(),register);
	  //  register.setGP(readelf.getGp());   
              
		if(option.equals(Debug))
		{
			debug = true;
		}
		else if (option.equals(Log))
		{
			runlog=true;
		}
		
		while(cpu.STATE_RUN && onlyrun )
		{
			Instruction instruction = cpu.fetch();
			
			cpu.execute(instruction);
			InstructionCounter++;
		}
		
//#####################enter debug mode
		if(debug)
		{
			System.out.println("please input: ");
			System.out.println("\"n\": run one step");
			System.out.println("\"r\": run until program end");
			System.out.println("\"until 0xaddress\": run until pc = 0xaddress");
			System.out.println("\"reg\": print all info of register file");
			System.out.println("\"mem 0xaddress len\": print num of len memory data from address");
		}		
		while(cpu.STATE_RUN && debug)
		{
			System.out.print(":");
			Scanner scan = new Scanner(System.in);
			String doption = scan.next();
						
			if(doption.equals("n"))
			{				
				Instruction instruction = cpu.fetch();
    				System.out.format("PC: 0x%x ",cpu.register.pc);
				cpu.execute(instruction);
				cpu.printInstructionInformation();
				InstructionCounter++;
			}
			else if(doption.equals("r"))
			{
				while(cpu.STATE_RUN)
				{
					Instruction instruction = cpu.fetch();
					cpu.execute(instruction);
					InstructionCounter++;
				}
			}
			else if(doption.equals("until"))
			{
				int address = Integer.decode(scan.next());
				while(cpu.STATE_RUN && cpu.register.pc != address)
				{
					Instruction instruction = cpu.fetch();
					cpu.execute(instruction);
					InstructionCounter++;
				}
				
			}
			else if(doption.equals("reg"))
			{
				register.print();
			}
			else if(doption.equals("mem"))
			{
				int address = Integer.decode(scan.next());
				int len = scan.nextInt();
				for(int i=0;i<len;i++)
				{
					System.out.format("address: 0x%x, value: 0x%x\n",address,cpu.memory.getByte(address));
//					System.out.print("memory illegal info: ");
	//				cpu.memory.printlnDebug();
					address++;
				}
			}
			else if(doption.equals("symbol"))
			{
				readelf.printSymbols(cpu.memory);
			}
			else if(doption.equals("q"))
			{
				System.exit(0);
			}
		
		}
		
		while(cpu.STATE_RUN && runlog )
		{
			Instruction instruction = cpu.fetch();
			
    			System.out.format("PC: 0x%x ",cpu.register.pc);
			cpu.execute(instruction);
			cpu.printInstructionInformation();
			InstructionCounter++;
		}
		
		System.out.println("Total Num of Instructions: "+(InstructionCounter-1));
		
		//##########################################debug################################
	//	readelf.printSymbols(memory);
	//	System.out.format("0x0001a598: %x  0x0001a59c: %x 0x0001a5a0:%x",memory.getWord(0x1a598),memory.getWord(0x1a59c),memory.getWord(0x1a5a0));
	}
	
		
	


}
