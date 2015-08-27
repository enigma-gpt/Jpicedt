// LineThicknessExpression.java --- -*- coding: iso-8859-1 -*-
// jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2013  Sylvain Reynal
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
// Version: $Id: LineThicknessExpression.java,v 1.9 2013/03/31 06:56:59 vincentb1 Exp $
// Keywords: parser
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
package jpicedt.format.input.latex;

import jpicedt.format.input.util.*;
import jpicedt.graphic.model.*;

import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.StyleConstants.*;
import static jpicedt.format.input.util.ExpressionConstants.*;


/**
 *	 Legal syntax :
 * <ul>
 * <li>	      \\linethickness{0.4pt}
 * <li>       \\linethickness{0.4mm}
 * <li>       \\linethickness{0.4cm}
 * <li>       \\linethickness{0.4} // default to mm
 * </ul>
 * note : 1pt = 1/72.27 inch = 0.3515 mm    cf. LaTeX Book (Leslie Lamport) p.192
 * @author Sylvain Reynal
 * @since jpicedt 1.3
 * @version $Id: LineThicknessExpression.java,v 1.9 2013/03/31 06:56:59 vincentb1 Exp $
 *
 */
public class LineThicknessExpression extends SequenceExpression   {

    private Pool pool;
    private double lineWidth;

    public LineThicknessExpression(Pool pl){

		super(true); // throw IncompleteSequence Exception
		pool = pl;

		// \\linethickness{val}
		add(new LiteralExpression("\\linethickness{"));
		add(WHITE_SPACES_OR_EOL);
		add(new NumericalExpression(DOUBLE, POSITIVE, null, false){
			public void action(ParserEvent e){
				if (DEBUG) System.out.println(e);
				lineWidth = ((Number)e.getValue()).doubleValue();
				pool.getAttributeSet(LaTeXParser.KEY_ATTRIBUTES).setAttribute(LINE_WIDTH,lineWidth);
		}});
		// no white-space here !
		add(new WordExpression("}", true){ // postfix="}", swallow
			public void action(ParserEvent e){
				if (DEBUG) System.out.println(e);
				String s= (String)e.getValue();
				if (s.equals("pt")) {
					lineWidth *= PS_POINT;
					pool.getAttributeSet(LaTeXParser.KEY_ATTRIBUTES).setAttribute(LINE_WIDTH,new Double(lineWidth));
					return;
				}
				if (s.equals("cm")) {
					lineWidth *= 10;
					pool.getAttributeSet(LaTeXParser.KEY_ATTRIBUTES).setAttribute(LINE_WIDTH,new Double(lineWidth));
					return;
				}
				// "mm" ? -> unchanged
		}});
    }

	public String toString(){
		return "[LineThicknessExpression]";
	}

} // LineThicknessExpression
