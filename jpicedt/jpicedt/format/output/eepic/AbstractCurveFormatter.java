/*
 AbstractCurveFormatter.java - August 29, 2003 - jPicEdt, a picture editor for LaTeX.
 Copyright (C) 1999-2006 Sylvain Reynal

 Département de Physique
 École Nationale Supérieure de l'Électronique et de ses Applications (ENSEA)
 6, avenue du Ponceau
 F-95014 CERGY CEDEX

 Tel : +33 130 736 245
 Fax : +33 130 736 667
 e-mail : reynal@ensea.fr
 jPicEdt web page : http://www.jpicedt.org/

*/
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

package jpicedt.format.output.eepic;

import jpicedt.graphic.*;
import jpicedt.graphic.model.*;
import jpicedt.graphic.io.formatter.Formatter;
import jpicedt.graphic.io.formatter.AbstractFormatter;
import jpicedt.graphic.PicPoint;

import java.awt.*;

import static jpicedt.format.output.eepic.EepicConstants.*;
import static jpicedt.graphic.model.StyleConstants.*;
import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.AbstractCurve.PointType.*;

/**
 * Format an AbstractCurve, or any subclass of it, to the eepic format.
 * Basically, a single \\path macro is used if the curve is a polygon, so that filling works properly. Otherwise we make use
 * of \\qbezier, \\path and/or \\dashline macros.
 * @author Sylvain Reynal
 * @since jpicedt 1.4
 * @version $Id: AbstractCurveFormatter.java,v 1.11 2013/03/27 07:11:05 vincentb1 Exp $
 * <p>
 *
 */
public class AbstractCurveFormatter extends AbstractFormatter {

	/** the Element this formater acts upon */
	protected AbstractCurve curve;
	protected EepicFormatter factory;

   /**
	* @since jPicEdt 1.6
	*/
	public Element getElement(){ return curve;}

	/** */
	public AbstractCurveFormatter(AbstractCurve curve, EepicFormatter factory){
		this.curve = curve;
		this.factory=factory;
	}

	/**
	 * Returns a string representing this AbstractCurve using \\line and \\qbezier LaTeX macros only.
	 * Dashed style is allowed if all segments are straight, since \\qbezier does not support this style.
	 */
	public String format(){
		StringBuffer buf = new StringBuffer(100); // 100 char as initial capacity seems a good guess
		if (curve.isPolygon()) return formatPolygon(buf);
		else return formatCurve(buf);
	}

	/**
	 * formats curves containing at least one non-straight segment
	 */
	protected String formatCurve(StringBuffer buf){


		PicAttributeSet set = curve.getAttributeSet();

		factory.appendThicknessString(buf,curve);
		final double dash=0; // qbezier are not dashable

		// for each segment :
		// - straight : append \\path
		// - curved : append \\qbezier command, after converting cubic to two quads (see PEToolKit)
		// If curve is open, first and last segment deserve special processing as for arrows
		for (int segIdx=0; segIdx < curve.getSegmentCount(); segIdx++){
			PicPoint p1 = curve.getBezierPt(curve.segmentToPointIndex(segIdx, SUBDIVISION_POINT),null);
			PicPoint p2 = curve.getBezierPt(curve.segmentToPointIndex(segIdx+1, SUBDIVISION_POINT),null);
			if (curve.isStraight(segIdx)){
				ArrowStyle arrowL=ArrowStyle.NONE;
				ArrowStyle arrowR=ArrowStyle.NONE;
				if (!curve.isClosed()){
					if (segIdx==0)
						arrowL = curve.getAttribute(LEFT_ARROW);
					if (segIdx==curve.getSegmentCount()-1)
						arrowR = curve.getAttribute(RIGHT_ARROW);
				}
				if (segIdx > 0 && !curve.isStraight(segIdx-1)) {
					buf.append(factory.getLineSeparator());
					buf.append("\\path");
					buf.append(p1);
				}
				buf.append(p2);
			}
			else {
				PicPoint pCtrl1 = curve.getBezierPt(curve.segmentToPointIndex(segIdx, FIRST_SEGMENT_CONTROL_POINT),null);
				PicPoint pCtrl2 = curve.getBezierPt(curve.segmentToPointIndex(segIdx, SECOND_SEGMENT_CONTROL_POINT),null);
				PicPoint[] quadsPts = PEToolKit.convertCubicBezierToQuad(p1,pCtrl1,pCtrl2,p2); // five points = two joined quads
				buf.append(factory.getLineSeparator());
				buf.append("\\qbezier"); // first quad
				buf.append(quadsPts[0]);buf.append(quadsPts[1]);buf.append(quadsPts[2]);
				buf.append(factory.getLineSeparator());
				buf.append("\\qbezier"); // second quad
				buf.append(quadsPts[2]);buf.append(quadsPts[3]);buf.append(quadsPts[4]);
				buf.append(factory.getLineSeparator());
				if (!curve.isClosed()){
					if (segIdx==0 && curve.getAttribute(LEFT_ARROW) != ArrowStyle.NONE){
						PicVector dir = new PicVector(pCtrl1,p1).normalize();
						buf.append(factory.arrowToLatexString(p1, dir));
					}
					if (segIdx==curve.getSegmentCount()-1 && curve.getAttribute(RIGHT_ARROW) != ArrowStyle.NONE){
						PicVector dir = new PicVector(pCtrl2,p2).normalize();
						buf.append(factory.arrowToLatexString(p2, dir));
					}
				}
			}
		}
		buf.append(factory.getLineSeparator());
		return buf.toString();
	}

