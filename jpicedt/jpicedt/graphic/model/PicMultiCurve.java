// PicMultiCurve.java --- December 25, 2002 - jPicEdt, a picture editor for LaTeX.
// Copyright 1999-2006 Sylvain Reynal
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
// Version: $Id: PicMultiCurve.java,v 1.45 2013/03/27 07:01:44 vincentb1 Exp $
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
import jpicedt.graphic.toolkit.BasicEditPointConstraint;
import jpicedt.graphic.event.DrawingEvent;
import jpicedt.widgets.*;
import jpicedt.graphic.view.HitInfo;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.*;

import static jpicedt.Log.*;
import static java.lang.Math.min;
import static jpicedt.Localizer.*;
import static jpicedt.graphic.model.EditPointConstraint.EditConstraint.*;
import static jpicedt.graphic.model.AbstractCurve.PointType.*;

/**
 * <code>PicMultiCurve</code> is basically an <code>AbstractCurve</code> where each subdivision point has
 * additionnal features regarding 2nd-order smoothness and symmetry. These features, however, are dynamic in
 * the sense that they may be enforced by passing geometric constraints to the {@link #setCtrlPt setCtrlPt()}
 * method instead of having them tightly bound to the class. Besides, {@link #setSmooth setSmooth()} and
 * {@link #setSymmetric setSymmetric()} methods may be used to dynamically enforce these features w/o having
 * to move control-points.
 * @author    Vincent Guirardel, Sylvain Reynal
 * @since     jpicedt 1.3.3
 * @version $Id: PicMultiCurve.java,v 1.45 2013/03/27 07:01:44 vincentb1 Exp $
 */
public class PicMultiCurve extends AbstractCurve implements CustomizerFactory,PicMultiCurveConvertable {


	//////////////////////////
	//// PRIVATE FIELDS
	//////////////////////////

	//////////////////////////////
	/// CONSTRUCTORS
	//////////////////////////////

	/**
	 * Create a new empty open multi curve, with a default attribute set.
	 */
	public PicMultiCurve() {
		this(false);
	}

	/**
	 * Create a new empty multi curve, with a default attribute set.
	 * @param closed  whether this curve is closed or not
	 */
	public PicMultiCurve(boolean closed) {
		super(closed);
	}

	/**
	 * Create a new empty multi curve with the given set of attribute
	 * @param closed  whether this curve is closed or not
	 * @param set     attribute set to be bound to this element
	 */
	public PicMultiCurve(boolean closed, PicAttributeSet set) {
		this(closed);
		this.attributeSet = new PicAttributeSet(set);
	}

	/**
	 * Create a new open multi curve reduced to the given point, and a default attribute set.
	 */
	public PicMultiCurve(PicPoint pt1) {
		super(0, false); // reminder : 0 segment (hence 1pt), open
		getBezierPt(0).setCoordinates(pt1);
	}

	/**
	 * Create a new open multi curve reduced to the given point, and the given attribute set
	 */
	public PicMultiCurve(PicPoint pt1, PicAttributeSet set) {
		super(0, false); // reminder : 0 segment (hence 1pt), open
		getBezierPt(0).setCoordinates(pt1);
		this.attributeSet = new PicAttributeSet(set);
	}

	/**
	 * Create a new line, i.e. a new straight open curve using the two given points, and a default attribute set.
	 */
	public PicMultiCurve(PicPoint pt1, PicPoint pt2) {
		this(pt1);
		lineTo(pt2);
	}

	/**
	 * Create a new straight open multi curve with the two given points, and the given attribute set.
	 */
	public PicMultiCurve(PicPoint pt1, PicPoint pt2, PicAttributeSet set) {
		this(pt1,pt2);
		this.attributeSet = new PicAttributeSet(set);
	}

	/**
	 * Create an open single cubic bezier curve from the four given points, and a default attribute set.
	 */
	public PicMultiCurve(PicPoint pt1, PicPoint ctrl1, PicPoint ctrl2, PicPoint pt2) {
		this(pt1);
		curveTo(ctrl1,ctrl2,pt2);
	}

	/**
	 * Create a new multi-curve filled with the given array of PicPoint's, each
	 * segment being a curved Bezier segment.
	 * <ul>
	 * <li> if the number of points mod 3 equals 0, then it will be closed.
	 * <li> if the number of points mod 3 equals 1 then the curve will be open,
	 * <li> if the number of points mod 3 equals 2, curve is open and last segment is a straight one.
	 * </ul>
	 * Smoothness and symmetry properties are automatically asserted
	 * by checking if the given points do satisfy them.
	 */
	public PicMultiCurve(PicPoint[] bezierPts, PicAttributeSet set) {
		isClosed = (bezierPts.length%3==0);
		if (bezierPts.length%3 == 2){ // open curve, last segment is straight
			for (int i = 0; i < bezierPts.length-1; i++) this.bezierPts.add(new PicPoint(bezierPts[i]));
			lineTo(new PicPoint(bezierPts[bezierPts.length-1]));
		}
		else for (int i = 0; i < bezierPts.length; i++) this.bezierPts.add(new PicPoint(bezierPts[i]));

		this.attributeSet = new PicAttributeSet(set);
	}


	/**
	 *  Constructor for conversion of <code>PicSmoothPolygon</code> into <code>PicMultiCurve</code>.
	 * @param poly  The <code>PicSmoothPolygon</code> to be converted
	 */
	public PicMultiCurve(PicSmoothPolygon poly) {
		super(poly);
	}

	/**
	 *  Constructor for conversion of <code>PicPsCurve</code> into <code>PicMultiCurve</code>.
	 * @param curve The <code>PicPsCurve</code> to be converted
	 */
	public PicMultiCurve(PicPsCurve curve) {
		super(curve);
	}


	/**
	 * "cloning" constructor (to be used by clone())
	 *
	 * @param curve  The curve to be cloned.
	 */
	public PicMultiCurve(PicMultiCurve curve) {
		super(curve);
	}

	/**
	 * Override <code>Object.clone()</code> method.
	 */
	public PicMultiCurve clone() {
		return new PicMultiCurve(this);
	}

	/**
	 * @return   A localised string that represents this object's name.
	 */
	public String getDefaultName() {
		return localize("model.MultiCurve");
	}

	/**
	 * converts this <code>ELement</code> to a <code>PicMultiCurve</code>, i.e., itself (no copy).
	 */
	 public PicMultiCurve convertToMultiCurve(){
		 return this;
	 }

	//////////////////////////////////
	/// GLOBAL GEOM. OPERATIONS
	//////////////////////////////////

	/**
	 * Returns the shortest distance b/w the end-points of this curve and those of the given one.
	 * @since jpicedt 1.4pre5
	 */
	public double distance(PicMultiCurve other){
		 double distance = this.getBezierPt(0).distance(other.getBezierPt(0));
		 distance = min(distance, this.getBezierPt(0).distance(other.getBezierPt(other.getLastPointIndex())));
		 distance = min(distance, this.getBezierPt(this.getLastPointIndex()).distance(other.getBezierPt(0)));
		 distance = min(distance, this.getBezierPt(this.getLastPointIndex()).distance(other.getBezierPt(other.getLastPointIndex())));
		 return distance;
	}

