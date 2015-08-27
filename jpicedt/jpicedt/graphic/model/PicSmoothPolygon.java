// PicSmoothPolygon.java --- -*- coding: iso-8859-1 -*-
// December 25, 2002 - jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: PicSmoothPolygon.java,v 1.35 2013/03/27 07:01:18 vincentb1 Exp $
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.event.*;
import javax.swing.table.*;

import jpicedt.graphic.*;
import jpicedt.graphic.toolkit.*;
import jpicedt.graphic.event.DrawingEvent;
import jpicedt.graphic.view.View;
import jpicedt.graphic.view.HitInfo;
import jpicedt.ui.dialog.UserConfirmationCache;
import jpicedt.widgets.*;

import static jpicedt.Localizer.*;

/**
 * A multicurve, either closed or not, the geometry of which is specified using a polygon, in such a way that
 * the curve snakes "smoothly" along the polygon. For each polygon point, a coefficient helps altering the
 * smoothness of the the path. Like the class it inherits from, this element has a variable number of
 * user-controlled points that can be added/inserted/deleted by the user.<br>
 * Each polygon point is also an anchor point (e.g. for grid alignment).
 * <p><b> Implementation notes : </b> inherited <code>pts</code> ArrayList acts as an array of specification
 * points for the multicurve (this behaviour being mostly inherited from superclass). Besides, there's an
 * <code>ArrayList</code> of <code>PicPoint</code>'s, namely <code>polygonPts</code>, which backs the geometry
 * of the polygon, and acts as a list of user-controlled points.
 * Moving a control-point thus updates specification points as appropriate.
 *
 * [SR:todo:11/01/2003] add documentation about curve equation.
 *
 * @author    Vincent Guirardel
 * @since     jpicedt 1.3.3
 * @version   $Id: PicSmoothPolygon.java,v 1.35 2013/03/27 07:01:18 vincentb1 Exp $
 *
 */
public class PicSmoothPolygon extends AbstractCurve implements ActionFactory, CustomizerFactory,PicMultiCurveConvertable {

	//////////////////////////
	//// PROTECTED FIELDS
	//////////////////////////


	/**
	 * An array of <code>PicPoint</code>'s backing the geometry of the polygon. Aka caching mechanism holding
	 * a list of user-controlled points.<br>
	 * Note: now, inherited <b>bezierPts</b> array obviously acts as an array of specification points only.
	 */
	protected ArrayList<PicPoint> polygonPts = new ArrayList<PicPoint>();

	// inherited : protected boolean isClosed;

	/**
	 * The following array contains one Double per polygon point, specifying how close to
	 * polygon points the curve should go along.
	 */
	protected ArrayList<Double> smoothCoeff = new ArrayList<Double>();

	/**
	 * The following array contains one boolean per polygon point, used in the Customizer inner class
	 * to know whether the JSlider component should adjust the corresponding coeff or not.
	 */
	private ArrayList<Boolean> adjust = new ArrayList<Boolean>();

	/**
	 * The default value for the smoothness coefficient on startup.
	 * [SR:pending] fetch from a Properties object in constructor
	 */
	protected static double DEFAULT_SMOOTH_COEFF = 0.7;

	/**
	 * Min value of smoothness coefficient accessible to JSlider in the geometry editor
	 */
	protected static int COEF_SLIDER_MIN = -50 ;

	/**
	 * Max value of smoothness coefficient accessible to Jslider in the geometry editor
	 */
	protected static int COEF_SLIDER_MAX = 200 ;



	//////////////////////////////
	/// CONSTRUCTORS
	//////////////////////////////

	/**
	 * Create a new empty open smooth polygon, with a default attribute set.
	 */
	public PicSmoothPolygon() {
		this(false);
	}

	/**
	 * Create a new empty smooth polygon, open or closed, with a default attribute set bound to it.
	 * @param closed  whether the generated multi-curve will be closed or not
	 */
	public PicSmoothPolygon(boolean closed) {
		super(closed);
	}


	/**
	 * Create a new empty smooth polygon with the given attribute set.
	 * @param set     attribute set to be bound to this element
	 * @param closed  whether the generated multi-curve will be closed or not
	 */
	public PicSmoothPolygon(boolean closed, PicAttributeSet set) {
		this(closed);
		this.attributeSet = new PicAttributeSet(set);
	}

