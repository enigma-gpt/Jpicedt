// JPICXmlFormatExtractor.java --- -*- coding: iso-8859-1 -*-
// Copyright 2009/2013 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: JPICXmlFormatExtractor.java,v 1.6 2013/03/27 07:21:09 vincentb1 Exp $
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
 * sauvegarde jPicEdt dont le format est connu (ou suppos� l'�tre), et tel que
 * l'enfouissement utilise un m�thode g�n�rique consistant � mettre le code
 * JPIC-XML en commentaire, avec chaque commentaire d�limit� par une marque de
 * d�part et une marque de fin ligne par ligne.
 *@author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela�che</a>
 *@since jPicEdt 1.6
 */
public class JPICXmlFormatExtractor extends JPICXmlExtractor implements JPICXmlExtraction{
	String formatName;
	String startCommentRegexp;
	String endCommentRegexp;
	boolean isCommentedXml;
	int    lineCountPerMetaLine;
    /**
	 *@param formatName nom du format tel qu'il appara�t dans la ligne "Created by &hellip;"
	 *@param startCommentRegexp expression rationnelle marquant le d�but d'un
	 *commentaire.
	 *@param endCommentRegexp expression rationnelle marquant la fin d'un commentaire.
	 *@param isCommentedXml true si le code JPIC-XML appara�t en commentaire
	 *dans le fichier, o� les commentaires sont selon le format correspondant
	 *� l'extracteur implantant l'interface.
	 *@param lineCountPerMetaLine le fichier est d�coup� en m�ta-ligne, chaque
	 *m�ta ligne �tant  bloc de lineCountPerMetaLine lignes.
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
	 *@param inputString tampon contenant le fichier o� chercher le code
	 *JPIC-XML enfoui.
	 *@return null si pas de JPIC-XML trouv�, un descripteur ExtractedXml
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
				// tire une m�ta-ligne du tampon
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
					// donn�es utilisateur de pr�ambule
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
					// en-t�te de d�marrage
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
					// dessin cod� selon le format d�sign�
					//-------------------------------------------------------
					m = userStartPattern.matcher(metaLine);
					if(m.matches())
					{
						state = 4;
					}
					else
					{
						// le dessin encod� a toujours au moins une ligne de
						// code, m�me si le dessin ne comprend aucun �l�ment,
						// car il y a au moins un commentaire de d�marrage.
						// La pr�sence d'au moins une ligne permet de tester
						// que le dessin est d�j� encod�. Ceci est utile en
						// cas d'une interaction avec Emacs pour modification
						// directe du fichier.
						extractedXml.needsEncoding = false;
					}
					break;
				}
				case 4:
				{
					// donn�es utilisateur en postambule
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
