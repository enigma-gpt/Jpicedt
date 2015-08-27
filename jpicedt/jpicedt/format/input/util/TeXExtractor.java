// TeXExtractor.java --- -*- coding: iso-8859-1 -*-
// jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2013  Sylvain Reynal
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
// Version: $Id: TeXExtractor.java,v 1.5 2013/03/31 06:58:24 vincentb1 Exp $
// Keywords: parser
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
package jpicedt.format.input.util;
import jpicedt.graphic.io.parser.CodedContentExtraction;
import jpicedt.graphic.io.parser.Parser;
import jpicedt.format.input.DefaultParser;

/**
 * Détecte si le dessin codé est codé dans l'une des variante de TeX (c'est à
 * dire LaTeX env picture de base, ou Epic/Eepic, ou pstricked).
 * Permet d'obtenir le parser pour du TeX.
 *@author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Belaïche</a>
 *@since jPicEdt 1.6
 */
public class TeXExtractor implements CodedContentExtraction{
	public Parser getParser(){
		return new DefaultParser();
	}

	public ExtractedCodedContent extractCodedContent(String inputString){
		ExtractedCodedContent ret = new ExtractedCodedContent();
		ret.extractedCodedText = inputString;
		return ret;
	}
}


/// TeXExtractor.java ends here
