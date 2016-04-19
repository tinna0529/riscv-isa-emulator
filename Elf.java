//********************************************************************
//  Elf.java       Author: Tinna
//
//  Represents one elf 
//********************************************************************
import java.util.ArrayList;
 
public class Elf
{
   private int e_phoff;
   private int e_phentsiz;
   private int e_phnum;
   private int e_entry;
   private int e_shoff;
   private int e_shentsiz;
   private int e_shnum;
   private int e_shstrndx;
   private int _gp;
   
   byte[] symbol=null;
   byte[] str=null;
   
   public ArrayList<Elf_Ph> e_ph= new ArrayList<Elf_Ph>(); 
   public ArrayList<Elf_Sh> e_sh= new ArrayList<Elf_Sh>();
   public Elf(int ph_off, int ph_size, int ph_num, int entry, int sh_off, int sh_size, int sh_num, int sh_str)
   {
	e_phoff=ph_off;
	e_phentsiz=ph_size;
	e_phnum=ph_num;
	e_entry=entry;
	e_shoff=sh_off;
	e_shentsiz=sh_size;
	e_shnum=sh_num;
	e_shstrndx=sh_str;
	_gp=-1;
   }
   public void add_ph(Elf_Ph obj)
   {
	   e_ph.add(obj);
   }
   public void add_sh(Elf_Sh obj)
   {
	   e_sh.add(obj);
   }
   public int get_phoff(){ return e_phoff;}
   public int get_phsize() { return e_phentsiz;}
   public int get_phnum()   { return e_phnum;}
   public int get_shoff(){ return e_shoff;}
   public int get_shsize() { return e_shentsiz;}
   public int get_shnum()   { return e_shnum;}
   public int get_shstr() {return e_shstrndx;}
   public int get_entry() { return e_entry;}
   public int get_gp() { return  _gp; }
   public void set_phoffset(int off) { e_phoff=off;}
   public void set_phsize(int size){ e_phentsiz=size;}
   public void set_phnum(int num){ e_phnum=num;   }
   public void set_shoffset(int off) { e_shoff=off;}
   public void set_shsize(int size){ e_shentsiz=size;}
   public void set_shnum(int num){ e_shnum=num;   }
   public void set_gp(int gp) {_gp=gp;}

}

class Elf_Ph
{
   private int p_type;
   private int p_addr;
   private int p_offset;
   private int p_size;
   private int p_align;

   public Elf_Ph(int type, int off, int addr, int size, int align)
   {
	p_type=type;
	p_addr=addr;
	p_offset=off;
	p_size=size;
	p_align=align;
   }
   public int get_type(){ return p_type;   }
   public int get_addr(){ return p_addr;}
   public int get_offset() { return p_offset;}
   public int get_size(){ return p_size;}
   public int get_align(){ return p_align;}
   public void set_name(int type){ p_type=type;   }
   public void set_addr(int addr){  p_addr=addr;}
   public void set_offset(int off) { p_offset=off;}
   public void set_size(int size){ p_size=size;}
   public void set_align(int ali){ p_align=ali;}
}

class Elf_Sh
{
   private int s_name;
   private int s_addr;
   private int s_offset;
   private int s_size;
   private int s_align;

   public Elf_Sh(int name, int addr, int off, int size, int align)
   {
	s_name=name;
	s_addr=addr;
	s_offset=off;
	s_size=size;
	s_align=align;
   }
   public int get_name(){ return s_name;   }
   public int get_addr(){ return s_addr;}
   public int get_offset() { return s_offset;}
   public int get_size(){ return s_size;}
   public int get_align(){ return s_align;}
   public void set_name(int name){ s_name=name;   }
   public void set_addr(int addr){  s_addr=addr;}
   public void set_offset(int off) { s_offset=off;}
   public void set_size(int size){ s_size=size;}
   public void set_align(int ali){ s_align=ali;}
}