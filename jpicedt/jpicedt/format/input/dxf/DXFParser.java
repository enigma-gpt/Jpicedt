// DXFParser.java --- -*- coding: iso-8859-1 -*-
// Copyright 2009/2013 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: DXFParser.java,v 1.4 2013/03/27 07:18:49 vincentb1 Exp $
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
package jpicedt.format.input.dxf;

import jpicedt.graphic.model.Drawing;
import jpicedt.graphic.io.parser.JPICParser;
import jpicedt.graphic.io.parser.ParsedDrawing;
import jpicedt.graphic.io.parser.ParserException;
import jpicedt.graphic.io.parser.ExtractionParsing;
import java.io.Reader;

/**
 * Analyse syntaxique d'un dessin codé en DXF.
 *@author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Belaïche</a>
 *@since JPicEdt 1.6
 */
public class DXFParser implements ExtractionParsing{


	/**
	 * signature pour compatibilité ascendante.
	 *@since JPicEdt 1.6
	 */
	public Drawing parse(Reader reader) throws ParserException{
		ParsedDrawing dr = new ParsedDrawing();
		parse(reader,dr);
		return dr.drawing;
	}


	/**
	 * Point d'entrée pour l'analyse syntaxique d'un dessin codé en DXF.
	 *@since JPicEdt 1.6
	 */
	public void parse(Reader reader,ParsedDrawing dr) throws ParserException{
		throw new ParserException.UnrecognizedFileFormat();
	}


	/**
	 * C'est juste fourni au cas où on voudrait brider JPicEdt pour qu'il ne
	 * soit capable de ne lire que du DXF (il suffirait d'utiliser DXFParser
	 * au lieu de JPICParser dans {@link jpicedt.MiscUtilities#createParser()}.
	 *@since JPicEdt 1.6
	 */
	public ParsedDrawing extractAndParse(Reader reader)
		throws ParserException{
		return JPICParser.extractAndParse(new DXFExtractor(),this,reader);
	}

}

/// DXFParser.java ends here
