// DefaultParser.java --- -*- coding: iso-8859-1 -*-
// February 21, 2002 - jPicEdt 1.3.3, a picture editor for LaTeX.
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
// Version: $Id: DefaultParser.java,v 1.15 2013/03/31 07:00:15 vincentb1 Exp $
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
package jpicedt.format.input;


import jpicedt.graphic.PicPoint;
import jpicedt.graphic.model.*;
import jpicedt.format.input.util.*;
import jpicedt.graphic.io.parser.JPICParser;
import jpicedt.graphic.io.parser.Parser;
import jpicedt.graphic.io.parser.ParsedDrawing;
import jpicedt.graphic.io.parser.ExtractionParsing;
import jpicedt.graphic.io.parser.ParserException;

import java.awt.Color;
import java.io.*;
import java.util.*;

import static jpicedt.format.input.util.ExpressionConstants.*;

/**
 * Default implementation of the Parser interface.
 * <p>
 * This expression doesn't contains rules in itself. On the contrary, it simply lumps
 * sub-trees together, each sub-tree containing grammar rules for a particular format, e.g.
 * PsTricks, eepic,&hellip;
 * <p>
 * Basically, this parser hold data using three objects :
 * <ul>
 * <li> an instance of {@link jpicedt.format.input.util.Pool  util.Pool} : this stores persistent data, and
 * allow them to be shared by AbstractRegularExpression's across the whole parser tree.
 * <li> an instance of {@link jpicedt.format.input.util.Context util.Context} : this holds the current state
 * of the lexicographic analyser (line number, caret position, block boundaries, etc&hellip;)
 * <li> an instance of AlternateExpression : this holds a reference to the various supported grammars by
 * mixing them into a single tree.
 * </ul>
 * <p>
 * [Developpers] for those willing to add their own parser to the tree, simply write a new parser-tree whose
 * root expression inherits from
 * {@link jpicedt.format.input.util.RootExpression RootExpression},
 * then add it to this parser by using the
 * <code>addGrammar</code> method.
 * @since jpicedt 1.2
 * @author Sylvain Reynal
 * @version $Id: DefaultParser.java,v 1.15 2013/03/31 07:00:15 vincentb1 Exp $
 *
 */
public class DefaultParser extends SequenceExpression implements ExtractionParsing{

	/** a pool of primitive types and objects that may be shared by sub-expressions */
	protected Pool pool;

	/** holds information about the current parser state */
	protected Context context;

	/** holds a reference to each supported grammar */
	protected AlternateExpression headExp;

	/**
	 * Creates a new DefaultParser, and attach some grammar rules to it.<br>
	 * Tree structure = (WhiteSpaceOrEOL &amp; (headExp | notParsable))*<br>
	 * where : headExp = Root1 | Root2 | Root3 &hellip; (each RootX sub-tree representing a particular
	 * format). 
	 * <p>
	 * Note that with this current structure, sub-trees must NOT be mutually exclusive, ie it's ok with eepic
	 * and pstricks for instance (it wouldn't work with PDF and LaTeX, since once we've started with PDF, it
	 * wouldn't make sense trying to parse LaTeX commands&hellip;) -&gt;
	 */
	public DefaultParser() {

		super(false); // doesn't throw IncompleteSequence exception (wouldn't make sense, seeing that we may have no whitespaces at all, see below)
		pool = new Pool();
		AlternateExpression alt = new AlternateExpression();
		alt.add(headExp = new AlternateExpression());
		// must always be the last-added expression :
		alt.add(new NotParsableExpression(pool));
		// swallow white-spaces and/or EOL between each picObjExp's subexpression:
		add(new RepeatExpression(new SequenceExpression(new WhiteSpacesOrEOL(), alt), 0, AT_LEAST));

		// [SR:pending] load grammar dynamically by using our own classloader
		this.addGrammar(new jpicedt.format.input.latex.LaTeXParser(this.getPool()));
		this.addGrammar(new jpicedt.format.input.eepic.EepicParser(this.getPool()));
		this.addGrammar(new jpicedt.format.input.pstricks.PstricksParser(this.getPool()));

	}

