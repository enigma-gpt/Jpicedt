// TikzContentType.java --- -*- coding: iso-8859-1 -*-
// Copyright 2008/2011 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: TikzContentType.java,v 1.5 2013/03/27 07:08:50 vincentb1 Exp $
// Keywords: Tikz, PGF
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

/// Installation:


/// Code:
package jpicedt.format.output.tikz;

import jpicedt.graphic.toolkit.AbstractCustomizer;
import jpicedt.graphic.ContentType;
import jpicedt.graphic.io.formatter.FormatterFactory;
import jpicedt.graphic.view.ViewFactory;

import java.util.*;

/**
 * Spécifie le type de contenue pour des Drawing créés par des editor-kits.
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Belaïche</a>
 * @since jPicEdt 1.6
 */
public class TikzContentType implements ContentType {

	protected String name, mime;

	/**
	 * @return le nom de présentation de ce type de contenu
	 * @since jPicEdt 1.6
	 */
	public String getPresentationName(){
		return "TikZ";
	}

	/**
	 * Crée un ViewFactory adapté à ce type de contenu
	 * @since jPicEdt 1.6
	 */
	public ViewFactory createViewFactory(){
		return new TikzViewFactory();
	}


	/**
	 * Crée un FormatterFactory adapté à ce type de contenu
	 * l'implantation par défaut renvoie null.
	 * @since jPicEdt 1.6
	 */
	public FormatterFactory createFormatter(){
		return new TikzFormatter();
	}

	/**
	 * @param prop : propriétés utilisé pour initialiser le customiseur ou pour
	 * stocker les préférences de l'utiulisateur à la demande.
	 * @return un panneau de customization pour ce type de contenu
	 * @since jPicEdt 1.6
	 */
	 public AbstractCustomizer createCustomizer(Properties prop){
		 return new TikzCustomizer(prop);
	 }

	/**
	 * Configure l' EditorKit et le FormatterFactory renvoyé par les méthodes
	 * de la fabrique factory, pour les propriété p passées en argument.
	 * @param p : propriétés pour configurer la fabrique.
	 * @since jPicEdt 1.6
	 */
	public void configure(Properties p){
		TikzFormatter.configure(p);
	}

}



/// TIKZContentType.java ends here
