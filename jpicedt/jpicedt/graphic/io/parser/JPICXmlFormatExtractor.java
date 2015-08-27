// JPICXmlFormatExtractor.java --- -*- coding: iso-8859-1 -*-
// Copyright 2009/2013 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: JPICXmlFormatExtractor.java,v 1.6 2013/03/27 07:21:09 vincentb1 Exp $
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

import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.BufferedReader;
import java.io.StringReader;
import jpicedt.graphic.io.util.UserDataCommentedLine;
import jpicedt.graphic.io.util.UserDataVerbatimLine;

import static jpicedt.Log.debug;


/**
 * Cette classe permet d'extraire le JPIC-XML enfoui dans un fichier de
 * sauvegarde jPicEdt dont le format est connu (ou supposé l'être), et tel que
 * l'enfouissement utilise un méthode générique consistant à mettre le code
 * JPIC-XML en commentaire, avec chaque commentaire délimité par une marque de
 * départ et une marque de fin ligne par ligne.
 *@author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Belaïche</a>
 *@since jPicEdt 1.6
 */
public class JPICXmlFormatExtractor extends JPICXmlExtractor implements JPICXmlExtraction{
	String formatName;
	String startCommentRegexp;
	String endCommentRegexp;
	boolean isCommentedXml;
	int    lineCountPerMetaLine;
    /**
	 *@param formatName nom du format tel qu'il apparaît dans la ligne "Created by &hellip;"
	 *@param startCommentRegexp expression rationnelle marquant le début d'un
	 *commentaire.
	 *@param endCommentRegexp expression rationnelle marquant la fin d'un commentaire.
	 *@param isCommentedXml true si le code JPIC-XML apparaît en commentaire
	 *dans le fichier, où les commentaires sont selon le format correspondant
	 *à l'extracteur implantant l'interface.
	 *@param lineCountPerMetaLine le fichier est découpé en méta-ligne, chaque
	 *méta ligne étant  bloc de lineCountPerMetaLine lignes.
	 *@since jPicEdt 1.6
	 */
	public JPICXmlFormatExtractor(
		String   formatName,
		String   startCommentRegexp,
		String   endCommentRegexp,
		boolean  isCommentedXml,
		int    lineCountPerMetaLine){
		this.formatName           = formatName;
		this.startCommentRegexp   = startCommentRegexp;
		this.endCommentRegexp     = endCommentRegexp;
		this.isCommentedXml       = isCommentedXml;
		this.lineCountPerMetaLine = lineCountPerMetaLine;
	}
	/**
	 * Extrait le code JPIC-XML enfoui, s'il en est, dans un fichier au format
	 * TeX.
	 *@param inputString tampon contenant le fichier où chercher le code
	 *JPIC-XML enfoui.
	 *@return null si pas de JPIC-XML trouvé, un descripteur ExtractedXml
	 *du fichier sinon.
	 *@since jPicEdt 1.6
	 */
	public ExtractedXml extractXml(String inputString){
		StringBuilder extractedXmlText = new StringBuilder(100);
		ExtractedXml extractedXml = new ExtractedXml();
		BufferedReader input = new BufferedReader(new StringReader(inputString)); // gives access to a readLine() method which handles CR-LF properly
		Pattern createdPattern = Pattern.compile(
			"^"+startCommentRegexp+XML_HEAD_MARK_REGEXP+endCommentRegexp+"$");
		Pattern commentedLinePattern = Pattern.compile(
			"^" +startCommentRegexp + "(.*)"+endCommentRegexp+"$");
		Pattern beginPattern = Pattern.compile(
			"^" +startCommentRegexp + XML_BEGIN_MARK_REGEXP + endCommentRegexp+"$");
		Pattern endPattern = Pattern.compile(
			"^" +startCommentRegexp + XML_END_MARK_REGEXP + endCommentRegexp+"$");
		Pattern userStartPattern = Pattern.compile(
			"^" +startCommentRegexp + USER_START_MARK_REGEXP + endCommentRegexp+"$");
		String line = null;
		StringBuilder metaLineSB = new StringBuilder();
		String        metaLine;
		int state = 0;
		try {
			for(;;)
			{
				// tire une méta-ligne du tampon
				metaLineSB.setLength(0);
				for(int i = 0;i < lineCountPerMetaLine;++i)
				{
					if((line=input.readLine())!=null)
					{
						if(i > 0)
							metaLineSB.append('\n');
						metaLineSB.append(line);
					}
					else if(i == 0 && state >= 3)
					{
						extractedXml.extractedCodedText = extractedXmlText.toString();
						return extractedXml;
					}
					else
						return null;
				}
				metaLine = metaLineSB.toString();
				Matcher m;
				switch(state)
				{
				case 0:
				{
					// données utilisateur de préambule
					//-------------------------------------------------------
					m = createdPattern.matcher(metaLine);
					if(m.matches())
					{
						state = 1;
						extractedXml.version = m.group(XML_HEAD_MARK_VERSION_GROUP);
						if(!m.group(XML_HEAD_MARK_FORMAT_GROUP).equals(formatName))
							return null;
					}
					else
					{
						m = commentedLinePattern.matcher(metaLine);
						if(m.matches())
							extractedXml.preambleUserData
								.add(new UserDataCommentedLine(m.group(1)));
						else
							extractedXml.preambleUserData
								.add(new UserDataVerbatimLine(metaLine));

					}
					break;
				}
				case 1:
				{
					// en-tête de démarrage
					//-------------------------------------------------------
					m = beginPattern.matcher(metaLine);
					if(m.matches())
						state = 2;
					break;
				}
				case 2:
				{
					// code JPIC-XML
					//-------------------------------------------------------
					m = endPattern.matcher(metaLine);
					if(m.matches())
						state = 3;
					else
					{
						if(isCommentedXml)
						{
							m = commentedLinePattern.matcher(metaLine);
							if(m.matches())
							{
								extractedXmlText.append(m.group(1));
							}
							else
								return null;
						}
						else
						{
							extractedXmlText.append(metaLine);
						}
						// append Unix-like CR-LF = no problemo :
						// we don't care if it's not the current platform's CR-LF, cause
						// this StringBuilder will be used by our SAX parser only, and
						// this one knows how to handle any type of CR-LF properly
						extractedXmlText.append('\n');
					}
					break;
				}
				case 3:
				{
					// dessin codé selon le format désigné
					//-------------------------------------------------------
					m = userStartPattern.matcher(metaLine);
					if(m.matches())
					{
						state = 4;
					}
					else
					{
						// le dessin encodé a toujours au moins une ligne de
						// code, même si le dessin ne comprend aucun élément,
						// car il y a au moins un commentaire de démarrage.
						// La présence d'au moins une ligne permet de tester
						// que le dessin est déjà encodé. Ceci est utile en
						// cas d'une interaction avec Emacs pour modification
						// directe du fichier.
						extractedXml.needsEncoding = false;
					}
					break;
				}
				case 4:
				{
					// données utilisateur en postambule
					m = commentedLinePattern.matcher(metaLine);
					if(m.matches())
						extractedXml.postambleUserData
							.add(new UserDataCommentedLine(m.group(1)));
					else
						extractedXml.postambleUserData
							.add(new UserDataVerbatimLine(metaLine));
					break;
				}
				}
			}

		}
		catch (IOException ioEx){
			return null;
		}

	}

};


/// JPICXmlFormatExtractor.java ends here
