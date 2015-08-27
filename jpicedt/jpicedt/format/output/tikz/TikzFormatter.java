// TikzFormatter.java --- -*- coding: iso-8859-1 -*-
// Copyright 2009/2013 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: TikzFormatter.java,v 1.12 2013/06/18 20:48:27 vincentb1 Exp $
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

import java.util.BitSet;
import java.util.Properties;
import java.util.Stack;
import java.awt.geom.Rectangle2D;
import jpicedt.format.output.util.*;
import jpicedt.graphic.PicPoint;
import jpicedt.graphic.io.formatter.AbstractFormatterFactory;
import jpicedt.graphic.io.formatter.AbstractDrawingFormatter;
import jpicedt.graphic.io.formatter.Formatter;
import jpicedt.graphic.io.formatter.FormatterFactory;
import jpicedt.graphic.model.AbstractCurve;
import jpicedt.graphic.model.Drawing;
import jpicedt.graphic.model.Element;
import jpicedt.graphic.model.PicAttributeSet;
import jpicedt.graphic.model.PicEllipse;
import jpicedt.graphic.model.PicGroup;
import jpicedt.graphic.model.PicParallelogram;
import jpicedt.graphic.model.PicText;

import java.io.IOException;
import java.io.StringWriter;

import static jpicedt.format.output.tikz.TikzConstants.*;

/**
 * Formateur TikZ. Fabrique pour convertir un dessin depuis le format jPicEdt
 * vers le format Tikz.
 * @since jPicEdt 1.6
 */
public class TikzFormatter  extends AbstractFormatterFactory
{

	static TikzCustomProperties tikzCustomProperties = new TikzCustomProperties(0);
	private PicAttributeSet defaultAttributes = new PicAttributeSet();

	private Stack<PicAttributeSet>  defaultAttributesStack
	= new Stack<PicAttributeSet>();

	protected static String fileWrapperProlog = PRPTY_KEY_DEFAULT_TABLE[1];
	protected static String fileWrapperEpilog = PRPTY_KEY_DEFAULT_TABLE[3];

	protected String eoCmdMark = ";" + lineSeparator;
	public String getEOCmdMark(){ return eoCmdMark;}

	public void setLineSepartor(String lineSeparator){
		super.setLineSeparator(lineSeparator);
		eoCmdMark = ";" + lineSeparator;
	}

	public TikzCustomization getCustomProperties(){ return tikzCustomProperties; }


	/**
	 * @return le prologue de formatage d'un fichier autonome (stand-alone)
	 * correspondant au type de contenu offrant cette interface.
	 */
	protected String getFileWrapperProlog(){ return fileWrapperProlog;}

	/**
	 * @return l'�pilogue de formatage d'un fichier autonome (stand-alone)
	 * correspondant au type de contenu offrant cette interface.
	 */
	protected String getFileWrapperEpilog(){ return fileWrapperEpilog;}

	/**
	 * Constructeur avec les propri�t� par d�faut.
	 */
	public TikzFormatter(){
		super();
		map(AbstractCurve.class, AbstractCurveFormatter.class);
		map(PicEllipse.class, PicEllipseFormatter.class);
		map(PicParallelogram.class, PicParallelogramFormatter.class);
		map(PicText.class, PicTextFormatter.class);
		map(PicGroup.class, PicGroupFormatter.class);
	}

	/**
	 * Configure static fields using the given Properties object
	 * @param preferences used to read shared parameters
	 *        If null, default values are used.
	 */
	public static void configure(Properties preferences){
		TikzFormatter.fileWrapperProlog = preferences.getProperty(
			PRPTY_KEY_DEFAULT_TABLE[0],PRPTY_KEY_DEFAULT_TABLE[1]);
		TikzFormatter.fileWrapperEpilog = preferences.getProperty(
			PRPTY_KEY_DEFAULT_TABLE[2],PRPTY_KEY_DEFAULT_TABLE[3]);
		tikzCustomProperties.load(preferences);

	}

	/** Ins�re dans le tampon <code>buf</code> la macro <code>\draw</code>
	 * avec les param�tres correspondant � l'�l�ment de dessin <code>e</code>.
	 * @param buf Tampon o� ins�rer la macro <code>\draw</code>
	 * @param e �lement de dessin pour lequel la macro <code>\draw</code> est
	 * ins�r�e dans le tampon <code>buf</code>.
	 */
	public void draw(StringBuffer buf,Element e,BitSet mask){
		ParameterString param = TikzUtilities.createParameterString(e,defaultAttributes,tikzCustomProperties,
																	mask);
		TikzUtilities.addUserDefinedColourDefinitions(buf,param,this);
		buf.append("\\" + param.getDrawCommand().toString() +" ");
		if(param.getParameterBuffer().length() != 0)
		{
			buf.append('[');
			buf.append(param.getParameterBuffer());
			buf.append(']');
		}
	}
	public void draw(StringBuffer buf,Element e){
		draw(buf,e,TikzConstants.EMPTY_BITSET/*do not swap arrows or do otherthings*/);
	}

