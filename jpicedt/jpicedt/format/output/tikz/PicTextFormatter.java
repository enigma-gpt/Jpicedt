// PicTextFormatter.java --- -*- coding: iso-8859-1 -*-
// Copyright 2009/2013 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: PicTextFormatter.java,v 1.7 2013/03/27 07:22:39 vincentb1 Exp $
// Keywords: Tikz, PGF
// X-URL: http://www.jpicedt.org/
//
// Ce logiciel est régi par la licence CeCILL soumise au droit français et respectant les principes de
// diffusion des logiciels libres. Vous pouvez utiliser, modifier et/ou redistribuer ce programme sous les
// conditions

// de la licence CeCILL telle que diffusée par le CEA, le CNRS et l'INRIA sur le site
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
import jpicedt.graphic.model.Element;
import java.lang.String;
import java.io.StringWriter;
import jpicedt.graphic.PEToolKit;
import jpicedt.graphic.io.formatter.Formatter;
import jpicedt.graphic.io.formatter.AbstractFormatter;
import jpicedt.graphic.model.PicAttributeName;
import jpicedt.graphic.model.PicText.HorAlign;
import jpicedt.graphic.model.PicText.VertAlign;
import jpicedt.graphic.model.PicText;
import static jpicedt.graphic.model.PicAttributeName.TEXT_ROTATION;
import static jpicedt.graphic.model.PicText.HorAlign.*;
import static jpicedt.graphic.model.PicText.VertAlign.*;
/**
 * Formateur de PicText au format TikZ.
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Belaïche</a>
 * @since jpicedt 1.6
 */
public class PicTextFormatter extends AbstractFormatter
{
	/** les éléments sur lesquels ce formatteur agit */
	protected PicText text;
	protected TikzFormatter factory;

   /**
	* @since jPicEdt 1.6
	*/
	public Element getElement(){ return text;}

	/** */
	public PicTextFormatter(PicText text, TikzFormatter factory){
		this.text = text;
		this.factory=factory;
	}


   /**
	* @return une chaîne de caractères contenant le code TikZ formatant le
	* PicText passé à la construction.
	* @since jPicEdt 1.6
	*/
	public String format(){
		try
		{
			StringWriter buf = new StringWriter(100);
			factory.draw(buf.getBuffer(),text);
			buf.write(text.getCtrlPt(PicText.P_ANCHOR,null).toString());
			buf.write(" node ");

			double angle = ((Double)text.getAttribute(TEXT_ROTATION)).doubleValue();// degrès
			if(text.getVertAlign()!= VertAlign.CENTER
			   || text.getHorAlign()!= HorAlign.CENTER
			   || angle != 0.0)
			{
				buf.write('[');
				int keyInserted = 0;
				if(text.getVertAlign()!= VertAlign.CENTER
				   || text.getHorAlign()!= HorAlign.CENTER)
				{
					buf.write("anchor=");
					++keyInserted;
					switch (text.getVertAlign()){
					case TOP:
						buf.write("north");
						break;
					case BOTTOM:
						buf.write("south");
						break;
					case BASELINE:
						buf.write("base");
						break;
					default:
						--keyInserted;
						break;
					}

					if(keyInserted++ != 0)
						buf.write(' ');

					switch (text.getHorAlign()){
					case LEFT:
						buf.write("west");
						break;
					case RIGHT:
						buf.write("east");
						break;
					default:
						--keyInserted;
						break;
					}
				}
				if(angle != 0.0)
				{
					if(keyInserted++ != 0)
						buf.write(',');
					buf.write("rotate=");
					buf.write(PEToolKit.doubleToString(angle));
				}
				buf.write(']');
			}

			buf.write('{');

			factory.textWriteMultiLine(buf,text);

			buf.write("}" + factory.getEOCmdMark());
			return buf.toString();
		}
		catch(java.io.IOException ioEx)
		{
			System.err.println("Error formatting '" + text.toString() + "':");
			ioEx.printStackTrace();
			return null;
		}
	}

}




/// PicTextFormatter.java ends here
