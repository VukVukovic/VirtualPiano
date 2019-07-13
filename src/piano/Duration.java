package piano;

public class Duration {
	public static final Duration d1_8 = new Duration(1,8), d1_4 = new Duration(1,4);
	private int a, b;
	
	public Duration(int a, int b) {
		this.a = a;
		this.b = b;
		correct();
	}
	
	public Duration add(Duration d) {
		int aa = this.a*d.b + d.a*this.b;
		int bb = this.b * d.b;
		this.a = aa; this.b = bb;
		correct();
		return this;
	}
	
	public Duration sub(Duration d) {
		int aa = this.a*d.b - d.a*this.b;
		int bb = this.b * d.b;
		this.a = aa; this.b = bb;
		correct();
		return this;
	}
	
	public String toString() {
		if (b == 1) return a+"";
		else return a+"/"+b;
	}
	
	private void correct() {
		if (b < 0) { a=-a; b=-b; }
		int g = Math.abs(gcd(a, b));
		a /= g;
		b /= g;
	}
	
	static int gcd(int a, int b) {
	    while (b != 0) {
	        int t = a;
	        a = b;
	        b = t % b;
	    }
	    return a;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Duration) {
			Duration d = (Duration)o;
			return d.a == this.a && d.b == this.b;
		}
		return false;
	}
}
