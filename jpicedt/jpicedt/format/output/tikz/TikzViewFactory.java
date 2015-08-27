// TikzViewFactory.java --- -*- coding: iso-8859-1 -*-
// Copyright 2009/2011 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: TikzViewFactory.java,v 1.6 2013/03/27 07:08:22 vincentb1 Exp $
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
	 * @return une View pour l'élément passé en argument. Ceci permet de
	 * renvoyer une vue spécifique à Tikz pour certaine classe d'objet
	 * graphique. Par défaut appelle la superclass sinon.
	 * @since jPicEdt 1.6
	 */
	public View createView(Element element){
		if (DEBUG) debug("Creating view for "+element);
		return super.createView(element);
	}


	/**
	 * La méthode <code>configure</code> est appelée lorsque les préférences utilisateurs sont mises à jour.
	 * @param preferences une valeur <code>Properties</code> utilsier pour lire les préférences
	 * utilisateur. Si <code>null</code>, les valeurs par défaut sont utilisées.
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
	 * @return une <code>ArrowView</code> déduite du <code>PicAttributeSet set</code> passé en argument, ou
	 * <code>null</code> si le style de la flèche est <code>ArrowStyle.NONE</code>.
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
