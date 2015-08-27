// AbstractCurve.java --- -*- coding: iso-8859-1 -*-
// Decembre 25, 2002 - jPicEdt, a picture editor for LaTeX.
// Copyright 1999/2006 Sylvain Reynal
// Copyright 2007/2013 Sylvain Reynal, Vincent Belaïche
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
// Version: $Id: AbstractCurve.java,v 1.46 2013/03/27 07:20:51 vincentb1 Exp $
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

import jpicedt.graphic.PEToolKit;
import jpicedt.graphic.PicPoint;
import jpicedt.graphic.PicVector;
import jpicedt.graphic.event.DrawingEvent;
import jpicedt.graphic.toolkit.AbstractCustomizer;
import jpicedt.graphic.toolkit.ActionDispatcher;
import jpicedt.graphic.toolkit.ActionFactory;
import jpicedt.graphic.toolkit.ActionLocalizer;
import jpicedt.graphic.toolkit.ConvexZoneGroup;
import jpicedt.graphic.toolkit.PEAction;
import jpicedt.graphic.util.VecPolynomial;
import jpicedt.graphic.view.ArrowView;
import jpicedt.graphic.view.HitInfo;
import jpicedt.ui.dialog.UserConfirmationCache;
import jpicedt.util.math.Matrix;
import jpicedt.util.math.Polynomial;
import jpicedt.util.math.PolynomialRealRoot;
import jpicedt.widgets.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.EnumSet;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

import static jpicedt.Log.*;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;
import static java.lang.Math.abs;
import static java.lang.Math.sqrt;
import static java.lang.Math.acos;
import static java.lang.Math.atan;
import static java.lang.Math.PI;
import static java.lang.Math.max;
import static jpicedt.graphic.model.AbstractCurve.PointType.*;

/**
 * A curve made up of a concatenation of cubic bezier curves and straight lines,
 * hereafter globally denoted as "segments". Straight lines are actually just bezier curves
 * with control-points and their respective subdivision-points being at the same location.
 * This curve may be either closed or not, and has a
 * variable number of points. Segments may be added/inserted/deleted.<p>
 *
 * There are two sort of points in this curve:
 * <ul>
 *   <li> <b>subdivision</b> points, that is, endpoints of Bezier segments (indicated by SP(i), where
 *        i=segment_index, in figure above)</li>
 *   <li> <b>control-points</b> of these Bezier segments (indicated by CP_first(i) and CP_second(i) for the
 *        first and the second control-point respectively of the ith-segment, see figure above)</li>
 * </ul>
 * These are globally denoted as "Bezier points" and act as specification-points (cf bezierPts ArrayList)
 * <p>
 *
 * <h2>Specification (or Bezier) points</h2>
 * They are stored in a protected <code>bezierPts</code> ArrayList with the following indexing scheme :
 * <ul>
 * <li> subdivision-points are indexed 0,3,6,9,12,&hellip;
 * <li> control points are indexed 1,2 ; 4,5 ; 7,8 ; 10,11 ; &hellip;
 * </ul>
 * These indices are globally referred to as "Bezier indices" throughout the code documentation.
 * <p>
 * Arrays of specification-point coordinates are managed in a distinct way if the curve is open or close :
 * <ul>
 * <li>If the curve is <b>open</b>, the last element of the array is a subdivision point,
 *     namely the second curve's end-point.  To sum up, bezierPts[3i,3i+1,3i+2,3i+3] contains the
 *     1st endpoint, two control-points and the last endpoint of the corresponding
 *     (elementary) Bezier curve resp., for i=[0..number of segments-1].
 * <li>If the curve is <b>closed</b>, these arrays end up at the last control-point (e.g. 11 for a curve having 4
 *     subdivision-points), hence for the last segment, bezierPts[3i+3] is undefined.
 *     Yet in this case, getPBCBezierIndex() arranges for every index to be a valid index
 *     using periodic-boundary-conditions (PBC). This rule in turns applies to every method involving a point index.
 * </ul><p>
 *
 * <h2>Subdivision-point vs. segment numbering scheme</h2>
 * When used throughout the code documentation, the former expression
 * means that 0,1,2 &hellip; indices relate to the first, second, third,&hellip; subdivision point.
 * This is indicated by SP(i) on picture above.
 * Conversely, each segment is made up of three points (with the exception of the last curve's end-point if the curve is open, which
 * comprises a single segment), amongst which there is a single subdivision point.
 * Hence segment numbering ranges from 0 to getSegmentCount()-1 following the same numbering scheme as subdivision-points.
 * Methods pointToSegmentIndex() and
 * segmentToPointIndex() translates between segment and bezier-point numbering schemes.<p>
 *
 * <h2>Adding/removing points</h2>
 * Methods splitSegment(), removeSubdivisionPoint(), lineTo() and curveTo() provide means to add/remove points to this curve.
 * Abstract methods addPoint(),removePoint() and single-argument splitSegment(), whose implementation depends on the
 * geometric "meaning" of user-controlled points,  is left to subclassers.
 *
 * <h2>DrawingEvent's dispatching</h2>
 * As a rule of thumb, any concrete subclass should post DrawingEvent's itself,
 * where it thinks it has to, since none of the methods implemented here does it (this is aimed at reducing the burden
 * for registered listeners which otherwise might -now and often- receive events twice).
 *
 * <p><img src="doc-files/abstract_curve.png"></p>
 *
 * [SR:TODO] split curve into several pieces (aka old PicPolygon.convertToLines() method) ; <br>
 * [SR:TODO] join curves ;  <br>
 * [SR:TODO] add resample() method allowing to delete m point out of n (useful for polygons generated from
 *           data files)
 * [SR:BUG] setSmooth()/setSymmetric() &Rarr; bug when neighbouring control-points are identical.
 * @author    Vincent Guirardel, Sylvain Reynal.
 * @since     jpicedt 1.3.3
 * @version $Id: AbstractCurve.java,v 1.46 2013/03/27 07:20:51 vincentb1 Exp $
 */
public abstract class AbstractCurve extends AbstractElement implements ActionFactory {

	/** constant field for getPointType() */
	public static enum PointType {
		INVALID_POINT_INDEX, FIRST_CURVE_END_POINT, SUBDIVISION_POINT, FIRST_SEGMENT_CONTROL_POINT,
		SECOND_SEGMENT_CONTROL_POINT, LAST_CURVE_END_POINT}

	//////////////////////////
	//// PROTECTED FIELDS
	//////////////////////////

	/** list of specification points */
	protected ArrayList<PicPoint> bezierPts=new ArrayList<PicPoint>();

	/**
	 * tells whether this curve is closed of not
	 */
	protected boolean isClosed = false;


	//////////////////////////////
	/// CONSTRUCTORS
	//////////////////////////////

	/**
	 * Creates a new empty open Abstract curve
	 */
	public AbstractCurve() {
		this(false);
	}

	/**
	 * Creates a new empty Abstract curve
	 * @param closed whether the generated multi-curve will be closed or not
	 */
	public AbstractCurve(boolean closed) {
		super();
		isClosed = closed;
	}


	/**
	 * Creates a new empty Abstract curve with the given set of attributes
	 * @param closed  whether the generated multi-curve will be closed or not
	 * @param set     attribute set to be bound to this curve
	 */
	public AbstractCurve(boolean closed, PicAttributeSet set) {
		this(closed);
		this.attributeSet = new PicAttributeSet(set);
	}

	/**
	 * Creates a new Abstract curve and allocates as many points as needed by
	 * the given number of segments.
	 * Each segment comprises three Bezier points (i.e. one end-point, and
	 * two control-points), yet if the curve is open, there is one more Bezier point, namely the last
	 * curve end-point.
	 * @param nbSegments nb of elementary lines or cubic Bezier curves ; if 0, this curve is reduced to a single point
	 * @param closed whether this curve is closed or not
	 * @throws IllegalArgumentException if nbSegments is negative
	 */
	public AbstractCurve(int nbSegments, boolean closed) {
		this(closed);
		if (nbSegments<0)
			throw new IllegalArgumentException("nbSegments must be non-negative");
		else if (nbSegments>0){
			int nPts = 3*nbSegments;
			if (!closed) nPts += 1;
			for (int i=0; i<nPts; i++)
				bezierPts.add(new PicPoint());
		}
		else if (nbSegments==0){
			bezierPts.add(new PicPoint());
		}

	}


