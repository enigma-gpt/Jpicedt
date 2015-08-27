/* File: Complex.java -*- coding: iso-8859-1-unix -*-
 *                             -- A  Java  class  for performing complex
 *                                number arithmetic to double precision.
 *
 * Copyright (c) 1997 - 2001, Alexander Anderson.
 *
 * This  program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published  by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be  useful,  but
 * WITHOUT   ANY   WARRANTY;   without  even  the  implied  warranty  of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR  PURPOSE.   See  the  GNU
 * General Public License for more details.
 *
 * You  should  have  received  a copy of the GNU General Public License
 * along  with  this  program;  if  not,  write  to  the  Free  Software
 * Foundation,  Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA.
 */

//package  ORG.netlib.math.complex;
package jpicedt.util.math;

import java.io.Serializable;
import java.lang.Math;

/**
* <p>
* @version
*     <b>1.0.1</b> <br>
*     <tt>
*     Last change:  ALM  23 Mar 2001    8:56 pm
*     </tt>
* <p>
* A Java class for performing complex number arithmetic to <code>double</code>
* precision.
*
* <p>
* @author               <a HREF="mailto:Alexander Anderson <sandy@almide.demon.co.uk>">Sandy Anderson</a>
* @author               Priyantha Jayanetti
* <p>
* <font color="000080">
* <pre>
*  <b>Copyright (c) 1997 - 2001, Alexander Anderson.</b>
*
*  This  program is free software; you can redistribute it and/or modify
*  it under the terms of the <a href="http://www.gnu.org/">GNU</a> General Public License as published  by
*  the Free Software Foundation; either version 2 of the License, or (at
*  your option) any later version.
*
*  This program is distributed in the hope that it will be  useful,  but
*  WITHOUT   ANY   WARRANTY;   without  even  the  implied  warranty  of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR  PURPOSE.   See  the  GNU
*  General Public License for more details.
*
*  You  should  have  received  a copy of the GNU General Public <a href="GNU_GeneralPublicLicence.html">License</a>
*  along  with  this  program;  if  not,  write  to  the  Free  Software
*  Foundation,  Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
*  USA.
* </pre>
* </font>
* <p>
* The latest version of this <code>Complex</code> class is available from
* the <a href="http://www.netlib.org/">Netlib Repository</a>.
* <p>
* Here's an example of the style the class permits:<br>
*
* <pre>
*         <b>import</b>  ORG.netlib.math.complex.Complex;<br>
*         <b>public</b> <b>class</b> Test {<br>
*             <b>public boolean</b> isInMandelbrot (Complex c, <b>int</b> maxIter) {
*                 Complex z= <b>new</b> Complex(0, 0);<br>
*                 <b>for</b> (<b>int</b> i= 0; i &lt; maxIter; i++) {
*                     z= z.cMul(z).cAdd(c);
*                     <b>if</b> (z.abs() &gt; 2) <b>return false</b>;
*                 }<br>
*                 <b>return true</b>;
*             }<br>
*         }
* </pre>
* </dd>
* <p>
* <dd>This class was developed by
*     <a HREF="http://www.almide.demon.co.uk">Sandy Anderson</a> at the
*     School of Electronic Engineering,
*     <a HREF="http://www.mdx.ac.uk/">Middlesex University</a>, UK, and
*     Priyantha Jayanetti at The Power Systems Program, the
*     <a HREF="http://www.eece.maine.edu/">University of Maine</a>, USA.
* </dd>
* <p>
* <dd>And many, many thanks to <a href="mailto:R.D.Hirsch@red-deer.demon.co.uk">Mr. Daniel
*     Hirsch</a>, for his constant advice on the mathematics, his exasperating
*     ability to uncover bugs blindfold, and for his persistent badgering over
*     the exact wording of this documentation.
* </dd>
* <p>
* <dd>For instance, he starts to growl like a badger if you say "infinite set".</dd><br>
* <dd>"Grrr...What's <i>that</i> mean?  <i>Countably</i> infinite?"</dd><br>
* <dd>You think for a while.</dd><br>
* <dd>"Grrr..."</dd><br>
* <dd>"Yes."</dd><br>
* <dd>"Ah! Then you mean <i>infinitely many</i>."</dd><br>
* <p>
**/
public class Complex implements Cloneable, Serializable{

    public    static final String          VERSION             =  "1.0.1";
    public    static final String          DATE                =  "Fri 23-Mar-2001 8:56 pm";
    public    static final String          AUTHOR              =  "sandy@almide.demon.co.uk";
    public    static final String          REMARK              =  "Class available from "
                                                                  + "http://www.netlib.org/";

    /**
    * Switches on debugging information.
    * <p>
	*/

    // protected static       boolean         debug               =  false;

    /**
    * Whilst debugging:  the nesting level when tracing method calls.
    * <p>
	*/

    // private   static       int             trace_nesting       =  0;

    /**
    * Twice <a
    * href="http://cad.ucla.edu/repository/useful/PI.txt"><tt><b>PI</b></tt></a>
    * radians is the same thing as 360 degrees.
    * <p>
    * @since jPicEdt 1.6
	*/
    public static final double          TWO_PI              =  2.0 * Math.PI;

    /**
    * A constant representing <i><b>i</b></i>, the famous square root of
    * <i>-1</i>.
    * <p>
    * The other square root of <i>-1</i> is - <i><b>i</b></i>.
    * <p>
    * @since jPicEdt 1.6
    */
    public    static final Complex I    = new Complex(0.0, 1.0);
    public    static final Complex ZERO = new Complex(0.0, 0.0);
    public    static final Complex ONE  = new Complex(1.0, 0.0);

    private                double          re;
    private                double          im;



    //---------------------------------//
    //           CONSTRUCTORS          //
    //---------------------------------//



    /**
    * Constructs a <code>Complex</code> representing the number zero.
    *
    * <p>
    * @since jPicEdt 1.6
    */
    public Complex(){
        this(0.0, 0.0);
    }//end Complex()



    /**
    * Constructs a <code>Complex</code> representing a real number.
    *
    * <p>
    * @param  re               The real number
    * <p>
    * @since jPicEdt 1.6
    */
	 public Complex(double re){
		 this(re, 0.0);
	 }//end Complex(double)



	 /**
	 * Constructs a separate new <code>Complex</code> from an existing
	 * <code>Complex</code>.
	 *
	 * <p>
	 * @param  z                A <code>Complex</code> number
	 * <p>
	 * @since jPicEdt 1.6
	 */
	 public Complex(Complex z){
		 this(z.re, z.im);
	 }//end Complex(Complex)



	 /**
	 * Constructs a <code>Complex</code> from real and imaginary parts.
	 *
	 * <p>
	 * <i><b>Note:</b><ul> <font color="000080">All methods in class
	 * <code>Complex</code> which deliver a <code>Complex</code> are written such that
	 * no intermediate <code>Complex</code> objects get generated.  This means that
	 * you can easily anticipate the likely effects on garbage collection caused
	 * by your own coding.</font>
	 * </ul></i>
	 * <p>
	 * @param  re               Real part
	 * @param  im               Imaginary part
	 * <p>
	 * @see                     #cart(double re, double im)
	 * @see                     #polar(double, double)
	 * @since jPicEdt 1.6
	 */
	 public Complex(double re, double im){
		 this.re =  re;
		 this.im =  im;

		 // if (debug) System.out.println(indent(trace_nesting) + "new Complex, #" + (++objectCount));// !!!
	 }//end Complex(double,double)



	 //---------------------------------//
	 //              DEBUG              //
	 //---------------------------------//



	 /*

	 // BETA Debugging methods...

	 private static void
	 entering (String what) {
		 System.out.print(indent(trace_nesting) + what);
		 trace_nesting++;
	 }//end entering(String)

	 private static void
	 enter (String what, double param1, double param2) {
		 entering(what);
		 System.out.println("(" + param1 + ", " + param2 + ") ");
	 }//end enter(String,double,double)

	 private static void
	 enter (String what, double param) {
		 entering(what);
		 System.out.println("(" + param + ") ");
	 }//end enter(String,double)

	 private static void
	 enter (String what, Complex z) {
		 entering(what);
		 System.out.println("(" + z + ") ");
	 }//end enter(String,Complex)

	 private static void
	 enter (String what, Complex z1, Complex z2) {
		 entering(what);
		 System.out.println("(" + z1 + ", " + z2 + ") ");
	 }//end enter(String,Complex,Complex)

	 private static void
	 enter (String what, Complex z, double x) {
		 entering(what);
		 System.out.println("(" + z + ", " + x + ") ");
	 }//end enter(String,Complex,double)

	 private static void
	 enter (String what, Complex z, double x, double y) {
		 entering(what);
		 System.out.println("(" + z + ", " + cart(x, y) + ") ");
	 }//end enter(String,Complex,double)

	 private static void
	 enter (String what, Complex z1, Complex z2, double x) {
		 entering(what);
		 System.out.println("(" + z1 + ", " + z2 + ", " + x + ") ");
	 }//end enter(String,Complex,Complex,double)

	 private static void
	 leaving (String what) {
		 trace_nesting--;
		 System.out.print(indent(trace_nesting) + "is ");
	 }//end leaving(String)

	 private static void
	 leave (String what, boolean result) {
		 leaving(what);
		 System.out.println(result);
	 }//end leave(String,boolean)

	 private static void
	 leave (String what, double result) {
		 leaving(what);
		 System.out.println(result);
	 }//end leave(String,double)

	 private static void
	 leave (String what, Complex result) {
		 leaving(what);
		 System.out.println(result);
	 }//end leave(String,Complex)

	 private static String
	 indent (int nesting) {
		 StringBuffer indention =  new StringBuffer("");

		 for (int i =  0; i < nesting; i++) {
			 indention.append("    ");
		 }//endfor

		 return  indention.toString();
	 }//end indent(int)

	 */



