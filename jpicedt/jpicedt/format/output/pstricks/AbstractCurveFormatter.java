// AbstractCurveFormatter.java --- -*- coding: iso-8859-1 -*-
// December 25, 2003 - jPicEdt 1.3.3, a picture editor for LaTeX.
// Copyright (C) 1999/2007 Sylvain Reynal
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
// Version: $Id: AbstractCurveFormatter.java,v 1.25 2013/03/27 07:09:35 vincentb1 Exp $
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
package jpicedt.format.output.pstricks;

import jpicedt.graphic.io.formatter.Formatter;
import jpicedt.graphic.io.formatter.AbstractFormatter;
import jpicedt.graphic.*;
import jpicedt.graphic.model.*;
import java.awt.*;

import static jpicedt.format.output.pstricks.PstricksConstants.*;
import static jpicedt.graphic.model.StyleConstants.*;
import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.AbstractCurve.PointType.*;
import static jpicedt.graphic.view.ArrowView.Direction;

/**
 * Format an AbstractCurve, or any subclass of it, using PsTricks macros.
 * These are "rendered" to PsTricks using a <code>\\pscustom</code> command that lumps
 * several Bezier curves together in a single Pstricks object.
 *
 * @author    Vincent Guirardel, Sylvain Reynal
 * @since jpicedt 1.4
 * @version   $Id: AbstractCurveFormatter.java,v 1.25 2013/03/27 07:09:35 vincentb1 Exp $
 */
public class AbstractCurveFormatter extends AbstractFormatter {

	/**
	 * the Element this formatter acts upon
	 */
	protected AbstractCurve element;
	/** the producing factory */
	protected PstricksFormatter factory;

   /**
	* @since jPicEdt 1.6
	*/
	public Element getElement(){ return element;}

	public AbstractCurveFormatter(AbstractCurve element, PstricksFormatter factory) {
		this.element = element;
		this.factory = factory;
	}


	/**
	 * Return a String representing this curve in the PsTricks format.
	 * This delegates to either {@link #formatPsCustom formatPsCustom()}
	 * or {@link #formatPsDots formatPsDots()} depending on the value of POLYDOTS_STYLE attribute.
	 */
	public String format() {
		boolean isPsDots = (element.getAttribute(POLYDOTS_STYLE) != PolydotsStyle.NONE);
		boolean isSuperimposed = element.getAttribute(POLYDOTS_SUPERIMPOSE);
		StringBuffer buf = new StringBuffer(100);
		if (isPsDots) {
			if (isSuperimposed) buf.append(formatCurve());
			buf.append(formatPsDots());
			return buf.toString();
		}
		else return formatCurve();
	}

	/**
	 * this is a convenience call to either formatPsCustom() or formatPsBezier() or
	 * formatPsLine().
	 */

	protected String formatCurve(){
		if (element.isPolygon()) return formatPsLine(); // one or several straight segments exclusively
		else if (element instanceof PicPsCurve) return formatPsCurve();
		else if (element.getSegmentCount()>1) return formatPsCustom(); // more than one segment
		else return formatPsBezier(); // only one segment, not straight
	}



	/**
	 * If the curve contains a single Bezier segment: \\psbezier... <br>
	 */
	protected String formatPsBezier() {

		StringBuffer buf = new StringBuffer(100);// 100 as initial capacity seems to be a good guess
		PicAttributeSet set = element.getAttributeSet();

		// first handle possibly user-defined colours
		PstricksFormatter.ParameterString paramStr = factory.createParameterString(element);
		if (paramStr.isDefinedColourString()) buf.append(paramStr.getUserDefinedColourBuffer());

		buf.append("\\psbezier");
		// parameters
		if (paramStr.getParameterBuffer().length()>0){
			buf.append("[");
			buf.append(paramStr.getParameterBuffer());
			buf.append("]");
		}

		// arrows
		if (!element.isClosed()) buf.append(PstricksUtilities.createPstricksStringFromArrows(element));

		// points
		buf.append(element.getBezierPt(element.segmentToPointIndex(0,SUBDIVISION_POINT), null));
		buf.append(element.getBezierPt(element.segmentToPointIndex(0,FIRST_SEGMENT_CONTROL_POINT), null));
		buf.append(element.getBezierPt(element.segmentToPointIndex(0,SECOND_SEGMENT_CONTROL_POINT), null));
		buf.append(element.getBezierPt(element.segmentToPointIndex(1,SUBDIVISION_POINT), null));
		buf.append(factory.getLineSeparator());
		return buf.toString();
	}

