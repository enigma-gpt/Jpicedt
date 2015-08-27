// JPICXmlHandler.java --- -*- coding: iso-8859-1 -*-
// August 6, 2003 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2006 Sylvain Reynal
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
// Version: $Id: JPICXmlHandler.java,v 1.36 2013/08/05 19:21:47 vincentb1 Exp $
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
package jpicedt.graphic.io.parser;

import java.util.Stack;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.geom.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import jpicedt.graphic.PicPoint;
import jpicedt.graphic.model.*;

import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.StyleConstants.*;
import static jpicedt.graphic.model.StyleConstants.LineStyle.*;
import static jpicedt.graphic.model.StyleConstants.FillStyle.*;
import static jpicedt.graphic.model.StyleConstants.PolydotsStyle.*;
import static jpicedt.graphic.model.PicText.*;
import static jpicedt.Log.*;

/**
 * Content-handler and error-handler for the JPICParser class.
 * @since jPicEdt 1.3.3
 * @author Sylvain Reynal
 * @version $Id: JPICXmlHandler.java,v 1.36 2013/08/05 19:21:47 vincentb1 Exp $
 */
public class JPICXmlHandler extends DefaultHandler {

	private static final boolean  DEBUG = false;

	Drawing drawing;
	Rectangle2D.Double boundingBox; // set to a non-null value if the "auto-bounding" parameter is false

	Locator locator;


	private static Pattern textSpecialRe = Pattern.compile("\r?\n|\r|&([lg]t|amp);");

	/** either the main drawing, or a subgroup ; each new parsed Element should be added to this group */
	PicGroup currentGroup;

	/**
	 * a fifo-like stack used to store the main drawing and its subgroups ; each time a "begin group" is encountered,
	 * the current PicGroup is pushed onto the stack, and a new PicGroup is instanciated, which then
	 * represents the current PicGroup ; the opposite operations are executed in the reverse order
	 * when a "end group" is found. */
	Stack<PicGroup> picGroupStack;

	/**
	 * Convenience used to share information (e.g. parameters, location, ...) across expressions acting
	 * on the same element. Generally, an InstanciationExpression reinits "currentObj" to an instance of
	 * an Element of the proper type, then ensuing expression modify this element's attributes and/or
	 * geometry.
	 */
	Element currentObj;

	/**
	 * A buffer which stores characters fed by the {@link #characters characters()} method on behalf
	 * of the SAXParser. It is cleared each time the {@link #startElement startElement()} method is invoked so
	 * that we get a fresh buffer each time a new XML tag is encountered.
	 */
	StringBuffer characterBuffer;
	/** nombre de fins de lignes comptées  {@link #characters characters()} dans le texte.*/
	int      eolCount;

	/**
	 *
	 */
	public JPICXmlHandler(){
	}


	/**
	 * @return un nouvel exemplaire de <code>Drawing</code> peuplé par les éléments obtenus par l'analyse
	 * des balises JPIC-XML.
	 */
	public Drawing fetchParsedDrawing(){
		if (drawing==null) {
			drawing = new Drawing(currentGroup);

			Rectangle2D bb = this.boundingBox;
			// bb == null <=> auto-bounding
			boolean isAutoCompute = bb == null;
			if(isAutoCompute)
				bb = drawing.getBoundingBox();

			// bb == null <=> empty drawing
			if(bb != null)
			{
				drawing.setBoundingBox(bb);
				drawing.setAutoComputeBoundingBox(isAutoCompute);
			}
		}
		return drawing;
	}


	// ======================================== content handling =======================

	// Receive a Locator object for document events.
	public void setDocumentLocator(Locator locator){
		this.locator = locator;
	}

	// Receive notification of the start of the document.
	public void startDocument(){
		currentGroup = new PicGroup();
		picGroupStack = new Stack<PicGroup>();
	}

	// Receive notification of the end of the document.
	public void endDocument(){
	}

	///////////////////////////////// PARSE OPENING TAGS ////////////////////////////////

