// PEToolKit.java --- -*- coding: iso-8859-1 -*-
// 1999 - jPicEdt, a picture editor for LaTeX.
// Copyright 1999/2006 Sylvain Reynal
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
// Version: $Id: PEToolKit.java,v 1.28 2013/03/27 07:20:41 vincentb1 Exp $
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
// les mêmes raisons, seule une responsabilité restreinte pqèse sur l'auteur du programme, le titulaire des
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

import jpicedt.widgets.*;
import static jpicedt.jpicedt_env.EnvConstants.APP_ICON;

import java.awt.geom.*;
import java.awt.image.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.text.*;



/**
 * A collection of static &ldquo;utilities&rdquo; methods targetting number formatting, Swing's widget
 * creation, computation of geometrical properties,&hellip;
 * @since jpicedt 1.0
 * @author Sylvain Reynal
 * @version $Id: PEToolKit.java,v 1.28 2013/03/27 07:20:41 vincentb1 Exp $
 * <p>
 */
public class PEToolKit {

	//////////////////////
	//// NUMBER FORMATING
	//////////////////////

	private static final NumberFormat integerFormatter;
	private static final NumberFormat doubleFormatter;
	private static final int DEFAULT_MAX_DIGITS=2;

	static {
		doubleFormatter = NumberFormat.getNumberInstance(Locale.US);
		doubleFormatter.setMaximumFractionDigits(DEFAULT_MAX_DIGITS);
		doubleFormatter.setGroupingUsed(false);
		integerFormatter = NumberFormat.getIntegerInstance(Locale.US);
		integerFormatter.setGroupingUsed(false);
	}

	/**
	 * Set the maximum number of fraction digits to be used when formatting doubles using
	 * the <code>doubleToString()</code> method.
	 * @param n set to a negative number to retrieve the default behaviour
	 */
	public static void setMaximumFractionDigits(int n){
		if (n < 0) doubleFormatter.setMaximumFractionDigits(DEFAULT_MAX_DIGITS);
		else doubleFormatter.setMaximumFractionDigits(n);
	}

	/**
	 * Returns a string representation of the given double.
	 * Max nb of digits may be changed by invoking <code>setMaximumFractionDigits</code> beforehands.
	 */
	public static String doubleToString(double x){

		if (Double.isNaN(x)) return "NaN"; // cheese NaN
		if (Double.isInfinite(x)) return "Inf"; // no geek pun ;-(
		return doubleFormatter.format(x);
	}

	/**
	 * @return a string representation of the given double, ako %0.2f
	 */
	public static String doubleToString(Double x){

		if (x.isNaN()) return "NaN"; // cheese NaN
		if (x.isInfinite()) return "Inf"; // no geek pun ;-(
		return doubleFormatter.format(x);
	}

	/**
	 * @return a string representation of the given int (with implicit cast from double to int)
	 */
	public static String intToString(double x){

		return integerFormatter.format(x);
	}


	////////////////////////
	//// ARRAYS UTILITIES
	///////////////////////
	/**
	 * Return an array of length 2 containing the minimum and the maximum of the given array, in that order.
	 * @param array an array of length greater than 0
	 */
	public static double[] minMaxArray(double[] array){

		double[] minMax = new double[2];
		minMax[0]=array[0]; // min
		minMax[1]=array[0]; // max
		for (double x: array){
			if (x>minMax[1]) minMax[1]=x;
			else if (x<minMax[0]) minMax[0] = x;
		}
		return minMax;
	}


	////////////////////////
	//// GEOM. UTILITIES
	///////////////////////

	/**
	 * Return true if the given point "pt" lies closer to the given shape path (= stroke)
	 * than the given maximum distance. Merely used by {@link #testDistanceToPath testDistanceToPath}.
	 * <p>
	 * This implementation relies on FlatteningPathIterator, and is therefore
	 * faster than a global test simply relying on Shape's getPathIterator method, as
	 * is the case in testDistanceToPath.
	 *<p>
	 * Besides, using a FlatteningPathIterator is necessary in order
	 * to obtain a meaningful segment index in testDistanceToPath.
	 * @param shape basically this would rather be a quadratic or a cubic bezier segment.
	 * @param maxDist set it to Double.POSITIVE_INFINITY if you just want to fetch the tangent
	 */
	private static boolean testDistanceToElementaryPath(Shape s, PicPoint pt, double maxDist){
		return computeTangentToPath(s,pt,maxDist)!=null; // hack to know avoid redundant coding ;-)
	}

