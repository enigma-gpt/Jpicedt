// PicPsCurve.java --- -*- coding: iso-8859-1 -*-
// April 2005 - jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: PicPsCurve.java,v 1.15 2013/03/27 07:01:24 vincentb1 Exp $
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
package jpicedt.graphic.model;

import jpicedt.graphic.*;
import jpicedt.graphic.toolkit.*;
import jpicedt.graphic.event.DrawingEvent;
import jpicedt.graphic.view.View;
import jpicedt.graphic.view.HitInfo;
import jpicedt.widgets.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import jpicedt.ui.dialog.UserConfirmationCache;
import java.util.*;

import static jpicedt.Log.*;
import static jpicedt.Localizer.*;

import static java.lang.Math.floor;
import static java.lang.Math.pow;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.abs;
import static java.lang.Math.sqrt;
import static java.lang.Math.acos;
import static java.lang.Math.atan;
import static java.lang.Math.atan2;
import static java.lang.Math.PI;
import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * <p>
 * A multicurve, either closed or not, going through a set of control points.
 * This is the implementation of the (somewhat obscure) pstricks \pscurve function.
 * There are three coefficients a,b,c altering the curvature.
 *
 * @author    Vincent Guirardel, Sylvain Reynal
 * @since     jpicedt 1.4pre5
 * @version   $Id: PicPsCurve.java,v 1.15 2013/03/27 07:01:24 vincentb1 Exp $
 *
 */
public class PicPsCurve extends AbstractCurve implements ActionFactory, CustomizerFactory,PicMultiCurveConvertable {

	//////////////////////////
	//// PROTECTED FIELDS
	//////////////////////////

	/**
	 * An array of <code>PicPoint</code>'s backing the geometry of the polygon.
	 * Aka caching mechanism holding a list of user-controlled points.
	 */
	protected ArrayList<PicPoint> polygonPts = new ArrayList<PicPoint>();

	// inherited : protected boolean isClosed;

	/**
	 * Curvature parameters a,b,c of pscurve.
	 */
	protected double curvatureA ;
	protected double curvatureB ;
	protected double curvatureC ;

	/**
	 * Auxiliary curvature parameters a,b,c.  They correspond to the values of the variables a,b,c in
	 * PostScript pscurve code once "IC" has been called.
	 */
	private double auxA ;
	private double auxB;
	private double auxC;

	/**
	 * default values for the curvature parameters
	 */
	protected final static double DEFAULT_CURVATURE_A = 1;
	protected final static double DEFAULT_CURVATURE_B = 0.1;
	protected final static double DEFAULT_CURVATURE_C = 0;

	/**
	 * Min value of curvature coefficient accessible to <code>JSlider</code> in the geometry editor
	 */
	protected final static int A_SLIDER_MIN = -100 ;
	protected final static int B_SLIDER_MIN = -100 ;
	protected final static int C_SLIDER_MIN = -100 ;

	/**
	 * Max value of curvature coefficient accessible to <code>Jslider</code> in the geometry editor
	 */
	protected final static int A_SLIDER_MAX = 100 ;
	protected final static int B_SLIDER_MAX = 100 ;
	protected final static int C_SLIDER_MAX = 100 ;



	//////////////////////////////
	/// CONSTRUCTORS
	//////////////////////////////

	/**
	 * Create a new empty open pscurve, with a default attribute set.
	 */
	public PicPsCurve() {
		this(false);
	}

	/**
	 * Create a new empty pscurve, open or closed, with a default attribute set bound to it.
	 * @param closed  whether the pscurve will be closed or not
	 */
	public PicPsCurve(boolean closed) {
		super(closed);
		this.curvatureA=DEFAULT_CURVATURE_A;
		this.curvatureB=DEFAULT_CURVATURE_B;
		this.curvatureC=DEFAULT_CURVATURE_C;
	}


	/**
	 * Create a new empty pscurve with the given attribute set.
	 * @param set     attribute set to be bound to this element
	 * @param closed  whether the generated multi-curve will be closed or not
	 */
	public PicPsCurve(boolean closed, PicAttributeSet set) {
		this(closed);
		this.attributeSet = new PicAttributeSet(set);
	}

