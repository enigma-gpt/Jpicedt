// BaseCommentFormatter.java --- -*- coding: iso-8859-1 -*-
// Copyright 2008/2011 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: BaseCommentFormatter.java,v 1.6 2013/03/27 07:22:19 vincentb1 Exp $
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
 * Formateur de base pour les commentaires servant � enfouir le code JPIC-XML dans un fichier en format de
 * sauvegarde.
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela�che</a>
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
	 * D�finit le writere � utiliser pour formater les commentaires
	 *@param writer le writer � utiliser pour formater les commentaires.
	 *@since jPicEdt 1.6
	 */
	public void setWriter(Writer writer){
		this.writer = writer;
	}

	/**
     * Formate un commentaire contenant du JPIC-XML. Le code JPIC-XML peut
     * �tre multiligne.
	 * @param prefix Pr�fixe de d�but de commentaire, chaque ligne de commentaire
	 * commen�ant par ce pr�fixe et s'�tendant jusqu'� une fin de ligne.
	 * @param s La Cha�ne de caract�res contenant le JPIC-XML nu (avant
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