	/**
	 * Creates a new Abstract curve with the given number of segments, and attaches the given attribute set to it.
	 * @param nbSegments nb of elementary lines or cubic Bezier curves
	 * @param set     attribute set to be bound to this element
	 */
	public AbstractCurve(int nbSegments, boolean closed, PicAttributeSet set) {
		this(nbSegments, closed);
		this.attributeSet = new PicAttributeSet(set);
	}

	/**
	 * "cloning" constructor (to be used by clone())
	 */
	public AbstractCurve(AbstractCurve curve) {
		super(curve); // reminder : deep copy of attribute set
		this.isClosed = curve.isClosed;
		for (PicPoint pt: curve.bezierPts){
			this.bezierPts.add(pt.clone());
		}
	}


	///////////////////////////////////////////
	/// OPERATIONS on point and segment indices
	///////////////////////////////////////////

	/**
	 * Returns whether the given point index relates to a curve's end-point,
	 * a subdivision point, a control point (in which case
	 * this method returns the rank of the control point inside the segment to which it belongs), or (if the curve
	 * is open) is an invalid point index.<br>
	 * Note that if this curve is CLOSED, this method never returns FIRST_CURVE_END_POINT nor LAST_CURVE_END_POINT for that matter.
	 * <br><b>author:</b> Sylvain Reynal [18/01/2003]
	 * @return a constant field, i.e. either SUBDIVISION_POINT, FIRST_SEGMENT_CONTROL_POINT,
	 *         SECOND_SEGMENT_CONTROL_POINT, FIRST_CURVE_END_POINT, LAST_CURVE_END_POINT or INVALID_POINT_INDEX.
	 */
	public PointType getPointType(int index){
		index = getPBCBezierIndex(index);
		if (index<0 || index>=getBezierPtsCount())
			return INVALID_POINT_INDEX;
		else if (index==0  && (!isClosed()))
			return FIRST_CURVE_END_POINT;
		else if (index==getBezierPtsCount()-1 && (!isClosed()))
			return LAST_CURVE_END_POINT;
		else switch (index%3){
			case 0 :
				return SUBDIVISION_POINT;
			case 1 :
				return FIRST_SEGMENT_CONTROL_POINT;
			case 2 :
				return SECOND_SEGMENT_CONTROL_POINT;
			default :
				return INVALID_POINT_INDEX; // unreachable (unless JVM is buggy, huu huu)
			}
	}

	/**
	 * Convenience method for Periodic Boundary Condition (PBC) management when curve is CLOSED.<p>
	 * If curve is CLOSED, returns the given index modulo the number of bezier points, i.e. an index
	 * guaranteed to lie b/w 0 and the number of bezier points minus one.<br>
	 * Otherwise leaves unchanged.<p>
	 * @param bezierPtIdx any integer, positive or not, greater than the number of bezier points or not, etc...
	 * @return a bezier-point index guaranteed to lie between 0 and the number of bezier points minus one.
	 */
	public int getPBCBezierIndex(int bezierPtIdx) {
		if (isClosed()) {
			bezierPtIdx = bezierPtIdx % getBezierPtsCount(); // may be negative ...
			if (bezierPtIdx < 0) bezierPtIdx += getBezierPtsCount();
			return bezierPtIdx;
		}
		else return bezierPtIdx;
	}

	/**
	 * Convenience method for Periodic Boundary Condition (PBC) management when curve is CLOSED.<p>
	 * If curve is CLOSED, returns the given index modulo the number of segment, i.e. an index
	 * guaranteed to lie b/w 0 and the number of segments minus one.<br>
	 * Otherwise leaves unchanged.<p>
	 * @param segmentIdx any integer, positive or not, greater than the number of bezier points or not, etc...
	 * @return a segment index guaranteed to lie between 0 and the number of segments minus one.
	 */
	public int getPBCSegmentIndex(int segmentIdx) {
		return getPBCBezierIndex(segmentIdx*3)/3;
	}

	/**
	 * Returns the bezier-index of the nearest subdivision-point of the given control-point. The policy is to
	 * return the same index if the given point is NOT a control-point.
	 */
	public int getNearestSubdivisionPoint(int ctrlPtIdx){
		if (!isControlPoint(ctrlPtIdx)) return ctrlPtIdx;
		PointType type = getPointType(ctrlPtIdx);
		switch (type){
			case FIRST_SEGMENT_CONTROL_POINT:
				return getPBCBezierIndex(ctrlPtIdx-1);
			case SECOND_SEGMENT_CONTROL_POINT:
				return getPBCBezierIndex(ctrlPtIdx+1);
			default:
				return ctrlPtIdx;
		}
	}

	/**
	 * Returns the bezier-index of the alternate control-point of the given control-point. The policy is to
	 * return the same index if the given point is NOT a control-point. Also note that the returned index is
	 * not guaranteed to be a valid index (there's no convincing way to test it here).
	 */
	public int getAlternateControlPoint(int ctrlPtIdx){
		if (!isControlPoint(ctrlPtIdx)) return ctrlPtIdx;
		PointType type = getPointType(ctrlPtIdx);
		switch (type){
			case FIRST_SEGMENT_CONTROL_POINT:
				return getPBCBezierIndex(ctrlPtIdx-2);
			case SECOND_SEGMENT_CONTROL_POINT:
				return getPBCBezierIndex(ctrlPtIdx+2);
			default:
				return ctrlPtIdx;
		}
	}

	/**
	 * Return whether the bezier-point with the given index is a control-point.
	 */
	public boolean isControlPoint(int index){
		PointType type = getPointType(index);
		return type==FIRST_SEGMENT_CONTROL_POINT || type==SECOND_SEGMENT_CONTROL_POINT;
	}

	/**
	 * Returns the index of the segment the given point belongs to.
	 */
	public int pointToSegmentIndex(int pointIndex){
		return getPBCBezierIndex(pointIndex)/3;
	}

	/**
	 * Returns the index of the bezier-point belonging to the given segment, and having the given
	 * pointType.
	 * @param pointType one of SUBDIVISION_POINT, FIRST_SEGMENT_CONTROL_POINT or SECOND_SEGMENT_CONTROL_POINT.
	 *        Other qualifiers are meaningless here, and act as the SUBDIVISION_POINT qualifier.
	 */
	public int segmentToPointIndex(int segmentIndex, PointType pointType){
		segmentIndex = getPBCSegmentIndex(segmentIndex); // security
		switch (pointType){
			case FIRST_SEGMENT_CONTROL_POINT :
				return getPBCBezierIndex(segmentIndex * 3 + 1);
			case SECOND_SEGMENT_CONTROL_POINT :
				return getPBCBezierIndex(segmentIndex * 3 + 2);
			default :
				return getPBCBezierIndex(segmentIndex * 3);
		}

	}

	/**
	 * Returns true iff the given bezier point index is a valid point index.
	 * @see #getPointType(int)
	 */
	public boolean isValidBezierIndex(int pointIndex) {
		return getPointType(pointIndex)!=INVALID_POINT_INDEX;
	}

	/**
	 * Returns true iff the given segment index is a valid segment index. This is always true for closed curves.
	 */
	public boolean isValidSegmentIndex(int segIdx) {
		if (isClosed()) return true;
		return (segIdx >= 0) && (segIdx < getSegmentCount());
	}

	/**
	 * Return whether the current nb of specification points is valid, i.e. there is no incomplete segment.
	 * This may be used in conjunction with the "assert" keyword (as of JDK1.4) for debugging purpose.
	 */
	protected final boolean hasValidSize(){
		return bezierPts.isEmpty() || bezierPts.size()==1
		|| (isClosed() && (bezierPts.size())%3 == 0) || (!isClosed() && (bezierPts.size()-1)%3 == 0);
	}

	//////////////////////////////////
	/// OPERATIONS on Bezier POINTS
	//////////////////////////////////

	/**
	 * Return the number of specification points backing the geometry of this element.
	 */
	public int getBezierPtsCount() {
		return bezierPts.size();
	}

	/**
	 * Returns a reference on the Bezier point with the given index.
	 * If the curve is closed, any index
	 * is a valid index thx to the use of Periodic Boundary Conditions.
	 */
	protected PicPoint getBezierPt(int index){
		return bezierPts.get(getPBCBezierIndex(index));
	}

	/**
	 * If the curve is closed, any index
	 * is a valid index thx to the use of Periodic Boundary Conditions.
	 * @param index index of point of which a copy is to be returned.
	 * @param dest if null, it is instanciated on-the-fly and returned for
	 * convenience, otherwise used to store the returned point
	 * @return a copy of the Bezier point with the given index.
	 */
	public PicPoint getBezierPt(int index, PicPoint dest) {
		if (dest==null)
			dest = new PicPoint();
		dest.setCoordinates(getBezierPt(index));
		return dest;
	}

