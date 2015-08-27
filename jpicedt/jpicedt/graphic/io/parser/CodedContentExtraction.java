// CodedContentExtraction.java --- -*- coding: iso-8859-1 -*-
// Copyright 2009/2011 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: CodedContentExtraction.java,v 1.5 2013/03/27 07:04:19 vincentb1 Exp $
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
package jpicedt.graphic.io.parser;
import java.lang.String;
import jpicedt.graphic.model.Drawing;

/**
 * Interface pour extraire un dessin cod� depuis un fichier.
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela�che</a>
 * @since jPicEdt 1.6
 * @version $Id: CodedContentExtraction.java,v 1.5 2013/03/27 07:04:19 vincentb1 Exp $
 */
public interface CodedContentExtraction{

	/**
	 * Classe contenant le texte cod� � analyser.
	 *@author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela�che</a>
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
		 * cod�, mais pas de l'analyseur de dessin cod� (c'est � dire des
		 * information qui ne seraient pas de dans le dessin cod�, mais ailleurs
		 * dans le m�me fichier).
		 * <br><b>author:</b> Vincent Bela�che
		 * @since jPicEdt 1.6
		 */
		public void qualifyDrawing(ParsedDrawing parsedDrawing){}
	};

	/**
	 * @return Le parser � utiliser pour analyser le dessin cod�.
	 * @since jPicEdt 1.6
	 */
	public Parser            getParser();

	/**
	 *
	 * @return null si pas de dessin cod� trouv�, un descripteur du dessin
	 *  cod� sinon.
	 * @since jPicEdt 1.6
	 */
	ExtractedCodedContent extractCodedContent(String input);

};



/// CodedContentExtraction.java ends here
