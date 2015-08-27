// LatexFormatter.java --- -*- coding: iso-8859-1 -*-
// jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2006  Sylvain Reynal
//
// Author: Sylvain Reynal
//         Département de Physique
//         École Nationale Supérieure de l'Électronique et de ses Applications (ÉNSÉA)
//         6, avenue du Ponceau
//         95014 CERGY CEDEX
//         FRANCE
//
//         Tel : +33 130 736 245
//         Fax : +33 130 736 667
//         e-mail : reynal@ensea.fr
//
// Version: $Id: LatexFormatter.java,v 1.23 2013/03/27 07:23:32 vincentb1 Exp $
// Keywords:
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
package jpicedt.format.output.latex;

import jpicedt.format.output.util.*;
import jpicedt.graphic.io.formatter.Formatter;
import jpicedt.graphic.io.formatter.FormatterFactory;
import jpicedt.graphic.io.formatter.AbstractDrawingFormatter;
import jpicedt.graphic.io.formatter.AbstractFormatterFactory;
import jpicedt.graphic.*;
import jpicedt.graphic.model.*;

import java.awt.*;
import java.awt.geom.*;
import java.io.StringWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.Iterator;

import static jpicedt.format.output.latex.LatexConstants.*;
import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.StyleConstants.*;

/**
 * LaTeX 'picture-env' formater
 * @since jpicedt 1.0 (code refactoring 1.3.2, 1.5)
 */
public class LatexFormatter extends AbstractFormatterFactory {

	/* package */ //static double lineThickness= DEFAULT_LINE_THICKNESS;
	/* package */ static double emLineLength= DEFAULT_EM_LINE_LENGTH;
	/* package */ static double maxLatexCircleDiameter= DEFAULT_MAX_CIRCLE_DIAMETER;
	/* package */ static double maxLatexDiskDiameter= DEFAULT_MAX_DISK_DIAMETER;
	/* package */ static double maxEmCircleSegmentLength= DEFAULT_MAX_EM_CIRCLE_SEGMENT_LENGTH;
	/* package */ static double maxEmLineSlope= DEFAULT_MAX_EM_LINE_SLOPE; // lineToLatexString
	/* package */ static double minEmLineSlope= 1.0/DEFAULT_MAX_EM_LINE_SLOPE;
	protected static String fileWrapperProlog = DEFAULT_FILE_WRAPPER_PROLOG;
	protected static String fileWrapperEpilog = DEFAULT_FILE_WRAPPER_EPILOG;

	/**
	 * @return le prologue de formatage d'un fichier autonome (stand-alone)
	 * correspondant au type de contenu offrant cette interface.
	 */
	protected String getFileWrapperProlog(){ return fileWrapperProlog;}

	/**
	 * @return l'épilogue de formatage d'un fichier autonome (stand-alone)
	 * correspondant au type de contenu offrant cette interface.
	 */
	protected String getFileWrapperEpilog(){ return fileWrapperEpilog;}


	/**
	 * Constructor using default properties values
	 */
	public LatexFormatter(){
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
		//lineThickness = Double.parseDouble(preferences.getProperty(KEY_LINE_THICKNESS,PEToolKit.doubleToString(DEFAULT_LINE_THICKNESS)));
		emLineLength = Double.parseDouble(preferences.getProperty(KEY_EM_LINE_LENGTH,PEToolKit.doubleToString(DEFAULT_EM_LINE_LENGTH)));
		maxLatexCircleDiameter = Double.parseDouble(preferences.getProperty(KEY_MAX_CIRCLE_DIAMETER,PEToolKit.doubleToString(DEFAULT_MAX_CIRCLE_DIAMETER)));
		maxLatexDiskDiameter = Double.parseDouble(preferences.getProperty(KEY_MAX_DISK_DIAMETER,PEToolKit.doubleToString(DEFAULT_MAX_DISK_DIAMETER)));
		maxEmCircleSegmentLength = Double.parseDouble(preferences.getProperty(KEY_MAX_EM_CIRCLE_SEGMENT_LENGTH,PEToolKit.doubleToString(DEFAULT_MAX_EM_CIRCLE_SEGMENT_LENGTH)));
		maxEmLineSlope = Double.parseDouble(preferences.getProperty(KEY_MAX_EM_LINE_SLOPE,PEToolKit.doubleToString(DEFAULT_MAX_EM_LINE_SLOPE)));
		minEmLineSlope= 1.0/maxEmLineSlope;
		fileWrapperProlog = preferences.getProperty(KEY_FILE_WRAPPER_PROLOG,DEFAULT_FILE_WRAPPER_PROLOG);
		fileWrapperEpilog = preferences.getProperty(KEY_FILE_WRAPPER_EPILOG,DEFAULT_FILE_WRAPPER_EPILOG);
	}

