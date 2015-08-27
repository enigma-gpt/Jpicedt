// DXFStringBuffer.java --- -*- coding: iso-8859-1 -*-
// Copyright 2008/2012 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: DXFStringBuffer.java,v 1.7 2013/03/27 07:23:57 vincentb1 Exp $
// Keywords: AutoCAD, DXF (marque d�pos�e)
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
 * �tend StringBuffer (ce n'est pas vraiment une extension de classe car
 * StringBuffer est final) pour les besoins du formattage DXF (marque d�pos�e). Notamment la
 * notion de couple �tiquette/valeur est support�e.<br/>
 * Cette classe s'occupe du formatage de bas niveau (c'est � dire que tout
 * �l�ment d'information dans un document DXF (marque d�pos�e) est sur un couple de deux ligne,
 * avec une prmei�re ligne (de num�ro impaire) portant un num�ro d'�tiquette
 * (group code id) permettant de typer l'information et une seconde ligne (de
 * num�ro paire) portant l'information elle-m�me.
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela�che</a>
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