	/**
	 * Create a new pscurve initialized from the given array of PicPoint's.
	 *
	 * @param polyPts array of PicPoint's specifying the pscurve geometry.
	 * @param set     attribute set to be bound to this element
	 * @param closed  whether the generated multi-curve will be closed or not
	 * @param a  the a-curvature value
	 * @param b  the b-curvature value
	 * @param c  the c-curvature value
	 * @since         jpicedt 1.4pre5
	 */
	public PicPsCurve(PicPoint[] polyPts, boolean closed, double a, double b, double c, PicAttributeSet set) {
		super(closed);
		this.attributeSet = new PicAttributeSet(set);
		this.curvatureA=a;
		this.curvatureB=b;
		this.curvatureC=c;
		for (PicPoint pt: polyPts)
			this.polygonPts.add(new PicPoint(pt));
		this.allocateBezierPts(); // at least, we this call we're clear !
		this.updateBezierPts();
	}

	/**
	 * Create a new pscurve initialized from the given array of PicPoint's, using the
	 * default curvature values.
	 *
	 * @param polygonPts array of PicPoint's specifying the polygon geometry.
	 * @param set     attribute set to be bound to this element
	 * @param closed  whether the generated multi-curve will be closed or not
	 * @since         jpicedt 1.4pre5
	 */
	public PicPsCurve(PicPoint[] polygonPts, boolean closed, PicAttributeSet set) {
		this(polygonPts, closed, DEFAULT_CURVATURE_A, DEFAULT_CURVATURE_B, DEFAULT_CURVATURE_C,  set);
	}

	/**
	 * "cloning" constructor (to be used by clone())
	 * @param poly  PicPsCurve to be cloned
	 * @since       jpicedt 1.4pre5
	 */
	public PicPsCurve(PicPsCurve poly) {
		super(poly);
		curvatureA = poly.curvatureA;
		curvatureB = poly.curvatureB;
		curvatureC = poly.curvatureC;
		for (Iterator<PicPoint> it=poly.polygonPts.iterator(); it.hasNext();)
			this.polygonPts.add(new PicPoint(it.next()));
	}


	/**
	 * Overide Object.clone() method
	 *
	 * @since    jpicedt 1.4pre5
	 */
	public PicPsCurve clone() {
		return new PicPsCurve(this);
	}


	/**
	 * @return   a localised string that represents this object's name
	 */
	public String getDefaultName() {
		return localize("model.PsCurve");
	}


	//////////////////////////////////
	/// OPERATIONS ON CONTROL POINTS
	//////////////////////////////////
	/**
	 * Computes the number of Bezier points corresponding to the given number of
	 * polygon points and to the closeness of the curve.
	 */
	private int computeNumberOfBezierPoints(int nbPolygonPts, boolean closed) {
		int nbBezierPts;

		if (closed)
			nbBezierPts = 3 * nbPolygonPts ;
		else {
			if (nbPolygonPts==0)
				nbBezierPts = 0;
			else if (nbPolygonPts<=3)
				nbBezierPts = 1;
			else
				nbBezierPts = 3*nbPolygonPts-8;
		}
		return nbBezierPts;
	}

	/**
	 * allocates a number of Bezier points appropriate for the current number of polygon points.
	 */
	private void allocateBezierPts() {
		int lb;
		int l = polygonPts.size();
		lb = computeNumberOfBezierPoints(l, isClosed);
		bezierPts.clear();
		for (int i=0; i<lb; i++) bezierPts.add(new PicPoint());
	}