	/**
	 * Returns the curve in the given collection that is closest to this one in the sense of the
	 * <code>distance()</code> method.  In case of two or more curves being equally close to this one, the
	 * first occurence in the collection is returned.
	 * @param l a list of <code>PicMultiCurveConvertable</code>'s ; <code>l</code> may contain this curve, in
	 * which case it's simply skipped.
	 * @return <code>null</code> if <code>l</code> doesn't contain any <code>PicMultiCurveConvertable</code>.
	 * @since jpicedt 1.4pre5
	 */
	public PicMultiCurveConvertable fetchClosestCurve(Collection l){
		PicMultiCurveConvertable closest = null;
		double d = Double.MAX_VALUE;
		for (Iterator it = l.iterator(); it.hasNext();){
			Object o = it.next();
			if (o==this) continue;
			if (o instanceof PicMultiCurveConvertable){
				PicMultiCurve c = ((PicMultiCurveConvertable)o).convertToMultiCurve();
				if (this.distance(c) < d) {
					closest = (PicMultiCurveConvertable)o;
					d = this.distance(c);
				}
			}
		}
		return closest;
	}
	//////////////////////////////////
	/// OPERATIONS ON CONTROL POINTS
	//////////////////////////////////

	/**
	 * Returns the index of the first user-controlled point that can be retrieved by <code>getCtrlPt()</code>.
	 * This default implementation returns 0.
	 */
	public int getFirstPointIndex() {
		return 0;
	}

	/**
	 * Returns the index of the last user-controlled point that can be retrieved by <code>getCtrlPt()</code>.
	 * This default implementation returns the number of specification points minus one.
	 */
	public int getLastPointIndex() {
		return bezierPts.size()-1;
	}

	/**
	 * Reverse the order in which points are stored in the protected <b>bezierPts</b> array.
	 * If the curve is open, this results in the origin being swapped with the last point, etc&hellip;
	 * May be useful when, e.g., joining two curves.
	 * @since jpicedt 1.4pre5
	 */
	public void reverseIndexing(){
		ArrayList<PicPoint> _bezierPts=new ArrayList<PicPoint>(); // tmp
		for (int i=bezierPts.size(); --i>=0; ){
			_bezierPts.add(bezierPts.get(i));
		}
		this.bezierPts = _bezierPts;
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}

	/**
	 * Join this curve and the given <code>Element</code> after proper conversion of the latter to a
	 * <code>PicMultiCurve</code>, by appending the control points of the given curve to this one.  The order
	 * in which points are appended, i.e., wheter an index-reverse takes place before appending (see {@link
	 * #reverseIndexing() }), is such that both curves get joined along their shortest path. If both curves are
	 * not in contact, a segment is added between them (through a call to <code>lineTo()</code>).
	 * @since jpicedt 1.4pre5
	 */
	public void join(PicMultiCurveConvertable c){
		if (c==null) return;
		PicMultiCurve other = c.convertToMultiCurve(); // if c is a multicurve, simply returns itself.
		//System.out.println("Joining to " + other);
		// 1) let A=this curve and B=other curve, _0 and _N the first and last point indices,
		//    check which distance is the shortest amidst:
		//    d(A_0,B_0): reverse A, append B
		//	  d(A_0,B_N): reverse A and B, append B
		//    d(A_N,B_0): append B
		//    d(A_N,B_N): reverse B, append B
		double dA0B0 = this.getBezierPt(0).distanceSq(other.getBezierPt(0));
		double dA0BN = this.getBezierPt(0).distanceSq(other.getBezierPt(other.getLastPointIndex()));
		double dANB0 = this.getBezierPt(this.getLastPointIndex()).distanceSq(other.getBezierPt(0));
		double dANBN = this.getBezierPt(this.getLastPointIndex()).distanceSq(other.getBezierPt(other.getLastPointIndex()));
		double[] distances = new double[]{dA0B0,dA0BN,dANB0,dANBN};
		Arrays.sort(distances);
		double distMin = distances[0];
		if (distMin==dA0B0){
			this.reverseIndexing();
		}
		else if (distMin==dA0BN){
			this.reverseIndexing();
			other.reverseIndexing();
		}
		else if (distMin==dANBN){
			other.reverseIndexing();
		}
		// 2) append B:
		// check if we must add a segment:
		boolean isSegmentAdded = false;
		final int joinSegIdx = this.getSegmentCount(); // "join" segment if any
		if (this.getBezierPt(this.getLastPointIndex()).distance(other.getBezierPt(0))>0){
			//this.addPoint(other.getBezierPt(0));
			this.lineTo(other.getBezierPt(0));
			isSegmentAdded=true;
		}
		// then simply add other's "bezierPts" to this's "bezierPts":
		// BUT: skip first point to avoid duplicates (get(0), either was already added through lineto(),
		// or curves are already in contact)
		for (int i = 1; i<other.bezierPts.size(); i++){
			PicPoint pt = other.bezierPts.get(i);
			this.bezierPts.add(new PicPoint(pt));
		}
		// ensure smoothness: [pending] not fully functional yet
		/*
		if (!isSegmentAdded){ // "direct" contact
			if (isStraight(joinSegIdx)==false && isStraight(joinSegIdx-1)==false){
				setSmooth(joinSegIdx);
				setSymmetric(joinSegIdx);
			}
		}
		else if (this.getSegmentCount() > joinSegIdx+1){ // make sure enough points were added
			// move to first subdiv point of second curve, which we want to smoothen
			int subdiv = segmentToPointIndex(joinSegIdx+1, SUBDIVISION_POINT);
			int srcControl = getPBCBezierIndex(subdiv+1);
			if (isValidBezierIndex(srcControl)){
				int destControl = getPBCBezierIndex(subdiv-1); // that point we wanna move so that smoothness is ensured
				PicPoint pt = getCtrlPt(srcControl);
				pt.symmetry(getCtrlPt(subdiv));
				getBezierPt(destControl).setCoordinates(pt);
			}
		}*/
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}

	/**
	 * Return a copy of the <b>user-controlled point</b> having the given index.  If <code>src</code> is null,
	 * allocates a new PicPoint and return it, otherwise directly modifies <code>src</code> and returns it as
	 * well for convenience.
	 * @param numPoint the point index, should be greater or equal to the value returned by
	 *        <code>getFirstPointIndex</code>, and lower or equal to <code>getLastPointIndex</code>.
	 */
	 public PicPoint getCtrlPt(int numPoint, PicPoint dest) {
		 if (dest==null)
			 dest = new PicPoint();
		return dest.setCoordinates(bezierPts.get(numPoint));
	}