	/**
	 * Returns the tangent to the given path, computed at
	 * the path-point which is closest to the given point "pt" than the given maximum distance
	 * Return null otherwise (ie if pt is too far from the given shape).
	 * <p>
	 * This implementation relies on FlatteningPathIterator, hence the smaller the "maxDist" parameter, the
	 * better the return result.
	 *<p>
	 * @param s basically this would rather be a quadratic or a cubic bezier segment, but Arc2D should work as well..
	 * @param maxDist the maximum distance allowed between the given PicPoint and the stroke of the given Shape before
	 *        null is returned ; may be Double.POSITIVE_INFINITY, in which case a tangent is always returned.
	 * @return a unit-norm vector
	 */
	public static PicVector computeTangentToPath(Shape s, PicPoint pt, double maxDist){
		PathIterator pi;
		if (maxDist==Double.POSITIVE_INFINITY)
			pi = new FlatteningPathIterator(s.getPathIterator(null),0,4); // flatness=0, 2^4=16 subdivision max
		else pi = new FlatteningPathIterator(s.getPathIterator(null),maxDist);
		double[] coords = new double[6]; // (x0,y0,x1,y1,x2,y2)
		double oldX=0.0;
		double oldY=0.0;
		double currentX=0.0;
		double currentY=0.0;
		double moveX=0.0;
		double moveY=0.0;
		double maxDistSq = maxDist * maxDist;
		Line2D.Double _tangent = new Line2D.Double(); // temp buffer
		Line2D.Double tangent=null; // asa it's non-null, this is the best tangent ever (i.e. distance b/w segment and
									// pt is minimum AND lower than maxDist)
		double ptSegDist2=0; // temp buffer

		while(!pi.isDone()){
			int type = pi.currentSegment(coords); // fill coords with current segment
			//for (int i=0; i<6; i++){System.out.print(" c["+i+"]="+coords[i]);}
			switch (type){
			case PathIterator.SEG_MOVETO: // reinit "new"
				moveX = currentX = coords[0]; moveY = currentY = coords[1];
				// System.out.println(" SEG_MOVETO : Current point=("+currentX+","+currentY+")");
				break;
			case PathIterator.SEG_CLOSE:
				oldX = currentX; oldY = currentY;
				currentX = moveX; currentY = moveY;
				_tangent.setLine(oldX, oldY, currentX, currentY);
				// System.out.println(" SEG_CLOSE : Line2D=("+oldX+","+oldY+","+currentX+","+currentY+")");
				ptSegDist2 = _tangent.ptSegDistSq(pt);
				if ( ptSegDist2 <= maxDistSq) {
					maxDistSq = ptSegDist2;
					if (tangent==null) tangent = new Line2D.Double();
					tangent.setLine(_tangent);
				}

				break;
			case PathIterator.SEG_LINETO: // update "new"
				oldX = currentX; oldY = currentY;
				currentX = coords[0]; currentY = coords[1];
				_tangent.setLine(oldX, oldY, currentX, currentY);
				// System.out.println(" SEG_LINETO : Line2D=("+oldX+","+oldY+","+currentX+","+currentY+")");
				ptSegDist2 = _tangent.ptSegDistSq(pt);
				if ( ptSegDist2 <= maxDistSq) {
					maxDistSq = ptSegDist2;
					if (tangent==null) tangent = new Line2D.Double();
					tangent.setLine(_tangent);
				}
				break;
			default:
			}
			pi.next();
		}
		if (tangent==null) return null;
		else return new PicVector(tangent).normalize();
	}


	/**
	 * Return a positive integer if the given point "pt" lies closer to the given shape path (= stroke)
	 *         than the given maximum distance ; this integer indicates which segment/quad/cubic of the path,
	 *         starting from 0 (ie according to getPathIterator numbering scheme),
	 *         lies at the closest distance from "pt" ; -1 otherwise.
	 * <p>
	 * This may be used by the UI to determine if a mouse-click occured on a stroke path, especially
	 * for complex shapes like Bezier curves, or arcs (although the returned integer only really makes
	 * sense for shape which are made of bezier curves or segments). This uses
	 * {@link java.awt.geom.FlatteningPathIterator FlatteningPathIterator}, with
	 * the given maxDist parameter also used as the flatness parameter, since it's the value which gives the
	 * best results.
	 */