	 /**
	 * Useful for checking up on the exact version.
	 *
	 * <p>
	 * @since jPicEdt 1.6
	 */
	 public static void main(String[] args){
		 System.out.println();
		 System.out.println("Module : " + Complex.class.getName());
		 System.out.println("Version: " + Complex.VERSION);
		 System.out.println("Date   : " + Complex.DATE);
		 System.out.println("Author : " + Complex.AUTHOR);
		 System.out.println("Remark : " + Complex.REMARK);
		 System.out.println();
		 System.out.println("Hint:  use TestComplex to test the class.");
		 System.out.println();
	 }//end main(String[])



	 //---------------------------------//
	 //             STATIC              //
	 //---------------------------------//

	 /**
	 * Returns a <code>Complex</code> from real and imaginary parts.
	 *
	 * <p>
	 * @param  re               Real part
	 * @param  im               Imaginary part
	 * <p>
	 * @return                  <code>Complex</code> from Cartesian coordinates
	 * <p>
	 * @see                     #re()
	 * @see                     #im()
	 * @see                     #polar(double, double)
	 * @see                     #toString()
	 * @since jPicEdt 1.6
	 */
	 public static Complex cart(double re, double im){
		 return  new Complex(re, im);
	 }//end cart(double,double)



	 /**
	 * Returns a <code>Complex</code> from a size and direction.
	 *
	 * <p>
	 * @param  r                Size
	 * @param  theta            Direction (in <i>radians</i>)
	 * <p>
	 * @return                  <code>Complex</code> from Polar coordinates
	 * <p>
	 * @see                     #abs()
	 * @see                     #arg()
	 * @see                     #cart(double re, double im)
	 * @since jPicEdt 1.6
	 */
	 public static Complex 
	 polar(double r, double theta) {
		 if (r < 0.0) {
			 theta +=  Math.PI;
			 r      =  -r;
		 }//endif

		 theta =  theta % TWO_PI;

		 return  cart(r * Math.cos(theta), r * Math.sin(theta));
	 }//end polar(double,double)



	 /**
	 * Returns the <code>Complex</code> base raised to the power of the exponent.
	 *
	 * <p>
	 * @param  base             The base "to raise"
	 * @param  exponent         The exponent "by which to raise"
	 * <p>
	 * @return                  base "raised to the power of" exponent
	 * <p>
	 * @see                     #pow(double, Complex)
	 * @since jPicEdt 1.6
	 */
	 public static Complex pow(Complex base, double exponent){        

	 // return  base.cLog).cScale(exponent).exp();

		 double re =  .5*exponent * Math.log(base.abs2());
		 double im =  exponent * base.arg();

		 double scalar =  Math.exp(re);

		 return  cart( scalar * Math.cos(im), scalar * Math.sin(im) );
	 }//end pow(Complex,double)



	 /**
	 * Returns the base raised to the power of the <code>Complex</code> exponent.
	 *
	 * <p>
	 * @param  base             The base "to raise"
	 * @param  exponent         The exponent "by which to raise"
	 * <p>
	 * @return                  base "raised to the power of" exponent
	 * <p>
	 * @see                     #pow(Complex, Complex)
	 * @see                     #exp()
	 * @since jPicEdt 1.6
	 */
	 public static Complex pow(double base, Complex exponent){
		 // return  new Complex(base).cLog).cMul(exponent).exp();

		 double re =  Math.log(Math.abs(base));
		 double im =  Math.atan2(0.0, base);

		 double re2 =  (re*exponent.re) - (im*exponent.im);
		 double im2 =  (re*exponent.im) + (im*exponent.re);

		 double scalar =  Math.exp(re2);

		 return  cart( scalar * Math.cos(im2), scalar * Math.sin(im2) );
	 }//end pow(double,Complex)



	 /**
	 * Returns the <code>Complex</code> base raised to the power of the <code>Complex</code> exponent.
	 *
	 * <p>
	 * @param  base             The base "to raise"
	 * @param  exponent         The exponent "by which to raise"
	 * <p>
	 * @return                  base "raised to the power of" exponent
	 * <p>
	 * @see                     #pow(Complex, double)
	 * @see                     #cPow(Complex)
	 * @since jPicEdt 1.6
	 */
	 public static Complex pow (Complex base, Complex exponent) {
		 // return  base.cLog).cMul(exponent).exp();

		 double re =  .5*Math.log(base.abs2());
		 double im =  base.arg();

		 double re2 =  (re*exponent.re) - (im*exponent.im);
		 double im2 =  (re*exponent.im) + (im*exponent.re);

		 double scalar =  Math.exp(re2);

		 return  cart( scalar * Math.cos(im2), scalar * Math.sin(im2) );
	 }//end pow(Complex,Complex)



	 //---------------------------------//
	 //             PUBLIC              //
	 //---------------------------------//



	 /**
	 * Returns <code>true</code> if either the real or imaginary component of this
	 * <code>Complex</code> is an infinite value.
	 *
	 * <p>
	 * @return                  <code>true</code> if either component of the <code>Complex</code> object is infinite; <code>false</code>, otherwise.
	 * <p>
	 * @since jPicEdt 1.6
	 */
	 public boolean isInfinite(){
		 return  ( Double.isInfinite(re) || Double.isInfinite(im) );
	 }//end isInfinite()



	 /**
	 * Returns <code>true</code> if either the real or imaginary component of this
	 * <code>Complex</code> is a Not-a-Number (<tt>NaN</tt>) value.
	 *
	 * <p>
	 * @return                  <code>true</code> if either component of the <code>Complex</code> object is <tt>NaN</tt>; <code>false</code>, otherwise.
	 * <p>
	 * @since jPicEdt 1.6
	 */
	 public boolean isNaN(){
		 return  ( Double.isNaN(re) || Double.isNaN(im) );
	 }//end isNaN()



	 /**
     * Renvoie <code>absoluteIsCloseTo(z,Math.abs(tolerance))</code>. Veuillez
     * préférer la méthode {@link #absoluteIsCloseTo} si vous savez que
     * <code>tolerance &gt; 0</code>.
	 *
	 * @param  z                Le <code>Complex</code> auquel on compare <code>this</code>
	 * @param  tolerance        LA tolérance pour la comparaison
	 * @return                  <code>true</code> ou <code>false</code>
	 * @since jPicEdt 1.6
	 * @see #absoluteIsCloseTo
	 */
	public boolean equals(Complex z, double tolerance){ 
		return absoluteIsCloseTo(z,Math.abs(tolerance)); 
	}
	//end equals(Complex,double)


	/**
	 * Renvoie <code>cSub(z).normInf() <= tolerance</code>.
	 *
	 * @param z une valeur <code>Complex</code> à laquelle <code>this</code>
	 * est comparé.
	 * @param tolerance une valeur <code>double</code>, donnant la limite
	 * jusqu'à laquelle <code>this</code> et <code>z</code> sont considérés
	 * proches.
	 * @return une valeur <code>boolean</code>, vraie la distance de
	 * <code>this</code> à <code>z</code> selon la norme infinie {@link
	 * #normInf} n'excède pas la tolérance <code>tolerance</code>.
	 * @since jPicEdt 1.6
	 * @see #relativeIsCloseTo
	 */
	public boolean absoluteIsCloseTo(Complex z, double tolerance){
		 return  cSub(z).normInf() <= Math.abs(tolerance);
	}