	/**
	 * Set the coordinates of the Bezier point with the given index
	 */
	protected void setBezierPt(int index, PicPoint pt){
		getBezierPt(index).setCoordinates(pt);
	}

	public double getBezierPtX(int index){
		return bezierPts.get(getPBCBezierIndex(index)).x;
	}

	public double getBezierPtY(int index){
		return bezierPts.get(getPBCBezierIndex(index)).y;
	}

	/**
	 * Set the coordinates of the Bezier point with the given index to the given location,
	 * but doesn't fire any DrawingEvent. If the curve is CLOSED, this methods arranges for any
	 * index to be a valid index by means of PBC's.
	 * @param c not used here
	 */
	public void setCtrlPt(int index, PicPoint pt, EditPointConstraint c) {
		if (!isValidBezierIndex(index))
			throw new IndexOutOfBoundsException(new Integer(index).toString());
		else
			bezierPts.get(getPBCBezierIndex(index)).setCoordinates(pt);
	}

	/**
	 * Adds the given point to the end of this curve.
	 */
	public abstract void addPoint(PicPoint pt);

	/**
	 * Split the segment having the given index.
	 * @return the index of the user-controlled point which got inserted, according to the indexing scheme of set/getCtrlPt().
	 *         This may for instance allow a receiver to control the "new" segment shape by calling setCtrlPt()
	 *         with this index as a parameter w/o the burden of computing an exact point index (a thing that may depend
	 *         on the particular implementation of this method).
	 */
	public abstract int splitSegment(int index, PicPoint pt);

	/**
	 * Remove the point with the given index from this curve
	 */
	public abstract void removePoint(int index);

	/**
	 * If this curve if OPEN and NON-EMPTY,
	 * adds the given points (2 control points and an endpoint) to the end of the curve.<br>
	 * If the curve is closed,  use <code>splitSegment</code> instead.
	 * @param ptCtrl1  first control point of the new Bezier segment
	 * @param ptCtrl2  second control point of the new Bezier segment
	 * @param ptEnd second end-point of the new Bezier segment
	 */
	public void curveTo(PicPoint ptCtrl1, PicPoint ptCtrl2, PicPoint ptEnd) {
		if (isClosed() || getBezierPtsCount()==0) return;
		bezierPts.add(new PicPoint(ptCtrl1)); // safe copy
		bezierPts.add(new PicPoint(ptCtrl2));
		bezierPts.add(new PicPoint(ptEnd));
		//isStraightSeg.add(Boolean.FALSE); // new segment is curved
	}


	/**
	 * Utilisable pour approcher une courbe paramétrée P(t) par la chaîne de
	 * courbe de Bézier this lorqu'on dispose des dérivées d'ordre 1 de
	 * P(t).<br>
	 * Si la courbe this n'est ni OPEN, ni non vide, alors l'appel à cette
	 * méthode est ignoré.<br>
	 * Sinon, en supposant que le dernier point de la courbe paramétrée ajouté
	 * à this est P(t0), ajoute une courbe de Bézier pour la section P(t0) à
	 * P(t1) en assurant que la courbe de Bézier ajouté est tangent à la
	 * courbe paramétrée P(t) en t=t0+ et t=t1-.<br>
	 * Pour approcher P(t) par N courbes de Bézier pour t sur l'intervalle [a b] on
	 * peut utiliser le code suivant:<br>
	 * <pre>
	 * <code>
	 * for(int i = 0; i &lt;= N;++i)
	 * {
	 *    t = (a*i + b*(N-i))/N;
	 *    p1   = P(t);
	 *    dP1  = dP/dt(t);
	 *    if(i == 0)
	 *       curve.addPoint(p1);
	 *    else
	 *       curve.diff1CurveTo(dP0,dP1,p1);
	 *    dP0 = dP1;
	 * }
	 * </code>
	 * </pre>
	 * @param dP0 valeur de dP(t)/dt en t=t0
	 * @param dP1 valeur de dP(t)/dt en t=t1
	 * @param p1 valeur de P(t) en t=t1
	 * @since jPicEdt 1.6
	 */
	public void diff1CurveTo(PicVector dP0,PicVector dP1,PicPoint p1){
		if (isClosed() || getBezierPtsCount()==0) return;
		PicPoint p0 = bezierPts.get(getBezierPtsCount()-1);
		PicVector dVec = new PicVector(p0,p1);
		double d = dVec.norm();
		/* 1/3 = cuisine. */
		double bOffs = d/3; /* bOffset: Bézier Offset, les second et troisième points
							   de contrôle sont :
							   p0 + bOffs*u0
							   p1 - bOffs*u1
							 */
		PicVector u0;
		if(dP0.isNull())
			u0 = (new PicVector(dVec)).normalize();
		else
			u0 = (new PicVector(dP0)).normalize();
		PicVector u1;
		if(dP1.isNull())
			u1 = (new PicVector(dVec)).normalize();
		else
			u1 = (new PicVector(dP1)).normalize();

		curveTo(p0.clone().translate(u0,bOffs),
				p1.clone().translate(u1,-bOffs),
				p1);

	}

	/**
	 * Classe utilisée à l'intérieur de la méthode diff2CurveTo. Ça sert à
	 * mémoriser les paramètres bOffs0 et bOffs1 tel que si on connaît les
	 * points p0 et p1 au extrémités d'une courbe élémentaire de Bézier et les
	 * vecteurs u0 et u1 unitaire tangents en ces point, alors les 4 points de
	 * contrôle de la courbe de Bézier considérée sont:</p>
	 *<p style="text-align:center">
	 *   <code>p0,  p0 + bOffs0 * u0, p1 - bOffs1 * u1, p1</code>
	 *</p>
	 *<p>
	 *  BOffsetPair signifie <i><b>B</b>ézier curve second and third control
	 *  points <b>Offset Pair</b></i>
	 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Belaïche</a>
	 * @since jPicEdt 1.6
	 */
	private class BOffsetPair{
		public double bOffs0;
		public double bOffs1;
		public BOffsetPair(double bOffs0,double bOffs1){
			this.bOffs0 = bOffs0;
			this.bOffs1 = bOffs1;
		}
		public String toString(){
			return "[" + PEToolKit.doubleToString(bOffs0)
				+ ", " + PEToolKit.doubleToString(bOffs1) + "]";
		}
	};


