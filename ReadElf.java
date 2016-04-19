//********************************************************************
//  ReadElf.java       Author: Tinna
//
//  Test Elf class.
//********************************************************************
import java.io.*;

public class ReadElf
{
   public static final int ET_EXEC=0x2;
   public static final int P_LOAD=0x1;
   public static final int SHT_SYMBOL=0x2;
   public static final int SHT_STRTAB=0x3;
   public static final int STT_OBJECT=0x1;
   Elf myelf;
   
   public ReadElf(String filename,Memory memory) throws IOException
   {
	   //read elf32
	byte[] cont=readFile(filename);	
	if(cont[0]==0x7f && cont[1]==0x45 && cont[2]==0x4c && cont[3]==0x46 && cont[4]==0x01 && cont[16]==ET_EXEC)
	{
		System.out.println("This is a excutable elf32 file\n");
		myelf=new Elf(word(cont[28],cont[29],cont[30],cont[31]),
			halfWord(cont[42],cont[43]),halfWord(cont[44],cont[45]),
			word(cont[24],cont[25],cont[26],cont[27]),
			word(cont[32],cont[33],cont[34],cont[35]),
			halfWord(cont[46],cont[47]),halfWord(cont[48],cont[49]),
			halfWord(cont[50],cont[51]));
			//load sh to myelf and shstr[]
		int moff=myelf.get_shoff();
		int size=myelf.get_shsize();
		int off=moff;
			
		off=moff;
		for(int i=0; i<myelf.get_shnum();i++)
		{
			off=moff+size*i;
				
			//load sh_sym to myelf
			if(word(cont[off+4],cont[off+5],cont[off+6],cont[off+7])==SHT_SYMBOL)
			{
				Elf_Sh sh_symbol=new Elf_Sh(word(cont[off],cont[off+1],cont[off+2],cont[off+3]),
							word(cont[off+12],cont[off+13],cont[off+14],cont[off+15]),
							word(cont[off+16],cont[off+17],cont[off+18],cont[off+19]),
							word(cont[off+20],cont[off+21],cont[off+22],cont[off+23]),
							word(cont[off+32],cont[off+33],cont[off+34],cont[off+35]));
									
				myelf.add_sh(sh_symbol);
					
				int symlen=sh_symbol.get_size();
				int symoff=sh_symbol.get_offset();
				myelf.symbol=new byte[symlen];
					
				for(int symi=0; symi<symlen; symi++)
				{
					myelf.symbol[symi]=cont[symoff];
					symoff++;
				}
					
			}
			//load sh_str to myelf
			else if((word(cont[off+4],cont[off+5],cont[off+6],cont[off+7])==SHT_STRTAB )
						&&(i!= myelf.get_shstr()) )
			{
				Elf_Sh sh_str=new Elf_Sh(word(cont[off],cont[off+1],cont[off+2],cont[off+3]),
							word(cont[off+12],cont[off+13],cont[off+14],cont[off+15]),
							word(cont[off+16],cont[off+17],cont[off+18],cont[off+19]),
							word(cont[off+20],cont[off+21],cont[off+22],cont[off+23]),
							word(cont[off+32],cont[off+33],cont[off+34],cont[off+35]));
				myelf.add_sh(sh_str);
				int strlen=sh_str.get_size();
				int stroff=sh_str.get_offset();
				myelf.str=new byte[strlen];
				for(int stri=0; stri<strlen; stri++)
				{
					myelf.str[stri]=cont[stroff];
					stroff++;
				}
			}
		}
		//get gp from str to check symbol
		int symnum=(myelf.symbol.length)/16;
		for(int i=0;i<symnum; i++)
		{
			off=16*i;
			int index=word(myelf.symbol[off],myelf.symbol[off+1],myelf.symbol[off+2],myelf.symbol[off+3]);
			if((index+3<=myelf.str.length)&&myelf.str[index]=='_'&&
				myelf.str[index+1]=='g'&&myelf.str[index+2]=='p'&&myelf.str[index+3]=='\0')
			{
				myelf.set_gp(word(myelf.symbol[off+4],myelf.symbol[off+5],myelf.symbol[off+6],myelf.symbol[off+7]));
			}
			
		}
			
		//load ph to myelf and memory
		moff=myelf.get_phoff();
		size=myelf.get_phsize();
		off=moff;
		for(int i=0; i<myelf.get_phnum(); i++)
		{
			if(word(cont[off],cont[off+1],cont[off+2],cont[off+3])==P_LOAD)
			{
				off=moff+size*i;
				Elf_Ph ph=new Elf_Ph(1,
						word(cont[off+4],cont[off+5],cont[off+6],cont[off+7]),
						word(cont[off+8],cont[off+9],cont[off+10],cont[off+11]),
						word(cont[off+16],cont[off+17],cont[off+18],cont[off+19]),
						word(cont[off+28],cont[off+29],cont[off+30],cont[off+31]));
				myelf.add_ph(ph);
	
				int vaddr=ph.get_addr();
				int psize=ph.get_size();
				int poff=ph.get_offset();

				for(int j=vaddr;j<=(vaddr+psize);j=j+4)
				{
					int insn=word(cont[poff],cont[poff+1],cont[poff+2],cont[poff+3]);
					memory.putWord(j,insn);
					poff+=4;
				}
			}
		}
	}
	else
	{
		System.out.println("This isn't a excutable elf32 file\n");
	}
   }
  