	/**
	 * Renvoie <code>cSub(z).normInf() <= tolerance *
	 * Math.max(normInf(), z.normInf())</code>.
	 *
	 * @param z une valeur <code>Complex</code> à laquelle <code>this</code>
	 * est comparé.
	 * @param tolerance une valeur <code>double</code>, donnant la limite
	 * jusqu'à laquelle <code>this</code> et <code>z</code> sont considérés
	 * proches.
	 * @return une valeur <code>boolean</code>, vraie la distance de
	 * <code>this</code> à <code>z</code> selon la norme infinie {@link
	 * #normInf} n'excède pas <code>tolerance</code> partie-pour-un de la plus
	 * grande des normes {@link #normInf} de <code>this</code> et
	 * <code>z</code>.
	 * @since jPicEdt 1.6
	 * @see #absoluteIsCloseTo
	 */
	public boolean relativeIsCloseTo(Complex z, double tolerance){
		return  cSub(z).normInf() <= Math.abs(tolerance) 
			* Math.max(normInf(), z.normInf());
	}


	 /**
	 * Overrides the {@link java.lang.Cloneable Cloneable} interface.
	 *
	 * <p>
	 * Standard override; no change in semantics.
	 * <p>
	 * The following Java code example illustrates how to clone, or <i>copy</i>, a
	 * <code>Complex</code> number:
	 * <p>
	 * <pre>
	 *     Complex z1 =  <b>new</b> Complex(0, 1);
	 *     Complex z2 =  (Complex) z1.clone();
	 * </pre>
	 * <p>
	 * @return                  An <code>Object</code> that is a copy of this <code>Complex</code> object.
	 * <p>
	 * @see                     java.lang.Cloneable
	 * @see                     java.lang.Object#clone()
	 * @since jPicEdt 1.6
	 */
	 public Object clone (){
		 try {
			 return  (Object)(super.clone());
		 } catch (java.lang.CloneNotSupportedException e) {
			 return null;                                                       // This cannot happen: there would have to be a serious internal error in the Java runtime if this codepath happens!
		 }//endtry
	 }//end clone()



	 /**
	 * Extracts the real part of a <code>Complex</code> as a <code>double</code>.
	 *
	 * <p>
	 * <pre>
	 *     re(x + <i><b>i</b></i>*y)  =  x
	 * </pre>
	 * <p>
	 * @return                  The real part
	 * <p>
	 * @see                     #im()
	 * @see                     #cart(double re, double im)
	 * @since jPicEdt 1.6
	 */
	 public double re(){
		 return  re;
	 }//end re()



	 /**
	 * Extracts the imaginary part of a <code>Complex</code> as a <code>double</code>.
	 *
	 * <p>
	 * <pre>
	 *     im(x + <i><b>i</b></i>*y)  =  y
	 * </pre>
	 * <p>
	 * @return                  The imaginary part
	 * <p>
	 * @see                     #re()
	 * @see                     #cart(double re, double im)
	 * @since jPicEdt 1.6
	 */
	 public double im(){
		 return  im;
	 }//end im()



	 /**
	 * Returns the square of the "length" of a <code>Complex</code> number.
	 *
	 * <p>
	 * <pre>
	 *     (x + <i><b>i</b></i>*y).abs2()  =  x*x + y*y
	 * </pre>
	 * <p>
	 * Always non-negative.
	 * <p>
	 * @return                  The square norm
	 * <p>
	 * @see                     #abs()
	 * @since jPicEdt 1.6
	 */
	 public double abs2(){
		 return  (re*re) + (im*im);
	 }//end abs2()


	 /** @return |re(this)|+|im(this)| */
	 public double norm1(){
		 return  Math.abs(re) + Math.abs(im);
	 }//end norm1()

	 /** @return max(|re(this)|,|im(this)|) */
	 public double normInf(){
		 return  Math.max(Math.abs(re),Math.abs(im));
	 }//end normInf()


	 /**
	 * Returns the magnitude of a <code>Complex</code> number.
	 *
	 * <p>
	 * <pre>
	 *     abs(z)  =  sqrt(abs2(z))
	 * </pre>
	 * <p>
	 * In other words, it's Pythagorean distance from the origin
	 * (<i>0 + 0<b>i</b></i>, or zero).
	 * <p>
	 * The magnitude is also referred to as the "modulus" or "length".
	 * <p>
	 * Always non-negative.
	 * <p>
	 * @return                  The magnitude (or "length")
	 * <p>
	 * @see                     #arg()
	 * @see                     #polar(double, double)
	 * @see                     #abs2()
	 * @since jPicEdt 1.6
	 */
	 public double abs(){
		 return  abs(re, im);
	 }//end abs()

	 static private double abs(double x, double y){
		 //  abs(z)  =  sqrt(abs2(z))

		 // Adapted from
		 // "Numerical Recipes in Fortran 77: The Art of Scientific Computing"
		 // (ISBN 0-521-43064-X)

		 double absX =  Math.abs(x);
		 double absY =  Math.abs(y);

		 if (absX == 0.0 && absY == 0.0) {                                      // !!! Numerical Recipes, mmm?
			 return  0.0;
		 } else if (absX >= absY) {
			 double d =  y / x;
			 return  absX*Math.sqrt(1.0 + d*d);
		 } else {
			 double d =  x / y;
			 return  absY*Math.sqrt(1.0 + d*d);
		 }//endif
	 }//end abs()



	 /**
	 * Returns the <i>principal</i> angle of a <code>Complex</code> number, in
	 * radians, measured counter-clockwise from the real axis.  (Think of the
	 * reals as the x-axis, and the imaginaries as the y-axis.)
	 *
	 * <p>
	 * There are infinitely many solutions, besides the principal solution.
	 * If <b>A</b> is the principal solution of <i>arg(z)</i>, the others are of
	 * the form:
	 * <p>
	 * <pre>
	 *     <b>A</b> + 2*k*<b>PI</b>
	 * </pre>
	 * <p>
	 * where k is any integer.
	 * <p>
	 * <code>arg()</code> always returns a <code>double</code> between
	 * -<tt><b>PI</b></tt> and +<tt><b>PI</b></tt>.
	 * <p>
	 * <i><b>Note:</b><ul> 2*<tt><b>PI</b></tt> radians is the same as 360 degrees.
	 * </ul></i>
	 * <p>
	 * <i><b>Domain Restrictions:</b><ul> There are no restrictions: the
	 * class defines arg(0) to be 0
	 * </ul></i>
	 * <p>
	 * @return                  Principal angle (in radians)
	 * <p>
	 * @see                     #abs()
	 * @see                     #polar(double, double)
	 * @since jPicEdt 1.6
	 */


	 public double
	 arg () {
		 return  Math.atan2(im, re);
	 }//end arg()



	 /**
	 * Returns the "negative" of a <code>Complex</code> number.
	 *
	 * <p>
	 * <pre>
	 *     cNeg(a + <i><b>i</b></i>*b)  =  -a - <i><b>i</b></i>*b
	 * </pre>
	 * <p>
	 * The magnitude of the negative is the same, but the angle is flipped
	 * through <tt><b>PI</b></tt> (or 180 degrees).
	 * <p>
	 * @return                  Negative of the <code>Complex</code>
	 * <p>
	 * @see                     #cScale(double)
	 * @since jPicEdt 1.6
	 */


	 public Complex cNeg(){
		 return  cart(-re,-im);
	 }//end cNeg()

	 /** negate this, which modifies this.
	  * @since jPicEdt 1.6
	 */

	 public Complex neg(){
		 re = -re;
		 im = -im;
		 return this;
	 }//end cNeg()


	 /**
	 * Returns the <code>Complex</code> "conjugate" of this.
	 *
	 * <p>
	 * <pre>
	 *     cConj(x + <i><b>i</b></i>*y)  =  x - <i><b>i</b></i>*y
	 * </pre>
	 * <p>
	 * The conjugate appears "flipped" across the real axis.
	 * <p>
	 * @return                  The <code>Complex</code> conjugate
	 *<p>
	 * @since jPicEdt 1.6
	 */

	 public Complex cConj(){
		 return  cart(re, -im);
	 }//end cConj()

	 /** conjugate this, and return it for convenience 
	  * @since jPicEdt 1.6
	 */

	 public Complex conj(){
		 im = -im;
		 return this;
	 }

	 /** do z = 1/z; 
	  * @since jPicEdt 1.6
	 */

	 static private void inv (Complex z){
		 double zRe, zIm;
		 double scalar;

		 if (Math.abs(z.re) >= Math.abs(z.im)) {
			 scalar =  1.0 / ( z.re + z.im*(z.im/z.re) );

			 zRe =    scalar;
			 zIm =    scalar * (- z.im/z.re);
		 } else {
			 scalar =  1.0 / ( z.re*(z.re/z.im) + z.im );

			 zRe =    scalar * (  z.re/z.im);
			 zIm =  - scalar;
		 }//endif

		 z.re = zRe;
		 z.im = zIm;
	 }//end inv(Complex)