	/**
	 * Add a new sub-tree to this parser
	 */
	public void addGrammar(RootExpression root){
		headExp.add(root);
	}

	/**
	 * Return the pool containing variables shared across the grammar tree
	 */
	public Pool getPool(){
		return pool;
	}

	/**
	 * kicks off the parsing process
	 */
	public void parse(Reader reader,ParsedDrawing parsedDrawing)
		throws REParserException {
		// [pending] change to "Drawing parse(Reader r, Drawing d)" allowing to pass an already init'd drawing


		// reinit Pool, then every RootExpression that was added to headExp :
		pool.reinit();
		ArrayList rootExpList = headExp.getExpressionList();
		for (Iterator it = rootExpList.iterator(); it.hasNext();){
			Object o = it.next();
			if (o instanceof RootExpression)  ((RootExpression)o).reinit();
		}

		try{
			interpret(context=new Context(reader));
		}
		catch(REParserException.EndOfPicture e){
			// check if every "begin group" has a matching "end group", which is the case
			// only if picGroupStack (the fifo that stores open groups) is empty.
			if (!pool.picGroupStack.empty()) throw new REParserException.BeginGroupMismatch(context, null);
			parsedDrawing.drawing = new Drawing(pool.currentGroup);
			// [pending] change to dr.addElement(pool.currentGroup)
			// [pending] set ContentType for Drawing
			parsedDrawing.drawing.setNotparsedCommands(pool.notParsed.toString()); // add commands that weren't parsed
			// to the "end" of the current drawing (i.e. these may appear at the end of the text file, though
			// not on the drawing).
			//System.out.println(this);
			return;
		}
		catch(REParserException.EOF e){
			if (!pool.picGroupStack.empty()) throw new REParserException.BeginGroupMismatch(context, null);
			parsedDrawing.drawing = new Drawing(pool.currentGroup);
			parsedDrawing.drawing.setNotparsedCommands(pool.notParsed.toString()); // add commands that weren't parsed
			//System.out.println(this);
			return;
		}
		throw new REParserException.EndOfPictureNotFound();
	}

	/**
	 * pour compatibilité ascendante.
	 *@since JPicEdt 1.6
	 */
	public Drawing parse(Reader reader) throws REParserException {
		ParsedDrawing dr = new ParsedDrawing();
		parse(reader,dr);
		return dr.drawing;
	}


	/**
	 * @return a String representation of this Parser, i.e. global variables contents
	 */
	public String toString(){

		return "DefaultParser :\n"
		       + pool.toString()
			   + "\nGrammar :\n"
			   + headExp.toString();
	}

	/**
	 * parser standalone test
	 * @param arg arg[0] is the name of file to parse.
	 */
	public static void main(String arg[]){

		DefaultParser parser = new DefaultParser(); // create a new grammar
		/*
		Pool pool = parser.getPool();
		parser.addGrammar(new jpicedt.format.input.latex.LaTeXParser(pool));
		parser.addGrammar(new jpicedt.format.input.eepic.EepicParser(pool));
		parser.addGrammar(new jpicedt.format.input.pstricks.PstricksParser(pool));
		*/

		FileReader reader=null;
		try{
			reader = new FileReader(arg[0]);
		} catch (IOException ioex){ioex.printStackTrace(); System.exit(0);}
		if (reader==null) System.exit(0);
		Drawing dr = null;
		try{
			dr=parser.parse(reader);
		}
		catch (Exception e){e.printStackTrace();}
		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		System.out.println("Default parser=" + parser);
		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		System.out.println("Parsed Drawing=" + dr);
		try{
			reader.close();
		} catch (IOException ioex){ioex.printStackTrace(); System.exit(0);}
		System.exit(0);

	}

	/**
	 *Extraction d'un dessin codé à partir d'un fichier, et analyse syntaxique de
	 *ce dessin codé.
	 *@param reader lecteur du fichier d'où extraire le dessin codé à analyser
	 *@since jPicEdt 1.6
	 */
	public ParsedDrawing extractAndParse(Reader reader) throws ParserException{
		return JPICParser.extractAndParse(new TeXExtractor(),this, reader);
	}
}
