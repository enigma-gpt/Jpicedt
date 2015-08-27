// TikzViewFactory.java --- -*- coding: iso-8859-1 -*-
// Copyright 2009/2011 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: TikzViewFactory.java,v 1.6 2013/03/27 07:08:22 vincentb1 Exp $
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



/// Code:
package jpicedt.format.output.tikz;

import jpicedt.graphic.model.Element;
import jpicedt.graphic.model.PicAttributeName;
import jpicedt.graphic.model.PicAttributeSet;
import jpicedt.graphic.view.DefaultViewFactory;
import jpicedt.graphic.view.ArrowView;
import jpicedt.graphic.view.View;

import java.util.Properties;

import static jpicedt.Log.debug;
import static jpicedt.Log.DEBUG;
import static jpicedt.graphic.model.StyleConstants.*;
import static jpicedt.graphic.view.ViewConstants.*;


/**
 * Fabrique de <code>View</code> pour le type de contenu TikZ.
 *
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela&iuml;che</a>
 * @since jPicEdt 1.6
 * @version $Id: TikzViewFactory.java,v 1.6 2013/03/27 07:08:22 vincentb1 Exp $
 */
public class TikzViewFactory extends DefaultViewFactory
{
	private static TikzCustomProperties tikzCustomProperties = new TikzCustomProperties();

	public  TikzViewFactory(){
		super();
		if (DEBUG) debug("Initializing");
		if (DEBUG) debug(toString());
	}


	/**
	 * @return une View pour l'�l�ment pass� en argument. Ceci permet de
	 * renvoyer une vue sp�cifique � Tikz pour certaine classe d'objet
	 * graphique. Par d�faut appelle la superclass sinon.
	 * @since jPicEdt 1.6
	 */
	public View createView(Element element){
		if (DEBUG) debug("Creating view for "+element);
		return super.createView(element);
	}


	/**
	 * La m�thode <code>configure</code> est appel�e lorsque les pr�f�rences utilisateurs sont mises � jour.
	 * @param preferences une valeur <code>Properties</code> utilsier pour lire les pr�f�rences
	 * utilisateur. Si <code>null</code>, les valeurs par d�faut sont utilis�es.
	 */
	public static void configure(Properties preferences){
		DefaultViewFactory.configure(preferences); // super
		int changed;
		if(preferences == null)
			changed = tikzCustomProperties.loadDefault();
		else
			changed = tikzCustomProperties.load(preferences);

		if((changed & TikzCustomProperties.ChangedPropertyMask.HAS_ARROW_TIP_PACKAGE.value()) != 0){
			// TODO
		}
	}

	/**
	 * @return une <code>ArrowView</code> d�duite du <code>PicAttributeSet set</code> pass� en argument, ou
	 * <code>null</code> si le style de la fl�che est <code>ArrowStyle.NONE</code>.
	 * @param direction <code>LEFT_ARROW</code> ou <code>RIGHT_ARROW</code>.
	 */
	public ArrowView createArrow(PicAttributeSet set, PicAttributeName<ArrowStyle> direction){
		//if (DEBUG) debug(".createArrow");
		ArrowStyle a = set.getAttribute(direction);
		a = TikzUtilities.toTZArrow(a).getTZArrow(tikzCustomProperties.getHasArrowTipPackage()).getArrowStyle();

		ArrowView	v = ArrowView.createArrowView(a,set);
		return v;
	}


}


/// TikzViewFactory.java ends here
