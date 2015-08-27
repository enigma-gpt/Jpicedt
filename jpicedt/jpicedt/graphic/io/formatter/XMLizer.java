// XMLizer.java --- -*- coding: iso-8859-1 -*-
// Copyright 2009/2013 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: XMLizer.java,v 1.3 2013/03/27 07:04:24 vincentb1 Exp $
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

// La fonction membre appendXMLized est un copier coller de la fonction
// appendEncoded de JPICTextFormatter.



/// Code:
package jpicedt.graphic.io.formatter;
import java.lang.StringBuffer;
import java.lang.String;
import java.io.IOException;
/**
 * Cette classe permet d'�chapper les caract�res sp�ciaux de XML, c'est � dire
 * `&lt;',``&gt;' et `&amp;' respectivement en `&amp;lt;',`&amp;gt;' et
 * `&amp;amp;'.
 *
 *@author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela�che</a>
 *@since jPicEdt 1.6
 */
public class XMLizer{

	/**
	 * Encode les caract�res speciaux dans text, et ajoute de r�sultat dans
	 * buf.
	 * @param buf Tampon de destination du r�sultat de l'encodage
	 * @param text Texte � encoder.
	 *@since jPicEdt 1.6
	 */
	public static void appendXMLized(Appendable buf, CharSequence text){
		try
		{
			for (int i=0; i<text.length(); i++){
				char c = text.charAt(i);
				switch (c){
				case '<' : buf.append("&lt;"); break;
				case '>' : buf.append("&gt;"); break;
				case '&' : buf.append("&amp;"); break;
				default : buf.append(c);
				}
			}
		}
		catch(IOException ioEx)
		{
			System.err.println("Error In appendXMLized:");
			ioEx.printStackTrace();
			return;
		}
	}

};


/// XMLizer.java ends here
