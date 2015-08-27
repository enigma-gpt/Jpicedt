// JPICXmlExtraction.java --- -*- coding: iso-8859-1 -*-
// Copyright 2009/2013 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: JPICXmlExtraction.java,v 1.7 2013/03/27 07:21:14 vincentb1 Exp $
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

/// Installation:



/// Code:
package jpicedt.graphic.io.parser;
import java.lang.String;
import jpicedt.graphic.io.util.JpicDocUserData;
import jpicedt.graphic.model.Drawing;


/**
 * Interface pour extraire le code JPIC-XML enfoui dans un fichier ayant un
 * format de sauevgarde jPicEdt.
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Belaïche</a>
 * @since jPicEdt 1.6
 * @version $Id: JPICXmlExtraction.java,v 1.7 2013/03/27 07:21:14 vincentb1 Exp $
 */
public interface JPICXmlExtraction extends CodedContentExtraction {
	final String XML_HEAD_MARK_REGEXP = "%Created by ([0-9A-Za-z_ \\-\\.]++): ([A-Za-z\\- /]+?) format";
	final int    XML_HEAD_MARK_VERSION_GROUP = 1;
	final int    XML_HEAD_MARK_FORMAT_GROUP = 2;
	final String XML_BEGIN_MARK_REGEXP = "%Begin JPIC-XML\\p{Blank}*";
	final String XML_END_MARK_REGEXP = "%End JPIC-XML\\p{Blank}*";
	final String USER_START_MARK_REGEXP = "%User Data\\p{Blank}*";

	/**
	 * classe contenant les informations extraites d'un fichier ayant un format de
	 * sauvegarde de jPicEdt.
	 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Belaïche</a>
	 * @since jPicEdt 1.6
	 */
	public class ExtractedXml extends ExtractedCodedContent{
		public String            version;
		public boolean           needsEncoding = true;
		public JpicDocUserData   preambleUserData = new JpicDocUserData();
		public JpicDocUserData   postambleUserData = new JpicDocUserData();

		/**
		 * Qualifie le dessin de sale (isDirty) lorsque l'extraction du JPIC-XML
		 * a détecté que c'est le cas.
		 * Permet également de passer les prologues/épilogues utilisateur
		 * <br><b>author:</b> Vincent Belaïche
		 * @since jPicEdt 1.6
		 */
		public void qualifyDrawing(ParsedDrawing parsedDrawing){
			parsedDrawing.isDirty = needsEncoding;
			if(preambleUserData.size() != 0)
				parsedDrawing.preambleUserData = preambleUserData;
			else
				parsedDrawing.preambleUserData = null;
			if(postambleUserData.size() != 0)
				parsedDrawing.postambleUserData = postambleUserData;
			else
				parsedDrawing.postambleUserData = null;
		}

	};

	/**
	 * @param input tampon contenant le fichier où chercher le code JPIC-XML enfoui.
	 * @return null si pas de JPIC-XML trouvé, un descripteur <code>ExtractedXml</code> du
	 * fichier sinon.
	 * @since jPicEdt 1.6
	 */
	ExtractedXml extractXml(String input);

};

/// JPICXmlExtraction.java ends here
