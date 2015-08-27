// JPICXmlParser.java --- -*- coding: iso-8859-1 -*-
// Copyright 2009/2012 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: JPICXmlParser.java,v 1.6 2013/03/27 07:04:04 vincentb1 Exp $
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

import jpicedt.graphic.model.Drawing;
import java.io.Reader;
import java.io.IOException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;


/**
 * Une implantation de l'interface Parser pour analyser le code JPIC-XML enfoui.<p>
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Belaïche</a>
 * @since jpicedt 1.6
 */
public class JPICXmlParser implements ExtractionParsing{

	/**
	 * Initie l'analyse du code JPIC-XML. Le code JPIC-XML a déjà été extrait.
	 * @param reader Lecteur du code JPIC-XML
	 * @param dr une nouvelle instance de dessin peuplée avec les éléments qui
	 * ont été analysés dans le dessin codé en JPIC-XML.
	 * @since jpicedt 1.6
	 */
	public void parse(Reader reader,ParsedDrawing dr) throws ParserException {
		try {
			// ok, we have got an XML-ready String ; we must pass it to a SAX-parser as an InputSource object...
			InputSource inputSrc = new InputSource(reader);

			// instanciate SAX parser (following hints given in the "Java Tutorial on XML") :
			SAXParserFactory saxFactory = SAXParserFactory.newInstance(); // returns an org.apache.xerces.jaxp.SAXParserFactoryImpl when using SUN or IBM JVM's
			// possibly configure factory here to adapt parsing behaviours :
			//saxFactory.setNamespaceAware(true/false); // support XML namespacing ?
			//saxFactory.setValidating(true/false); // use DtD ?
			SAXParser xmlParser = saxFactory.newSAXParser(); // we don't use separate handlers, hence don't rely on the encapsulated XMLReader.

			// pass it our own content-handler :
			JPICXmlHandler xmlHandler = new JPICXmlHandler();
			// initiate the parsing process :
			xmlParser.parse(inputSrc, xmlHandler);
			// fetch parsed Drawing from our Handler
			dr.drawing = xmlHandler.fetchParsedDrawing();
			return;
		}
		catch (ParserConfigurationException pce){ // thrown by SAXParserFactory methods
			throw new ParserException(pce);
		}
		catch (SAXParseException se){ // [SR:pending] use getLineNumber() and getColumnNumber()
			throw new ParserException(se);
		}
		catch (SAXException se){
			throw new ParserException(se);
		}
		catch (IOException ioEx){
			throw new ParserException(ioEx);
		}
		catch (NumberFormatException nfe){
			throw new ParserException(nfe);
		}
	}

	/**
	 *@since JpicEdt 1.6
	 */
	public Drawing parse(Reader reader) throws ParserException{
		ParsedDrawing dr = new ParsedDrawing();
		parse(reader,dr);
		return dr.drawing;
	}

	/**
	 *C'est fourni au cas où on voudrait brider JPicEdt à un format de fichier
	 *donné. Il suffirait d'utiliser JPICXmlParser au lieu de {@link
	 *JPICParser} dans {@link jpicedt.MiscUtilities#createParser}.
	 *@since JpicEdt 1.6
	 */
	public ParsedDrawing extractAndParse(Reader reader) throws ParserException{
		return JPICParser.extractAndParse(new NakedJPICXmlExtractor(),this,reader);
	}
}




/// JPICXmlParser.java ends here