   public int getEntry()
   {
    //	System.out.format("entry is: %x\n",myelf.get_entry());
	   return myelf.get_entry();
   }
   
   public int getGp()
   {
	   return myelf.get_gp();
   }
   
   public int halfWord(byte b1, byte b2)
   {
	   int res= (b1&0xff) + ((b2&0xff)<<8); 
	   return res;
   }

   public int word(byte b1, byte b2, byte b3, byte b4)
   {
	   int res= (b1&0xff) + ((b2&0xff)<<8) + ((b3&0xff)<<16 ) + ((b4&0xff)<<24 );
	   return res;
   }
   public byte[] readFile(String filename) throws IOException
   {
	File file=new File(filename);
	long fileSize=file.length();
	if (fileSize>Integer.MAX_VALUE) 
	{  
        	System.out.println("file too big...");  
        	return null;  
        }  
	FileInputStream in=new FileInputStream(file);
	byte[] buffer=new byte[(int) fileSize];
	int offset=0;
	int numRead=0;
	while (offset<buffer.length  
        	&&(numRead=in.read(buffer, offset, buffer.length-offset))>=0) 
	{  
        	offset += numRead;  
        }
	if(offset!=buffer.length)
	{
		in.close();
		throw new IOException("Could not completely read file: "+file.getName());
	}
	in.close();
	return buffer;
   }
   
   public String getName(byte[] str,int index)
   {
	   String name="";
	   while(str[index]!='\0')
	   {
		   name=name+(char)(str[index]);	   
		   index++;
	   }
	   return name;
   }
   
   public void printSymbols(Memory memory)
   {
	   byte[] sym=myelf.symbol;
	   byte[] str=myelf.str;
	   int off=0;
	   for(int i=0;i<(sym.length)/16;i++)
	   {
		   if((sym[off+12]&0x0f)==STT_OBJECT)
		   {
			   String name=getName(str,word(sym[off],sym[off+1],sym[off+2],sym[off+3]));
			   int size=word(sym[off+8],sym[off+9],sym[off+10],sym[off+11])/4;
			   int addr=word(sym[off+4],sym[off+5],sym[off+6],sym[off+7]);
			   System.out.format("\nvar: %s  addr: %x \n", name, addr);
			   for(int j=0;j<size;j++)
			   {
				   System.out.format("value: %d  ",memory.getWord(addr));
				   if((j+1)%5==0)
					System.out.println();
				   addr+=4;
			   }
		   }
		   off=off+16;
	   }
	   System.out.println();
   }

}
