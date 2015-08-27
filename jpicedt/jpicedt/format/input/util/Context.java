// Context.java --- -*- coding: iso-8859-1 -*-
// jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2013  Sylvain Reynal
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
// Version: $Id: Context.java,v 1.10 2013/03/31 06:59:54 vincentb1 Exp $
// Keywords: parser
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
package jpicedt.format.input.util;

import jpicedt.graphic.PicPoint;

import java.util.*;
import java.util.regex.*;
import java.io.*;
import java.nio.*;

import static jpicedt.format.input.util.ExpressionConstants.*;



/**
 * A class that stores context information about the parsing process, like: current line number, current
 * parsed substring, block markers, stack for markers&hellip;<p>
 * By convention, end markers (EOF, EndOfBlocks, &hellip;) always refer to a position one character ahead of
 * the last character (e.g. of the block), so that String.substring() works properly w/o adding 1 to the
 * end-index.
 * <p>
 * Context also offers a "convenience" marker which may be handled by using mark() and reset().
 * @since jpicedt 1.3 [refactored from scratch as of 1.3.1]
 * @author Sylvain Reynal
 * @version $Id: Context.java,v 1.10 2013/03/31 06:59:54 vincentb1 Exp $
 *
 */
public class Context  {

	// holds the text to be parsed in one piece, each line being separated by a '\n'
	private String buffer;

	// current position in the buffer ; read() returns the char at this position, then increments caret by one.
	// reset() moves the caret back to the position given by "marker"
	private int caret;

	// convenience marker, to be set by mark() ; initially 0 ; there's one such marker per block (see push())
	private int marker;

	// marks the beginning and the end of the current block ; if no push() operation occured yet, this
	// simply mark the beginning and the end of the entire buffer.
	private int beginBlockMarker;
	private int endBlockMarker;

	// an array containing position of line-breaks ; used e.g. by getLineNumber()
	// lineBreaks[i] is the position of the CR for line number "i", with line numbers beginning at "0"
	private int[] lineBreaks;

	// a fifo where we can push/pop block-markers and the convenience-marker
	private Stack<Integer> blockMarkersStack=new Stack<Integer>();

	private int stackLevel=0; // increased/decreased each time a marker is pushed/poped

	/**
	 * Build a new <code>Context</code> fed by the given <code>reader</code>.
	 * @param reader a reader encapsulating the text to be parsed
	 */
	public Context(Reader reader) throws REParserException.EOF  {

		// first create the buffer...
		StringBuffer buf = new StringBuffer();
		ArrayList<Integer> cr = new ArrayList<Integer>(); // use to temporarily to store lineBreaks as Integer's
		BufferedReader bufReader = new BufferedReader(reader);
		try {
			int crPos = -1; // location of carriage-return
			while(true){ // read until EOF
				String l = bufReader.readLine();
				if (l==null) break;
				buf.append(l);
				buf.append('\n');
				crPos += l.length() + 1;
				cr.add(new Integer(crPos));
			}
		}
		catch(EOFException eof){}
		catch (IOException ioe){throw new REParserException.EOF();}

		buffer = buf.toString();

		// and add line-breaks (i.e. CR) :
		lineBreaks = new int[cr.size()];
		for (int i=0; i<cr.size(); i++){
			lineBreaks[i] = ((Integer)cr.get(i)).intValue();
		}

		// init markers
		caret = marker = beginBlockMarker = 0;
		endBlockMarker = buffer.length();

	}

	/**
	 *
	 */
	public String toString(){

		String s;
		if (isAtEOF()) s = "EOF";
		else s = "\""
		       + buffer.substring(getBOL(), caret)
		       + "^"
		       + buffer.substring(caret,getEOL())
		       + "\"";
		s += ", caret=" + getCaretPosition()
			+ ", line=" + getLineNumber()
			+ ", stk=" + stackLevel
			+ ", bbm=" + getBeginningOfBlock()
			+ ", ebm=" + getEndOfBlock()
			+ ", mark=" + marker;
		/*
		s += ", CR";
		for (int i=0; i<lineBreaks.length; i++){
			s += "@"+Integer.toString(lineBreaks[i]);
		}
		*/
		return s;
	}