	 /**
	 * Returns the <code>Complex</code> scaled by a real number.
	 *
	 * <p>
	 * <pre>
	 *     cScale((x + <i><b>i</b></i>*y), s)  =  (x*s + <i><b>i</b></i>*y*s)
	 * </pre>
	 * <p>
	 * Scaling by the real number <i>2.0</i>, doubles the magnitude, but leaves
	 * the <code>arg()</code> unchanged.  Scaling by <i>-1.0</i> keeps the magnitude
	 * the same, but flips the <code>arg()</code> by <tt><b>PI</b></tt> (180 degrees).
	 * <p>
	 * @param  scalar           A real number scale factor
	 * <p>
	 * @return                  <code>Complex</code> scaled by a real number
	 * <p>
	 * @see                     #cMul(Complex)
	 * @see                     #cDiv(Complex)
	 * @see                     #cNeg()
	 * @see                     #cNeg()
	 * @since jPicEdt 1.6
	 */


	 public Complex cScale (double scalar){
		 return  cart(scalar*re, scalar*im);
	 }//end cScale(double)



	 /**
	 * To perform z1 + z2, you write <code>z1.cAdd(z2)</code>.
	 *
	 * <p>
	 * <pre>
	 *     (a + <i><b>i</b></i>*b) + (c + <i><b>i</b></i>*d)  =  ((a+c) + <i><b>i</b></i>*(b+d))
	 * </pre>
	 * <p>
	 * @since jPicEdt 1.6
	 */


	 public Complex cAdd(Complex z){
		 return  cart(re + z.re, im + z.im);
	 }//end cAdd(Complex)


	 /**
	  * @return this+z
	  * @since jPicEdt 1.6
	 */


	 public Complex add(Complex z){
		 re += z.re;
		 im += z.im;
		 return  this;
	 }//end cAdd(Complex)



	 /**
	  * @return this-z
	  * @since jPicEdt 1.6
	 */


	 public Complex cSub(Complex z){
		 return  cart(re - z.re, im - z.im);
	 }//end cSub(Complex)

	 /**
	  * @return this -= z
	  * @since jPicEdt 1.6
	 */

	 public Complex sub(Complex z){
		 re -= z.re;
		 im -= z.im;
		 return  this;
	 }//end cSub(Complex)

	 /** @return this -= z 
	  * @since jPicEdt 1.6
	 */

	 public Complex sub(double z){
		 re -= z;
		 return  this;
	 }//end cSub(Complex)

	 /**
	 * Subtracts z from this without modifying this, and returns the result.
	 * @return this-z
	 * @since jPicEdt 1.6
	 */

	 public Complex cSub(double z){
		 return  cart(re - z, im);
	 }//end cSub(double)
	 /**
	 * CAdd z to this without modifying this, and returns the result.
	 * @return this+z
	 * @since jPicEdt 1.6
	 */

	 public Complex cAdd(double z){
		 return  cart(re + z, im);
	 }//end cAdd(double)



	 /**
	 * To perform z1 * z2, you write <code>z1.cMul(z2)</code> .
	 *
	 * <p>
	 * <pre>
	 *     (a + <i><b>i</b></i>*b) * (c + <i><b>i</b></i>*d)  =  ( (a*c) - (b*d) + <i><b>i</b></i>*((a*d) + (b*c)) )
	 * </pre>
	 * <p>
	 * @see                     #cScale(double)
	 * @since jPicEdt 1.6
	 */
	 public Complex cMul(Complex z){
		 return  cart( (re*z.re) - (im*z.im), (re*z.im) + (im*z.re) );
		 // return  cart( (re*z.re) - (im*z.im), (re + im)*(z.re + z.im) - re*z.re - im*z.im);
	 }//end cMul(Complex)

	 /** 
	  * @since jPicEdt 1.6
	  */
	 public Complex cMul(double z){
		 return  cart( re*z, im*z );
	 }//end cMul(double)

	 /** 
	  * @since jPicEdt 1.6
	  */
	 public Complex mul(Complex z){
		 double tempRe = (re*z.re) - (im*z.im);
		 im = (re*z.im) + (im*z.re);
		 re = tempRe;
		 return  this;
	 }//end mul(Complex)

	 /** 
	  * @since jPicEdt 1.6
	  */
	 public Complex mul(double z) {
		 re *= z;
		 im *= z;
		 return  this;
	 }//end mul(double)



	 /**
	 * To perform z1 / z2, you write <code>z1.cDiv(z2)</code> .
	 *
	 * <p>
	 * <pre>
	 *     (a + <i><b>i</b></i>*b) / (c + <i><b>i</b></i>*d)  =  ( (a*c) + (b*d) + <i><b>i</b></i>*((b*c) - (a*d)) ) / norm(c + <i><b>i</b></i>*d)
	 * </pre>
	 * <p>
	 * <i><b>Take care not to divide by zero!</b></i>
	 * <p>
	 * <i><b>Note:</b><ul> <code>Complex</code> arithmetic in Java never causes
	 * exceptions.  You have to deliberately check for overflow, division by
	 * zero, and so on, <u>for yourself</u>.
	 * </ul></i>
	 * <p>
	 * <i><b>Domain Restrictions:</b><ul> z1/z2 is undefined if z2 = 0
	 * </ul></i>
	 * <p>
	 * @see                     #cScale(double)
	 * @since jPicEdt 1.6
	 */

	 public Complex cDiv(Complex z){
		 Complex result =  new Complex(this);
		 div(result, z.re,z.im);
		 return  result;
	 }//end cDiv(Complex)

	 public Complex div(Complex z){
		 div(this, z.re,z.im);
		 return  this;
	 }//end div(Complex)


	 /** 
	  * @since jPicEdt 1.6
	  */
	 static private void div(Complex z1, double z2Re,double z2Im){
		 // Adapted from
		 // "Numerical Recipes in Fortran 77: The Art of Scientific Computing"
		 // (ISBN 0-521-43064-X)

		 double z1Re, z1Im;
		 double scalar;

		 if (Math.abs(z2Re) >= Math.abs(z2Im)) {
			 scalar =  1.0 / ( z2Re + z2Im*(z2Im/z2Re) );

			 z1Re =  scalar * (z1.re + z1.im*(z2Im/z2Re));
			 z1Im =  scalar * (z1.im - z1.re*(z2Im/z2Re));

		 } else {
			 scalar =  1.0 / ( z2Re*(z2Re/z2Im) + z2Im );

			 z1Re =  scalar * (z1.re*(z2Re/z2Im) + z1.im);
			 z1Im =  scalar * (z1.im*(z2Re/z2Im) - z1.re);
		 }//endif

		 z1.re = z1Re;
		 z1.im = z1Im;
	 }//end div(Complex,double,double)



	 /**
	 * Returns a <code>Complex</code> representing one of the two square roots.
	 *
	 * <p>
	 * <pre>
	 *     sqrt(z)  =  sqrt(abs(z)) * ( cos(arg(z)/2) + <i><b>i</b></i> * sin(arg(z)/2) )
	 * </pre>
	 * <p>
	 * For any <i>complex</i> number <i>z</i>, <i>sqrt(z)</i> will return the
	 * <i>complex</i> root whose <i>arg</i> is <i>arg(z)/2</i>.
	 * <p>
	 * <i><b>Note:</b><ul> There are always two square roots for each
	 * <code>Complex</code> number, except for 0 + 0<b>i</b>, or zero.  The other
	 * root is the <code>cNeg()</code> of the first one.  Just as the two roots of
	 * 4 are 2 and -2, the two roots of -1 are <b>i</b> and - <b>i</b>.
	 * </ul></i>
	 * <p>
	 * @return                  The square root whose <i>arg</i> is <i>arg(z)/2</i>.
	 * <p>
	 * @see                     #pow(Complex, double)
	 * @since jPicEdt 1.6
	 */
	 public Complex cSqrt(){
		 Complex result =  new Complex(this);
		 sqrt(result);
		 return  result;
	 }//end sqrts()



	 static private void sqrt(Complex z){
		 // with thanks to Jim Shapiro <jnshapi@argo.ecte.uswc.uswest.com>
		 // adapted from "Numerical Recipies in C" (ISBN 0-521-43108-5)
		 // by William H. Press et al

		 double mag =  z.abs();

		 if (mag > 0.0) {
			 if (z.re > 0.0) {
				 double temp =  Math.sqrt(0.5 * (mag + z.re));

				 z.re =  temp;
				 z.im =  0.5 * z.im / temp;
			 } else {
				 double temp =  Math.sqrt(0.5 * (mag - z.re));

				 if (z.im < 0.0) {
					 temp =  -temp;
				 }//endif

				 z.re =  0.5 * z.im / temp;
				 z.im =  temp;
			 }//endif
		 } else {
			 z.re =  0.0;
			 z.im =  0.0;
		 }//endif
	 }//end sqrt(Complex)