	/**
	 * Set the coordinates of a Bezier point having the given index to the given location,
	 * possibly moving other control-points according to the given constraint.
	 * Available constraints are :
	 * <dl>
	 * <dt><code>SMOOTHNESS</code></dt>
	 * <dd> enforces smoothness at nearby subdivision-point when moving a control-point ; N/A otherwise</dd>
	 * <dt><code>SYMMETRY</code></dt>
	 * <dd>enforces symmetry at nearby subdivision-point when moving a control-point ; N/A otherwise</dd>
	 * <dt><code>SMOOTHNESS_SYMMETRY</code></dt>
	 * <dd>a combination of both.</dd>
	 * <dt><code>FREELY</code></dt>
	 * <dd>control-points are freely moved w/o any constraint being put on alternate control-points</dd>
	 * <dt><code>null</code> (default)</dt>
	 * <dd>only the given point index gets moved ; this is aka editing a single specification point. This
	 *      is applicable for both subdivision- and control-points.</dd>
	 * </dl>
	 * Except in the <b>default case</b>, subdivision-points generally "drag" their control-points with them,
	 * with the exception of straight-segment end-points which need a somewhat more complex handling.
	 */
	public void setCtrlPt(int index, PicPoint pt, EditPointConstraint constraint) {
		// this method use ptBuffer1

		if (DEBUG) debug("idx="+index);
		PointType ptType = getPointType(index);
		if (ptType==INVALID_POINT_INDEX)
			throw new IndexOutOfBoundsException(new Integer(index).toString());

		// aka setBezierPt :
		if (constraint==null){
			getBezierPt(index).setCoordinates(pt);
			fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
			return;
		}

		if (ptType==SUBDIVISION_POINT || ptType==FIRST_CURVE_END_POINT || ptType==LAST_CURVE_END_POINT) {
			// if we are at a subdivision point, don't have to care about Smoothness/Symmetry :
			// if neighbouring segments exist and are not straight , we translate neighbouring control points accordingly.
			// otherwise, we just have to retain smoothness at straight-segment's end-points : this is carried out by
			// projecting appropriate control-points onto the line directed by the straight-segment (a tough one indeed ;-)
			int thisSeg = pointToSegmentIndex(index);
			int prevSeg = getPBCSegmentIndex(thisSeg-1); // we don't take for granted these are valid indices !
			int nextSeg = getPBCSegmentIndex(thisSeg+1);
			PicVector mouseTranslation = new PicVector(getBezierPt(index),pt); // store for later use
			// the following are used for handling straight-segment smoothness only : it's vital to store these booleans
			// BEFORE moving anything !
			boolean smoothAtThisSubdiv, smoothAtPrevSubdiv=false, smoothAtNextSubdiv=false;
			boolean isThisSegStraight=false, isPrevSegStraight=false; // putain c'est bourrin ;-(
			smoothAtThisSubdiv = isSmooth(thisSeg);
			if (isValidBezierIndex(index+1)) {
				smoothAtNextSubdiv = isSmooth(nextSeg);
				isThisSegStraight = isStraight(thisSeg);
			}
			if (isValidBezierIndex(index-1)) {
				smoothAtPrevSubdiv = isSmooth(prevSeg);
				isPrevSegStraight = isStraight(prevSeg);
			}

			// and now we are entitled to move points...
			getBezierPt(index).translate(mouseTranslation); // move subdiv point
			if (isValidBezierIndex(index+1)) getBezierPt(index+1).translate(mouseTranslation); // (ptBuffer1)
			if (isValidBezierIndex(index-1)) getBezierPt(index-1).translate(mouseTranslation);

			// and enforce smoothness at both end-points if applicable...
			if (isThisSegStraight) enforceSmoothnessOfStraightSegment(thisSeg, smoothAtThisSubdiv, smoothAtNextSubdiv);
			if (isPrevSegStraight) enforceSmoothnessOfStraightSegment(prevSeg, smoothAtPrevSubdiv, smoothAtThisSubdiv);
		}

		else { // CONTROL_POINT :
			int subdiv = getNearestSubdivisionPoint(index); // nearest subdivision-point
			int altControl = getAlternateControlPoint(index); // alternate control-point

			// then check if we have to modify altControl
			if (isValidBezierIndex(altControl)) {
				int thisSeg = pointToSegmentIndex(index); // segment containing this control-point
				int altSeg = pointToSegmentIndex(altControl); // alternate segment = segment containing alternate control-point
				if (!isStraight(altSeg)){
					getBezierPt(index).setCoordinates(pt); //[SR:pending] change to setSpecPoint after "fireEvent" gets removed from it

					double length;
					switch (constraint.getEditConstraint()){
					case SMOOTHNESS_SYMMETRY:
						getBezierPt(altControl).setCoordinates(pt).symmetry(getBezierPt(subdiv));
						break;
					case SMOOTHNESS:
						length = getBezierPt(subdiv).distance(pt);
						if (length != 0) {
							double altlength = getBezierPt(subdiv).distance(getBezierPt(altControl));
							getBezierPt(altControl).setCoordinates(pt).symmetry(getBezierPt(subdiv));
							getBezierPt(altControl).scale(getBezierPt(subdiv), altlength/length);
						}
						break;
					case SYMMETRY:
						double altlength = getBezierPt(subdiv).distance(getBezierPt(altControl));
						if (altlength != 0) {
							length = getBezierPt(subdiv).distance(pt);
							getBezierPt(altControl).scale(getBezierPt(subdiv),length/altlength);
						}
						else {// if altlength ==0, we set altControl point to the symmetric of original control point.
							getBezierPt(altControl).setCoordinates(pt);
							getBezierPt(altControl).symmetry(getBezierPt(subdiv));
						}
						break;
					case FREELY:
						// don't modify altControl
					}
				}
				// else if altSeg is straight, possibly enforce smoothness by restricting the movement of THIS control-point
				// to the line directed by the straight-segment.
				// Since for a straight segment, altControl is identical with the corresponding subdiv-point,
				// we leave it as is (i.e. we don't modify segment straightness)
				else {
					getBezierPt(index).setCoordinates(pt);
					if (constraint.getEditConstraint()==SMOOTHNESS){ // make correction so that control-point is aligned with straight-segment
						// by projecting onto the line directed by the straight-segment
						getBezierPt(index).project(getBezierPt(altSeg*3),getBezierPt(altSeg*3+3));
					}
				}
			}
			else getBezierPt(index).setCoordinates(pt); // no altControl => nothing else to do
			//end of modification of altControl.
		}
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}


	/**
	 * Utility method that enforces smoothness at each end-point of the given straight segment
	 * by moving alternate control-points
	 * (provided alternate segments aren't straight themselves).
	 * <br>
	 * The policy is to move each alternate control-point to the same position as <code>setCtrlPt()</code>
	 * would do when called with the index of this alternate control-point, that is, to project this
	 * control-point onto the line directed by the straight-segment (geez, this is an obscure sentence indeed
	 * ;-)
	 * <br>
	 * This is used e.g. by <code>setCtrlPt()</code> when moving a subdivision-point belonging to a straight
	 * segment, because the usual "translate-all-points-at-once" scheme does not work anymore for straight
	 * segments (this would obviously break smoothness except for very exceptional cases).
	 * @param segIndex index of a straight segment, following the segment numbering scheme. The given segment
	 *        is supposed to be straight (no check is done), but this will work with non-straight segment as
	 *        well, yet with probably amazing results&hellip;.
	 * @param atFirstEndPoint whether to enforce smoothness at the first end-point
	 * @param atSecondEndPoint whether to enforce smoothness at the second end-point
	 */
	private void enforceSmoothnessOfStraightSegment(int segIndex, boolean atFirstEndPoint, boolean atSecondEndPoint){
		// Note : this private method uses "ptBuffer1/2".
		segIndex = getPBCSegmentIndex(segIndex);
		if (!isValidSegmentIndex(segIndex)) return ;

		int leftSubdiv = segmentToPointIndex(segIndex, SUBDIVISION_POINT);
		int rightSubdiv = getPBCBezierIndex(leftSubdiv+3);
		if (atFirstEndPoint){
			int altControl = getPBCBezierIndex(leftSubdiv-1);
			if (isValidBezierIndex(altControl)){
				double length = getBezierPt(leftSubdiv).distance(getBezierPt(altControl));
				// check if we must change sign of length if altControl belongs to the segment [leftSubdiv, leftSubdiv+3]
				// this is carried out by computing a dot product :
				PicVector vec1 = new PicVector(getBezierPt(leftSubdiv),getBezierPt(altControl));
				PicVector vec2 = new PicVector(getBezierPt(leftSubdiv),getBezierPt(leftSubdiv+3)).normalize();
				double dotProduct = vec1.dot(vec2);
				if (dotProduct < 0) length = -length;
				getBezierPt(altControl).setCoordinates(getBezierPt(leftSubdiv)).translate(vec2, length);
			}
		}
		if (atSecondEndPoint){
			int altControl = getPBCBezierIndex(leftSubdiv+4);
			if (isValidBezierIndex(altControl)){
				// see comment above
				double length = getBezierPt(rightSubdiv).distance(getBezierPt(altControl));
				PicVector vec1 = new PicVector(getBezierPt(rightSubdiv),getBezierPt(altControl));
				PicVector vec2 = new PicVector(getBezierPt(rightSubdiv),getBezierPt(leftSubdiv)).normalize();
				double dotProduct = vec1.dot(vec2);
				if (dotProduct < 0) length = -length;
				getBezierPt(altControl).setCoordinates(getBezierPt(rightSubdiv)).translate(vec2, length);
			}
		}
	}


