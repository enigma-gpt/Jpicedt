// DXFExtractor.java --- -*- coding: iso-8859-1 -*-
// Copyright 2009/2013 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: DXFExtractor.java,v 1.4 2013/03/27 12:09:45 vincentb1 Exp $
// Keywords: AutoCAD, DXF
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
import jpicedt.graphic.io.parser.CodedContentExtraction;
import jpicedt.graphic.io.parser.Parser;
import java.util.regex.Pattern;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.StringReader;

public class DXFExtractor implements CodedContentExtraction{

	/**
	 *@return Un nouveau parser à utiliser pour analyser un dessin codé en DXF.
	 *@since jPicEdt 1.6
	 */
	public Parser            getParser(){
		return new DXFParser();
	}

	/**
	 * Recherche la séquence 0<CR>SECTION>CR> au début du fichier pour
	 * détecter que le fichier est au format DXF.
	 *@param inputString tampon contenant le fichier où chercher le dessin codé.
	 *@return null si pas de dessin codé trouvé, un descripteur
	 *        de dessin codé sinon.
	 *@since jPicEdt 1.6
	 */
	public ExtractedCodedContent extractCodedContent(String inputString){
		BufferedReader input = new BufferedReader(new StringReader(inputString)); // donne accès à la méthode readLine() qui gère les CR-LF correctement
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
