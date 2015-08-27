// PicEllipseFormatter.java --- -*- coding: iso-8859-1 -*-
// August 30, 2003 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2013 Sylvain Reynal
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
// Version: $Id: PicEllipseFormatter.java,v 1.24 2013/08/05 19:21:36 vincentb1 Exp $
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
import java.awt.geom.*;

import static jpicedt.format.output.pstricks.PstricksConstants.*;
import static jpicedt.graphic.model.StyleConstants.*;
import static jpicedt.graphic.model.PicAttributeName.*;
import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.atan2;
import static java.lang.Math.abs;


/**
 * Formats a PicEllipse using PsTricks macros, including pstricks-add's \\psellipticarc if applicable.
 * [SR:pending] arrows for flat ellipse
 * @author Vincent Guirardel, Sylvain Reynal
 * @since jpicedt 1.4
 * @version $Id: PicEllipseFormatter.java,v 1.24 2013/08/05 19:21:36 vincentb1 Exp $
 *
 */
public class PicEllipseFormatter extends AbstractFormatter {

	private PicEllipse ellipse;
	private PstricksFormatter factory;

   /**
	* @since jPicEdt 1.6
	*/
	public Element getElement(){ return ellipse;}

	public PicEllipseFormatter(PicEllipse ellipse,PstricksFormatter factory){
		this.ellipse = ellipse;
		this.factory=factory;
	}

	// if true, this changes the way rotation are formatted
	private boolean isInsidePSCustom(){
		BranchElement parent = ellipse.getParent();
		if (parent == null)
			return false;
		else {
			BranchElement.CompoundMode cm = parent.getCompoundMode();
			if (cm == BranchElement.CompoundMode.JOINT)
				return true;
		}
		return false;
	}

