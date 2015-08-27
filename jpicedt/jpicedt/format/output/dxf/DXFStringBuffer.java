// DXFStringBuffer.java --- -*- coding: iso-8859-1 -*-
// Copyright 2008/2012 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: DXFStringBuffer.java,v 1.7 2013/03/27 07:23:57 vincentb1 Exp $
// Keywords: AutoCAD, DXF (marque déposée)
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
//---------------------------------------------------------------------------
package jpicedt.format.output.dxf;

import jpicedt.graphic.*;
import jpicedt.graphic.model.*;
import jpicedt.graphic.io.formatter.*;
import jpicedt.graphic.PicPoint;

import java.awt.*;

import static jpicedt.format.output.dxf.DXFConstants.*;
import static jpicedt.graphic.model.StyleConstants.LineStyle.*;
import static jpicedt.graphic.model.StyleConstants.*;
import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.AbstractCurve.PointType.*;

/**
 * Étend StringBuffer (ce n'est pas vraiment une extension de classe car
 * StringBuffer est final) pour les besoins du formattage DXF (marque déposée). Notamment la
 * notion de couple étiquette/valeur est supportée.<br/>
 * Cette classe s'occupe du formatage de bas niveau (c'est à dire que tout
 * élément d'information dans un document DXF (marque déposée) est sur un couple de deux ligne,
 * avec une prmeière ligne (de numéro impaire) portant un numéro d'étiquette
 * (group code id) permettant de typer l'information et une seconde ligne (de
 * numéro paire) portant l'information elle-même.
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Belaïche</a>
 * @since jpicedt 1.6
 */
public class DXFStringBuffer
{
	StringBuffer buf;
	String       lineSeparator;
	/**
	 * @since jPicEdt 1.6
	 */
	public DXFStringBuffer(int size, String lineSeparator){
		buf = new StringBuffer(size);
		this.lineSeparator = lineSeparator;
	}
	/**
	 * @since jPicEdt 1.6
	 */
	void tagVal(int groupCode,String val){
		tag(groupCode);
		buf.append(val);
		buf.append(lineSeparator);
	}
	/**
	 * @since jPicEdt 1.6
	 */
	void tagVal(int groupCode,char[] val){
		tag(groupCode);
		buf.append(val);
		buf.append(lineSeparator);
	}
	/**
	 * @since jPicEdt 1.6
	 */
	void tagVal(int groupCode,char[] val,int len){
		tag(groupCode);
		buf.append(val,0,len);
		buf.append(lineSeparator);
	}


	/**
	 * @since jPicEdt 1.6
	 */
	void tag(int groupCode){
		if(groupCode >= 0)
		{
			if(groupCode <= 9)
				buf.append("  ");
			else if(groupCode <= 99)
				buf.append(" ");
		}
		buf.append(groupCode);
		buf.append(lineSeparator);
	}
	/**
	 * @since jPicEdt 1.6
	 */
	void tagVal(int groupCode,double val){
		tag(groupCode);
		buf.append(val);
		buf.append(lineSeparator);
	}

	/**
	 * @since jPicEdt 1.6
	 */
	void tagVal(int groupCode,int val){
		tag(groupCode);
		buf.append(val);
		buf.append(lineSeparator);
	}

	/**
	 * @since jPicEdt 1.6
	 */
	void tagPoint(int groupCode,PicPoint pt){
		tag(groupCode+10);
		buf.append(PEToolKit.doubleToString(pt.getX()));
		buf.append(lineSeparator);
		tag(groupCode+20);
		buf.append(PEToolKit.doubleToString(pt.getY()));
		buf.append(lineSeparator);
	}

	/**
	 * @since jPicEdt 1.6
	 */
	void comment(String comment){
		String[] lines = comment.split("\r?\n|\r");
		if(lines != null)
			for(int i=0;i < lines.length; ++i)
				tagVal(999,lines[i]);
	}

	/**
	 * @since jPicEdt 1.6
	 */
	void append(String string){
		buf.append(string);
	}

	/**
	 * @since jPicEdt 1.6
	 */
	public String toString(){ return buf.toString(); }
};




/// DXFStringBuffer.java ends here