	/*
	 * @return a Formatter able to format the given Element in the LaTeX picture env. format
	 *
	public Formatter createFormatter(Element e){
		if (e instanceof AbstractCurve) return new AbstractCurveFormatter((AbstractCurve)e,this);
		if (e instanceof PicEllipse) return new PicEllipseFormatter((PicEllipse)e,this);
		if (e instanceof PicParallelogram) return new PicParallelogramFormatter((PicParallelogram)e,this);
		// [SR:en_cours] if (e instanceof PicCircleFrom3Points) return new PicEllipseFormatter(((PicCircleFrom3Points)e).toEllipse(),this);//[pending: to be improved: add comments for the 3 control points]
		if (e instanceof PicText) return new PicTextFormatter((PicText)e,this);
		if (e instanceof PicGroup) return new PicGroupFormatter((PicGroup)e,this);
		return new NonSupportedFormatter(e);
	}

	class NonSupportedFormatter implements Formatter {
		Element element;
		NonSupportedFormatter(Element e){
			this.element = e;
		}
		public String format(){
			return "% sorry, " + element.getName() + " is not supported yet for the LaTeX-picture content-type.";
		}
	}*/

	/**
	 * @return a Formatter able to format the given Drawing in the LaTeX picture environment format ;
	 *         this may reliy on calls to <code>createFormatter(Element e)</code> on the elements
	 *         of the drawing, plus creating auxiliary
	 * @param outputConstraints constraint used by the factory to create a specific Formatter on-the-fly
	 */
	public Formatter createFormatter(Drawing d, Object outputConstraints){
		return new DrawingFormatter(d, outputConstraints);
	}


	//////////////////////////////////////////////////////////
	/// Toolkit
	//////////////////////////////////////////////////////////

	/**
	 * <p>Create a string representation of the thickness command for the given PicObjet in the LaTeX format,
	 *  and append it to the given StringBuffer. </p>
	 * <p>Such a command should preceed every object command. This string is CR-terminated.</p>
	 * @since jpicedt 1.3.2
	 */
	public void appendThicknessString(StringBuffer buf, Element obj){

		buf.append("\\linethickness{");
		buf.append(PEToolKit.doubleToString(((Double)obj.getAttribute(LINE_WIDTH)).doubleValue()));
		buf.append("mm}");
		buf.append(getLineSeparator());
	}

	/*private static final int NEG_GREATER_THAN_1 = 1;
	private static final int NEG_LOWER_THAN_1 = 2;
	private static final int HORIZONTAL = 3;
	private static final int POS_LOWER_THAN_1 = 4;
	private static final int POS_GREATER_THAN_1 = 5;
	private static final int VERTICAL = 6;*/

	// used by lineToLatexString :
	private static enum LatexSlope {NEG_GREATER_THAN_1, NEG_LOWER_THAN_1, HORIZONTAL, POS_LOWER_THAN_1, POS_GREATER_THAN_1, VERTICAL};


	/**
	 * Computes a LaTeX string for a line segment, given its two end-points and
	 * decoration parameters.
	 *
	 * @param pt0 the start point of the line segment
	 * @param pt1 the end point of the line segment
	 * @param leftArrow first arrow (we make no distinction b/w Arrow types, that is, we simply draw an arrow by using \\vector)
	 * @param rightArrow second arrow
	 * @param dash The dash step in mm ; must be .le. 0 if there's no dash
	 *
	 * @return a LaTeX string (that is, not a "eepic" string !)
	 * @since picedt 1.0
	 */
	public String lineToLatexString(PicPoint pt0, PicPoint pt1, ArrowStyle leftArrow, ArrowStyle rightArrow, double dash){
		return lineToLatexString(pt0.x, pt0.y, pt1.x, pt1.y, leftArrow, rightArrow, dash);
	}

