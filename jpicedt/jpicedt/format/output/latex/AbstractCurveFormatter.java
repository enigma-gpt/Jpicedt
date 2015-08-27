// AbstractCurveFormatter.java --- -*- coding: iso-8859-1 -*-
// August 29, 2003 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2006 Sylvain Reynal
// Copyright (C) 2007/2013 Sylvain Reynal, Vincent Bela�che
//
// Author: Sylvain Reynal
//         D�partement de Physique
//         �cole Nationale Sup�rieure de l'�lectronique et de ses Applications (�NS�A)
//         6, avenue du Ponceau
//         95014 CERGY CEDEX
//         FRANCE
//
//         Tel : +33 130 736 245
//         Fax : +33 130 736 667
//         e-mail : reynal@ensea.fr
//
// Version: $Id: AbstractCurveFormatter.java,v 1.12 2013/03/27 07:23:42 vincentb1 Exp $
// Keywords:
// X-URL: http://www.jpicedt.org/
//
// Ce logiciel est r�gi par la licence CeCILL soumise au droit fran�ais et respectant les principes de
// diffusion des logiciels libres. Vous pouvez utiliser, modifier et/ou redistribuer ce programme sous les
// conditions de la licence CeCILL telle que diffus�e par le CEA, le CNRS et l'INRIA sur le site
// "http://www.cecill.info".
//
// En contrepartie de l'accessibilit� au code source et des droits de copie, de modification et de
// redistribution accord�s par cette licence, il n'est offert aux utilisateurs qu'une garantie limit�e.  Pour
// les m�mes raisons, seule une responsabilit� restreinte p�se sur l'auteur du programme, le titulaire des
// droits patrimoniaux et les conc�dants successifs.
//
// � cet �gard l'attention de l'utilisateur est attir�e sur les risques associ�s au chargement, �
// l'utilisation, � la modification et/ou au d�veloppement et � la reproduction du logiciel par l'utilisateur
// �tant donn� sa sp�cificit� de logiciel libre, qui peut le rendre complexe � manipuler et qui le r�serve
// donc � des d�veloppeurs et des professionnels avertis poss�dant des connaissances informatiques
// approfondies.  Les utilisateurs sont donc invit�s � charger et tester l'ad�quation du logiciel � leurs
// besoins dans des conditions permettant d'assurer la s�curit� de leurs syst�mes et ou de leurs donn�es et,
// plus g�n�ralement, � l'utiliser et l'exploiter dans les m�mes conditions de s�curit�.
//
// Le fait que vous puissiez acc�der � cet en-t�te signifie que vous avez pris connaissance de la licence
// CeCILL, et que vous en avez accept� les termes.
//
/// Commentary:

//



/// Code:
package jpicedt.format.output.latex;

import jpicedt.graphic.*;
import jpicedt.graphic.model.*;
import jpicedt.graphic.io.formatter.Formatter;
import jpicedt.graphic.io.formatter.AbstractFormatter;
import jpicedt.graphic.PicPoint;

import java.awt.*;

import static jpicedt.format.output.latex.LatexConstants.*;
import static jpicedt.graphic.model.StyleConstants.LineStyle.*;
import static jpicedt.graphic.model.StyleConstants.*;
import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.AbstractCurve.PointType.*;


/**
 * Format an AbstractCurve, or any subclass of it, to the LaTeX-picture format.
 * [SR:pending] synchronize LatexViewFactory with new (un)supported attributes.
 * @author Sylvain Reynal
 * @since jpicedt 1.4
 * @version $Id: AbstractCurveFormatter.java,v 1.12 2013/03/27 07:23:42 vincentb1 Exp $
 * <p>
 *
 */
public class AbstractCurveFormatter extends AbstractFormatter {

	/** the Element this formater acts upon */
	protected AbstractCurve curve;
	protected LatexFormatter factory;

   /**
	* @since jPicEdt 1.6
	*/
	public Element getElement(){ return curve;}

