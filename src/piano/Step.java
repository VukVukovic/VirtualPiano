package piano;

public enum Step {
	C(0), D(1), E(2), F(3), G(4), A(5), B(6);
	
	private int value;
	Step(int value) { this.value = value;}
	public int value() { return value; }
	
	public static Step chrToStep(char c) {
		if (c == 'C') return C;
		else if (c=='D') return D;
		else if (c=='E') return E;
		else if (c=='F') return F;
		else if (c=='G') return G;
		else if (c=='A') return A;
		else return B;
	}
	
	public static Step intToStep(int i) {
		switch (i) {
		case 0: return C;
		case 1: return D;
		case 2: return E;
		case 3: return F;
		case 4: return G;
		case 5: return A;
		case 6: return B;
		}
		
		return B;
	}
}