	/**
	 * Update the location of Bezier points according to the current location of polygon points.
	 */
	private void updateBezierPts() {
		int nbPolyPts = polygonPts.size();
		// compute curvature auxiliary values [see call to "IC" in pstricks.pro]
		auxA = curvatureA * 2/3 * pow(2,curvatureB/2);
		auxB = curvatureB ;
		auxC = min(max(curvatureC+1,0),3) ;

		if (isClosed())
			switch (nbPolyPts) {
			case 0: break;
			case 1:
				setBezierPt(0, getPolygonPt(0));
				setBezierPt(1, getPolygonPt(0));
				setBezierPt(2, getPolygonPt(0));
				break;
			case 2: //set it to the segment. Note: in pstricks, does not draw anything.
				setBezierPt(0, getPolygonPt(0));
				setBezierPt(1, getPolygonPt(0));
				setBezierPt(2, getPolygonPt(1));
				setBezierPt(3, getPolygonPt(1));
				setBezierPt(4, getPolygonPt(1));
				setBezierPt(5, getPolygonPt(0));
				break;
			default:
				PicPoint bezierL=new PicPoint();
				PicPoint bezierR=new PicPoint();
				int nbBezierPoints=3*nbPolyPts;
				PicPoint pL=getPolygonPt(nbPolyPts-1);
				PicPoint p=getPolygonPt(0);
				PicPoint pR=getPolygonPt(1);
				int i=0;
				while (i<nbPolyPts) {
					computeBezierControlPoints(pL,p,pR,bezierL,bezierR);//sets bezierL and bezierR
					setBezierPt( (3*i-1 + nbBezierPoints) % nbBezierPoints,bezierL);
					setBezierPt(3*i,p);
					setBezierPt(3*i+1,bezierR);
					i++;
					pL=p; //shift
					p=pR;
					pR=getPolygonPt( (i+1) % nbPolyPts );
				}
				break;
			}
		else { // open curve
			switch (nbPolyPts) {
			case 0: break;
			case 1:
				setBezierPt(0, getPolygonPt(0));
				break;
			case 2:
				setBezierPt(0, getPolygonPt(1));
				break;
			case 3:
				setBezierPt(0, getPolygonPt(1)); // that's the intermediate point
				break;
			default:
				PicPoint bezierL=new PicPoint();
				PicPoint bezierR=new PicPoint();
				PicPoint pL=getPolygonPt(0);
				PicPoint p=getPolygonPt(1);
				PicPoint pR=getPolygonPt(2);
				//initial segment
				computeBezierControlPoints(pL,p,pR,bezierL,bezierR);//sets bezierL and bezierR
				setBezierPt(0,p);
				setBezierPt(1,bezierR);
				int i=2;
				while ( i < nbPolyPts-2 ) {
					//one iteration for each point of the curve of index i distinct from 0,1 and not n-1, n-2 (n=nbPolypts)
					pL=p; //shift
					p=pR;
					pR=getPolygonPt( i+1 );
					computeBezierControlPoints(pL,p,pR,bezierL,bezierR);//sets bezierL and bezierR
					setBezierPt(3*i-4,bezierL);
					setBezierPt(3*i-3,p);
					setBezierPt(3*i-2,bezierR);
					i++;
				}
				//final segment
				pL=p; //shift
				p=pR;
				pR=getPolygonPt( i+1 );
				computeBezierControlPoints(pL,p,pR,bezierL,bezierR);//sets bezierL and bezierR
				setBezierPt(3*i-4,bezierL);
				setBezierPt(3*i-3,p);
				break;
			}
		} // end of open case
	}


	/**
	 * Given 3 consecutive points of the curve: <code>inLeft, in, inRight</code> computes the control points
	 * <code>outLeft,outRight</code> controling the tangency near the point <code>in</code> of the two Bezier
	 * curves adjacent to <code>in</code>
	 */
	private void computeBezierControlPoints(PicPoint inLeft, PicPoint  in, PicPoint  inRight, PicPoint outLeft, PicPoint outRight){
		// en commentaires, a quoi ca correspond dans le code postscript de pscurve.
		// Les noms de variables x,y,x0,x1,dx0,dx1,etc correspondent
		// Cette methode correspond essentiellement a l'appel de la procedre postscript CC (definie dans pstricks.pro)
		// appel a CCA dans CC
		// the values of auxA,auxB,auxC should be up to date before calling me. This is done by updateBezierPts
		double x= in.getX();  double y= in.getY();
		double dx0= x-inLeft.getX(); double dy0=y-inLeft.getY();
		double l0=sqrt(dx0*dx0+dy0*dy0);
		double dx1= inRight.getX()-x; double dy1=inRight.getY()-y;
		double l1=sqrt(dx1*dx1+dy1*dy1);
		// fin d'appel de CCA
		double dx=dx0*pow(l1,auxC)+dx1*pow(l0,auxC);// formule magique... (dx,dy) is a kind of "unit" vector pointing towards the Bezier points
		double dy=dy0*pow(l1,auxC)+dy1*pow(l0,auxC);
		double alpha=atan2(dy0,dx0)-atan2(dy1,dx1); //angle entre les vecteurs.
		double m;// m roughly controls how far the control point should be
		if (sqrt(dx*dx+dy*dy)==0)
			m=0;
		else
			m=auxA*pow(abs(cos(alpha/2)),auxB)/2/sqrt(dx*dx+dy*dy);// si c'est pas magique ca...

		if (DEBUG) debug("m="+m+"l0="+l0+"xL="+(x-l0*dx*m));
		outLeft.setCoordinates(x-l0*dx*m,y-l0*dy*m);
		outRight.setCoordinates(x+l1*dx*m,y+l1*dy*m);
	}