	/**
	 * Utilisable pour approcher une courbe paramétrée P(t) par la chaîne de
	 * courbe de Bézier this lorqu'on dispose des dérivées d'ordre 1 et 2 de
	 * P(t).<br>
	 * <b>Attention</b> cette méthode comprend un algorithme d'optimisation
	 * itératif qui la rend assez lente<br>
	 * Utilisable pour approcher une courbe paramétrée P(t) par la chaîne de
	 * courbe de Bézier this. Si la courbe this n'est ni OPEN, ni non vide,
	 * alors l'appel à cette méthode est ignoré.<br>
	 * Sinon, en supposant que le dernier point de la courbe paramétrée ajouté
	 * à this est P(t0), ajoute une courbe de Bézier pour la section P(t0) à
	 * P(t1) en assurant que la courbe de Bézier ajouté est tangente à la
	 * courbe paramétrée P(t) en t=t0+ et t=t1-, et en assurant que la courbe
	 * de Bézier ajoutée à sensiblement le même rayon de courbure que la
	 * coubre paramétrée P(t) en t=t0+ et t=t1-.<br>
	 * Pour approcher P(t) par N courbes de Bézier pour <var>t</var> sur l'intervalle [a b] on
	 * peut utiliser le code suivant:
	 * <code>
	 * for(int i = 0; i &lt;= N;++i)
	 * {
	 *    t = (a*i + b*(N-i))/N;
	 *    p1   = P(t);
	 *    dP1  = dP/dt(t);
	 *    d2P1 = d2P/d2t(t);
	 *    if(i == 0)
	 *       curve.addPoint(p1);
	 *    else
	 *       curve.diff2CurveTo(d2P0,d2P1,dP0,dP1,p1);
	 *    dP0  = dP1;
	 *    d2P0 = d2P1;
	 * }
	 * </code>
	 * @param d2P0 valeur de d2P(t)/d2t en t=t0+
	 * @param d2P1 valeur de d2P(t)/d2t en t=t1-
	 * @param dP0 valeur de dP(t)/dt en t=t0+
	 * @param dP1 valeur de dP(t)/dt en t=t1-
	 * @param p1 valeur de P(t) en t=t1
	 * @since jPicEdt 1.6
	 */
	public void diff2CurveTo(PicVector d2P0,PicVector d2P1,
							 PicVector dP0,PicVector dP1,PicPoint p1){
		if (isClosed() || getBezierPtsCount()==0) return;
		PicPoint p0 = bezierPts.get(getBezierPtsCount()-1);
		PicVector dVec = new PicVector(p0,p1);
		double d = dVec.norm();
		double smallBOffs = 0.001*d;
		PicVector u0;
		double a0den;
		double a1den;
		if(dP0.isNull())
		{
			u0     = (new PicVector(dVec)).normalize();
			a0den  = 1;
		}
		else
		{
			u0    = (new PicVector(dP0)).normalize();
			a0den = dP0.norm2();
		}
		PicVector u1;
		if(dP1.isNull())
		{
			u1 = (new PicVector(dVec)).normalize();
			a1den  = 1;
		}
		else
		{
			u1 = (new PicVector(dP1)).normalize();
			a1den  = dP1.norm2();
		}

		PicVector a0 = d2P0.cAdd(u0,-u0.dot(d2P0)).scale(9.0/a0den);
		PicVector a1 = d2P1.cAdd(u1,-u1.dot(d2P1)).scale(9.0/a1den);

		/* bOffset: Bézier Offset, les second et troisième points
		   de contrôle sont :

		   p0 + bOffs*u0
		   p1 - bOffs*u1
		*/
		double bOffs0 = d/3; // 1/3 = cuisine
		double bOffs1 = bOffs0;


		VecPolynomial q00Vec;
		{
			PicVector[] coeff = { a0};
			q00Vec = new VecPolynomial(2,2,coeff);
		}
		VecPolynomial q10Vec;
		{
			PicVector[] coeff = { a1};
			q10Vec = new VecPolynomial(2,2,coeff);
		}
		double u0u1 = u0.dot(u1);
		double u0d  = u0.dot(dVec);
		double u1d  = u1.dot(dVec);
		VecPolynomial q01Vec;
		{
			PicVector[] coeff = {
				u0.cMul(-u0d).add(dVec).scale(6),
				u0.cMul(u0u1).subtract(u1).scale(6)
			};
			q01Vec = new VecPolynomial(0,1,coeff);
		}
		VecPolynomial q11Vec;
		{
			PicVector[] coeff = {
				u1.cMul(u1d).subtract(dVec).scale(6),
				u0.cSub(u1.cMul(u0u1)).scale(6)
			};
			q11Vec = new VecPolynomial(0,1,coeff);
		}

		/* le but de la manipe c'est de trouver bOffs0 et bOffs1 tels que

		      q00Vec(bOffs0) = q01Vec(bOffs1)
		   et q10Vec(bOffs1) = q11Vec(bOffs0).

		   Ces deux équations assurant que les courbures sont les mêmes en
		   t = 0 et t=1. Cela se déduit de ce que les 4 points de contrôle de la
		   courbe de Bézier qui va être ajoutée sont

  		   p0
  		   p0 + bOffs0*u0
  		   p1 - bOffs1*u1
  		   p1

  		   Notez que la condition de tangence est déjà assurée par
  		   construction de u0 et u1.

		   la première chose qu'on fait est de projeter les deux équations
		   vectorielle sur une direction pour passer de 4 équations scalaire à
		   2 équations scalaire. Pour chacune des équations vectorielles, les
		   deux équations scalaire correspondante sont par construction liée,
		   donc on ne réduit pas le "rang" du système en faisant ça. Je mets
		   rang entre guillemets, parce que ce n'est pas un système linéaire */


		Polynomial q00;
		Polynomial q01;
		Polynomial q10;
		Polynomial q11;
		PicVector  c1 = q01Vec.coeff(1);
		if(abs(c1.getX()) > abs(c1.getY()))
		{
			q01 = q01Vec.dotXAxis();
			q00 = q00Vec.dotXAxis();
		}
		else
		{
			q01 = q01Vec.dotYAxis();
			q00 = q00Vec.dotYAxis();
		}
		c1 = q11Vec.coeff(1);
		if(abs(c1.getX()) > abs(c1.getY()))
		{
			q11 = q11Vec.dotXAxis();
			q10 = q10Vec.dotXAxis();
		}
		else
		{
			q11 = q11Vec.dotYAxis();
			q10 = q10Vec.dotYAxis();
		}

		boolean swapped;
		if(abs(q01.coeff(1)) > abs(q11.coeff(1)))
			swapped = false;
		else
		{
			Polynomial temp = q00;
			q00 = q10;
			q10 = temp;

			temp  = q01;
			q01   = q11;
			q11   = temp;

			swapped = true;
		}

		Polynomial boffs1Of0 = q00.cSub(q01.coeff(0)).mul(1/q01.coeff(1));
		Polynomial boffs1Eq  = q10.cCompose(boffs1Of0).sub(q11);

		ArrayList<PolynomialRealRoot> zeros =
			boffs1Eq.findZerosInInterval(Double.NEGATIVE_INFINITY,
										 Double.POSITIVE_INFINITY,0.001*d);

		ArrayList<BOffsetPair> bOffsPairs =
			new ArrayList<BOffsetPair>(zeros.size());
		for(PolynomialRealRoot root : zeros)
		{
			BOffsetPair pair = new BOffsetPair(
				root.getValue(),
				boffs1Of0.eval(root.getValue()));
			if(pair.bOffs0 >= 0 && pair.bOffs1 >= 0)
				bOffsPairs.add(pair);
		}

		if(bOffsPairs.size() > 0)
		{

			BOffsetPair bestBOffsPair = bOffsPairs.get(0);
			bOffs0 = bestBOffsPair.bOffs0;
			bOffs1 = bestBOffsPair.bOffs1;
		}
		else
		{
			bOffs1 = bOffs0 = d/3;
		}

		if(swapped)
		{
			double temp = bOffs0;
			bOffs0 = bOffs1;
			bOffs1 = temp;
		}
		/* */
		curveTo(p0.clone().translate(u0,bOffs0),
				p1.clone().translate(u1,-bOffs1),
				p1);

	}

	/** */
	public abstract class Segment{}
	/** */
	public class LineToSegment extends Segment{
		PicPoint from;
		PicPoint to;
		public PicPoint getFromPt(){ return from; }
		public PicPoint getToPt(){ return to; }
		public LineToSegment(PicPoint from, PicPoint to){
			this.from = from;
			this.to = to;
		}

	}
	/** */
	public class CurveToSegment extends Segment{
		PicPoint from;
		PicVector fromTangent;
		PicVector toTangent;
		PicPoint to;
		public PicPoint getFromPt(){ return from; }
		public PicPoint getToPt(){ return to; }
		public PicPoint getFromCtrlPt(){ return (new PicPoint(from)).translate(fromTangent); }
		public PicPoint getToCtrlPt(){ return (new PicPoint(to)).translate(toTangent,-1.0); }
		public PicVector getFromTangent(){ return fromTangent; }
		public PicVector getToTangent(){ return toTangent; }
		public CurveToSegment(PicPoint from, PicVector fromTangent, PicVector toTangent, PicPoint to){
			this.from = from;
			this.fromTangent = fromTangent;
			this.toTangent = toTangent;
			this.to = to;
		}
	}
	/** Dernier <code>Segment</code> rendu par {@link #getMiminalSegmentList() getMiminalSegmentList()}. Ne
	 * sert qu'à préciser que si la courbe est fermée ou non.
	 */
	public class EndSegment extends Segment{
		boolean isClosed;
		public EndSegment(boolean isClosed){ this.isClosed = isClosed; }
	}
	/** Cette fonction est destiné à faciliter le formattage du code LaTeX. Elle renvoie une liste de
		<code>Segment</code> non redondant. C'est à dire que les points de contrôle double n'apportant pas
		d'information sont supprimés.
		@return La liste de <code>Segment</code> non redondante qui forme la courbe.
	 */
	public ArrayList<Segment> getMiminalSegmentList(){
		ArrayList<Segment> ret = new ArrayList<Segment>();
		LineToSegment previousLT = null;
		CurveToSegment previousCT = null;
		PicPoint from = getBezierPt(0);
		PicVector prevDir = null;
		int lastI = getBezierPtsCount()-2;
		for(int i = 0;i<= lastI; i += 3)
		{
			PicPoint to = new PicPoint(getBezierPt(i+3));
			PicVector fromTangent = new PicVector(from,getBezierPt(i+1));
			PicVector toTangent = new PicVector(getBezierPt(i+2),to);
			PicVector dir = new PicVector(from, to);
			if(fromTangent.isNull() && toTangent.isNull())
			{
				boolean done = false;
				if(previousLT != null)
				{
					if(prevDir.isColinear(dir))
					{
						previousLT.to = to;
						done = true;
					}
				}
				if(!done && !from.equals(to))
				{
					ret.add(previousLT = new LineToSegment(from, to));
					done = true;
				}
				if(done)
					previousCT = null;
			}
			else
			{
				boolean done = false;
				ret.add(previousCT= new CurveToSegment(from, fromTangent, toTangent, to));
				done = true;
				if(done)
					previousLT  = null;

			}
			from = to;
			prevDir = dir;
		}
		ret.add(new EndSegment(isClosed()));
		return ret;
	}