	/**
	 * Polygon: \\psline...
	 */
	protected String formatPsLine() {

		StringBuffer buf = new StringBuffer(100);// 100 as initial capacity seems to be a good guess
		PicAttributeSet set = element.getAttributeSet();

		// first handle possibly user-defined colours
		PstricksFormatter.ParameterString paramStr = factory.createParameterString(element);
		if (paramStr.isDefinedColourString()) buf.append(paramStr.getUserDefinedColourBuffer());

		if (element.isClosed()) buf.append("\\pspolygon");
		else buf.append("\\psline");

		// parameters
		if (paramStr.getParameterBuffer().length()>0){
			buf.append("[");
			buf.append(paramStr.getParameterBuffer());
			buf.append("]");
		}

		if (!element.isClosed()) buf.append(PstricksUtilities.createPstricksStringFromArrows(element));

		for (int i = 0; i < element.getSegmentCount(); i++) {
			buf.append(element.getBezierPt(element.segmentToPointIndex(i,SUBDIVISION_POINT), null));
			// only add second point if this is the last segment (thereby avoiding redundancy, since writing each
			// point twice was also reported to create some mess when in conjunction with a "linearc" PSTricks parameter)
			if ( i == element.getSegmentCount() - 1 ) { // JHf
            			buf.append(element.getBezierPt(element.segmentToPointIndex(i+1,SUBDIVISION_POINT), null)); // JHf
        		} // JHf
			buf.append(factory.getLineSeparator());
		}
		return buf.toString();
	}


	/**
	*  Format for pscurve
	* <br><b>author:</b> Vincent
	* @since jPicEdt 1.4pre5
	*/
	protected String formatPsCurve(){

		PicPsCurve pscurve=(PicPsCurve)element;
		StringBuffer buf = new StringBuffer(100);// 100 as initial capacity seems to be a good guess
		PicAttributeSet set = pscurve.getAttributeSet();

		// first handle possibly user-defined colours
		PstricksFormatter.ParameterString paramStr = factory.createParameterString(pscurve);
		if (paramStr.isDefinedColourString()) buf.append(paramStr.getUserDefinedColourBuffer());

		if (pscurve.isClosed()) buf.append("\\psccurve");
		else buf.append("\\psecurve");

		// parameters
		if (paramStr.getParameterBuffer().length()>0){
			buf.append("[");
			buf.append(paramStr.getParameterBuffer());
			// curvatures: (bug fix 01/08/2006)
			if (paramStr.getParameterBuffer().length()>0){
				buf.append(',');
				buf.append("curvature=");
				double[] curvatures = pscurve.getCurvatures();
				buf.append(curvatures[0]);
				buf.append(' ');
				buf.append(curvatures[1]);
				buf.append(' ');
				buf.append(curvatures[2]);
			}
			buf.append("]");
		}

		if (!pscurve.isClosed()) buf.append(PstricksUtilities.createPstricksStringFromArrows(pscurve));


		for (int i = 0; i <= pscurve.getLastPointIndex(); i++) {
			buf.append(pscurve.getCtrlPt(i,null));
		}
		buf.append(factory.getLineSeparator());
		return buf.toString();
	}