	 /**
	 * Renvoie la valeur <code>Complex</code> du <code>this</code> élevée
	 * raised to the power of a à la puissance d'un exposant
	 * <code>Complex</code> sans que <code>this</code> ne soit modifié
	 *
	 * @param  exponent         L'exposant "auquel on élève"
	 * @return                  ce <code>Complex</code> "raised to the power of" the exponent
	 * @see                     #pow(Complex, Complex)
	 * @since jPicEdt 1.6
	 */
	 public Complex cPow(Complex exponent){
		 return  Complex.pow(this, exponent);
	 }//end cPow(Complex)



	 /**
	 * Returns the number <i><b>e</b></i> "raised to" a <code>Complex</code> power.
	 *
	 * <p>
	 * <pre>
	 *     exp(x + <i><b>i</b></i>*y)  =  exp(x) * ( cos(y) + <i><b>i</b></i> * sin(y) )
	 * </pre>
	 * <p>
	 * <i><b>Note:</b><ul> The value of <i><b>e</b></i>, a transcendental number, is
	 * roughly 2.71828182846...
	 * <p>
	 *
	 * Also, the following is quietly amazing:
	 * <pre>
	 *     <i><b>e</b></i><sup><font size=+0><b>PI</b>*<i><b>i</b></i></font></sup>    =    - 1
	 * </pre>
	 * </ul>
	 * </i>
	 * <p>
	 * @return                  <i><b>e</b></i> "raised to the power of" this <code>Complex</code>
	 * <p>
	 * @see                     #cLog()
	 * @see                     #cPow(Complex exponent)
	 * @since jPicEdt 1.6
	 */
	 public Complex exp(){
		 double scalar =  Math.exp(re);                                         // e^ix = cis x
		 return  cart( scalar * Math.cos(im), scalar * Math.sin(im) );
	 }//end exp()



	 /**
	 * Returns the <i>principal</i> natural logarithm of a <code>Complex</code>
	 * number.
	 *
	 * <p>
	 * <pre>
	 *     log(z)  =  log(abs(z)) + <i><b>i</b></i> * arg(z)
	 * </pre>
	 * <p>
	 * There are infinitely many solutions, besides the principal solution.
	 * If <b>L</b> is the principal solution of <i>log(z)</i>, the others are of
	 * the form:
	 * <p>
	 * <pre>
	 *     <b>L</b> + (2*k*<b>PI</b>)*<i><b>i</b></i>
	 * </pre>
	 * <p>
	 * where k is any integer.
	 * <p>
	 * @return                  Principal <code>Complex</code> natural logarithm
	 * <p>
	 * @see                     #exp()
	 * @since jPicEdt 1.6
	 */
	public Complex cLog(){
		 return  cart( .5*Math.log(this.abs2()), this.arg() );                      // principal value
	 }//end cLog()

	 /**
	 * Returns the <i>principal</i> logarithm (<i>base 10</i>) of a
	 * <code>Complex</code> number.
	 *
	 * <p>
	 * <pre>
	 *     log10(z)  =  log(z) / log(10)
	 * </pre>
	 * <p>
	 * There are infinitely many solutions, besides the principal solution.
	 * If <b>L</b> is the principal solution of <i>log10(z)</i>, the others are
	 * of the form:
	 * <p>
	 * <pre>
	 *     <b>L</b> + (2*k*<b>PI</b>)*<i><b>i</b></i>
	 * </pre>
	 * <p>
	 * where k is any integer.
	 * <p>
	 * @return                  Principal <code>Complex</code> logarithm (base 10)
	 * <p>
	 * @see                     #exp()
	 * @see                     #log()
	 * @since jPicEdt 1.6
	 */

 /* DEPRECATED !!!
	 public Complex
	 log10 () {
		 Complex result;
		 // if (debug) enter("log10", this);

			 double scalar =  1.0/Math.log(10.0);

			 // result =  this.log().cScale(scalar);

			 result =  cart( scalar * Math.log(this.abs()), scalar * this.arg() );

		 // if (debug) leave("log10", result);
		 return  result;
	 }//end log10()
 /* */


	 /**
	 * Returns the sine of a <code>Complex</code> number.
	 *
	 * <p>
	 * <pre>
	 *     sin(z)  =  ( exp(<i><b>i</b></i>*z) - exp(-<i><b>i</b></i>*z) ) / (2*<i><b>i</b></i>)
	 * </pre>
	 * <p>
	 * @return                  The <code>Complex</code> sine
	 * <p>
	 * @see                     #asin()
	 * @see                     #sinh()
	 * @see                     #cosec()
	 * @see                     #cCos()
	 * @see                     #cTan()
	 * @since jPicEdt 1.6
	 */
	 public Complex cSin(){
		 Complex result;
			 //  sin(z)  =  ( exp(i*z) - exp(-i*z) ) / (2*i)

			 double scalar;
			 double iz_re, iz_im;
			 double _re1, _im1;
			 double _re2, _im2;

			 // iz:      i.cMul(z) ...
			 iz_re =  -im;
			 iz_im =   re;

			 // _1:      iz.exp() ...
			 scalar =  Math.exp(iz_re);
			 _re1 =  scalar * Math.cos(iz_im);
			 _im1 =  scalar * Math.sin(iz_im);

			 // _2:      iz.cNeg().exp() ...
			 scalar =  Math.exp(-iz_re);
			 _re2 =  scalar * Math.cos(-iz_im);
			 _im2 =  scalar * Math.sin(-iz_im);

			 // _1:      _1.cSub(_2) ...
			 _re1 = _re1 - _re2;                                                // !!!
			 _im1 = _im1 - _im2;                                                // !!!

			 // result:  _1.cDiv(2*i) ...
			 result =  cart( 0.5*_im1, -0.5*_re1 );
			 // ... result =  cart(_re1, _im1);
			 //     cDiv(result, 0.0, 2.0);
		 return  result;
	 }//end sin()



	 /**
	 * Returns the cosine of a <code>Complex</code> number.
	 *
	 * <p>
	 * <pre>
	 *     cos(z)  =  ( exp(<i><b>i</b></i>*z) + exp(-<i><b>i</b></i>*z) ) / 2
	 * </pre>
	 * <p>
	 * @return                  The <code>Complex</code> cosine
	 * <p>
	 * @see                     #acos()
	 * @see                     #cosh()
	 * @see                     #sec()
	 * @see                     #cSin()
	 * @see                     #cTan()
	 * @since jPicEdt 1.6
	 */


	 public Complex cCos(){
		 Complex result;
			 //  cos(z)  =  ( exp(i*z) + exp(-i*z) ) / 2

			 double scalar;
			 double iz_re, iz_im;
			 double _re1, _im1;
			 double _re2, _im2;

			 // iz:      i.cMul(z) ...
			 iz_re =  -im;
			 iz_im =   re;

			 // _1:      iz.exp() ...
			 scalar =  Math.exp(iz_re);
			 _re1 =  scalar * Math.cos(iz_im);
			 _im1 =  scalar * Math.sin(iz_im);

			 // _2:      iz.cNeg().exp() ...
			 scalar =  Math.exp(-iz_re);
			 _re2 =  scalar * Math.cos(-iz_im);
			 _im2 =  scalar * Math.sin(-iz_im);

			 // _1:      _1.cAdd(_2) ...
			 _re1 = _re1 + _re2;                                                // !!!
			 _im1 = _im1 + _im2;                                                // !!!

			 // result:  _1.cScale(0.5) ...
			 result =  cart( 0.5 * _re1, 0.5 * _im1 );
		 return  result;
	 }//end cos()


	 /** multiply this by i, which  modifies this.
	  * @since jPicEdt 1.6
	  */
	 public Complex iMul(){
		 double temp = re;
		 re = -im;
		 im = temp;
		 return this;
	 }

	 /** multiply this by i, without modifying this. 
	  * @since jPicEdt 1.6
	  */
	 public Complex cIMul(){
		 return cart(-im,re);
	 }

	 /** multiply this by -i, which  modifies this.
	  * @since jPicEdt 1.6
	  */
	 public Complex miMul(){
		 double temp = -re;
		 re = im;
		 im = temp;
		 return this;
	 }

	 /** multiply this by -i, without modifying this. 
	  * @since jPicEdt 1.6
	  */
	 public Complex cMIMul(){
		 return cart(im,-re);
	 }