	/**
	 * Computes a LaTeX string for a line segment, given its two end-points and
	 * decoration parameters.
	 *
	 * @param x0 The X coordinate (in mm) of the start point of the line segment
	 * @param y0 The Y coordinate (in mm) of the start point of the line segment
	 * @param x1 The X coordinate (in mm) of the end point of the line segment
	 * @param y1 The Y coordinate (in mm) of the end point of the line segment
	 * @param leftArrow first arrow (we make no distinction b/w Arrow types, that is, we simply draw an arrow by using \\vector)
	 * @param rightArrow second arrow
	 * @param dash The dash step in mm ; must be .le. 0 if there's no dash
	 *
	 * @return a LaTeX string (that is, not a "eepic" string !)
	 * @since picedt 1.0
	 */
	public String lineToLatexString(double x0, double y0, double x1, double y1,
	                                       ArrowStyle leftArrow, ArrowStyle rightArrow, double dash){

		double trueLineLen = emLineLength; // en mm
		String lineLenStr = PEToolKit.doubleToString(trueLineLen);
		StringBuffer buf = new StringBuffer(50);
		boolean isFirstArrow = (leftArrow != ArrowStyle.NONE);
		boolean isSecondArrow = (rightArrow != ArrowStyle.NONE);

		// evaluate slope type :
		double slope =  (y1 - y0)/(x1 - x0);

		LatexSlope slopeType=LatexSlope.HORIZONTAL; // default
		if (slope < -1) slopeType = LatexSlope.NEG_GREATER_THAN_1;
		else if (slope < 0 && slope >= -1) slopeType = LatexSlope.NEG_LOWER_THAN_1;
		else if (slope > 0 && slope <= 1) slopeType = LatexSlope.POS_LOWER_THAN_1;
		else if (slope > 1) slopeType = LatexSlope.POS_GREATER_THAN_1;
		if (Math.abs(slope) > maxEmLineSlope) { // nearly vertical lines -> approximate to exact vertical lines
			slopeType = LatexSlope.VERTICAL;
			x1 = x0;
		}
		else if (Math.abs(slope) < minEmLineSlope) { // nearly horizontal lines -> approx. to exact horizontal lines
			slopeType = LatexSlope.HORIZONTAL;
			y1 = y0;
		}
		//if (x1==x0) slopeType = VERTICAL;

		// always place Point NÂ°0 on the left side, and Point NÂ°1 on the right side, except for vertical lines :
		if (x0 > x1) {
			// swap first and second point :
			double xyBuf = x1;
			x1 = x0;
			x0 = xyBuf;
			xyBuf = y1;
			y1 = y0;
			y0 = xyBuf;
			// swap arrows :
			boolean boolBuf = isFirstArrow;
			isFirstArrow = isSecondArrow;
			isSecondArrow = boolBuf;
		}

		// VERTICAL lines : always place Point NÂ°1 ABOVE Point NÂ°0
		if (x0 == x1){
			if (y0 == y1) return getLineSeparator(); // line reduced to a point !!
			if (y0 > y1) { // swap y0 and y1
				double xyBuf;
				xyBuf = y1;
				y1 = y0;
				y0 = xyBuf;
				// swap arrows :
				boolean boolBuf = isFirstArrow;
				isFirstArrow = isSecondArrow;
				isSecondArrow = boolBuf;
			}
		}

		PicPoint pt0 = new PicPoint(x0,y0);
		PicPoint pt1 = new PicPoint(x1,y1);

		String lineTypeStr;

		double length = Math.sqrt((x1-x0)*(x1-x0)+(y1-y0)*(y1-y0));

		// dash :
		double nDash=0;
		if (dash >= 0){
			// number of dash must always be odd, so that we always get a "- - -" (n=5) pattern and not something like "- - " (n=4) :
			nDash = Math.floor(length/dash/2)+1; // here we'd get "3"
			// now we compute the "real" dash length, which according to the calculation hereabove, is the closest possible value to the dash given as argument :
			dash = length / (nDash*2-1); // here we'd get "L/5" ; this is the length of the dash line, i.e. "-", not "- " !!!
		}

		//System.out.println("slope=" + slopeType);

		///////////////////////////////////////
		// 1Â°) vertical/horizontal lines
		///////////////////////////////////////
		if (slopeType == LatexSlope.VERTICAL || slopeType == LatexSlope.HORIZONTAL){
			double delta; // dx or dy, always > 0
			switch (slopeType){
			case VERTICAL :
				delta = y1-y0;
				lineTypeStr = "{\\line(0,1){";
				break;
			case HORIZONTAL :
				delta = x1-x0;
				lineTypeStr = "{\\line(1,0){";
				break;
			default :
				lineTypeStr = "";
				delta=0;
			}
			// no dash : \\put(x,y){\\line(?,?){delta} Note : delta = length here
			if (dash <= 0){
				buf.append("\\put");
				buf.append(pt0);
				buf.append(lineTypeStr);  // e.g. "{\\line(1,0){"
				buf.append(PEToolKit.doubleToString(delta));
				buf.append("}}");
			}
			// dash : \\multiput(x,y)(dx,dy){n}{\\line(?,?){dash}} :
			else {
				buf.append("\\multiput");
				buf.append(pt0); // (x,y)
				if (slopeType == LatexSlope.VERTICAL) buf.append("(0,"); // vert -> dx=0
				else buf.append("(");
				buf.append(PEToolKit.doubleToString(2*dash)); // vert -> dy ; hor -> dx
				if (slopeType == LatexSlope.VERTICAL) buf.append("){");
				else buf.append(",0){");  // hor -> dy = 0
				buf.append(PEToolKit.intToString(nDash)); // {n}
				buf.append("}");
				buf.append(lineTypeStr);
				buf.append(PEToolKit.doubleToString(dash)); // {dash}
				buf.append("}}");
			}

			if (isSecondArrow){
				buf.append(getLineSeparator());
				buf.append("\\put");
				buf.append(pt1);
				switch(slopeType){
				case VERTICAL :
					buf.append("{\\vector(0,1){");
					break;
				case HORIZONTAL :
					buf.append("{\\vector(1,0){");
					break;
				default:
				}
				buf.append(lineLenStr);
				buf.append("}}");
			}
			if (isFirstArrow){
				buf.append(getLineSeparator());
				buf.append("\\put");
				buf.append(pt0);
				switch(slopeType){
				case VERTICAL :
					buf.append("{\\vector(0,-1){");
					break;
				case HORIZONTAL :
					buf.append("{\\vector(-1,0){");
					break;
				default:
				}
				buf.append(lineLenStr);
				buf.append("}}");
			}
			return buf.toString();
		}
		////////////////////////////////////////////////////
		// 2Â°) other line (neither vertical, nor horizontal)
		////////////////////////////////////////////////////
		else {
			Point ptVecSlope=null;
			if (isFirstArrow || isSecondArrow) {
				ptVecSlope = getXYNearestSlope(slope, isFirstArrow || isSecondArrow);
			}

			double times; // the number of small segments that emulate the whole line [pending] I can't still make it out !!!
			double segLength; // the real length of the emulated segment
			double deltax = x1-x0; // >0
			double deltay = y1-y0; // <0
			double dash_x = dash * deltax / length; // >0 : the horizontal projection of the dash length
			double dash_y = dash * deltay / length; // <0 : the vertical projection of the dash length

			switch (slopeType){

			case NEG_GREATER_THAN_1 :
				lineTypeStr = "{\\line(0,-1){";
				if (dash <= 0){
					times = Math.round(deltax/trueLineLen);
					if (times == 0) times = 1;
					segLength = -deltay/times; // [pending] strange, no ?
				}
				else {
					times = Math.round(dash_x/trueLineLen); // number of small segments used to emulate ONE dash segment
					if (times == 0) times = 1;
					segLength = -dash_y/times;
				}
				break;
			case POS_GREATER_THAN_1 :
				lineTypeStr = "{\\line(0,1){";
				if (dash <= 0){
					times = Math.round(deltax/trueLineLen);
					if (times == 0) times = 1;
					segLength = deltay/times; // [pending] strange, no ?
				}
				else {
					times = Math.round(dash_x/trueLineLen); // number of small segments used to emulate ONE dash segment
					if (times == 0) times = 1;
					segLength = dash_y/times;
				}
				break;
			case POS_LOWER_THAN_1 :
				lineTypeStr = "{\\line(1,0){";
				if (dash <= 0){
					times = Math.round(deltay/trueLineLen);
					if (times == 0) times = 1;
					segLength = deltax/times; // [pending] strange, no ?
				}
				else {
					times = Math.round(dash_y/trueLineLen); // number of small segments used to emulate ONE dash segment
					if (times == 0) times = 1;
					segLength = dash_x/times;
				}
				break;
			case NEG_LOWER_THAN_1 :
				lineTypeStr = "{\\line(1,0){";
				if (dash <= 0){
					times = Math.round(-deltay/trueLineLen);
					if (times == 0) times = 1;
					segLength = deltax/times;
				}
				else {
					times = Math.round(-dash_y/trueLineLen);
					if (times == 0) times = 1;
					segLength = dash_x/times;
				}
				break;
			default:
				lineTypeStr = "";
				times = 0;
				segLength = 0;
			}


			// no dash :
			if (dash <= 0) {
				buf.append("\\multiput");
				buf.append(pt0);
				buf.append("(");
				buf.append(PEToolKit.doubleToString(deltax/times)); // dX
				buf.append(",");
				buf.append(PEToolKit.doubleToString(deltay/times)); // dY
				buf.append("){");
				buf.append(PEToolKit.intToString(times)); // number-of-time
				buf.append("}");
				buf.append(lineTypeStr);
				buf.append(PEToolKit.doubleToString(segLength)); // length
				buf.append( "}}");
			}
			// dash : \\multiput(x,y)(2*dashx,2*dashy){n}{\\multiput(0,0)(dx,dy){N}{\\line(?,?){length}}}
			else {
				buf.append("\\multiput");
				buf.append( pt0 );
				buf.append( "(");
				buf.append( PEToolKit.doubleToString(2*dash_x) );
				buf.append( ",");
				buf.append( PEToolKit.doubleToString(2*dash_y) );
				buf.append("){");
				buf.append( PEToolKit.intToString(nDash) );
				buf.append( "}{" );
				// multiput to emulate dash segment :
				buf.append("\\multiput(0,0)" );
				//buf.append(pt0); // [pending] (0,0) ???
				buf.append("(");
				buf.append(PEToolKit.doubleToString(dash_x/times));
				buf.append(",");
				buf.append(PEToolKit.doubleToString(dash_y/times));
				buf.append("){");
				buf.append(PEToolKit.intToString(times)); // number-of-multiput
				buf.append("}");
				buf.append(lineTypeStr);
				buf.append(PEToolKit.doubleToString(segLength)); // segment length
				buf.append("}}}");
			}

			if (isSecondArrow){
				buf.append(getLineSeparator());
				buf.append("\\put");
				buf.append( pt1 );
				buf.append( "{\\vector(" );
				buf.append( ptVecSlope.x );
				buf.append( "," );
				if (slopeType == LatexSlope.NEG_GREATER_THAN_1 || slopeType == LatexSlope.NEG_LOWER_THAN_1) buf.append("-");
				buf.append( ptVecSlope.y );
				buf.append("){" );
				buf.append( lineLenStr );
				buf.append( "}}");
			}

			if (isFirstArrow){
				buf.append(getLineSeparator());
				buf.append("\\put" );
				buf.append( pt0 );
				buf.append( "{\\vector(-" );
				buf.append( ptVecSlope.x );
				buf.append( "," );
				if (slopeType == LatexSlope.POS_GREATER_THAN_1 || slopeType == LatexSlope.POS_LOWER_THAN_1) buf.append("-");
				buf.append( ptVecSlope.y );
				buf.append("){" );
				buf.append( lineLenStr );
				buf.append( "}}");
			}
			return buf.toString();
		}
	}



