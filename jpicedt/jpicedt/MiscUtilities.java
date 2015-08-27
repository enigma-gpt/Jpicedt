// MiscUtilities.java --- -*- coding: iso-8859-1 -*-
// March 10, 2002 - jPicEdt 1.3.2, a picture editor for LaTeX.
// Copyright (C) 1999/2013 Sylvain Reynal
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
// Version: $Id: MiscUtilities.java,v 1.24 2013/03/27 06:53:01 vincentb1 Exp $
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

package jpicedt;

import jpicedt.graphic.ContentType;
import jpicedt.graphic.io.parser.ExtractionParsing;

import java.awt.*;
import java.util.*;
import java.text.*;
import javax.swing.*;
import java.awt.datatransfer.*;
import java.io.*;
import java.awt.Font;

/**
 * A collection of static utilities methods.
 * <p>
 * Developpers, be careful before adding new methods here : keep in mind that this class MUST
 * remain independent of the jpicedt.ui.* package (as well as of jpicedt.JPicEdt), since it's shipped
 * with the jpicedt's library, which only contains the jpicedt.graphic.* and jpicedt.format.* packages !
 * @author Sylvain Reynal
 * @version $Id: MiscUtilities.java,v 1.24 2013/03/27 06:53:01 vincentb1 Exp $
 * @since jPicEdt 1.3.2
 */
public class MiscUtilities  {

	public static enum ContentTypeBasics{
		LATEX(
			0,
			"jpicedt.format.output.latex.LatexContentType",
			"Emulated LaTeX",
			"pic:tex"
			),
			EEPIC(
				1,
				"jpicedt.format.output.eepic.EepicContentType",
				"Epic/Eepic",
				"epic:eepic"
				),
			PSTRICKS(
				2,
				"jpicedt.format.output.pstricks.PstricksContentType",
				"PSTricks",
				"pst:jpe.pstricks"
				),
			TIKZ(
				3,
				"jpicedt.format.output.tikz.TikzContentType",
				"TikZ",
				"jpe.tikz"
				),
			DXF(4,
				"jpicedt.format.output.dxf.DXFContentType",
				"DXF",
				"jpe.dxf:dxf"
				);


		private int    index;
		private String className;
		private String contentTypeName;
		private String fileExtentions;

		public String getClassName(){ return className; }
		public String getContentTypeName(){ return contentTypeName; }
		public String getFileExtentions(){ return fileExtentions; }
		ContentTypeBasics(int index,String className,String contentTypeName,String fileExtentions){
			this.index = index;
			this.className = className;
			this.contentTypeName = contentTypeName;
			this.fileExtentions = fileExtentions;
		}
	}

	////////////////////////////////////////////////////////////////////
	///////////// CONTENT-TYPE MANAGEMENT
	////////////////////////////////////////////////////////////////////

	/** default index in array returned by getAvailableContentTypes */
	public static final int DEFAULT_CONTENT_TYPE_INDEX = 0;

	/**
	 * @return an array containing the class names of the currently available ContentType's
	 * [pending] fetch from a Properties on disk ?
	 */
	public static String[] getAvailableContentTypes(){
		ContentTypeBasics[] v = ContentTypeBasics.values();
		String[] a = new String[v.length];
		int i = 0;
		for(ContentTypeBasics e : v)
			a[i++] = e.getClassName();
		return a;
	}

	/**
	 * @return an array containing the class names of the currently available ContentType's
	 * [pending] fetch from a Properties on disk ?
	 */
	public static String[] getAvailableContentTypesNames(){
		ContentTypeBasics[] v = ContentTypeBasics.values();
		String[] a = new String[v.length];
		int i = 0;
		for(ContentTypeBasics e : v)
			a[i++] = e.getContentTypeName();
		return a;
	}

	/**
	 * @return an array containing the class names of the currently available ContentType's
	 * [pending] fetch from a Properties on disk ?
	 */
	public static String[][] getAvailableContentTypesFileExtensions(){
		ContentTypeBasics[] v = ContentTypeBasics.values();
		String[][] a = new String[v.length][];
		int i = 0;
		for(ContentTypeBasics e : v){
			a[i++] = e.getFileExtentions().split(":");
		}
		return a;
	}

