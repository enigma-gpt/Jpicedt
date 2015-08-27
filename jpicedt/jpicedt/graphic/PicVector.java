// PicVector.java --- -*- coding: iso-8859-1 -*-
// August 8, 2003 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2006 Sylvain Reynal
//
// Author: Sylvain Reynal
//         Département de Physique
//         École Nationale Supérieure de l'Électronique et de ses Applications (ÉNSÉA)
//         6, avenue du Ponceau
//         95014 CERGY CEDEX
//         FRANCE
//
//         Tel : +33 130 736 245
//         Fax : +33 130 736 667
//         e-mail : reynal@ensea.fr
//
// Version: $Id: PicVector.java,v 1.19 2013/03/27 07:00:38 vincentb1 Exp $
// Keywords:
// X-URL: http://www.jpicedt.org/
//
// Ce logiciel est régi par la licence CeCILL soumise au droit français et respectant les principes de
// diffusion des logiciels libres. Vous pouvez utiliser, modifier et/ou redistribuer ce programme sous les
// conditions de la licence CeCILL telle que diffusée par le CEA, le CNRS et l'INRIA sur le site
// "http://www.cecill.info".
//
// En contrepartie de l'accessibilité au code source et des droits de copie, de modification et de
// redistribution accordés par cette licence, il n'est offert aux utilisateurs qu'une garantie limitée.  Pour
// les mêmes raisons, seule une responsabilité restreinte pèse sur l'auteur du programme, le titulaire des
// droits patrimoniaux et les concédants successifs.
//
// À cet égard l'attention de l'utilisateur est attirée sur les risques associés au chargement, à
// l'utilisation, à la modification et/ou au développement et à la reproduction du logiciel par l'utilisateur
// étant donné sa spécificité de logiciel libre, qui peut le rendre complexe à manipuler et qui le réserve
// donc à des développeurs et des professionnels avertis possédant des connaissances informatiques
// approfondies.  Les utilisateurs sont donc invités à charger et tester l'adéquation du logiciel à leurs
// besoins dans des conditions permettant d'assurer la sécurité de leurs systèmes et ou de leurs données et,
// plus généralement, à l'utiliser et l'exploiter dans les mêmes conditions de sécurité.
//
// Le fait que vous puissiez accéder à cet en-tête signifie que vous avez pris connaissance de la licence
// CeCILL, et que vous en avez accepté les termes.
//
/// Commentary:

//



/// Code:
package jpicedt.graphic;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import java.lang.Math.*;
import jpicedt.util.math.Complex;

import static java.lang.Math.sqrt;
import static java.lang.Math.acos;
import static java.lang.Math.PI;
import static java.lang.Math.toDegrees;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static jpicedt.Log.*;
/**
 * This class encapsulates a geometrical vector (ie it has absolutely no relation with java.util.Vector !).
 * It is based on PicPoint, from where it conveniently inherits formatting methods, yet add some useful
 * vector-calculus related methods, e.g. dot product, norm, etc... Being a subclass of PicPoint is a major
 * benefit where polymorphism is concerned, since instances
 * of this class can be fed as argument to most getters and setters in jpicedt.graphic.model.Element.
 * @since jPicEdt 1.3.3
 * @author Sylvain Reynal
 * @version $Id: PicVector.java,v 1.19 2013/03/27 07:00:38 vincentb1 Exp $
 */
public class PicVector extends PicPoint{

	/** the (1,0) unit vector ; this may be used to compute e.g. angles, projections, ... */
	final public static PicVector X_AXIS = new PicVector(1,0);

	/** the (0,1) unit vector ; this may be used to compute e.g. angles, projections, ...  */
	final public static PicVector Y_AXIS = new PicVector(0,1);

	// inherited from Point2D.Double via PicPoint : double x,y

	/**
	 * Construct a null vector.
	 */
	public PicVector() {
		super();
	}




	/**
	 * Clone the given vector.
	 */
	public PicVector(PicVector picVec){
		this(picVec.x, picVec.y);
	}

	/**
	 * Construct a vector with coordinates (x,y)
	 */
	public PicVector(double x, double y){
		super(x,y);
	}

	/**
	 * Construct a vector with same coordinate as real part and imaginary part
	 * as a complex number.
	 * @since jPicEdt 1.6
	 */
	public PicVector(Complex z){
		this(z.re(),z.im());
	}


