// FormatConstants.java --- -*- coding: iso-8859-1 -*-
// Copyright 2008/2013 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: FormatConstants.java,v 1.5 2013/03/27 07:22:14 vincentb1 Exp $
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

import  java.io.Writer;
import  java.lang.String;
import  java.lang.StringBuffer;
import  java.util.regex.Matcher;
import  java.util.regex.Pattern;

public class FormatConstants {

	/**
	 * Line separator for the current platform (i.e. "\n" on Unix, "\n\r" on Windows, "\r" on Mac,&hellip;)
	 */
	public static final String CR_LF = System.getProperty("line.separator");

	private static Pattern eolRe = Pattern.compile("(\r?\n|\r)");

	/**
	   �crit la cha�ne <code>in</code> en respectant le s�parateur de ligne du fichier de sauvegarde.
	 */
	public static void textWriteMultiLine(Writer out, String in) throws java.io.IOException{
		int pos = 0;
		Matcher m = eolRe.matcher(in);
		while(m.find(pos)){
			out.append(in.substring(pos, m.regionStart()));
			out.append(CR_LF);
			pos = m.regionEnd();
		}
		out.append(in.substring(pos));
	}

}



/// FormatConstants.java ends here
