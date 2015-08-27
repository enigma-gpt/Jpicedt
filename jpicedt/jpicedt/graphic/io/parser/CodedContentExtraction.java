// CodedContentExtraction.java --- -*- coding: iso-8859-1 -*-
// Copyright 2009/2011 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: CodedContentExtraction.java,v 1.5 2013/03/27 07:04:19 vincentb1 Exp $
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
import java.lang.String;
import jpicedt.graphic.model.Drawing;

/**
 * Interface pour extraire un dessin codé depuis un fichier.
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Belaïche</a>
 * @since jPicEdt 1.6
 * @version $Id: CodedContentExtraction.java,v 1.5 2013/03/27 07:04:19 vincentb1 Exp $
 */
public interface CodedContentExtraction{

	/**
	 * Classe contenant le texte codé à analyser.
	 *@author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Belaïche</a>
	 *@since jPicEdt 1.6
	 */
	public class ExtractedCodedContent{
		public String            extractedCodedText;
		
		public String			 sourceType;

		public String            getExtractedCodedText(){
			return extractedCodedText;
		}

		/**
		 * Permet de rajouter au dessin des attributs connu de l'extracteur de dessin
		 * codé, mais pas de l'analyseur de dessin codé (c'est à dire des
		 * information qui ne seraient pas de dans le dessin codé, mais ailleurs
		 * dans le même fichier).
		 * <br><b>author:</b> Vincent Belaïche
		 * @since jPicEdt 1.6
		 */
		public void qualifyDrawing(ParsedDrawing parsedDrawing){}
	};

	/**
	 * @return Le parser à utiliser pour analyser le dessin codé.
	 * @since jPicEdt 1.6
	 */
	public Parser            getParser();

	/**
	 *
	 * @return null si pas de dessin codé trouvé, un descripteur du dessin
	 *  codé sinon.
	 * @since jPicEdt 1.6
	 */
	ExtractedCodedContent extractCodedContent(String input);

};



/// CodedContentExtraction.java ends here