	/**
	  * Formats curves containing straight segments only
	  */
	protected String formatPolygon(StringBuffer buf){

		factory.appendThicknessString(buf,curve);

		PicAttributeSet set = curve.getAttributeSet();

		// there are only straight segments, hence we can afford dashed lines :
		double dash = (curve.getAttribute(DASH_OPAQUE) + curve.getAttribute(DASH_TRANSPARENT))/2.0;
		if (set.getAttribute(LINE_STYLE)!=LineStyle.DASHED) dash=0;// [pending] add support for black/white dash + dotted

		// eepic commands
		int lastPt = curve.getLastPointIndex();
		if (curve.getAttribute(FILL_STYLE)==FillStyle.SOLID){
			Color fillColor = curve.getAttribute(FILL_COLOR);
			if (fillColor.equals(Color.WHITE))
				buf.append("\\whiten");
			else if (fillColor.equals(Color.BLACK))
				buf.append("\\blacken");
			else
				buf.append("\\shade"); // SHADE
			buf.append("\\path"); // create a closed path so that it can be filled correctly
			for (int segIdx=0; segIdx<=curve.getSegmentCount(); segIdx++){
				PicPoint pt = curve.getBezierPt(curve.segmentToPointIndex(segIdx, SUBDIVISION_POINT),null);
				buf.append(pt);
			}
			//buf.append(curve.getBezierPt(0,null)); // close path
			buf.append(factory.getLineSeparator());
		}
		else {	 // not filled
			if (dash <= 0){
				buf.append("\\path");
				for (int segIdx=0; segIdx<=curve.getSegmentCount(); segIdx++){
					PicPoint pt = curve.getBezierPt(curve.segmentToPointIndex(segIdx, SUBDIVISION_POINT),null);
					buf.append(pt);
				}
				//if (curve.isClosed()) buf.append(curve.getBezierPt(0,null)); // close path
				buf.append(factory.getLineSeparator());
			}
			else {
				String dashStr = PEToolKit.doubleToString(dash);
				for (int segIdx=0; segIdx<curve.getSegmentCount(); segIdx++){
					buf.append("\\dashline{");
					buf.append(dashStr);
					buf.append("}");
					PicPoint p1 = curve.getBezierPt(curve.segmentToPointIndex(segIdx, SUBDIVISION_POINT),null);
					PicPoint p2 = curve.getBezierPt(curve.segmentToPointIndex(segIdx+1, SUBDIVISION_POINT),null);
					buf.append(p1);
					buf.append(p2);
					buf.append(factory.getLineSeparator());
				}
			}
		}
		// add arrows if not closed
		if (!curve.isClosed()){
			// first arrow
			if (set.getAttribute(LEFT_ARROW)!=ArrowStyle.NONE){
				PicPoint p0 = curve.getBezierPt(curve.segmentToPointIndex(0, SUBDIVISION_POINT),null);
				PicPoint p1 = curve.getBezierPt(curve.segmentToPointIndex(1, SUBDIVISION_POINT),null);
				PicVector dir = new PicVector(p1,p0).normalize();
				buf.append(factory.arrowToLatexString(p0, dir));
				buf.append(factory.getLineSeparator());
			}
			// second arrow
			if (set.getAttribute(RIGHT_ARROW)!=ArrowStyle.NONE){
				int lastSegIdx = curve.getSegmentCount()-1;
				PicPoint p1 = curve.getBezierPt(curve.segmentToPointIndex(lastSegIdx, SUBDIVISION_POINT),null);
				PicPoint p0 = curve.getBezierPt(curve.segmentToPointIndex(lastSegIdx+1, SUBDIVISION_POINT),null);
				PicVector dir = new PicVector(p1,p0).normalize();
				buf.append(factory.arrowToLatexString(p0, dir));
				buf.append(factory.getLineSeparator());
			}
		}
		buf.append(factory.getLineSeparator());
		return buf.toString();
	}

}
