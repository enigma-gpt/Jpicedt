// AbstractRegularExpression.java --- -*- coding: iso-8859-1 -*-
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
// Version: $Id: AbstractRegularExpression.java,v 1.8 2013/03/31 07:00:10 vincentb1 Exp $
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
package jpicedt.format.input.util;


import static jpicedt.format.input.util.ExpressionConstants.*;


/**
 * This is the abstract superclass for all regular expressions that may help building a RegExp-based parser.
 * It mimics the RegExp scheme, while at the same time allowing expressions to send ParserEvent's when
 * a piece of input text was successfully parsed (that is, instead of delegating to a separate ParserHandler
 * through a <i>callback</i> mechanism,  events are handled in the core of the <code>action</code> method).
 * A <code>Context</code> is then used to feed successive pieces of text to the set of reg-exp's that build up
 * the grammar tree, and a <code>Pool</code> allow reg-exp's to share data across the whole grammar tree.
 * @since jpicedt 1.3
 * @author Sylvain Reynal
 * @version $Id: AbstractRegularExpression.java,v 1.8 2013/03/31 07:00:10 vincentb1 Exp $
 *
 */
public abstract class AbstractRegularExpression {

    /**
     * Parses this expression, possibly using the given Context to fetch the String to interpret
	 * if this Expression is a leaf expression.
     * @return TRUE if parsing was successful
     * @throws REParserException if an error occur during parsing
     */
    public abstract boolean interpret(Context c) throws REParserException;

    /**
     * Called in the course of the <i>interpret</i> operation at the end of a SUCCESSFUL interpret operation.
     * Should be overriden by daughter classes to process proper action, e.g. set Element's attributes&hellip;
     * Current implementation does nothing, but sending a debugging string.
     */
    public void action(ParserEvent e) throws REParserException {if (DEBUG) System.out.println(e);}

}