	public static int testDistanceToPath(Shape s, PicPoint pt, double maxDist){
	    //		ArrayList shapeList = new ArrayList();
		PathIterator pi = s.getPathIterator(null);
		double[] coords = new double[6]; // (x0,y0,x1,y1,x2,y2)
		double oldX=0.0;
		double oldY=0.0;
		double currentX=0.0;
		double currentY=0.0;
		double moveX=0.0;
		double moveY=0.0;
		double maxDistSq = maxDist * maxDist;
		QuadCurve2D quad=null;
		CubicCurve2D cubic=null;
		int segmentIndex=0;
		while(!pi.isDone()){
			int type = pi.currentSegment(coords); // fill coords with current segment
			//for (int i=0; i<6; i++){System.out.print(" c["+i+"]="+coords[i]);}
			switch (type){
			case PathIterator.SEG_MOVETO: // reinit "new"
				moveX = currentX = coords[0]; moveY = currentY = coords[1];
				//System.out.println(" SEG_MOVETO : Current point=("+currentX+","+currentY+")");
				segmentIndex--; // will be compensated for by "segmentIndex++" just below
				break;
			case PathIterator.SEG_CLOSE:
				oldX = currentX; oldY = currentY;
				currentX = moveX; currentY = moveY;
				//System.out.println(" SEG_CLOSE : Line2D=("+oldX+","+oldY+","+currentX+","+currentY+")");
				if (Line2D.ptSegDistSq(oldX,oldY, currentX,currentY, pt.x, pt.y) <= maxDistSq) return segmentIndex;
				break;
			case PathIterator.SEG_LINETO: // update "new"
				oldX = currentX; oldY = currentY;
				currentX = coords[0]; currentY = coords[1];
				//System.out.println(" SEG_LINETO : Line2D=("+oldX+","+oldY+","+currentX+","+currentY+")");
				if (Line2D.ptSegDistSq(oldX,oldY, currentX,currentY, pt.x, pt.y) <= maxDistSq) return segmentIndex;
				break;
			case PathIterator.SEG_QUADTO: // reinit "new"
				oldX = currentX; oldY = currentY;
				currentX = coords[2]; currentY = coords[3];
				if (quad ==null) {quad=new QuadCurve2D.Double(oldX,oldY,coords[0],coords[1],currentX,currentY);}
				else { quad.setCurve(oldX,oldY,coords[0],coords[1],currentX,currentY); }
				if (testDistanceToElementaryPath(quad,pt,maxDist)) return segmentIndex;
				//System.out.println(" SEG_QUADTO : Quad2D=("+ oldX+","+oldY+","+coords[0]+","+coords[1]+","+currentX+","+currentY+")");
				break;
			case PathIterator.SEG_CUBICTO: // reinit "new"
				oldX = currentX; oldY = currentY;
				currentX = coords[4]; currentY = coords[5];
				if (cubic ==null) {cubic=new CubicCurve2D.Double(oldX,oldY,coords[0],coords[1],coords[2],coords[3],currentX,currentY);}
				else { cubic.setCurve(oldX,oldY,coords[0],coords[1],coords[2],coords[3],currentX,currentY); }
				if (testDistanceToElementaryPath(cubic,pt,maxDist)) return segmentIndex;
				//System.out.println(" SEG_CUBICTO : Cubic2D=("+ oldX+","+oldY+","+coords[0]+","+coords[1]+","+coords[2]+","+coords[3]+","+currentX+","+currentY+")");
				break;
			default:
			}
			pi.next();
			segmentIndex++;
		}
		return -1;
	}

