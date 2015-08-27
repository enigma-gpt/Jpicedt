/*
 PicParallelogramFormatter.java - August 29, 2003 - jPicEdt, a picture editor for LaTeX.
 Copyright (C) 1999-2006 Sylvain Reynal

 D�partement de Physique
 �cole Nationale Sup�rieure de l'�lectronique et de ses Applications (ENSEA)
 6, avenue du Ponceau
 F-95014 CERGY CEDEX

 Tel : +33 130 736 245
 Fax : +33 130 736 667
 e-mail : reynal@ensea.fr
 jPicEdt web page : http://www.jpicedt.org/

*/
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