	/**
	 * Construct a vector from the given pair of Number (using their double value).
	 */
	public PicVector(Number x, Number y){
		super(x,y);
	}

	/**
	 * Construct a vector from the first two elements of the given array.
	 */
	public PicVector(float[] f){
		super(f);
	}

	/**
	 * Construct a vector from the first two elements of the given array.
	 */
	public PicVector(double[] f){
		super(f);
	}

	/**
	 * Construct a point by parsing a String similar to that given by the
	 * {@link #toString() toString()} method, ie <code>(x,y)</code>.
	 * <br><b>author:</b> Sylvain Reynal
	 * @since jpicedt 1.3.3
	 */
	public PicVector(String str) throws NumberFormatException {
		super(str);
	}

	/**
	 * Construct a new PicVector from the two given points
	 */
	public PicVector(Point2D pt1, Point2D pt2){
		setCoordinates(pt1,pt2);
	}

	/**
	 * Construct a new PicVector pointing from (0,0) to the given point
	 */
	public PicVector(Point2D pt){
		super(pt);
	}

	/**
	 * Construct a new PicVector from the two end-points of the given Line2D object.
	 */
	public PicVector(Line2D line){
		this(line.getP1(), line.getP2());
	}

	/**
	 * Set the coordinates of this vector from the two given points.
	 * @return this for convenience
	 */
	public PicVector setCoordinates(Point2D pt1, Point2D pt2){
		this.x = pt2.getX() - pt1.getX();
		this.y = pt2.getY() - pt1.getY();
		return this;
	}

	/**
	 * Set the coordinates of this vector from the given Line2D object
	 * @return this for convenience
	 */
	public PicVector setCoordinates(Line2D line){
		setCoordinates(line.getP1(), line.getP2());
		return this;
	}

	// ========================== GEOM. UTILITIES ====================

	/**
	 * Add the given vector to this vector, which modifies this, and return
	 * this for convenience.
	 */
	public PicVector add(PicPoint other){
		x += other.x;
		y += other.y;
		return this;
	}

	/**
	 * Add the given vector scaled by "a" to this vector, which modifies this,
	 * and return this for convenience.
	 */
	public PicVector add(PicPoint other, double a){
		x += a*other.x;
		y += a*other.y;
		return this;
	}

	/**
	 * Subtract the given vector from this vector, which modifies this, and
	 * return this for convenience.
	 */
	public PicVector subtract(PicPoint other){
		x -= other.x;
		y -= other.y;
		return this;
	}

	/**
	 * Make the subtraction the given vector from this vector, without
	 * modifying this, and return the result.
	 * @since jPicEdt 1.6
	 */
	public PicVector cSub(PicPoint other){
		PicVector ret = new PicVector(this);
		ret.x -= other.x;
		ret.y -= other.y;
		return ret;
	}

	/**
	 * Make the addition the given vector to this vector, without
	 * modifying this, and return the result.
	 * @since jPicEdt 1.6
	 */
	public PicVector cAdd(PicPoint other){
		PicVector ret = new PicVector(this);
		ret.x += other.x;
		ret.y += other.y;
		return ret;
	}

	/**
	 * Make the addition the given other vector scaled by a to this vector,
	 * without modifying this nor other, and return the result.
	 * @since jPicEdt 1.6
	 */
	public PicVector cAdd(PicVector other,double a){
		PicVector ret = new PicVector(this);
		ret.x += a*other.x;
		ret.y += a*other.y;
		return ret;
	}


	/**
	 * Rotate this by PI/2 radian, which modifies this, and return this for
	 * convenience. This is the same thing as multiplying the equivalent
	 * complex number by i = exp(i*pi/2).
	 * @since jPicEdt 1.6
	 * @see  #rotate(double angle)
	 * @see  #cIMul()
	 * @see  #cMIMul()
	 */
	public PicVector iMul(){
		double _x = x;
		x = -y;
		y = _x;
		return this;
	}

	/**
	 * Rotate this by PI/2 radian, without modifying this, and return the
	 * result of this rotation. This is the same thing as multiplying the equivalent
	 * complex number by i = exp(i*pi/2).
	 * @since jPicEdt 1.6
	 * @see  #rotate(double angle)
	 * @see  #iMul()
	 * @see  #miMul()
	 * @see  #cMIMul()
	 */
	public PicVector cIMul(){
		return (new PicVector(this)).iMul();
	}