	/**
	 * Return an array of Line2D, Quad2D and Cubic2D, representing the (visible part of the) path
	 *         of the given shape. This is based on
	 * 		   {@link java.awt.Shape#getPathIterator(AffineTransform at) Shape.getPathIterator()}
	 *         This may be used, for instance,
	 *         by formaters willing to express a given shape only in terms of Bezier curves and segments,
	 *         if these are the only shapes available in the given target language (e.g. this is the
	 *         case with LaTeX's picture environment, where ellipses are not available).
	 */
	public static Shape[] createPath(Shape s){
		//System.out.println("Creating path...");
		ArrayList<Shape> shapeList = new ArrayList<Shape>();
		PathIterator pi = s.getPathIterator(null);
		double[] coords = new double[6]; // (x0,y0,x1,y1,x2,y2)
		double oldX=0.0;
		double oldY=0.0;
		double currentX=0.0;
		double currentY=0.0;
		double moveX=0.0;
		double moveY=0.0;
		while(!pi.isDone()){
			int type = pi.currentSegment(coords); // fill coords with current segment
			//for (int i=0; i<6; i++){System.out.print(" c["+i+"]="+coords[i]);}
			switch (type){
			case PathIterator.SEG_MOVETO: // reinit "new"
				moveX = currentX = coords[0]; moveY = currentY = coords[1];
				//System.out.println(" SEG_MOVETO : Current point=("+currentX+","+currentY+")");
				break;
			case PathIterator.SEG_CLOSE:
				oldX = currentX; oldY = currentY;
				currentX = moveX; currentY = moveY;
				shapeList.add(new Line2D.Double(oldX,oldY,currentX,currentY));
				//System.out.println(" SEG_CLOSE : Line2D=("+oldX+","+oldY+","+currentX+","+currentY+")");
				break;
			case PathIterator.SEG_QUADTO: // reinit "new"
				oldX = currentX; oldY = currentY;
				currentX = coords[2]; currentY = coords[3];
				shapeList.add(new QuadCurve2D.Double(oldX,oldY,coords[0],coords[1],currentX,currentY));
				//System.out.println(" SEG_QUADTO : Quad2D=("+ oldX+","+oldY+","+coords[0]+","+coords[1]+","+currentX+","+currentY+")");
				break;
			case PathIterator.SEG_CUBICTO: // reinit "new"
				oldX = currentX; oldY = currentY;
				currentX = coords[4]; currentY = coords[5];
				shapeList.add(new CubicCurve2D.Double(oldX,oldY,coords[0],coords[1],coords[2],coords[3],currentX,currentY));
				//System.out.println(" SEG_CUBICTO : Cubic2D=("+ oldX+","+oldY+","+coords[0]+","+coords[1]+","+coords[2]+","+coords[3]+","+currentX+","+currentY+")");
				break;
			case PathIterator.SEG_LINETO: // update "new"
				oldX = currentX; oldY = currentY;
				currentX = coords[0]; currentY = coords[1];
				shapeList.add(new Line2D.Double(oldX,oldY,currentX,currentY));
				//System.out.println(" SEG_LINETO : Line2D=("+oldX+","+oldY+","+currentX+","+currentY+")");
				break;
			default:
			}
			pi.next();
		}
		return shapeList.toArray(new Shape[0]);
	}


	/**
	 * Return an array of Line2D's representing the (visible part of the) flattened path
	 *         of the given shape. This is based on FlattenedPathIterator, and may be used
	 *         by the UI to test the distance b/w a given shape and the mouse, or by a formatter
	 *         to format a given shape in terms of segments only, if the target language only supports
	 *         lines.
	 * @param flatness the flatness used to build the FlattenedPathIterator (max. dist. b/w shape
	 *        and segments of the flattened path).
	 */
	public static Line2D[] createFlattenedPath(Shape s, double flatness){
		//System.out.println("Creating flattened path...");
		ArrayList<Shape> shapeList = new ArrayList<Shape>(); // temp. content storage
		PathIterator pi = new FlatteningPathIterator(s.getPathIterator(null),flatness);
		double[] coords = new double[6]; // (x0,y0,x1,y1,x2,y2)
		double oldX=0.0;
		double oldY=0.0;
		double currentX=0.0;
		double currentY=0.0;
		double moveX=0.0;
		double moveY=0.0;
		while(!pi.isDone()){
			int type = pi.currentSegment(coords); // fill coords with current segment
			//for (int i=0; i<6; i++){System.out.print(" c["+i+"]="+coords[i]);}
			switch (type){
			case PathIterator.SEG_MOVETO: // reinit "new"
				moveX = currentX = coords[0]; moveY = currentY = coords[1];
				//System.out.println(" SEG_MOVETO : Current point=("+currentX+","+currentY+")");
				break;
			case PathIterator.SEG_CLOSE:
				oldX = currentX; oldY = currentY;
				currentX = moveX; currentY = moveY;
				shapeList.add(new Line2D.Double(oldX,oldY,currentX,currentY));
				//System.out.println(" SEG_CLOSE : Line2D=("+oldX+","+oldY+","+currentX+","+currentY+")");
				break;
			case PathIterator.SEG_LINETO: // update "new"
				oldX = currentX; oldY = currentY;
				currentX = coords[0]; currentY = coords[1];
				shapeList.add(new Line2D.Double(oldX,oldY,currentX,currentY));
				//System.out.println(" SEG_LINETO : Line2D=("+oldX+","+oldY+","+currentX+","+currentY+")");
				break;
			default:
			}
			pi.next();
		}
		return shapeList.toArray(new Line2D[0]);
	}

