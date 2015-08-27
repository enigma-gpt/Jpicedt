// PicPoint.java --- -*- coding: iso-8859-1 -*-
// 1999 - jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: PicPoint.java,v 1.25 2013/03/27 07:00:43 vincentb1 Exp $
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

import jpicedt.util.math.Complex;
import java.awt.*;
import java.awt.geom.*;
import java.util.StringTokenizer;
import java.util.Comparator;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Enhancement of Point2D.Double with convenient formatting methods and constructors.
 * @since jpicedt 1.0
 * @author Sylvain Reynal
 * @version $Id: PicPoint.java,v 1.25 2013/03/27 07:00:43 vincentb1 Exp $
 */

public class PicPoint extends Point2D.Double implements Cloneable {

	/**
	 * Construct a (0,0) point.
	 */
	public PicPoint() {
		super();
	}

	/**
	 * Clone the given point.
	 */
	public PicPoint(Point2D p){
		super(p.getX(), p.getY());
	}

	/**
	 * Creates and returns a deep copy of this PicPoint.
	 */
	public PicPoint clone(){
		return new PicPoint(this);
	}

	/**
	 * Construct a new PicPoint "p" located on the line joining p1 with p2, so that
	 * (p1,p) = ratio * (p1,p2)
	 * @param ratio any double, positive or not.
	 */
	public PicPoint(Point2D p1, Point2D p2, double ratio){
		x = p1.getX() + ratio * (p2.getX() - p1.getX());
		y = p1.getY() + ratio * (p2.getY() - p1.getY());
	}

	/**
	 * Construct (x,y)
	 */
	public PicPoint(double x, double y){
		super(x,y);
	}

	/**
	 * Construct a point from the given pair of Number (using their double value).
	 */
	public PicPoint(Number x, Number y){
		super(x.doubleValue(), y.doubleValue());
	}

	/**
	 * Construct a point from the first two elements of the given array.
	 */
	public PicPoint(float[] f){
		super(f[0], f[1]);
	}

	/**
	 * Construct a point from the first two elements of the given array.
	 */
	public PicPoint(double[] f){
		super(f[0], f[1]);
	}

	/**
	 * Construct a point by parsing a String similar to that given by the
	 * {@link #toString() toString()} method, ie <code>(x,y)</code>.
	 * <br><b>author:</b> Sylvain Reynal
	 * @since jpicedt 1.3.3
	 */
	public PicPoint(String str) throws NumberFormatException {
		StringTokenizer tokenizer = new StringTokenizer(str," (,)",false); // doesn't return delimiters
		// if everything went fine, should have 2 tokens, namely x and y :
		if (tokenizer.countTokens() != 2) throw new NumberFormatException(str+": wrong PicPoint formatting, should be \"(double,double)\".");
		else {
			this.x = java.lang.Double.parseDouble(tokenizer.nextToken()); // explicit package specification needed
			this.y = java.lang.Double.parseDouble(tokenizer.nextToken()); // to lift ambiguity with Point2D.Double
		}
	}


	/**
	 * Set the coordinates of this point from the given point.
	 * @return this for convenience
	 */
	public PicPoint setCoordinates(PicPoint pt){
		this.x = pt.x;
		this.y = pt.y;
		return this;
	}

	/**
	 * Set the coordinates of this point from the given pair
	 */
	public PicPoint setCoordinates(double x, double y){
		this.x = x;
		this.y = y;
		return this;
	}

	/**
	 * Renvoie l'image par réflection du <code>PicPoint</code>
	 * <code>this</code> relativement à l'axe passant par le point
	 * <code>ptOrg</code> et normal au vecteur <code>normalVector</code>.
	 *
	 * si un <code>PicPoint</code> <code>ret</code> est passé en argument, il
	 * est utilisé pour le résultat. Si <code>ret</code> est égal à
	 * <code>this</code>, alors, la réflection est effectuée sur
	 * <code>this</code>.
	 *
	 * @param ptOrg le <code>PicPoint</code> par lequel passe l'axe de réflection.
	 * @param normalVector le <code>PicVector</code> normal à l'axe de réflection
	 * @param ret un <code>PicPoint</code> permettant d'allouer l'image à
	 * l'extérieur de la méthode. Omettre <code>ret</code> pour que
	 * <code>mirror</code> se charge de l'allocation.
	 * @return l'image par réflection de <code>this</code>.
	 */
	public PicPoint mirror(PicPoint ptOrg,PicVector normalVector,PicPoint ret)
		{
			if(ret == null)
				ret = new PicPoint();
			double dotProduct = (this.x - ptOrg.x) * normalVector.x
				+ (this.y - ptOrg.y) * normalVector.y;
			ret.x = this.x - 2 * dotProduct * normalVector.x;
			ret.y = this.y - 2 * dotProduct * normalVector.y;

			return ret;
		}