	/**
	 * Return the current line number ; beware that first line is numbered "0" !!! A line is terminated by a
	 * '\n'. 
	 */
	public int getLineNumber(){
		// lineBreaks[getLineNumber()] is the position of the next CR, and
		// lineBreaks[getLineNumber()-1] is the position of the previous CR

		// compute line number from current caret position and CR location stored in "lineBreaks"
		for (int i=0; i<lineBreaks.length; i++){
			if (caret <= lineBreaks[i]) return i;
		}
		// no match (highly improbable... yet, we never know, like !)
		return lineBreaks.length-1;
	}

	/////////////////////////////
	//// Caret control
	/////////////////////////////


	/**
	 * Return the start-index of the remaining substring to be analysed.<p>
	 * Ranges from 0 to currentLine.length()-1
	 */
	public int getCaretPosition(){
		return caret;
	}

	/**
	 * Returns the position of the beginning of the current line, or the beginning of the current block
	 * if the caret is located b/w the B.of.Block and the first CR inside the current block.
	 */
	public int getBOL(){
		int n = getLineNumber();
		if (n==0) return Math.max(0, getBeginningOfBlock());
		else return Math.max(lineBreaks[n-1]+1,getBeginningOfBlock());
	}

	/**
	 * Returns the position of the end of the current line, i.e. the location of the "\n" character,
	 * OR the end of the current block, if the caret is located b/w the E.of.Block and the last CR
	 * inside the block.
	 */
	public int getEOL(){
		return (int)Math.min(lineBreaks[getLineNumber()], getEndOfBlock());
	}

	/**
	 * Return true if the caret is located at the end of the entire buffer
	 */
	public boolean isAtEOF(){
		return (caret >= getEOF());
	}

	/**
	 * Return the position of the end of the buffer, i.e. of the last character in the buffer.
	 */
	public int getEOF(){
		return buffer.length()-1;
	}

	/**
	 * Move cursor position forward by the given increment, possibly proceeding to linefeeds.
	 * @return false if at EOF, or the end of the current block, was reached.
	 */
	public boolean moveCaretBy(int increment) throws REParserException.EOF  {
		return moveCaretTo(caret + increment);
	}

	/**
	 * Move cursor position forward to the given position, possibly proceeding to linefeeds.<p>
	 * In any case, caret CAN'T move out of the current block.
	 * @param newPos new caret position ; can be negative, null, or positive.
	 * @return false if EOF, or the end of the current block, was reached.
	 * @throws REParserException.EOF if EOF was reached.
	 */
	public boolean moveCaretTo(int newPos) throws REParserException.EOF {
		caret = newPos;
		if (caret < getBeginningOfBlock()) {
			caret = getBeginningOfBlock();
			return true;
		}
		if (caret > getEOF()) {
			caret = getEOF();
			throw new REParserException.EOF();
		}
		if (caret > getEndOfBlock()) { // code reached only if we're currently inside a nested block
			caret=getEndOfBlock();
			return false;
		}
		return true;
	}

	/**
	 * Move cursor to the beginning of the next line, EVEN if we aren't at an EOL
	 * @return FALSE if at EOF, or if we went past the current sub-buffer (enclosing expression)
	 * @throws REParserException.EOF if there's nothing to be read from the reader (EOF)
	 */
	public boolean lineFeed() throws REParserException.EOF {
		return moveCaretTo(getEOL()+1);
	}


	//////////////////////////////////////////
	// Marking and resetting
	//////////////////////////////////////////

	/**
	 * Set the convenience marker to the current caret position.
	 */
	public void mark(){
		marker = caret;
		if (DEBUG) System.out.println("!!!!!!!!!!!!! Mark() context : \n" + this.toString());
	}