	/**
	 * Given four specification points of a cubic bezier spline, returns an array of five PicPoint's containing
	 * the specification points of two quad bezier splines having the same geometry as the given curve.
	 * @return an array containing {Q1P1, Q1CTRL,Q1P2=Q2P1, Q2CTRL, Q2P2},
	 *         where Q1 and Q2 refer to the two quad splines, and QiCTRL are quad's control-points.
	 * @param p1 first cubic spline end-point
	 * @param p2 second cubic spline end-point
	 * @param pCtrl1 first cubic spline control-point
	 * @param pCtrl2 second cubic spline control-point
	 * @see java.awt.geom.CubicCurve2D
	 * @see java.awt.geom.QuadCurve2D
	 */
	public static PicPoint[] convertCubicBezierToQuad(Point2D p1, Point2D pCtrl1, Point2D pCtrl2, Point2D p2){

		PicPoint[] ptArray = new PicPoint[5];

		ptArray[0] = new PicPoint(p1.getX(),p1.getY());
		ptArray[1] = new PicPoint(
		                       (9*p1.getX() + 21*pCtrl1.getX() + 3*pCtrl2.getX() - p2.getX())/32,
		                       (9*p1.getY() + 21*pCtrl1.getY() + 3*pCtrl2.getY() - p2.getY())/32);
		ptArray[2] = new PicPoint(
		                       (p1.getX() + 3*pCtrl1.getX() + 3*pCtrl2.getX() + p2.getX())/8,
		                       (p1.getY() + 3*pCtrl1.getY() + 3*pCtrl2.getY() + p2.getY())/8);
		ptArray[3] = new PicPoint(
		                       (-p1.getX() + 3*pCtrl1.getX() + 21*pCtrl2.getX() + 9*p2.getX())/32,
		                       (-p1.getY() + 3*pCtrl1.getY() + 21*pCtrl2.getY() + 9*p2.getY())/32);
		ptArray[4] = new PicPoint(p2.getX(),p2.getY());
		return ptArray;
	}

	/**
	 * Given the three specification points of a quad bezier spline, returns an array of PicPoint's
	 * containing the four specification points of a cubic spline having the same geometry.
	 * <p>
	 * The following algorith is being used (xQ and xC refer to quad and cubic spline resp.) :
	 * <ul>
	 *	<li> xC(0) = xQ(0)
	 *	<li> xC(1) = (xQ(0) + 2 xQ(1))/3
	 *	<li> xC(2) = (2xQ(1) + xQ(2))/3
	 *	<li> xC(3) = xQ(2)
	 * </ul>
	 * where xQ(0) = pt1, xQ(1) = ptCtrl and xQ(2) = pt2
	 * @return {P1, PCTRL1, PCTRL2, P2}
	 * @param p1 first quad end-point
	 * @param p2 second quad end-point
	 * @param pCtrl quad control-point
	 */
	public static PicPoint[] convertQuadBezierToCubic(Point2D p1, Point2D pCtrl, Point2D p2) {
		PicPoint[] ptArray = new PicPoint[4];
		ptArray[0] = new PicPoint(p1);
		ptArray[1] = new PicPoint((p1.getX()+2*pCtrl.getX())/3.0, (p1.getY()+2*pCtrl.getY())/3.0);
		ptArray[2] = new PicPoint((2*pCtrl.getX()+p2.getX())/3.0, (2*pCtrl.getY()+p2.getY())/3.0) ;
		ptArray[3] = new PicPoint(p2);
		return ptArray;
	}