	/**
	 * Returns the index of the first user-controlled point that can be retrieved by <code>getCtrlPt()</code>.
	 * This implementation returns 0.
	 */
	public int getFirstPointIndex(){
		return 0;
	}

	/**
	 * Returns the index of the last user-controlled point that can be retrieved by <code>getCtrlPt()</code>.
	 * This default implementation returns the number of polygon-points minus one.
	 */
	public int getLastPointIndex(){
		return polygonPts.size()-1;
	}

	/**
	 * Return a copy of the <b>user-controlled point</b> (ie a polygon point, not a bezier point) having the
	 * given index.<br>
	 * Note: Bezier points may be accessed through a call to <code>getBezierPt()</code> in super class.
	 * @return the point indexed by <code>numPoint</code> ;
	 *         if <code>dest</code> is null, allocates a new PicPoint and return it,
	 *         otherwise directly modifies <code>dest</code> and returns it as well for convenience.
	 * @param numPoint the point index, should be greater or equal to the value returned by
	 *        <code>getFirstPointIndex</code>, and lower or equal to <code>getLastPointIndex</code>.
	 */
	public PicPoint getCtrlPt(int numPoint, PicPoint dest){
		if (dest==null)
			dest = new PicPoint();
		dest.setCoordinates(getPolygonPt(numPoint));
		return dest;
	}

	/**
	 * Return a reference to the polygon point with the given index.  This is equivalent to calling
	 * <code>getCtrlPt(int,PicPoint)</code>, except that a mere reference is returned.
	 */
	protected final PicPoint getPolygonPt(int numPoint){
		return polygonPts.get(numPoint % polygonPts.size());
	}

	/////////////////////////////////
	//// TRANSFORMS
	/////////////////////////////////

	/**
	 * Translate this <code>Element</code> by (dx,dy) ; this implementation translates the
	 * specification-points, then fires a changed-update event.
	 * @param dx The X coordinate of translation vector
	 * @param dy The Y coordinate of translation vector
	 * @since PicEdt 1.0
	 */
	public void translate(double dx, double dy){
		for (PicPoint pt: polygonPts){
			pt.translate(dx,dy);
		}
		super.translate(dx,dy); // translates bezierPts
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}

	/**
	 * Scale this object by <code>(sx,sy)</code> using <code>(ptOrgX,ptOrgY)</code> as the origin. This
	 * implementation simply apply a scaling transform to all specification-points.  Note that <code>sx</code>
	 * and <code>sy</code> may be negative.  This method eventually fires a changed-update event.
	 */
	public void scale(double ptOrgX, double ptOrgY, double sx, double sy, UserConfirmationCache ucc){
		for (PicPoint pt: polygonPts){
			pt.scale(ptOrgX, ptOrgY, sx, sy);
		}
		super.scale(ptOrgX, ptOrgY, sx, sy,ucc); // scales bezierPts
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}

	/**
	 * Rotate this Element by the given angle along the given point
	 * @param angle rotation angle in radians
	 */
	public void rotate(PicPoint ptOrg, double angle){
		for (PicPoint pt: polygonPts)
			pt.rotate(ptOrg,angle);
		super.rotate(ptOrg, angle); // rotate bezierPts
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}

	/**
	 * Effectue une réflexion sur <code>this</code> relativement à l'axe
	 * défini par <code>ptOrg</code> et <code>normalVector</code>.
	 *
	 * @param ptOrg le <code>PicPoint</code> par lequel passe l'axe de réflexion.
	 * @param normalVector le <code>PicVector</code> normal à l'axe de réflexion.
	 */
	public void mirror(PicPoint ptOrg, PicVector normalVector){
		for (PicPoint pt: polygonPts)
			pt.mirror(ptOrg,normalVector);
		super.mirror(ptOrg, normalVector); // mirror bezierPts
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}


	/**
	 * Shear this <code>Element</code> by the given params wrt to the given origin
	 */
	public void shear(PicPoint ptOrg, double shx, double shy, UserConfirmationCache ucc){
		for (PicPoint pt: polygonPts)
			pt.shear(ptOrg,shx,shy);
		super.shear(ptOrg,shx,shy,ucc); // shear bezierPts
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}

