// AbstractFormatterFactory.java --- -*- coding: iso-8859-1 -*-
// January 15, 2007 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2007 Sylvain Reynal
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
// Version: $Id: AbstractFormatterFactory.java,v 1.9 2013/05/11 06:40:11 vincentb1 Exp $
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

package jpicedt.graphic.io.formatter;

import jpicedt.format.output.util.TeXCommentFormatter;
import jpicedt.graphic.model.Element;
import jpicedt.graphic.model.Drawing;
import jpicedt.graphic.model.PicAttributeName;
import jpicedt.graphic.model.PicText;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;
import java.lang.reflect.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static jpicedt.Log.*;

/**
 * Classe de base des fabriques de formatteurs. Permet de mapper des classes d'éléments du modèle graphique
 * sur leur formatteurs respectifs pour un <code>ContentType</code> donné.
 * @author Sylvain Reynal, Vincent Belaïche
 * @since jpicedt 1.5
 */
public abstract class AbstractFormatterFactory implements FormatterFactory {

	/**
	 * Line separator for the current platform (i.e. "\n" on Unix, "\n\r" on Windows, "\r" on Mac,&hellip;)
	 */
	private static final String CR_LF = System.getProperty("line.separator");

	private static Pattern eolRe = Pattern.compile("\r?\n|\r");

	/** le séparateur multi-ligne courant pour ce fichier de sauvegarde */
	protected String lineSeparator = CR_LF;

	public String getLineSeparator(){ return lineSeparator; }
	public void   setLineSeparator(String lineSeparator){ this.lineSeparator = lineSeparator; }

	protected CommentFormatting commentFormatter = null;

	/**
	   Écrit la chaîne <code>in</code> en respectant le séparateur de ligne du fichier de sauvegarde.
	 */
	public void stringWriteMultiLine(Writer out, String in) throws java.io.IOException{
		int pos = 0;
		Matcher m = eolRe.matcher(in);
		while(m.find(pos))
		{
			out.append(in.substring(pos, m.start()));
			out.append(lineSeparator);
			pos = m.end();
		}
		out.append(in.substring(pos));
	}

	//public String stringMultiline(String in){
	//	StringWriter out = new StringWriter(in.length()+4);
	//	try
	//	{
	//		stringWriteMultiLine(out, in);
	//	}
	//	catch(IOException io)
	//	{
	//		io.printStackTrace();
	//	}
	//	return out.toString();
	//}

	public void textWriteMultiLine(Writer out, PicText in) throws java.io.IOException{
		if(in.getAttribute(PicAttributeName.TEXT_MODE) == PicText.TextMode.TEXT_AREA)
			stringWriteMultiLine(out, in.getText());
		else
			out.append(in.getText());
	}

	/** a HashMap that associates Formatter's classes with Element's classes */
	protected HashMap<Class<? extends Element>,Class<? extends Formatter>> factoryMap;

	public boolean revertedArrowsAttribute(){
		error("revertedArrowsAttribute should not be called at Factory level");
		return false;
	}

	public AbstractFormatterFactory(){
		factoryMap = new HashMap<Class<? extends Element>,Class<? extends Formatter>>();
	}

	public String toString(){
		String s = super.toString();
		s += "factoryMap:\n";
		for (Class<? extends Element> c: factoryMap.keySet()){
			s += c + " => " + factoryMap.get(c) + "\n";
		}
		return s;
	}

	/**
	 * Associates the given Formatter's class with the given Element's class in the hashmap responsible
	 * for creating Formatter's for Element's.
	 * @since jpicedt 1.5
	 */
	public void map(Class<? extends Element> classElement, Class<? extends Formatter> classFormatter){
		factoryMap.put(classElement,classFormatter);
	}

	/**
	 * Remove the element/view mapping for the given Element's class in the hashmap responsible
	 * for creating Formatter's for Element's.
	 * @since jpicedt 1.5
	 */
	public void unmap(Class<? extends Element> classElement){
		factoryMap.remove(classElement);
	}

