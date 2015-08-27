// PicParallelogram.java --- -*- coding: iso-8859-1 -*-
// March 2003 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2006 Sylvain Reynal
// Copyright (C) 2007/2013 Sylvain Reynal, Vincent Belaïche
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
// Version: $Id: PicParallelogram.java,v 1.54 2013/10/07 19:16:33 vincentb1 Exp $
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

import java.awt.Shape;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.geom.GeneralPath;
import java.awt.geom.RectangularShape;
import java.awt.geom.Rectangle2D;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

import jpicedt.graphic.PECanvas;
import jpicedt.graphic.PEToolKit;
import jpicedt.graphic.PicPoint;
import jpicedt.graphic.PicVector;
import jpicedt.graphic.event.DrawingEvent;
import jpicedt.graphic.toolkit.*;
import jpicedt.graphic.view.HitInfo;
import jpicedt.graphic.view.View;
import jpicedt.ui.dialog.UserConfirmationCache;
import jpicedt.util.math.MathConstants;
import jpicedt.widgets.*;

import static jpicedt.Log.*;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static jpicedt.graphic.model.EditPointConstraint.EditConstraint.*;

/**
 * A class implementing a parallelogram. Geometry is fully specified by three corner points
 * (namely, the bottom-left, bottom-right and top-right corners).
 * [SR:TODO] add rounded-corners feature.
 * @author Vincent Guirardel, Sylvain Reynal.
 * @since jPicEdt 1.4
 * @version $Id: PicParallelogram.java,v 1.54 2013/10/07 19:16:33 vincentb1 Exp $
 */