	/**
	 * Create a new smooth polygon initialized from the given array of PicPoint's.
	 *
	 * @param polyPts array of PicPoint's specifying the polygon geometry.
	 * @param set     attribute set to be bound to this element
	 * @param closed  whether the generated multi-curve will be closed or not
	 * @param smoothCoeff the default smoothness value for each polygon point
	 * @since         PicEdt 1.3.3
	 */
	public PicSmoothPolygon(PicPoint[] polyPts, boolean closed, double smoothCoeff, PicAttributeSet set) {
		super(closed);
		this.attributeSet = new PicAttributeSet(set);
		for (int i = 0; i < polyPts.length; i++) {
			this.polygonPts.add(new PicPoint(polyPts[i]));
			this.smoothCoeff.add(new Double(smoothCoeff));
			this.adjust.add(Boolean.TRUE);
		}
		this.allocateBezierPts(); // at least, we this call we're clear !
		this.updateBezierPts();
	}

	/**
	 * Create a new smooth polygon initialized from the given array of PicPoint's, using the
	 * given array of smoothness values. If array sizes do not match,
	 * number of polygon points gets trimmed to the smaller of both.
	 *
	 * @param polygonPts array of PicPoint's specifying the polygon geometry.
	 * @param set     attribute set to be bound to this element
	 * @param closed  whether the generated multi-curve will be closed or not
	 * @param smoothCoeffs array of smoothness values for each polygon point
	 * @since         PicEdt 1.3.3
	 */
	public PicSmoothPolygon(PicPoint[] polygonPts, boolean closed, double[] smoothCoeffs, PicAttributeSet set) {
		super(closed);
		this.attributeSet = new PicAttributeSet(set);
		int size = (polygonPts.length < smoothCoeffs.length ? polygonPts.length : smoothCoeffs.length);
		for (int i = 0; i < size; i++) {
			this.polygonPts.add(new PicPoint(polygonPts[i]));
			this.smoothCoeff.add(new Double(smoothCoeffs[i]));
			this.adjust.add(Boolean.TRUE);
		}
		this.allocateBezierPts(); // at least, we this call we're clear !
		this.updateBezierPts();
	}

	/**
	 * Create a new smooth polygon initialized from the given array of PicPoint's, using the
	 * default smoothness value.
	 *
	 * @param polygonPts array of PicPoint's specifying the polygon geometry.
	 * @param set     attribute set to be bound to this element
	 * @param closed  whether the generated multi-curve will be closed or not
	 * @since         PicEdt 1.3.3
	 */
	public PicSmoothPolygon(PicPoint[] polygonPts, boolean closed, PicAttributeSet set) {
		this(polygonPts, closed, DEFAULT_SMOOTH_COEFF, set);
	}

	/**
	 * "cloning" constructor (to be used by clone())
	 * @param poly  PicSmoothPolygon to be cloned
	 * @since       jpicedt 1.3.3
	 */
	public PicSmoothPolygon(PicSmoothPolygon poly) {
		super(poly);
		smoothCoeff = new ArrayList<Double>(poly.smoothCoeff);
		adjust = new ArrayList<Boolean>(poly.adjust);
		for (PicPoint pt : poly.polygonPts)
			this.polygonPts.add(new PicPoint(pt));
	}


	/**
	 * Overide Object.clone() method
	 *
	 * @return   Description of the Return Value
	 * @since    PicEdt 1.1
	 */
	public PicSmoothPolygon clone() {
		return new PicSmoothPolygon(this);
	}


	/**
	 * @return   a localised string that represents this object's name
	 */
	public String getDefaultName() {
		return localize("model.SmoothPoly");
	}


	//////////////////////////////////
	/// OPERATIONS ON CONTROL POINTS
	//////////////////////////////////
	/**
	 * Computes the number of Bezier points corresponding to the given number of
	 * polygon points and to the closeness of the curve.
	 */
	private int computeNumberOfBezierPts(int nbPolygonPts, boolean closed) {
		int nbBezierPts;

		if (closed)
			switch (nbPolygonPts) {
			case 0: nbBezierPts = 0; break; // nbBezSeg=0
			case 1: nbBezierPts = 3; break; // nbBezSeg=1
			case 2: nbBezierPts = 3; break; // nbBezSeg=1
			default: nbBezierPts = 3*nbPolygonPts; break; // nbBezSeg=nbPolygonPts
			}
		else
			switch (nbPolygonPts) {
			case 0: nbBezierPts = 0; break; // nbBezSeg=inconsistant (-1/3) !!!
			case 1: nbBezierPts = 1; break; // nbBezSeg=0
			case 2: nbBezierPts = 4; break; // nbBezSeg=1
			default: nbBezierPts = 3*nbPolygonPts-5; break; // nbBezSeg=nbPolygonPts-2
			}
		return nbBezierPts;
	}