	 /**
	 * Returns the tangent of a <code>Complex</code> number.
	 *
	 * <p>
	 * <pre>
	 *     tan(z)  =  sin(z) / cos(z)
	 * </pre>
	 * <p>
	 * <i><b>Domain Restrictions:</b><ul> tan(z) is undefined whenever z = (k + 1/2) * <tt><b>PI</b></tt><br>
	 * where k is any integer
	 * </ul></i>
	 * <p>
	 * @return                  The <code>Complex</code> tangent
	 * <p>
	 * @see                     #atan()
	 * @see                     #tanh()
	 * @see                     #cCot()
	 * @see                     #cSin()
	 * @see                     #cCos()
	 * @since jPicEdt 1.6
	 */
	 public Complex cTan(){
		 Complex result;
		 //  tan(z)  =  sin(z) / cos(z)

		 double scalar;
		 double iz_re, iz_im;
		 double _re1, _im1;
		 double _re2, _im2;
		 double _re3, _im3;

		 double cs_re, cs_im;

		 // sin() ...

		 // iz:      i.cMul(z) ...
		 iz_re =  -im;
		 iz_im =   re;

		 // _1:      iz.exp() ...
		 scalar =  Math.exp(iz_re);
		 _re1 =  scalar * Math.cos(iz_im);
		 _im1 =  scalar * Math.sin(iz_im);

		 // _2:      iz.cNeg().exp() ...
		 scalar =  Math.exp(-iz_re);
		 _re2 =  scalar * Math.cos(-iz_im);
		 _im2 =  scalar * Math.sin(-iz_im);

		 // _3:      _1.cSub(_2) ...
		 _re3 = _re1 - _re2;
		 _im3 = _im1 - _im2;

		 // result:  _3.cDiv(2*i) ...
		 result =  cart( 0.5*_im3, -0.5*_re3 );
		 // result =  cart(_re3, _im3);
		 // cDiv(result, 0.0, 2.0);

		 // cos() ...

		 // _3:      _1.cAdd(_2) ...
		 _re3 = _re1 + _re2;
		 _im3 = _im1 + _im2;

		 // cs:      _3.cScale(0.5) ...
		 cs_re =  0.5 * _re3;
		 cs_im =  0.5 * _im3;

		 // result:  result.cDiv(cs) ...
		 div(result, cs_re, cs_im);
		 return  result;
	 }//end tan()



	 /**
	 * Returns the cosecant of a <code>Complex</code> number.
	 *
	 * <p>
	 * <pre>
	 *     cosec(z)  =  1 / sin(z)
	 * </pre>
	 * <p>
	 * <i><b>Domain Restrictions:</b><ul> cosec(z) is undefined whenever z = k * <tt><b>PI</b></tt><br>
	 * where k is any integer
	 * </ul></i>
	 * <p>
	 * @return                  The <code>Complex</code> cosecant
	 * <p>
	 * @see                     #cSin()
	 * @see                     #sec()
	 * @see                     #cCot()
	 * @since jPicEdt 1.6
	 */
	 public Complex cosec(){
		 Complex result;
			 //  cosec(z)  =  1 / sin(z)

			 double scalar;
			 double iz_re, iz_im;
			 double _re1, _im1;
			 double _re2, _im2;

			 // iz:      i.cMul(z) ...
			 iz_re =  -im;
			 iz_im =   re;

			 // _1:      iz.exp() ...
			 scalar =  Math.exp(iz_re);
			 _re1 =  scalar * Math.cos(iz_im);
			 _im1 =  scalar * Math.sin(iz_im);

			 // _2:      iz.cNeg().exp() ...
			 scalar =  Math.exp(-iz_re);
			 _re2 =  scalar * Math.cos(-iz_im);
			 _im2 =  scalar * Math.sin(-iz_im);

			 // _1:      _1.cSub(_2) ...
			 _re1 = _re1 - _re2;                                                // !!!
			 _im1 = _im1 - _im2;                                                // !!!

			 // _result: _1.cDiv(2*i) ...
			 result =  cart( 0.5*_im1, -0.5*_re1 );
			 // result =  cart(_re1, _im1);
			 // cDiv(result, 0.0, 2.0);

			 // result:  one.cDiv(_result) ...
			 inv(result);
		 return  result;
	 }//end cosec()



	 /**
	 * Returns the secant of a <code>Complex</code> number.
	 *
	 * <p>
	 * <pre>
	 *     sec(z)  =  1 / cos(z)
	 * </pre>
	 * <p>
	 * <i><b>Domain Restrictions:</b><ul> sec(z) is undefined whenever z = (k + 1/2) * <tt><b>PI</b></tt><br>
	 * where k is any integer
	 * </ul></i>
	 * <p>
	 * @return                  The <code>Complex</code> secant
	 * <p>
	 * @see                     #cCos()
	 * @see                     #cosec()
	 * @see                     #cCot()
	 * @since jPicEdt 1.6
	 */
	 public Complex sec(){
		 Complex result;
			 //  sec(z)  =  1 / cos(z)

			 double scalar;
			 double iz_re, iz_im;
			 double _re1, _im1;
			 double _re2, _im2;

			 // iz:      i.cMul(z) ...
			 iz_re =  -im;
			 iz_im =   re;

			 // _1:      iz.exp() ...
			 scalar =  Math.exp(iz_re);
			 _re1 =  scalar * Math.cos(iz_im);
			 _im1 =  scalar * Math.sin(iz_im);

			 // _2:      iz.cNeg().exp() ...
			 scalar =  Math.exp(-iz_re);
			 _re2 =  scalar * Math.cos(-iz_im);
			 _im2 =  scalar * Math.sin(-iz_im);

			 // _1:      _1.cAdd(_2) ...
			 _re1 = _re1 + _re2;
			 _im1 = _im1 + _im2;

			 // result: _1.cScale(0.5) ...
			 result =  cart(0.5*_re1, 0.5*_im1);

			 // result:  one.cDiv(result) ...
			 inv(result);
		 return  result;
	 }//end sec()



	 /**
	 * Returns the cotangent of a <code>Complex</code> number.
	 *
	 * <p>
	 * <pre>
	 *     cot(z)  =  1 / tan(z)
	 * </pre>
	 * <p>
	 * <i><b>Domain Restrictions:</b><ul> cot(z) is undefined whenever z = k *
	 * <tt><b>PI</b></tt><br>  
	 * where k is any integer
	 * </ul></i>
	 * <p>
	 * @return                  The <code>Complex</code> cotangent
	 * <p>
	 * @see                     #cTan()
	 * @see                     #cosec()
	 * @see                     #sec()
	 * @since jPicEdt 1.6
	 */
	 public Complex cCot(){
		 Complex result;
			 //  cot(z)  =  1 / tan(z)  =  cos(z) / sin(z)

			 double scalar;
			 double iz_re, iz_im;
			 double _re1, _im1;
			 double _re2, _im2;
			 double _re3, _im3;

			 double sn_re, sn_im;

			 // cos() ...

			 // iz:      i.cMul(z) ...
			 iz_re =  -im;
			 iz_im =   re;

			 // _1:      iz.exp() ...
			 scalar =  Math.exp(iz_re);
			 _re1 =  scalar * Math.cos(iz_im);
			 _im1 =  scalar * Math.sin(iz_im);

			 // _2:      iz.cNeg().exp() ...
			 scalar =  Math.exp(-iz_re);
			 _re2 =  scalar * Math.cos(-iz_im);
			 _im2 =  scalar * Math.sin(-iz_im);

			 // _3:      _1.cAdd(_2) ...
			 _re3 = _re1 + _re2;
			 _im3 = _im1 + _im2;

			 // result:  _3.cScale(0.5) ...
			 result =  cart( 0.5*_re3, 0.5*_im3 );

			 // sin() ...

			 // _3:      _1.cSub(_2) ...
			 _re3 = _re1 - _re2;
			 _im3 = _im1 - _im2;

			 // sn:      _3.cDiv(2*i) ...
			 sn_re =    0.5 * _im3;                                             // !!!
			 sn_im =  - 0.5 * _re3;                                             // !!!

			 // result:  result.cDiv(sn) ...
			 div(result, sn_re, sn_im);
		 return  result;
	 }//end cot()



	 /**
	 * Returns the hyperbolic sine of a <code>Complex</code> number.
	 *
	 * <p>
	 * <pre>
	 *     sinh(z)  =  ( exp(z) - exp(-z) ) / 2
	 * </pre>
	 * <p>
	 * @return                  The <code>Complex</code> hyperbolic sine
	 * <p>
	 * @see                     #cSin()
	 * @see                     #asinh()
	 * @since jPicEdt 1.6
	 */
	 public Complex sinh(){
		 Complex result;
			 //  sinh(z)  =  ( exp(z) - exp(-z) ) / 2

			 double scalar;
			 double _re1, _im1;
			 double _re2, _im2;

			 // _1:      z.exp() ...
			 scalar =  Math.exp(re);
			 _re1 =  scalar * Math.cos(im);
			 _im1 =  scalar * Math.sin(im);

			 // _2:      z.cNeg().exp() ...
			 scalar =  Math.exp(-re);
			 _re2 =  scalar * Math.cos(-im);
			 _im2 =  scalar * Math.sin(-im);

			 // _1:      _1.cSub(_2) ...
			 _re1 = _re1 - _re2;                                                // !!!
			 _im1 = _im1 - _im2;                                                // !!!

			 // result:  _1.cScale(0.5) ...
			 result =  cart( 0.5 * _re1, 0.5 * _im1 );
		 return  result;
	 }//end sinh()



