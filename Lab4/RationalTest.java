import junit.framework.TestCase;

public class RationalTest extends TestCase {

    protected Rational HALF;

    protected void setUp() {
      HALF = new Rational( 1, 2 );
    }

    // Create new test
    public RationalTest (String name) {
        super(name);
    }

    public void testEquality() {
        assertEquals(new Rational(1,3), new Rational(1,3));
        assertEquals(new Rational(1,3), new Rational(2,6));
        assertEquals(new Rational(3,3), new Rational(1,1));
        assertEquals(new Rational(3,1), new Rational(6,2));
        assertEquals(new Rational(-2,1), new Rational(8,-4));
        assertEquals(new Rational(0,1), new Rational(0,5));
        assertEquals(new Rational(3,7), new Rational(6,14));
        assertEquals(new Rational(4,-2), new Rational(-2,1));
        assertEquals(new Rational(-5,-2), new Rational(10,4));
        assertEquals(new Rational(Integer.MAX_VALUE,Integer.MIN_VALUE+1), new Rational(Integer.MIN_VALUE+1,Integer.MAX_VALUE));
        assertEquals(new Rational(Integer.MAX_VALUE,Integer.MIN_VALUE+1), new Rational(-1,1));
        assertEquals(new Rational(Integer.MAX_VALUE,1), new Rational(Integer.MIN_VALUE+1,-1));
        assertEquals(new Rational(1, Integer.MIN_VALUE), new Rational(1, -Integer.MAX_VALUE-1));
        assertEquals(new Rational(Integer.MAX_VALUE, Integer.MAX_VALUE), new Rational(Integer.MIN_VALUE, Integer.MIN_VALUE));
    }

    // Test for nonequality
    public void testNonEquality() {
        assertFalse(new Rational(2,3).equals(new Rational(1,3)));
        assertFalse(new Rational(Integer.MIN_VALUE,1).equals(new Rational(Integer.MIN_VALUE,-1)));
        assertFalse(new Rational(2,1).equals(new Rational(2, Integer.MIN_VALUE+1)));
    }

    public void testAccessors() {
    	assertEquals(new Rational(2,3).numerator(), 2);
    	assertEquals(new Rational(2,3).denominator(), 3);
    	assertEquals(new Rational(-2,-1).numerator(), 2);
    	assertEquals(new Rational(-2,-1).denominator(), 1);
    }
    
    public void testPlus() {
    	assertEquals(new Rational(Integer.MAX_VALUE, 1).plus(new Rational(Integer.MIN_VALUE, 1)), new Rational(-1, 1));
    	assertEquals(new Rational(Integer.MAX_VALUE-1, Integer.MAX_VALUE).plus(new Rational(0, 1)), new Rational(Integer.MAX_VALUE-1, Integer.MAX_VALUE));
    	assertEquals(new Rational(-1,-1).plus(new Rational(1,1)), new Rational(2, 1));
    }
    
    public void testMinus() {
    	assertEquals(new Rational(1,1).minus(new Rational(1,-1)), new Rational(2, 1));
    	assertEquals(new Rational(1,1).minus(new Rational(1,1)), new Rational(0, 1));
    }

    public void testRoot() {
        Rational s = new Rational( 1, 4 );
        Rational sRoot = null;
        try {
            sRoot = s.root();
        } catch (IllegalArgumentToSquareRootException e) {
            e.printStackTrace();
        }
        assertTrue( sRoot.isLessThan( HALF.plus( Rational.getTolerance() ) ) 
                        && HALF.minus( Rational.getTolerance() ).isLessThan( sRoot ) );
    }
    
    public void testTimes() {
    	assertEquals(new Rational(3,1).times(new Rational(1,3)), new Rational(1,1));
    	assertEquals(new Rational(Integer.MAX_VALUE, Integer.MIN_VALUE).times(new Rational(Integer.MIN_VALUE, Integer.MAX_VALUE)), new Rational(1,1));
    	assertEquals(new Rational(Integer.MAX_VALUE, Integer.MAX_VALUE-1).times(new Rational(Integer.MAX_VALUE-1, Integer.MAX_VALUE-2)), new Rational(Integer.MAX_VALUE, Integer.MAX_VALUE-2));
    	assertEquals(new Rational(0,1).times(new Rational(4,5)),new Rational(0,1));
    	assertEquals(new Rational(4,2).times(new Rational(1,2)), new Rational(1,1));
    	assertFalse(new Rational(Integer.MAX_VALUE,1).times(new Rational(2,1)).isLessThan(new Rational(0,1)));
    }
    
    public void testDivides(){
    	assertEquals(new Rational(3,1).divides(new Rational(5,1)), new Rational(3,5));
    	assertEquals(new Rational(-3,5).divides(new Rational(7,4)), new Rational(12, -35));
    	assertEquals(new Rational(Integer.MAX_VALUE, 1).divides(new Rational(Integer.MIN_VALUE,1)), new Rational(Integer.MAX_VALUE, Integer.MIN_VALUE));
    	assertFalse(new Rational(Integer.MAX_VALUE,1).divides(new Rational(1,2)).isLessThan(new Rational(0,1)));
    }
    
    public void testIsLessThan() {
    	assertTrue(new Rational(0,1).isLessThan(new Rational(3,5)));
    	assertTrue(new Rational(-3,4).isLessThan(new Rational(0, Integer.MAX_VALUE)));
    	assertTrue(new Rational(4,-6).isLessThan(new Rational(3,4)));
    	assertTrue(new Rational(-4,6).isLessThan(new Rational(1,-2)));
    	assertFalse(new Rational(0,4).isLessThan(new Rational(0,98)));
    }
    
    public void testABS(){
    	assertEquals(new Rational(-4,-3).abs(), new Rational(4,3));
    	assertEquals(new Rational(0, -4).abs(), new Rational(0,2));
    	assertEquals(new Rational(-2, 4).abs(), new Rational(1,2));
    	assertEquals(new Rational(3,-5).abs(), new Rational(3,5));
    	assertEquals(new Rational(3,1).abs(), new Rational(-6,-2));
    	assertEquals(new Rational(Integer.MIN_VALUE, 1).abs(), new Rational(Integer.MIN_VALUE, -1));
    	assertEquals(new Rational(Integer.MIN_VALUE, Integer.MAX_VALUE).abs(), new Rational(Integer.MIN_VALUE, -Integer.MAX_VALUE));
    }
    
    public void testForException(){
    	try{
    		new Rational(4,0);
    		fail("Should have thrown an Exception!");
    	}catch(RuntimeException e){
    		assertTrue(true);
    	}
    }

    public static void main(String args[]) {
        String[] testCaseName = 
            { RationalTest.class.getName() };
        // junit.swingui.TestRunner.main(testCaseName);
        junit.textui.TestRunner.main(testCaseName);
    }
}
