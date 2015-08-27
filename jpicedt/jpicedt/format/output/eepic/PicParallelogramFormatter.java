/*
 PicParallelogramFormatter.java - August 29, 2003 - jPicEdt, a picture editor for LaTeX.
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

/**
 * Formats a PicParallelogram to the eepic package, using \\path macros, with appropriate filling if any,
 * or \\dashline macros if dash is positive.
 * @author Sylvain Reynal
 * @since jpicedt 1.4
 * @version $Id: PicParallelogramFormatter.java,v 1.9 2013/03/27 07:10:25 vincentb1 Exp $
 * <p>
 */
public class PicParallelogramFormatter extends AbstractFormatter {

	/** the Element this formater acts upon */
	protected PicParallelogram element;
	/** the producing factory */
	protected EepicFormatter factory;

   /**
	* @since jPicEdt 1.6
	*/
	public Element getElement(){ return element;}

	public PicParallelogramFormatter(PicParallelogram element, EepicFormatter factory){
		this.element = element;
		this.factory=factory;
	}

	public String format(){

		StringBuffer buf = new StringBuffer(100);
		PicAttributeSet set = element.getAttributeSet();

		PicPoint ptTR = element.getCtrlPt(PicParallelogram.P_TR,null);
		PicPoint ptBL = element.getCtrlPt(PicParallelogram.P_BL,null);
		PicPoint ptTL = element.getCtrlPt(PicParallelogram.P_TL,null);
		PicPoint ptBR = element.getCtrlPt(PicParallelogram.P_BR,null);

		factory.appendThicknessString(buf,element);

		if (set.getAttribute(FILL_STYLE)==FillStyle.SOLID){
			Color fillColor = set.getAttribute(FILL_COLOR);
			if (fillColor.equals(Color.WHITE))
				buf.append("\\whiten");
			else if (fillColor.equals(Color.BLACK))
				buf.append("\\blacken");
			else
				buf.append("\\shade"); // SHADE
			buf.append("\\path");
			buf.append(ptBL);
			buf.append(ptBR);
			buf.append(ptTR);
			buf.append(ptTL);
			buf.append(ptBL);
			buf.append(factory.getLineSeparator());
		}

		// else non-filled, and we handle the non-zero dash case as well :
		else {
			float dash = (set.getAttribute(DASH_OPAQUE).floatValue() + set.getAttribute(DASH_TRANSPARENT).floatValue())/2.0f;
			if (set.getAttribute(LINE_STYLE)!=LineStyle.DASHED) dash=0;// [pending] add support for black/white dash + dotted
			if (dash <= 0){
				buf.append("\\path");
				buf.append(ptBL);
				buf.append(ptBR);
				buf.append(ptTR);
				buf.append(ptTL);
				buf.append(ptBL);
				buf.append(factory.getLineSeparator());
			}
			else {
				factory.appendDashLine(buf, ptBL, ptBR, dash);
				factory.appendDashLine(buf, ptBR, ptTR, dash);
				factory.appendDashLine(buf, ptTR, ptTL, dash);
				factory.appendDashLine(buf, ptTL, ptBL, dash);
			}
		}
		buf.append(factory.getLineSeparator());
		return buf.toString();
	}

}
