// PicEllipseFormatter.java --- -*- coding: iso-8859-1 -*-
// August 30, 2003 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999-2006 Sylvain Reynal
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
// Version: $Id: PicEllipseFormatter.java,v 1.12 2013/03/27 07:10:30 vincentb1 Exp $
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
package jpicedt.format.output.eepic;

import jpicedt.graphic.*;
import jpicedt.graphic.model.*;
import jpicedt.graphic.io.formatter.*;
import jpicedt.graphic.PicPoint;

import java.awt.*;

import static jpicedt.format.output.eepic.EepicConstants.*;
import static jpicedt.graphic.model.StyleConstants.*;
import static jpicedt.graphic.model.PicAttributeName.*;

/**
 * Formats a PicEllipse using macros of the eepic package.
 * [SR:pending] handle rotated ellipses
 * @author Sylvain Reynal
 * @since jpicedt 1.3.2
 * @version $Id: PicEllipseFormatter.java,v 1.12 2013/03/27 07:10:30 vincentb1 Exp $
 *
 */
public class PicEllipseFormatter extends jpicedt.format.output.latex.PicEllipseFormatter  {

	// inherited : ellipse, factory

	public PicEllipseFormatter(PicEllipse ellipse,EepicFormatter factory){
		super(ellipse,factory); // init width and height
	}

	/**
	 * Returns a String representing this ellipse or arc using epic/eepic macros
	 */
	public String format(){

		StringBuffer buf = new StringBuffer(100); // 100 as initial capacity seems to be a good guess
		PicAttributeSet set = ellipse.getAttributeSet();

		factory.appendThicknessString(buf,ellipse);

		// plain circle/ellipse :
		if (ellipse.isPlain()){
			if (ellipse.isRotated()){ // emulated lines, not filled
				super.appendEmulatedArcString(buf);
			}
			else { // \\[filltype]\\ellipse{x-diam}{y-diam}
				buf.append("\\put");
				buf.append(ellipse.getCtrlPt(PicEllipse.P_CENTER,null));
				buf.append("{");
				if (ellipse.getAttribute(FILL_STYLE)==FillStyle.SOLID){
					Color fillColor = set.getAttribute(FILL_COLOR);
					if (fillColor.equals(Color.WHITE))
						buf.append("\\whiten");
					else if (fillColor.equals(Color.BLACK))
						buf.append("\\blacken");
					else
						buf.append("\\shade"); // SHADE
				}
				buf.append("\\ellipse{");
				double width = ellipse.getCtrlPt(PicEllipse.P_BL,null).distance(ellipse.getCtrlPt(PicEllipse.P_BR,null));
				buf.append(PEToolKit.doubleToString(width));
				buf.append("}{");
				double height = ellipse.getCtrlPt(PicEllipse.P_BR,null).distance(ellipse.getCtrlPt(PicEllipse.P_TR,null));
				buf.append(PEToolKit.doubleToString(height));
				buf.append("}}");buf.append(factory.getLineSeparator());
			}
		}

		// an arc
		else {
			// circular arc (can be filled) : [\\filltype]\\arc{diameter}{start angle}{end angle}
			// where : 0 < start-angle < 2 PI ; start-angle < end-angle < start-angle + 2 PI
			// and angles are counted CW and in radians.
			// Note that stroke is always painted black
			if (ellipse.isCircular()){
				// we've got to construct specific startAngleStr and endAngleStr strings
				// since eepic requests CLOCKWISE angles, in RADIAN units,
				// while we store this information COUNTERCLOCKWISE and in DEGREES in PicEllipse
				buf.append("\\put");
				buf.append(ellipse.getCtrlPt(PicEllipse.P_CENTER,null));
				buf.append("{");
				if (ellipse.getAttribute(FILL_STYLE)==FillStyle.SOLID){
					Color fillColor = set.getAttribute(FILL_COLOR);
					if (fillColor.equals(Color.WHITE))
						buf.append("\\whiten");
					else if (fillColor.equals(Color.BLACK))
						buf.append("\\blacken");
					else
						buf.append("\\shade"); // SHADE
				}
				buf.append("\\arc{"); // ! OPEN arcs only ! (and aka CHORD is filled)
				buf.append(PEToolKit.doubleToString(ellipse.getGreatAxisLength()));
				buf.append("}{");
				double startAngle, endAngle;
				if (ellipse.getSmallAxisLength()<0){
					startAngle = ellipse.getRotatedAngleStart()%360; // guaranteed to lie b/w -360 and 360
					endAngle = ellipse.getRotatedAngleEnd()%360;
				}
				else {
					startAngle = -ellipse.getRotatedAngleEnd()%360;
					endAngle = -ellipse.getRotatedAngleStart()%360;
				}
				if (startAngle < 0) startAngle += 360;
				while (endAngle < startAngle) endAngle += 360;
				double startAngleRad = Math.toRadians(startAngle);
				double endAngleRad = Math.toRadians(endAngle);
				buf.append(PEToolKit.doubleToString(startAngleRad));
				buf.append("}{");
				buf.append(PEToolKit.doubleToString(endAngleRad));
				buf.append("}}");buf.append(factory.getLineSeparator());

				// since filled \\arc = CHORD, we must add a white polygon to emulate a pie if angle-extend > 180
				// or a properly filled polygon if angle-extent < 180
				if (ellipse.getArcType()==PicEllipse.PIE){
					if (ellipse.getAttribute(FILL_STYLE) ==FillStyle.SOLID){
						double angleExtent = endAngle - startAngle; // guaranteed to be positive, in DEGREES
						if (angleExtent != 180){ // add a filled path
							if (angleExtent < 180){
								Color fillColor = set.getAttribute(FILL_COLOR);
								if (fillColor.equals(Color.WHITE))
									buf.append("\\whiten");
								else if (fillColor.equals(Color.BLACK))
									buf.append("\\blacken");
								else
									buf.append("\\shade"); // SHADE
							}
							else buf.append("\\whiten"); // assume bgColor=white
						}
						// else do nothing
					}
					buf.append("\\path");
					buf.append(ellipse.getCtrlPt(PicEllipse.P_ANGLE_START,null));
					buf.append(ellipse.getCtrlPt(PicEllipse.P_CENTER,null));
					buf.append(ellipse.getCtrlPt(PicEllipse.P_ANGLE_END,null));
					buf.append(factory.getLineSeparator());
				}


				// append chord or pie line if applicable
				else if (ellipse.getArcType()==PicEllipse.CHORD){
					buf.append("\\path");
					buf.append(ellipse.getCtrlPt(PicEllipse.P_ANGLE_START,null));
					buf.append(ellipse.getCtrlPt(PicEllipse.P_ANGLE_END,null));
					buf.append(factory.getLineSeparator());
				}
				// else OPEN: leave it as it is
			}
			// ELLIPTIC arc (CAN'T be filled) and we emulate it
			else super.appendEmulatedArcString(buf);
			// ARROW in the ARC case :
			appendArrows(buf);
		}
		buf.append(factory.getLineSeparator());
		return buf.toString();
	}
}