	/**
	 * Receive notification of the start of an element. Since we don't make use of any XML-namespace capability,
	 * only qName and attributes matter for out purpose.
	 * @param qName the qName of the processed tag, e.g. "rect", "g",...
	 */
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		if (DEBUG)
			debug("qName="+qName);
		// reset buffer so that we get a fresh buffer for this new element :
		characterBuffer = new StringBuffer();
		eolCount        = 0;
		// test for opening/closing tag mismatch for elements having a closing tag (or is it done by
		// the SAX parser once the DtD has been set up ? check...)
		if (currentObj != null) throw new SAXParseException("Tag mismatch", locator); // [SR:pending] localize

		if (qName.equals("jpic")) startJPic(attributes);
		else if (qName.equals("text")) startText(attributes);
		else if (qName.equals("parallelogram")) startParallelogram(attributes);
		else if (qName.equals("ellipse")) startEllipse(attributes);
		else if (qName.equals("circle")) startCircle(attributes);
		else if (qName.equals("smoothpolygon")) startSmoothpolygon(attributes);
		else if (qName.equals("multicurve")) startMulticurve(attributes);
		else if (qName.equals("pscurve")) startPsCurve(attributes);
		else if (qName.equals("g")) startGroup(attributes);
	}

	// parse a "jpic" tag (there should be a single tag of this kind)
	private void startJPic(Attributes attributes) throws SAXException {
		boolean autoBounding = parseBoolean(attributes, "auto-bounding", true);
		if (!autoBounding){
			double minX = parseDouble(attributes, "x-min", 0.0);
			double maxX = parseDouble(attributes, "x-max", 100.0);
			double minY = parseDouble(attributes, "y-min", 0.0);
			double maxY = parseDouble(attributes, "y-max", 100.0);
			this.boundingBox = new Rectangle2D.Double();
			this.boundingBox.setFrameFromDiagonal(minX, minY, maxX, maxY);
		}
		else this.boundingBox = null;
	}

	// parse a "text" tag
	private void startText(Attributes attributes) throws SAXException {
		PicText text;
		this.currentObj = text = new PicText();
		text.setCtrlPt(PicText.P_ANCHOR, new PicPoint(attributes.getValue("anchor-point")),null);
		text.setAttributeSet(createAttributeSet(attributes));// also handles alignment, box and rotation
		// text will be set when "characters()" is invoked
		currentGroup.add(currentObj);
	}

	// parse a "parallelogram" tag
	private void startParallelogram(Attributes attributes) throws SAXException {
			this.currentObj = new PicParallelogram(
										new PicPoint(attributes.getValue("p1")),
										new PicPoint(attributes.getValue("p2")),
										new PicPoint(attributes.getValue("p3")),
										createAttributeSet(attributes));
			currentGroup.add(currentObj);
			currentObj=null;
	}

	// parse a "circle" tag
	private void startCircle(Attributes attributes) throws SAXException {
			String closureVal = attributes.getValue("closure");
			boolean isPlain=false;
			int closure = PicEllipse.OPEN;
			if (closureVal==null) isPlain=true;
			else {
				if (closureVal.equals("plain")) isPlain=true;
				else if (closureVal.equals("open")) closure=PicEllipse.OPEN;
				else if (closureVal.equals("pie")) closure=PicEllipse.PIE;
				else if (closureVal.equals("chord")) closure=PicEllipse.CHORD;
			}
			this.currentObj = new PicCircleFrom3Points(
										new PicPoint(attributes.getValue("p1")),
										new PicPoint(attributes.getValue("p2")),
										new PicPoint(attributes.getValue("p3")),
										isPlain,
										closure,
										createAttributeSet(attributes));
			currentGroup.add(currentObj);
			currentObj=null;
	}

	// parse a "ellipse" tag
	private void startEllipse(Attributes attributes) throws SAXException {
			this.currentObj = new PicEllipse(
										new PicPoint(attributes.getValue("p1")),
										new PicPoint(attributes.getValue("p2")),
										new PicPoint(attributes.getValue("p3")),
										PicEllipse.OPEN, // default
										createAttributeSet(attributes));
			PicEllipse arc = (PicEllipse)currentObj;
			String closureStr = attributes.getValue("closure");
			if (closureStr==null || closureStr.equals("open")) arc.setArcType(PicEllipse.OPEN);
			else if (closureStr.equals("chord")) arc.setArcType(PicEllipse.CHORD);
			else if (closureStr.equals("pie")) arc.setArcType(PicEllipse.PIE);
			arc.setAngleStart(Double.parseDouble(attributes.getValue("angle-start")));
			arc.setAngleEnd(Double.parseDouble(attributes.getValue("angle-end")));
			currentGroup.add(currentObj);
			currentObj=null;
	}

	// parse a "smoothpolygon" tag
	private void startSmoothpolygon(Attributes attributes) throws SAXException {
			PicPoint[] pts = parsePointList(attributes.getValue("points"));
			if (pts==null) throw new SAXParseException("Missing mandatory attribute:points", locator); // [SR:pending] localize
			double[] smoothCoeffs = parseDoubleList(attributes.getValue("smoothness"));
			PicAttributeSet set = createAttributeSet(attributes);
			boolean closed = "true".equals(attributes.getValue("closed"));
			if (smoothCoeffs==null) // default smooth coeff !
				this.currentObj = new PicSmoothPolygon(pts, closed,set);
			else
				this.currentObj = new PicSmoothPolygon(pts, closed,smoothCoeffs, set);
			currentGroup.add(currentObj);
			currentObj=null;
	}

	// parse a "pscurve" tag
	private void startPsCurve(Attributes attributes) throws SAXException {
			PicPoint[] pts = parsePointList(attributes.getValue("points"));
			if (pts==null) throw new SAXParseException("Missing mandatory attribute:points", locator); // [SR:pending] localize
			double[] curvature = parseDoubleList(attributes.getValue("curvature"));
			PicAttributeSet set = createAttributeSet(attributes);
			boolean closed = "true".equals(attributes.getValue("closed"));
			if (curvature==null || curvature.length < 3)
				this.currentObj = new PicPsCurve(pts, closed,set);
			else
				this.currentObj = new PicPsCurve(pts, closed,curvature[0],curvature[1],curvature[2], set);
			currentGroup.add(currentObj);
			currentObj=null;
	}

	// parse a "multicurve" tag
	private void startMulticurve(Attributes attributes) throws SAXException {
		PicPoint[] pts = parsePointList(attributes.getValue("points"));
		if (pts==null)
			throw new SAXParseException("Missing mandatory attribute:points", locator);

		int errorCriterion = (pts.length/3)*3+1 - pts.length;
		if(errorCriterion != 0 && errorCriterion != 1)
			throw new SAXParseException("Invalid points count, shall be 3*N for closed curve, or 3*N+1 for open curves", locator);

		this.currentObj = new PicMultiCurve(pts,createAttributeSet(attributes));
		currentGroup.add(currentObj);
		currentObj=null;
	}

	// parse a "g" tag
	private void startGroup(Attributes attributes) throws SAXException {
			picGroupStack.push(currentGroup);
			currentGroup = new PicGroup();
			currentObj=null;
			PicAttributeSet set = createAttributeSet(attributes);
			currentGroup.setAttributeSet(set);
			String compoundMode = attributes.getValue("compound-mode");
			if ("joint".equals(compoundMode))
				currentGroup.setCompoundMode(BranchElement.CompoundMode.JOINT);
	}

	// Receive notification of character data inside an element. Used by <text> tags only.
	public void characters(char[] ch, int start, int length){
		if (characterBuffer==null)
		{
			characterBuffer = new StringBuffer(length);
			eolCount = 0;
		}
		String strIn = new String(ch,start,length);
			if (DEBUG) debug( "[strIn=" + strIn + "]");
		int pos = 0;
		Matcher m = textSpecialRe.matcher(strIn);
		while(m.find(pos))
		{
			if (DEBUG) debug( "[substring=" + strIn.substring(pos, m.start()) + "=>" + hexDump(strIn.substring(pos, m.start()))+ "]");
			characterBuffer.append(strIn.substring(pos, m.start()));
			String g = m.group();
			if (DEBUG) debug( "[separator=" + g + "=>" + hexDump(g) + "]");
			if(g.startsWith("&"))
			{
				switch(g.charAt(1))
				{
					case 'l': // &lt;
						characterBuffer.append('<');
						break;
					case 'g': // &gt;
						characterBuffer.append('>');
						break;
					case 'a': // &amp;
						characterBuffer.append('&');
						break;
				}
			}
			else
			{
				++eolCount;
				characterBuffer.append('\n');
			}
			pos = m.end();
			if (DEBUG) debug( "[pos=" + Integer.toString(pos) + "]");
		}
		characterBuffer.append(strIn.substring(pos));

		if (DEBUG) debug( "[" + characterBuffer.toString()
						  + "=>" + hexDump(characterBuffer.toString()) + "]");
	}

	///////////////////////////////// PARSE CLOSING TAGS ////////////////////////////////

	// Receive notification of the end of an element.
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equals("text")) endText();
		else if (qName.equals("g")) endGroup();
	}

	private void endText(){
		if (currentObj instanceof TextEditable){
			String str = characterBuffer.toString();
			if(DEBUG) debug("[in-str=" + str + "=>" + hexDump(str) + "]");
			// remove leading and trailing "\n", "\n\r" or "\r"
			int eolCount = this.eolCount;
			int start = 0;
			int end = str.length();
			if (str.charAt(0) == '\n')
			{
				++start;
				--eolCount;
			}
			if(str.endsWith("\n"))
			{
				--end;
				--eolCount;
			}
			str = str.substring(start,end);
			if(DEBUG) debug("[out-str=" + str + "=>" + hexDump(str) + "]");
			TextEditable txt = (TextEditable)currentObj;
			txt.setText(str);
			if(eolCount >= 1)
				txt.setAttribute(PicAttributeName.TEXT_MODE, PicText.TextMode.TEXT_AREA);
		}
		currentObj = null;
	}

	private void endGroup() throws SAXException {
		if (picGroupStack.empty()) throw new SAXParseException("End group mismatch", locator); // [SR:pending] localize
		// save current PicGroup
		PicGroup obj = currentGroup;
		// recall old (i.e. parent) PicGroup
		currentGroup = (PicGroup)picGroupStack.pop();
		// add current PicGroup to parent
		currentGroup.add(obj);
	}

	// Receive a Locator object for document events.
	// public void setDocumentLocator(Locator locator)

	// Receive notification of a skipped entity.
	//public void skippedEntity(String name){System.out.println("skipped entity : name="+name);}



	// ====================== ERROR handling ===================================

	public void warning(SAXParseException e) throws SAXException {
		throw e;
	}

	/**
	 * Receive notification of a recoverable parser error.
	 */
	public void error(SAXParseException e) throws SAXException {
		throw e;
	}

	/**
	 * Report a fatal XML parsing error.
	 */
	public void fatalError(SAXParseException e) throws SAXException {
		throw e;
	}


	// ====================== private delegates ===============================

	private boolean parseBoolean(Attributes attributes, String name, boolean def){
		String value = attributes.getValue(name);
		if (value==null) return def;
		return (value.equals("true"));
	}

	private double parseDouble(Attributes attributes, String name, double def){
		String value = attributes.getValue(name);
		//debug("name="+name+", value="+value);
		if (value==null) return def;
		return Double.parseDouble(value);
	}

	/**
	 * Convert the given string in RGB Hex radix into a Color object
	 * @param hexStr a string of the form "#rrggbb", where rr, gg and bb are hex numbers.
	 * [SR:pending] compare with static fields in class java.awt.Color, and return one of them if applicable.
	 */
	private Color hexRGBToColor(String hexStr) throws SAXParseException {
		if (!hexStr.startsWith("#") || hexStr.length()!=7)
			throw new SAXParseException("Wrong color formatting", locator); // [SR:pending] localize
		int red = Integer.parseInt(hexStr.substring(1,3),16);
		int green = Integer.parseInt(hexStr.substring(3,5),16);
		int blue = Integer.parseInt(hexStr.substring(5,7),16);
		return new Color(red,green,blue);
	}

	/**
	 * Returns an array point parsed from the given string, which is assumed to have the following format :
	 * <br>
	 * "(x1,y1);(x2,y2);...;(xn,yn)".
	 */
	private PicPoint[] parsePointList(String str) throws NumberFormatException {
		if (str==null) return null;
		ArrayList<PicPoint> ptList = new ArrayList<PicPoint>();
		StringTokenizer tokenizer = new StringTokenizer(str,";");
		while(tokenizer.hasMoreTokens()){
			String token = tokenizer.nextToken();
			PicPoint pt = new PicPoint(token);
			ptList.add(pt);
		}
		PicPoint[] ptArray = new PicPoint[ptList.size()];
		ptList.toArray(ptArray);
		return ptArray;
	}

	/**
	 * Returns an array of doubles parsed from the given string, which is assumed to have the following format :
	 * <br>
	 * "x1;x2;...;xn".
	 */
	private double[] parseDoubleList(String str) throws NumberFormatException {
		if (str==null) return null;
		ArrayList<Double> list = new ArrayList<Double>();
		StringTokenizer tokenizer = new StringTokenizer(str,";");
		while(tokenizer.hasMoreTokens()){
			String token = tokenizer.nextToken();
			Double x = new Double(token);
			list.add(x);
		}
		double[] xArray = new double[list.size()];
		for (int i=0; i<xArray.length; i++) xArray[i]=((Double)list.get(i)).doubleValue();
		return xArray;
	}

	/**
	 * Return a new PicAttributeSet initialized from the given JPIC-XML Attributes.
	 */
	private PicAttributeSet createAttributeSet(Attributes attr) throws SAXParseException {
		PicAttributeSet set = new PicAttributeSet();

		for (int i=0; i<attr.getLength(); i++){
			String name = attr.getQName(i);
			String value = attr.getValue(i);
			//debug("name="+name+", val="+value);

			// ================ STROKE ================
			if (name.startsWith("stroke")){
				if (name.equals(LINE_STYLE.getName())) {
					for (LineStyle v:LineStyle.values()){
						if (value.equals(v.toString())){
							set.setAttribute(LINE_STYLE,v);
							break;
						}
					}
					// [pending]else throw new IncorrectAttributeValue(name,value);
				}
				else if (name.equals(LINE_WIDTH.getName())) set.setAttribute(LINE_WIDTH, new Double(value));
				else if (name.equals(LINE_COLOR.getName())) set.setAttribute(LINE_COLOR, hexRGBToColor(value));
				else if (name.equals("stroke-dasharray")) { // "a,b" thus far
					StringTokenizer tokenizer = new StringTokenizer(value,";");
					if (tokenizer.countTokens()<2) throw new  SAXParseException("Syntax error", locator);
					set.setAttribute(DASH_OPAQUE, new Double(tokenizer.nextToken()));
					set.setAttribute(DASH_TRANSPARENT, new Double(tokenizer.nextToken()));
				}
				else if (name.equals(DOT_SEP.getName()))  set.setAttribute(DOT_SEP, new Double(value));
				else if (name.equals(DOUBLE_LINE.getName())) set.setAttribute(DOUBLE_LINE, new Boolean(value));
				else if (name.equals(DOUBLE_SEP.getName()))  set.setAttribute(DOUBLE_SEP, new Double(value));
				else if (name.equals(DOUBLE_COLOR.getName())) set.setAttribute(DOUBLE_COLOR, hexRGBToColor(value));
				else if (name.equals(OVER_STRIKE.getName())) set.setAttribute(OVER_STRIKE, new Boolean(value));
				else if (name.equals(OVER_STRIKE_WIDTH.getName())) set.setAttribute(OVER_STRIKE_WIDTH, new Double(value));
				else if (name.equals(OVER_STRIKE_COLOR.getName())) set.setAttribute(OVER_STRIKE_COLOR, hexRGBToColor(value));
				else throw new IncorrectAttributeName(name);
			}

			// ================ FILL ================
			else if (name.startsWith("fill")) {
				if (name.equals(FILL_STYLE.getName())) {
					for (FillStyle v:FillStyle.values()){
						if (value.equals(v.toString())){
							set.setAttribute(FILL_STYLE,v);
							break;
						}
					}
					// [pending]else throw new IncorrectAttributeValue(name,value);
				}
				else if (name.equals(FILL_COLOR.getName())) set.setAttribute(FILL_COLOR, hexRGBToColor(value));
				else if (name.equals(HATCH_WIDTH.getName())) set.setAttribute(HATCH_WIDTH, new Double(value));
				else if (name.equals(HATCH_SEP.getName())) set.setAttribute(HATCH_SEP, new Double(value));
				else if (name.equals(HATCH_ANGLE.getName())) set.setAttribute(HATCH_ANGLE, new Double(value));
				else if (name.equals(HATCH_COLOR.getName())) set.setAttribute(HATCH_COLOR, hexRGBToColor(value));
				else throw new IncorrectAttributeName(name);
			}

			// ================ SHADOW ================
			else if (name.startsWith("shadow")){
				if (name.equals(SHADOW.getName())) set.setAttribute(SHADOW, new Boolean(value));
				else if (name.equals(SHADOW_SIZE.getName())) set.setAttribute(SHADOW_SIZE, new Double(value));
				else if (name.equals(SHADOW_ANGLE.getName())) set.setAttribute(SHADOW_ANGLE, new Double(value));
				else if (name.equals(SHADOW_COLOR.getName())) set.setAttribute(SHADOW_COLOR, hexRGBToColor(value));
				else throw new IncorrectAttributeName(name);
			}

			// ================ ARROWS ================
			else if (name.equals(LEFT_ARROW.getName())) set.setAttribute(LEFT_ARROW, createArrow(value));
			else if (name.equals(RIGHT_ARROW.getName())) set.setAttribute(RIGHT_ARROW, createArrow(value));
			else if (name.startsWith("arrow")){ // arrow parameters (except for dots, which are handled below)
				if (name.equals(ARROW_GLOBAL_SCALE_WIDTH.getName())) set.setAttribute(ARROW_GLOBAL_SCALE_WIDTH,new Double(value));
				else if (name.equals(ARROW_GLOBAL_SCALE_LENGTH.getName())) set.setAttribute(ARROW_GLOBAL_SCALE_LENGTH,new Double(value));
				else if (name.equals(ARROW_WIDTH_MINIMUM_MM.getName())) set.setAttribute(ARROW_WIDTH_MINIMUM_MM,new Double(value));
				else if (name.equals(ARROW_WIDTH_LINEWIDTH_SCALE.getName())) set.setAttribute(ARROW_WIDTH_LINEWIDTH_SCALE,new Double(value));
				else if (name.equals(ARROW_LENGTH_SCALE.getName())) set.setAttribute(ARROW_LENGTH_SCALE,new Double(value));
				else if (name.equals(ARROW_INSET_SCALE.getName())) set.setAttribute(ARROW_INSET_SCALE,new Double(value));
				else if (name.equals(TBAR_WIDTH_MINIMUM_MM.getName())) set.setAttribute(TBAR_WIDTH_MINIMUM_MM,new Double(value));
				else if (name.equals(TBAR_WIDTH_LINEWIDTH_SCALE.getName())) set.setAttribute(TBAR_WIDTH_LINEWIDTH_SCALE,new Double(value));
				else if (name.equals(BRACKET_LENGTH_SCALE.getName())) set.setAttribute(BRACKET_LENGTH_SCALE,new Double(value));
				else if (name.equals(RBRACKET_LENGTH_SCALE.getName())) set.setAttribute(RBRACKET_LENGTH_SCALE,new Double(value));
				else throw new IncorrectAttributeName(name);
			}

			else if (name.endsWith("custom")){
				// ================ PSTCUSTOM ===============
				if (name.equals(PST_CUSTOM.getName())) set.setAttribute(PST_CUSTOM, value);

				// ================ TIKZ CUSTOM ===============
				else if (name.equals(TIKZ_CUSTOM.getName())) set.setAttribute(TIKZ_CUSTOM, value);
				else throw new IncorrectAttributeName(name);
			}
			// ================ POLYDOTS =================
			else if (name.startsWith("polydots")){
				if (name.equals(POLYDOTS_STYLE.getName())){
					for (PolydotsStyle v:PolydotsStyle.values()){
						if (value.equals(v.toString())){
							set.setAttribute(POLYDOTS_STYLE,v);
							break;
						}
					}
					// [pending]else throw new IncorrectAttributeValue(name,value);
				}
				else if (name.equals(POLYDOTS_SUPERIMPOSE.getName())) set.setAttribute(POLYDOTS_SUPERIMPOSE,new Boolean(value));
				else if (name.equals(POLYDOTS_SIZE_MINIMUM_MM.getName())) set.setAttribute(POLYDOTS_SIZE_MINIMUM_MM, new Double(value));
				else if (name.equals(POLYDOTS_SIZE_LINEWIDTH_SCALE.getName())) set.setAttribute(POLYDOTS_SIZE_LINEWIDTH_SCALE, new Double(value));
				else if (name.equals(POLYDOTS_SCALE_H.getName())) set.setAttribute(POLYDOTS_SCALE_H, new Double(value));
				else if (name.equals(POLYDOTS_SCALE_V.getName())) set.setAttribute(POLYDOTS_SCALE_V, new Double(value));
				else if (name.equals(POLYDOTS_ANGLE.getName())) set.setAttribute(POLYDOTS_ANGLE, new Double(value));
				//else throw new IncorrectAttributeName(name); // otherwise breaks backward compatibility with 1.4pre3,
						// since several strings have changed in PicObjectContants
			}

			// ================= TEXT ===================
			else if (name.startsWith("text")){
				if (name.equals(TEXT_VERT_ALIGN.getName())) { // [pending] use for (enum.values()) instead
					for (VertAlign v:VertAlign.values()){
						if (value.equals(v.toString())){
							set.setAttribute(TEXT_VERT_ALIGN,v);
							break;
						}
					}
				}
				else if (name.equals(TEXT_HOR_ALIGN.getName())) {
					for (HorAlign v:HorAlign.values()){
						if (value.equals(v.toString())){
							set.setAttribute(TEXT_HOR_ALIGN,v);
							break;
						}
					}
				}
				else if (name.equals(TEXT_FRAME.getName())) {
					for (FrameStyle v:FrameStyle.values()){
						if (value.equals(v.toString())){
							set.setAttribute(TEXT_FRAME,v);
							break;
						}
					}
				}
				else if (name.equals(TEXT_ROTATION.getName())){
					set.setAttribute(TEXT_ROTATION, new Double(value));
				}

				else if (name.equals(TEXT_MODE.getName())) {
					for(TextMode v:TextMode.values()){
						if (value.equals(v.toString())){
							set.setAttribute(TEXT_MODE, v);
						}
					}
				}

				else if (name.equals(TEXT_ICON.getName())) {
					for(TextIcon v: TextIcon.values()){
						if (value.equals(v.toString())){
							set.setAttribute(TEXT_ICON,v);
						}
					}
				}
				else throw new IncorrectAttributeName(name);
			}

			// else if (name.equals()) set.setAttribute();
			//debug("Set="+set);

		}
		return set;
	}

	/**
	 * Convert the given Arrow name to a predefined Arrow
	 * @see ArrowStyle.getPredefinedArrow()
	 */
	private ArrowStyle createArrow(String name){
		for (ArrowStyle as: ArrowStyle.values())
			if (as.toString().equals(name)) return as;
		return ArrowStyle.NONE;
	}

	// ============================ XML-JPIC specific exception
	class IncorrectAttributeValue extends SAXParseException {
		public IncorrectAttributeValue(String name, String value){
			super("Incorrect value for attribute \"" + name + "\": " + value, locator);
		}
	}

	class IncorrectAttributeName extends SAXParseException {
		public IncorrectAttributeName(String name){
			super("Attribute \"" + name + "\" is not supported", locator);
		}
	}
}