	/**
	 * Adds a STRAIGHT segment to the end of this curve, i.e. control-points and subdivision-points are identical
	 * at each segment's end. This is a convenient call to {@link #curveTo curveTo}.
	 * <p>
	 * Note that this method does NOT fire any DrawingEvent.<br>
	 * This method does nothing if the curve is EMPTY or CLOSED.
	 * @param pt The second end-point of the line to be added
	 */
	public void lineTo(PicPoint pt) {
		if (isClosed() || getBezierPtsCount()==0) return; // do nothing if curve is either closed or empty.

		// int oldNbPts = getBezierPtsCount(); // length of original curve
		// PicVector v = new PicVector(getBezierPt(oldNbPts-1), pt);
		// set first bezier ctrl point to 1/3 :
		// PicPoint C1 = getBezierPt(oldNbPts-1,null); // safe copy
		// C1.translate(v,1/3.0);
		// set second bezier ctrl point to 2/3 :
		// PicPoint C2 = getBezierPt(oldNbPts-1,null);
		// C2.translate(v,2/3.0);

		PicPoint ctrl1 = getBezierPt(getBezierPtsCount()-1); // last curve end-point
		curveTo(ctrl1,pt,pt);

		//isStraightSeg.set(isStraightSeg.size()-1, Boolean.TRUE); // make correction against curveTo() behaviour
	}


	/**
	 * Split a segment (either straight or curved) at a given point using two additionnal control points given
	 * as parameters.<p>
	 * Implementation works as follow : parameters ptLeft and ptRight act as new control points,
	 * the original segment - labelled as [a,b,c,d] - being split up into two new segments, i.e.
	 * [a,b,ptleft,pt] and [pt,ptright,c,d]. These two segments inherit their
	 * straightness attribute from the original segment.
	 * This works for closed as well as open curves.<p>
	 * Note that this method does NOT fire any DrawingEvent.
	 *
	 * @param seg      index of segment to be split, i.e. 0,1,2,... for the 1st, 2nd, 3rd,... segment.
	 * @param ptleft   first new control-point
	 * @param pt       the point at which segment must be split (= new subdivision point)
	 * @param ptright  second new control-point
	 * @return the index of the subdivision-point which got inserted (see splitSegment(int,PicPoint) for details)
	 */
	public int splitSegment(int seg, PicPoint ptleft, PicPoint pt, PicPoint ptright) {

		if (seg < 0 || seg >= getSegmentCount()) throw new IndexOutOfBoundsException(new Integer(seg).toString());

		int oldNbPts = getBezierPtsCount(); //length of the old curve
		int oldNbSeg = getSegmentCount(); //old number of segments
		int ileft = segmentToPointIndex(seg, SECOND_SEGMENT_CONTROL_POINT); // index at which ptleft has to be inserted in bezierPtX

		// PicPoint arguments must be SAFELY copied, since we can't take for granted caller will never alter them afterwards
		bezierPts.add(ileft,new PicPoint(ptright)); // ArrayList.add(i,object) shifts any element at index >= i to the "right"
		bezierPts.add(ileft,new PicPoint(pt));      // hence we indeed end up with "ptleft ; pt ; ptright" in the right order ;-)
		bezierPts.add(ileft,new PicPoint(ptleft));
		if (DEBUG) debug( "seg="+seg+", ptL="+ptleft+", pt="+pt+", ptR="+ptright);
		return getPBCBezierIndex(ileft+1);
	}


	/**
	 * Remove a subdivision point from this curve, together with the two neighbouring control points.
	 * This shifts any ensuing points to the "left", i.e. reduces indices by one.<br>
	 * Nothing is done if this curve has only one point. If both neighbouring segments
	 * had the same straightness value, it is inherited by the new segment. Otherwise, we set it to false.<p>
	 * This method does NOT fire any DrawingEvent.
	 * @param subdivIndex index of the subdivision point to be removed with respect to SUBDIVISION point numbering scheme,
	 *        e.g. 0 for the first subdivision point (= first curve end-point), 1 for the second one (= bezier point
	 *        with index "3"), etc... This is similar to the SEGMENT numbering scheme incidentally.
	 * @throws an IllegalArgumentException if the given index is not a valid subdivision point index, i.e.
	 *         is greater than the nb of segments.
	 */
	public void removeSubdivisionPoint(int subdivIndex) {
		if (getSegmentCount() <= 1) return; // we won't remove the only remaining segment ! [meaning 1 subdivision point + 2 control points for a closed curve]

		int pos = segmentToPointIndex(subdivIndex, SUBDIVISION_POINT);
		PointType ptType = getPointType(pos);
		if (ptType == INVALID_POINT_INDEX)
			throw new IllegalArgumentException("Invalid subdivision-point index : " + new Integer(pos).toString());

		if (!isClosed()){
			if (ptType!=FIRST_CURVE_END_POINT && ptType!=LAST_CURVE_END_POINT) {
				bezierPts.remove(pos-1); // remove control-point, and shift subsequent points to the left
				bezierPts.remove(pos-1); // remove subdivision-point, and "                             "
				bezierPts.remove(pos-1); // remove other control-point
			}
			else if (ptType==FIRST_CURVE_END_POINT) {// remove first segment
				bezierPts.remove(0); // remove end-point
				bezierPts.remove(0); // remove first control-point
				bezierPts.remove(0); // remove second control-point
			}
			else if (ptType == LAST_CURVE_END_POINT) {// remove last segment
				bezierPts.remove(bezierPts.size()-1); // remove end-point
				bezierPts.remove(bezierPts.size()-1); // remove 2nd control-point
				bezierPts.remove(bezierPts.size()-1); // remove 2nd contorl-point
			}
		}
		else  {// remove first Bezier point (index 0) and its neigbouring points: index 1 and oldNbPts-1.
			if (pos==0){
				bezierPts.set(bezierPts.size()-1, bezierPts.get(2)); bezierPts.remove(2); // move 2nd control-point to end of point list
				bezierPts.remove(1); // remove 1st control point (index 1)
				bezierPts.remove(0); // remove end-point (index 0)
			}
			else {
				bezierPts.remove(pos-1);bezierPts.remove(pos-1);bezierPts.remove(pos-1);
			}
		}
	}

	/**
	 * Removes the last subdivision point. This is a convenience call to <code>removeSubdivisionPoint</code>.
	 */
	public void removeLastSubdivisionPoint() {
		removeSubdivisionPoint(getNumberOfSubdivisionPoints()-1);
	}

	////////////////////////////
	//// OTHER FIELDS ACCESSORS
	////////////////////////////

	/**
	 * Returns the number of segments that make up this curve.
	 */
	public int getSegmentCount() {
		// return isStraightSeg.size();
		if (isClosed()) return getBezierPtsCount()/3;
		else return (getBezierPtsCount()-1)/3;
	}

	/**
	 * Returns the number of subdivision points (including endpoints if the curve is open)
	 */
	public int getNumberOfSubdivisionPoints() {
		if (isClosed) return getSegmentCount();
		else if (getBezierPtsCount() == 0) return 0;// special case if curve is EMPTY
		else return getSegmentCount() + 1;
	}


