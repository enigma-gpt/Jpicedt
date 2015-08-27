/*  jPicEdt, a picture editor for LaTeX.
    Copyright (C) 1999-2006  Sylvain Reynal
*/
// Author: Sylvain Reynal
//         Département de Physique
//         École Nationale Supérieure de l'Électronique et de ses Applications (ENSEA)
//         6, avenue du Ponceau
//         95014 CERGY CEDEX
//         FRANCE
//
// Tel : +33 130 736 245
// Fax : +33 130 736 667
// e-mail : reynal@ensea.fr
// Version: $Id: PicTextFormatter.java,v 1.14 2013/03/27 07:23:15 vincentb1 Exp $
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

import jpicedt.graphic.io.formatter.*;
import jpicedt.graphic.*;
import jpicedt.graphic.model.*;

import java.awt.*;
import java.io.StringWriter;

import static jpicedt.format.output.pstricks.PstricksConstants.*;
import static jpicedt.graphic.model.StyleConstants.*;
import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.PicText.*;

/**
 * A formatter for PicText objects in the PsTricks format :<br>
 * \\rput[refpoint]{rotation}(x,y){stuff} <br>
 * where : <br>
 * <ul>
 * <li>stuff can be any of the following :<br>
 * \\psframebox[param]{text}}<br>
 * \\pscirclebox[param]{text}}<br>
 * \\psovalbox[param]{text}}<br>
 * <li>refpoint = B|b|t for vertical and l|r for horizontal (B = baseline)
 * <li>rotation = any angle in degree or U,L,D,R,N,W,S or E.
 * </ul>
 * <p>
 * The following are built from "frame", "oval" or "circle", but are allegedly more efficient :<br>
 *	\\psdblframebox[param]{stuff} % same as psframebox with doubleline=true<br>
 *	\\psshadowbox[param]{stuff} % same as psframebox with shadow=true<br>
 *	\\cput[shadow=true](x,y){a cput with a shadow} % same as pscirclebox, yet more efficient<br>
 * We don't use them so far.
 * @author Sylvain Reynal
 * @version $Id: PicTextFormatter.java,v 1.14 2013/03/27 07:23:15 vincentb1 Exp $
 */
public class PicTextFormatter extends AbstractFormatter {

	/** the Element this formatter acts upon */
	private PicText obj;
	private PstricksFormatter factory;

	public Element getElement(){ return obj; }

	/**
	 *
	 */
	public PicTextFormatter(PicText obj, PstricksFormatter factory){
		this.obj = obj;
		this.factory=factory;
	}

	/**
	 * @return a String representing this Element in the PsTricks
	 */
	public String format(){
		try
		{

			StringWriter buf = new StringWriter(100);

			// first handle possibly user-defined colours
			PstricksFormatter.ParameterString paramStr = factory.createParameterString(obj);
			if (paramStr.isDefinedColourString()) buf.append(paramStr.getUserDefinedColourBuffer());

			if (obj.isFramed()){
				// line-thickness correction
				//we need to \rput this further to take care of the linewidth because pstricks does align the frame on its outer boundary,
				// and not on the middle of the thickness of the frame
				// solution: without rotation, and aligned on bottom-left point, that would be \rput(-0.5*linethickness,-0.5*linethickness){ }
				// if alignment is on the right or top, signs should be adapted, and reset to 0 if aligned on center or baseline.
				// in case of rotation, we also have to rotate the vector (-0.5*linethickness,-0.5*linethickness).
				// Note that the following solution does not work:  \rput[l]{angle}(x,y){\rput(-0.5*linethickness,-0.5*linethickness){\psframebox{T}}}
				// because the inner rput sets the box size to zero and thus changes the refpoint position

				double sX=0; double sY=0; // for center or baseline
				switch (obj.getVertAlign()){
				case TOP: sY=1; break;
				case BOTTOM: sY=-1; break;
				}

				switch (obj.getHorAlign()){
				case LEFT: sX=-1; break;
				case RIGHT: sX=1; break;
				}

				double lw=0.5*((Double)(obj.getAttribute(LINE_WIDTH))).doubleValue();
				// rotate
				double angle = ((Double)obj.getAttribute(TEXT_ROTATION)).doubleValue(); // in degrees
				double c=Math.cos(Math.toRadians(angle));
				double s=Math.sin(Math.toRadians(angle));
				double dx=lw*(c*sX-s*sY);
				double dy=lw*(c*sY+s*sX);
				buf.write("\\rput(" + PEToolKit.doubleToString(dx)
						   + ","+  PEToolKit.doubleToString(dy) +")");
				buf.write("{");

				//end of linethickness correction
			}

			// rput command : \rput[refpoint]{rotation}(x,y){stuff}

			// \\rput
			buf.write("\\rput");
			if (obj.getVertAlign()!= VertAlign.CENTER || obj.getHorAlign()!= HorAlign.CENTER){
				buf.write("[");
				// [refpoint]
				switch (obj.getVertAlign()){
				case TOP: buf.write("t");break;
				case BOTTOM: buf.write("b"); break;
				case BASELINE: buf.write("B"); break;
				default:
				}

				switch (obj.getHorAlign()){
				case LEFT: buf.write("l"); break;
				case RIGHT: buf.write("r"); break;
				default:
				}

				buf.write("]");
			}

			// {rotation} [pending] todo
			double angle = ((Double)obj.getAttribute(TEXT_ROTATION)).doubleValue(); // in degrees
			if (angle!=0) {
				buf.write('{');
				buf.write(PEToolKit.doubleToString(angle));
				buf.write('}');
			}

			// (x,y)
			buf.write(obj.getCtrlPt(PicText.P_ANCHOR,null).toString());

			// {stuff}
			buf.write("{");
			if (obj.getFrameType()==FrameStyle.NO_FRAME) { // stuff = text
				factory.textWriteMultiLine(buf,obj);
			}
			else { // stuff = psframebox or psovalbox or pscirclebox

				switch (obj.getFrameType()){
				case CIRCLE: buf.write("\\pscirclebox"); break;
				case OVAL: buf.write("\\psovalbox"); break;
				default: buf.write("\\psframebox");
				}
				// parameters
				buf.write("[");
				buf.append(paramStr.getParameterBuffer());
				buf.write("]");
				buf.write("{");
				factory.textWriteMultiLine(buf,obj);
				buf.write("}");
				// closing line-thickness correction:
				buf.write("}");
			}
			buf.write("}");buf.write(factory.getLineSeparator());
			return buf.toString();
		}
		catch(java.io.IOException ioEx)
		{
			System.err.println("Error formatting '" + obj.toString() + "':");
			ioEx.printStackTrace();
			return null;
		}
	}

}
