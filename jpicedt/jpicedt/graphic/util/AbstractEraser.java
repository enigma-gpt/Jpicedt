// AbstractEraser.java --- -*- coding: iso-8859-1 -*-
// Copyright 2009/2011 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: AbstractEraser.java,v 1.6 2013/03/27 06:56:06 vincentb1 Exp $
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
package jpicedt.graphic.util;
import jpicedt.graphic.model.*;

/**
 * <p> classe abstraite d'un effaceur. Les sous-classes permettent d'effacer
 * une zone convexe d'un Element.  </p>
 * <p> Par effacement on entend juste qu'on calcule le résultat de
 * l'effacement: l'élément graphique passé en entrée n'est ni détruit, ni
 * modifié, ni retiré d'un dessin.</p>
 *
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Belaïche</a>
 * @since jPicEdt 1.6
 * @version $Id: AbstractEraser.java,v 1.6 2013/03/27 06:56:06 vincentb1 Exp $
 */
public abstract class AbstractEraser{
	public enum ErasureStatus{
		NO_ERASURE, PARTIALLY_ERASED, TOTALLY_ERASED};

	ErasureStatus status = ErasureStatus.TOTALLY_ERASED;
	Element       erasedElt = null;

	/**
	 * Renvoie l'<code>Element</code> après avoir effacement. Selon l'état
	 * d'affacement cela peut être le même élément que celui donné en entré
	 * &mdash; c'est à dire qu'aucun effacement n'aurait eu lieu &mdash; ou un
	 * élément distinct, ou pas d'élément du tout si l'effacement est total,
	 * c'est à dire que <code>null</code> est renvoyé.
	 * @return la valeur <code>Element</code> résultant de l'effacement
	 */
	public Element       getErasedElt(){ return erasedElt; }


	/**
	 * Renvoie l'état de l'effacement. Cela peut être: <dl>
	 * <dt><code>NO_ERASURE</code></dt><dd>Aucun n'effacement n'a eu lieu
	 * l'élement renvoyé par <code>getErasedElt</code> est identique &mdash;
	 * pas de clonage &mdash; à celui passé en entrée.</dd>
	 * <dt><code>PARTIALLY_ERASED</code></dt><dd>L'élément en entrée à état
	 * partiellement effacé, le résultat est un nouvel élément renvoyé au
	 * moyen de <code>getErasedElt</code></dd>
	 * <dt><code>TOTALLY_ERASED</code></dt><dd></dd>L'élément en entrée a été totalement effacé</dl>
	 * @return an <code>ErasureStatus</code> value
	 */
	public ErasureStatus getStatus(){ return status;}

};



/// AbstractEraser.java ends here