	/**
	 * A le même effet que l'appel à
	 * <code>mirror(ptOrg,normalVector,this)</code>, c'est à dire que la
	 * valeur de <code>this</code> est changée par reflexion relativement à
	 * l'axe passant par <code>ptOrg</code> et normal à
	 * <code>normalVector</code>.
	 *
	 * @param normalVector le <code>PicVector</code> normal à l'axe de réflexion
	 * @param ptOrg un <code>PicPoint</code> par lequel passe l'axe de réflexion
	 * @return l'image par réflection de <code>this</code>.
	 */
	public PicPoint mirror(PicPoint ptOrg,PicVector normalVector)
		{

			double dotProduct = (this.x - ptOrg.x) * normalVector.x
				+ (this.y - ptOrg.y) * normalVector.y;
			this.x = this.x - 2 * dotProduct * normalVector.x;
			this.y = this.y - 2 * dotProduct * normalVector.y;

			return this;
		}


	/**
	 * Compute the distance with regards to norm norm1.
	 * Preferable to distance when you want to check that two points are close
	 * enough, as norm1 computes faster than norm.
	 * @return abs(this.getX() - other.getX()) + abs(this.getY() - other.getY())
	 */
	public double norm1Distance(Point2D other){
		return Math.abs(this.x-other.getX())+Math.abs(this.x-other.getY());
	}

	/**
	 * Compute the distance with regards to norm normInf.
	 * @return max(abs(this.getX() - other.getX()),abs(this.getY() - other.getY()))
	 */
	public double normInfDistance(Point2D other){
		return Math.max(Math.abs(this.x-other.getX()),Math.abs(this.y-other.getY()));
	}

	// ================= translators ==============================

	/**
	 * Return the equivalent complex number of <code>this</code>, that is to say complex number with real part
	 * equal to <code>this.getX()</code>, and imaginary part equal to <code>this.getY()</code>;
	 * @since jPicEdt 1.6
	 */
	public Complex toComplex(){
		return new Complex(x,y);
	}

	/**
	 * @return a two-element array filled with x and y ; if <code>f</code> is null, a new array is allocated ;
	 * otherwise, the given array is directly modified and returned for convenience.
	 */
	public float[] toFloatArray(float[] f){
		if (f==null) f = new float[2];
		f[0] = (float)x;
		f[1] = (float)y;
		return f;
	}

	/**
	 * @return a two-element array filled with x and y ; if <code>f</code> is null, a new array is allocated ;
	 * otherwise, the given array is directly modified and returned for convenience.
	 */
	public double[] toDoubleArray(double[] f){
		if (f==null) f = new double[2];
		f[0] = x;
		f[1] = y;
		return f;
	}

	/**
	 * Return a "(x,y)" string representing this point.
	 * The returned String in turn may be fed to the PicPoint(String) constructor.
	 */
	public String toString(){
		StringBuffer buf = new StringBuffer(15);
		buf.append("(");
		buf.append(PEToolKit.doubleToString(x));
		buf.append(",");
		buf.append(PEToolKit.doubleToString(y));
		buf.append(")");
		return buf.toString();
	}

	/**
	 * Convert a PicPoint with coordinates expressed in the given unitlenth (expressed in mm),
	 * to a new PicPoint in mm coordinates.
	 * @param unitLength In mm
	 */
	public PicPoint toMm(double unitLength){
		return new PicPoint(x * unitLength, y * unitLength);
	}

	/**
	 * Convert a PicPoint with coordinate expressed in the given unitlenths along X- and Y-axis (the latter being expressed in mm)
	 * to a new PicPoint in mm coordinate.
	 * @param xUnit unilength along the X-axis expressed in mm
	 * @param yUnit unilength along the Y-axis expressed in mm
	 */
	public PicPoint toMm(double xUnit, double yUnit){
		return new PicPoint(xUnit * x, yUnit * y);
	}


	// ====================== GEOM. UTILITIES ===========================

	/**
	 * translates this point by (dx,dy)
	 * @return this for convenience
	 */
	public PicPoint translate(double dx, double dy){
		x += dx;
		y += dy;
		return this;
	}

	/**
	 * translates this point by (p.x, p.y), ie the given point is considered as a translation vector.
	 * @return this for convenience
	 */
	public PicPoint translate(PicPoint p){
		x += p.x;
		y += p.y;
		return this;
	}

	/**
	 * translates this point by (p2.x-p1.x, p2.y-p2.y), ie the given point is considered as a translation vector
	 * build from the two given points.
	 * @return this for convenience
	 */
	public PicPoint translate(PicPoint p1, PicPoint p2){
		x += p2.x-p1.x;
		y += p2.y-p1.y;
		return this;
	}