	/**
	 * Adds a new subdivision point to the end of this curve. As far as control-points location goes, a
	 * <code>SMOOTHNESS_SYMMETRY</code> constraint is imposed (which as a nice side-effect results in the
	 * added segment automagically inheriting its "straightness" property from the last segment of the
	 * original curve).  <br> This implementation relies on {@link #lineTo lineTo()} and {@link #curveTo
	 * curveTo()} methods.
	 */
	public void addPoint(PicPoint pt) {

		if (!hasValidSize()) throw new RuntimeException("wrong nb of specification points [error]"); // [SR:pending] use assert keyword instead

		if (getSegmentCount() < 1){
			if (getBezierPtsCount()==0) {
				if (isClosed()){
					bezierPts.add(new PicPoint(pt));
					bezierPts.add(new PicPoint(pt));
					bezierPts.add(new PicPoint(pt)); // because nb bezierPts = 0,3,6,... so now we've got ONE segment
				}
				else
					bezierPts.add(new PicPoint(pt)); // because nb bezierPts = 0,1,4,...
			}
			else if (getBezierPtsCount()==1) { // this SHOULD be an open curve (otherwise there's a bug somewhere ;-)
				// assert !isClosed();
				lineTo(pt);
			}
			// else should never happen (pending : use "assert")
			fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
		}
		else {
			if (isClosed()) splitSegment(getSegmentCount() - 1, pt); // split last segment
			else {
				PicPoint C1,C2; // 1st and 2nd control points
				int p = getBezierPtsCount() - 1; // index of old end-point
				C1 = getBezierPt(p-1,null); // safely copy last control-point
				C1.symmetry(getBezierPt(p)); // C1 = symmetric of last control point wrt last subdivision point
			                     // note that this handles the case with straight segments as well ;-)

				// the following is too complicated and does not give nice results.
				// Take C2= so that the Bezier segment is symmetrical (if v1=is the control vector defined by C1,
				// and \vec PQ=vector betwen endpoints, we take control vector v2=symmetric of v1 wrt the line perpendicular to PQ
				// that is v2=v1-2*(v1.\vec PQ / ||PQ||^2 )\vec PQ (the dot is a dot product)
				// so C2=Q+(C1-P)-2*(v1.\vec PQ / ||PQ||^2 )\vec PQ
				// special case if P=Q
				//		double PQx=pt.x-bezierPtsX[p];
				//		double PQy=pt.y-bezierPtsY[p];
				//		double v1x=bezierPtsX[p-1]-bezierPtsX[p];
				//		double v1y=bezierPtsY[p-1]-bezierPtsY[p];
				//		double l=PQx*PQx+PQy*PQy;
				//		if (l!= 0){
				//		    C2.x=pt.x+v1x-2*(v1x*PQx+v1y*PQy)/l*PQx;
				//		    C2.y=pt.y+v1y-2*(v1x*PQx+v1y*PQy)/l*PQy;
				//			}
				//		else {
				//		    C2.x=C1.x;
				//		    C2.y=C1.y;
				//		};
				// this is MUCH simpler, and better: set 2nd control point = endpoint
				C2 = new PicPoint(pt);
				// now C1 and C2 are defined
				curveTo(C1, C2, pt);
			}
		}
	}

	/**
	 * If this curve if OPEN and NON-EMPTY,
	 * adds the given points (2 control points and an endpoint) to the end of the curve, then
	 * fire a GEOMETRY_CHANGE event.<br>
	 * If the curve is closed,  you should use <code>splitSegment</code> instead, since this method does nothing
	 * in this case ;-)
	 * @param ptCtrl1  first control point of the new Bezier segment
	 * @param ptCtrl2  second control point of the new Bezier segment
	 * @param ptEnd second end-point of the new Bezier segment
	 */
	public void curveTo(PicPoint ptCtrl1, PicPoint ptCtrl2, PicPoint ptEnd) {
		super.curveTo(ptCtrl1, ptCtrl2, ptEnd);
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}

	// [developpers] lineTo doesn't need overriding where firing DrawingEvent is concerned, since it calls curveTo anyway.


	/**
	 * Adds a new point to a (maybe curved) segment then fires a changed-update.
	 * Control-points are computed automagically.
	 * @param seg_idx  index of the segment to be split
	 * @param pt point at which segment is to be split
	 */
	public int splitSegment(int seg_idx, PicPoint pt) {
		if (seg_idx < 0 || seg_idx >= getSegmentCount()) throw new IndexOutOfBoundsException(new Integer(seg_idx).toString());

		PicPoint ptL,ptR; // these will be the control points to insert together with pt.
		if (isStraight(seg_idx)) {
			ptL = ptR = pt; // means new segments are straight as well.
		}
		else { // compute ptL and ptR so as to retain tangent at split point
			// this is so far the best idea i've had...
			CubicCurve2D.Double cubic = new CubicCurve2D.Double();
			cubic.x1 = getBezierPt(segmentToPointIndex(seg_idx,SUBDIVISION_POINT)).x;
			cubic.y1 = getBezierPt(segmentToPointIndex(seg_idx,SUBDIVISION_POINT)).y;
			cubic.ctrlx1 = getBezierPt(segmentToPointIndex(seg_idx,FIRST_SEGMENT_CONTROL_POINT)).x;
			cubic.ctrly1 = getBezierPt(segmentToPointIndex(seg_idx,FIRST_SEGMENT_CONTROL_POINT)).y;
			cubic.ctrlx2 = getBezierPt(segmentToPointIndex(seg_idx,SECOND_SEGMENT_CONTROL_POINT)).x;
			cubic.ctrly2 = getBezierPt(segmentToPointIndex(seg_idx,SECOND_SEGMENT_CONTROL_POINT)).y;
			cubic.x2 = getBezierPt(segmentToPointIndex(seg_idx+1,SUBDIVISION_POINT)).x;
			cubic.y2 = getBezierPt(segmentToPointIndex(seg_idx+1,SUBDIVISION_POINT)).y;
			PicVector tangent = PEToolKit.computeTangentToPath(cubic, pt, Double.POSITIVE_INFINITY);
			// tangent is oriented in ascending order of the "t" bezier-spline parameter, i.e. in ascending order of point indices

			// now set new control-points along this tangent, at a some arbitrary distance (ok, and this is where i've
			// got to improve the deal somehow ;-( The only cool thing is that SMOOTHNESS & SYMMETRY are enforced
			// [SR:pending] add EditPointConstraint's as argument to this method (as in setCtrlPt()) in order to adapt behaviour
			ptL = new PicPoint(pt);
			double dist1 = getBezierPt(segmentToPointIndex(seg_idx,SUBDIVISION_POINT)).distance(getBezierPt(segmentToPointIndex(seg_idx,FIRST_SEGMENT_CONTROL_POINT)));
			double dist2 = getBezierPt(segmentToPointIndex(seg_idx+1,SUBDIVISION_POINT)).distance(getBezierPt(segmentToPointIndex(seg_idx,SECOND_SEGMENT_CONTROL_POINT)));
			ptL.translate(tangent, -(dist1+dist2)/4.0); // best comprise is to take half the mean value ; feel free to improve if you can
			ptR = new PicPoint(pt);
			ptR.translate(tangent, (dist1+dist2)/4.0);
		}
		int idx = super.splitSegment(seg_idx, ptL, pt, ptR); // save index of new subdivision point
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
		return idx;
	}