	/**
	 * Create a string representation of an arrow in the LaTeX format using \\vector's.
	 * The slope of the vector is as near as possible of the given slope.
	 * @param loc the location of the arrow, in mm (i.e. in the LaTeX coordinate system)
	 * @param dir a vector that indicates the direction of the slope
	 */
	public StringBuffer arrowToLatexString(PicPoint loc, PicPoint dir){

		StringBuffer buf = new StringBuffer(30);
		Point nearestSlope = getXYNearestSlope(dir.y/dir.x, true); // look-up vector, not line

		buf.append("\\put");
		buf.append(loc);
		buf.append("{\\vector(");
		if (dir.x < 0) buf.append("-");
		buf.append(nearestSlope.x);
		buf.append(",");
		if (dir.y < 0) buf.append("-");
		buf.append(nearestSlope.y);
		buf.append("){");
		buf.append(PEToolKit.doubleToString(emLineLength));
		buf.append("}}");
		return buf;
	}




	/**
	 * @return a pair (x,y) of positive integers ranging from 1 to 4 (for vectors) or 6 (for lines), so
	 * that y/x  gives the better possible approximation of the given slope.
	 * @param slope the slope to be matched by y/x (can be either positive or negative, or Double.POSITIVE_INFINITY)
	 * @param isVector if true, x and y range from 1 to 4 ; from 1 to 6 otherwise
	 */
	public Point getXYNearestSlope(double slope, boolean isVector){

		if (slope == 0) return new Point(1,0);

		if (slope == Double.POSITIVE_INFINITY ) return new Point(0,1);

		Point pt = new Point();
		double absSlope = Math.abs(slope);
		double end = (isVector ? 4.0 : 6.0);

		if (absSlope > Math.tan((Math.atan(end)+Math.PI/2)/2)) return new Point(0,1); // same as infinity
		double error=1;
		for (double x = 1; x<=end; x++){
			for (double y = 0; y<=end; y++){
				double newError = Math.abs(y/x/absSlope-1);
				if (newError < error) {
					error= newError;
					pt.x = (int)x;
					pt.y = (int)y;
				}
			}
		}
		// if no matching pair has been found yet...
		if (error == 1){
			if (absSlope > 1) return new Point(0,1);
			if (absSlope < 1) return new Point(1,0);
		}
		return pt;
	}