	/**
	 * reset caret position to the convenience marker position, or to the beginning of the
	 * current block if no marker has been set.<p>
	 * There's one convenience marker per block, i.e. marker defaults to beginning-of-block when
	 * entering a block.
	 */
	public void reset(){
		caret = marker;
		if (DEBUG) System.out.println("!!!!!!!!!!!!! Reset() context : \n" + this.toString());
	}

	//////////////////////////////////////////
	// Reading data
	//////////////////////////////////////////

	/**
	 * Return the entire buffer
	 */
	public String getBuffer(){
		return buffer;
	}

	/**
	 * Return the character at the given position, or null if index is negative, or past EOF
	 */
	public Character getCharAt(int index){
		if (index < 0 || index >= getEOF()) return null;
		return new Character(buffer.charAt(index));
	}

	/**
	 * @return the current line, i.e. substring from BOL to EOL markers, w/o the trailing "\n"
	 * Doesn't move the caret !!! If we're inside a block, BOL and EOL have different meaning (see
	 * getBOL() and getEOL()).
	 */
	public String getCurrentLine(){
		return buffer.substring(getBOL(),getEOL());
	}

	/**
	 * read a character, and increment the caret position by one, if this is possible.
	 * @return null if we've reached the end of the current block (which can be EOF)
	 */
	public Character read() throws REParserException.EOF {
		if (caret >= getEOF()) throw new REParserException.EOF();
		if (caret >= getEndOfBlock()) return null;
		Character c = new Character(buffer.charAt(caret));
		if (moveCaretBy(1)) return c;
		else return null;
	}

	/**
	 * After a read(), pushes back the char that has been read
	 */
	public boolean pushBack() throws REParserException.EOF {
		return moveCaretBy(-1);
	}

	/**
	 * read the given number of character, wrapped in a String, and move the caret past the String
	 * being returned. May return "" if we're inside a block, and end-of-block has been reached.
	 */
	public String read(int n) throws REParserException.EOF {
		if (n<=0) return "";
		int endIndex = Math.min(caret+n, getEndOfBlock());
		String s = buffer.substring(caret, endIndex);
		moveCaretTo(endIndex);
		return s;
	}

	/**
	 * Returns the remaining substring up to the end of the current line, EOL NOT INCLUDED !
	 * Doesn't proceed to any linefeed, i.e. simply move the caret to EOL<p>
	 * !!! If we're inside a block, and current EOL is greater than getEndOfBlock(),
	 * getEndOfBlock() is taken as the EOL (see getEOL()). Note that this wouldn't make sense
	 * otherwise&hellip;
	 * @return NULL if EOF has been reached or "" if at EOL.
	 */
	public String readToEOL() throws REParserException.EOF {
		String s = buffer.substring(caret, getEOL());
		moveCaretTo(getEOL());
		return s;
	}

	/**
	 * Return the remaining substring up to the given position
	 * if (pos &lt; caret) return "". Move the caret just past the String being returned.
	 */
	public String readTo(int pos) throws REParserException.EOF {
		if (pos <= caret) return "";
		int endIndex = Math.min(pos, getEndOfBlock());
		String s = buffer.substring(caret, endIndex);
		moveCaretTo(endIndex);
		return s;
	}

	/**
	 * Test if the string at the caret position starts with the given string, and in case of success
	 * moves the caret past this string ; return false otherwise.
	 */
	public boolean matchAndMove(String s)  throws REParserException.EOF {
		boolean b = startsWith(s); // it's ok even inside a block (return false if s sticks out of the current block)
		if (b==true) {
			moveCaretBy(s.length());
			return true;
		}
		else return false;
	}

	/////////////////////////////////////////
	// String utilities
	/////////////////////////////////////////

	/**
	 * Return the index of the first occurence of the given String, starting from the current
	 * caret position.
	 * @return -1 if not found inside the current block
	 */
	public int indexOf(String s){
		int i = buffer.indexOf(s,caret);
		if (i >= getEndOfBlock()) return -1;
		else return i;
	}

