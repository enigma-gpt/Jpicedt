/*  jPicEdt, a picture editor for LaTeX.
    Copyright (C) 1999-2006  Sylvain Reynal
*/
// Author: Sylvain Reynal
//         D�partement de Physique
//         �cole Nationale Sup�rieure de l'�lectronique et de ses Applications (ENSEA)
//         6, avenue du Ponceau
//         95014 CERGY CEDEX
//         FRANCE
//
// Tel : +33 130 736 245
// Fax : +33 130 736 667
// e-mail : reynal@ensea.fr
// Version: $Id: PicParallelogramFormatter.java,v 1.11 2013/03/27 07:09:20 vincentb1 Exp $
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

package jpicedt.format.output.pstricks;

import jpicedt.graphic.io.formatter.*;
import jpicedt.graphic.*;
import jpicedt.graphic.model.*;

import java.awt.*;

import static jpicedt.format.output.pstricks.PstricksConstants.*;
import static jpicedt.graphic.model.StyleConstants.*;
import static jpicedt.graphic.model.PicAttributeName.*;


/**
 * Pstricks formatter for PicParallelogram paraects.
 * @author Vincent Guirardel, Sylvain Reynal
 * @since jpicedt 1.4
 * @version $Id: PicParallelogramFormatter.java,v 1.11 2013/03/27 07:09:20 vincentb1 Exp $
 */
public class PicParallelogramFormatter extends AbstractFormatter {

	/** the Element this formatter acts upon */
	private PicParallelogram para;
	/** the producing factory */
	private PstricksFormatter factory;

	public Element getElement(){ return para; }

	public PicParallelogramFormatter(PicParallelogram para, PstricksFormatter factory){
		this.para = para;
		this.factory=factory;
	}

	/**
	 * \\psframe[param](x1,y1)(x2,y2) => not used anymore [SR:pending]<br>
	 * \\pspolygon[param](x1,y1)...(x4,y4)
	 */
	public String format(){
		StringBuffer buf = new StringBuffer(100); // 100 as initial capacity seems to be a good guess
		// first handle possibly user-defined colours
		PstricksFormatter.ParameterString paramStr = factory.createParameterString(para);
		if (paramStr.isDefinedColourString()) buf.append(paramStr.getUserDefinedColourBuffer());

		// pspolygon
		buf.append("\\pspolygon");
		buf.append("[");
		buf.append(paramStr.getParameterBuffer());
		buf.append("]");
		buf.append(para.getCtrlPt(PicParallelogram.P_BL,null));
		buf.append(para.getCtrlPt(PicParallelogram.P_BR,null));
		buf.append(para.getCtrlPt(PicParallelogram.P_TR,null));
		buf.append(para.getCtrlPt(PicParallelogram.P_TL,null));
		buf.append(factory.getLineSeparator());

		return buf.toString();
	}

	/*
		// psframe
		buf.append("\\psframe");
		buf.append("[");
		buf.append(paramStr.getParameterBuffer());
		buf.append("]");
		buf.append(para.getCtrlPt(PicRectangle.P_BL,null));
		buf.append(para.getCtrlPt(PicRectangle.P_TR,null));
	*/


}