	/** */
	public AbstractCurveFormatter(AbstractCurve curve, LatexFormatter factory){
		this.curve = curve;
		this.factory=factory;;
	}

	/**
	 * Returns a string representing this AbstractCurve using \line and \qbezier LaTeX macros only.
	 * Dashed style is allowed if all segments are straight, since \qbezier does not support this style.
	 */
	public String format(){

		StringBuffer buf = new StringBuffer(100); // 100 char as initial capacity seems a good guess
		PicAttributeSet set = curve.getAttributeSet();

		factory.appendThicknessString(buf,curve);

		// check if there are only straight segments, in which case we can afford dashed lines ; else only
		// solid style is supported by the  \\qbezier command :
		float dash = (curve.getAttribute(DASH_OPAQUE).floatValue() + curve.getAttribute(DASH_TRANSPARENT).floatValue())/2.0f;
		if (set.getAttribute(LINE_STYLE)!=DASHED || !curve.isPolygon()) dash=0;// [pending] add support for black/white dash + dotted

		// for each segment :
		// - straight : add \\line command
		// - curved : add \\qbezier command, after converting cubic to two quads (see PEToolKit)
		// If curve is open, first and last segment deserve special processing as for arrows
		int segIdx = 0;
		for(AbstractCurve.Segment segment : curve.getMiminalSegmentList())
		{
			if(segment instanceof AbstractCurve.LineToSegment)
			{
				AbstractCurve.LineToSegment lineToSegment = (AbstractCurve.LineToSegment)segment;
				ArrowStyle arrowL=ArrowStyle.NONE;
				ArrowStyle arrowR=ArrowStyle.NONE;
				if (!curve.isClosed()){
					if (segIdx==0)
						arrowL = set.getAttribute(LEFT_ARROW);
					if (segIdx==curve.getSegmentCount()-1)
						arrowR = set.getAttribute(RIGHT_ARROW);
				}
				buf.append(factory.lineToLatexString(lineToSegment.getFromPt(), lineToSegment.getToPt(),
													 arrowL,arrowR,dash));
				buf.append(factory.getLineSeparator());
				
			}
			else if(segment instanceof AbstractCurve.CurveToSegment)
			{
				AbstractCurve.CurveToSegment curveToSegment  = (AbstractCurve.CurveToSegment)segment;
				PicPoint p1 = curveToSegment.getFromPt();
				PicPoint p2 = curveToSegment.getToPt();
				PicPoint pCtrl1 = curveToSegment.getFromCtrlPt();
				PicPoint pCtrl2 = curveToSegment.getToCtrlPt();
				PicPoint[] quadsPts = PEToolKit.convertCubicBezierToQuad(p1,pCtrl1,pCtrl2,p2); // five points = two joined quads
				buf.append("\\qbezier"); // first quad
				buf.append(quadsPts[0]);buf.append(quadsPts[1]);buf.append(quadsPts[2]);
				buf.append(factory.getLineSeparator());
				buf.append("\\qbezier"); // second quad
				buf.append(quadsPts[2]);buf.append(quadsPts[3]);buf.append(quadsPts[4]);
				buf.append(factory.getLineSeparator());
				if (!curve.isClosed()){
					if (segIdx == 0 && set.getAttribute(LEFT_ARROW) != ArrowStyle.NONE){
						PicVector dir = curveToSegment.getToTangent().cInverse().normalize();
						buf.append(factory.arrowToLatexString(p1, dir));
						buf.append(factory.getLineSeparator());
					}
					if (segIdx == curve.getSegmentCount()-1 
						&& set.getAttribute(RIGHT_ARROW) != ArrowStyle.NONE)
					{
						PicVector dir = curveToSegment.getFromTangent().cInverse().normalize();
						buf.append(factory.arrowToLatexString(p2, dir));
						buf.append(factory.getLineSeparator());
					}
				}
			}
			++ segIdx;
		}
		return buf.toString();
	}

}