	/**
	 * Allocates a number of Bezier points appropriate for the current number of polygon points.
	 */
	private void allocateBezierPts() {
		int lb;
		int l = polygonPts.size();
		lb = computeNumberOfBezierPts(l, isClosed);
		bezierPts.clear();
		for (int i=0; i<lb; i++)
			bezierPts.add(new PicPoint());
	}


	/**
	 * Update the location of Bezier points according to the current location of polygon points.
	 * This obviously relies on the value of the smoothness coefficient for each polygon point.
	 */
	private void updateBezierPts() {
		int nbPolyPts = polygonPts.size();
		if (isClosed())
			switch (nbPolyPts) {
			case 0: break;
			case 1:
				setBezierPt(0, getPolygonPt(0));
				setBezierPt(1, getPolygonPt(0));
				setBezierPt(2, getPolygonPt(0));
				break;
			case 2:
				getBezierPt(0).setCoordinates(getPolygonPt(0)).middle(getPolygonPt(1)); // mid of poly(0,1)

				setBezierPt(1, getBezierPt(0)); // mid of poly(0,1)
				getBezierPt(1).translate(getPolygonPt(0), getPolygonPt(1), getSmoothCoefficient(1)/2.0); // translate by poly(0,1)*smooth/2

				setBezierPt(2, getBezierPt(0)); // mid of poly(0,1)
				getBezierPt(2).translate(getPolygonPt(1), getPolygonPt(0), getSmoothCoefficient(0)/2.0);

				break;
			default:
				for (int i = 0; i < nbPolyPts; i++) {
					getBezierPt(3*i).setCoordinates(getPolygonPt(i)).middle(getPolygonPt(i+1)); // mid of poly(i,i+1)

					setBezierPt(3*i+1, getBezierPt(3*i)); // mid of poly(i,i+1)
					getBezierPt(3*i+1).translate(getPolygonPt(i), getPolygonPt(i+1), getSmoothCoefficient(i+1)/2.0); // translate by poly(0,1)*smooth/2

					getBezierPt(3*i+2).setCoordinates(getPolygonPt(i+1)).middle(getPolygonPt(i+2)); // mid of poly(i+1,i+2)
					getBezierPt(3*i+2).translate(getPolygonPt(i+2), getPolygonPt(i+1), getSmoothCoefficient(i+1)/2.0); // translate by poly(0,1)*smooth/2
				}
			}
		else { // open polygon
			switch (nbPolyPts) {
			case 0: break;
			case 1:
				setBezierPt(0, getPolygonPt(0));
				break;
			case 2:
				setBezierPt(0, getPolygonPt(0));
				setBezierPt(3, getPolygonPt(1));

				setBezierPt(1, getPolygonPt(0));
				getBezierPt(1).translate(getPolygonPt(0), getPolygonPt(1), getSmoothCoefficient(0)/2.0); // translate by poly(0,1)*smooth/2

				setBezierPt(2, getPolygonPt(1));
				getBezierPt(2).translate(getPolygonPt(1), getPolygonPt(0), getSmoothCoefficient(1)/2.0); // translate by poly(1,0)*smooth/2

				break;
			default:
				int nbBzPts = 3*nbPolyPts-5;

				setBezierPt(0, getPolygonPt(0));

				setBezierPt(1, getPolygonPt(0));
				getBezierPt(1).translate(getPolygonPt(0), getPolygonPt(1), getSmoothCoefficient(0)); // translate by poly(0,1)*smooth

				setBezierPt(nbBzPts-2, getPolygonPt(nbPolyPts-1));
				getBezierPt(nbBzPts-2).translate(getPolygonPt(nbPolyPts-1), getPolygonPt(nbPolyPts-2), getSmoothCoefficient(nbPolyPts-1)); // translate by poly(0,1)*smooth/2

				setBezierPt(nbBzPts-1, getPolygonPt(nbPolyPts-1)); // last end-point

				for (int i = 1; i < nbPolyPts-2; i++) {
					// Bezier endpoint at mid-seg
					getBezierPt(3*i).setCoordinates(getPolygonPt(i)).middle(getPolygonPt(i+1)); // mid of poly(i,i+1)
					//control points
					setBezierPt(3*i-1, getBezierPt(3*i));
					getBezierPt(3*i-1).translate(getBezierPt(3*i), getPolygonPt(i), getSmoothCoefficient(i)); // =polygonPtsX[i] when smoothCoeff==1

					setBezierPt(3*i+1, getBezierPt(3*i));
					getBezierPt(3*i+1).translate(getBezierPt(3*i), getPolygonPt(i+1), getSmoothCoefficient(i+1)); // =polygonPtsX[i+1] when smoothCoeff==1
				}
				break;
			} // end of open case
		}
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
	 * Return the <b>user-controlled point</b> (ie a polygon point, not a bezier point) having the given
	 * index.  The general contract in Element is to return an IMMUTABLE instance of PicPoint, so that the
	 * only way to alter the geometry of this element is by calling the <code>setCtrlPt</code> method.<br>
	 * Overriden so as to return a "polygon" point instead of a bezier-point (which act as specification-point
	 * here).
	 * @return the point indexed by <code>numPoint</code> ;
	 *         if <code>dest</code> is null, allocates a new PicPoint and return it,
	 *         otherwise directly modifies <code>dest</code> and returns it as well for convenience.
	 * @param numPoint the point index, should be greater or equal to the value returned by
	 *        <code>getFirstPointIndex</code>, and lower or equal to <code>getLastPointIndex</code>.
	 */
	public PicPoint getCtrlPt(int numPoint, PicPoint dest){
		if (dest==null) dest = new PicPoint();
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
	 * Add the given point at the end of this <code>PicSmoothPolygon</code>.
	 */
	public void addPoint(PicPoint pt) {
		addPoint(pt, DEFAULT_SMOOTH_COEFF);
	}

	/**
	 * Add the given point with the given smoothness-coefficient at the end of this
	 * <code>PicSmoothPolygon</code>.
	 */
	public void addPoint(PicPoint pt, double smoothCoeff) {
		polygonPts.add(new PicPoint(pt));
		this.smoothCoeff.add(new Double(smoothCoeff));
		adjust.add(Boolean.TRUE);
		this.allocateBezierPts();
		this.updateBezierPts();
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}

	/**
	 * Add the given polygon point at the given position, shifting ensuing point indices to the right.  For
	 * instance, adding at point at position "1" is equivalent to splitting segment "0".
	 */
	public void addPoint(int ptIndex, PicPoint pt){
		addPoint(ptIndex, pt, DEFAULT_SMOOTH_COEFF);
	}

	/**
	 * Add the given polygon point at the given position, shifting ensuing point indices to the right.  For
	 * instance, adding at point at position "1" is equivalent to splitting segment "0".
	 */
	public void addPoint(int ptIndex, PicPoint pt, double smoothCoeff){
		splitSegment(ptIndex-1, pt, smoothCoeff);
	}

	/**
	 * Split the given polygon segment (starting from 0), by inserting a new polygon point at the appropriate
	 * position.  then fires a changed-update.  For instance, spliting segment "2" yields the following
	 * polygon points : 0, 1, 2, insertion pt, 3, 4, etc&hellip;
	 *
	 * @param segIdx index of the polygon segment that must be split.
	 * @param pt   the PicPoint to be added
	 */
	public int splitSegment(int segIdx, PicPoint pt) {
		return splitSegment(segIdx, pt, DEFAULT_SMOOTH_COEFF);
	}

	/**
	 * Split the given polygon segment (starting from 0), by inserting a new polygon point at the appropriate
	 * position.  then fires a changed-update.  For instance, spliting segment "2" yields the following
	 * polygon points : 0, 1, 2, insertion pt, 3, 4, etc&hellip;
	 *
	 * @param segIdx index of the polygon segment that must be split.
	 * @param smoothCoeff the smoothness coefficient to be used for the new polygon point.
	 * @param pt   the PicPoint to be added
	 */
	public int splitSegment(int segIdx, PicPoint pt, double smoothCoeff) {

		if (segIdx < 0 || segIdx > polygonPts.size()) throw new IndexOutOfBoundsException(new Integer(segIdx).toString());

		polygonPts.add(segIdx+1, new PicPoint(pt)); // ArrayList.add shifts ensuing indices to the right
		this.smoothCoeff.add(segIdx+1,new Double(smoothCoeff));
		adjust.add(segIdx+1, Boolean.TRUE);

		this.allocateBezierPts();
		this.updateBezierPts();
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
		return segIdx+1;
	}

	/**
	 * Remove a point at the given position from this PicSmoothPolygon, then fire a
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
		smoothCoeff.remove(pos);
		adjust.remove(pos);

		this.allocateBezierPts();
		this.updateBezierPts();
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}


	/**
	 * Remove the last point of this <code>PicSmoothPolygon</code>.
	 * This is a convenience call to <code>removePoint(polygonPts.size()-1)</code>
	 */
	public void removeLastPoint() {
		removePoint(polygonPts.size()-1);
	}


	/**
	 * Convert this <code>PicSmoothPolygon</code> to a <code>PicMultiCurve</code>, by simply relying on the
	 * appropriate constructor in class {@link PicMultiCurve PicMultiCurve}.
	 */
	public PicMultiCurve convertToMultiCurve(){
		PicMultiCurve curve=new PicMultiCurve(this);
		return curve;
	}



	////////////////////////////
	//// OTHER FIELDS ACCESSORS
	////////////////////////////

	/**
	 * @param index  Description of the Parameter
	 * @return smoothness smoothCoeff of the given control point
	 */

	public double getSmoothCoefficient(int index) { // was : getCoeff
		return smoothCoeff.get(index % polygonPts.size()).doubleValue();
	}


	/**
	 * @param index index of point whose smoothCoeff is to be adjusted
	 * @param c value of the smoothCoeff
	 */
	public void setSmoothCoefficient(int index, double c) { // was : setCoeff
		smoothCoeff.set(index, new Double(c));
		updateBezierPts();
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}


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

	//////////////////////////////////////////////////////////
	//// TRANSFORMS
	//////////////////////////////////////////////////////////

	/**
	 * Translate this Element by (dx,dy) ; this implementation translates the specification-points,
	 * then fires a changed-update event.
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
	 * Shear this Element by the given params wrt to the given origin
	 */
	public void shear(PicPoint ptOrg, double shx, double shy, UserConfirmationCache ucc){
		for (PicPoint pt: polygonPts)
			pt.shear(ptOrg,shx,shy);
		super.shear(ptOrg,shx,shy,ucc); // shear bezierPts
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}

	/**
	 * @see jpicedt.graphic.model#Element.getCtrlPtSubset(ConvexZoneGroup csg,BitSet czExtension)
	 * @param csg l'ensemble de zones convexes <code>ConvexZoneGroup</code> auquel on teste l'appartenance des
	 * points de contrôle.
	 * @param czExtension cet argument est ignoré
	 * @return a <code>CtrlPtSubset</code> value
	 * @since jPicEdt 1.6
	 */
	public CtrlPtSubset getCtrlPtSubset(ConvexZoneGroup csg,BitSet czExtension){
		ArrayList<PicPoint> ctrlPts = new ArrayList<PicPoint>(polygonPts.size());

		for(PicPoint pt : polygonPts)
			if(csg.containsPoint(pt)) ctrlPts.add(pt);

		if(ctrlPts.size() == 0) return null;
		if(ctrlPts.size() == polygonPts.size()) return new CtrlPtSubsetPlain(this);

		return new CtrlPtSubsetSmoothPolygon(this, ctrlPts);
	}

	class CtrlPtSubsetSmoothPolygon implements CtrlPtSubset{
		PicSmoothPolygon     smoothPoly;
		ArrayList<PicPoint>  ctrlPts;
		public CtrlPtSubsetSmoothPolygon(PicSmoothPolygon smoothPoly,
										 ArrayList<PicPoint> ctrlPts){
			this.smoothPoly = smoothPoly;
			this.ctrlPts = ctrlPts;
		}

		public void translate(double dx, double dy){
			for(PicPoint pt : ctrlPts)
			   pt.translate(dx, dy);
			smoothPoly.updateBezierPts();
			smoothPoly.fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
		}
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
	 * @since jpicedt 1.3.3
	 */
	public Rectangle2D getBoundingBox(Rectangle2D r){
		r = super.getBoundingBox(r);
		for (PicPoint pt: polygonPts)
			r.add(pt); // then enlarge bb by adding subsequent points
		return r;
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
		for (int i=0; i<polygonPts.size(); i++){
			s += "coeff[" + i + "]=" + getSmoothCoefficient(i) + " ";
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
	 * @param actionDispatcher  dispatches events to the proper <code>PECanvas</code>
	 * @param localizer         i18n localizer for <code>PEAction</code>'s
	 */
	public ArrayList<PEAction> createActions(ActionDispatcher actionDispatcher, ActionLocalizer localizer, HitInfo hi) {
		ArrayList<PEAction> actionArray = super.createActions(actionDispatcher, localizer, hi);
		if (actionArray==null)
			actionArray = new ArrayList<PEAction>();
		actionArray.add( new ConvertToCurveAction(actionDispatcher, localizer));
		return actionArray;
	}

	/**
	 * @return   A Customizer for geometry editing.
	 */
	public AbstractCustomizer createCustomizer(){
		if (cachedCustomizer == null)
			cachedCustomizer = new Customizer();
		cachedCustomizer.load();
		return cachedCustomizer;
	}

	private Customizer cachedCustomizer = null;

	// ---- SmoothPoly to multiCurve ----

	/**
	 * Convert this <code>PicSmoothPolygon</code> to a multicurve, selecting it if applicable.
	 * @author    Sylvain Reynal
	 * @since     jpicedt 1.4
	 */
	class ConvertToCurveAction extends PEAction {

		public static final String KEY = "action.editorkit.ConvertSmoothPolyToCurve";

		public ConvertToCurveAction(ActionDispatcher actionDispatcher, ActionLocalizer localizer){
			super(actionDispatcher, KEY, localizer);
		}

		public void undoableActionPerformed(ActionEvent e){
			PicMultiCurve curve = convertToMultiCurve();
			Drawing dr = getDrawing();
			if (dr != null){
				dr.replace(PicSmoothPolygon.this, curve);
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
	 * Geometry customizer.
	 *
	 * @author    reynal
	 * @created   January 11, 2003
	 */
	class Customizer extends AbstractCurve.Customizer {

		private DefaultCellEditor doubleEditor;
		private JSlider coefSlider;// to adjust coef
		private boolean isListenersAdded = false;// flag
		private	PolygonJTable table ;

		/**
		 * Constructor for the Customizer object
		 */
		public Customizer() {

			super();
			Box p = new Box(BoxLayout.Y_AXIS);
			// polygon points:
			table = new PolygonJTable(this);
			table.setPreferredScrollableViewportSize(new Dimension(500, 400));

			// wrap scrollpane around:
			JScrollPane scrollPane = new JScrollPane(table);
			p.add(scrollPane);
			p.add(coefSlider = new JSlider(COEF_SLIDER_MIN,COEF_SLIDER_MAX,70));
			coefSlider.setMajorTickSpacing(50);
			coefSlider.setMinorTickSpacing(10);
			coefSlider.setPaintTicks(true);
			coefSlider.setPaintLabels(true);
			add(p, BorderLayout.NORTH);
			add(super.createPanel(), BorderLayout.CENTER);
			setPreferredSize(new Dimension(500,500));
		}

		// a listener for the slider
		class SliderListener implements ChangeListener {
			boolean ignoreNextEvent = false;
			public void setIgnoreNextEvent(){ ignoreNextEvent = true; }

			public void stateChanged(ChangeEvent e) {
				if(ignoreNextEvent)
					ignoreNextEvent = false;
				else
				{
					JSlider source = (JSlider)e.getSource();
					double coef = (double)source.getValue()/100;
					for (int i=0;i <= getLastPointIndex();i++){
						if (adjust.get(i)==Boolean.TRUE) smoothCoeff.set(i,new Double(coef));
					}
					if (!source.getValueIsAdjusting()) {table.repaint();}
					updateBezierPts();
					fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
				}
			}
		}

		/**
		 * Add action listeners to widgets to reflect changes immediately.
		 */
		private void addActionListeners() {
			if (isListenersAdded)
				return;// already done
			coefSlider.addChangeListener(new SliderListener());
			isListenersAdded = true;
		}

		private void removeActionListeners() {
			if (!isListenersAdded)
				return;// already done
			coefSlider.removeChangeListener(new SliderListener());
			isListenersAdded = false;
		}

		/** Règle la glissière sans que cela n'ait d'effet sur les coefficients &mdash; en effet les écouteurs
		 * d'action sont ôtés avant le réglage, et remis après.
		 */
		protected void setSlider(){

			double sliderValue = 0;
			int adjustableCount = 0;
			for (int i=0;i <= getLastPointIndex();i++){
				if(adjust.get(i))
				{
					++adjustableCount;
					sliderValue += smoothCoeff.get(i);
				}
			}
			if(adjustableCount == 0)
			{
				adjustableCount = 1;
				sliderValue = smoothCoeff.get(0);
			}
			int oldValue = coefSlider.getValue();
			int newValue = (int)Math.round(sliderValue*100.0/adjustableCount);
			if(newValue != oldValue)
			{
				ChangeListener[] listeners = coefSlider.getChangeListeners();
				for(ChangeListener cl : listeners)
				{
					SliderListener sl = (SliderListener)cl;
					sl.setIgnoreNextEvent();
				}
				coefSlider.setValue((int)Math.round(sliderValue*100.0/adjustableCount));
			}

		}

		/**
		 * Load widgets with object's properties.
		 */
		public void load() {
			super.load();

			removeActionListeners();
			setSlider();

			// [pending] use JTable.resizeAndRepaint() ??? SR: apparently not needed
			// add listeners AFTERWARDS ! otherwise loading widgets initial value has a painful side-effet...
			// since it call "store" before everything has been loaded
			addActionListeners();// done the first time load is called

		}

		/**
		 * Update <code>Element</code>'s properties.
		 */
		public void store() {
			super.store();
			for (int i=0;i <= getLastPointIndex();i++){
				if(adjust.get(i))
					smoothCoeff.set(i,new Double(((double)coefSlider.getValue())/100));
			}
			updateBezierPts();
			fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
		}

		// public void actionPerformed(ActionEvent e)  // inherited

		/**
		 * @return   The panel title, used e.g. for Border or Tabpane title.
		 */
		public String getTitle() {
			return PicSmoothPolygon.this.getName();
		}
	}

	///////////////////////////////////////////////////
	//// INNER CLASSES for Customizer
	///////////////////////////////////////////////////

	/**
	 * An implementation of <code>JTable</code> for <code>PicSmoothPolygon</code>'s
	 */
	class PolygonJTable extends JTable {

		private final CoordinateCellEditor coordCellEditor = new CoordinateCellEditor(new DecimalNumberField(0, 5));
		//	        private final DefaultCellEditor BooleanEditor = new DefaultCellEditor(new Boolean(true));
		private final PolygonTableModel model;

		/**
		 * Constructor for the <code>PolygonJTable</code> object
		 */
		PolygonJTable(Customizer customizer) {
			super();
			setModel(model = new PolygonTableModel(customizer));
		}

		/**
		 * Returns an appropriate editor for the cell specified by row and column.
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
		 * Returns the value contained in the editor-overriden from
		 * <code>DefaultCellEditor</code> so as to return a Double instead of a String.
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

		private final Customizer customizer;

		PolygonTableModel(Customizer customizer){
			this.customizer = customizer;
		}

		/**
		 * Returns the name of the column at columnIndex.
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
		 * Returns the number of columns in the model.
		 *
		 * @return   The columnCount value
		 */
		public int getColumnCount() {
			return 5;// 4 columns : "pt", "X", "Y", and "adjust"
		}


		/**
		 * Returns the number of rows in the model.
		 *
		 * @return   The rowCount value
		 */
		public int getRowCount() {
			return polygonPts.size();// there are as many rows as points in the associated PicSmoothPolygon
		}


		/**
		 * Returns true if the cell at rowIndex and columnIndex is editable.
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
			case 3:
				return (Boolean)adjust.get(rowIndex); // box is checked
			case 4:
				return new Double(PEToolKit.doubleToString(100*getSmoothCoefficient(rowIndex)));// coefficient
			default:
				return null;
			}
		}


		/**
		 * Called when a user entered a new value in the cell at columnIndex and
		 * rowIndex to value. We update <code>PicSmoothPolygon</code> geometry according to the cell
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
			case 3:
				adjust.set(rowIndex,(Boolean)value); // Boolean
				customizer.setSlider();
				break;
			case 4:
				smoothCoeff.set(rowIndex, new Double(((Double)value).doubleValue()/100));
				customizer.setSlider();
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
