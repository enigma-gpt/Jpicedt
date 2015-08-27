// DXFExtractor.java --- -*- coding: iso-8859-1 -*-
// Copyright 2009/2013 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: DXFExtractor.java,v 1.4 2013/03/27 12:09:45 vincentb1 Exp $
// Keywords: AutoCAD, DXF
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
import jpicedt.graphic.io.parser.CodedContentExtraction;
import jpicedt.graphic.io.parser.Parser;
import java.util.regex.Pattern;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.StringReader;

public class DXFExtractor implements CodedContentExtraction{

	/**
	 *@return Un nouveau parser � utiliser pour analyser un dessin cod� en DXF.
	 *@since jPicEdt 1.6
	 */
	public Parser            getParser(){
		return new DXFParser();
	}

	/**
	 * Recherche la s�quence 0<CR>SECTION>CR> au d�but du fichier pour
	 * d�tecter que le fichier est au format DXF.
	 *@param inputString tampon contenant le fichier o� chercher le dessin cod�.
	 *@return null si pas de dessin cod� trouv�, un descripteur
	 *        de dessin cod� sinon.
	 *@since jPicEdt 1.6
	 */
	public ExtractedCodedContent extractCodedContent(String inputString){
		BufferedReader input = new BufferedReader(new StringReader(inputString)); // donne acc�s � la m�thode readLine() qui g�re les CR-LF correctement
		String line;
		boolean odd = false;
		Pattern commentGroupPattern = Pattern.compile(
			"^\\p{Blank}+999\\p{Blank}+$");
		Pattern sectionGroupPattern = Pattern.compile(
			"^\\p{Blank}+0\\p{Blank}+$");
		Pattern sectionPattern = Pattern.compile(
			"^\\p{Blank}+SECTION\\p{Blank}+$");

		int progress = 0;

		try{
			for(;;)
			{
				if((line=input.readLine())!=null)
				{
					switch(progress)
					{
					case 0:
						if(!odd)
						{
							if(commentGroupPattern.matcher(line).matches())
							{}
							else if(sectionGroupPattern.matcher(line).matches())
								++progress;
							else
								return null;
						}
						break;
					case 1:
						if(odd)
						{
							if(sectionPattern.matcher(line).matches())
							{
								ExtractedCodedContent ret = new ExtractedCodedContent();
								ret.extractedCodedText = inputString;
								return ret;
						    }
							else
								return null;
						}
						break;

					}


					odd = !odd;
				}
				else
					return null;
			}

		}
		catch (IOException ioEx){
			return null;
		}

	}


}


/// DXFExtractor.java ends here
