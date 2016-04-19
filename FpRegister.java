
public class FpRegister {

	double[] fpregister;
	
	FpRegister()
	{
		fpregister = new double[32];
	}
	public double get(int num)
	{
		return fpregister[num];
	}
	public float getFloat(int num)
	{
		return Float.parseFloat(String.valueOf(fpregister[num]));
	}
	public void set(int num,double value)
	{
		fpregister[num]=value;
	}
	public void setFloat(int num,float value)
	{
		fpregister[num]=Double.parseDouble(String.valueOf(value));
	}
}