	/**
	 * Removes a subdivision point from this curve, together with its two neighbouring control points.
	 * This shifts any ensuing points to the "left", and creates a new segment by concatenating
	 * dangling control-points.
	 * Nothing is done if this curve is reduced to a single point. <br>
	 * If both neighbouring segments
	 * had the same straightness value beforehands, the latter property
	 * is simply inherited by the new segment. Otherwise, we set it to false as this is the more flexible approach.<p>
	 * This method fires a DrawingEvent of type GEOMETRY_CHANGE..
	 * @param subdivIndex index of the subdivision point to be removed with respect to the SUBDIVISION point numbering scheme,
	 *        e.g. 0 for the first subdivision point (= first curve end-point).
	 */
	public void removeSubdivisionPoint(int subdivIndex) {
		if (getSegmentCount() <= 1) return; // we won't remove the only remaining segment
		super.removeSubdivisionPoint(subdivIndex);

		// [SR:pending] the following code snipet is probably useless now that straightness depends on control-point location...
		//           i'm leaving it until we make up our mind over it.
		//int pos = segmentToPointIndex(subdivIndex, SUBDIVISION_POINT);
		// pos-3 = index of subdivision-point of "new" segment
		//if (isValidBezierIndex(pos-3) && getPointType(pos-3)!=LAST_CURVE_END_POINT)
		//	enforceSmoothnessOfStraightSegment(pointToSegmentIndex(pos-3)); // possibly move alternate controls to enforce smoothness
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}


	/**
	 * Removes the point with the given index from this curve.
	 * If this is a subdivision point, we simply delegate to {@link #removeSubdivisionPoint removeSubdivisionPoint}.
	 * Else, this means removing a control point, and we just "straighten" the corresponding segment end-point
	 *  by moving the control-point to the location of the nearest subdivision-point (hence this has no effect on
	 * straight segments).
	 * <br>
	 * This method fires a DrawingEvent of type GEOMETRY_CHANGE.
	 * @param index any valid control- or subdivision- index to be removed
	 */
	public void removePoint(int index) {
		if (DEBUG) debug( "index="+index);
		PointType type = getPointType(index);
		if (type==INVALID_POINT_INDEX)
			throw new IndexOutOfBoundsException(new Integer(index).toString());
		if (type==SUBDIVISION_POINT || type==FIRST_CURVE_END_POINT || type==LAST_CURVE_END_POINT)
			removeSubdivisionPoint(pointToSegmentIndex(index)); // already fires drawing event, don't need to do it twice ;-)
		else {
			getBezierPt(index).setCoordinates(getBezierPt(getNearestSubdivisionPoint(index)));
			fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
		}
	}


	////////////////////////////
	//// OTHER FIELDS ACCESSORS
	////////////////////////////


	/**
	 * close or open this curve, that is, remove or add an end-point.<br>
	 * This methods fires a DrawingEvent of type GEOMETRY_CHANGE .
	 * @param state  The new close value
	 */
	public void setClosed(boolean state) {
		super.setClosed(state);
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}

	/**
	 * Forces the smoothness feature of the given subdivision point by moving alternate control-points.<br>
	 * If one neightbouring segment is straight, it imposes the location of the alternate control.<br>
	 * If both segments are straight, this method can't do anything unfortunately.<br>
	 * If both segments are curved, the resulting tangent is computed from the external bissector of both control vectors,
	 * yet retaining norm of control-vectors.<br>
	 * This method fires a drawing-event of type GEOMETRY_CHANGE.
	 * @param subdivIndex index of the subdivision point, e.g. 0,1,2... for the 1st,2nd,3rd,... subdivision point.
	 */
	public void setSmooth(int subdivIndex) {
		if (isSmooth(subdivIndex)) return; // also checks for index validity ;-)

		if (getPointType(subdivIndex*3)!=SUBDIVISION_POINT) return; // N/A for closed curve only!

		int thisSeg = subdivIndex;
		int prevSeg = getPBCSegmentIndex(thisSeg-1);

		if (isStraight(thisSeg) && isStraight(prevSeg)) return;

		if (isStraight(thisSeg)){ // move alternate control "subdivIndex-1" so as to enforce smoothness at first end-point
			enforceSmoothnessOfStraightSegment(thisSeg, true, false);
			fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
			return;
		}

		if (isStraight(prevSeg)){ // move alternate control "subdivIndex+1" so as to enforce smoothness at second end-point of "prevSeg"
			enforceSmoothnessOfStraightSegment(prevSeg, false, true);
			fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
			return;
		}

		// else both segment are curved :
		//PicVector v = new PicVector(getBezierPt(subdivIndex*3),getBezierPt(subdivIndex*3-1));
		//PicVector w = new PicVector(getBezierPt(subdivIndex*3),getBezierPt(subdivIndex*3+1));
		PicVector v = getIncomingTangent(subdivIndex); // non-normalized
		PicVector w = getOutgoingTangent(subdivIndex);

		PicVector biss = new PicVector(v); // don't modify v directly, cause we'll use it later on
		biss.inverse(); // get rid of dangling PI in final rotation angle
		double angle = biss.angle(w); // positive if (-v) -> w CCW !
		biss.rotate(angle/2.0); // now biss is the external bissector of (subdiv,subdiv+1) and (subdiv, subdiv-1),
		biss.normalize();       // it has a unit norm, and point "toward" w (ok, just sketch a drawing, and you'll get what i mean)
		// move both control point onto the line directed by biss,
		// retaining the norm of both control vectors : (see also enforceSmoothOf..., same trick used there, by the way)
		// a) "previous" control-point :
		double length = v.norm();
		getBezierPt(subdivIndex*3-1).setCoordinates(getBezierPt(subdivIndex*3)).translate(biss, -length);
		// b) "next" control-point :
		length = w.norm();
		getBezierPt(subdivIndex*3+1).setCoordinates(getBezierPt(subdivIndex*3)).translate(biss, length);
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}