	/**
	 * Rotate this by -PI/2 radian, which modifies this, and return this for
	 * convenience. This is the same thing as multiplying the equivalent
	 * complex number by i = exp(-i*pi/2).
	 * @since jPicEdt 1.6
	 * @see  #rotate(double angle)
	 * @see  #cIMul()
	 */
	public PicVector miMul(){
		double _x = -x;
		x = y;
		y = _x;
		return this;
	}

	/**
	 * Rotate this by -PI/2 radian, without modifying this, and return the
	 * result of this rotation. This is the same thing as multiplying the equivalent
	 * complex number by -i = exp(-i*pi/2).
	 * @since jPicEdt 1.6
	 * @see  #rotate(double angle)
	 * @see  #iMul()
	 */
	public PicVector cMIMul(){
		return (new PicVector(this)).miMul();
	}

	/** Multiplie le vecteur this par s, sans modifier this. Renvoie le résultat.
	 * @since jPicEdt 1.6
	 */
	public PicVector cMul(double s){
		return new PicVector(x*s,y*s);
	}

	/**
	 * Return the norm of this vector
	 */
	public double norm(){
		return sqrt(x*x+y*y);
	}

	/**
	 * Return the squared of the norm of this vector
	 */
	public double norm2(){
		return x*x+y*y;
	}

	/**
	 * Return the abs(x)+abs(y) norm1 for this vector=(x,y)
	 */
	public double norm1(){
		return Math.abs(x)+Math.abs(y);
	}


	/**
	 * Normalize this vector so that this becomes a unitary vector
	 * Does nothing if this vector is a null-vector.
	 * @return this for convenience
	 */
	public PicVector normalize(){
		double norm = norm();
		if (norm==0) return this;
		this.x /= norm;
		this.y /= norm;
		return this;
	}

	/**
	 * Return the dot product of this vector with the given vector <code>other</code>
	 */
	public double dot(PicPoint other){
		return x * other.x + y * other.y;
	}

	/**
	 * Return whether this vector is orthogonal to the given vector
	 */
	public boolean isOrthogonal(PicVector other){
		return this.dot(other)==0;
	}

	/**
	 * Return whether this vector is proportional to the given vector
	 */
	public boolean isColinear(PicVector other){
		return this.det(other)==0;
	}

	/**
	 * Multiply each coordinate of this vector by the given double
	 * @return this for convenience
	 */
	public PicVector scale(double a){
		x *= a;
		y *= a;
		return this;
	}

	/**
	 * Multiply each coordinate of this vector by the given pair of double
	 * @param ax scale factor for the X-coord
	 * @param ay scale factor for the Y-coord
	 * @return this for convenience
	 */
	public PicVector scale(double ax, double ay){
		x *= ax;
		y *= ay;
		return this;
	}

	/**
	 * Change the sign of each coordinate of this vector
	 * @return this for convenience
	 */
	public PicVector inverse(){
		x = -x;
		y = -y;
		return this;
	}

	/**
	 * @return un clone de ce <code>PicVector</code> dont le signe des
	 * coordonnées est changé.
	 */
	public PicVector cInverse(){
		return new PicVector(-x, -y);
	}


	/**
	 * Return true if this vector has a null-norm
	 */
	public boolean isNull(){
		return (x==0 && y==0);
	}

	/**
	 * Return the determinant of {{x,y},{other.x,other.y}} ; this is aka
	 * vector product, where only the z-coordinate
	 * gets returned.
	 */
	public double det(PicVector other){
		return x * other.y - y * other.x;
	}

	/**
	 * Return the (CCW oriented) angle, in radians, of the other vector
	 * relative to this vector.
	 * @return an angle in the range (-PI, PI]
	 */
	public double angle(PicVector other){
		double thisNorm = this.norm();
		if (thisNorm==0) return 0;
		double otherNorm = other.norm();
		if (otherNorm==0) return 0;
		double thisDetOther = this.det(other);
		double thisDotOther = this.dot(other);
		if (thisDetOther==0) {
			if (thisDotOther < 0) return PI;
			else return 0;
		}
		double theta = acos(thisDotOther / thisNorm / otherNorm);
		if (thisDetOther < 0) theta = - theta;
		return theta;
	}