	/**
	 * \\rput{rotation}(centerX,centerY){\\psellipse[param](0,0)(greatAxis/2,smallAxis/2)} => plain ellipse<br>
	 * \\rput{rotationAngle}(centerX,centerY){\qline(min,0)(max,0)} => flat ellipse
	 */
	public String format(){

		StringBuffer bufAngleCorrection = null;
		String angles =null;
		String correctedAngles = null;
		StringBuffer buf = new StringBuffer(100); // 100 as initial capacity seems to be a good guess
		PicAttributeSet set = ellipse.getAttributeSet();

		// first handle possibly user-defined colours
		PstricksFormatter.ParameterString paramStr = factory.createParameterString(ellipse);
		if (paramStr.isDefinedColourString()) buf.append(paramStr.getUserDefinedColourBuffer());

		boolean isPSCustom = isInsidePSCustom();
		double rotAngle = toDegrees(ellipse.getRotationAngle());
		PicPoint ptCenter = ellipse.getCtrlPt(PicEllipse.P_CENTER,null);

		// -------------- prolog ------------------------
		if (isPSCustom){
			// center
			if (ptCenter.x != 0 || ptCenter.y != 0){
				buf.append("\\translate");
				buf.append(ptCenter);
			}

			if (rotAngle != 0){
				buf.append("\\rotate{");
				buf.append(PEToolKit.doubleToString(rotAngle));
				buf.append("}");
			}
		}
		else {
			buf.append("\\rput{");
			buf.append(PEToolKit.doubleToString(toDegrees(ellipse.getRotationAngle())));
			buf.append("}");

			// center
			buf.append(ellipse.getCtrlPt(PicEllipse.P_CENTER,null));

			buf.append("{");
		}

		// -------------- plain ellipse -----------------
		if (ellipse.isPlain() && ellipse.getArcType()!=Arc2D.CHORD){
			// [SR:pending] why do we exclude CHORD here ???
			//  \\rput{rotationAngle}(centerX,centerY){ \\psellipse[param](0,0)(greatAxis/2,smallAxis/2)

			buf.append("\\psellipse");
			// parameters
			buf.append("[");
			buf.append(paramStr.getParameterBuffer());
			buf.append("]");
			buf.append("(0,0)");
			// greatAxis
			buf.append("(");
			buf.append(PEToolKit.doubleToString(ellipse.getGreatAxisLength()/2));
			buf.append(",");
			// smallAxis
			buf.append(PEToolKit.doubleToString(ellipse.getSmallAxisLength()/2));
			buf.append(")");

		} // plain ellipse

		// ------------------ flat ellipse -----------------------
		else if (ellipse.isFlat()){ // [pending: SR] This CAN'T be inside a pscustom, see doc!
			// draw a line:  \\rput{rotationAngle}(centerX,centerY){\qline(min,0)(max,0)}
			double start=ellipse.getRotatedAngleStart();//degrees
			double end=ellipse.getRotatedAngleEnd();//degrees
			double min=min(cos(toRadians(start)),cos(toRadians(end)));
			double max=max(cos(toRadians(start)),cos(toRadians(end)));
			if ( ( start<0 && end >0) || (end >360 )) max=1;
			if ( end >180) min=-1;
			if (ellipse.getArcType()==Arc2D.PIE){//take center into account in this case
				if (min>0) min=0;
				if (max<0) max=0;
			}
			min *= ellipse.getGreatAxisLength();
			max *= ellipse.getGreatAxisLength();

			//the line
			buf.append("\\qline(");
			buf.append(PEToolKit.doubleToString(min));
			buf.append(",0)(");
			buf.append(PEToolKit.doubleToString(max));
			buf.append(",0)");
			//[pending]: arrows !
		} // flat ellpise

		// ------------------- arc (flat case excluded) --------------------
		else {//case of an arc (chord/open/pie). Flat case excluded.
			// OPEN:  \\rput{rotationAngle}(centerX,centerY){\psellipticarc{<->}[param](0,0)(greatAxis,smallAxis){angle1}{angle2}}
			// PIE:  \\rput{rotationAngle}(centerX,centerY){\pscustom[param]{\psellipticarc(0,0)(greatAxis,smallAxis){angle1}{angle2}\lineto(0,0)\closepath}}
			// CHORD:  \\rput{rotationAngle}(centerX,centerY){\pscustom[param]{\psellipticarc(0,0)(greatAxis,smallAxis){angle1}{angle2}\closepath}}

			// Docs: see http://www.perce.de/LaTeX/pstricks-add/

			boolean clockwisewedge = false;

			// CHORD => use pscustom so as to handle filling, since chord=arc+close_path !
			if (ellipse.getArcType()==Arc2D.CHORD) {
				buf.append("\\pscustom");
				//[parameters]
				buf.append("[");
				buf.append(paramStr.getParameterBuffer());
				buf.append("]");
				buf.append("{\\psellipticarc");
				if(ellipse.getSmallAxisLength() < 0)
					buf.append("n");
			}
			// else for open arcs and pies, [parameters] will be included as part of the corresponding command, i.e.
			// * OPEN:
			// \\psellipticarc[<param>]{<arrows>}(<center>)(a,b){start-angle}{end-angle}
			// * PIE:
			// \\psellipticwedge[<param>](<center>)(a,b){start-angle}{end-angle}
			else {
				if (ellipse.getArcType()==Arc2D.PIE){
					buf.append("\\psellipticwedge");
					clockwisewedge = ellipse.getSmallAxisLength() < 0;
				}
				else {
					buf.append("\\psellipticarc"); // Arc2D.OPEN
					if(ellipse.getSmallAxisLength() < 0)
						buf.append("n");
				}

				//[parameters]+arrows
				buf.append("[");
				buf.append(paramStr.getParameterBuffer());
				buf.append("]");
				if (ellipse.getArcType()==Arc2D.OPEN)
					buf.append(PstricksUtilities.createPstricksStringFromArrows(ellipse));
			}

			buf.append("(0,0)");
			buf.append("(");
			buf.append(PEToolKit.doubleToString(0.5*ellipse.getGreatAxisLength()));
			buf.append(",");
			if(clockwisewedge)
				buf.append(PEToolKit.doubleToString(0.5*ellipse.getSmallAxisLength()));
			else
				buf.append(PEToolKit.doubleToString(0.5*abs(ellipse.getSmallAxisLength())));
			buf.append(")");
			buf.append("{");

			double start=ellipse.getRotatedAngleStart();//degrees
			double end=ellipse.getRotatedAngleEnd();//degrees
			if(ellipse.getSmallAxisLength() < 0 && !clockwisewedge){
				// intervertir les angles au cas ou \psellipticarcn est utilisé
				double temp = 180+start;
				start = 180+end;
				end = temp;
			}

			if(start >= 360 || end >= 360){
				start -= 360;
				end -= 360;
			}

			double correctedStart, correctedEnd;


			if(factory.getCustomProperties().getAngleCorrection()
			   == PstricksAngleCorrection.ANGLE_CORRECTION_BY_JPICEDT
				|| factory.getCustomProperties().getAngleCorrection()
			   == PstricksAngleCorrection.ANGLE_CORRECTION_BY_JPICEDT_AUTO){
				double ga = ellipse.getGreatAxisLength();
				double ssa = ellipse.getSmallAxisLength();
				double sa = abs(ssa);

				correctedStart = toDegrees(ellipse.getCorrectedAngleStart());
				correctedEnd = toDegrees(ellipse.getCorrectedAngleEnd());
				if(clockwisewedge){
					correctedStart = -correctedStart;
					correctedEnd = -correctedEnd;
				}
//				else
//				{
//					correctedStart = 180-correctedStart;
//					correctedEnd = 180-correctedEnd;
//				}

				if(factory.getCustomProperties().getAngleCorrection()
				   == PstricksAngleCorrection.ANGLE_CORRECTION_BY_JPICEDT){
					start = correctedStart;
					end = correctedEnd;
				}
				else
				{
					buf.append("#1}{#2}");
					bufAngleCorrection = new StringBuffer(50);
					bufAngleCorrection.append(PEToolKit.doubleToString(start));
					bufAngleCorrection.append("}{");
					bufAngleCorrection.append(PEToolKit.doubleToString(end));
					angles = bufAngleCorrection.toString();
					bufAngleCorrection = new StringBuffer(50);
					bufAngleCorrection.append(PEToolKit.doubleToString(correctedStart));
					bufAngleCorrection.append("}{");
					bufAngleCorrection.append(PEToolKit.doubleToString(correctedEnd));
					correctedAngles = bufAngleCorrection.toString();
					bufAngleCorrection = new StringBuffer(buf.length() + 50);
					bufAngleCorrection.append("\\makeatletter\\def\\@tempa#1#2{%");
					bufAngleCorrection.append(factory.getLineSeparator());
				}
			}

			if(bufAngleCorrection == null){
				buf.append(PEToolKit.doubleToString(start));
				buf.append("}{");
				buf.append(PEToolKit.doubleToString(end));
				buf.append("}");
			}
			if (ellipse.getArcType()==Arc2D.CHORD)
				buf.append("\\closepath}");
			// Note: PIE is now handled by psellipticwedge
		}

		// ------------------- epilog --------------------

		if (isPSCustom){ // brings coord system back to its initial value
			if (rotAngle != 0){
				buf.append("\\rotate{");
				buf.append(PEToolKit.doubleToString(-rotAngle));
				buf.append("}");
			}
			if (ptCenter.x != 0 || ptCenter.y != 0){
				buf.append("\\translate");
				buf.append(ptCenter.scale(0.0,0.0,-1.0));
			}
		}
		else { // close \\pscustom
			buf.append("}");
		}
		buf.append(factory.getLineSeparator());

		if(bufAngleCorrection != null){
			bufAngleCorrection.append(buf);
			bufAngleCorrection.append("}\\@ifundefined{Pst@correctAnglefalse}{\\@tempa{");
			bufAngleCorrection.append(angles);
			bufAngleCorrection.append("}}{\\@tempa{");
			bufAngleCorrection.append(correctedAngles);
			bufAngleCorrection.append("}}\\makeatother");
			bufAngleCorrection.append(factory.getLineSeparator());
			buf = bufAngleCorrection;
		}

		return buf.toString();

	}


}