	/**
	 * Returns the Formatter's class associated with the given Element's class in the hashmap responsible
	 * for creating Formatter's for Element's.
	 * @since jpicedt 1.5
	 */
	public Class<? extends Formatter> getMappedClass(Class<? extends Element> classElement){
		Class<? extends Formatter> classFormatter = factoryMap.get(classElement);
		Class superclassElement = classElement;
		while (classFormatter == null){
			superclassElement = superclassElement.getSuperclass();
			if (Element.class.isAssignableFrom(superclassElement)){ // if superclassElement extends Element
				classFormatter = factoryMap.get(superclassElement);
			}
			else
				break;
		}
		if (classFormatter==null) return null;
		return (Class<? extends Formatter>)classFormatter; // pseudo unchecked cast, it's ok if HashMap has been populated in a proper way
	}

	/**
	 * @return a Formatter able to format the given Element according to the format of this factory
	 * @since jpicedt 1.5
	 */
	public Formatter createFormatter(Element element){
		if (DEBUG) debug("Creating formater for "+element);
		//if (DEBUG) debugAppendLn(toString());
		Class<? extends Formatter> classFormatter = getMappedClass(element.getClass());
		if (DEBUG) debugAppendLn("Mapped to:" + classFormatter);
		if (classFormatter==null) return null;
		try {
			// look for a constructor like Formatter(<? extends Element> e, <? extends FormatterFactory> f)
			//Constructor<?>[] constructors = classFormatter.getConstructors();
			for (Constructor<?> c:  classFormatter.getConstructors()){
				if (DEBUG) debugAppendLn("Constructor:" + c);
				Class<?>[] params = c.getParameterTypes();
				// check if element instanceof param[0] and this instanceof params[1]
				if (params.length == 2 && params[0].isAssignableFrom(element.getClass()) && params[1].isAssignableFrom(this.getClass())){
					Constructor<? extends Formatter> cc = classFormatter.getConstructor(params[0],params[1]);
					Formatter v = cc.newInstance(element,this);
					return v;
				}
			}
			return null;
		}
		catch (Exception e){
			System.err.println("Formatter " + classFormatter + " for element " + element.getClass() + " can't be instantiated.");
			e.printStackTrace();
			return null;
		}
	}

	/** Renvoie un formatteur de commentaire, ce formatteur sert à encapsuler
   	 *	le code JPIC-XML dans un autre type de contenu sous la forme de
	 *	commentaires forts (pour la délimitation du début et de la fin du code
	 *	JPIC-XML) et faibles (pour le code JPIC-XML lui-même)
	 *	@return le formatteur de commentaire correspondant au types de contenu
	 *	LaTeX, Epic/Eepic, et PsTrick.
	 */
	public CommentFormatting getCommentFormatter()
		{
			if(commentFormatter == null)
				commentFormatter = new TeXCommentFormatter(this);
			return commentFormatter;
		}

	/** Configure le formatteur de commentaire. Utile seulement pour les format non dérivé TeX comme DXF
	 * car {@line #getCommentFormatter() getCommentFormatter} instancie par défaut un formatteur de type
	 * <code>TeXCommentFormatter</code>. */
	public void setCommentFormatter(CommentFormatting commentFormatter){
		this.commentFormatter = commentFormatter;
	}

	/**
	 * C'est juste pour avoir une implantation par défaut pour les types de
	 * contenu qui n'ont pas besoin de cette méthode d'interface.
	 * @return null pour le prologue de formatage d'un fichier autonome (stand-alone)
	 * correspondant au type de contenu offrant cette interface.
	 */
	protected String getFileWrapperProlog(){ return null;}

	/**
	 * C'est juste pour avoir une implantation par défaut pour les types de
	 * contenu qui n'ont pas besoin de cette méthode d'interface.
	 * @return null l'épilogue de formatage d'un fichier autonome (stand-alone)
	 * correspondant au type de contenu offrant cette interface.
	 */
	protected String getFileWrapperEpilog(){ return null;}

	/** Renvoie le <code>this</code>. Utile pour les sous-classes non statiques. */
	protected FormatterFactory getFormatterFactory(){ return this; }

}
