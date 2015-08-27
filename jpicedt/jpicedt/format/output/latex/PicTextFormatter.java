// PicTextFormatter.java --- -*- coding: iso-8859-1 -*-
// jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2006  Sylvain Reynal
//
// Author: Sylvain Reynal
//         D�partement de Physique
//         �cole Nationale Sup�rieure de l'�lectronique et de ses Applications (�NS�A)
//         6, avenue du Ponceau
//         95014 CERGY CEDEX
//         FRANCE
//
//         Tel : +33 130 736 245
//         Fax : +33 130 736 667
//         e-mail : reynal@ensea.fr
//
// Version: $Id: PicTextFormatter.java,v 1.14 2013/03/27 07:23:27 vincentb1 Exp $
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

package jpicedt.format.output.latex;

import jpicedt.graphic.*;
import jpicedt.graphic.model.*;
import jpicedt.graphic.io.formatter.Formatter;
import jpicedt.graphic.io.formatter.FormatterFactory;
import jpicedt.graphic.io.formatter.AbstractFormatter;
import jpicedt.graphic.PicPoint;

import java.awt.*;
import java.io.StringWriter;

import static jpicedt.format.output.latex.LatexConstants.*;
import static jpicedt.graphic.model.PicText.*;

/**
 * An interface that specifies common formatting behaviours for Drawable objects
 */
public class PicTextFormatter extends AbstractFormatter {

	/** the Element this formater acts upon */
	private PicText        element;
	private LatexFormatter factory;

   /**
	* @since jPicEdt 1.6
	*/
	public Element getElement(){ return element;}

	/**
	 *
	 */
	public PicTextFormatter(PicText element, LatexFormatter factory){
		this.element = element;
		this.factory=factory;
	}

	/**
	  * @return A string representation of a PicText object in the LaTeX format
	  * We don't need PicEdt special comments (e.g. %Ellipse... whith the whole bunch of parameters)
	  * for PicText objects, since the formating produces a single \makebox command, which can be easily parsed.
	  * \makebox(0,0)[b]{hello} &rarr; not framed
	  * \framebox{$x=1$} &rarr; framed
	  * There's no support for dashboxes any more, use a PicRectangle in conjonction with a makebox instead.
	  *
	  * @since PicEdt 1.0
	  */
	public String format(){
		try
		{
			StringWriter buf = new StringWriter(100);

			// put command :
			buf.write("\\put");
			buf.write(element.getCtrlPt(PicText.P_ANCHOR,null).toString());
			buf.write("{\\");
			if (!element.isFramed()) { // \\makebox(0,0)[bl]{hello} -> not framed
				buf.write("makebox(0,0)");
				buf.write("[");

				switch (element.getVertAlign()){
				case TOP: buf.write("t"); break;
				case BOTTOM: buf.write("b"); break;
				default: buf.write("c"); // middle or baseline
				}

				switch (element.getHorAlign()){
				case LEFT: buf.write("l");break;
				case RIGHT: buf.write("r"); break;
				default: buf.write("c");
				}

				buf.write("]");
			}
			else { // \\framebox{$x=1$} -> framed
				buf.write("framebox");
			}

			buf.write("{");
			factory.textWriteMultiLine(buf,element);
			buf.write("}");
			buf.write("}");buf.write(factory.getLineSeparator());buf.write(factory.getLineSeparator());
			return buf.toString();
		}
		catch(java.io.IOException ioEx)
		{
			System.err.println("Error formatting '" + element.toString() + "':");
			ioEx.printStackTrace();
			return null;
		}
		
	}


} // class PicTextLatexFormatter
