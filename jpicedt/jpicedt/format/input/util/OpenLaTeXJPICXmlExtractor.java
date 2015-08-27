// OpenLaTeXJPICXmlExtractor.java --- -*- coding: iso-8859-1 -*-
// jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2013  Sylvain Reynal
//
// Author: Sylvain Reynal
//         D�partement de Physique
//         �cole Nationale Sup�rieure de l'�lectronique et de ses Applications (�NS�A)
//         6, avenue du Ponceau
//         95014 CERGY CEDEX
//         FRANCE
//
//         Tel : +33 130 736 245
//         Fax : +33 130 736 667
//         e-mail : reynal@ensea.fr
//
// Version: $Id: OpenLaTeXJPICXmlExtractor.java,v 1.5 2013/03/31 06:59:19 vincentb1 Exp $
// Keywords: parser
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
package jpicedt.format.input.util;
import  java.lang.StringBuilder;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import  java.io.StringReader;
import  java.io.BufferedReader;
import  java.io.IOException;
import jpicedt.graphic.io.parser.JPICXmlExtraction;
import jpicedt.graphic.io.parser.JPICXmlFormatExtractor;
import jpicedt.graphic.io.formatter.XMLizer;


/**
 * Cette classe permet d'extraire le code JPIC-XML enfoui dans un fichier au
 * au format "open LaTeX JPIC-XML", c'est � dire du JPIC-XML o� le code XML
 * proprement dit est comment� � la LaTeX, sauf le code LaTeX des �l�ments
 * &lt;text&gt;, celui-ci appairaissant en clair. Ce format permet de hacker
 * le contenu des objets PicText d'un dessin avec un �diteur de LaTeX (par
 * exemple Emacs+AucTeX).
 *@author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela�che</a>
 *@since jPicEdt 1.6
 */
public class OpenLaTeXJPICXmlExtractor extends JPICXmlFormatExtractor{

	public OpenLaTeXJPICXmlExtractor(){
		super("open LaTeX JPIC-XML","%","",false,1);
	}

	/**
	 *@param input tampon contenant le fichier o� chercher le code JPIC-XML.
	 *@return null si pas de JPIC-XML trouv�, un descripteur ExtractedXml
	 *du fichier sinon.
	 *@since jPicEdt 1.6
	 */
	public ExtractedXml extractXml(String input){
		ExtractedXml extractedXml= super.extractXml(input);
		if(extractedXml == null)
			return extractedXml;
		StringBuilder extractedXmlText = new StringBuilder();
		StringBuilder picTextText =  new StringBuilder();
		String line = null;
		BufferedReader inputXmlText = new BufferedReader(new StringReader(extractedXml.extractedCodedText));
		int state = 0;
		Pattern completeTextTag = Pattern.compile("^%<text.++>\\p{Blank}*$");
		Pattern closeTextTag = Pattern.compile("^%\\p{Blank}*>\\p{Blank}*$");
		Matcher m;
		try
		{
			while((line = inputXmlText.readLine()) != null)
			{
				switch(state)
				{
				case 0:
					// pendant le code JPIC-XML
					if(line.startsWith("%<text"))
					{
						m = completeTextTag.matcher(line);
						if(m.matches())
							state = 1;
						else
							state = 2;
					}
					else if(!line.startsWith("%"))
						return null;
					extractedXmlText.append(line.substring(1));
					extractedXmlText.append('\n');
					break;
				case 1:
					// pendant le code LaTeX
					if(line.startsWith("%</text>"))
					{
						XMLizer.appendXMLized(extractedXmlText,picTextText);
						picTextText.setLength(0);
						extractedXmlText.append(line.substring(1));
						extractedXmlText.append('\n');
						state = 0;
						break;
					}
					else
					{
						if(line.startsWith("%&amp;"))
						{
							picTextText.append("%&");
							picTextText.append(line.substring(6));
						}
						else if(line.startsWith("%&lt;"))
						{
							picTextText.append("%<");
							picTextText.append(line.substring(5));
						}
						else
							picTextText.append(line);
						picTextText.append('\n');
					}
					break;
				case 2:
					// pendant la balise d'ouverture de <text>
					m = closeTextTag.matcher(line);
					if(m.matches())
					{
						state = 1;
					}
					else if(!line.startsWith("%"))
						return null;
					extractedXmlText.append(line.substring(1));
					extractedXmlText.append('\n');
					break;
				}
			}
		}
		catch(IOException ioEx)
		{
			return null;
		}


		extractedXml.extractedCodedText = extractedXmlText.toString();
		return extractedXml;
	}

};


/// OpenLaTeXJPICXmlExtractor.java ends here