	 /**
	 * Returns the hyperbolic cosine of a <code>Complex</code> number.
	 *
	 * <p>
	 * <pre>
	 *     cosh(z)  =  ( exp(z) + exp(-z) ) / 2
	 * </pre>
	 * <p>
	 * @return                  The <code>Complex</code> hyperbolic cosine
	 * <p>
	 * @see                     #cCos()
	 * @see                     #acosh()
	 * @since jPicEdt 1.6
	 */
	 public Complex cosh(){
		 Complex result;
			 //  cosh(z)  =  ( exp(z) + exp(-z) ) / 2

			 double scalar;
			 double _re1, _im1;
			 double _re2, _im2;

			 // _1:      z.exp() ...
			 scalar =  Math.exp(re);
			 _re1 =  scalar * Math.cos(im);
			 _im1 =  scalar * Math.sin(im);

			 // _2:      z.cNeg().exp() ...
			 scalar =  Math.exp(-re);
			 _re2 =  scalar * Math.cos(-im);
			 _im2 =  scalar * Math.sin(-im);

			 // _1:  _1.cAdd(_2) ...
			 _re1 = _re1 + _re2;                                                // !!!
			 _im1 = _im1 + _im2;                                                // !!!

			 // result:  _1.cScale(0.5) ...
			 result =  cart( 0.5 * _re1, 0.5 * _im1 );
		 return  result;
	 }//end cosh()



	 /**
	 * Returns the hyperbolic tangent of a <code>Complex</code> number.
	 *
	 * <p>
	 * <pre>
	 *     tanh(z)  =  sinh(z) / cosh(z)
	 * </pre>
	 * <p>
	 * @return                  The <code>Complex</code> hyperbolic tangent
	 * <p>
	 * @see                     #cTan()
	 * @see                     #atanh()
	 * @since jPicEdt 1.6
	 */
	 public Complex tanh(){
		 Complex result;
			 //  tanh(z)  =  sinh(z) / cosh(z)

			 double scalar;
			 double _re1, _im1;
			 double _re2, _im2;
			 double _re3, _im3;

			 double ch_re, ch_im;

			 // sinh() ...

			 // _1:      z.exp() ...
			 scalar =  Math.exp(re);
			 _re1 =  scalar * Math.cos(im);
			 _im1 =  scalar * Math.sin(im);

			 // _2:      z.cNeg().exp() ...
			 scalar =  Math.exp(-re);
			 _re2 =  scalar * Math.cos(-im);
			 _im2 =  scalar * Math.sin(-im);

			 // _3:      _1.cSub(_2) ...
			 _re3 =  _re1 - _re2;
			 _im3 =  _im1 - _im2;

			 // result:  _3.cScale(0.5) ...
			 result =  cart(0.5*_re3, 0.5*_im3);

			 // cosh() ...

			 // _3:      _1.cAdd(_2) ...
			 _re3 =  _re1 + _re2;
			 _im3 =  _im1 + _im2;

			 // ch:      _3.cScale(0.5) ...
			 ch_re =  0.5 * _re3;
			 ch_im =  0.5 * _im3;

			 // result:  result.cDiv(ch) ...
			 div(result, ch_re, ch_im);
		 return  result;
	 }//end tanh()



	 /**
	 * Returns the <i>principal</i> arc sine of a <code>Complex</code> number.
	 *
	 * <p>
	 * <pre>
	 *     asin(z)  =  -<i><b>i</b></i> * log(<i><b>i</b></i>*z + sqrt(1 - z*z))
	 * </pre>
	 * <p>
	 * There are infinitely many solutions, besides the principal solution.
	 * If <b>A</b> is the principal solution of <i>asin(z)</i>, the others are
	 * of the form:
	 * <p>
	 * <pre>
	 *     k*<b>PI</b> + (-1)<sup><font size=-1>k</font></sup>  * <b>A</b>
	 * </pre>
	 * <p>
	 * where k is any integer.
	 * <p>
	 * @return                  Principal <code>Complex</code> arc sine
	 * <p>
	 * @see                     #cSin()
	 * @see                     #sinh()
	 * @since jPicEdt 1.6
	 */
	 public Complex asin(){
		 Complex result;
			 //  asin(z)  =  -i * log(i*z + sqrt(1 - z*z))

			 double _re1, _im1;

			 // _1:      one.cSub(z.cMul(z)) ...
			 _re1 =  1.0 - ( (re*re) - (im*im) );
			 _im1 =  0.0 - ( (re*im) + (im*re) );

			 // result:  _1.sqrt() ...
			 result =  cart(_re1, _im1);
			 sqrt(result);

			 // _1:      z.cMul(i) ...
			 _re1 =  - im;
			 _im1 =  + re;

			 // result:  _1.cAdd(result) ...
			 result.re =  _re1 + result.re;
			 result.im =  _im1 + result.im;

			 // _1:      result.log() ...
			 _re1 =  .5*Math.log(result.abs2());
			 _im1 =  result.arg();

			 // result:  i.cNeg().cMul(_1) ...
			 result.re =    _im1;
			 result.im =  - _re1;
		 return  result;
	 }//end asin()



	 /**
	 * Returns the <i>principal</i> arc cosine of a <code>Complex</code> number.
	 *
	 * <p>
	 * <pre>
	 *     acos(z)  =  -<i><b>i</b></i> * log( z + <i><b>i</b></i> * sqrt(1 - z*z) )
	 * </pre>
	 * <p>
	 * There are infinitely many solutions, besides the principal solution.
	 * If <b>A</b> is the principal solution of <i>acos(z)</i>, the others are
	 * of the form:
	 * <p>
	 * <pre>
	 *     2*k*<b>PI</b> +/- <b>A</b>
	 * </pre>
	 * <p>
	 * where k is any integer.
	 * <p>
	 * @return                  Principal <code>Complex</code> arc cosine
	 * <p>
	 * @see                     #cCos()
	 * @see                     #cosh()
	 * @since jPicEdt 1.6
	 */
	 public Complex acos(){
		 Complex result;
			 //  acos(z)  =  -i * log( z + i * sqrt(1 - z*z) )

			 double _re1, _im1;

			 // _1:      one.cSub(z.cMul(z)) ...
			 _re1 =  1.0 - ( (re*re) - (im*im) );
			 _im1 =  0.0 - ( (re*im) + (im*re) );

			 // result:  _1.sqrt() ...
			 result =  cart(_re1, _im1);
			 sqrt(result);

			 // _1:      i.cMul(result) ...
			 _re1 =  - result.im;
			 _im1 =  + result.re;

			 // result:  z.cAdd(_1) ...
			 result.re =  re + _re1;
			 result.im =  im + _im1;

			 // _1:      result.log()
			 _re1 =  .5*Math.log(result.abs2());
			 _im1 =  result.arg();

			 // result:  i.cNeg().cMul(_1) ...
			 result.re =    _im1;
			 result.im =  - _re1;
		 return  result;
	 }//end acos()



	 /**
	 * Returns the <i>principal</i> arc tangent of a <code>Complex</code> number.
	 *
	 * <p>
	 * <pre>
	 *     atan(z)  =  -<i><b>i</b></i>/2 * log( (<i><b>i</b></i>-z)/(<i><b>i</b></i>+z) )
	 * </pre>
	 * <p>
	 * There are infinitely many solutions, besides the principal solution.
	 * If <b>A</b> is the principal solution of <i>atan(z)</i>, the others are
	 * of the form:
	 * <p>
	 * <pre>
	 *     <b>A</b> + k*<b>PI</b>
	 * </pre>
	 * <p>
	 * where k is any integer.
	 * <p>
	 * <i><b>Domain Restrictions:</b><ul> atan(z) is undefined for z = + <b>i</b> or z = - <b>i</b>
	 * </ul></i>
	 * <p>
	 * @return                  Principal <code>Complex</code> arc tangent
	 * <p>
	 * @see                     #cTan()
	 * @see                     #tanh()
	 * @since jPicEdt 1.6
	 */
	 public Complex atan(){
		 Complex result;
		 //  atan(z)  =  -i/2 * log( (i-z)/(i+z) )

		 double _re1, _im1;

		 // result:  i.cSub(z) ...
		 result =  cart(- re, 1.0 - im);

		 // _1:      i.cAdd(z) ...
		 _re1 =  + re;
		 _im1 =  1.0 + im;

		 // result:  result.cDiv(_1) ...
		 div(result, _re1, _im1);

		 // _1:      result.log() ...
		 _re1 =  .5*Math.log(result.abs2());
		 _im1 =  result.arg();

		 // result:  half_i.cNeg().cMul(_2) ...
		 result.re =   0.5*_im1;
		 result.im =  -0.5*_re1;
		 return  result;
	 }//end atan()



