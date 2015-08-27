// PicParallelogramFormatter.java --- -*- coding: iso-8859-1 -*-
// August 29, 2003 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999-2006 Sylvain Reynal
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
// Version: $Id: PicParallelogramFormatter.java,v 1.11 2013/07/21 12:54:03 vincentb1 Exp $
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
package jpicedt.format.output.latex;

import jpicedt.graphic.*;
import jpicedt.graphic.model.*;
import jpicedt.graphic.io.formatter.Formatter;
import jpicedt.graphic.io.formatter.AbstractFormatter;
import jpicedt.graphic.PicPoint;

import java.awt.*;
import static jpicedt.format.output.latex.LatexConstants.*;
import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.StyleConstants.*;

/**
 * Formats a PicParallelogram to the LaTeX-picture format.
 * @author Sylvain Reynal
 * @since jpicedt 1.4
 * @version $Id: PicParallelogramFormatter.java,v 1.11 2013/07/21 12:54:03 vincentb1 Exp $
 * <p>
 *
 */
public class PicParallelogramFormatter extends AbstractFormatter {

	/** the Element this formater acts upon */
	protected PicParallelogram element;
	/** the producing factory */
	protected LatexFormatter factory;

   /**
	* @since jPicEdt 1.6
	*/
	public Element getElement(){ return element;}

	public PicParallelogramFormatter(PicParallelogram element, LatexFormatter factory){
		this.element = element;
		this.factory=factory;
	}

	/**
	  * Depending on the parallelogram being rectangle or not, either use LaTeX's \\line command,
	  * or the \\rule command if the shape is to be filled.
	  * @return The LaTeX string corresponding to this object
	  */
	public String format(){

		StringBuffer buf = new StringBuffer(100);

		PicPoint ptTR = element.getCtrlPt(PicParallelogram.P_TR,null);
		PicPoint ptBL = element.getCtrlPt(PicParallelogram.P_BL,null);
		PicPoint ptTL = element.getCtrlPt(PicParallelogram.P_TL,null);
		PicPoint ptBR = element.getCtrlPt(PicParallelogram.P_BR,null);

		factory.appendThicknessString(buf,element);

		// LaTeX commands
		if(element.isXYorYXRectangle()){
			// on fait les réflexions nécessaires quand l'ordre des points l'exige
			// de sorte que BL soit vraiment en bas à gauche
			if(ptBL.getX() > ptBR.getX()){
				PicPoint temp = ptBL;
				ptBL = ptBR;
				ptBR = temp;
				temp = ptTL;
				ptTL = ptTR;
				ptTR = temp;
			}
			if(ptBL.getY() > ptTL.getY()){
				PicPoint temp = ptBL;
				ptBL = ptTL;
				ptTL = temp;
				temp = ptBR;
				ptBR = ptTR;
				ptTR = temp;
			}

			if(element.getAttribute(FILL_STYLE)==FillStyle.SOLID){
				// we use a LaTeX \\rule if it's a filled rectangle
				buf.append("\\put");
				buf.append(ptBL);
				buf.append("{\\rule{");
				buf.append(PEToolKit.doubleToString(ptBL.distance(ptBR))); // horizontal length
				buf.append("\\unitlength}{");
				buf.append(PEToolKit.doubleToString(ptBL.distance(ptTL))); // vertical length
				buf.append("\\unitlength}}");
			}
			else if(element.getAttribute(LINE_STYLE)==LineStyle.DASHED){
				 // [pending] support black/white dash
				double dash = (Double)(element.getAttribute(DASH_OPAQUE)).doubleValue();
				buf.append("\\put");
				buf.append(ptBL);
				buf.append("{\\dashbox{");
				buf.append(PEToolKit.doubleToString(dash));
				buf.append("}(");
				buf.append(PEToolKit.doubleToString(ptBL.distance(ptBR))); // horizontal length
				buf.append(',');
				buf.append(PEToolKit.doubleToString(ptBL.distance(ptTL))); // vertical length
				buf.append("){}}");
			}
			else{
				buf.append("\\put");
				buf.append(ptBL);
				buf.append("{\\framebox(");
				buf.append(PEToolKit.doubleToString(ptBL.distance(ptBR))); // horizontal length
				buf.append(',');
				buf.append(PEToolKit.doubleToString(ptBL.distance(ptTL))); // vertical length
				buf.append("){}}");
			}
		}
		// else we handle the non-zero dash case as well :
		else {
			double dash = element.getAttribute(LINE_STYLE)==LineStyle.DASHED ? ((Double)element.getAttribute(DASH_OPAQUE)).doubleValue() : 0; // [pending] support black/white dash
			buf.append(factory.lineToLatexString(ptBL.x, ptBL.y, ptBR.x, ptBR.y, ArrowStyle.NONE,ArrowStyle.NONE, dash));
			buf.append(factory.getLineSeparator());
			buf.append(factory.lineToLatexString(ptBL.x, ptBL.y, ptTL.x, ptTL.y, ArrowStyle.NONE,ArrowStyle.NONE, dash));
			buf.append(factory.getLineSeparator());
			buf.append(factory.lineToLatexString(ptBR.x, ptBR.y, ptTR.x, ptTR.y, ArrowStyle.NONE,ArrowStyle.NONE, dash));
			buf.append(factory.getLineSeparator());
			buf.append(factory.lineToLatexString(ptTL.x, ptTL.y, ptTR.x, ptTR.y, ArrowStyle.NONE,ArrowStyle.NONE, dash));
		}
		buf.append(factory.getLineSeparator());
		return buf.toString();
	}

}
