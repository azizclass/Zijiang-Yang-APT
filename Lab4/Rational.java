// Many concepts in this class taken from published java Rational class example
// at http://www.cs.princeton.edu/introcs/92symbolic/Rational.java.html
// Adapted and simplified 7/12/08 by Ian Varley
//
// Added square root functionality and associated helper methods 3/23/2011 by
// Adnan Aziz

public class Rational {

    private int _numerator;
    private int _denominator;

    // default tolerance for square root computation
    private static Rational TOLERANCE = new Rational( 1, 1000 );


    public Rational (int numerator, int denominator) {

        // reduce to lowest common form
        int g = gcd(numerator, denominator);
        _numerator = numerator / g;
        _denominator = denominator / g;
    }

    public int numerator()   { return _numerator; }

    public int denominator() { return _denominator; }

    // return greatest common denominator
    private static int gcd(int x, int y) {
        if (x < 0) x = -x;
        if (y < 0) y = -y;
        if (0 == y) return x;
        else return gcd(y, x % y);
    }

    // return lowest common multiple
    private static int lcm(int x, int y) {
        if (x < 0) x = x * -1;
        if (y < 0) y = y * -1;
        return x * (y / gcd(x, y) );
    }

    // two rationals are equal if their cross products are the same
    public boolean equals(Object y) {
        if (y == null) return false;
        if (y.getClass() != this.getClass()) return false;
        Rational b = (Rational) y;
        Rational a = this;
        int lhs = a._numerator * b._denominator;
        int rhs = a._denominator * b._numerator;     
        return lhs == rhs;
    }

    public Rational plus(Rational b) {
        Rational a = this;

        int x = gcd(a._numerator, b._numerator);
        int y = gcd(a._denominator, b._denominator);

        Rational z = new Rational( (a._numerator / x) * (b._denominator / y) 
                             + (b._numerator / x) * (a._denominator / y), 
                             lcm(a._denominator, b._denominator) );
        z._numerator *= x;
        return z;
    }

    public Rational times(Rational b) {
        Rational a = this;

        // use cross-cancellation to prevent some degree of overflow
        Rational c = new Rational(a._numerator, b._denominator);
        Rational d = new Rational(b._numerator, a._denominator);
        return new Rational(c._numerator * d._numerator, c._denominator * d._denominator);
    }

    // to subtract, just add a negated version
    public Rational minus(Rational b) {
        Rational a = this;
        Rational c = new Rational(b._numerator * -1, b._denominator);
        return a.plus(c);
    }

    // to divide, multiply by a flipped version
    public Rational divides(Rational b) {
        Rational a = this;
        Rational c = new Rational(b._denominator, b._numerator);
        return a.times(c);
    }

    public static void setTolerance( Rational epsilon ) {
      Rational.TOLERANCE = epsilon;
    }

    public static Rational getTolerance( ) {
      return Rational.TOLERANCE;
    }

    // bin search based square root computation, 
    // works for rationals in the range [0,2147483647]
    public Rational root( ) throws IllegalArgumentToSquareRootException {

        Rational low = new Rational(0,1);
        // Max Int value is 2147483647, square root is 46341
        // Rational high = new Rational(46341,1);
        Rational high = new Rational(46340,1);

        if ( this.isLessThan( low ) || !this.isLessThan( high) ) {
          throw new IllegalArgumentToSquareRootException( this );
        }
        System.out.println("46341* 46341 = " + (46341 * 46341) );
        System.out.println("46340* 46340 = " + (46340 * 46340) );

        Rational half = new Rational(1,2 );
        Rational midpoint = ( low.plus( high ) ).times( half );
        Rational tmp = null;
        while ( !high.minus(low).abs().isLessThan(TOLERANCE) ) {
            System.out.println("low high: " + low + " " + high );
            System.out.println("square of midpoint is: " + midpoint.times( midpoint ) );
            if ( midpoint.times( midpoint ).isLessThan( this ) ) {
                tmp = new Rational( midpoint );
                midpoint = high.plus( midpoint ).times( half );
              low = tmp;
            } else {
              tmp = new Rational( midpoint );
              midpoint = low.plus( midpoint ).times( half );
              high = tmp;
            }
        }
        return midpoint;
    }

    public boolean isLessThan( Rational r ) {
        return ( _numerator * r._denominator < r._numerator * _denominator );
    }

    public Rational abs( ) {
        Rational result = new Rational(1,1);
        if ( ( this.numerator() >= 0 && this.denominator() >= 0 ) || 
             ( this.numerator() < 0  && this.denominator() < 0 ) ) {
            result._numerator = this.numerator();
            result._denominator = this.denominator();
        } else {
            result._numerator = -this.numerator();
            result._denominator = this.denominator();
        }
        return result;
    }

    public Rational( Rational r ) {
        this._numerator = r._numerator;
        this._denominator = r._denominator;
    }

    public String toString( ) {
        String result = _numerator + "/" + _denominator;
        return result;
    }

    public static void main( String [] args ) {
      Rational half = new Rational(1, 2);
      try {
        System.out.println("Root of half is " + half.root() );
      } catch (IllegalArgumentToSquareRootException e) {
        e.printStackTrace();
      }
    }

}

class IllegalArgumentToSquareRootException extends Exception {
  public IllegalArgumentToSquareRootException( Rational r ) {
    super("Illegal argument to square root, should be in range [0,2147483647] got " + r.toString() );
  }
}

