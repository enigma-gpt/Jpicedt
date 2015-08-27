// PicTextFormatter.java --- -*- coding: iso-8859-1 -*-
// Copyright 2008/2012 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: PicTextFormatter.java,v 1.8 2013/03/27 07:11:10 vincentb1 Exp $
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
package jpicedt.format.output.dxf;

import jpicedt.graphic.*;
import jpicedt.graphic.model.*;
import jpicedt.graphic.io.formatter.Formatter;
import jpicedt.graphic.io.formatter.AbstractFormatter;
import jpicedt.graphic.PicPoint;
import jpicedt.Log;

import java.lang.Math;
import java.awt.*;

import static jpicedt.format.output.dxf.DXFConstants.DXFVersion.*;
import static jpicedt.graphic.model.PicText.VertAlign.*;
import static jpicedt.graphic.model.PicText.HorAlign.*;


/**
 * Formatteur de PicText vers le format DXF (marque d�pos�e). Selon les pr�f�rences utilisateur
 * le r�sultat est un TEXT ou un MTEXT. Pour l'instant seul TEXT est support�.
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela�che</a>
 * @since jpicedt 1.6
 */
public class PicTextFormatter extends AbstractFormatter {

	/** l' Element sur lequel ce formatteur agit */
	protected PicText text;
	protected DXFFormatter factory;

   /**
	* @since jPicEdt 1.6
	*/
	public Element getElement(){ return text;}

	/** Construit un formatteur de PicText pour un type de contenu DXF (marque d�pos�e).
	 * @param text l'objet de classe PicText � formatter
	 * @param factory la fabrique de formatage DXF (marque d�pos�e) � utiliser pour le formatage.
	 * @since jPicEdt 1.6
	 */
	public PicTextFormatter(PicText text, DXFFormatter factory)
		{
			this.text = text;
			this.factory = factory;
		}

	/**
	 * Tampon permettant de grouper les caract�res par blocs de 255 en g�rant la transition de group code id.
	 * @since jPicEdt 1.6
	 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela�che</a>
	 */
	class Buffer
	{
		char[] bufferContent = new char[255];
		int bufferOccupancy = 0;
		int groupCode = 1;
		DXFStringBuffer ret;
		/**
		 * @since jPicEdt 1.6
		 */
		void pushChar(char c){
			bufferContent[bufferOccupancy ++] = c;
			if(bufferOccupancy == 255)
			{
				ret.tagVal(groupCode,bufferContent);
				groupCode = 3;
				bufferOccupancy = 0;
			}
		}
		/**
		 * @since jPicEdt 1.6
		 */
		void flush(){
			if(bufferOccupancy != 0)
			{
				ret.tagVal(groupCode,bufferContent,bufferOccupancy);
			}
		}
		/**
		 * @since jPicEdt 1.6
		 */
		Buffer(DXFStringBuffer ret){
			this.ret = ret;
		}
	};

	/**
	 * @since jPicEdt 1.6
	 */
	public String format(){
		DXFStringBuffer ret = new DXFStringBuffer(127,factory.getLineSeparator());

		if(factory.showJpic())
		{
			ret.comment(text.toString());
		}

		PicPoint pt = text.getCtrlPt(text.getFirstPointIndex(),null);

		ret.tagVal(0,"TEXT");
		factory.commonTagVal(ret);
		if(factory.getDXFVersion().getValue() >= AUTO_CAD_RELEASE_13.getValue())
		{
			ret.tagVal(100,"AcDbText");
			int verticalAlignment = 0;
			switch(text.getVertAlign())
			{
			case BASELINE: /*n�ant */ break;
			case BOTTOM	 : verticalAlignment = 1; break;
			case CENTER	 : verticalAlignment = 2; break;
			case TOP     : verticalAlignment = 3; break;
			}
			int horizontalAlignment = 0;
			switch(text.getHorAlign())
			{
			case CENTER: horizontalAlignment = 1; break;
			case LEFT  : /* n�ant */ break;
			case RIGHT : horizontalAlignment = 2; break;
			default:
				Log.error("valeur renvoy�e par text.getHorAlign() inattendue");
				break;
			}
			if(horizontalAlignment != 0)
				ret.tagVal(72,horizontalAlignment);
			if(verticalAlignment != 0)
				ret.tagVal(73,verticalAlignment);
			ret.tagVal(10,pt.getX());
			ret.tagVal(20,pt.getY());
			if(horizontalAlignment != 0 || verticalAlignment != 0){
				ret.tagVal(11,pt.getX());
				ret.tagVal(21,pt.getY());
			}
		}
		else
		{
			boolean nonLeftAligned = false;
			switch(text.getHorAlign()){
			case CENTER:
				switch(text.getVertAlign()){
				case BASELINE:
					ret.tagVal(72,1);
					nonLeftAligned = true;
					break;
				case CENTER:
					ret.tagVal(72,4);
					nonLeftAligned = true;
					break;
				}
				break;
			case RIGHT:
				ret.tagVal(72,2);
				nonLeftAligned = true;
				break;
			}
			ret.tagVal(10,pt.getX());
			ret.tagVal(20,pt.getY());
			if(nonLeftAligned){
				ret.tagVal(11,pt.getX());
				ret.tagVal(21,pt.getY());
			}
		}

		double rotationAngle = text.getAttribute(PicAttributeName.TEXT_ROTATION); // in degrees
		if(rotationAngle != 0.0)
			ret.tagVal(50,rotationAngle);


		String textVal = text.getText();
		Buffer buffer =  new Buffer(ret);

		for(int i = 0; i < textVal.length(); ++i)
		{
			char c = textVal.charAt(i);
			if(c < 32)
			{
				buffer.pushChar('^');
				buffer.pushChar((char)(c + 64));
			}
			else if(c == '^')
			{
				buffer.pushChar('^');
				buffer.pushChar(' ');
			}
			else
				buffer.pushChar(c);
		}
		buffer.flush();


		return ret.toString();

	}

};


/// PicTextFormatter.java ends here