	////////////////////////////////////////////////////////////////////////////////////
	//// DRAWING
	////////////////////////////////////////////////////////////////////////////////////
	/**
	 *
	 */
	public class DrawingFormatter extends AbstractDrawingFormatter implements Formatter {


		/**
		 * init this formater for the given drawing
		 */
		public DrawingFormatter(Drawing drawing, Object outputConstraints){
			super(drawing,outputConstraints);
		}

		/**
		 * @return the content-type specific header
		 */
		protected String createHeader(){
			return "%LaTeX-picture environment using emulated lines and arcs"+getLineSeparator()+
			"%You can rescale the whole picture (to 80% for instance) by using the command \\def"+
				   LatexConstants.RESCALING_TEX_FUNCTION+"{0.8}"+getLineSeparator();

		}

		/**
		 * @return  a String representing this Drawing in the LaTeX/eepic or PsTricks format, depending on the state
		 *            of the global Options.formaterMode variable.
		 */
		public String format() throws IOException{

			StringWriter buf = new StringWriter(200);
			buf.write(createHeader());

			if (outputConstraints == FormatterFactory.MAKE_STANDALONE_FILE)
				stringWriteMultiLine(buf,getFileWrapperProlog());
			buf.write("\\ifx"+LatexConstants.RESCALING_TEX_FUNCTION+"\\undefined\\def"+
				   LatexConstants.RESCALING_TEX_FUNCTION+"{1}\\fi"+getLineSeparator());// allow the user to change scale using \jPicScale
			buf.write("\\unitlength "+LatexConstants.RESCALING_TEX_FUNCTION+" mm");
			buf.write(getLineSeparator());

			/* first we compute the width and height arguments of the "begin{picture}(w)(h)" command from the drawing's bounding box */

			// "begin{picture}(width,height)" (LaTeX or epic/eepic)
			//
			// first we compute the location of the upper-right corner of the picture,
			// then we convert it in LaTeX coordinates
			Rectangle2D box = drawing.getBoundingBox();
			if(box == null)
				box = new LatexEmptyDrawingBoundingBox();

			buf.write("\\begin{picture}(");
			buf.write(PEToolKit.doubleToString(box.getMaxX()));
			buf.write(",");
			buf.write(PEToolKit.doubleToString(box.getMaxY()));
			buf.write(")(0,0)");
			buf.write(getLineSeparator());

			/*  then for each Element in the Drawing, we call "toFormatedString" and append the returned String to our buffer */
			for (Element e:drawing){
				buf.write(createFormatter(e).format());
			}

			// previously registered not-parsed-commands:
			String s = drawing.getNotparsedCommands();
			if (s != null && !s.equals("")){
				buf.write("%Begin not parsed");
				buf.write(getLineSeparator());
				buf.write(s);
				buf.write("%End not parsed");
				buf.write(getLineSeparator());
			}
			//Épilogue
			buf.write("\\end{picture}");buf.write(getLineSeparator());
			if (outputConstraints == FormatterFactory.MAKE_STANDALONE_FILE)
				stringWriteMultiLine(buf,getFileWrapperEpilog());
			return buf.toString();
		}
	}
}

