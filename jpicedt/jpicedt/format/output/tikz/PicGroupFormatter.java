// PicGroupFormatter.java --- -*- coding: iso-8859-1 -*-
// Copyright 2009/2013 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: PicGroupFormatter.java,v 1.5 2013/03/27 07:22:49 vincentb1 Exp $
// Keywords: Tikz, PGF
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