	/**
	 * Return the (CCW oriented) angle between this vector and the given vector, in degrees.
	 */
	public double angleDegrees(PicVector other){
		return toDegrees(this.angle(other));
	}

	/**
	 * Rotate this vector by the given (CCW-oriented) angle in radians.<br>
	 * Current implementation arranges for a very fast code if angle is PI, PI/2 or
	 * -PI/2.
	 * @return this for convenience
	 * @see  #iMul()
	 * @see  #cIMul()
	 */
	public PicVector rotate(double angle){
		if (angle == PI){
			if (DEBUG) debug( "angle=PI (exact)");
			return inverse();
		}
		else if (angle == PI/2){
			if (DEBUG) debug( "angle=PI/2 (exact)");
			double _x = -this.y;
			this.y = this.x;
			this.x = _x;
		}
		else if (angle == -PI/2){
			if (DEBUG) debug( "angle=-PI/2 (exact)");
			double _x = this.y;
			this.y = -this.x;
			this.x = _x;
		}
		else {
			double cosTheta = cos(angle);
			double sinTheta = sin(angle);
			double _x = cosTheta * this.x - sinTheta * this.y;
			this.y = sinTheta * this.x + cosTheta * this.y;
			this.x = _x;
		}
		return this;
	}

	// ========================== STATIC UTILITIES =======================

	/** validation tests */
	public static void main(String[] args){
		PicVector v = new PicVector(1,1);
		PicVector w = new PicVector(v);
		w.rotate(0);
		System.out.println("dot="+v.dot(w));
		System.out.println("det="+v.det(w));
		System.out.println("angle="+v.angle(w));
		System.out.println("angleDeg="+v.angleDegrees(w));
	}

	/**
	 * Return a normalized copy of the given vector.
	 * If dest==null, it is allocated and returned for convenience.
	 */
	public static PicVector normalize(PicVector src, PicVector dest){
		if (dest==null) dest = new PicVector();
		dest.setCoordinates(src);
		dest.normalize();
		return dest;
	}

	/**
	 * Return a rotated copy of the given vector. If dest==null, it is allocated and returned for convenience..
	 * @see #rotate(double)
	 */
	public static PicVector rotate(PicVector src, PicVector dest, double angle){
		if (dest==null) dest = new PicVector();
		dest.setCoordinates(src);
		dest.rotate(angle);
		return dest;
	}

	/**
	 * Return a scaled copy of the given vector. If dest==null, it is allocated and returned for convenience..
	 * @see #rotate(double)
	 */
	public static PicVector scale(PicVector src, PicVector dest, double scale){
		if (dest==null) dest = new PicVector();
		dest.setCoordinates(src);
		dest.scale(scale);
		return dest;
	}

	/**
	 * Return a unitary vector pointing from startPt to endPt
	 */
	public static PicVector getDirector(PicPoint startPt, PicPoint endPt){
		return getDirector(startPt, endPt, null);
	}

	/**
	 * Return a unitary vector pointing from startPt to endPt ; if dest is non-null, it's filled with the result
	 * and returned for convenience ; otherwise, a new PicPoint gets allocated.
	 */
	public static PicVector getDirector(PicPoint startPt, PicPoint endPt, PicVector dest){
		if (dest == null) dest = new PicVector();
		dest.setCoordinates(startPt, endPt);
		dest.normalize();
		return dest;
	}

	/**
	 * Return a unitary vector pointing from startPt to endPt ; if dest is non-null, it's filled with the result
	 * and returned for convenience ; otherwise, a new PicPoint gets allocated.
	 */
	public static PicVector getDirector(double startPtX, double startPtY, double endPtX, double endPtY, PicVector dest){
		if (dest == null) dest = new PicVector();
		dest.x = endPtX-startPtX;
		dest.y = endPtY-startPtY;
		dest.normalize();
		return dest;
	}

	/**
	 * Return a UNITARY vector orthogonal to the vector pointing from startPt to endPt and
	 * built by rotating this vector CCW.
	 */
	public static PicVector getOrthogonal(PicPoint startPt, PicPoint endPt){

		PicVector orthogonal = new PicVector(startPt, endPt);
		orthogonal.rotate(PI/2);
		orthogonal.normalize();
		return orthogonal;

	}

}