	/**
	 * If the curve has more than one segment, and it's not a polygon.
	 * \\pscustom[param]{arrows}{\\psline... or  \\psbezier... \\closepath} <br>
	 */
	protected String formatPsCustom() {

		StringBuffer buf = new StringBuffer(100);// 100 as initial capacity seems to be a good guess
		PicAttributeSet set = element.getAttributeSet();

		// first handle possibly user-defined colours
		PstricksFormatter.ParameterString paramStr = factory.createParameterString(element);
		if (paramStr.isDefinedColourString()) buf.append(paramStr.getUserDefinedColourBuffer());

		buf.append("\\pscustom");
		// parameters
		buf.append("[");
		buf.append(paramStr.getParameterBuffer()); // [VG:pending] we should strip redundant border-related parameters from this buffer
		buf.append("]{");

		ArrowStyle leftArrow = set.getAttribute(LEFT_ARROW);
		ArrowStyle rightArrow = set.getAttribute(RIGHT_ARROW);
		boolean isAtLeastOneArrow = (leftArrow!=ArrowStyle.NONE || rightArrow!=ArrowStyle.NONE);

		for (int i = 0; i < element.getSegmentCount(); i++) {
			if (element.isStraight(i)) buf.append("\\psline");
			else buf.append("\\psbezier");

			// arrows if at least one arrow is non-NONE, curve is open, and this is either the first or the
			// last segment...
			boolean allowArrow = (isAtLeastOneArrow && !element.isClosed() && element.getSegmentCount() >= 1 && (i == 0 || i == element.getSegmentCount() - 1));
			if (allowArrow) {
				buf.append("{");
				if (i == 0) buf.append(PstricksUtilities.toPstricksString(leftArrow, Direction.LEFT));// e.g. "<" for ARROW_HEAD
				buf.append("-");
				if (i == element.getSegmentCount() - 1)
					buf.append(PstricksUtilities.toPstricksString(rightArrow, Direction.RIGHT));// e.g. ">" for ARROW_HEAD (this way, we really get "<->" instead of ">->" which was wrong !)
				buf.append("}");
			}

			// points
			if (element.isStraight(i)) {
				buf.append(element.getBezierPt(element.segmentToPointIndex(i,SUBDIVISION_POINT), null));
				buf.append(element.getBezierPt(element.segmentToPointIndex(i+1,SUBDIVISION_POINT), null));
			}
			else {
				if (i == 0)
					buf.append(element.getBezierPt(element.segmentToPointIndex(i,SUBDIVISION_POINT), null));
				buf.append(element.getBezierPt(element.segmentToPointIndex(i,FIRST_SEGMENT_CONTROL_POINT), null));
				buf.append(element.getBezierPt(element.segmentToPointIndex(i,SECOND_SEGMENT_CONTROL_POINT), null));
				buf.append(element.getBezierPt(element.segmentToPointIndex(i+1,SUBDIVISION_POINT), null));
			}
			buf.append(factory.getLineSeparator());
		}// end of ``for'' loop
		if (element.isClosed()) buf.append("\\closepath");

		// pscustom does not draw the shadow by itself...
		if (set.getAttribute(SHADOW)){ // if we have to draw a shadow
			if (element.isClosed()) buf.append("\\closedshadow ");
			else buf.append("\\openshadow ");
		}

		if (set.getAttribute(OVER_STRIKE)){ // if we have to draw a border
			//first, get parameters
			PstricksFormatter.ParameterString paramStrForBorder = createParameterStringForBorderInPsCustom(element);
			buf.append("\\stroke[");
			buf.append(paramStrForBorder.getParameterBuffer());
			buf.append("]");
		}// end of border

		buf.append("}"); // end of pscustom
		buf.append(factory.getLineSeparator());
		return buf.toString();
	}

	/**
	 * Helper method for formatPSCustom().
	 * Returns PsTricks's special parameters for handling the border in a \\pscustom command
	 * <br><b>author:</b> V.G. (moved from PsTricksFormatter by S.R. 01-08-06)
	 * @return the total width of the border to be struck underneath the real path.
	 * Some day, should also return a color. So far color=white.
	 * @since jpicedt 1.4pre3
	 */
	private PstricksFormatter.ParameterString createParameterStringForBorderInPsCustom(Element obj){

		PicAttributeSet attributes = obj.getAttributeSet();

		// only useful if border > 0
		if (! attributes.getAttribute(OVER_STRIKE)){
			return null;
		}

		StringBuffer paramBuf = new StringBuffer(100);
		StringBuffer userDefinedColorBuffer=null; //  [VG:pending] unused so far

		// linewidth equals  the linewidth plus twice the border.
		double lineWidth = attributes.getAttribute(LINE_WIDTH);
		double border = attributes.getAttribute(OVER_STRIKE_WIDTH);
		double totalWidth = lineWidth + 2*border;

		//if (paramBuf.length()>0) paramBuf.append(','); // not necessary since we're building the buffer from scratch
		paramBuf.append("linewidth=");
		paramBuf.append(PEToolKit.doubleToString(totalWidth));
		paramBuf.append(",linecolor=white");

		PstricksFormatter.ParameterString ps = factory.createParameterString(paramBuf, userDefinedColorBuffer);
		return ps;
	}