	public void drawWithOptions(StringBuffer buf,Element e){
		drawWithOptions(buf,e,TikzConstants.EMPTY_BITSET/*do not swap arrows or do other things*/);
	}

	public void drawWithOptions(StringBuffer buf,Element e,BitSet mask){
		ParameterString param = TikzUtilities.createParameterString(e,defaultAttributes,tikzCustomProperties,
																	mask);
		TikzUtilities.addUserDefinedColourDefinitions(buf,param,this);
		buf.append("\\" + param.getDrawCommand().toString() +" [");
		if(param.getParameterBuffer().length() != 0)
		{
			buf.append(param.getParameterBuffer());
			buf.append(',');
		}

	}

	/** formatteur de document au format TikZ pour un dessin Drawing donn�.
	 * @since jPicEdt 1.6
	 */
	class DrawingFormatter extends AbstractDrawingFormatter
	{

		/** le Drawing sur lequel ce  formateur agit */
		protected TikzFormatter   factory;

		/**
		 * construit ce formateur pour le dessin donn�.
		 * @since jPicEdt 1.6
		 */
		public DrawingFormatter
		(
			Drawing drawing,
			Object outputConstraint,
			TikzFormatter factory){
			super(drawing,outputConstraint);
			this.factory = factory;
		}


		/**
		 * Forme une en-t�te donnant des informations supl�mentaires li�es au
		 * type de contenu. En l'occurrence il s'agit des pr�f�rences
		 * utilisateur avec lesquelles le document est format�.
		 * @return l'en-t�te sp�cifique au type de contenu.
		 * @since jPicEdt 1.6
		 */
		protected String createHeader(){
			StringBuffer header =  new StringBuffer(80);
			header.append("");
			return header.toString();
		}
		/**
		 * @since jPicEdt 1.6
		 */
		public String format() throws IOException{

			StringWriter buf = new StringWriter(200);
			buf.write(createHeader());


			if (outputConstraints == FormatterFactory.MAKE_STANDALONE_FILE)
				factory.stringWriteMultiLine(buf,factory.getFileWrapperProlog());

			buf.write("\\ifx"+TikzConstants.RESCALING_TEX_MACRO+"\\undefined\\def"+
				   TikzConstants.RESCALING_TEX_MACRO+"{1}\\fi"+lineSeparator);// allow the user to change scale using \jPicScale
			buf.write("\\unitlength "+TikzConstants.RESCALING_TEX_MACRO+" mm");
			buf.write(lineSeparator);


			buf.write("\\begin{tikzpicture}[x=\\unitlength,y=\\unitlength,inner sep=0pt]"
					   +lineSeparator);

			if(tikzCustomProperties.getClipBasedOnJPE_BB()){
				Rectangle2D box = drawing.getBoundingBox();
				if(box != null)
				{
					buf.write("\\clip ");
					PicPoint pt = new PicPoint(box.getMinX(),box.getMinY());
					buf.write(pt.toString());
					buf.write(" rectangle ");
					pt.translate(box.getWidth(),box.getHeight());
					buf.write(pt.toString());
					buf.write(factory.getEOCmdMark());
				}
			}



			for (Element e:drawing)
			{
				buf.write(factory.createFormatter(e).format());
			}

			buf.write("\\end{tikzpicture}"+lineSeparator);

			if (outputConstraints == FormatterFactory.MAKE_STANDALONE_FILE)
				factory.stringWriteMultiLine(buf,factory.getFileWrapperEpilog());

			return buf.toString();
		}
	};


	/**
	 * @return un formatteur adapt� au formatter du dessin (Drawing) pass� en
	 *         param�tre au format TikZ.
	 * @param outputConstraints constraintes utilis�e par la fabrique pour
	 *        cr�er un formatteur � la vol�e
	 * @since jPicEdt 1.6
	 */
	public Formatter createFormatter(Drawing d, Object outputConstraints){
		return new DrawingFormatter(d, outputConstraints,this);
	}

}



/// TikzFormatter.java ends here