	/**
	 * Utility to retrieve the index of the given content-type class name in the array
	 * returned by <code>getAvailableContentTypes</code> ; to be used by JComboBox'es.
	 * @param contentTypeClassName e.g. "jpicedt.format.latex.LatexContentType"
	 * @return DEFAULT_CONTENT_TYPE_INDEX if not found.
	 */
	public static int getContentTypeIndex(String contentTypeClassName){
		ContentTypeBasics[] v = ContentTypeBasics.values();
		int i = 0;
		for (ContentTypeBasics c : v){
			if (c.getClassName().equals(contentTypeClassName))
				return i;
			else
				++i;

		}
		return DEFAULT_CONTENT_TYPE_INDEX;
	}

	/**
	 * @return an instanciation of the given content-type class name, or null if class wasn't found
	 */
	public static ContentType getContentTypeFromClassName(String contentTypeClassName){
		if (contentTypeClassName==null)
			contentTypeClassName=getAvailableContentTypes()[DEFAULT_CONTENT_TYPE_INDEX];
		ContentType ct = null;
		try {
			Class clazz = Class.forName(contentTypeClassName);
			ct = (ContentType)clazz.newInstance();
		}
		catch(ClassNotFoundException cnfex){
			if (jpicedt.Log.DEBUG) cnfex.printStackTrace();
			JPicEdt.getMDIManager().showMessageDialog(
			                              "The pluggable content-type you asked for (class " + cnfex.getLocalizedMessage() + ") isn't currently installed, using default instead...",
			                              Localizer.currentLocalizer().get("msg.LoadingContentTypeClass"),
			                              JOptionPane.ERROR_MESSAGE);
		}
		catch(Exception ex){
			if (jpicedt.Log.DEBUG) ex.printStackTrace();
			JPicEdt.getMDIManager().showMessageDialog(
			                              ex.getLocalizedMessage(),
			                              Localizer.currentLocalizer().get("msg.LoadingContentTypeClass"),
			                              JOptionPane.ERROR_MESSAGE);
		}
		return ct;
	}

	/**
	 * Return a parser dynamically created from the sub-trees found in directory jpicedt.format.*
	 */
	public static ExtractionParsing createParser(){

		jpicedt.graphic.io.parser.JPICParser jpicParser = new jpicedt.graphic.io.parser.JPICParser();
		return jpicParser;
	}

	////////////////////////////////////////////////////////////////////
	///////////// UTILS for PROPERTIES HANDLING
	////////////////////////////////////////////////////////////////////

	/**
	 * @return a boolean built from the value fetched from the given key in the given properties,
	 * or the "def" value if the key wasn't found
	 */
	public static boolean parseProperty(Properties preferences, String key, boolean def){
		String val = preferences.getProperty(key);
		if (val == null) return def;
		if (val.equals("true") || val.equals("on") || val.equals("yes")) return true;
		return false;
	}

	/**
	 * @return a Color built from the value fetched from the given key in the given properties,
	 * or the "def" value if the key wasn't found
	 */
	public static Color parseProperty(Properties preferences, String key, Color def){
		String val = preferences.getProperty(key);
		if (val == null) return def;
		return new Color(Integer.parseInt(val)); // [pending] should catch NFE here
	}

	/**
	 * @return a double parsed from the value associated with the given key in the given Properties.
	 *         returns "def" in key wasn't found, or if a parsing error occured. If "value" contains
	 *         a "%" sign, we use a <code>NumberFormat.getPercentInstance</code> to convert it to a double.
	 */
	public static double parseProperty(Properties preferences, String key, double def){
		NumberFormat formatPercent = NumberFormat.getPercentInstance(Locale.US); // for zoom factor
		String val = preferences.getProperty(key);
		if (val==null) return def;
		if (val.indexOf("%") == -1){ // not in percent format !
			try{
				return Double.parseDouble(val);
			}
			catch(NumberFormatException nfe){
				nfe.printStackTrace();
				return def;
			}
		}
		// else it's a percent format -> parse it
		try {
			Number n = formatPercent.parse(val);
			return n.doubleValue();
		}
		catch (ParseException ex){
			ex.printStackTrace();
			return def;
		}
	}

