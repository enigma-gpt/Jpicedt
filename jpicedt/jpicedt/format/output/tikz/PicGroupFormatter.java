// PicGroupFormatter.java --- -*- coding: iso-8859-1 -*-
// Copyright 2009/2013 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: PicGroupFormatter.java,v 1.5 2013/03/27 07:22:49 vincentb1 Exp $
// Keywords: Tikz, PGF
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
package jpicedt.format.output.tikz;
import jpicedt.graphic.io.formatter.Formatter;
import jpicedt.graphic.model.PicGroup;

import java.lang.String;
import java.lang.StringBuffer;
import java.io.IOException;

import jpicedt.Log;
import static jpicedt.graphic.model.BranchElement.CompoundMode.*;


public class PicGroupFormatter
	extends jpicedt.format.output.util.PicGroupFormatter
	implements Formatter
{
	public PicGroupFormatter(PicGroup group, TikzFormatter factory){
		super(group, factory);
	}

	public TikzFormatter getFactory(){
		return (TikzFormatter)factory;
	}

	public String format() throws IOException{
		switch(group.getCompoundMode())
		{
		case JOINT:
			return formatJoint();
		case SEPARATE:
			return formatSeparate();
		default:
			Log.error("Unexpected CompoundMode");
			return null;
		}
	}


	String formatJoint() throws IOException{
		return super.format();
	}


	String formatSeparate() throws IOException{
		return super.format();
	}

}



/// PicGroupFormatter.java ends here