	/**
	 * Return a non-normalized vector tangent to the incoming segment (i.e., wrt the control-point that comes before the given subdivision point)
	 * @return a non-normalized vector, or null if the given subdivision point has no incoming tangent because it's the first point of an open curve
	 */
	public PicVector getIncomingTangent(int subdivIndex){
		if (!isClosed() && subdivIndex ==0) return null;
		int ptIndex = segmentToPointIndex(subdivIndex,SUBDIVISION_POINT);
		if (!isValidBezierIndex(ptIndex)) throw new IllegalArgumentException("Invalid subdiv-point index : " + new Integer(subdivIndex).toString());
		int previousControlPointIndex = getPBCBezierIndex(ptIndex-1);
		// check if both control- and subdiv- points are at the same location :
		if (getBezierPt(ptIndex).equals(getBezierPt(previousControlPointIndex))){
			// in this case, tangent is given by the next-to-previous control-point:
			previousControlPointIndex = getPBCBezierIndex(ptIndex-2);
		}
		PicVector v = new PicVector(getBezierPt(ptIndex), getBezierPt(previousControlPointIndex));
		return v;
	}

	/**
	 * Return a non-normalized vector tangent to the outcoming segment (i.e., wrt the control-point that comes after the given subdivision point)
	 * @return a non-normalized vector, or null if the given subdivision point has no outcoming tangent because it's the last point of an open curve
	 */
	public PicVector getOutgoingTangent(int subdivIndex){
		if (!isClosed() && subdivIndex ==getNumberOfSubdivisionPoints()-1) return null;
		int ptIndex = segmentToPointIndex(subdivIndex,SUBDIVISION_POINT);
		if (!isValidBezierIndex(ptIndex)) throw new IllegalArgumentException("Invalid subdiv-point index : " + new Integer(subdivIndex).toString());
		int nextControlPointIndex = getPBCBezierIndex(ptIndex+1);
		// check if both control- and subdiv- points are at the same location :
		if (getBezierPt(ptIndex).equals(getBezierPt(nextControlPointIndex))){
			// in this case, tangent is given by the next-to-next control-point:
			nextControlPointIndex = getPBCBezierIndex(ptIndex+2);
		}
		PicVector v = new PicVector(getBezierPt(ptIndex), getBezierPt(nextControlPointIndex));
		return v;
	}

	/**
	 * Returns whether the given segment is straight. This is carried out by checking whether
	 * control-points and subdivision-points are identical at each end of the given segment.
	 * Return false if this segment is reduced to a point.
	 * @param segIndex segment index with respect to the segment numbering scheme, that is, 0 for the first segment, etc...
	 */
	public boolean isStraight(int segIndex) {
		PicPoint ptLeft = getBezierPt(segmentToPointIndex(segIndex, SUBDIVISION_POINT));
		PicPoint ptCtrl1 = getBezierPt(segmentToPointIndex(segIndex, FIRST_SEGMENT_CONTROL_POINT));
		PicPoint ptCtrl2 = getBezierPt(segmentToPointIndex(segIndex, SECOND_SEGMENT_CONTROL_POINT));
		PicPoint ptRight = getBezierPt(segmentToPointIndex(segIndex+1,SUBDIVISION_POINT));
		if (ptLeft.equals(ptRight)) return false;
		return (ptLeft.equals(ptCtrl1) && ptRight.equals(ptCtrl2));
	}

	/**
	 * Return true if this curve is a polygon, ie has only straight segments
	 */
	public boolean isPolygon(){
		for (int segIdx=0; segIdx < getSegmentCount(); segIdx++){
			if (!isStraight(segIdx)) return false;
		}
		return true;
	}

	/**
	 * Check whether the two control points around the given subdivision point satisfy (up to 5%)
	 * the 2nd order smoothness criterion. If this curve is open, and <code>subdivIndex</code> relates
	 * to a curve's end-point, return true. If one of the neighbouring segment is straight, end-points are
	 * used instead of control-points.
	 * @param subdivIndex subdivision-point index, with respect to subdivision-point (SP) numbering scheme, i.e.
	 *        0,1,2,... for the 1st, 2nd, 3rd... subdivision point.
	 */
	public boolean isSmooth(int subdivIndex) {
		// new code; takes care of special cases, e.g. control- and subdivision points at the location (=> quadratic curve)
		int ptIndex = segmentToPointIndex(subdivIndex,SUBDIVISION_POINT);
		if (!isValidBezierIndex(ptIndex)) throw new IllegalArgumentException("Invalid point index : " + new Integer(ptIndex).toString());

		// if curve is open and subdivIndex points to an end-point, THIS is smooth:
		if (!isValidBezierIndex(ptIndex - 1) || !isValidBezierIndex(ptIndex + 1)) return true;

		// normalized tangents:
		PicVector incomingTg = getIncomingTangent(subdivIndex);
		PicVector outgoingTg = getOutgoingTangent(subdivIndex);
		return abs(incomingTg.det(outgoingTg)) < (.05) * max(incomingTg.norm2(), outgoingTg.norm2());//Returns true if aligned at up to 5%
	}

	/**
	 * Check whether the control points around the given subdivision point satisfy (up to 5%) the
	 * symmetry criterion.
	 * @param subdivIndex subdivision-point index (with respect to subdivision-point numbering scheme), that is,
	 *        0,1,2,... for the 1st, 2nd, 3rd,... subdivision point.
	 */
	public boolean isSymmetric(int subdivIndex) { // was "checkSymmetry"
		int ptIndex = segmentToPointIndex(subdivIndex,SUBDIVISION_POINT);
		if (!isValidBezierIndex(ptIndex)) throw new IllegalArgumentException("Invalid point index : " + new Integer(ptIndex).toString());
		if (!isValidBezierIndex(ptIndex - 1) || !isValidBezierIndex(ptIndex + 1)) return true;

		int thisSeg = subdivIndex;
		int prevSeg = getPBCSegmentIndex(thisSeg-1);
		if (isStraight(thisSeg) || isStraight(prevSeg)) return true;

		// [SR: there was a bug as for sign ?] return abs(vx * vx + vy * vy - wx * wx + wy * wy) < (.05) * (vx * vx + vy * vy);//Returns true if symmetric at up to 5%
		//[old code; didn't care for special cases where both subdiv- and control- points were similar]
		//PicVector v = new PicVector(getBezierPt(ptIndex),getBezierPt(ptIndex-1));
		//PicVector w = new PicVector(getBezierPt(ptIndex),getBezierPt(ptIndex+1));
		PicVector v = getIncomingTangent(subdivIndex); // non-normalized
		PicVector w = getOutgoingTangent(subdivIndex);
		return abs(v.norm2()-w.norm2()) < .05 * v.norm2();//Returns true if length are equal up to 5%
	}


	/**
	 * close or open this curve, either by opening the last segment if curve is closed,
	 * or by adding two control-points after the last segment if it is open.<br>
	 * This methods doesn't fire any DrawingEvent.
	 * @param state  The new closeness value
	 * @see #isClosed()
	 */
	public void setClosed(boolean state) {
		if (isClosed() == state) return; // no change
		else if (getBezierPtsCount() <= 1) {
			isClosed = state;
		}
		else { // now there's a least one segment (3*i bezierPts if closed, 3*i+1 if open)
			isClosed = state;

			// close curve -> the policy is to leave the existing points as is, and to add
			// two control-points after the last curve end-point :
			if (state){
				PicPoint pCtrl1 = getBezierPt(bezierPts.size()-2,null); // second control-point of last segment (clone)
				bezierPts.add(pCtrl1.symmetry(getBezierPt(bezierPts.size()-1))); // sym. wrt curve last end-point
				PicPoint pCtrl2 = getBezierPt(1,null); // first control-point of first segment (clone)
				bezierPts.add(pCtrl2.symmetry(getBezierPt(0))); // sym. wrt curve first end-point
			}

			// open the curve -> simply remove the last two control-points
			else {
				bezierPts.remove(bezierPts.size()-1);
				bezierPts.remove(bezierPts.size()-1);
				//bezierPts.add(getBezierPt(getBezierPtsCount()-1).clone());
			}
		}
	}


	/**
	 * Returns true if this curve is closed.
	 * @see #setClosed(boolean)
	 */
	public boolean isClosed() {
		return isClosed;
	}