	/**
	 * changes the smoothness feature of all the subdivision points at once.
	 * This method indirectly calls setSmooth(int), hence fires drawing-events of type GEOMETRY_CHANGE.
	 */
	public void setSmooth() {
		for (int i=0; i<getNumberOfSubdivisionPoints(); i++)
			setSmooth(i);
	}


	/**
	 * Force the SYMMETRY feature of the given subdivision point.
	 * This is straightforwardly carried out by setting the length of both control-vectors to their "mean value".
	 * This method does nothing if at least one neighbouring segment is straight.
	 * @param subdivIndex index of the subdivision point, e.g. 0,1,2... for the 1st,2nd,3rd,... subdivision point.
	 */
	public void setSymmetric(int subdivIndex) {
		if (isSymmetric(subdivIndex)) return;
		if (getPointType(subdivIndex*3)!=SUBDIVISION_POINT) return; // N/A for closed curve only!

		int thisSeg = subdivIndex;
		int prevSeg = getPBCSegmentIndex(thisSeg-1);

		if (isStraight(thisSeg) || isStraight(prevSeg)) return;

		// else both segment are curved :
		//PicVector v = new PicVector(getBezierPt(subdivIndex*3),getBezierPt(subdivIndex*3-1));
		//PicVector w = new PicVector(getBezierPt(subdivIndex*3),getBezierPt(subdivIndex*3+1));
		PicVector v = getIncomingTangent(subdivIndex); // non-normalized
		PicVector w = getOutgoingTangent(subdivIndex);
		double meanLength = (v.norm() + w.norm())/2.0;
		v.normalize();
		w.normalize();
		// a) "previous" control-point :
		getBezierPt(subdivIndex*3-1).setCoordinates(getBezierPt(subdivIndex*3)).translate(v, meanLength);
		// b) "next" control-point :
		getBezierPt(subdivIndex*3+1).setCoordinates(getBezierPt(subdivIndex*3)).translate(w, meanLength);
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}


	/**
	 * force the symmetry feature of all the subdivision points at once.
	 * This method indirectly calls setSymmetric(int), hence fires drawing-events of type GEOMETRY_CHANGE.
	 */
	public void setSymmetric() {
		for (int i=0; i<getNumberOfSubdivisionPoints(); i++) setSymmetric(i);
	}

	/**
	 * changes the straightness feature for the given segment by making its control- and end-points identical.
	 * We then force smoothness on each end-point depending on the "old" smoothness value, that is, before straightening
	 * this segment.
	 */
	public void setStraight(int segIndex) {
		if (isStraight(segIndex)) return;
		int leftIdx = segmentToPointIndex(segIndex, SUBDIVISION_POINT);
		int ctrl1Idx = segmentToPointIndex(segIndex, FIRST_SEGMENT_CONTROL_POINT);
		int ctrl2Idx = segmentToPointIndex(segIndex, SECOND_SEGMENT_CONTROL_POINT);
		int rightIdx = segmentToPointIndex(segIndex+1, SUBDIVISION_POINT);
		// store old smoothness value at both end-points :
		boolean smoothAtFirstEndPoint = isSmooth(segIndex);
		boolean smoothAtSecondEndPoint = isSmooth(getPBCSegmentIndex(segIndex));
		bezierPts.get(ctrl1Idx).setCoordinates(bezierPts.get(leftIdx));
		bezierPts.get(ctrl2Idx).setCoordinates(bezierPts.get(rightIdx));
		enforceSmoothnessOfStraightSegment(segIndex, smoothAtFirstEndPoint, smoothAtSecondEndPoint);
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}

	/**
	 * Straighten ALL segments (ie make this curve a polygon).
	 * This method indirectly calls setStraight(int), hence fires drawing-events of type GEOMETRY_CHANGE.
	 */
	public void setStraight() {
		for (int i=0; i<getSegmentCount(); i++) setStraight(i);
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
	public void translate(double dx, double dy) {
		super.translate(dx,dy);
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}

	/**
	 * Scale this object by (sx,sy) using (ptOrgX,ptOrgY) as the origin. This implementation
	 * simply apply a scaling transform to all specification-points.
	 * Note that <code>sx</code> and <code>sy</code> may be negative.
	 * This method eventually fires a changed-update event.
	 */
	public void scale(double ptOrgX, double ptOrgY, double sx, double sy) {
		super.scale(ptOrgX, ptOrgY, sx, sy);
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}

	/**
	 * Rotate this Element by the given angle along the given point
	 * @param angle rotation angle in radians
	 */
	public void rotate(PicPoint ptOrg, double angle) {
		super.rotate(ptOrg, angle);
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
		super.mirror(ptOrg, normalVector);
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}


	/**
	 * Shear this Element by the given params wrt to the given origin
	 */
	public void shear(PicPoint ptOrg, double shx, double shy) {
		super.shear(ptOrg, shx, shy);
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}

	////////////////////////////////////
	//// debug
	////////////////////////////////////

	/**
	 * Return a string for debugging purpose.
	 */
	public String toString() {
		String s = super.toString();
		s += "\n\tisSmooth=";
		for (int i = 0; i < getNumberOfSubdivisionPoints(); i++)
			s+= " " + isSmooth(i);
		s += "\n\tisSym=";
		for (int i = 0; i < getNumberOfSubdivisionPoints(); i++)
			s+= " " + isSymmetric(i);
		return s;
	}

	////////////////////////////////
	//// GUI
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
		if (getNumberOfSubdivisionPoints()==0) return actionArray;
		// we add some actions to make all subdivision points smooth or symmetric
		// meaningless actions are disabled (they will show up in grey colour)
		boolean existNonSmooth=false;  // will be true if isSmooth(i)==false for some i
		boolean existNonSymmetric=false; // will be true if isSymmetric(i)==false for some i
		boolean existNonStraight=false; // will be true if isStraight(i)==false for some i
		for (int i=0;i< getNumberOfSubdivisionPoints();i++) {
			if (!isSmooth(i)) existNonSmooth=true;
			if (!isSymmetric(i)) existNonSymmetric=true;
		}
		for (int i=0;i< getSegmentCount();i++) {
			if (!isStraight(i)) existNonStraight=true;
		}

		PEAction a;
		a = new SetAllSegmentsPropertyAction(actionDispatcher, SetAllSegmentsPropertyAction.SET_SMOOTH, localizer);
		a.setEnabled(existNonSmooth);
		actionArray.add(a);


		a = new SetAllSegmentsPropertyAction(actionDispatcher, SetAllSegmentsPropertyAction.SET_SYMMETRIC, localizer);
		a.setEnabled(existNonSymmetric);
		actionArray.add(a);

		a = new SetAllSegmentsPropertyAction(actionDispatcher, SetAllSegmentsPropertyAction.SET_STRAIGHT, localizer);
		a.setEnabled(existNonStraight);
		actionArray.add(a);

		if (hi instanceof HitInfo.Point){
			HitInfo.Point hip = (HitInfo.Point)hi;
			int ptIndex=-1;// point index, with priority given to subdivision points
			for (int j=0; j<hip.getNbHitPoints(); j++){ // look-up first subdiv-point in the set of clicked points
				if (!isControlPoint(hip.getIndex(j))){  // ok, we got one !
						ptIndex = hip.getIndex(j);
						break;
				}
			}
			if (ptIndex<0) ptIndex = hip.getIndex(); // if no subdiv point undex cursor
			//debug("ptIndex="+ptIndex);
			// compute nearest subdivision point:
			int subdivIdx = getNearestSubdivisionPoint(ptIndex)/3; // nearest subdiv point
			//debug("subdivIdx="+subdivIdx);
			a = new SetSegmentPropertyAction(actionDispatcher, SetSegmentPropertyAction.SET_SMOOTH, localizer, subdivIdx);
			a.setEnabled(!this.isSmooth(subdivIdx));
			actionArray.add(a);

			a = new SetSegmentPropertyAction(actionDispatcher, SetSegmentPropertyAction.SET_SYMMETRIC, localizer, subdivIdx);
			a.setEnabled(!this.isSymmetric(subdivIdx));
			actionArray.add(a);
		}

		else if (hi instanceof HitInfo.Stroke && !(hi instanceof HitInfo.HighlighterStroke)){
			HitInfo.Stroke his = (HitInfo.Stroke)hi;
			int segIdx = his.getClickedSegment();
			a = new SetSegmentPropertyAction(actionDispatcher, SetSegmentPropertyAction.SET_STRAIGHT, localizer, segIdx);
			a.setEnabled(!this.isStraight(segIdx));
			actionArray.add(a);
		}

		return actionArray ;
	}



