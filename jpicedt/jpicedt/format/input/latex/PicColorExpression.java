// PicColorExpression.java --- -*- coding: iso-8859-1 -*-
// July 23, 2002 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999-2013 Sylvain Reynal
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
// Version: $Id: PicColorExpression.java,v 1.10 2013/03/31 06:56:44 vincentb1 Exp $
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
package jpicedt.format.input.latex;

import jpicedt.format.input.util.*;
import jpicedt.graphic.model.*;

import java.awt.Color;
import static jpicedt.format.input.util.ExpressionConstants.*;
import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.StyleConstants.*;

/**
 * jPicEdt syntax (ie lines starting with a %)
 * color : filled/blacken/whiten/shade
 * @author Sylvain Reynal
 * @since jpicedt 1.3
 * @version $Id: PicColorExpression.java,v 1.10 2013/03/31 06:56:44 vincentb1 Exp $
 *
 *
 */
public class PicColorExpression extends AlternateExpression   {

    private Pool pool;

    public PicColorExpression(Pool pl){
		pool = pl;
		add(new LiteralExpression("filled"){ // old jPicEdt 1.1 format
			public void action(ParserEvent e){
				if (DEBUG) System.out.println(e);
				pool.currentObj.setAttribute(FILL_COLOR,Color.black);
				pool.currentObj.setAttribute(FILL_STYLE,FillStyle.SOLID);
		}});

		add(new LiteralExpression("blacken"){
			public void action(ParserEvent e){
				if (DEBUG) System.out.println(e);
				pool.currentObj.setAttribute(FILL_COLOR,Color.black);
				pool.currentObj.setAttribute(FILL_STYLE,FillStyle.SOLID);
		}});

		add(new LiteralExpression("whiten"){
			public void action(ParserEvent e){
				if (DEBUG) System.out.println(e);
				pool.currentObj.setAttribute(FILL_COLOR,Color.white);
				pool.currentObj.setAttribute(FILL_STYLE,FillStyle.SOLID);
		}});

		add(new LiteralExpression("shade"){
			public void action(ParserEvent e){
				if (DEBUG) System.out.println(e);
				pool.currentObj.setAttribute(FILL_COLOR,Color.gray);
				pool.currentObj.setAttribute(FILL_STYLE,FillStyle.SOLID);
		}});
    }
}