	/**
	 * Set the user-controlled point with the given index to the given value.
	 * This implementation sets the value of the polygon-point having the same index,
	 * then update the associated Bezier curve, and fires a changed-update event.
	 * @param constraint not used so far
	 */
	public void setCtrlPt(int index, PicPoint pt, EditPointConstraint constraint) {
		getPolygonPt(index).setCoordinates(pt);
		updateBezierPts();
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}


	/**
	 * adds the given point at the end of this pscurve.
	 */
	public void addPoint(PicPoint pt) {
		if (DEBUG) debug("addpoint PicPsCurve :"+pt.toString());

		polygonPts.add(new PicPoint(pt));
		this.allocateBezierPts();
		this.updateBezierPts();
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);

		if (DEBUG) debug("addpoint new pscurve"+this.toString());
	}


	/**
	 * Inserts the specified point at the specified index.
	 * Shifts the point currently at that position (if any) and any subsequent points to the right.
	 */
	public void addPoint(int ptIndex, PicPoint pt){
		if (DEBUG) debug("addpoint PicPsCurve index"+ptIndex+" :"+pt.toString());
		splitSegment(ptIndex-1, pt);
	}


	/**
	 * Split the given polygon segment (starting from 0),
	 * by inserting a new polygon point at the appropriate position.
	 * then fires a changed-update.
	 * For instance, spliting segment "2" yields the following polygon points : 0, 1, 2, insertion pt, 3, 4,
	 * etc&hellip;
	 *
	 * @param segIdx index of the polygon segment that must be split.
	 * @param pt   the PicPoint to be added
	 */
	public int splitSegment(int segIdx, PicPoint pt) {

		if (segIdx < 0 || segIdx > polygonPts.size()) throw new IndexOutOfBoundsException(new Integer(segIdx).toString());

		polygonPts.add(segIdx+1, new PicPoint(pt)); // ArrayList.add shifts ensuing indices to the right

		this.allocateBezierPts();
		this.updateBezierPts();
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
		return segIdx+1;
	}


	/**
	 * Remove a point at the given position from this <code>PicPsCurve</code>, then fire a
	 * changed-update. This shifts any subsequent points to the left.<p>
	 *
	 * Nothing is done if this polygon has only one point.
	 *
	 * @param pos  index of point to be removed
	 */
	public void removePoint(int pos) {

		if (pos < 0 || pos >= polygonPts.size()) throw new IndexOutOfBoundsException(new Integer(pos).toString());
		if (polygonPts.size() == 1) return;// we won't remove the only point !

		polygonPts.remove(pos);

		this.allocateBezierPts();
		this.updateBezierPts();
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}


	/**
	 * Remove the last point of this PicPsCurve.
	 * This is a convenience call to <code>removePoint(polygonPts.size()-1)</code>
	 */
	public void removeLastPoint() {
		removePoint(polygonPts.size()-1);
	}


	/**
	 * Convert this PicPsCurve to a PicMultiCurve, by simply relying on the appropriate constructor
	 * in class {@link PicMultiCurve PicMultiCurve}.
	 */

	public PicMultiCurve convertToMultiCurve(){
		PicMultiCurve curve=new PicMultiCurve(this);
		return curve;
	}


	/**
	 * Returns the 4 Bezier points of the initial prolongation of the <code>PicPsCurve</code>.
	 * Used to draw the dotted control line when highlighted.
	 */
	public PicPoint[] getInitialControlCurve(){
		if (isClosed) return new PicPoint[]{};
		int n=polygonPts.size();
		if (n <= 1) return new PicPoint[]{};
		PicPoint pL=new PicPoint();
		PicPoint pR=new PicPoint();
		PicPoint p0=getCtrlPt(0,null);
		PicPoint p1=getCtrlPt(1,null);
		PicPoint p2=getCtrlPt(2,null);
		computeBezierControlPoints(p0,p1,p2,pL,pR);//sets pL and pR
		return new PicPoint[]{p0,p0,pL,p1};
	}

	/**
	 * Return the 4 Bezier points of the final prolongation of the <code>PicPsCurve</code>.
	 * Used to draw the dotted control line when highlighted.
	 */
	public PicPoint[] getFinalControlCurve(){
		if (isClosed) return new PicPoint[]{};
		int n=getLastPointIndex();
		if (n <= 1) return new PicPoint[]{};
		PicPoint pL=new PicPoint();
		PicPoint pR=new PicPoint();
		PicPoint p0=getCtrlPt(n-2,null);
		PicPoint p1=getCtrlPt(n-1,null);
		PicPoint p2=getCtrlPt(n,null);
		computeBezierControlPoints(p0,p1,p2,pL,pR);
		return new PicPoint[]{p1,pR,p2,p2};
	}


	////////////////////////////
	//// OTHER FIELDS ACCESSORS
	////////////////////////////


	/**
	 * Set close path state, then fires a changed-update.
	 *
	 * @param state  The new closed value
	 */
	public void setClosed(boolean state) {
		super.setClosed(state);
		allocateBezierPts();
		updateBezierPts();
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}

	/**
	 * Return an array containing the three curvature coefficients in an array, namely [a b c].
	 */
	public double[] getCurvatures(){
		return new double[]{this.curvatureA,this.curvatureB, this.curvatureC};
	}

	//////////////////////////////////////////////////////////
	/// BOUNDING BOX
	//////////////////////////////////////////////////////////

	/**
	 * Returns the bounding box (ie the surrounding rectangle) in double precision
	 * This implementation compute the bb from the union of :
	 * <ul>
	 * <li> the bb as computed by super-class
	 * <li> and the smallest rectangle that encompasses  all the polygon-points.
	 * </ul>
	 * @since jpicedt 1.4pre5
	 */
	public Rectangle2D getBoundingBox(Rectangle2D r){
		return super.getBoundingBox(r);
	}

	///////////////////////////////
	//// STRING FORMATING
	///////////////////////////////

	/**
	 * Used for debugging purpose.
	 */
	public String toString() {
		String s = super.toString();
		s += "\n\t";
		for (int i=0; i<polygonPts.size(); i++){
			s += "polyPts[" + i + "]=" + getPolygonPt(i)  + " ";
		}
		s += "\n\t";
		s += (isClosed ? " closed" : " open");
		return s;
	}

	////////////////////////////////
	//// Action's
	////////////////////////////////

	/**
	 * Create an array of Action's related to this object
	 *
	 * @param actionDispatcher  dispatches events to the proper PECanvas
	 * @param localizer         i18n localizer for PEAction's
	 */
	public ArrayList<PEAction> createActions(ActionDispatcher actionDispatcher, ActionLocalizer localizer, HitInfo hi) {
		ArrayList<PEAction> actionArray = super.createActions(actionDispatcher, localizer, hi);
		if (actionArray==null)
			actionArray = new ArrayList<PEAction>();
		actionArray.add(new ConvertToCurveAction(actionDispatcher, localizer));
		return actionArray;
	}

	/**
	 * @return   A Customizer for geometry editing
	 */
	public AbstractCustomizer createCustomizer(){
		if (cachedCustomizer == null)
			cachedCustomizer = new Customizer();
		cachedCustomizer.load();
		return cachedCustomizer;
	}

	private Customizer cachedCustomizer = null;

	// ---- conversion to multiCurve ----

	/**
	 * Convert this pscurve to a multicurve, selecting it if applicable.
	 * @author    Sylvain Reynal
	 * @since     jpicedt 1.4
	 */
	class ConvertToCurveAction extends PEAction {

		public static final String KEY = "action.editorkit.ConvertPsCurveToCurve";

		public ConvertToCurveAction(ActionDispatcher actionDispatcher, ActionLocalizer localizer){
			super(actionDispatcher, KEY, localizer);
		}

		public void undoableActionPerformed(ActionEvent e){
			PicMultiCurve curve = convertToMultiCurve();
			Drawing dr = getDrawing();
			if (dr != null){
				dr.replace(PicPsCurve.this, curve);
				View view = curve.getView();
				if (view != null){
					PECanvas canvas = view.getContainer();
					if (canvas != null)   canvas.select(curve, PECanvas.SelectionBehavior.INCREMENTAL);
				}
			}
		}
	}


	////////////////////////////////////////////////////////////////////////////////////
	//// GUI
	////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Geometry customizer
	 *
	 * @author    reynal
	 */
	class Customizer extends AbstractCurve.Customizer {

		private DefaultCellEditor doubleEditor;
		private JSlider aSlider,bSlider,cSlider;// to adjust curvature
		private boolean isListenersAdded = false;// flag
		private	PolygonJTable table ;

		/**
		 * Constructor for the Customizer object
		 */
		public Customizer() {

			super();
			Box p = new Box(BoxLayout.Y_AXIS);
			// polygon points:
			table = new PolygonJTable();
			table.setPreferredScrollableViewportSize(new Dimension(500, 300));

			// wrap scrollpane around:
			JScrollPane scrollPane = new JScrollPane(table);
			p.add(scrollPane);
			p.add(aSlider = new JSlider(A_SLIDER_MIN,A_SLIDER_MAX,(int)floor(100*curvatureA)));
			aSlider.setMajorTickSpacing(50);
			aSlider.setMinorTickSpacing(10);
			aSlider.setPaintTicks(true);
			aSlider.setPaintLabels(true);

			p.add(bSlider = new JSlider(B_SLIDER_MIN,B_SLIDER_MAX,(int)floor(100*curvatureB)));
			bSlider.setMajorTickSpacing(50);
			bSlider.setMinorTickSpacing(10);
			bSlider.setPaintTicks(true);
			bSlider.setPaintLabels(true);

			p.add(cSlider = new JSlider(C_SLIDER_MIN,C_SLIDER_MAX,(int)floor(100*curvatureC)));
			cSlider.setMajorTickSpacing(50);
			cSlider.setMinorTickSpacing(10);
			cSlider.setPaintTicks(true);
			cSlider.setPaintLabels(true);

			add(p, BorderLayout.NORTH);
			add(super.createPanel(), BorderLayout.CENTER);
			setPreferredSize(new Dimension(500,500));
		}

		// a listener for the sliders
		class SliderListenerA implements ChangeListener {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				curvatureA = (double)source.getValue()/100;
				if (!source.getValueIsAdjusting()) {table.repaint();}
				updateBezierPts();
				fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
			}
		}

		class SliderListenerB implements ChangeListener {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				curvatureB = (double)source.getValue()/100;
				if (!source.getValueIsAdjusting()) {table.repaint();}
				updateBezierPts();
				fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
			}
		}

		class SliderListenerC implements ChangeListener {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				curvatureC = (double)source.getValue()/100;
				if (!source.getValueIsAdjusting()) {table.repaint();}
				updateBezierPts();
				fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
			}
		}

		/**
		 * Add action listeners to widgets to reflect changes immediately
		 */
		private void addActionListeners() {
			if (isListenersAdded)
				return;// already done
			aSlider.addChangeListener(new SliderListenerA());
			bSlider.addChangeListener(new SliderListenerB());
			cSlider.addChangeListener(new SliderListenerC());
			isListenersAdded = true;
		}

		private void removeActionListeners() {
			if (!isListenersAdded)
				return;// already done
			aSlider.removeChangeListener(new SliderListenerA());
			bSlider.removeChangeListener(new SliderListenerB());
			cSlider.removeChangeListener(new SliderListenerC());
			isListenersAdded = false;
		}

		/**
		 * Load widgets with object's properties.
		 */
		public void load() {
			super.load();
			removeActionListeners();
			aSlider.setValue((int)floor(100*curvatureA));
			bSlider.setValue((int)floor(100*curvatureB));
			cSlider.setValue((int)floor(100*curvatureC));
			// [pending] use JTable.resizeAndRepaint() ???
			// add listeners AFTERWARDS ! otherwise loading widgets initial value has a painful side-effet...
			// since it call "store" before everything has been loaded
			addActionListeners();// done the first time load is called

		}

		/**
		 * Update <code>Element</code>'s properties.
		 */
		public void store() {
			super.store();
			curvatureA = ((double)aSlider.getValue())/100;
			curvatureB = ((double)bSlider.getValue())/100;
			curvatureC = ((double)cSlider.getValue())/100;
			updateBezierPts();
			fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
		}

		// public void actionPerformed(ActionEvent e)  // inherited

		/**
		 * @return The panel title, used e.g. for <code>Border</code> or <code>Tabpane</code> title.
		 */
		public String getTitle() {
			return PicPsCurve.this.getName();
		}
	}

	///////////////////////////////////////////////////
	//// INNER CLASSES for Customizer
	///////////////////////////////////////////////////

	/**
	 * An implementation of <code>JTable</code> for <code>PicPsCurve</code>'s
	 */
	class PolygonJTable extends JTable {

		private final CoordinateCellEditor coordCellEditor = new CoordinateCellEditor(new DecimalNumberField(0, 5));
		//	        private final DefaultCellEditor BooleanEditor = new DefaultCellEditor(new Boolean(true));
		private final PolygonTableModel model;

		/**
		 * Constructor for the <code>PolygonJTable</code> object.
		 */
		PolygonJTable() {
			super();
			setModel(model = new PolygonTableModel());
		}

		/**
		 * Return an appropriate editor for the cell specified by row and column.
		 */
		public TableCellEditor getCellEditor(int row, int col) {
			if (col > 0 && col !=3)
				return coordCellEditor;
			//			else if (col >0 )
			//    return BooleanEditor;
			else
				return super.getCellEditor(row, col);
		}
	}

	/**
	 * <code>PolygonJTable</code>'s cell editor is based on a <code>DecimalNumberField</code>.
	 */
	class CoordinateCellEditor extends DefaultCellEditor {

		private DecimalNumberField dnf;


		/**
		 * Construct a new cell editor from the given <code>DecimalNumberField</code>.
		 */
		CoordinateCellEditor(DecimalNumberField dnf) {
			super(dnf);
			this.dnf = dnf;

		}


		/**
		 * Return the value contained in the editor-overriden from
		 * DefaultCellEditor so as to return a Double instead of a String.
		 */
		public Object getCellEditorValue() {
			return new Double(dnf.getValue());
		}

	}

	/**
	 * A class that specifies the methods the <code>JTable</code> will use to interrogate a
	 * tabular data model.
	 */
	class PolygonTableModel extends AbstractTableModel {

		/**
		 * Return the name of the column at columnIndex.
		 */
		public String getColumnName(int colIndex) {
			switch (colIndex) {
			case 0:
				return localize("misc.Point");// polygon point index
			case 1:
				return "X";// x-coordinate
			case 2:
				return "Y";// y-coordinate
			case 3:
				return localize("misc.Adjust");
			case 4:
				return localize("misc.Coefficient");
			default:
				return "";
			}
		}


		/**
		 * Return the number of columns in the model.
		 *
		 * @return   The columnCount value
		 */
		public int getColumnCount() {
			return 3;// 3 columns : "pt", "X", "Y"
		}


		/**
		 * Return the number of rows in the model.
		 *
		 * @return   The rowCount value
		 */
		public int getRowCount() {
			return polygonPts.size();// there are as many rows as points in the associated PicPsCurve
		}


		/**
		 * Return true if the cell at rowIndex and columnIndex is editable.
		 *
		 * @param rowIndex  Description of the Parameter
		 * @param colIndex  Description of the Parameter
		 * @return          The cellEditable value
		 */
		public boolean isCellEditable(int rowIndex, int colIndex) {
			if (colIndex == 0)
				return false;// first column is not editable since it containts point indexes
			else
				return true;
		}

		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		/**
		 * Called when the associated <code>JTable</code> wants to know what to display at
		 * columnIndex and rowIndex. Depending on colIndex, we return, either the
		 * point index, the x-coord, or the y-coord of the corresponding polygon's
		 * point.
		 *
		 * @param rowIndex  Description of the Parameter
		 * @param colIndex  Description of the Parameter
		 * @return          The valueAt value
		 */
		public Object getValueAt(int rowIndex, int colIndex) {

			switch (colIndex) {
			case 0:
				return new Integer(rowIndex);// polygon point index
			case 1:
				return new Double(PEToolKit.doubleToString(getPolygonPt(rowIndex).x));// x-coord for the row-th point
			case 2:
				return new Double(PEToolKit.doubleToString(getPolygonPt(rowIndex).y));// y-coord for the row-th point
			default:
				return null;
			}
		}


		/**
		 * Called when a user entered a new value in the cell at columnIndex and
		 * rowIndex to value. We update the pscurve geometry according to the cell
		 * value.<br>
		 * This method does nearly the same thing as actionPerformed in other
		 * xxxxCustomizer's.
		 *
		 * @param value     The new valueAt value
		 * @param rowIndex  The new valueAt value
		 * @param colIndex  The new valueAt value
		 */
		public void setValueAt(Object value, int rowIndex, int colIndex) {

			PicPoint pt = getCtrlPt(rowIndex, null);
			switch (colIndex) {// do we modify x or y ?
			case 1:
				pt.x = ((Double)value).doubleValue();
				break;
			case 2:
				pt.y = ((Double)value).doubleValue();
				break;
			default:
				return;
			}
			setCtrlPt(rowIndex, pt,null);
			updateBezierPts();
			fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
		}
	}// inner class
}
