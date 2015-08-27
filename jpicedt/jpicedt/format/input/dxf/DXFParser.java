// DXFParser.java --- -*- coding: iso-8859-1 -*-
// Copyright 2009/2013 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: DXFParser.java,v 1.4 2013/03/27 07:18:49 vincentb1 Exp $
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
package jpicedt.format.input.dxf;

import jpicedt.graphic.model.Drawing;
import jpicedt.graphic.io.parser.JPICParser;
import jpicedt.graphic.io.parser.ParsedDrawing;
import jpicedt.graphic.io.parser.ParserException;
import jpicedt.graphic.io.parser.ExtractionParsing;
import java.io.Reader;

/**
 * Analyse syntaxique d'un dessin cod� en DXF.
 *@author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela�che</a>
 *@since JPicEdt 1.6
 */
public class DXFParser implements ExtractionParsing{


	/**
	 * signature pour compatibilit� ascendante.
	 *@since JPicEdt 1.6
	 */
	public Drawing parse(Reader reader) throws ParserException{
		ParsedDrawing dr = new ParsedDrawing();
		parse(reader,dr);
		return dr.drawing;
	}


	/**
	 * Point d'entr�e pour l'analyse syntaxique d'un dessin cod� en DXF.
	 *@since JPicEdt 1.6
	 */
	public void parse(Reader reader,ParsedDrawing dr) throws ParserException{
		throw new ParserException.UnrecognizedFileFormat();
	}


	/**
	 * C'est juste fourni au cas o� on voudrait brider JPicEdt pour qu'il ne
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
