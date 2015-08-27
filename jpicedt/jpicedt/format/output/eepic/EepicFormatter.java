// EepicFormatter.java --- -*- coding: iso-8859-1 -*-
//  jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999-2006  Sylvain Reynal
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
package jpicedt.format.output.eepic;

import jpicedt.format.output.util.*;
import jpicedt.graphic.io.formatter.*;
import jpicedt.format.output.latex.*;
import jpicedt.graphic.*;
import jpicedt.graphic.model.*;

import java.awt.*;
import java.awt.geom.*;
import java.util.Properties;

import static jpicedt.format.output.eepic.EepicConstants.*;
import static jpicedt.format.output.eepic.EepicConstants.*;
import static jpicedt.graphic.model.PicAttributeName.*;

/**
 * Formatter for the eepic package.
 */
public class EepicFormatter extends LatexFormatter  {

	private static double thinLinesMaxWidth = DEFAULT_THIN_LINES_MAX_WIDTH;
	private static double thickLinesMaxWidth = 	DEFAULT_THICK_LINES_MAX_WIDTH;
	private static String fileWrapperProlog = EepicConstants.DEFAULT_FILE_WRAPPER_PROLOG;
	private static String fileWrapperEpilog = EepicConstants.DEFAULT_FILE_WRAPPER_EPILOG;


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
	 * Constructor using default properties values
	 */
	public EepicFormatter(){
		super();
		map(PicEllipse.class, PicEllipseFormatter.class);
		map(PicParallelogram.class, PicParallelogramFormatter.class);
		map(AbstractCurve.class, AbstractCurveFormatter.class);
	}

	/**
	 * Configure static fields using the given Properties object
	 * @param preferences used to read shared parameters
	 *        If null, default values are used.
	 */
	public static void configure(Properties preferences){
		thinLinesMaxWidth = Double.parseDouble(preferences.getProperty(EepicConstants.KEY_THIN_LINES_MAXWIDTH,PEToolKit.doubleToString(EepicConstants.DEFAULT_THIN_LINES_MAX_WIDTH)));
		thickLinesMaxWidth = Double.parseDouble(preferences.getProperty(EepicConstants.KEY_THICK_LINES_MAXWIDTH,PEToolKit.doubleToString(EepicConstants.DEFAULT_THICK_LINES_MAX_WIDTH)));
		fileWrapperProlog = preferences.getProperty(EepicConstants.KEY_FILE_WRAPPER_PROLOG,EepicConstants.DEFAULT_FILE_WRAPPER_PROLOG);

		fileWrapperEpilog = preferences.getProperty(EepicConstants.KEY_FILE_WRAPPER_EPILOG,EepicConstants.DEFAULT_FILE_WRAPPER_EPILOG);
	}	

	/* 
	 * @return a Formatter able to format the given Element according to the format of this factory
	 *
	public Formatter createFormatter(Element e){
		if (e instanceof PicEllipse) return new PicEllipseFormatter((PicEllipse)e,this);
		if (e instanceof PicParallelogram) return new PicParallelogramFormatter((PicParallelogram)e,this);
		if (e instanceof AbstractCurve) return new AbstractCurveFormatter((AbstractCurve)e,this);
		return super.createFormatter(e);
	}*/

	/** 
	 * @return a Formatter able to format the given Drawing according to the format of this factory ;
	 *         this may reliy on calls to <code>createFormatter(Element e)</code> on the elements
	 *         of the drawing, plus creating auxiliary
	 * @param outputConstraint constraint used by the factory to create a specific Formatter on-the-fly
	 */
	public Formatter createFormatter(Drawing d, Object outputConstraint){
		return new DrawingFormatter(d, outputConstraint);
	}


	////////////////////////////////////////////////////////////////////////////////////
	//// TOOLKIT
	////////////////////////////////////////////////////////////////////////////////////

	/**
	 * <p>Create a string representation of the thickness command for the given PicObjet in the eepic format,
	 *  and append it to the given StringBuffer.</p>
	 * <p>Such a command should preceed every object command. This string is CR-terminated.</p>
	 * @since jpicedt 1.3.2
	 */
	public void appendThicknessString(StringBuffer buf, Element obj){

		double thickness = ((Double)obj.getAttribute(LINE_WIDTH)).doubleValue();
		if (thickness < thinLinesMaxWidth) buf.append("\\thinlines"+CR_LF); 
		else if (thickness < thickLinesMaxWidth) buf.append("\\thicklines"+CR_LF);
		else buf.append("\\Thicklines"+CR_LF);
	}

	/**
	 * Append a \\dashline with the given points and dash value to the given buffer
	 */
	protected void appendDashLine(StringBuffer buf, PicPoint p1, PicPoint p2, double dash){
		buf.append("\\dashline{");
		buf.append(PEToolKit.doubleToString(dash));
		buf.append("}");
		buf.append(p1);
		buf.append(p2);
		buf.append(getLineSeparator());
	}
		

	////////////////////////////////////////////////////////////////////////////////////
	//// DRAWING
	////////////////////////////////////////////////////////////////////////////////////
	/**
	 *
	 */
	public class DrawingFormatter extends LatexFormatter.DrawingFormatter {

		/**
		 * init this formater for the given drawing
		 */
		public DrawingFormatter(Drawing drawing, Object outputConstraint){
			super(drawing, outputConstraint);
		}

		/**
		 * @return the content-type specific header
		 */
		protected String createHeader(){
			return "%Eepic content-type (epic.sty and eepic.sty packages needed)"+getLineSeparator()+
			"%Add \\usepackage{epic,eepic} in the preambule of your LaTeX file"+getLineSeparator()+
			"%You can rescale the whole picture (to 80% for instance) by using the command \\def"+
				   LatexConstants.RESCALING_TEX_FUNCTION+"{0.8}"+getLineSeparator();

		}
	}
}

