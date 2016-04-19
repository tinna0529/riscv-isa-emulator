import java.io.*;
import java.util.*;
public class CPU {
	int nextPC;
	String insnInfo="";
	String insnInfo2="";
	Register register;
	Memory memory;
	FpRegister fpregister;
	Syscall mysyscall;
	
	public static boolean STATE_RUN=true;
	public static final int CPU_FREQ=1;	
	public static final int OP_IMM=0x13;
	public static final int LUI=0x37;
	public static final int AUIPC=0x17;
	public static final int JAL=0x6F;
	public static final int JALR=0x67;
	public static final int BRANCH=0x63;
	public static final int LOAD=0x3;
	public static final int FDLOAD=0x7;
	public static final int STORE=0x23;
	public static final int FDSTORE=0x27;
	public static final int R_COMPUTE=0x33;
	public static final int HALT=0x7F;
	public static final int SCALL=0x73;
	
	
	public CPU(Register register,FpRegister fpregister,Memory memory)
	{
		this.register=register;
		this.memory=memory;
		this.fpregister=fpregister;
		this.mysyscall=new Syscall(memory,register);
	}
	
	public Instruction fetch()
	{
		Instruction instruction =  new Instruction();
		instruction.setVal(memory.getWord(register.pc));
		//modify nextPC
		nextPC=register.pc+4;
		return instruction;
	}
	public boolean execute(Instruction instruction)
	{
		
		
		switch(instruction.opcode())
		{
		case OP_IMM:
			switch(instruction.func3())
			{
			case 0://ADDI
			{
				int imm12=instruction.imm12();
				int rs1Val=register.get(instruction.rs1Number());
				register.set(instruction.rdNumber(), imm12+rs1Val);
				
				//test////////////////////////////////////////////////////
				insnInfo="addi "+register.regname[instruction.rdNumber()]+" "+register.regname[instruction.rs1Number()]+" "+imm12;
				
				break;
			}
			case 1://SLLI
			{
				int value=register.get(instruction.rs1Number())<<instruction.shamt5();
				register.set(instruction.rdNumber(), value);
				
				//test///////////////////////////////////////////////////////
				insnInfo="slli "+register.regname[instruction.rdNumber()]+" "+register.regname[instruction.rs1Number()]+" "+instruction.shamt5();
				break;
			}
			case 2://SLTI
			{
				int rs1Val=register.get(instruction.rs1Number());
				int imm12=instruction.imm12();
				register.set(instruction.rdNumber(), rs1Val<imm12? 1:0);
				
				insnInfo="slti "+register.regname[instruction.rdNumber()]+" "+register.regname[instruction.rs1Number()]+" "+imm12;
				break;
			}
			case 3://SLTIU
			{
				int rs1Val=register.get(instruction.rs1Number());
				int imm12=instruction.imm12();
				register.set(instruction.rdNumber(), Lib.toUnsignedInt(rs1Val)<Lib.toUnsignedInt(imm12)? 1:0 );
				
				insnInfo="sltiu "+register.regname[instruction.rdNumber()]+" "+register.regname[instruction.rs1Number()]+" "+Lib.toUnsignedInt(imm12);
				
				break;
			}
			case 4://XORI
			{
				int rs1Val=register.get(instruction.rs1Number());
				int imm12=instruction.imm12();
				register.set(instruction.rdNumber(), rs1Val ^ imm12);
				
				insnInfo="xori "+register.regname[instruction.rdNumber()]+" "+register.regname[instruction.rs1Number()]+" "+imm12;
				break;
			}
			case 5:// SRLI/SRAI 
			{
				switch(instruction.imm7())
				{
				case 0://SRLI
				{
					int value=register.get(instruction.rs1Number()) >>> instruction.shamt5();
					register.set(instruction.rdNumber(), value);	
					
					insnInfo="srli "+register.regname[instruction.rdNumber()]+" "+register.regname[instruction.rs1Number()]+" "+instruction.shamt5();
					break;
				}
				case 32://SRAI
				{
					int value=register.get(instruction.rs1Number()) >> instruction.shamt5();
					register.set(instruction.rdNumber(), value);	
					
					insnInfo="srai "+register.regname[instruction.rdNumber()]+" "+register.regname[instruction.rs1Number()]+" "+instruction.shamt5();
					break;
				}
				default:
				{
					insnInfo="Unkown instruction!!!!Shit!!!";
					register.pc=nextPC;
					return false;
				}
				}
				break;
			}
			case 6://ORI
			{
				int rs1Val=register.get(instruction.rs1Number());
				int imm12=instruction.imm12();
				register.set(instruction.rdNumber(), rs1Val | imm12);	
				
				insnInfo="ori "+register.regname[instruction.rdNumber()]+" "+register.regname[instruction.rs1Number()]+" "+imm12;
				break;
			}
			case 7://ANDI
			{
				int rs1Val=register.get(instruction.rs1Number());
				int imm12=instruction.imm12();
				register.set(instruction.rdNumber(), rs1Val & imm12);	
				
				insnInfo="andi "+register.regname[instruction.rdNumber()]+" "+register.regname[instruction.rs1Number()]+" "+imm12;
				break;
			}
			default:
			{
				insnInfo="Unkown instruction!!!!Shit!!!BBBBBBB";
				register.pc=nextPC;
				return false;			
			}
			}
			break;
		    case LUI:
		    {
		    	int imm20=instruction.u_imm20();
		    	int value=imm20 << 12;
		    	register.set(instruction.rdNumber(), value);
		    	
		    	insnInfo="lui "+register.regname[instruction.rdNumber()]+" "+Integer.toHexString(value);
			    break;
		    }
		    case AUIPC:
		    {
		    	int imm20=instruction.u_imm20();
		    	int offset=imm20 << 12;
		    	int value=register.pc + offset;
		    	register.set(instruction.rdNumber(), value);
		    	
		    	insnInfo="luipc "+register.regname[instruction.rdNumber()]+" "+Integer.toHexString(imm20);
		    	break;
		    }
		    case JAL:
		    {
		    	int offset21=instruction.offset21();
		    	nextPC=register.pc + offset21;
		    	register.set(instruction.rdNumber(), register.pc+4);
		    	
		    	insnInfo="jal "+register.regname[instruction.rdNumber()]+" "+Integer.toHexString(nextPC);
		    	break;
		    }
		    case JALR:
		    {
		    	int imm12=instruction.imm12();
		    	int rs1Val=register.get(instruction.rs1Number());
		    	nextPC= (rs1Val + imm12) & 0xFFFFFFFE;
		    	register.set(instruction.rdNumber(), register.pc+4);
		    	
		    	insnInfo="jalr "+register.regname[instruction.rdNumber()]+" "+register.regname[instruction.rs1Number()]+" "+Integer.toHexString(imm12);
		    	break;
		    }
		    case BRANCH:
		    {
		    	int target_address=instruction.offset13()+register.pc;
				int rs1Val=register.get(instruction.rs1Number());
				int rs2Val=register.get(instruction.rs2Number());
		    	switch(instruction.func3())
		    	{
		    		case 0://BEQ
		    		{
		    			if(rs1Val == rs2Val)
		    				nextPC=target_address;
		    			insnInfo="beq "+register.regname[instruction.rs1Number()]+" "+register.regname[instruction.rs2Number()]+" "+Integer.toHexString(target_address);
		    			break;
		    		}
		    		case 1://BNE
		    		{
		    			if(rs1Val != rs2Val)
		    				nextPC=target_address;
		    			
		    			insnInfo="bne "+register.regname[instruction.rs1Number()]+" "+register.regname[instruction.rs2Number()]+" "+Integer.toHexString(target_address);
		    			break;
		    		}
		    		case 4://BLT
		    		{
		    			if(rs1Val < rs2Val)
		    				nextPC=target_address;
		    			
		    			insnInfo="blt "+register.regname[instruction.rs1Number()]+" "+register.regname[instruction.rs2Number()]+" "+Integer.toHexString(target_address);
		    			break;
		    		}
		    		case 5://BGE
		    		{
		    			if(rs1Val >= rs2Val)
		    				nextPC=target_address;
		    			
		    			insnInfo="bge "+register.regname[instruction.rs1Number()]+" "+register.regname[instruction.rs2Number()]+" "+Integer.toHexString(target_address);
		    			break;
		    		}
		    		case 6://BLTU
		    		{
		    			if(Lib.toUnsignedInt(rs1Val) < Lib.toUnsignedInt(rs2Val))
		    				nextPC=target_address;
		    			
		    			insnInfo="bltu "+register.regname[instruction.rs1Number()]+" "+register.regname[instruction.rs2Number()]+" "+Integer.toHexString(target_address);
		    			break;
		    		}
		    		case 7://BGEU
		    		{
		    			if(Lib.toUnsignedInt(rs1Val) >= Lib.toUnsignedInt(rs2Val))
		    				nextPC=target_address;
		    			
		    			insnInfo="bgeu "+register.regname[instruction.rs1Number()]+" "+register.regname[instruction.rs2Number()]+" "+Integer.toHexString(target_address);
		    			break;
		    		}
		    		default:
		    		{
		    			insnInfo="Unkown Instruction!!!!!CCCCCCCC";
					register.pc=nextPC;
		    			return false;
		    		}
		    	}
		    	
		    	break;
		    }
		    case LOAD:
		    {
		    	int rs1Val=register.get(instruction.rs1Number());
		    	int address=instruction.imm12()+ rs1Val;
		    	switch(instruction.func3())
		    	{
		    		case 0://LB
		    		{
		    			byte data=memory.getByte(address);
		    			int value= (int)data;
		    			register.set(instruction.rdNumber(), value);
		    			
		    			///////////////////test//////////////////////
		    			insnInfo="lb "+register.regname[instruction.rdNumber()]+" "+instruction.imm12()+"("+register.regname[instruction.rs1Number()]+")";
		    			insnInfo2="addr "+Integer.toHexString(address)+" value "+Integer.toHexString(memory.getByte(address));	
		    			break;
		    		}
		    		case 1://LH
		    		{
		    			short data=memory.getHarfWord(address);
		    			int value= (int)data ;
		    			register.set(instruction.rdNumber(), value);
		    			
		    			///////////////////test//////////////////////
		    			insnInfo="lh "+register.regname[instruction.rdNumber()]+" "+instruction.imm12()+"("+register.regname[instruction.rs1Number()]+")";		    			
		    			insnInfo2="addr "+Integer.toHexString(address)+" value "+Integer.toHexString(memory.getHarfWord(address));	
		    			
		    			break;
		    		}
		    		case 2://LW
		    		{
		    			int value=memory.getWord(address);
		    			register.set(instruction.rdNumber(), value);
		    			
		    			///////////////////test//////////////////////
		    			insnInfo="lw "+register.regname[instruction.rdNumber()]+" "+instruction.imm12()+"("+register.regname[instruction.rs1Number()]+")";		    			
		    			insnInfo2="addr "+Integer.toHexString(address)+" value "+Integer.toHexString(memory.getWord(address));	
		    			
		    			break;
		    		}
		    		case 4://LBU
		    		{
		    			byte data=memory.getByte(address);
		    			int value=data & 0x0FF;
		    			register.set(instruction.rdNumber(), value);
		    			
		    			///////////////////test//////////////////////
		    			insnInfo="lbu "+register.regname[instruction.rdNumber()]+" "+instruction.imm12()+"("+register.regname[instruction.rs1Number()]+")" ;		    			
		    			insnInfo2="addr "+Integer.toHexString(address)+" value "+Integer.toHexString(memory.getByte(address));	
		    			
		    			break;
		    		}
		    		case 5://LHU
		    		{
		    			short data=memory.getHarfWord(address);
		    			int value= data & 0x0FFFF;
		    			register.set(instruction.rdNumber(), value);
		    			
		    			///////////////////test//////////////////////
		    			insnInfo="lhu "+register.regname[instruction.rdNumber()]+" "+instruction.imm12()+"("+register.regname[instruction.rs1Number()]+")" ;	    			
		    			insnInfo2="addr "+Integer.toHexString(address)+" value "+Integer.toHexString(memory.getHarfWord(address));	
		    			
		    			break;
		    		}
		    		default:
		    		{
		    			insnInfo="Illegal instruction!!!!!DDDDDDDDDDDD";
					register.pc=nextPC;
		    			return false;
		    		}
		    	}
		    	break;
		    }
		    case STORE:
		    {
		    	int rs1Val=register.get(instruction.rs1Number());
		    	int value=register.get(instruction.rs2Number());
		    	int address=instruction.offset12()+ rs1Val;		    	
		    	switch(instruction.func3())
		    	{
		    		case 0://SB
		    		{
		    			memory.putByte(address, (byte)value);
		    			
		    			//################test################
		    			insnInfo="sb "+register.regname[instruction.rs2Number()]+" "+instruction.offset12()+"("+register.regname[instruction.rs1Number()]+")";
		    			insnInfo2="addr "+Integer.toHexString(address)+" value "+Integer.toHexString(memory.getByte(address));
		    			break;
		    		}
		    		case 1://SH
		    		{
		    			memory.putHarfWord(address, (short)value);
		    			
		    			insnInfo="sh "+register.regname[instruction.rs2Number()]+" "+instruction.offset12()+"("+register.regname[instruction.rs1Number()]+")";
		    			insnInfo2="addr "+Integer.toHexString(address)+" value "+Integer.toHexString(memory.getHarfWord(address));   
		    			break;
		    		}
		    		case 2://SW
		    		{
		    			memory.putWord(address, value);
		    			
		    			//test/////////////////////////////////////////
		    			insnInfo="sw "+register.regname[instruction.rs2Number()]+" "+instruction.offset12()+"("+register.regname[instruction.rs1Number()]+")";
		    			insnInfo2="addr "+Integer.toHexString(address)+" value "+Integer.toHexString(memory.getWord(address));   
		    			break;
		    		}
		    		default:
		    		{
		    			insnInfo="Wrong instruction !!! GOD!!!EEEEEEEEEEEEEE";
					register.pc=nextPC;
		    			return false;
		    		}
		    	}
		    	break;
		    }
		    case R_COMPUTE:
		    {
		    	switch(instruction.func3())
		    	{
		    		case 0:
		    		{
		    			switch(instruction.func7())
		    			{
		    				case 0://ADD
		    				{
		    					int rs1Val=register.get(instruction.rs1Number());
		    					int rs2Val=register.get(instruction.rs2Number());
		    					register.set(instruction.rdNumber(), rs1Val+rs2Val);
		    					
		    					insnInfo="add "+register.regname[instruction.rdNumber()]+" "+register.regname[instruction.rs1Number()]+" "+register.regname[instruction.rs2Number()];
		    					break;
		    				}
						case 1:
						{
		    					int rs1Val=register.get(instruction.rs1Number());
		    					int rs2Val=register.get(instruction.rs2Number());
		    					register.set(instruction.rdNumber(), rs1Val*rs2Val);
		    					
		    					insnInfo="mul "+register.regname[instruction.rdNumber()]+" "+register.regname[instruction.rs1Number()]+" "+register.regname[instruction.rs2Number()];
							break;	
						}	
		    				case 32://SUB
		    				{
		    					int rs1Val=register.get(instruction.rs1Number());
		    					int rs2Val=register.get(instruction.rs2Number());
		    					register.set(instruction.rdNumber(), rs1Val-rs2Val);	
		    					
		    					insnInfo="sub "+register.regname[instruction.rdNumber()]+" "+register.regname[instruction.rs1Number()]+" "+register.regname[instruction.rs2Number()];
		    					break;
		    				}
		    				default:
		    				{
		    					insnInfo="Illegal instruction !!!FFFFFFFFFFFF";
							register.pc=nextPC;
		    					return false;
		    				}
		    			}
		    			break;
		    		}
		    		case 1:
		    		{
		    			int rs1Val=register.get(instruction.rs1Number());
		    			int rs2Val=register.get(instruction.rs2Number());
		    			switch(instruction.func7())
		    			{
		    				case 0://SLL
		    				{
			    				int value=rs1Val << (rs2Val & 0x01F);
			    				register.set(instruction.rdNumber(), value);
		    			
			    				insnInfo="sll "+register.regname[instruction.rdNumber()]+" "+register.regname[instruction.rs1Number()]+" "+register.regname[instruction.rs2Number()];
			    				break;
		    				}
		    				case 1://mulh
		    				{
		    					register.set(instruction.rdNumber(), (int) ( ((long)rs1Val*(long)rs2Val) >> 32 ) );
		    					insnInfo="mulh "+register.regname[instruction.rdNumber()]+" "+register.regname[instruction.rs1Number()]+" "+register.regname[instruction.rs2Number()];
			    				break;
							
		    				}	
		    				default:
		    				{
		    					insnInfo="Illegal instruction !!!FFFFFFFFFFFF";
							register.pc=nextPC;
		    					return false;
		    				}
		    			}
		    			break;
		    		}
		    		case 2:
		    		{
		    			int rs1Val=register.get(instruction.rs1Number());
		    			int rs2Val=register.get(instruction.rs2Number());
		    			switch(instruction.func7())
		    			{
		    				case 0://SLT
		    				{
				    			register.set(instruction.rdNumber(), rs1Val < rs2Val ? 1:0);
				    			
				    			insnInfo="slt "+register.regname[instruction.rdNumber()]+" "+register.regname[instruction.rs1Number()]+" "+register.regname[instruction.rs2Number()];
				    			break;
		    				}
		    				case 1://MULHSU
		    				{
		    					register.set(instruction.rdNumber(), (int) ( ((long)rs1Val*Lib.toUnsignedInt(rs2Val)) >> 32 ) );
		    					insnInfo="mulhsu "+register.regname[instruction.rdNumber()]+" "+register.regname[instruction.rs1Number()]+" "+register.regname[instruction.rs2Number()];
			    				break;
							
		    				}	
		    				default:
		    				{
		    					insnInfo="Illegal instruction !!!FFFFFFFFFFFF";
							register.pc=nextPC;
		    					return false;
		    				}
		    			}
		    			break;
		    		}
		    		case 3://SLTU
		    		{
		    			int rs1Val=register.get(instruction.rs1Number());
		    			int rs2Val=register.get(instruction.rs2Number());
		    			switch(instruction.func7())
		    			{
		    				case 0://SLTU
		    				{
		    					register.set(instruction.rdNumber(), Lib.toUnsignedInt(rs1Val) < Lib.toUnsignedInt(rs2Val) ? 1:0);
				    			
		    					insnInfo="sltu "+register.regname[instruction.rdNumber()]+" "+register.regname[instruction.rs1Number()]+" "+register.regname[instruction.rs2Number()];
				    			break;
		    				}
		    				case 1://MULHU
		    				{
		    					register.set(instruction.rdNumber(), (int) ( (Lib.toUnsignedInt(rs1Val)*Lib.toUnsignedInt(rs2Val)) >> 32 ) );
		    					insnInfo="mulhu "+register.regname[instruction.rdNumber()]+" "+register.regname[instruction.rs1Number()]+" "+register.regname[instruction.rs2Number()];
			    				break;
							
		    				}	
		    				default:
		    				{
		    					insnInfo="Illegal instruction !!!FFFFFFFFFFFF";
							register.pc=nextPC;
		    					return false;
		    				}
		    			}
		    			break;
    		
		    		}
		    		case 4:
		    		{
		    			int rs1Val=register.get(instruction.rs1Number());
		    			int rs2Val=register.get(instruction.rs2Number());
		    			switch(instruction.func7())
		    			{
		    				case 0://XOR
		    				{
		    					register.set(instruction.rdNumber(), rs1Val ^ rs2Val);
				    			
		    					insnInfo="xor "+register.regname[instruction.rdNumber()]+" "+register.regname[instruction.rs1Number()]+" "+register.regname[instruction.rs2Number()];
				    			break;
		    				}
		    				case 1://DIV
		    				{
		    					register.set(instruction.rdNumber(), rs1Val/rs2Val );
		    					insnInfo="div "+register.regname[instruction.rdNumber()]+" "+register.regname[instruction.rs1Number()]+" "+register.regname[instruction.rs2Number()];
			    				break;				
		    				}	
		    				default:
		    				{
		    					insnInfo="Illegal instruction !!!FFFFFFFFFFFF";
							register.pc=nextPC;
		    					return false;
		    				}
		    			}
		    			break;
 
		    		}
		    		case 5:
		    		{
		    			switch(instruction.func7())
		    			{
		    				case 0://SRL
		    				{
				    			int rs1Val=register.get(instruction.rs1Number());
				    			int rs2Val=register.get(instruction.rs2Number());
				    			int value=rs1Val >>> (rs2Val & 0x01F);
				    			register.set(instruction.rdNumber(), value);
				    			
				    			insnInfo="srl "+register.regname[instruction.rdNumber()]+" "+register.regname[instruction.rs1Number()]+" "+register.regname[instruction.rs2Number()];
		    					break;
		    				}
		    				case 1:
		    				{
		    					int rs1Val=register.get(instruction.rs1Number());
				    			int rs2Val=register.get(instruction.rs2Number());
		    					register.set(instruction.rdNumber(), (int)(Lib.toUnsignedInt(rs1Val)/Lib.toUnsignedInt(rs2Val)) );
		    					insnInfo="divu "+register.regname[instruction.rdNumber()]+" "+register.regname[instruction.rs1Number()]+" "+register.regname[instruction.rs2Number()];
			    				break;	
		    				}
		    				case 32://SRA
		    				{
				    			int rs1Val=register.get(instruction.rs1Number());
				    			int rs2Val=register.get(instruction.rs2Number());
				    			int value=rs1Val >> (rs2Val & 0x01F);
				    			register.set(instruction.rdNumber(), value);
				    			
				    			insnInfo="sra "+register.regname[instruction.rdNumber()]+" "+register.regname[instruction.rs1Number()]+" "+register.regname[instruction.rs2Number()];
		    					break;
		    				}
		    				default:
		    				{
		    					insnInfo="Unkown instruction!!!!GGGGGGGGGGG";
							register.pc=nextPC;
		    					return false;
		    				}
		    			}
		    			break;
		    		}
		    		case 6:
		    		{
		     			int rs1Val=register.get(instruction.rs1Number());
		    			int rs2Val=register.get(instruction.rs2Number());
		    			switch(instruction.func7())
		    			{
		    				case 0://OR
		    				{
		    		   			register.set(instruction.rdNumber(), rs1Val | rs2Val);	
				    			
		    		   			insnInfo="or "+register.regname[instruction.rdNumber()]+" "+register.regname[instruction.rs1Number()]+" "+register.regname[instruction.rs2Number()];
				    			break;
		    				}
		    				case 1://rem
		    				{
		    					register.set(instruction.rdNumber(), rs1Val%rs2Val );
		    					insnInfo="rem "+register.regname[instruction.rdNumber()]+" "+register.regname[instruction.rs1Number()]+" "+register.regname[instruction.rs2Number()];
			    				break;				
		    				}	
		    				default:
		    				{
		    					insnInfo="Illegal instruction !!!FFFFFFFFFFFF";
							register.pc=nextPC;
		    					return false;
		    				}
		    			}
		    			break;
		    		}
		    		case 7:
		    		{
		    			int rs1Val=register.get(instruction.rs1Number());
		    			int rs2Val=register.get(instruction.rs2Number());
		    			switch(instruction.func7())
		    			{
		    				case 0://and
		    				{
		    					register.set(instruction.rdNumber(), rs1Val & rs2Val);	
				    			
		    					insnInfo="and "+register.regname[instruction.rdNumber()]+" "+register.regname[instruction.rs1Number()]+" "+register.regname[instruction.rs2Number()];
				    			break;
		    				}
		    				case 1://remu
		    				{
		    					register.set(instruction.rdNumber(), (int)(Lib.toUnsignedInt(rs1Val)%Lib.toUnsignedInt(rs1Val)));
		    					insnInfo="remu "+register.regname[instruction.rdNumber()]+" "+register.regname[instruction.rs1Number()]+" "+register.regname[instruction.rs2Number()];
			    				break;				
		    				}	
		    				default:
		    				{
		    					insnInfo="Illegal instruction !!!FFFFFFFFFFFF";
							register.pc=nextPC;
		    					return false;
		    				}
		    			}
		    			break;
		    		}
		    		default:
		    		{
		    			insnInfo="UnKown instruction!!! Shit!!!!HHHHHHHHHHHH";
					register.pc=nextPC;
		    			return false;
		    		}
		    	}
		    	break;
		    }
		    case SCALL:
		    {
			mysyscall.do_syscall(register.get(10),register.get(11),register.get(12),register.get(13),
							register.get(14),register.get(15),register.get(16),register.get(17));
				insnInfo="ecall "+register.get(17);
		    	//return false;
		    	break;
		    }
		    case FDLOAD:
		    {
		    	int rs1Val=register.get(instruction.rs1Number());
		    	int address=instruction.imm12()+ rs1Val;
		    	switch(instruction.func3())
		    	{

		    		case 3://FLD
		    		{
		    			double value=memory.getDoubleWord(address);
		    			fpregister.set(instruction.rdNumber(), value);
		    			insnInfo="fld #f"+instruction.rdNumber()+" "+instruction.imm12()+"("+register.regname[instruction.rs1Number()]+")";
		    			break;
		    		}
		    		case 2://FLW
		    		{
		    			float value=memory.getFloatWord(address);
		    			fpregister.setFloat(instruction.rdNumber(),value);
		    			insnInfo="flw #f"+instruction.rdNumber()+" "+instruction.imm12()+"("+register.regname[instruction.rs1Number()]+")";
		    			break;
		    		}
		    		
		    	}
		    	break;
		    }
		    case FDSTORE:
		    {
		    	int rs1Val=register.get(instruction.rs1Number());
		    	int address=instruction.offset12()+ rs1Val;		
		    	switch(instruction.func3())
		    	{
		    		case 3://FSD
		    		{
		    			double value=fpregister.get(instruction.rs2Number());
		    			memory.putDoubleWord(address, value);
		    			insnInfo="fsd #f"+instruction.rs2Number()+" "+instruction.offset12()+"("+register.regname[instruction.rs1Number()]+")";
		    			break;
		    		}
		    		case 2://FSW
		    		{
		    			float value=fpregister.getFloat(instruction.rs2Number());
		    			memory.putFloatWord(address, value);;
		    			insnInfo="fsw #f"+instruction.rs2Number()+" "+instruction.offset12()+"("+register.regname[instruction.rs1Number()]+")";
		    			break;
		    		}
		    	}
		    	break;
		    }
		    case HALT:
		    {
		    	insnInfo="System halt";
		    	return false;
		    }
		    default:
		    {
		    	insnInfo="Unkown Instruction!!!Shit!!!!IIIIIIIIIII";
			register.pc=nextPC;
		    	return false;
		    }
		}
		
		//modify PC
		register.pc=nextPC;
		return true;
	}
    public void printInstructionInformation()
    {
    	System.out.println(insnInfo);
    //	System.out.println(insnInfo2);
    }
}