	/**
	 * \\psdots(x1,y1)...(xn,yn)
	 */
	protected String formatPsDots() {

		StringBuffer buf = new StringBuffer(100);// 100 as initial capacity seems to be a good guess
		PicAttributeSet set = element.getAttributeSet();
		PicAttributeSet defaultAttributes = new PicAttributeSet();

		// first handle possibly user-defined colours
		PstricksFormatter.ParameterString paramStr = factory.createParameterString(element);
		if (paramStr.isDefinedColourString())
			buf.append(paramStr.getUserDefinedColourBuffer());

		buf.append("\\psdots");

		// parameters
		buf.append("[");
		buf.append(paramStr.getParameterBuffer());

		// add psdots specific parameters : we only add non-default values to save some space (and possibly some memory shortage on some TeX installs)
		StringBuffer psdotsBuffer = new StringBuffer();

		double x =  set.getAttribute(POLYDOTS_SIZE_MINIMUM_MM);
		double y = set.getAttribute(POLYDOTS_SIZE_LINEWIDTH_SCALE);
		if (x!=defaultAttributes.getAttribute(POLYDOTS_SIZE_MINIMUM_MM) || y!=defaultAttributes.getAttribute(POLYDOTS_SIZE_LINEWIDTH_SCALE)){
			psdotsBuffer.append("dotsize=");
			psdotsBuffer.append(PEToolKit.doubleToString(x));
			psdotsBuffer.append(' ');
			psdotsBuffer.append(PEToolKit.doubleToString(y));
		}

		PolydotsStyle dotStyle = set.getAttribute(POLYDOTS_STYLE);
		if (!dotStyle.equals(PolydotsStyle.POLYDOTS_DISK)){ // default
			if (psdotsBuffer.length()>0) psdotsBuffer.append(',');
			psdotsBuffer.append("dotstyle=");
			switch (dotStyle){
			case POLYDOTS_CIRCLE: psdotsBuffer.append('o'); break;
			case POLYDOTS_PLUS: psdotsBuffer.append('+'); break;
			case POLYDOTS_TRIANGLE: psdotsBuffer.append("triangle"); break;
			case POLYDOTS_TRIANGLE_FILLED: psdotsBuffer.append("triangle*"); break;
			case POLYDOTS_SQUARE: psdotsBuffer.append("square"); break;
			case POLYDOTS_SQUARE_FILLED: psdotsBuffer.append("square*"); break;
			case POLYDOTS_PENTAGON: psdotsBuffer.append("pentagon"); break;
			case POLYDOTS_PENTAGON_FILLED: psdotsBuffer.append("pentagon*"); break;
			default: psdotsBuffer.append('*'); // default, should never happen
			}
		}

		x = set.getAttribute(POLYDOTS_SCALE_H);
		y = set.getAttribute(POLYDOTS_SCALE_V);
		if (x!=defaultAttributes.getAttribute(POLYDOTS_SCALE_H) || y!=defaultAttributes.getAttribute(POLYDOTS_SCALE_V)){
			if (psdotsBuffer.length()>0) psdotsBuffer.append(',');
			psdotsBuffer.append("dotscale=");
			psdotsBuffer.append(PEToolKit.doubleToString(x));
			psdotsBuffer.append(' ');
			psdotsBuffer.append(PEToolKit.doubleToString(y));
		}

		x = set.getAttribute(POLYDOTS_ANGLE);
		if (x!=defaultAttributes.getAttribute(POLYDOTS_ANGLE)){
			if (psdotsBuffer.length()>0) psdotsBuffer.append(',');
			psdotsBuffer.append("dotangle=");
			psdotsBuffer.append(PEToolKit.doubleToString(x));
		}

		if (psdotsBuffer.length()>0){
			if (paramStr.getParameterBuffer().length()>0) buf.append(',');
			buf.append(psdotsBuffer);
		}

		// close parameters string :
		buf.append(']');

		// no arrow for \\psdots

		// append coordinates :
		int segIdx;
		for (segIdx = 0; segIdx < element.getSegmentCount(); segIdx++) {
			buf.append(element.getBezierPt(element.segmentToPointIndex(segIdx,SUBDIVISION_POINT), null));
			if (segIdx%10==0) buf.append(factory.getLineSeparator()); // hard wrap
		}
		if (!element.isClosed())
			buf.append(element.getBezierPt(element.segmentToPointIndex(segIdx,SUBDIVISION_POINT), null));
		buf.append(factory.getLineSeparator());
		return buf.toString();
	}
}