	/**
	 * Force a given segment's properties, eg symmetry, smoothness or straightness.
	 * @author    Sylvain Reynal
	 */
	class SetSegmentPropertyAction extends PEAction {
		final static String SET_SMOOTH = "action.editorkit.SetSmooth";
		final static String SET_SYMMETRIC = "action.editorkit.SetSymmetric";
		final static String SET_STRAIGHT = "action.editorkit.SetStraight";
		String type;
		int index; // depending on type, either a segment or a subdivision point index

		/**
		 * @param type SET_SMOOTH, SET_STRAIGHT or SET_SYMMETRIC
		 */
		public SetSegmentPropertyAction(ActionDispatcher actionDispatcher, String type, ActionLocalizer localizer, int index) {
			super(actionDispatcher, type, localizer);
			this.type = type;
			this.index = index;
		}

		public void undoableActionPerformed(ActionEvent e) {
				if (type== SET_SYMMETRIC) setSymmetric(index);
				else if (type == SET_SMOOTH) setSmooth(index);
				else if (type == SET_STRAIGHT) setStraight(index);
		}
	}

	/**
	 * Force ALL segments' properties, eg symmetry, smoothness or straightness.
	 * @author    Vincent Guirardel
	 */
	class SetAllSegmentsPropertyAction extends PEAction {
		final static String SET_SMOOTH = "action.editorkit.SetSmoothAll";
		final static String SET_SYMMETRIC = "action.editorkit.SetSymmetricAll";
		final static String SET_STRAIGHT = "action.editorkit.SetStraightAll";
		String type;

		/**
		 * @param type SET_SMOOTH, SET_STRAIGHT or SET_SYMMETRIC
		 */
		public SetAllSegmentsPropertyAction(ActionDispatcher actionDispatcher, String type, ActionLocalizer localizer) {
			super(actionDispatcher, type, localizer);
			this.type = type;
		}

		public void undoableActionPerformed(ActionEvent e) {
				if (type== SET_SYMMETRIC) setSymmetric();
				else if (type == SET_SMOOTH) setSmooth();
				else if (type == SET_STRAIGHT) setStraight();
		}
	}

	/**
	 * Returns a Customizer for geometry editing
	 */
	public AbstractCustomizer createCustomizer(){
		if (cachedCustomizer == null)
			cachedCustomizer = new Customizer();
		cachedCustomizer.load();
		return cachedCustomizer;
	}

	private Customizer cachedCustomizer = null;


	/**
	 * geometry customizer
	 */
	class Customizer extends AbstractCurve.Customizer {

		private MultiCurveJTable table;


		/**
		 * Constructor for the Customizer object
		 */
		public Customizer() {

			Box p = new Box(BoxLayout.Y_AXIS);
			// polygon points:
			table = new MultiCurveJTable();
			table.setPreferredScrollableViewportSize(new Dimension(500, 400));

			// wrap scrollpane around:
			JScrollPane scrollPane = new JScrollPane(table);
			p.add(scrollPane);
			add(p, BorderLayout.NORTH);
			add(createPanel(), BorderLayout.CENTER);
			setPreferredSize(new Dimension(500,500));
		}

		/**
		 * update Element's properties
		 */
		public void store() {
			super.store();
			table.repaint();
		}

		// public void actionPerformed(ActionEvent e) => inherited

		/**
		 * @return   the panel title, used e.g. for Border or Tabpane title.
		 */
		public String getTitle() {
			return PicMultiCurve.this.getName();
		}
	}

	///////////////////////////////////////////////////
	//// INNER CLASSES for Customizer
	///////////////////////////////////////////////////

	/**
	 * an implementation of JTable for PicMultiCurve's. This makes use of the underlying model MultiCurveTableModel.
	 * @author guirardel
	 */
	class MultiCurveJTable extends JTable {

		private final CoordinateCellEditor coordCellEditor = new CoordinateCellEditor(new DecimalNumberField(0, 5));
		private final MultiCurveTableModel model;
		TableCellRenderer invisibleRenderer = new InvisibleRenderer();
		TableCellRenderer numberRenderer = new NumberRenderer();
		TableCellRenderer booleanRenderer = new BooleanRenderer();

		/**
		 * Constructor for the MultiCurveJTable object
		 */
		MultiCurveJTable() {
			super();
			setModel(model = new MultiCurveTableModel());
		}


		/**
		 * Returns an appropriate editor for the cell specified by row and column.
		 */
		public TableCellEditor getCellEditor(int row, int col) {
			if ((col > 0) && (col < 3))
				return coordCellEditor; // i.e. DecimalNumberField (=enhanced JTextField)
			else
				return super.getCellEditor(row, col); // JTable's native cell editor
		}


		// MultiCurveJTable inner class
		class InvisibleRenderer extends JLabel  implements TableCellRenderer {

			public InvisibleRenderer(){
				super();//empty JLabel
			}
			public Component getTableCellRendererComponent(JTable table, Object color,boolean isSelected, boolean hasFocus,int row, int column) {
				JLabel label=new JLabel();
				if ((row % 3) !=0) label.setBackground(Color.lightGray);//???ca marche pas, je sais pas pourquoi...
				else label.setBackground(Color.white);
				return label;// returns colored empty JLabel
			}
		}

		// MultiCurveJTable inner class
		class BooleanRenderer extends Component implements TableCellRenderer {

			public BooleanRenderer(){
				super();// empty component
			}
			public Component getTableCellRendererComponent(JTable table, Object color,boolean isSelected, boolean hasFocus,int row, int column) {
				JCheckBox comp=new JCheckBox();
				comp.setSelected(((Boolean)getValueAt(row,column)).booleanValue());
				if ((column ==0) || model.isCellEditable(row,column)){
					comp.setForeground(Color.black);
				}
				else {
					comp.setForeground(Color.lightGray);
				}
				if ((row % 3) ==0) comp.setBackground(Color.lightGray);
				else comp.setBackground(Color.white);

				return comp;
			}
		}

		// MultiCurveJTable inner class"
		class NumberRenderer extends  DefaultTableCellRenderer implements TableCellRenderer {

