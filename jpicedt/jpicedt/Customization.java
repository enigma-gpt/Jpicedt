// Customization.java --- -*- coding: iso-8859-1 -*-
// Copyright 2008/2013 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: Customization.java,v 1.7 2013/03/27 07:19:04 vincentb1 Exp $
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

/// Installation:

/// Code:
package jpicedt;
import java.util.Properties;

/**
 * Interface permettant de charger/décharger des propriétés de personnalisation
 * dans une classe en dépendant.
 *@author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Belaïche</a>
 *@since jPicEdt 1.6
 */
public interface Customization {
	/**
	 * Loads widgets display content with a default value,
	 * presumably from a "default preferences" file or a dedicated storage class.
	 *@since jPicEdt 1.6
	 */
	int loadDefault();

	/**
	 * Loads  Customization value from a Properties prop
	 *@param prop Propriétés de personnalisation à charger dans this
	 *@since jPicEdt 1.6
	 */
	int load(Properties prop);

	/**
	 * Stores current Customization value to a Properties prop
	 *@param prop Propriétés de personnalisation dans lesquelles this mémorise
	 *les valeurs de propriétés qui lui sont courantes.
	 *@since jPicEdt 1.6
	 */
	void store(Properties prop);


}



/// Customization.java ends here
