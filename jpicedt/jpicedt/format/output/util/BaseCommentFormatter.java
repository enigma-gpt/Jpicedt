// BaseCommentFormatter.java --- -*- coding: iso-8859-1 -*-
// Copyright 2008/2011 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: BaseCommentFormatter.java,v 1.6 2013/03/27 07:22:19 vincentb1 Exp $
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
package jpicedt.format.output.util;

import  jpicedt.Log;
import  jpicedt.graphic.io.formatter.FormatterFactory;
import  java.io.Writer;
import  java.io.IOException;
import  java.lang.*;

import static jpicedt.format.output.util.FormatConstants.*;


/**
 * Formateur de base pour les commentaires servant à enfouir le code JPIC-XML dans un fichier en format de
 * sauvegarde.
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Belaïche</a>
 * @since jPicEdt 1.6
 */
public class BaseCommentFormatter
{
	Writer writer;
	FormatterFactory factory;

	public BaseCommentFormatter(FormatterFactory factory){
		this.factory = factory;
	}

	public void verbatimWriteLine(String s) throws java.io.IOException{
			writer.write(s);
			writer.write(factory.getLineSeparator());
	}

	/**
	 * Définit le writere à utiliser pour formater les commentaires
	 *@param writer le writer à utiliser pour formater les commentaires.
	 *@since jPicEdt 1.6
	 */
	public void setWriter(Writer writer){
		this.writer = writer;
	}

	/**
     * Formate un commentaire contenant du JPIC-XML. Le code JPIC-XML peut
     * être multiligne.
	 * @param prefix Préfixe de début de commentaire, chaque ligne de commentaire
	 * commençant par ce préfixe et s'étendant jusqu'à une fin de ligne.
	 * @param s La Chaîne de caractères contenant le JPIC-XML nu (avant
	 * enfouissement dans un commentaire).
	 * @since jPicEdt 1.6
	 */
	protected void commentFormat(String prefix,String s){
			String[] sa = s.split("\r?\n|\r");

			try
			{
				for(String i : sa)
				{
					writer.write(prefix);
					writer.write(i);
					writer.write(CR_LF);
				}
			}
			catch (IOException io) {
				Log.error("Error writing comment: ");
				io.printStackTrace();
			}
	}
}


/// BaseCommentFormatter.java ends here