	/**
	 * Return a integer parsed from the value associated with the given key, or "def" in key wasn't found.
	 */
	public static int parseProperty(Properties preferences, String key, int def){
		String val = preferences.getProperty(key);
		if (val == null) return def;
		try {
			return Integer.parseInt(val);
		}
		catch (NumberFormatException nfe){
			nfe.printStackTrace();
			return def;
		}
	}

	/**
 	 *	Returns a RenderingHints parsed from the given Properties
	 */
	public static RenderingHints parseRenderingHints(Properties preferences){

		RenderingHints rh = new RenderingHints(null);
		String str;
		str = preferences.getProperty("rendering.antialiasing");
		if (str!=null) {
			if (str.equals("on")) rh.put(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
			else if (str.equals("off")) rh.put(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);
		}
		str = preferences.getProperty("rendering.text-antialiasing");
		if (str!=null) {
			if (str.equals("on")) rh.put(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			else if (str.equals("off")) rh.put(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		}
		str = preferences.getProperty("rendering.render");
		if (str!=null) {
			if (str.equals("speed")) rh.put(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_SPEED);
			else if (str.equals("quality")) rh.put(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
		}
		str = preferences.getProperty("rendering.dither");
		if (str!=null) {
			if (str.equals("on")) rh.put(RenderingHints.KEY_DITHERING,RenderingHints.VALUE_DITHER_ENABLE);
			else if (str.equals("off")) rh.put(RenderingHints.KEY_DITHERING,RenderingHints.VALUE_DITHER_DISABLE);
		}
		str = preferences.getProperty("rendering.fractional-metrics");
		if (str!=null) {
			if (str.equals("on")) rh.put(RenderingHints.KEY_FRACTIONALMETRICS,RenderingHints.VALUE_FRACTIONALMETRICS_ON);
			else if (str.equals("off")) rh.put(RenderingHints.KEY_FRACTIONALMETRICS,RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
		}
		return rh;
	}

	/**
	 * store the given RenderingHints to a Properties object
	 */
	public static void formatRenderingHints(RenderingHints rh, Properties preferences){

		if (rh.get(RenderingHints.KEY_ANTIALIASING).equals(RenderingHints.VALUE_ANTIALIAS_ON))
			preferences.put("rendering.antialiasing","on");
		else if (rh.get(RenderingHints.KEY_ANTIALIASING).equals(RenderingHints.VALUE_ANTIALIAS_OFF))
			preferences.put("rendering.antialiasing","off");

		if (rh.get(RenderingHints.KEY_TEXT_ANTIALIASING).equals(RenderingHints.VALUE_TEXT_ANTIALIAS_ON))
			preferences.put("rendering.text-antialiasing","on");
		else if (rh.get(RenderingHints.KEY_TEXT_ANTIALIASING).equals(RenderingHints.VALUE_TEXT_ANTIALIAS_OFF))
			preferences.put("rendering.text-antialiasing","off");

		if (rh.get(RenderingHints.KEY_RENDERING).equals(RenderingHints.VALUE_RENDER_SPEED))
			preferences.put("rendering.render","speed");
		else if (rh.get(RenderingHints.KEY_RENDERING).equals(RenderingHints.VALUE_RENDER_QUALITY))
			preferences.put("rendering.render","quality");

		if (rh.get(RenderingHints.KEY_DITHERING).equals(RenderingHints.VALUE_DITHER_ENABLE))
			preferences.put("rendering.dither","on");
		else if (rh.get(RenderingHints.KEY_DITHERING).equals(RenderingHints.VALUE_DITHER_DISABLE))
			preferences.put("rendering.dither","off");

		if (rh.get(RenderingHints.KEY_FRACTIONALMETRICS).equals(RenderingHints.VALUE_FRACTIONALMETRICS_ON))
			preferences.put("rendering.fractional-metrics","on");
		else if (rh.get(RenderingHints.KEY_FRACTIONALMETRICS).equals(RenderingHints.VALUE_FRACTIONALMETRICS_OFF))
			preferences.put("rendering.fractional-metrics","off");
	}


	/**
	 * Return a double parsed from the given string, possibly formatted with a "%" sign
	 */
	public static double parseDouble(String val) throws NumberFormatException, ParseException {
		NumberFormat formatPercent = NumberFormat.getPercentInstance(Locale.US); // for zoom factor
		if (val.indexOf("%") == -1){ // not in percent format !
				return Double.parseDouble(val);
		}
		// else it's a percent format -> parse it
		Number n = formatPercent.parse(val);
		return n.doubleValue();
	}

	/**
	 * format a given Font to a string, following "Font.decode()" format, ie
	 * fontname-style-pointsize, fontname-pointsize, fontname-style or fontname,
	 * where style is one of "BOLD", "ITALIC", "BOLDITALIC" (default being PLAIN)
	 */
	public static String formatFontAsProperties(Font font){
		//jpicedt.Log.debug(new MiscUtilities(),"formatFontAsProperties","font="+font);
		String family = font.getFamily();
		/*
		System.out.println("family="+family);
		String faceName = font.getFontName();
		System.out.println("faceName="+faceName);
		String logicalName = font.getName();
		System.out.println("logicalName"+logicalName);
		String psName = font.getPSName();
		System.out.println("PSName="+psName);
		*/

		StringBuffer buf = new StringBuffer(20);
		buf.append(family);
		buf.append("-");
		switch(font.getStyle()){
			case Font.ITALIC : buf.append("ITALIC-"); break;
			case Font.BOLD : buf.append("BOLD-"); break;
			case Font.BOLD|Font.ITALIC : buf.append("BOLDITALIC-"); break;
			default: // PLAIN -> nothing
		}
		buf.append(Integer.toString(font.getSize()));
		return buf.toString();
	}

	/////////////////////////////////////////////////////////////////////////
	//// RECENT FILES MANAGEMENT
	/////////////////////////////////////////////////////////////////////////

	/** key used to retrieve the list of recent files from a properties file
	  * a ".X" string, where X is a number starting from 1, should be appended to the key beforehands. */
	public static String KEY_RECENT_FILE = "menu.recent-file";

	/** max number of recent files */
	private static int MAX_RECENT_FILES = 10;

	/**
	 * @return an ArrayList containing recent file names, fetched from the given key
	 * (e.g. PEMenuBar.KEY_RECENT_FILE)
	 */
	public static ArrayList<String> parseRecentFiles(Properties preferences){
		int i=1;
		String fileName;
		ArrayList<String> list = new ArrayList<String>();
		// build key by appending ".X" where "X" runs from 1 to ...
		// i.e. fetch as many names as possible, until no key is found :
		while ((fileName=preferences.getProperty(KEY_RECENT_FILE + "." + Integer.toString(i++)))!=null){
			//System.out.println("filename=" + fileName + ";");
			if(!fileName.equals("") && !fileName.equals(" ")){
				list.add(fileName);
			}
		}
		return list;
	}

	/**
	 * Add the given file name to the list of recent files in the given Properties object, trimming
	 * the length of the list to 10. If the given name is already in the list, move it to the top.
	 */
	public static void addRecentFile(Properties preferences, String newName){

		// - if newName isn't already in the list, we add it to the top of the list.
		// - if it's already in the list, we move it to the top.
		ArrayList<String> list = MiscUtilities.parseRecentFiles(preferences);
		if (list.contains(newName)){
			// move it to the top :
			list.remove(newName);
			list.add(0,newName);
		}
		else {
			list.add(0,newName);
		}
		// store to preferences :
		for (int i=0; i< MAX_RECENT_FILES && i<list.size(); i++){
			String key = KEY_RECENT_FILE + "." + Integer.toString(i+1); // starts from 1
			String val = list.get(i);
			preferences.setProperty(key,val);
		}
	}

	///////////////////////////////////////////////////////////////////////////////////////
	/// SYSTEM CLIPBOARD
	///////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the clipboard content as a String (DataFlavor.stringFlavor)
	 *         Code snippet adapted from jEdit (Registers.java), http://www.jedit.org.
	 *	 	   Returns null if clipboard is empty.
	 */
	public static String getClipboardStringContent(Clipboard clipboard){
			//Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			try {
				String selection = (String)(clipboard.getContents(null).getTransferData(DataFlavor.stringFlavor));
				if (selection==null) return null;

				boolean trailingEOL = (selection.endsWith("\n")
					|| selection.endsWith(System.getProperty("line.separator")));

				// Some Java versions return the clipboard contents using the native line separator,
				// so have to convert it here , see jEdit's "registers.java"
				BufferedReader in = new BufferedReader(new StringReader(selection));
				StringBuffer buf = new StringBuffer();
				String line;
				while((line = in.readLine()) != null){
					buf.append(line);
					buf.append('\n');
				}
				// remove trailing \n
				if(!trailingEOL)
					buf.setLength(buf.length() - 1);
				return buf.toString();
			}
			catch(Exception e){
				e.printStackTrace();
				return null;
			}
	}

	/////////////////////////////////////////////////////////////////////////////////
	/// JPicEdt home
	///////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns JPicEdt's install directory w/o trailing "/", provided the command line looks like :<p>
	 * <code>java -classpath other-class-paths:jpicedt-install-dir/lib/jpicedt.jar jpicedt.JPicEdt</code>
	 * (where <code>/</code> may be replaced by the actual
	 * respective separator for files on the underlying
	 * platform).</p><p>For Windows platform, the install directory is tried
	 * to be detected 1st with the MSWindows file-separator (<code>\</code>), and if this
	 * does not work, a subsequent trial is made with <code>/</code>. </p><p>
	 * That is, the old way (java -jar jpicedt.jar) won't work.
	 * However, classpath can contain relative pathname (then user.dir get
	 * prepended).</p><p>
	 * Code snippet was adapted from jEdit/JEdit.java (http://www.jedit.org).</p>
	 * @return the value of the "user.dir" Java property if "lib/jpicedt.jar" wasn't found in the command line.
	 */
	public static String getJPicEdtHome(){

		String classpath = System.getProperty("java.class.path"); // e.g. "/usr/lib/java/jre/lib/rt.jar:/home/me/jpicedt/1.3.2/lib/jpicedt.jar"
		// File.separator = "/" on Unix, "\\" on Windows,...
		String fileSeparator = File.separator;
		int index;
		// File.pathSeparator = ":" on Unix/MacOS-X platforms, ";" on Windows
		// search ":" backward starting from "/usr/lib/java/jre/lib/rt.jar:/home/me/jpicedt/1.3.2/^lib/jpicedt.jar"

		String homeDir = null;
		int trials = 2;
		do
		{
			index = classpath.toLowerCase().indexOf("lib"+fileSeparator+"jpicedt.jar");
			int start = classpath.lastIndexOf(File.pathSeparator,index);
			if (start == -1) start = 0; // File.pathSeparator not found => lib/jpicedt.jar probably at beginning of classpath
			else start += File.pathSeparator.length(); // e.g. ":^/home..."

			if(index >= start){
				homeDir = classpath.substring(start,index);
				if (homeDir.endsWith(fileSeparator))
					homeDir = homeDir.substring(0,homeDir.length()-1);
			}
			switch(trials){
			case 2:
				if(File.pathSeparator.equals(";") && homeDir == null)
				{
					// MS-Windows case, this must work both with / and \
					trials = 1;
					fileSeparator="/";
				}
				else
					trials = 0;
				break;
			case 1:
				if(homeDir != null && !fileSeparator.equals(File.separator))
				{
					homeDir.replace(fileSeparator,File.separator);
				}
				trials = 0;
				break;

			default:
				trials = 0;
				break;
			}
		}
		while(trials != 0);

		if(homeDir!=null){
			if (homeDir.equals("")) homeDir = System.getProperty("user.dir");
			else if (!new File(homeDir).isAbsolute())
				homeDir = System.getProperty("user.dir") + File.separator + homeDir;
		}
		else {
			homeDir = System.getProperty("user.dir");
			if (homeDir.endsWith("lib")) // this is the case if jpicedt run as "java -jar jpicedt.jar" from inside lib/ dir
				homeDir = homeDir.substring(0, homeDir.lastIndexOf("lib"));
		}

		//System.out.println("JPicEdt's home = " + homeDir);
		return homeDir;

	}

	public static void main(String[] args){
		System.out.println("JPicEdt's home = " + getJPicEdtHome());
	}

	/**
	 * Return the platform standard tmp dir, or null if none is standardly defined.
	 */
	public static File getOSTmpDir(){
			// Note : default tmp dir can be obtained through :
			// System.getProperty("java.io.tmp")
		String tmp = System.getProperty("java.io.tmp");
		if (tmp==null) return null;
		else return new File(tmp);
	}

	/**
	 * Can't be instanciated.
	 */
	private MiscUtilities(){}

} // class
