package random;

import java.util.Random;

public class RandomGenerator {
	private Random rn;
	private int count;//¹ß»ý°¹¼ö
	private int avg;// Æò±Õ
	private int deviation;// ÆíÂ÷
	
	private double arr[];
	public RandomGenerator(int count, int avg, int deviation) {
		rn = new Random();
		this.count =count;
		this.avg=avg;
		this.deviation=deviation;
		arr = new double[count];
	}
	
	public void start()
	{
		for(int i=0;i<count;i++)
		{
			System.out.println(String.format("%.2f", (deviation*rn.nextGaussian())+avg));	
		}
	}
	public static void main(String[] args) {
		new RandomGenerator(100,45,10).start();
	}
	
	public void makeTemperature()
	{
		for(int i=0;i<count;i++)
		{
			System.out.println(String.format("%.2f", (deviation*rn.nextGaussian())+avg));	
		}
	}
	public void makeHuminity()
	{
		for(int i=0;i<count;i++)
		{
			System.out.println(String.format("%.2f", (deviation*rn.nextGaussian())+avg));	
		}
	}
	public void makeHit()
	{
		for(int i=0;i<count;i++)
		{
			System.out.println(String.format("%.2f", (deviation*rn.nextGaussian())+avg));	
		}
	}
	public void makeQuery()
	{
		
	}
	

}
