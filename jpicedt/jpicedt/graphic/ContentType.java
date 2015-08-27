// ContentType.java --- -*- coding: iso-8859-1 -*-
// February 16, 2002 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2013 Sylvain Reynal
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
// Version: $Id: ContentType.java,v 1.10 2013/03/27 07:07:32 vincentb1 Exp $
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
package jpicedt.graphic;

import jpicedt.graphic.toolkit.EditorKit;
import jpicedt.graphic.toolkit.AbstractCustomizer;
import jpicedt.graphic.io.formatter.FormatterFactory;
import jpicedt.graphic.view.ViewFactory;

import java.util.*;

/**
 * Specifies a content-type (aka mime-type) for a Drawing document created by an editor-kit. <p>
 * This interface is aimed at lumping together top-level classes needed by a given content-type
 * in the View-Model-Control framework : so far this involves obtaining an instance of EditorKit
 * appropriate for the given content-type, as well as a corresponding FormatterFactory.
 * A <code>configure</code> method is provided for conveniently configuring editor-kits and factories before use.
 * <p>
 * [SR:pending] better move to jpicedt.graphic.io ?
 * @since jpicedt 1.3.2
 * @author Sylvain Reynal
 * @version $Id: ContentType.java,v 1.10 2013/03/27 07:07:32 vincentb1 Exp $
 *
 */
public interface ContentType {

	/**
	 * Return the presentation name of this content-type
	 */
	String getPresentationName();

	/**
	 * Creates a ViewFactory that's suited for this content-type
	 */
	ViewFactory createViewFactory();

	/**
	 * Creates a FormatterFactory that's suited for this content-type
	 */
	FormatterFactory createFormatter();

	/**
	 * Configure the EditorKit and the FormatterFactory returned by the factory methods,
	 * from the given Properties
	 */
	void configure(Properties p);

	/**
	 * Returns a customizer panel for this content-type
	 * @return null if no customizer is available for this content-type
	 * @param prop used to init the component or to store preferences on-demand.
	 */
	 AbstractCustomizer createCustomizer(Properties prop);

}