	/**
	 * @see jpicedt.graphic.model#Element.getCtrlPtSubset(ConvexZoneGroup csg,BitSet czExtension)
	 * @since jPicEdt 1.6
	 */
	public CtrlPtSubset getCtrlPtSubset(ConvexZoneGroup csg,BitSet czExtension){
		int ctrlPtCount = getBezierPtsCount();
		boolean[] isInCZCtrlPoint = new boolean[ctrlPtCount];
		int       inCzCtrlPointCount = 0;

		for(int i = 0;i < ctrlPtCount; ++i){
			if(isInCZCtrlPoint[i] = csg.containsPoint(bezierPts.get(i)))
				++inCzCtrlPointCount;
		}

		if(inCzCtrlPointCount == 0) return null;

		if(czExtension != null && czExtension.get(CtrlPtSubset.CZExtension.ABSTRACT_CURVE_TANGENTS.value())){
			for(int i = 0; i < ctrlPtCount; i+=3){
				if(isInCZCtrlPoint[i]){
					for(int j = -1; j <= 1; j+=2){
						int k = getPBCBezierIndex(i+j);
						if(!isInCZCtrlPoint[k]){
							isInCZCtrlPoint[k] = true;
							++inCzCtrlPointCount;
						}
					}
				}
			}
		}
		if(inCzCtrlPointCount == ctrlPtCount)
			return new CtrlPtSubsetPlain(this);

		int[] toBeTranslated = new int[inCzCtrlPointCount];
		int j = 0;
		for(int i = 0;i < ctrlPtCount; ++i){
			if(isInCZCtrlPoint[i])
				toBeTranslated[j++] = i;
		}
		return new CtrlPtSubsetAbstractCurve(this,toBeTranslated);
	}

	public class CtrlPtSubsetAbstractCurve implements CtrlPtSubset{
		AbstractCurve abstractCurve;
		int[]         toBeTranslated;
		public CtrlPtSubsetAbstractCurve(AbstractCurve abstractCurve,int[] toBeTranslated){
			this.abstractCurve  = abstractCurve ;
			this.toBeTranslated = toBeTranslated;
		}

		public void translate(double dx, double dy){
			for(int index : toBeTranslated){
				abstractCurve.getBezierPt(index).translate(dx,dy);
			}
			abstractCurve.fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
		}
	}



	//////////////////////////////
	/// View
	//////////////////////////////

	private GeneralPath path;

	/**
	 * Create a Shape for the geometry of this model.
	 */
	public Shape createShape(){
		boolean hasSegments = (getSegmentCount()>=1); // check if curve has at least one segment
		if (!hasSegments ) {
			return null; // curve is reduced to a point -> don't paint !
		}

		if (path==null)
			path=new GeneralPath(); // avoid over-creating objects !
		// assume Npts > 2 (ie a "true" curve) since curve has a least one segment
		// update shape
		path.reset();
		path.moveTo((float)getBezierPtX(0), (float)getBezierPtY(0)); // set first point
		for (int i=0; i<getSegmentCount(); i=i+1){
			if (isStraight(i)){
				path.lineTo((float)getBezierPtX(3*i+3), (float)getBezierPtY(3*i+3));
			}
			else {
				path.curveTo((float)getBezierPtX(3*i+1), (float)getBezierPtY(3*i+1),
				             (float)getBezierPtX(3*i+2), (float)getBezierPtY(3*i+2),
				             (float)getBezierPtX(3*i+3), (float)getBezierPtY(3*i+3));
			}
		}
		// either close path
		if (isClosed())
			path.closePath();

		return path;
	}

	/**
	 * Helper for the associated View. This implementation updates the geometry of
	 * the given ArrowView only if isArc()==true.
	 */
	public void syncArrowGeometry(ArrowView v, ArrowView.Direction d){
		if (isClosed()) return;
		if(getBezierPtsCount() <= 1) return;
		PicPoint loc;
		PicVector dir;
		switch (d){
		case LEFT:
			loc=getBezierPt(0,null);
			if (isStraight(0))
				dir = new PicVector(getBezierPt(3,null),loc); // use arrow1Dir as buffer
			else
				dir = new PicVector(getBezierPt(1,null),loc);
			dir.normalize();
			v.updateShape(loc,dir);
			break;
		case RIGHT:
			loc=getBezierPt(getBezierPtsCount()-1,null);
			if (isStraight(getSegmentCount()-1)) // if last seg is straight...
				dir = new PicVector(getBezierPt(getBezierPtsCount()-4,null),loc); // use arrow2Dir as buffer
			else
				dir = new PicVector(getBezierPt(getBezierPtsCount()-2,null),loc);
			dir.normalize();
			v.updateShape(loc,dir);
		}
	}


	/**
	 * Returns the the smallest rectangle enclosing the shape of this curve, as
	 * opposed to {@link #getBoundingBox(Rectangle2D r) getBoundingBox}
	 * which is defined as the smallest rectangle encompassing ALL
	 * Bezier points.
	 * @param r if null, gets allocated and returned for convenience
	 * @see java.awt.Shape#getBounds2D()
	 */
	public Rectangle2D getShapeBounds2D(Rectangle2D r){ // was : getGeometricBoundingBox
		if (r==null) r = new Rectangle2D.Double();

		if (getBezierPtsCount() == 0) {
			r.setFrameFromDiagonal(0,0,0,0);
			return r;
		}
		if (getSegmentCount()==0) {
			r.setFrameFromDiagonal(getBezierPt(0),getBezierPt(0));
			return r;
		}
		double minX,maxX,minY,maxY;
		double[] minMaxX, minMaxY ;
		// curved segment :
		if (! isStraight(0))
			r = getSegmentShapeBounds2D(getBezierPt(0),getBezierPt(1),getBezierPt(2),getBezierPt(3),r);
		//straight segment :
		else r.setFrameFromDiagonal(getBezierPt(0),getBezierPt(3));

		Rectangle2D rectBuffer = new Rectangle2D.Double();
		for (int i=1; i< getSegmentCount(); i++){
			//curved segment :
			if (! isStraight(i))
				rectBuffer = getSegmentShapeBounds2D(getBezierPt(3*i),getBezierPt(3*i+1),getBezierPt(3*i+2),getBezierPt(3*i+3),rectBuffer);
			// straight segment :
			else rectBuffer.setFrameFromDiagonal(getBezierPt(3*i),getBezierPt(3*i+3));
			r.add(rectBuffer);
		}
		return r;
	}

	/**
	 * Computes a rectangle that bounds the PATH of a single bezier segment. This comes in strong contrast with
	 * {@link java.awt.geom.GeneralPath.getBounds2D() java.awt.geom.GeneralPath.getBounds2D()},
	 * where the smallest rectangle including ALL end- and control-points gets returned, no matter
	 * where the curve's path actually snakes around.
	 * @param a start-point coordinate
	 * @param b first control-point coordinate
	 * @param c second control-point coordinate
	 * @param d end-point coordinate
	 * @param dest if null, gets allocated and returned for convenience
	 * @return the shape's bounding-box of the given segment wrapped in a Rectangle2D
	 */
	private Rectangle2D getSegmentShapeBounds2D(PicPoint a, PicPoint b, PicPoint c, PicPoint d, Rectangle2D dest){
		if (dest==null) dest = new Rectangle2D.Double();
		double[] minMaxX = getSegmentShapeBounds2D(a.x, b.x, c.x, d.x);
		double[] minMaxY = getSegmentShapeBounds2D(a.y, b.y, c.y, d.y);
		dest.setFrameFromDiagonal(minMaxX[0], minMaxY[0], minMaxX[1], minMaxY[1]);
		return dest;
	}