	/**
	 * translates this point by a*(p.x, p.y), ie the given point is considered as a translation vector scaled
	 * by the given double.<br>
	 * This method proves a useful when one wants to minimize object creation, since it avoids cloning a given PicPoint,
	 * scaling it by "a", then passing it to the translate(PicPoint) method.
	 * @return this for convenience
	 */
	public PicPoint translate(PicPoint p, double a){
		x += a*p.x;
		y += a*p.y;
		return this;
	}

	/**
	 * translates this point by a*(p2.x-p1.x, p2.y-p2.y), ie the given point is considered as a translation vector
	 * build from the two given points, then scaled by the given double.
	 * This method proves a useful when one wants to minimize object creation.
	 * @return this for convenience
	 */
	public PicPoint translate(PicPoint p1, PicPoint p2, double a){
		x += a*(p2.x-p1.x);
		y += a*(p2.y-p1.y);
		return this;
	}

	/**
	 * Apply a central-symmetry wrt the given point
	 * @return this for convenience
	 */
	public PicPoint symmetry(PicPoint center){
		x = 2 * center.x - x;
		y = 2 * center.y - y;
		return this;
	}

	/**
	 * Return a new PicPoint obtained by applying a central-symmetry with the given center to
	 * the given src point. If src==null, it is allocated and returned for convenience.
	 */
	public static PicPoint symmetry(PicPoint center, PicPoint src){
		PicPoint dest = new PicPoint(src);
		dest.symmetry(center);
		return dest;
	}

	/**
	 * Apply a scaling transform to this point.
	 * @param sx Scaling factors along the X-axis
	 * @param sy Scaling factors along the Y-axis
	 * @param ptOrg transformation centre
	 * @return this for convenience
	 */
	public PicPoint scale(PicPoint ptOrg, double sx, double sy){
		x = ptOrg.x + sx * (x-ptOrg.x);
		y = ptOrg.y + sy * (y-ptOrg.y);
		return this;
	}

	/**
	 * Apply a scaling transform to this point.
	 * @param s Scaling factors along the X- and Y- axis
	 * @param ptOrg transformation centre
	 * @return this for convenience
	 */
	public PicPoint scale(PicPoint ptOrg, double s){
		x = ptOrg.x + s * (x-ptOrg.x);
		y = ptOrg.y + s * (y-ptOrg.y);
		return this;
	}

	/**
	 * Apply a scaling transform to this point.
	 * @param s Scaling factors along the X- and Y- axis
	 * @param ptOrgX X-coord of transformation centre
	 * @param ptOrgY Y-coord of transformation centre
	 * @return this for convenience
	 */
	public PicPoint scale(double ptOrgX, double ptOrgY, double s){
		x = ptOrgX + s * (x-ptOrgX);
		y = ptOrgY + s * (y-ptOrgY);
		return this;
	}

	/**
	 * Apply a scaling transform to this point.
	 * @param sx Scaling factors along the X-axis
	 * @param sy Scaling factors along the Y-axis
	 * @param ptOrgX X-coord of transformation centre
	 * @param ptOrgY Y-coord of transformation centre
	 * @return this for convenience
	 */
	public PicPoint scale(double ptOrgX, double ptOrgY, double sx, double sy){
		x = ptOrgX + sx * (x-ptOrgX);
		y = ptOrgY + sy * (y-ptOrgY);
		return this;
	}

	/**
	 * Apply a rotation of center ptOrg and the given angle in radians to this PicPoint
	 * Current implementation arranges for a very fast code if angle is PI, PI/2 or
	 * -PI/2.
	 * @return this for convenience
	 */
	public PicPoint rotate(PicPoint ptOrg, double angle){
		if (angle == PI){
			x = ptOrg.x + ptOrg.x - x;
			y = ptOrg.y + ptOrg.y - y;
		}
		else if (angle == PI/2){
			double _x =  - this.y + ptOrg.y + ptOrg.x;
			this.y = this.x-ptOrg.x+ ptOrg.y;
			this.x = _x;
		}
		else if (angle == -PI/2){
			double _x =  this.y-ptOrg.y + ptOrg.x;
			this.y = - this.x+ptOrg.x+ ptOrg.y;
			this.x = _x;
		}
		else {
			double cosTheta = cos(angle);
			double sinTheta = sin(angle);
			double _x = cosTheta * (this.x-ptOrg.x) - sinTheta * (this.y-ptOrg.y) + ptOrg.x;
			this.y = sinTheta * (this.x-ptOrg.x) + cosTheta * (this.y-ptOrg.y) + ptOrg.y;
			this.x = _x;
		}
		return this;
	}

