// ParsedDrawing.java --- -*- coding: iso-8859-1 -*-
// Copyright 2009/2012 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: ParsedDrawing.java,v 1.5 2013/03/27 07:03:54 vincentb1 Exp $
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
import jpicedt.graphic.model.Drawing;
import jpicedt.graphic.io.util.JpicDocUserData;

/**
 * Classe contenant les informations r�sultant de l'extraction de de l'analyse
 * d'un dessin cod� � partir d'un fichier.
 *@author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela�che</a>
 *@since jPicEdt 1.6
 */
public class ParsedDrawing{

	public boolean  isDirty;
	public Drawing  drawing;
	public JpicDocUserData preambleUserData;
	public JpicDocUserData postambleUserData;
	public String sourceType;


	public void weakCopy(ParsedDrawing x){
		isDirty = x.isDirty;
		drawing = x.drawing;
		preambleUserData  = x.preambleUserData;
		postambleUserData = x.postambleUserData;
	}

	public ParsedDrawing(){}
}


/// ParsedDrawing.java ends here