	/**
	 * Return the index of the first occurence of the given String in the current block,
	 * starting from the given position
	 * @return -1 if not found inside the current block
	 */
	public int indexOf(String s, int fromIndex){
		if (fromIndex < getBeginningOfBlock()) fromIndex = getBeginningOfBlock();
		else if ( fromIndex >= getEndOfBlock()) return -1;
		int i = buffer.indexOf(s,fromIndex);
		if (i >= getEndOfBlock()) return -1;
		else return i;
	}

	/**
	 * Return the index of the first occurence of the given String, starting from the current
	 * caret position, ending at getEndOfBlock() (not included!).
	 * @return -1 if not found
	 */
	public int indexOfBeforeEOL(String s){
		int i = buffer.indexOf(s,caret);
		if (i >= getEOL()) return -1;
		else return i;
	}

	/**
	 * Return whether the string at the caret position starts with the given string or not.
	 */
	public boolean startsWith(String s){
		if (getEndOfBlock()-caret < s.length()) return false; // length of given string must at least fit into current block
		return buffer.startsWith(s, caret);
	}

	/**
 	 * Remove '\n' and '\r' from a given string, and return a new string.
	 */
	public static String removeLineFeeds(String s){
		StringBuffer buf = new StringBuffer();
		for (int i=0; i < s.length(); i++){
			char c = s.charAt(i);
			if (c != '\n' && c != '\r' ) buf.append(c);
		}
		return buf.toString();
	}

	/**
 	 * Replace "  " (double-space) or tabs by ' ' ; always return a new String.
	 */
	public static String removeRedundantWhiteSpaces(String s){
		if (s.length()==0) return new String(s);
		s = s.replace('\t',' ');
		StringBuffer buf = new StringBuffer();
		buf.append(s.charAt(0));
		for (int i=1; i < s.length(); i++){
			char c = s.charAt(i);
			if (c == ' '){
				if (s.charAt(i-1)!=' ') buf.append(c);
			}
			else buf.append(c);
		}
		return buf.toString();
	}

	//////////////////////////////////////////
	// JDK1.4 RegExp
	//////////////////////////////////////////

	/**
	 * Returns the matcher built by matching the given pattern against the current block, starting
	 * at the current caret position. Hence indices returned by the produced matcher must be incremented
	 * by getCaretPosition() to become meaningful.
	 */
	public Matcher getMatcher(Pattern re){
		//CharBuffer charBuf = CharBuffer.wrap(getBuffer(), getCaretPosition(), getBuffer().length()); -> was : matches against remaining buffer
		CharBuffer charBuf = CharBuffer.wrap(getBuffer(), getCaretPosition(), getEndOfBlock());
		Matcher matcher = re.matcher(charBuf);
		return matcher;
	}

	/**
	 * match the given RE pattern on the current block, starting at the caret position, then move the caret
	 * past the matched string if found.
	 * @return false if no match, true if the given pattern matches the beginning of the current block
	 */
	public boolean lookingAt(Pattern re){
		Matcher matcher = getMatcher(re);
		if (matcher==null || !matcher.lookingAt()) return false; // if not found at current caret position
		int endIndex = matcher.end() + getCaretPosition(); // because matcher was built starting at caret position, indices are relative to this position
		try {moveCaretTo(endIndex);} // move caret past matched String
		catch (REParserException.EOF e){e.printStackTrace();return false;} // should never happen
		return true;
	}

	/**
	 * Attempts to find the next subsequence of the current block that matches the given pattern, starting at
	 * the caret position, then moves the caret past the matched string if found.
	 * @return null if no match, of the string from caret position to the start of the match, if found (aka
	 * "swallowed string") 
	 */
	public String find(Pattern re){
		Matcher matcher = getMatcher(re);
		if (matcher==null || !matcher.find()) return null; // if not found in current block, starting at caret position
		int startIndex = matcher.start() + getCaretPosition(); // because matcher was built starting at caret position,
		int endIndex = matcher.end() + getCaretPosition(); // indices are relative to this position
		try {
			String swallowedStr = readTo(startIndex);
			moveCaretTo(endIndex);
			return swallowedStr;
		} // move caret past matched String
		catch (REParserException.EOF e){
			e.printStackTrace();
			return null;
		} // should never happen
	}