	/**
	 * Apply a shearing transform of given parameters wrt to the given origin, to this PicPoint
	 * @return this for convenience
	 */
	public PicPoint shear(PicPoint ptOrg, double shx, double shy){
		double _x = this.x + shx * (this.y-ptOrg.y);
		this.y = shy * (this.x-ptOrg.x) + this.y;
		this.x = _x;
		return this;
	}

	/**
	 * Translate this point to the middle of the segment made of [this,other].
	 * @return this for convenience
	 * [SR:pending] refactor method name to "midpoint"
	 */
	public PicPoint middle(PicPoint other){
		return scale(other, 1./2.);
	}

	/**
	 * Project this point onto the line joining p1 and p2. Projectiong is orthogonal. Does nothing if p1==p2.
	 * @return this for convenience.
	 */
	public PicPoint project(PicPoint p1, PicPoint p2){
		if (p1.equals(p2)) return this;
		// algorithm : let H be the orthogonal projection of THIS on v=(p1,p2), let w=(p1,THIS), then
		// (P1,H) = v * (v.w)/||v||^2, which yields H.
		// Note that i don't make use of PicPoint's as intermediate buffer in order to reduce object creation ; code
		// obviously looks weirder, but "voor niets kom de zon op !" (dutch proverb)
		double vDotW = (p2.x-p1.x) * (x-p1.x) + (p2.y-p1.y) * (y-p1.y);
		double normV2 = (p2.x-p1.x) * (p2.x-p1.x) + (p2.y-p1.y) * (p2.y-p1.y);
		x = p1.x + (p2.x - p1.x) * vDotW / normV2;
		y = p1.y + (p2.y - p1.y) * vDotW / normV2;
		return this;
	}

	/**
	 * Project this point onto the line joining p1 and p2. Projection is along the given vector. Does nothing
	 * it p1==p2 or if "dir" is null, or if dir is parallel to the (p1,p2) line.
	 * @param dir a vector indicating the projection axis
	 * @return this for convenience.
	 */
	public PicPoint project(PicPoint p1, PicPoint p2, PicPoint dir){
		// algorithm : let H be the projection along "dir" of THIS point on v=(p1,p2), let w=(p1,THIS), and
		// e3 the vertical unit vector, then
		// (P1,H) =  (w x dir).e3 / || v x dir || * v, which yields H.
		// Note that i don't make use of PicPoint's as intermediate buffer in order to reduce object creation ; code
		// obviously looks weirder, but "voor niets kom de zon op !" (dutch proverb)
		double vDotDirvec = (p2.x-p1.x) * dir.y - (p2.y-p1.y) * dir.x;
		if (vDotDirvec==0) return this;
		double wDotDirvec = (x-p1.x) * dir.y - (y-p1.y) * dir.x;
		x = p1.x + (p2.x - p1.x) * wDotDirvec / vDotDirvec ;
		y = p1.y + (p2.y - p1.y) * wDotDirvec / vDotDirvec ;
		return this;
	}

	/** validation tests */
	public static void main(String[] args){
		PicPoint pt = new PicPoint(2,2);
		System.out.println(pt);
		pt.project(new PicPoint(4,2), new PicPoint(0,0), new PicPoint(1,2));
		System.out.println(pt);
	}

	/**
	 * Apply the given AffineTransform to the coordinates of this point
	 * @return this for convenience
	 */
	public PicPoint apply(AffineTransform at){
		double _x;
		_x = at.getScaleX()*x + at.getShearX()*y + at.getTranslateX();
		y =  at.getShearY()*x + at.getScaleY()*y + at.getTranslateY();
		x = _x;
		return this;
	}


	////////////////////// Collections specific //////////////////////////

	// note to developpers : since PicPoint extends java.awt.Point2D, it inherits the field-to-field
	// equal() method implemented therein.

	public static final XComparator X_COMPARATOR = new XComparator();
	public static final YComparator Y_COMPARATOR = new YComparator();

	/** a comparator b/w PicPoint for X-axis ordering */
	public static class XComparator implements Comparator {

		public int compare(Object o1, Object o2){
			return (int)(((PicPoint)o2).x-((PicPoint)o1).x);
		}

		public boolean equals(Object otherComp){
			return otherComp instanceof XComparator;
		}
	}

	/** a comparator b/w PicPoint for Y-axis ordering */
	public static class YComparator implements Comparator {

		public int compare(Object o1, Object o2){
			return (int)(((PicPoint)o2).y-((PicPoint)o1).y);
		}

		public boolean equals(Object otherComp){
			return otherComp instanceof YComparator;
		}
	}

} // PicPoint