	 /**
	 * Returns the <i>principal</i> inverse hyperbolic sine of a
	 * <code>Complex</code> number.
	 *
	 * <p>
	 * <pre>
	 *     asinh(z)  =  log(z + sqrt(z*z + 1))
	 * </pre>
	 * <p>
	 * There are infinitely many solutions, besides the principal solution.
	 * If <b>A</b> is the principal solution of <i>asinh(z)</i>, the others are
	 * of the form:
	 * <p>
	 * <pre>
	 *     k*<b>PI</b>*<b><i>i</i></b> + (-1)<sup><font size=-1>k</font></sup>  * <b>A</b>
	 * </pre>
	 * <p>
	 * where k is any integer.
	 * <p>
	 * @return                  Principal <code>Complex</code> inverse hyperbolic sine
	 * <p>
	 * @see                     #sinh()
	 * @since jPicEdt 1.6
	 */
	 public Complex asinh(){
		 Complex result;
			 //  asinh(z)  =  log(z + sqrt(z*z + 1))

			 double _re1, _im1;

			 // _1:      z.cMul(z).cAdd(one) ...
			 _re1 =  ( (re*re) - (im*im) ) + 1.0;
			 _im1 =  ( (re*im) + (im*re) ) + 0.0;

			 // result:  _1.sqrt() ...
			 result =  cart(_re1, _im1);
			 sqrt(result);

			 // result:  z.cAdd(result) ...
			 result.re =  re + result.re;                                       // !
			 result.im =  im + result.im;                                       // !

			 // _1:      result.log() ...
			 _re1 =  .5*Math.log(result.abs2());
			 _im1 =  result.arg();

			 // result:  _1 ...
			 result.re =  _re1;
			 result.im =  _im1;

			 /*
			 * Many thanks to the mathematicians of aus.mathematics and sci.math,
			 * and to Zdislav V. Kovarik of the  Department  of  Mathematics  and
			 * Statistics,     McMaster     University     and    John    McGowan
			 * <jmcgowan@inch.com> in particular, for their advice on the current
			 * naming conventions for "area/argumentus sinus hyperbolicus".
			 */

		 return  result;
	 }//end asinh()



	 /**
	 * Returns the <i>principal</i> inverse hyperbolic cosine of a
	 * <code>Complex</code> number.
	 *
	 * <p>
	 * <pre>
	 *     acosh(z)  =  log(z + sqrt(z*z - 1))
	 * </pre>
	 * <p>
	 * There are infinitely many solutions, besides the principal solution.
	 * If <b>A</b> is the principal solution of <i>acosh(z)</i>, the others are
	 * of the form:
	 * <p>
	 * <pre>
	 *     2*k*<b>PI</b>*<b><i>i</i></b> +/- <b>A</b>
	 * </pre>
	 * <p>
	 * where k is any integer.
	 * <p>
	 * @return                  Principal <code>Complex</code> inverse hyperbolic cosine
	 * <p>
	 * @see                     #cosh()
	 * @since jPicEdt 1.6
	 */
	 public Complex acosh(){
		 Complex result;
			 //  acosh(z)  =  log(z + sqrt(z*z - 1))

			 double _re1, _im1;

			 // _1:  z.cMul(z).cSub(one) ...
			 _re1 =  ( (re*re) - (im*im) ) - 1.0;
			 _im1 =  ( (re*im) + (im*re) ) - 0.0;

			 // result:  _1.sqrt() ...
			 result =  cart(_re1, _im1);
			 sqrt(result);

			 // result:  z.cAdd(result) ...
			 result.re =  re + result.re;                                       // !
			 result.im =  im + result.im;                                       // !

			 // _1:  result.log() ...
			 _re1 =  .5*Math.log(result.abs2());
			 _im1 =  result.arg();

			 // result:  _1 ...
			 result.re =  _re1;
			 result.im =  _im1;
		 return  result;
	 }//end acosh()



	 /**
	 * Returns the <i>principal</i> inverse hyperbolic tangent of a
	 * <code>Complex</code> number.
	 *
	 * <p>
	 * <pre>
	 *     atanh(z)  =  1/2 * log( (1+z)/(1-z) )
	 * </pre>
	 * <p>
	 * There are infinitely many solutions, besides the principal solution.
	 * If <b>A</b> is the principal solution of <i>atanh(z)</i>, the others are
	 * of the form:
	 * <p>
	 * <pre>
	 *     <b>A</b> + k*<b>PI</b>*<b><i>i</i></b>
	 * </pre>
	 * <p>
	 * where k is any integer.
	 * <p>
	 * <i><b>Domain Restrictions:</b><ul> atanh(z) is undefined for z = + 1 or z = - 1
	 * </ul></i>
	 * <p>
	 * @return                  Principal <code>Complex</code> inverse hyperbolic tangent
	 * <p>
	 * @see                     #tanh()
	 * @since jPicEdt 1.6
	 */
	 public Complex atanh(){
		 Complex result;
			 //  atanh(z)  =  1/2 * log( (1+z)/(1-z) )

			 double _re1, _im1;

			 // result:  one.cAdd(z) ...
			 result =  cart(1.0 + re, + im);

			 // _1:      one.cSub(z) ...
			 _re1 =  1.0 - re;
			 _im1 =  - im;

			 // result:  result.cDiv(_1) ...
			 div(result, _re1, _im1);

			 // _1:      result.log() ...
			 _re1 =  .5*Math.log(result.abs2());
			 _im1 =  result.arg();

			 // result:  _1.cScale(0.5) ...
			 result.re =  0.5 * _re1;
			 result.im =  0.5 * _im1;
		 return  result;
	 }//end atanh()



	 /**
	 * Converts a <code>Complex</code> into a {@link java.lang.String String} of the form
	 * <tt>(</tt><i>a</i><tt> + </tt><i>b</i><tt>i)</tt>.
	 *
	 * <p>
	 * This enables a <code>Complex</code> to be easily printed.  For example, if
	 * <tt>z</tt> was <i>2 - 5<b>i</b></i>, then
	 * <pre>
	 *     System.out.println("z = " + z);
	 * </pre>
	 * would print something like
	 * <pre>
	 *     z = (2.0 - 5.0i)
	 * </pre>
	 * <!--
	 * <i><b>Note:</b><ul>Concatenating {@link java.lang.String <tt>String</tt>}s, using a system
	 * overloaded meaning of the "<tt>+</tt>" operator, in fact causes the
	 * <tt>toString()</tt> method to be invoked on the object <tt>z</tt> at
	 * runtime.</ul></i>
	 * -->
	 * <p>
	 * @return                  {@link java.lang.String <tt>String</tt>} containing the cartesian coordinate representation
	 * <p>
	 * @see                     #cart(double re, double im)
	 * @since jPicEdt 1.6
	 */
	 public String toString(){
		 StringBuffer result =  new StringBuffer("(");
		 result.append(re);

		 if (im < 0.0) {                                                        // ...remembering NaN & Infinity
			 result.append(" - ").append(-im);
		 } else if (1.0 / im == Double.NEGATIVE_INFINITY) {
			 result.append(" - ").append(0.0);
		 } else {
			 result.append(" + ").append(+im);
		 }//endif

		 result.append("i)");
		 return  result.toString();
	 }//end toString()



	 /*
			 I know a young man called Daniel,
			 When you meet him, you'll like him, and you'll
			 Find him so true, so human and new,
			 You'll want to live life with no manual.
	 */

 }//end Complex




 /*           Jim Shapiro <jnshapi@argo.ecte.uswc.uswest.com>


							Priyantha Jayanetti
						---------------------------
						email: pidge@eece.maine.edu

				Dept.  of Electrical & Computer Engineering
						University of Maine,  Orono


							 Mr.  Daniel Hirsch
					  <R.D.Hirsch@red-deer.demon.co.uk>


 /*             C A U T I O N   E X P L O S I V E   B O L T S
 --                     REMOVE BEFORE ENGAGING REPLY
 //
 // Kelly and Sandy Anderson <kelsan@explosive-alma-services-bolts.co.uk>
 // (alternatively            kelsan_odoodle at ya who period, see oh em)
 // Alexander (Sandy)  1B5A DF3D A3D9 B932 39EB  3F1B 981F 4110 27E1 64A4
 // Kelly              673F 6751 6DBA 196F E8A8  6D87 4AEC F35E E9AD 099B
 // Homepages             http://www.explosive-alma-services-bolts.co.uk/
*/
