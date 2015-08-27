// UnitLengthExpression.java --- -*- coding: iso-8859-1 -*-
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
// Version: $Id: UnitLengthExpression.java,v 1.9 2013/03/31 06:56:04 vincentb1 Exp $
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
import static jpicedt.format.input.util.ExpressionConstants.*;
import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.StyleConstants.*;

/**
 * \\unitlength value<br>
 * \\setlength{\\unitlength}{value}<br>
 * <br>
 * where value's permitted syntaxs are : 0.11, 0.11mm, 0.11cm, 0.11pt with possible leading whitespaces before the "unit".<br>
 * @author Sylvain Reynal
 * @since jpicedt 1.3
 * @version $Id: UnitLengthExpression.java,v 1.9 2013/03/31 06:56:04 vincentb1 Exp $
 *
 */
public class UnitLengthExpression extends SequenceExpression   {

	private Pool pool;
	private double unitLength;

	public UnitLengthExpression(Pool pl){

		super(true); // throw IncompleteSequence Exception

		this.pool = pl;

		// "\\unitlength" or "\\setlength{\\unitlength}{":
		add(new AlternateExpression(
		        new LiteralExpression("\\unitlength "),
		        new LiteralExpression("\\setlength{\\unitlength}{")));
		// numerical value:
		add(new NumericalExpression(DOUBLE, POSITIVE, null, false){ // no postfix, but possibly whitespaces before
			    public void action(ParserEvent e){
				    if (DEBUG) System.out.println(e);
					unitLength = ((Number)e.getValue()).doubleValue();
				    pool.put(LaTeXParser.KEY_UNIT_LENGTH,unitLength);
		}});
		add(WHITE_SPACES);
		// unit: "cm" or "pt" or "mm" or nothing (which is the same as "mm")
		add(new OptionalExpression(new AlternateExpression(
		                               new LiteralExpression("mm"), // do nothing ! this is the default
		                               new LiteralExpression("cm"){
			                               public void action(ParserEvent e){
				                               if (DEBUG) System.out.println(e);
				                               unitLength *= 10;
											   pool.put(LaTeXParser.KEY_UNIT_LENGTH,unitLength);
									   }},
		                               new LiteralExpression("pt"){
			                               public void action(ParserEvent e){
				                               if (DEBUG) System.out.println(e);
				                               unitLength *= PS_POINT;
											   pool.put(LaTeXParser.KEY_UNIT_LENGTH,unitLength);
									   }}))
		   );
		// optional trailing "}" in case \\setlength was found
		add(new OptionalExpression(new LiteralExpression("}")));
	}

	public String toString(){
		return "[UnitLengthExpression]";
	}

} // UnitLengthExpression