	/**
	 * Depending on input coordinates being given on the X- or Y- axis,
	 * computes the X- or Y-coordinates of the
	 * rectangle that bounds the PATH of a single bezier segment. This comes in strong contrast with
	 * {@link java.awt.geom.GeneralPath.getBounds2D() java.awt.geom.GeneralPath.getBounds2D()},
	 * where the smallest rectangle including ALL end- and control-points gets returned, no matter
	 * where the curve's path actually snakes around.
	 * @param a start-point coordinate, along X- or Y- axis.
	 * @param b first control-point coordinate, along X- or Y- axis.
	 * @param c second control-point coordinate, along X- or Y- axis.
	 * @param d end-point coordinate, along X- or Y- axis.
	 * @return min and max coordinate of the shape's bounding-box along the axis corresponding to the input coordinates axis.
	 *         These are stored in an array of double, min given first.
	 */
	private double[] getSegmentShapeBounds2D(double a,double b,double c,double d){
		double min,max;
		// the bezier function is p(t)= a*(1-t)^3+3*b*t*(1-t)^2+3*c*t^2*(1-t)+d*t^3
		// the derivative of the bezier coordinate is A*t^2+2*B*t+C (up to a factor 3)
		if(a<d){min=a; max=d; } else {min=d; max=a; }
		double A=-a+3*b-3*c+d;
		double B=a-2*b+c;
		double C=-a+b;
		double t,p;
		if ( A != 0 ) { //degree is really 2
			double delta=B*B-A*C;
			if (delta>=0) {
				delta=sqrt(delta);
				t=(-B-delta)/A; // t=critical point
				if ( t>0 && t<1 ) {
					p=a*(1-t)*(1-t)*(1-t) + 3*b*(1-t)*(1-t)*t + 3*c*(1-t)*t*t + d*t*t*t ;
					// p= corresponding point (coordinate) on the bezier curve
					if (p<min) { min=p; }
					else if (p>max) { max=p; }
				}
				t=(-B+delta)/A;
				if ( t>0 && t<1 ) {
					p=a*(1-t)*(1-t)*(1-t) + 3*b*(1-t)*(1-t)*t + 3*c*(1-t)*t*t + d*t*t*t ;
					if (p<min) { min=p; }
					else if (p>max) { max=p; }
				}
			}
		}//degree 2
		else if (B != 0) { //degree is really 1
			t=-C/(2*B);
			if ( t>0 && t<1 ) {
				p=a*(1-t)*(1-t)*(1-t) + 3*b*(1-t)*(1-t)*t + 3*c*(1-t)*t*t + d*t*t*t ;
				if (p<min) { min=p; }
				else if (p>max) { max=p; }
			}
		}// degree 1
		//if degree 0, nothing to do
		double[] minMax = new double[2];
		minMax[0]=min;
		minMax[1]=max;
		return minMax;
	}

	/**
	 * Returns a string for debugging purpose.
	 */
	public String toString() {
		String s = super.toString();
		s += "\n\t";
		int i=0;
		for (PicPoint pt: bezierPts)
			s += (i++) + ":"+pt.toString()+";";
		s += "\n\tnbSegs=" + getSegmentCount();
		s += "\n\tisStraightSeg=";
		for (i = 0; i < getSegmentCount(); i++) s+= " " + isStraight(i);
		s += (isClosed ? " closed" : " open");
		return s;
	}

	//////////////////////////////////////////////////////////
	//// TRANSFORMS
	//////////////////////////////////////////////////////////

	/**
	 * Translate this Element by (dx,dy) ; this implementation translates the bezier points.
	 * but does not fire any change event.
	 * @param dx The X coordinate of translation vector
	 * @param dy The Y coordinate of translation vector
	 * @since PicEdt 1.0
	 */
	public void translate(double dx, double dy) {
		for (PicPoint pt: bezierPts)
			pt.translate(dx,dy);
		//fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE); // done in subclass
	}

	/**
	 * Scale this object by (sx,sy) using (ptOrgX,ptOrgY) as the origin. This implementation
	 * simply apply a scaling transform to all specification-points.
	 * Note that <code>sx</code> and <code>sy</code> may be negative.
	 * This method does not fire any change event.
	 */
	public void scale(double ptOrgX, double ptOrgY, double sx, double sy,UserConfirmationCache ucc) {
		for (PicPoint pt: bezierPts)
			pt.scale(ptOrgX,ptOrgY,sx,sy);
		//fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}

	/**
	 * Rotate this Element by the given angle along the given point
	 * This method does not fire any change event.
	 * @param angle rotation angle in radians
	 */
	public void rotate(PicPoint ptOrg, double angle) {
		for (PicPoint pt: bezierPts)
			pt.rotate(ptOrg,angle);
		//fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}


	/**
	 * Effectue une réflexion sur <code>this</code> relativement à l'axe
	 * défini par <code>ptOrg</code> et <code>normalVector</code>.
	 *
	 * @param ptOrg le <code>PicPoint</code> par lequel passe l'axe de réflexion.
	 * @param normalVector le <code>PicVector</code> normal à l'axe de réflexion.
	 */
	public void mirror(PicPoint ptOrg, PicVector normalVector){
		for (PicPoint pt: bezierPts)
			pt.mirror(ptOrg,normalVector);
	}

	/**
	 * Shear this <code>Element</code> by the given params wrt to the given origin
	 * This method does not fire any change event.
	 */
	public void shear(PicPoint ptOrg, double shx, double shy, UserConfirmationCache ucc) {
		for (PicPoint pt: bezierPts)
			pt.shear(ptOrg, shx, shy);
		//fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}

	//////////////////////////////////////////////////////////
	/// BOUNDING BOX
	//////////////////////////////////////////////////////////

	/**
	 * Returns the bounding box (i.e. the surrounding rectangle) in double precision
	 * Used e.g. to determine the arguments of a \\begin{picture} command.<p>
	 * This implementation compute the bb from the smallest rectangle that encompasses
	 * all specification-points.
	 * @since PicEdt 1.0
	 */
	public Rectangle2D getBoundingBox(Rectangle2D r) {
		if (r==null)
			r = new Rectangle2D.Double();
		if (bezierPts.isEmpty()) {
			r.setFrameFromDiagonal(0,0,0,0);
			return r;
		}
		r.setFrameFromDiagonal(bezierPts.get(0), bezierPts.get(0)); // init bb from 1st spec-point
		for (PicPoint pt: bezierPts)
			r.add(pt); // then enlarge bb by adding subsequent points
		return r;
	}

	////////////////////////////////
	//// Action's
	////////////////////////////////

	/**
	 * Creates an array of Action's related to this object
	 *
	 * @param actionDispatcher  dispatches events to the proper PECanvas
	 * @param localizer         i18n localizer for PEAction's
	 */
	public ArrayList<PEAction> createActions(ActionDispatcher actionDispatcher, ActionLocalizer localizer, HitInfo hi) {

		int N = getBezierPtsCount();
		ArrayList<PEAction> actionArray = super.createActions(actionDispatcher, localizer, hi);
		if (actionArray==null)
			actionArray = new ArrayList<PEAction>();

		// [reynal:17/01/2003] test if getBezierPtsCount() is a good indicator of "closeability"
		if (N <= 2) {
			return actionArray;
		}
		else {// N>2
			if (isClosed())
				actionArray.add(new CloseCurveAction(actionDispatcher, CloseCurveAction.OPEN, localizer));
			else
				actionArray.add(new CloseCurveAction(actionDispatcher, CloseCurveAction.CLOSE, localizer));
			return actionArray;
		}
	}

	/**
	 * Closes the path of this curve
	 * <br><b>author:</b>    Sylvain Reynal
	 * @since     jpicedt 1.3.3
	 */
	class CloseCurveAction extends PEAction {
		final static String CLOSE = "action.editorkit.CloseCurve";
		final static String OPEN = "action.editorkit.OpenCurve";
		private String type;

		/**
		 * @param type CLOSE or OPEN
		 */
		public CloseCurveAction(ActionDispatcher actionDispatcher, String type, ActionLocalizer localizer) {
			super(actionDispatcher, type, localizer);
			this.type = type;
		}

		public void undoableActionPerformed(ActionEvent e) {
			setClosed(type==CLOSE);
		}
	}

	////////////////////////////////
	//// GUI
	////////////////////////////////

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
	class Customizer extends AbstractCustomizer implements ActionListener {

		private JCheckBox isClosedCB;
		private boolean isListenersAdded = false;// flag set to true after listener have been registered

		/**
		 * Initializes the GUI.
		 */
		protected Box createPanel(){
			Box p = new Box(BoxLayout.Y_AXIS);
			p.add(isClosedCB = new JCheckBox(jpicedt.Localizer.currentLocalizer().get("action.editorkit.CloseCurve")));
			return p;
		}

		/**
		 * add action listeners to widgets to reflect changes immediately
		 */
		private void addActionListeners() {
			if (isListenersAdded)
				return;// already done
			isClosedCB.addActionListener(this);
			isListenersAdded=true;
		}

		/**
		 * add action listeners to widgets to reflect changes immediately
		 */
		private void removeActionListeners() {
			if (!isListenersAdded)
				return;// already done
			isClosedCB.removeActionListener(this);
			isListenersAdded=false;
		}

		/**
		 * load widgets with object's properties
		 */
		public void load() {
			removeActionListeners();
			isClosedCB.setSelected(isClosed());
			// add listeners AFTERWARDS ! otherwise loading widgets initial value has a painful side-effet...
			// since it call "store" before everything has been loaded
			addActionListeners();// done the first time load is called

		}

		/**
		 * update Element's properties
		 */
		public void store() {
			setClosed(isClosedCB.isSelected());
		}

		public void actionPerformed(ActionEvent e) {
			store();
		}
	}

}
