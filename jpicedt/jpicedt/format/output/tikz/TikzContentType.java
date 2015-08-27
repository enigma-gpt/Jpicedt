// TikzContentType.java --- -*- coding: iso-8859-1 -*-
// Copyright 2008/2011 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: TikzContentType.java,v 1.5 2013/03/27 07:08:50 vincentb1 Exp $
// Keywords: Tikz, PGF
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

/// Installation:


/// Code:
package jpicedt.format.output.tikz;

import jpicedt.graphic.toolkit.AbstractCustomizer;
import jpicedt.graphic.ContentType;
import jpicedt.graphic.io.formatter.FormatterFactory;
import jpicedt.graphic.view.ViewFactory;

import java.util.*;

/**
 * Sp�cifie le type de contenue pour des Drawing cr��s par des editor-kits.
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela�che</a>
 * @since jPicEdt 1.6
 */
public class TikzContentType implements ContentType {

	protected String name, mime;

	/**
	 * @return le nom de pr�sentation de ce type de contenu
	 * @since jPicEdt 1.6
	 */
	public String getPresentationName(){
		return "TikZ";
	}

	/**
	 * Cr�e un ViewFactory adapt� � ce type de contenu
	 * @since jPicEdt 1.6
	 */
	public ViewFactory createViewFactory(){
		return new TikzViewFactory();
	}


	/**
	 * Cr�e un FormatterFactory adapt� � ce type de contenu
	 * l'implantation par d�faut renvoie null.
	 * @since jPicEdt 1.6
	 */
	public FormatterFactory createFormatter(){
		return new TikzFormatter();
	}

	/**
	 * @param prop : propri�t�s utilis� pour initialiser le customiseur ou pour
	 * stocker les pr�f�rences de l'utiulisateur � la demande.
	 * @return un panneau de customization pour ce type de contenu
	 * @since jPicEdt 1.6
	 */
	 public AbstractCustomizer createCustomizer(Properties prop){
		 return new TikzCustomizer(prop);
	 }

	/**
	 * Configure l' EditorKit et le FormatterFactory renvoy� par les m�thodes
	 * de la fabrique factory, pour les propri�t� p pass�es en argument.
	 * @param p : propri�t�s pour configurer la fabrique.
	 * @since jPicEdt 1.6
	 */
	public void configure(Properties p){
		TikzFormatter.configure(p);
	}

}



/// TIKZContentType.java ends here
