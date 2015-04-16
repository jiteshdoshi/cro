package cro;

public class testClass {

	public static void main(String[] args){
		int[] a={1, 2, 3};
		
		run2(a);
		int[] b = new int[a.length];
		b=a;
		//System.arraycopy(a, 0, b, 0, a.length);
		
		System.out.println(b[1]);
	
	}
		public void run(){
		for(int i=0; i<1000; i++){
			System.out.println(i);
		}
		
	}
		
	public static void run2(int[] b){
		b[1]=100;
		
	}
	
}
