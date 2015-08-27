// JPICXmlExtractor.java --- -*- coding: iso-8859-1 -*-
// Copyright 2009/2011 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: JPICXmlExtractor.java,v 1.4 2013/03/27 07:04:09 vincentb1 Exp $
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
package jpicedt.graphic.io.parser;
import  java.lang.String;

/**
 * Classe de base extraire le code JPIC-XML enfoui dans un fichier ayant un
 * format de sauvgarde jPicEdt inconnu.
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Belaïche</a>
 * @since jPicEdt 1.6
 * @version $Id: JPICXmlExtractor.java,v 1.4 2013/03/27 07:04:09 vincentb1 Exp $
 */
public abstract class JPICXmlExtractor implements JPICXmlExtraction {

	/**
	 * Extait le code le dessin codé, dans le cas d'un codage en JPIC-XML.
	 *@since jPicEdt 1.6 */
	public ExtractedCodedContent extractCodedContent(String input){
		return extractXml(input);
	}

	/**
	 *@return un nouvel analyseur de JPIC-XML
	 *@since jPicEdt 1.6 */
	public Parser getParser(){
		return new JPICXmlParser();
	}
};



/// JPICXmlExtractor.java ends here
