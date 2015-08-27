// JPICParser.java --- -*- coding: iso-8859-1 -*-
// August 1, 2003 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2006 Sylvain Reynal
// Copyright (C) 2007/2013 Sylvain Reynal, Vincent Bela�che
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
// Version: $Id: JPICParser.java,v 1.12 2013/03/27 07:21:19 vincentb1 Exp $
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
package jpicedt.graphic.io.parser;

import jpicedt.graphic.model.Drawing;
import java.io.Reader;
import java.io.StringReader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import  jpicedt.format.input.util.TeXJPICXmlExtractor;
import  jpicedt.format.input.util.OpenLaTeXJPICXmlExtractor;
import  jpicedt.format.input.dxf.DXFJPICXmlExtractor;
import jpicedt.format.input.util.TeXExtractor;
import jpicedt.format.input.dxf.DXFExtractor;


/**
 * An implementation of the Parser interface dedicated to parsing JPIC-XML files.<p>
 * <b>How it works :</b>
 * We first try to know if we're parsing a JPIC-XML formatted file, i.e. starting with a "%%Begin JPIC-XML"
 * marker.
 * If this is the case :
 * <ul>
 * <li> look up matching "%%End JPIC-XML" marker
 * <li> save text between both markers, and drop leading "%"
 * <li> pass corresponding text to our JPIC-XML parser (implementation based on a Java-SAX parser).
 * <li> return parsed Drawing (parsing process then stops here)
 * </ul>
 * Otherwise, go ahead using the fallback Parser, if any.
 * @author Sylvain Reynal
 * @since jpicedt 1.3.3
 * @version $Id: JPICParser.java,v 1.12 2013/03/27 07:21:19 vincentb1 Exp $
 */
public class JPICParser implements ExtractionParsing{

	final CodedContentExtraction codedContentExtractions[] = {
		new TeXJPICXmlExtractor(),
		new OpenLaTeXJPICXmlExtractor(),
		new DXFJPICXmlExtractor(),
		new NakedJPICXmlExtractor(),
        new TeXExtractor(),
        new DXFExtractor()
	};

	/**
	 * kicks off the parsing process
	 * @return a new instance of a Drawing populated from the content of the
	 * given reader, along with extra-drawing information obtained by the extractor.
	 * @throws ParserException when a parsing error was encountered, e.g. syntax error, block mismatch,...
	 */
	public ParsedDrawing extractAndParse(Reader reader) throws ParserException {
		// first save stream content to a StringBuffer, for later use by fallback :
		String inputString = readerToString(reader);
		// now reader is UNUSABLE because it's been totally read

		ParsedDrawing parsedDrawing = new ParsedDrawing();
		// Maintenant essaie d'extraire un dessin cod� dans l'un des formats
		// g�r�s, en commen�ant pas JPIC-XML. Si avec JPIC-XML �a ne donne
		// rien on se rabat sur les autres formats.
		for(CodedContentExtraction codedContentExtraction :codedContentExtractions)
		{
			CodedContentExtraction.ExtractedCodedContent extractedCodedContent =
                 codedContentExtraction.extractCodedContent(inputString);
			if(extractedCodedContent != null)
			{
				Parser parser = codedContentExtraction.getParser();
				parser.parse(
					new StringReader(extractedCodedContent.getExtractedCodedText()),
					parsedDrawing);
				extractedCodedContent.qualifyDrawing(parsedDrawing);
				parsedDrawing.sourceType = extractedCodedContent.sourceType;
				return parsedDrawing;
			}
		}
		throw new ParserException.UnrecognizedFileFormat();
	}



	/**
	 * Analyse du dessin cod� seulement, l'extraction a d�j� �t� faite par l'appelant.
	 * @param parsedDrawing valeur retourn�e d'une nouvelle instance de dessin
	 * (<code>Drawing</code>) peupl�e des �l�ments instanci�e lors de l'analyse du dessin
	 * cod� lu � partir du <code>reader</code>.
	 * @throws ParserException en cas d'erreur lors de l'analyse syntaxique.
	 *@since JpicEdt 1.6
	 */
	public void parse(Reader reader,ParsedDrawing parsedDrawing)
		throws ParserException {
		parsedDrawing.weakCopy(extractAndParse(reader));
	}


	/**
	 * Fourni pour compatibilit� ascendante. Utiliser {@link #parse(Reader
	 * reader,ParsedDrawing parsedDrawing)}.
	 *@since JpicEdt 1.6
	 */
	public Drawing parse(Reader reader) throws ParserException {
		ParsedDrawing dr = new ParsedDrawing();
		parse(reader,dr);
		return dr.drawing;
	}

	/**
	 * for quick debugging purpose
	 */
	public static void main(String[] args){
		if (args.length == 0){
			System.out.println("Usage : java jpicedt.graphic.io.parser.JPICParser filename");
			System.exit(0);
		}
		try {
			BufferedReader reader = new BufferedReader(new FileReader(args[0]));
			JPICParser parser = new JPICParser();
			Drawing dr = parser.parse(reader);
			System.out.println(dr);
		}
		catch (Exception ex){
			System.out.println(ex);
		}
	}


	/**
	 * Returns a String initialized from the given Reader
	 */
	static private String readerToString(Reader reader){
		StringBuffer output = new StringBuffer(100);
		BufferedReader input = new BufferedReader(reader); // gives access to a readLine() method which handles CR-LF properly
		String line=null;
		try {
			while ((line=input.readLine())!=null){
				output.append(line);
				output.append('\n'); // convert CR to Unix-like CR
			}
		}
		catch (IOException ioEx){}
		return output.toString();
	}

	public static ParsedDrawing extractAndParse(
		CodedContentExtraction extractor,
		Parser                 parser,
		Reader                 reader
		)  throws ParserException{
		CodedContentExtraction.ExtractedCodedContent extractedCodedContent =
			extractor.extractCodedContent(readerToString(reader));
		if(extractedCodedContent != null)
		{
			ParsedDrawing parsedDrawing = new ParsedDrawing();
			parser.parse(reader,parsedDrawing);
			extractedCodedContent.qualifyDrawing(parsedDrawing);
			return parsedDrawing;
		}
		throw new ParserException.UnrecognizedFileFormat();
	}


	/**
	 * Transform the given input String by removing any leading "%"
	 * <br>
	 * Used so far by extractXMLText only.
	 */
	private String removeLeadingCommentPrefix(String str){
		if (str.startsWith("%")) return str.substring(1);
		else return str;
	}

}
