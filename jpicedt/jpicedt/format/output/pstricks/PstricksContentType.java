/*
 ContentType.java - February 16, 2002 - jPicEdt, a picture editor for LaTeX.
 Copyright (C) 1999-2006 Sylvain Reynal

 Departement de Physique
 �cole Nationale Sup�rieure de l'�lectronique et de ses Applications (ENSEA)
 6, avenue du Ponceau
 F-95014 CERGY CEDEX

 Tel : +33 130 736 245
 Fax : +33 130 736 667
 e-mail : reynal@ensea.fr
 jPicEdt web page : http://www.jpicedt.org/

*/
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
package jpicedt.format.output.pstricks;

import jpicedt.graphic.toolkit.EditorKit;
import jpicedt.graphic.toolkit.AbstractCustomizer;
import jpicedt.graphic.io.formatter.FormatterFactory;
import jpicedt.graphic.ContentType;
import jpicedt.graphic.view.ViewFactory;
import jpicedt.graphic.view.DefaultViewFactory;

import java.util.*;

/**
 * ContentType for the Pstricks format.
 * @author Sylvain Reynal
 * @version $Id: PstricksContentType.java,v 1.11 2013/03/27 07:09:15 vincentb1 Exp $
 */
public class PstricksContentType implements ContentType {

	protected String name, mime;

	/**
	 * @return the presentation name of this content-type
	 */
	public String getPresentationName(){
		return "PSTricks";
	}

	/**
	 * Creates a ViewFactory that's suited for this content-type
	 */
	public ViewFactory createViewFactory(){
		return new DefaultViewFactory();
	}


	/**
	 * creates a FormatterFactory that's suited for this content-type
	 * default implementation returns null.
	 */
	public FormatterFactory createFormatter(){
		return new PstricksFormatter();
	}

	/**
	 * Returns a customizer panel for this content-type
	 * @param props used to init the UI component or to store user-preferences on demand
	 */
	 public AbstractCustomizer createCustomizer(Properties props){
		 return new PstricksCustomizer(props);
	 }

	/**
	 * Configure the EditorKit and the FormatterFactory returned by the factory methods,
	 * from the given Properties
	 */
	public void configure(Properties p){
		PstricksFormatter.configure(p);
	}

}