	/**
	 * Converts the given quad curve to a cubic curve
	 */
	public static CubicCurve2D convertQuadBezierToCubic(QuadCurve2D quad) {
		PicPoint[] ptCubic = convertQuadBezierToCubic(quad.getP1(), quad.getCtrlPt(), quad.getP2());
		CubicCurve2D cubic = new CubicCurve2D.Double();
		cubic.setCurve(ptCubic[0],ptCubic[1],ptCubic[2],ptCubic[3]);
		return cubic;
	}

	/**
	 * Converts the given cubic curve to a quad curve
	 */
	public static QuadCurve2D[] convertCubicBezierToQuad(CubicCurve2D cubic) {
		PicPoint[] ptQuad = convertCubicBezierToQuad(cubic.getP1(), cubic.getCtrlP1(), cubic.getCtrlP2(), cubic.getP2());
		QuadCurve2D quad1 = new QuadCurve2D.Double();
		QuadCurve2D quad2 = new QuadCurve2D.Double();
		quad1.setCurve(ptQuad[0], ptQuad[1], ptQuad[2]);
		quad2.setCurve(ptQuad[2], ptQuad[3], ptQuad[4]);
		return new QuadCurve2D[]{quad1,quad2};
	}

	///////////////////////////////////////////////////////////
	//// GUI Utilities
	///////////////////////////////////////////////////////////
	/**
       @param icon Le nom de base de la ressouce image contenant l'icône, sans l'extension <code>.png</code>.
       @return Le chemin de la ressource*/
    public static String getIconLocation(String icon){
		return "/jpicedt/images/"+icon+".png";
	}

	/** create an <code>ImageIcon</code> built from 	"/jpicedt/images/"+icon+".png" */
	public static ImageIcon createImageIcon(String icon){
		String location = getIconLocation(icon);
		//return new ImageIcon(dummy.getClass().getResource(location));
		java.net.URL url = PEToolKit.class.getResource(location);
		if (url==null) return new ImageIcon(PEToolKit.class.getResource("/jpicedt/images/MissingIcon.png"));
		return new ImageIcon(PEToolKit.class.getResource(location));
		//return new ImageIcon(ClassLoader.getSystemClassLoader().getResource(location));

	}

	/** Configure l'icône du cadre <code>frame</code> à l'icône identifiée <code>icon</code> s'il est
		possible de la trouver, ou sinon ne fait rien.
		@return 0 si ça a réussi.
	*/
	public static int setImageIcon(JFrame frame, String icon){
		java.net.URL url = PEToolKit.class.getResource(PEToolKit.getIconLocation(icon));
		if(url != null)
		{
			frame.setIconImage(new ImageIcon(url).getImage());
			return 0;
		}
		return -1;
	}

	public static void setAppIconToDefault(JFrame frame){
		setImageIcon(frame, "appicon." + APP_ICON);
	}

	/** create a JLabel with an Icon built from  "/jpicedt/images/"+icon+".png" */
	public static  JLabel createJLabel(String icon){
		return new JLabel(createImageIcon(icon));
	}

	/** creates a JComboBox */
	public static JComboBox createComboBox(Object[] items){
		JComboBox cb = new JComboBox(items);
		//cb.setMaximumSize(new Dimension(dimButton.width * 3,dimButton.height));
		//[underway:check] if (UIManager.getLookAndFeel().getName().equals("CDE/Motif")) cb.setRenderer(new MotifLAFCellRenderer()); // special display for Motif LAF (since I didn't like the default one)
		return cb;
	}

	/**
	 * creates a PEComboBox (aka JComboBox) from an EnumMap.
	 * @since jpicedt 1.5
	 */
	public static <T> PEComboBox<T> createComboBox(Map<T,?> map){
		return new PEComboBox<T>(map);
	}


} // class PEToolKit