	//////////////////////////////////////////
	// saving/restoring contexts
	//////////////////////////////////////////

	/**
	 * Return the position of the beginning of the current block
	 */
	public int getBeginningOfBlock(){
		return beginBlockMarker;
	}

	/**
	 * Return the position of the end of the current block, i.e. the position of the last char in the block,
	 *         plus one (so that for example substring(getBeginningOfBlock(), getEndOfBlock()) returns
	 *         the entire block string.
	 */
	public int getEndOfBlock(){
		return endBlockMarker;
	}

	/**
	 * Return true if the caret is located at the beginning of the current block
	 */
	public boolean isAtBeginningOfBlock(){
		return (caret <= getBeginningOfBlock());
	}

	/**
	 * Return true if the caret is located at the end of the current block
	 */
	public boolean isAtEndOfBlock(){
		return (caret >= getEndOfBlock());
	}

	/**
 	 * Return the content of the current block. Note : this is an expensive operation since
	 * it creates a new String.
	 */
	public String getBlockContent(){
		return buffer.substring(getBeginningOfBlock(), getEndOfBlock());
	}


	/**
	 * Save current markers in the stack, and set new block boundaries :
	 * <ul>
	 * <li> new endBlockMarker is set to the given position
	 * <li> new beginBlockMarker is set to the current caret position
	 * <li> convenience marker is saved, and a new one is initialized to the beginning of the (new) current
	 * block. 
	 * </ul>
	 * If the new endBlockMarker position lies outside the range of the old current block, it's trimmed,
	 * that is, a new block CAN'T stick out of the block in which it was created.
	 */
	public void enterBlock(int blockEnd){

		if (blockEnd < getBeginningOfBlock()) blockEnd=getBeginningOfBlock();
		if (blockEnd > getEndOfBlock()) blockEnd=getEndOfBlock();

		blockMarkersStack.push(new Integer(beginBlockMarker));
		beginBlockMarker = caret; // new beginBlockMarker

		blockMarkersStack.push(new Integer(endBlockMarker));
		endBlockMarker = blockEnd;

		blockMarkersStack.push(new Integer(marker));
		marker = beginBlockMarker; // default convenience marker until mark() occurs

		stackLevel++;
	}

	/**
	 * Restore old markers (and forget current value), then move caret to the end of the block we just went out.
	 */
	public void exitBlock(){
		int eob = getEndOfBlock();
		marker = ((Integer)blockMarkersStack.pop()).intValue();
		endBlockMarker = ((Integer)blockMarkersStack.pop()).intValue();
		beginBlockMarker = ((Integer)blockMarkersStack.pop()).intValue();
		try {moveCaretTo(eob);}
		catch (REParserException.EOF pe){pe.printStackTrace();} // should never happen
		stackLevel--;
	}


	/**
	 * Context standalone test
	 * @param arg arg[0] is the name of file to parse.
	 */
	public static void main(String arg[]){

		FileReader reader=null;
		try{
			reader = new FileReader(arg[0]);
		}
		catch (Exception ioex){
			ioex.printStackTrace(); System.exit(0);
		}
		try {
			Context context = new Context(reader);
			AbstractRegularExpression are = new AlternateExpression(
				new StatementExpression("d", "=", null, DOUBLE, ANY_SIGN),
				new CommentExpression("%"));
			System.out.println("Grammar : \n" + are);
			boolean res = are.interpret(context);
			System.out.println((res ? "successfull" : "failed"));
		}
		catch (REParserException pe){pe.printStackTrace();}
	}

}