public class PicParallelogram extends AbstractElement implements CustomizerFactory, ActionFactory,
													  PicMultiCurveConvertable {


	private static final boolean DEBUG = false; // jpicedt.Log.DEBUG;

	////////////////////////////
	//// PUBLIC CONSTANT FIELDS
	////////////////////////////

	/**
	 * An enum containing allowed control point indices to be fed to setCtrlPt() and getCtrlPt(); the first three
	 * points are also specification points, while other are controlled points only [underway] */
	//public static enum ControlPointIndex { P_BL, P_BR, P_TR, P_TL, SIDE_B, SIDE_R, SIDE_T, SIDE_L, P_CENTER };

	// [pending:SR] there's just one pb with the following naming strategy : BR and TR may as well be upside down
	//              after a call to setCtrlPt() (see similar remark in PicGroup). Either advise developper "publicly" here,
	//              or change names to, e.g. X1Y1, ...
	/** Bottom-Left corner (also first specification point) */
	public static final int P_BL = 0;
	/** Bottom-Right corner (also second specification point) */
	public static final int P_BR = 1;
	/** Top-Right corner  (also third specification point) */
	public static final int P_TR = 2;
	/** Top-Left corner */
	public static final int P_TL = 3;

	/** Numéro du premier côté indéxé */
	protected static final int SIDE_0 = 4;

	/** Bottom side mid-point */
	public static final int SIDE_B = SIDE_0 + 0;
	/** Right side mid-point */
	public static final int SIDE_R = SIDE_0 + 1;
	/** Top side mid-point */
	public static final int SIDE_T = SIDE_0 + 2;
	/** Left side mid-pint */
	public static final int SIDE_L = SIDE_0 + 3;
	/** Parallelogram's centre */
	public static final int P_CENTER = 8;
	/** Index of first control-point */
	public static final int FIRST_PT = 0;
	/** Index of last control-point */
	public static final int LAST_PT = 8;

	protected static final int IN_SIDE_B = 1<<(SIDE_B-SIDE_0);
	protected static final int IN_SIDE_R = 1<<(SIDE_R-SIDE_0);
	protected static final int IN_SIDE_T = 1<<(SIDE_T-SIDE_0);
	protected static final int IN_SIDE_L = 1<<(SIDE_L-SIDE_0);
	protected static final int[] CTRL_PT_SIDE_BITMAP = {
		IN_SIDE_B|IN_SIDE_L, //P_BL
		IN_SIDE_B|IN_SIDE_R, //P_BR
		IN_SIDE_T|IN_SIDE_R,// P_TR
		IN_SIDE_T|IN_SIDE_L,// P_TL
		IN_SIDE_B,// SIDE_B
		IN_SIDE_R,// SIDE_R
		IN_SIDE_T,// SIDE_T
		IN_SIDE_L,// SIDE_L
		0// P_CENTER
	};

	/** Bottom-Left corner (specification point) */
	protected PicPoint ptBL = new PicPoint();
	/** Bottom-Right corner (specification point) */
	protected PicPoint ptBR = new PicPoint();
	/** Top-Right corner (specification point) */
	protected PicPoint ptTR = new PicPoint();

	/**
	 * "l2rVec" is a vector pointing from P_BL to P_BR (or equally, from P_TL to P_TR)
	 * "b2tVec" is a vector pointing from P_BR to P_TR (or equally, from P_BL to P_TL)
	 * These two vectors simply define a convenient parallelogram's basis, and
	 * are updated by a call to {@link #updateParalleloBasis()}.
	 * They are used e.g. when moving a corner-point.<p>
	 * An essential function of these vectors is to "retain" the initial parallelogram basis even
	 * if the element becomes flat at some point during an edit operation.
	 */
	protected PicVector l2rVec = new PicVector(1,0);
	/** see documentation for {@link #l2rVec l2rVec} */
	protected PicVector b2tVec = new PicVector(0,1);

	//////////////////////////////
	/// PRIVATE VARIABLES
	//////////////////////////////

	private GeneralPath path; // for the View

	//////////////////////////////
	/// CONSTRUCTORS
	//////////////////////////////

	/**
	 * Creates a new PicParallelogram reduced to (0,0)
	 * @since jpicedt 1.3.3
	 */
	public PicParallelogram(){
		super();
		l2rVec.setCoordinates(1,0);
		b2tVec.setCoordinates(0,1);
	}

	/**
	 * Creates a new PicParallelogram reduced to (0,0), with the given attribute set
	 * @since jpicedt 1.3.3
	 */
	public PicParallelogram(PicAttributeSet set){
		super(set);
		l2rVec.setCoordinates(1,0);
		b2tVec.setCoordinates(0,1);
	}

	/**
	 * Create a new PicParallelogram object using the 3 given points as 3 consecutive
	 * specification points of the parallelogram (namely BL, BR and TR)
	 * and a default attribute set.
	 * @since jpicedt 1.4
	 */
	public PicParallelogram(PicPoint ptBL, PicPoint ptBR, PicPoint ptTR){
		super();
		this.ptBL.setCoordinates(ptBL);
		this.ptBR.setCoordinates(ptBR);
		this.ptTR.setCoordinates(ptTR);
		updateParalleloBasis();
	}

	/**
	 * Create a new PicParallelogram object using the 3 given points as 3 consecutive points of the parallelogram.
	 * @since jpicedt 1.3.3
	 */
	public PicParallelogram(PicPoint ptBL, PicPoint ptBR, PicPoint ptTR, PicAttributeSet set){
		super(set);
		this.ptBL.setCoordinates(ptBL);
		this.ptBR.setCoordinates(ptBR);
		this.ptTR.setCoordinates(ptTR);
		updateParalleloBasis();
	}

	/**
	 * Create a new rectangle with axes parallel to X- and Y-axes, using the 2 given points to build the
	 * diagonal, and a default attribute set.
	 * @since jpicedt 1.4
	 */
	public PicParallelogram(PicPoint ptBL, PicPoint ptTR){
		this(ptBL, new PicPoint(ptTR.x, ptBL.y), ptTR);
	}

	/**
	 * Create a new PicParallelogram reduced to a point located at the given position
	 * @since jpicedt 1.3.3
	 */
	public PicParallelogram(PicPoint pt, PicAttributeSet set) {
		this(pt,pt,pt,set);
		l2rVec.setCoordinates(1,0); // security
		b2tVec.setCoordinates(0,1);
	}

	/**
	 * "cloning" constructor (to be used by clone())
	 * @param src The PicParallelogram object to clone
	 * @since jpicedt 1.3.3
	 */
	public PicParallelogram(PicParallelogram src){
		super(src); // clone attribute set
		this.ptBL.setCoordinates(src.ptBL);
		this.ptBR.setCoordinates(src.ptBR);
		this.ptTR.setCoordinates(src.ptTR);
		updateParalleloBasis();
	}

	/**
	 * Create a new rectangular PicParallelogram object using the given Rectangle2D.
	 * @since jpicedt 1.5
	 */
	public PicParallelogram(RectangularShape rect){
		this(new PicPoint(rect.getMinX(), rect.getMinY()), new PicPoint(rect.getMaxX(), rect.getMaxY()));
	}

	/**
	 * Overrides Object.clone() method
	 * @since jpicedt 1.3.3
	 */
	public PicParallelogram clone(){
		return new PicParallelogram(this);
	}

	/**
	 * @return a localised string that represents this object's name
	 * @since jpicedt 1.3.3
	 */
	public String getDefaultName(){
		return jpicedt.Localizer.currentLocalizer().get("model.Parallelogram");
	}

	//////////////////////////////////
	/// OPERATIONS ON CONTROL POINTS
	//////////////////////////////////

	/** Same as getCtrlPt(int, PicPoint), yet explicitely enforces the use of one
	 * of the predefined enum constants. [pending Aug' 06]
	 */
	//public PicPoint getCtrlPt(ControlPointIndex numPoint, PicPoint src){ return getCtrlPt(numPoint.ordinal(), src); }

	/**
	 * Return the <b>user-controlled point</b> having the given index. The general contract in
	 * <code>Element</code> is to return an IMMUTABLE instance of PicPoint, so that the only way to alter the
	 * geometry of this element is by calling the <code>setCtrlPt</code> method.<br>
	 * @return the point indexed by <code>numPoint</code> ;
	 *         if <code>src</code> is null, allocates a new PicPoint and return it,
	 *         otherwise directly modifies <code>src</code> and returns it as well for convenience.
	 * @param numPoint the point index, should be greater or equal to the value returned by
	 *        <code>getFirstPointIndex</code>, and lower or equal to <code>getLastPointIndex</code>.
	 */
	public PicPoint getCtrlPt(int numPoint, PicPoint src){
		if (src==null)
			src = new PicPoint();

		switch (numPoint){
			// spec-points:
		case P_BL:
			return src.setCoordinates(ptBL);
		case P_BR:
			return src.setCoordinates(ptBR);
		case P_TR:
			return src.setCoordinates(ptTR);
			// control-points:
		case P_TL:
			return src.setCoordinates(ptBL).translate(ptBR,ptTR);
		case SIDE_B:
			return src.setCoordinates(ptBR).middle(ptBL);
		case SIDE_R:
			return src.setCoordinates(ptBR).middle(ptTR);
		case SIDE_T:
			return getCtrlPt(SIDE_B,src).translate(ptBR,ptTR);
		case SIDE_L:
			return getCtrlPt(SIDE_R,src).translate(ptBR,ptBL);
		case P_CENTER:
			return src.setCoordinates(ptBL).middle(ptTR);
		default:
			throw new IndexOutOfBoundsException(new Integer(numPoint).toString());
		}
	}

	/**
	 * Return the index of the first point that can be retrieved by getPoint
	 */
	public int getFirstPointIndex(){
		return FIRST_PT;
	}

	/**
	 * Return the index of the last point that can be retrieved by getPoint
	 */
	public int getLastPointIndex(){
		return LAST_PT;
	}

	/**
	 * Compute the <code>l2r</code> and <code>b2t</code> vectors of the parallelogram basis from the location
	 * of the three specification-points of this parallelogram.
	 * @since jpicedt 1.3.3
	 */
	protected void updateParalleloBasis(){ // was "computeVectors"
		l2rVec.setCoordinates(ptBL,ptBR); // left-to-right
		b2tVec.setCoordinates(ptBR,ptTR); // bottom-to-top

		// check if l2r and b2t are parallels or null (in which case our parallelogram is flat either way),
		// or if parallelogram is reduced to a single-point :
		if (l2rVec.det(b2tVec) == 0){
			if (!l2rVec.isNull()){ // if l2r is a non-null vector, use it to generate basis
				b2tVec.setCoordinates(l2rVec);
				b2tVec.iMul();
			}
			else if (!b2tVec.isNull()){ // else this is just the other way around ;-)
				l2rVec.setCoordinates(b2tVec);
				l2rVec.miMul();
			}
			else { // our parallelogram is reduced to a single point => make it a rectangle parallel to cartesian axes next time setCtrlPt() is called
				l2rVec.setCoordinates(1,0);
				b2tVec.setCoordinates(0,1);
			}
			if (l2rVec.det(b2tVec) == 0){  //should almost never happen (merely in the event there is a very small but non-zero vector...)
				l2rVec.setCoordinates(1,0);
				b2tVec.setCoordinates(0,1);
			}
		}

		if (DEBUG) debug("l2rVec="+l2rVec+", b2tVec="+b2tVec);
	}

	/**
	 * Compute the contravariant coordinate of the given point "ptSrc" along the "l2r" and "b2t" vectors of
	 * the parallelogram's basis, the origin of the basis being set at "ptOrg"
	 * This simply reduces to a contravariant change of coordinates from the cartesian basis to the
	 * (non-orthonormalized) parallelogram basis.<br>
	 * To wind up : denoting OM = vec(ptOrg &rarr; ptSrc), (e1,e2) the cartesian basis (ie std
	 * model-coordinates),
	 * and (x',y') the new coords in the (l2r,b2t) basis, yields : <br>
	 * <b>OM</b> = x <b>e1</b> + y <b>e2</b> = x' <b>l2r</b> + y' <b>b2t</b>.
	 * <br>
	 * Change of coordinates is carried out by using elementary geometry. This yields :<br>
	 * <ul>
	 * <li> x' = (OM &times; b2t).e3/||l2r &times; b2t||
	 * <li> y' = -(OM &times; l2r).e3/||l2r &times; b2t||
	 * </ul>
	 * where e3 denotes the z-axis unit vector of the (x,y,z) direct diedra.
	 * <p>
	 * Note that {@link #updateParalleloBasis updateParalleloBasis} must be called beforehands for this method to return
	 * a valid results [SR:pending] need to be cleaned up.
	 * @param ptOrg origin of parallelogram's basis
	 * @param ptSrc source point whose coordinates have to be converted
	 * @param ptDest if null, gets allocated and returned for convenience ; besides, it is perfectly safe to call
	 *        this method with ptDest and one of ptOrg or ptSrc referencing the same PicPoint.
	 * @return the new coordinates (x',y') along (l2r,b2t), encapsulated in a PicVector
	 * @since jpicedt 1.3.3
	 */
	protected PicVector toParalleloBasisCoordinates(PicPoint ptOrg, PicPoint ptSrc, PicVector ptDest){
		if (ptDest==null)
			ptDest = new PicVector();
		double det = l2rVec.det(b2tVec); // updateParalleloBasis guarantees det != 0
		ptDest.setCoordinates(ptOrg, ptSrc); // OM vector
		double x = ptDest.det(b2tVec)/det;
		double y = -ptDest.det(l2rVec)/det;
		ptDest.setCoordinates(x, y);
		if (DEBUG)
			debug( "l2rVec="+l2rVec+", b2tVec="+b2tVec+"\n\tl2rCoord="+ptDest.x+", b2tCoord="+ptDest.y);
		return ptDest;
	}


	/**
	 * Set the coordinate of the control-point indexed by "numPoint" to the given value.
	 * The control-policy for the default (null or DEFAULT) constraint is as follows :
	 * <ul>
	 * <li> when moving a corner, the opposite CORNER is kept fixed (ie segments experience a parallel
	 *      transport)
	 * <li> when moving a SIDE_X point, the opposite SEGMENT is kept fixed, so that this operation allows one
	 *      to change the parallelogram skewness.
	 * </ul>
	 * <p>
	 * The SQUARE constraint imposes that the parallelogram be a square. The square diagonal is computed from
	 * the current control-point and its opposite corner. Other spec' points are set accordingly.
	 * <p>
	 * The CENTER_FIXED constraint imposes that the parallelogram center be kept fixed. Spec' points are set
	 * accordingly.
	 * <p>
	 * @param numPoint one of P_TL, P_TR, P_BL, P_BR, SIDE_T, SIDE_B, SIDE_L, SIDE_R or P_CENTER.
	 * @param constraint either null or one of PicParallelogram.EditConstraint
	 * @since jpicedt 1.3.3
	 */
	public void setCtrlPt(int numPoint, PicPoint pt, EditPointConstraint constraint){
		if (DEBUG) {
			debug("numPoint="+numPoint+",pt="+pt);
			debugAppendLn("l2rVec="+l2rVec+", b2tVec="+b2tVec);
		}
		if (constraint == null){
			setCtrlPtDefaultConstraint(numPoint,pt);
			fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
			return;
		}

		switch (constraint.getEditConstraint()){
		case DEFAULT: setCtrlPtDefaultConstraint(numPoint,pt); break;
		case SQUARE: setCtrlPtSquareConstraint(numPoint, pt); break;
		case CENTER_FIXED: setCtrlPtCenterFixedConstraint(numPoint, pt); break;
		default: setCtrlPtDefaultConstraint(numPoint,pt); break;
		}
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}

	/**
	 * Convenience call to setCtrlPt(int, PicPoint, EditPointConstraint) with a null constraint.
	 */
	public void setCtrlPt(int index, PicPoint pt) {
		setCtrlPt(index,pt,null);
	}

	/**
 	 * Helper for setCtrlPt() invoked with the DEFAULT constraint Remark: when moving SIDE_XX points, vectors
	 * defining the parallelogram basis change their direction, so that it's mandatory to call
	 * updateParalleloBasis() afterwards.
	 */
	private void setCtrlPtDefaultConstraint(int numPoint, PicPoint pt){
		//if (numPoint <0) setSpecPt(-numPoint-1,pt); return;
		//double tX,tY; //translation ...
		// the following code keeps the center fixed, while moving the corner
		//  		if (numPoint==P_BL) {
		//  		    ptsX[P_BL]=pt.x;ptsY[P_BL]=pt.y;
		//  		    ptsX[P_TR]=2*cX-pt.x;ptsY[P_TR]=2*cY-pt.y;
		//  		}
		//  		else if (numPoint==P_BR){
		//  		    ptsX[P_BR]=pt.x;ptsY[P_BR]=pt.y;
		//  		}
		//  		else if (numPoint==P_TR){
		//  		    ptsX[P_TR]=pt.x;ptsY[P_TR]=pt.y;
		//  		    ptsX[P_BL]=2*cX-pt.x;ptsY[P_BL]=2*cY-pt.y;
		//  		}
		//  		else if (numPoint==P_TL){
		//  		    ptsX[P_BR]=2*cX-pt.x;ptsY[P_BR]=2*cY-pt.y;
		//  		}

		// Reminder : P_BL, P_BR and P_TR are the three specification points
		PicPoint ptBuf1, ptBuf2;
		switch (numPoint){
		case P_BL: // also moves BR so that TR is kept fixed
			ptBL.setCoordinates(pt);
			ptBuf1 = ptTR.clone().translate(b2tVec); // rebuild BR->TR line in case these points were identical
			ptBR.setCoordinates(pt);
			ptBR.project(ptTR,ptBuf1,l2rVec); // (p1,p2,proj-axis)
			break;
		case P_TR: // also moves BR so that BL is kept fixed
			ptTR.setCoordinates(pt);
			ptBuf1 = ptBL.clone().translate(l2rVec); // rebuild BL->BR line in case these points were identical
			ptBR.setCoordinates(pt);
			ptBR.project(ptBL,ptBuf1,b2tVec); // (p1,p2,proj-axis)
			break;
		case P_BR: // also moves BL and TR so that TL is kept fixed
			// warning : P_TL it's NOT a specification point, hence moving specification points may "move" it as well ;
			ptBuf1 = getCtrlPt(P_TL, null);   // => it vital to save its value beforehands
			ptBuf2 = ptBuf1.clone();
			ptBR.setCoordinates(pt); // side-effect : this (indirectly) "moves" P_TL hence the need to save P_TL beforehands
			ptBuf2.translate(b2tVec); // rebuild TL->BL line
			ptBL.setCoordinates(pt).project(ptBuf1,ptBuf2,l2rVec); // (p1,p2,proj-axis)
			ptBuf2.setCoordinates(ptBuf1).translate(l2rVec); // rebuild TL->TR line
			ptTR.setCoordinates(pt).project(ptBuf1,ptBuf2,b2tVec); // (p1,p2,proj-axis)
			break;
		case P_TL: // also moves BL and TR so that BR is kept fixed
			ptBuf1 = ptBR.clone().translate(l2rVec); // rebuild BR->BL line
			ptBL.setCoordinates(pt).project(ptBR,ptBuf1,b2tVec); // (p1,p2,proj-axis)
			ptBuf1.setCoordinates(ptBR).translate(b2tVec); // rebuild BR->TR line
			ptTR.setCoordinates(pt).project(ptBR,ptBuf1,l2rVec); // (p1,p2,proj-axis)
			break;
		case SIDE_L: // keep BR and TR fixed, translate BL
			ptBL.translate(getCtrlPt(SIDE_L,null),pt);
			updateParalleloBasis();
			break;
		case SIDE_B: // keep TR and TL(*) fixed, translate BL and BR (*:TL is not a spec-point)
			ptBuf1 = getCtrlPt(SIDE_B,null); // save old SIDE_B in ptBuf1
			ptBL.translate(ptBuf1,pt);
			ptBR.translate(ptBuf1,pt);
			updateParalleloBasis();
			break;
		case SIDE_R: // keep BL and TL(*) fixed, translate TR and BR
			ptBuf1 = getCtrlPt(SIDE_R,null); // save old SIDE_R in ptBuf1
			ptTR.translate(ptBuf1,pt);
			ptBR.translate(ptBuf1,pt);
			updateParalleloBasis();
			break;
		case SIDE_T: // keep BL and BR fixed, translate TR
			ptTR.translate(getCtrlPt(SIDE_T,null),pt);
			updateParalleloBasis();
			break;
		case P_CENTER: // translate BL, BR and TR
			ptBuf1 = getCtrlPt(P_CENTER,null); // save old CENTER in ptBuf1
			ptTR.translate(ptBuf1,pt);
			ptBL.translate(ptBuf1,pt);
			ptBR.translate(ptBuf1,pt);
			break;
		default:
			new IndexOutOfBoundsException(new Integer(numPoint).toString());
		}
	}

	/**
 	 * Helper for setCtrlPt() invoked with the CENTER_FIXED constraint.
	 * Policy : center is kept fixed, spec' points are updated accordingly, otherwise this works exactly as with
	 * the DEFAULT constraint.
	 */
	private void setCtrlPtCenterFixedConstraint(int numPoint, PicPoint pt){
		// Reminder : P_BL, P_BR and P_TR are the three specification points
		PicVector buf = new PicVector();
		PicVector center = new PicVector();
		getCtrlPt(P_CENTER, center); // save center into p1
		switch (numPoint){
		case P_BL:
			ptBL.setCoordinates(pt); // BL=pt
			ptTR.setCoordinates(pt); // TR = sym. of BL wrt CENTER
			ptTR.symmetry(center);
			buf.setCoordinates(ptTR).translate(b2tVec); // rebuild BR->TR line in case these points were identical
			ptBR.setCoordinates(pt);
			ptBR.project(ptTR,buf,l2rVec); // and project (p1,p2,proj-axis)
			break;
		case P_TR:
			ptTR.setCoordinates(pt); // TR=pt
			ptBL.setCoordinates(pt); // BL = sym. of TR wrt CENTER
			ptBL.symmetry(center);
			buf.setCoordinates(ptBL).translate(l2rVec); // rebuild BL->BR line in case these points were identical
			ptBR.setCoordinates(pt);
			ptBR.project(ptBL,buf,b2tVec); // (p1,p2,proj-axis)
			break;
		case P_BR:
			ptBR.setCoordinates(pt); // BR=pt
			buf.setCoordinates(ptBR).symmetry(center); // p2 = TL (yet TL is NOT a spec' point)
			center.setCoordinates(buf).translate(l2rVec); // rebuild TL -> TR line for the projection (p1 as CENTER no longer used)
			ptTR.setCoordinates(pt);
			ptTR.project(center,buf,b2tVec); // (p1,p2,proj-axis)
			center.setCoordinates(buf).translate(b2tVec); // rebuild BL -> TL line for the projection
			ptBL.setCoordinates(pt);
			ptBL.project(center,buf,l2rVec); // (p1,p2,proj-axis)
			break;
		case P_TL:
			ptBR.setCoordinates(pt); // BR=sym of pt wrt center
			ptBR.symmetry(center);
			buf.setCoordinates(pt).translate(l2rVec); // rebuild TL -> TR line for the projection
			ptTR.setCoordinates(ptBR);
			ptTR.project(pt,buf,b2tVec); // (p1,p2,proj-axis)
			buf.setCoordinates(pt).translate(b2tVec); // rebuild BL -> TL line for the projection
			ptBL.setCoordinates(ptBR);
			ptBL.project(pt,buf,l2rVec); // (p1,p2,proj-axis)
			break;
		case SIDE_L:
			buf.setCoordinates(getCtrlPt(SIDE_L,buf),pt); // translation vector SIDE_L -> pt
			ptBL.translate(buf);
			ptBR.translate(buf.inverse());
			ptTR.translate(buf);
			updateParalleloBasis();
			break;
		case SIDE_B:
			buf.setCoordinates(getCtrlPt(SIDE_B,buf),pt); // translation vector SIDE_B -> pt
			ptBL.translate(buf);
			ptBR.translate(buf);
			ptTR.translate(buf.inverse());
			updateParalleloBasis();
			break;
		case SIDE_R:
			buf.setCoordinates(getCtrlPt(SIDE_R,buf),pt); // translation vector SIDE_R -> pt
			ptTR.translate(buf);
			ptBR.translate(buf);
			ptBL.translate(buf.inverse());
			updateParalleloBasis();
			break;
		case SIDE_T:
			buf.setCoordinates(getCtrlPt(SIDE_T,buf),pt); // translation vector SIDE_T -> pt
			ptTR.translate(buf);
			ptBL.translate(buf.inverse());
			ptBR.translate(buf);
			updateParalleloBasis();
			break;
		case P_CENTER:
			// does nothing (center fixed !)
			break;
		default:
			throw new IndexOutOfBoundsException(new Integer(numPoint).toString());
		}
	}

	/**
 	 * Helper for setCtrlPt() invoked with the SQUARE constraint.
	 * Policy : opposite corner is kept fixed. Third specification point is computed on-the-fly so that
	 * the parallelogram becomes or stays square.
	 */
	private void setCtrlPtSquareConstraint(int numPoint, PicPoint pt){
		// Reminder : P_BL, P_BR and P_TR are the three specification points
		PicVector ptBuf1 = new PicVector();
		PicVector ptBuf2 = new PicVector();
		switch (numPoint){
		case P_BL: // leave P_TR fixed, update P_BR
			ptBL.setCoordinates(pt);
			// compute P_BR = P_BL + p1 + p2, where p1+p2 = square's side properly oriented
			ptBuf1.setCoordinates(ptBL,ptTR);
			ptBuf1.scale(1/2.);
			ptBuf2.setCoordinates(ptBuf1);
			ptBuf2.rotate(PI/2);
			ptBR.setCoordinates(ptBL);
			ptBR.translate(ptBuf1.add(ptBuf2)); // BR = BL + p1 + p2, where p1+p2 = square's side
			updateParalleloBasis();
			break;

		case P_TR: // leave P_BL fixed, update P_BR
			ptTR.setCoordinates(pt);
			// compute P_BR = P_BL + p1 + p2, where p1+p2 = square's side
			ptBuf1.setCoordinates(ptBL,ptTR);
			ptBuf1.scale(1/2.);
			ptBuf2.setCoordinates(ptBuf1);
			ptBuf2.rotate(PI/2);
			ptBR.setCoordinates(ptBL);
			ptBR.translate(ptBuf1.add(ptBuf2));
			updateParalleloBasis();
			break;
		case P_BR: // leave P_TL fixed, update P_TR and P_BL
			// save P_TL into p1:
			getCtrlPt(P_TL,ptBuf1);
			// compute P_BL = P_BR + p1 + p2, where p1+p2 = square's side
			// and P_TR = P_BR + p1 - p2
			ptBuf1.setCoordinates(pt,ptBuf1); // i.e. p1 = (P_BR -> P_TL)
			ptBuf1.scale(1/2.);
			ptBuf2.setCoordinates(ptBuf1);
			ptBuf2.rotate(PI/2);
			ptBL.setCoordinates(pt); // pt = P_BR
			ptBL.translate(ptBuf1);
			ptBL.translate(ptBuf2);
			ptTR.setCoordinates(pt); // pt = P_BR
			ptTR.translate(ptBuf1);
			ptTR.translate(ptBuf2.inverse());
			// move P_BR to pt:
			ptBR.setCoordinates(pt);
			updateParalleloBasis();
			break;
		case P_TL: // leave P_BR fixed, update P_TR and P_BL
			// compute P_BL = P_BR + p1 + p2, where p1+p2 = square's side
			// and P_TR = P_BR + p1 - p2
			ptBuf1.setCoordinates(ptBR,pt); // p1 = P_BR -> P_TL
			ptBuf1.scale(1/2.);
			ptBuf2.setCoordinates(ptBuf1);
			ptBuf2.rotate(PI/2);
			ptBL.setCoordinates(ptBR);
			ptBL.translate(ptBuf1);
			ptBL.translate(ptBuf2);
			ptTR.setCoordinates(ptBR);
			ptTR.translate(ptBuf1);
			ptTR.translate(ptBuf2.inverse());
			updateParalleloBasis();
			break;
		case SIDE_L: // leave SIDE_R fixed, update all spec points
			ptBuf1.setCoordinates(pt,getCtrlPt(SIDE_R,ptBuf1));
			ptBuf1.scale(1/2.);
			ptBuf2.setCoordinates(ptBuf1);
			ptBuf2.rotate(-PI/2);
			// set BL
			ptBL.setCoordinates(pt);
			ptBL.translate(ptBuf2);
			// set BR
			ptBR.setCoordinates(ptBL);
			ptBR.translate(ptBuf1);
			ptBR.translate(ptBuf1);
			// set TR
			ptTR.setCoordinates(ptBR);
			ptTR.translate(ptBuf2.inverse());
			ptTR.translate(ptBuf2);
			updateParalleloBasis();
			break;
		case SIDE_B: // leave SIDE_T fixed, update all spec points
			ptBuf1.setCoordinates(pt,getCtrlPt(SIDE_T,ptBuf1));
			ptBuf1.scale(1/2.);
			ptBuf2.setCoordinates(ptBuf1);
			ptBuf2.rotate(PI/2);
			// set BL
			ptBL.setCoordinates(pt);
			ptBL.translate(ptBuf2);
			// set BR
			ptBR.setCoordinates(pt);
			ptBR.translate(ptBuf2.inverse());
			// set TR
			ptTR.setCoordinates(ptBR);
			ptTR.translate(ptBuf1);
			ptTR.translate(ptBuf1);
			updateParalleloBasis();
			break;
		case SIDE_R: // leave SIDE_L fixed, update all spec points
			ptBuf1.setCoordinates(pt,getCtrlPt(SIDE_L,ptBuf1));
			ptBuf1.scale(1/2.);
			ptBuf2.setCoordinates(ptBuf1);
			ptBuf2.rotate(-PI/2);
			// set TR
			ptTR.setCoordinates(pt);
			ptTR.translate(ptBuf2);
			// set BR
			ptBR.setCoordinates(pt);
			ptBR.translate(ptBuf2.inverse());
			// set BL
			ptBL.setCoordinates(ptBR);
			ptBL.translate(ptBuf1);
			ptBL.translate(ptBuf1);
			updateParalleloBasis();
			break;
		case SIDE_T: // leave SIDE_B fixed, update all spec points
			ptBuf1.setCoordinates(pt,getCtrlPt(SIDE_B,ptBuf1));
			ptBuf1.scale(1/2.);
			ptBuf2.setCoordinates(ptBuf1);
			ptBuf2.rotate(PI/2);
			// set TR
			ptTR.setCoordinates(pt);
			ptTR.translate(ptBuf2);
			// set BR
			ptBR.setCoordinates(ptTR);
			ptBR.translate(ptBuf1);
			ptBR.translate(ptBuf1);
			// set BL
			ptBL.setCoordinates(ptBR);
			ptBL.translate(ptBuf2.inverse());
			ptBL.translate(ptBuf2);
			updateParalleloBasis();
			break;
		case P_CENTER: // translate BL, BR and TR
			getCtrlPt(P_CENTER,ptBuf1); // save old CENTER in ptBuf1
			ptTR.translate(ptBuf1,pt);
			ptBL.translate(ptBuf1,pt);
			ptBR.translate(ptBuf1,pt);
			break;
		default:
			throw new IndexOutOfBoundsException(new Integer(numPoint).toString());
		}
	}

	/**
	 * Sets the coordinates of this element from those of the given source.
	 * This methods fires a DrawingEvent.
	 * @since jpicedt 1.5
	 */
	public void setGeometry(PicParallelogram para){
		ptBL.setCoordinates(para.ptBL);
		ptBR.setCoordinates(para.ptBR);
		ptTR.setCoordinates(para.ptTR);
		updateParalleloBasis();
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}

	/**
	 * Sets the coordinates of this element from the given shape.
	 * This methods fires a DrawingEvent.
	 * @since jpicedt 1.5
	 */
	public void setGeometry(RectangularShape rect){
		ptBL.setCoordinates(rect.getMinX(), rect.getMinY());
		ptBR.setCoordinates(rect.getMaxX(), rect.getMinY());
		ptTR.setCoordinates(rect.getMaxX(), rect.getMaxY());
		updateParalleloBasis();
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}

	/**
	 * Return the parallelogram's centre
	 * @return if <code>src</code> is null, allocates a new PicPoint and return it,
	 *         otherwise directly modifies <code>src</code> and returns it as well for convenience.
	 * @since jpicedt 1.3.3
	 */
	public PicPoint getCenter(PicPoint src){
		if (src==null) src=new PicPoint();
		src.setCoordinates(ptBL);
		src.middle(ptTR);
		return src;
	}

	/**
	 * Rotate this Element by the given angle along the given point
	 * @param angle rotation angle in radians
	 */
	public void rotate(PicPoint ptOrg, double angle){
		ptBL.rotate(ptOrg,angle);
		ptBR.rotate(ptOrg,angle);
		ptTR.rotate(ptOrg,angle);
		updateParalleloBasis();
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
		ptBL.mirror(ptOrg,normalVector);
		ptBR.mirror(ptOrg,normalVector);
		ptTR.mirror(ptOrg,normalVector);
		updateParalleloBasis();
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}

	/**
	 * Shear this <code>Element</code> by the given params wrt to the given origin.
	 */
	public void shear(PicPoint ptOrg, double shx, double shy, UserConfirmationCache ucc){
		ptBL.shear(ptOrg,shx,shy);
		ptBR.shear(ptOrg,shx,shy);
		ptTR.shear(ptOrg,shx,shy);
		updateParalleloBasis();
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}

	/**
	 * Scale this object by <code>(sx,sy)</code> using <code>(ptOrgX,ptOrgY)</code> as the origin. This
	 * implementation simply apply a scaling transform to all specification-points.  Note that <code>sx</code>
	 * and <code>sy</code> may be negative.  This method eventually fires a changed-update event.
	 */
	public void scale(double ptOrgX, double ptOrgY, double sx, double sy, UserConfirmationCache ucc) {
		ptBL.scale(ptOrgX,ptOrgY,sx,sy);
		ptBR.scale(ptOrgX,ptOrgY,sx,sy);
		ptTR.scale(ptOrgX,ptOrgY,sx,sy);
		// updateParalleloBasis(); not needed for scaling
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}

	/**
	 * Translate this Element by (dx,dy) ; this implementation translates the specification-points,
	 * then fires a changed-update event.
	 * @param dx The X coordinate of translation vector
	 * @param dy The Y coordinate of translation vector
	 * @since PicEdt 1.0
	 */
	public void translate(double dx, double dy) {
		ptBL.translate(dx,dy);
		ptBR.translate(dx,dy);
		ptTR.translate(dx,dy);
		// updateParalleloBasis(); not needed for translation
		fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	}

	/**
	 * @param csg l'ensemble de zones convexes auquel  on teste l'appartenance des points de contrôles
	 * @param czExtension ignoré
	 * @see jpicedt.graphic.model#Element.getCtrlPtSubset(ConvexZoneGroup csg,BitSet czExtension)
	 * @since jPicEdt 1.6
	 */
	public CtrlPtSubset getCtrlPtSubset(ConvexZoneGroup csg,BitSet czExtension){
		int bitmap = 0;
		int hW = 0; // Hamming weight
		PicPoint[] points = new PicPoint[CTRL_PT_SIDE_BITMAP.length];
		int [] inSideBitmaps = { 0, 0, 0, 0};
		int [] inSideHWs     = { 0, 0, 0, 0};
		int bm = 1;
		for(int i = 0; i < CTRL_PT_SIDE_BITMAP.length; ++i, bm<<=1){
			if(csg.containsPoint(points[i] = getCtrlPt(i,null))){
				bitmap |= bm;
				hW++;
				int inSide = CTRL_PT_SIDE_BITMAP[i];
				int side = 0;
				while(inSide != 0){
					if((inSide & 1) != 0){
						inSideBitmaps[side] |= bm;
						inSideHWs[side] += 1;
					}
					side++;
					inSide >>= 1;
				}
			}
		}

		if(hW == 0) return null;
		if(hW == 1){
			int i = 0;
			while(bitmap != 1){
				++i;
				bitmap >>=1;
			}
			return new CtrlPtSubsetCorner(this, points[i], i);
		}
		// si tous les points sont sur le même côté on déplace ce côté
		for(int i = 0; i < 4; ++i){
			if(bitmap == inSideBitmaps[i]){
				return new CtrlPtSubsetCorner(this, points[SIDE_0+i], SIDE_0+i);
			}
		}

		// sinon on translate toute la forme
		return new CtrlPtSubsetPlain(this);

		// // si on a au moins 3 points, ou une diagonale on prend le parallélogramme en entier
		// if(hW >= 3 || bitmap == 5 || bitmap == 10)
		// 	return new CtrlPtSubsetPlain(this);

		// switch(hW){
		// case 1:
		// 	return new CtrlPtSubsetCorner(this, new PicPoint(points[0]), index[0]);
		// case 2:
		// 	return new CtrlPtSubsetSide(this, points[0], points[1]);
		// }

		// return null;
	}

	// class CtrlPtSubsetSide implements CtrlPtSubset{
	// 	PicParallelogram parallelogram;
	// 	PicPoint pt1;
	// 	PicPoint pt2;

	// 	public CtrlPtSubsetSide(PicParallelogram parallelogram,PicPoint pt1, PicPoint pt2){
	// 		this.parallelogram = parallelogram;
	// 		this.pt1 = pt1;
	// 		this.pt2 = pt2;
	// 	}

	// 	public void translate(double dx, double dy){
	// 		pt1.translate(dx, dy);
	// 		pt2.translate(dx, dy);
	// 		parallelogram.fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
	// 	}
	// }

	class CtrlPtSubsetCorner implements CtrlPtSubset{
		// On prend Element et non pas PicParallelogram pour gérer convenablement les formes dérivées comme
		// les PicEllipses.
		Element   parallelogram;
		PicPoint  ptCorner;
		int       ptCornerIndex;

		/**
		 * Un <code>CtrlPtSubset</code> correspondant au déplacement d'un coin de parallelogramme. Le coin à
		 * déplacer est <code>ptCorner</code>, <code>ptCornerIndex</code> est son index.
		 * @param parallelogram le parallelogramme dont on déplace un coin
		 * @param ptCorner le coin à déplacer
		 * @param ptCornerIndex l'index du coint à déplacer
		 */
		public CtrlPtSubsetCorner(Element parallelogram,
								  PicPoint ptCorner, int ptCornerIndex){
			this.parallelogram = parallelogram;
			this.ptCorner = ptCorner;
			this.ptCornerIndex = ptCornerIndex;
		}

		public void translate(double dx, double dy){
			ptCorner.translate(dx, dy);
			parallelogram.setCtrlPt(ptCornerIndex,ptCorner, null);
		}
	}


	////////////////////////////////
	/// transforms
	////////////////////////////////

	/**
	 * Return a polygon created from the sides of this parallelogram.
	 *
	 * @since jpicedt 1.3.3
	 */
	public PicMultiCurve convertToMultiCurve(){

		PicMultiCurve curve = new PicMultiCurve(true, this.attributeSet); // closed
		curve.addPoint(getCtrlPt(P_BL,null));
		PicPoint pt = getCtrlPt(P_BR,null);
		curve.splitSegment(0, pt, pt, pt);
		pt = getCtrlPt(P_TR,null);
		curve.splitSegment(1, pt, pt, pt);
		pt = getCtrlPt(P_TL,null);
		curve.splitSegment(2, pt, pt, pt);
		return curve;
	}

	/**
	 * Returns true if this parallelogram is a rectangle
	 */
	public boolean isRectangle(){
		PicVector p1 = new PicVector(ptBR,ptBL);
		PicVector p2 = new PicVector(ptBR,ptTR);
		return abs(p1.dot(p2)) <= (p1.norm1() + p2.norm1())*MathConstants.DOUBLE_ABSOLUTE_TOLERANCE;
	}

	/**
	 * Makes this parallelogram a rectangle [pending]
	 */
	public void makeRectangle(){
		throw new RuntimeException("Not implemented!");
	}

	/**
	 * @return <code>true</code> si le parallélogramme est rectangle, et que l'axe (BL, BR) est parallèle à
	 * l'axe des <var>x</var>, et l'axe (BL, TL)  à l'axe des <var>y</var>.
	 */
	public boolean isXYRectangle(){
		return isRectangle() && (ptBL.y == ptBR.y);
	}

	/**
	 * @return <code>true</code> si le parallélogramme est rectangle, et que l'un de ses axes est parallèle à
	 * l'axe des <var>x</var>, et l'autre à l'axe des <var>y</var>.
	 */
	public boolean isXYorYXRectangle(){
		return isRectangle() && (ptBL.y == ptBR.y || ptBL.x == ptBR.x);
	}

	/**
	 * @return l'angle en radian que fait l'axe de (BL, BR) relativement à l'axe des <var>x</var>.
	 * @since jPicEdt 1.6
	 */
	public Double getL2RtoXAxisAngle(){
		return l2rVec.angle(PicVector.X_AXIS);
	}

	/**
	 * @return le vecteur (BL, BR).
	 * @since jPicEdt 1.6
	 */
	public final PicVector getL2RVec(){
		return l2rVec;
	}

	/**
	 * @return le vecteur (BR, TR).
	 * @since jPicEdt 1.6
	 */
	public final PicVector getB2TVec(){
		return b2tVec;
	}


	/**
	 * Makes this parallelogram a rectangle with its sides parallel to the X- and Y-axes.  [pending]
	 */
	public void makeXYRectangle(){
		throw new RuntimeException("Not implemented!");
	}

	//////////////////////////////////////////
	/// View
	//////////////////////////////////////////

	/**
	 * Returns a GeneralPath that represents the shape of this PicParallelogram.
	 */
	public Shape createShape(){
		if (this.path==null)
			this.path = new GeneralPath(GeneralPath.WIND_NON_ZERO, 4); // 4 segments
		else
			this.path.reset();
		// update shape
		path.moveTo((float)ptBL.x, (float)ptBL.y); // 0
		path.lineTo((float)ptBR.x, (float)ptBR.y); // 1
		path.lineTo((float)ptTR.x, (float)ptTR.y); // 2
		//PicPoint pt = getCtrlPt(P_TL, null);
		//path.lineTo((float)pt.x, (float)pt.y);
		// ptBL.translate(ptBR,ptTR);
		path.lineTo((float)(ptBL.x + ptTR.x - ptBR.x), (float)(ptBL.y + ptTR.y - ptBR.y));
		path.closePath();
		return path;
	}

	// not overriden: (does nothing)
	// void syncArrowGeometry(ArrowView v, ArrowView.Direction d);

	//////////////////////////////////////////
	/// Formatter
	//////////////////////////////////////////

	/**
	 * Returns the bounding box (ie the surrounding rectangle) in double precision
	 * Used for instance to determine the arguments of a \\begin{picture} command.<p>
	 * This implementation compute the bounding-box from the smallest rectangle that encompasses
	 * all the specification-points.
	 * @since jpicedt 1.3.3
	 */
	public Rectangle2D getBoundingBox(Rectangle2D r){
		if (r==null)
			r = new Rectangle2D.Double();
		r.setFrameFromDiagonal(ptBL,ptTR);
		r.add(ptBR);
		r.add(getCtrlPt(P_TL, null));
		if(DEBUG)
			debug("bounding box=" + r.toString());
		return r;
	}

	//////////////////////////////////////////
	/// Debug
	//////////////////////////////////////////

	/**
	 * Implementation of the Object.toString() method, used for debugging purpose
	 */
	public String toString() {

		String s = super.toString();
		s += "\n\t";
		s += "0:"+ptBL.toString()+";";
		s += "1:"+ptBR.toString()+";";
		s += "2:"+ptTR.toString()+";";
		s += "\n\t l2rVec=" + l2rVec + ", b2tVec=" + b2tVec;
		return s;
	}

	/////////////////////////////////
	//// Action's
	/////////////////////////////////

	/**
	 * Create an array of Action's related to this object
	 *
	 * @param actionDispatcher  dispatches events to the proper PECanvas
	 * @param localizer         i18n localizer for PEAction's
	 */
	public ArrayList<PEAction> createActions(ActionDispatcher actionDispatcher, ActionLocalizer localizer, HitInfo hi) {
		ArrayList<PEAction> l = super.createActions(actionDispatcher, localizer, hi);
		if (l==null)
			l = new ArrayList<PEAction>();
		l.add(new ConvertToCurveAction(actionDispatcher, localizer));
		return l;
	}

	// ---- parallelo to multiCurve ----

	/**
	 * Convert this parallelogram to a multicurve, selecting it if applicable.
	 * @author    Sylvain Reynal
	 * @since     jpicedt 1.4
	 */
	class ConvertToCurveAction extends PEAction {

		public static final String KEY = "action.editorkit.ConvertParallelogramToMulticurve";

		public ConvertToCurveAction(ActionDispatcher actionDispatcher, ActionLocalizer localizer){
			super(actionDispatcher, KEY, localizer);
		}

		public void undoableActionPerformed(ActionEvent e){
			PicMultiCurve curve = convertToMultiCurve();
			/*
			Drawing dr = getDrawing();
			if (dr != null){
				dr.replace(PicParallelogram.this, curve);
				View view = curve.getView();
				if (view != null){
					PECanvas canvas = view.getContainer();
					if (canvas != null)   canvas.select(curve, PECanvas.SelectionBehavior.INCREMENTAL);
				}
			}*/

			BranchElement parent = getParent();
			parent.replace(PicParallelogram.this, curve);
			View view = curve.getView();
			if (view != null){
				PECanvas canvas = view.getContainer();
				if (canvas != null)   canvas.select(curve, PECanvas.SelectionBehavior.INCREMENTAL);
			}
		}
	}

	////////////////////////////////
	//// GUI
	////////////////////////////////

	/**
	 * @return a Customizer for geometry editing
	 * @since jpicedt 1.4
	 */
	public AbstractCustomizer createCustomizer(){
		if (cachedCustomizer == null)
			cachedCustomizer = new Customizer();
		cachedCustomizer.load();
		return cachedCustomizer;
	}

	private Customizer cachedCustomizer = null;

	/**
	 * Geometry customizer
	 * @since jpicedt 1.4
	 */
	class Customizer extends AbstractCustomizer implements ActionListener {

		private DecimalNumberField paraBLCornerXTF,paraBLCornerYTF, paraBRCornerXTF,paraBRCornerYTF,paraTRCornerXTF,paraTRCornerYTF;
		private boolean isListenersAdded = false; // flag

		public Customizer(){

			super();
			JPanel p = new JPanel(new GridLayout(4,3,5,5));

			p.add(PEToolKit.createJLabel("action.draw.Parallelogram"));
			p.add(new JLabel("x"));
			p.add(new JLabel("y"));
			// line 1: bottom left corner label
			p.add(new JLabel("1"));
			// x-coordinate of bl-corner
			p.add(paraBLCornerXTF = new DecimalNumberField(4));
			// y-coordinate of bl-corner
			p.add(paraBLCornerYTF = new DecimalNumberField(4));


			// line 2: bottom right corner label
			p.add(new JLabel("2"));
			// x-coordinate of br-corner
			p.add(paraBRCornerXTF = new DecimalNumberField(4));
			// y-coordinate of br-corner
			p.add(paraBRCornerYTF = new DecimalNumberField(4));

			// line 3: top right corner label
			p.add(new JLabel("3"));
			// x-coordinate of TR-corner
			p.add(paraTRCornerXTF = new DecimalNumberField(4));
			// y-coordinate of TR-corner
			p.add(paraTRCornerYTF = new DecimalNumberField(4));

			add(p, BorderLayout.NORTH);
			setPreferredSize(new Dimension(400,200));
		}

		/** add action listeners to widgets to reflect changes immediately */
		private void addActionListeners(){
			if (isListenersAdded) return; // already done
			paraBLCornerXTF.addActionListener(this);
			paraBLCornerYTF.addActionListener(this);
			paraBRCornerXTF.addActionListener(this);
			paraBRCornerYTF.addActionListener(this);
			paraTRCornerXTF.addActionListener(this);
			paraTRCornerYTF.addActionListener(this);
			isListenersAdded = true;
		}

		/** add action listeners to widgets to reflect changes immediately */
		private void removeActionListeners(){
			if (!isListenersAdded) return; // already done
			paraBLCornerXTF.removeActionListener(this);
			paraBLCornerYTF.removeActionListener(this);
			paraBRCornerXTF.removeActionListener(this);
			paraBRCornerYTF.removeActionListener(this);
			paraTRCornerXTF.removeActionListener(this);
			paraTRCornerYTF.removeActionListener(this);
			isListenersAdded = false;
		}

		/**
		 * (re)init widgets with Element's properties
		 */
		public void load(){
			removeActionListeners();

			PicPoint pt = new PicPoint();
			getCtrlPt(PicParallelogram.P_BL,pt);
			paraBLCornerXTF.setValue(pt.x);
			paraBLCornerYTF.setValue(pt.y);
			getCtrlPt(PicParallelogram.P_BR,pt);
			paraBRCornerXTF.setValue(pt.x);
			paraBRCornerYTF.setValue(pt.y);
			getCtrlPt(PicParallelogram.P_TR,pt);
			paraTRCornerXTF.setValue(pt.x);
			paraTRCornerYTF.setValue(pt.y);
			// add listeners AFTERWARDS ! otherwise loading widgets initial value has a painful side-effet...
			// since it call "store" before everything has been loaded
			addActionListeners(); // done the first time load is called
		}

		/**
		 * update Element's properties
		 */
		public void store(){
			PicPoint bl= new PicPoint(paraBLCornerXTF.getValue(),paraBLCornerYTF.getValue());
			PicPoint br= new PicPoint(paraBRCornerXTF.getValue(),paraBRCornerYTF.getValue());
			PicPoint tr= new PicPoint(paraTRCornerXTF.getValue(),paraTRCornerYTF.getValue());
			ptBL.setCoordinates(bl);
			ptBR.setCoordinates(br);
			ptTR.setCoordinates(tr);
			updateParalleloBasis();
			fireChangedUpdate(DrawingEvent.EventType.GEOMETRY_CHANGE);
		}

		public void actionPerformed(ActionEvent e){
			store();
		}

		public String getTitle(){
			return PicParallelogram.this.getName();
		}

	}
} // PicParallelogram
