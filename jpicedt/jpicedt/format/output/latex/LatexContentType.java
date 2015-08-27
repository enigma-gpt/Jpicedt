// LatexContentType.java --- -*- coding: iso-8859-1 -*-
// February 16, 2002 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999-2006 Sylvain Reynal
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
// Version: $Id: LatexContentType.java,v 1.9 2013/03/27 07:10:05 vincentb1 Exp $
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

import jpicedt.graphic.ContentType;
import jpicedt.graphic.toolkit.EditorKit;
import jpicedt.graphic.toolkit.AbstractCustomizer;
import jpicedt.graphic.io.formatter.FormatterFactory;
import jpicedt.graphic.view.ViewFactory;

import java.util.*;


/**
 * Specifies content-type for Drawing's created by editor-kits.
 */
public class LatexContentType implements ContentType {

	/**
	 * @return the presentation name of this content-type
	 */
	public String getPresentationName(){
		return "LaTeX";
	}

	/**
	 * Creates a ViewFactory that's suited for this content-type
	 */
	public ViewFactory createViewFactory(){
		return new LatexViewFactory();
	}

	/**
	 * creates a FormatterFactory that's suited for this content-type
	 * default implementation returns null.
	 */
	public FormatterFactory createFormatter(){
		return new LatexFormatter();
	}

	/**
	 * Returns a customizer panel for this content-type
	 * @param prop used to init the component or to store user-preferences on demand
	 */
	 public AbstractCustomizer createCustomizer(Properties prop){
		 return new LatexCustomizer(prop);
	 }


	/**
	 * Configure the EditorKit and the FormatterFactory returned by the factory methods,
	 * from the given Properties
	 */
	public void configure(Properties p){
		LatexViewFactory.configure(p);
		LatexFormatter.configure(p);
	}

}
