import java.util.*;
import java.io.*;

public class Syscall {
	
	static final int SYS_OPENAT=56;
	static final int SYS_CLOSE=57;
	static final int SYS_LSEEK=62;
	static final int SYS_READ=63;
	static final int SYS_WRITE=64;
	static final int SYS_FSTAT=80;
	static final int SYS_EXIT=93;
	static final int SYS_BRK=214;
	static final int SYS_OPEN=1024;
	static final int SYS_GETTIMEOFDAY=169;

	static final String OUTPUTFILE="../console.output";
	
	
	Memory memory;
	Register register;
	public Syscall(Memory mem,Register regs){
		memory=mem;
		register=regs;
	}

	public void do_syscall (int a0,int a1,int a2,int a3,
				int a4,int a5,int a6,int sys_id)
	{
		switch(sys_id){
		case SYS_GETTIMEOFDAY:
		{
			//int sec=Run.InstructionCounter/(CPU.CPU_FREQ/1000000);	
			int sec=Run.InstructionCounter/(CPU.CPU_FREQ);	
			memory.putWord(a0,sec);
			memory.putWord(a0+4,0);
			memory.putWord(a0+8,0);
			memory.putWord(a0+12,0);
			register.set(10,0);
		//	System.out.println("--------SYS_gettimeofday\n");
			break;
		}
		case SYS_CLOSE:
		{
		//	System.out.println("--------SYS_close\n");
			break;
		}
		case SYS_READ:
		{
			
		//	System.out.println("--------SYS_read\n");
		
			int len=a2;
			int addr=a1;
			int fd=a0;
			byte[] buf=new byte[len];

	//		System.out.format("len is : %d, addr is 0x%x, fd is %d\n",len,addr,fd);
			int readLen=sys_read(fd,buf,len);
			register.set(10,readLen);
			for(int i=0;i<readLen;i++)
			{
				memory.putByte(addr++,buf[i]);
	//			System.out.format("memory addr:0x%x   value:0x%x\n",addr-1,memory.getByte(addr-1));
			}
			break;
		}
		case SYS_WRITE:
		{
	//		System.out.println("--------SYS_write\n");
			int len=a2;
			int addr=a1;
			int fd=a0;

			byte[] buf=new byte[len];
			for(int i=0;i<len;i++)
			{
				buf[i]=memory.getByte(addr++);
			}
			int tmp=sys_write(fd,buf,len);
			register.set(10,tmp);
			break;
		}
		case SYS_FSTAT:
		{
	//		System.out.println("--------SYS_fstat\n");
			break;
		}
		case SYS_LSEEK:
		{
	//		System.out.println("--------SYS_lseek\n");
			break;
		}
		case SYS_OPENAT:
		{
	//		System.out.println("--------SYS_openat\n");
			break;
		}
		case SYS_BRK:
		{
	//		System.out.println("--------SYS_brk\n");
			break;
		}
		case SYS_OPEN:
		{
	//		System.out.println("--------SYS_open\n");
			break;
		}
		case SYS_EXIT:
		{
	//		System.out.println("--------SYS_exit\n");
			CPU.STATE_RUN=false;
			break;
		}
		default:
		{
	//		System.out.println("--------SYS_undefined\n");
			break;
		}
		}
	}	
	

	public int sys_write(int fd,byte[] buf,int len){
		if(fd==1){
			console(1,buf,len);
			fd=len;
		}
		else{
			fd=-1;
		}	
		return fd;
	}

	public int sys_read(int fd,byte[] buf,int len){
		if(fd==0){
              
			fd=console(2,buf,len);
		}
		else{
			fd=-1;
		}
		return fd;
	}
	
	public int console(int type, byte[] buf, int len){
		int Length=0;
		if(type==1){//printf
			for(int i=0;i<len;i++)
			{
				System.out.print((char)buf[i]);
			}
/*			File f = new File(OUTPUTFILE);
			try {
				if(!f.exists()){
					f.createNewFile();
				}
		
				FileWriter fw = new FileWriter(f,true);
				BufferedWriter out = new BufferedWriter(fw);
				for(int i=0;i<len;i++){
					out.write(buf[i]);
				}
				out.close();
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
*/			Length=len;	
		}
		else if(type==2){//scanf
			try {

			

				DataInputStream in =  new DataInputStream(System.in);
				Length=in.read(buf,0,len);

/*				for(int i=0;i<Length;i++)
				{
					System.out.print(" buf["+i+"]: "+buf[i]);
				}
				System.out.println();*/
			}
			catch(Exception e) {
				System.out.println("Cannot read string from keyboard\n");
			}
		}
		else{
		}
		return Length;
	}
}