			public NumberRenderer(){
				super();// default renderer
			}
			public Component getTableCellRendererComponent(JTable table, Object color,boolean isSelected, boolean hasFocus,int row, int column) {
				Component comp=super.getTableCellRendererComponent(table,color,isSelected,hasFocus,row,column);
				if ((column ==0) || model.isCellEditable(row,column)){
					comp.setForeground(Color.black);
				}
				else {
					comp.setForeground(Color.lightGray);
				}
				if ((row % 3) ==0) comp.setBackground(Color.lightGray);
				else comp.setBackground(Color.white);

				return comp;
			}
		}

		public TableCellRenderer getCellRenderer(int row, int col) {
			// 6 columns : "pt", "X", "Y", smooth, symmetric, straight
			if (col == 5) { // straight
				if (model.getValueAt(row,col)==null) return invisibleRenderer; // if not a control-point
				else return booleanRenderer;
			}
			else if (col >= 3) {
				if (model.getValueAt(row,col)==null)  return invisibleRenderer;
				else return booleanRenderer;
			}
			//else
			return numberRenderer;
		}
	}


	/**
	 * MultiCurveJTable's coordinate cell editor is based on a DecimalNumberField
	 * @author    reynal
	 */
	class CoordinateCellEditor extends DefaultCellEditor {
		private DecimalNumberField dnf;

		/**
		 * construct a new cell editor from the given DecimalNumberField
		 */
		CoordinateCellEditor(DecimalNumberField dnf) {
			super(dnf);
			this.dnf = dnf;
		}

		/**
		 * Returns the value contained in the editor - overriden from
		 * DefaultCellEditor so as to return a Double instead of a String.
		 */
		public Object getCellEditorValue() {
			return new Double(dnf.getValue());
		}
	}

	/**
	 * a class that specifies the methods the JTable will use to interrogate a
	 * tabular data model.
	 * @author    reynal
	 */
	class MultiCurveTableModel extends AbstractTableModel {

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
				return localize("attributes.Smooth");
			case 4:
				return localize("attributes.Symmetric");
			case 5:
				return localize("attributes.Straight");
			default:
				return "";
			}
		}

		/**
		 * Returns the number of columns in the model.
		 */
		public int getColumnCount() {
			return 6;// 6 columns : "pt", "X", "Y", smooth, symmetric, straight
		}

		/**
		 * Returns the number of rows in the model.
		 */
		public int getRowCount() {
			return getBezierPtsCount();// there are as many rows as points in the associated PicMultiCurve
		}

		/**
		 * Returns true if the cell at rowIndex and columnIndex is editable.
		 * @param rowIndex this is equivalent to a bezier point index
		 */
		public boolean isCellEditable(int rowIndex, int colIndex) {
			if (colIndex == 0)
				return false;// first column is not editable since it containts point indexes
			else if (colIndex <= 2) // "X" and "Y"
				//return ( (rowIndex % 3 == 0)|| !isStraight(rowIndex /3) ); //can't edit control points when straight
				return true;
			else if (colIndex <= 4) // "smooth" "symmetric"
				return (getPointType(rowIndex) == SUBDIVISION_POINT); // smoothness and symmetry make sense only for subdivision points
			else
				return (getPointType(rowIndex) == FIRST_SEGMENT_CONTROL_POINT ); // "straightness" button will show up
			                               // beneath a control-point, so that it's clearly identified with
										   // a segment (putting it in front of a subdiv-point would certainly not
										   // make the deal.
		}

		/**
		 * Called when the associated JTable wants to know what to display at
		 * columnIndex and rowIndex. Depending on colIndex, we return, either the
		 * point index, the x-coord, or the y-coord of the corresponding curve point, and when applicable,
		 * smoothness, symmetry and straighness properties.
		 */
		public Object getValueAt(int rowIndex, int colIndex) {

			switch (colIndex) {
			case 0:
				return new Integer(rowIndex);// polygon point index
			case 1:
				return new Double(PEToolKit.doubleToString(getBezierPt(rowIndex).x));// x-coord for the row-th point
			case 2:
				return new Double(PEToolKit.doubleToString(getBezierPt(rowIndex).y));// y-coord for the row-th point
			case 3:
				if (getPointType(rowIndex)==SUBDIVISION_POINT)
					return new Boolean(isSmooth(rowIndex/3));// smoothness of the subdivision point
				else
					return null;
			case 4:
				if (getPointType(rowIndex)==SUBDIVISION_POINT)
					return new Boolean(isSymmetric(rowIndex/3));// symmetry of the subdivision point
				else
					return null;
			case 5:
				if (getPointType(rowIndex)==FIRST_SEGMENT_CONTROL_POINT)
					return new Boolean(isStraight((rowIndex-1)/3));// straightness of the segment
				else
					return null;
			default:
				return null;
			}
		}

		/**
		 * Returns the most specific superclass for all the cell values in the column.
		 * This is used by the JTable to set up a default renderer and editor for the column.
		 */
		public Class getColumnClass(int c) {
			switch (c) {
			case 0: return Integer.class;
			case 1: return Double.class;
			case 2: return Double.class;
			case 3: return Boolean.class;
			case 4: return Boolean.class;
			case 5: return Boolean.class;
			default: return null;
			}
		}


		/**
		 * Invoked by the UI (aka event-handler) when a user entered a new value in the cell at
		 * <code>colIndex</code> and <code>rowIndex</code>. We update <code>PicMultiCurve</code> geometry
		 * according to the cell value. This method does nearly the same thing as <code>actionPerformed</code>
		 * in other <code><var>xxxx</var>Customizer</code>'s.
		 *
		 * @param value     entered value
		 * @param rowIndex  equivalent to a bezier-point index
		 */
		public void setValueAt(Object value, int rowIndex, int colIndex) {
			switch (colIndex) {// do we modify x or y or what ?
			case 1:
				PicPoint pt = getCtrlPt(rowIndex, null);
				pt.x = ((Double)value).doubleValue();
				setCtrlPt(rowIndex, pt, BasicEditPointConstraint.SMOOTHNESS_SYMMETRY); // [SR:pending] change to AUTO as soon as it's supported by setCtrlPt
				break;
			case 2:
				pt = getCtrlPt(rowIndex, null);
				pt.y = ((Double)value).doubleValue();
				setCtrlPt(rowIndex, pt, BasicEditPointConstraint.SMOOTHNESS_SYMMETRY); // [SR:pending] change to AUTO as soon as it's supported by setCtrlPt
				break;
			case 3:
				if (rowIndex % 3==0)
					setSmooth(rowIndex/3);// smoothness of the subdivision point
				else
					return ;
				break;
			case 4:
				if (rowIndex % 3==0)
					setSymmetric(rowIndex/3);// symmetry of the subdivision point
				else
					return ;
				break;
			case 5:
				if (rowIndex % 3==1)
					setStraight((rowIndex-1)/3);// straightness of the segment
				else
					return ;
				break;
			default:
				return;
			}
			fireTableDataChanged();
		}
	}// inner class

	 /**
	  * Pour faire des tests de debug.
	  * @since jPicEdt 1.5.2
	  */
	public static void main(String[] args){
		PicMultiCurve curve = new PicMultiCurve();
		curve.addPoint(new PicPoint(20,0));
		curve.lineTo(new PicPoint());
		curve.lineTo(new PicPoint(0,10));
		curve.lineTo(new PicPoint(20,10));
		curve.lineTo(new PicPoint(16.67,5));
		curve.setClosed(true);
		curve.isPolygon();
	}

}
