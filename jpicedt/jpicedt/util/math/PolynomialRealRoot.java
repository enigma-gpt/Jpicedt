// PolynomialRealRoot.java --- -*- coding: iso-8859-1-unix -*-

// Copyright 2008 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: PolynomialRealRoot.java,v 1.3 2013/03/27 06:49:41 vincentb1 Exp $
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

// import javax.swing.*;
// import java.awt.*;
// import java.awt.event.*;
// import jpicedt.graphic.model.*;
// import java.lang.Math.*;


/// Code:
package jpicedt.util.math;
import java.util.*;


/**
 * racine réelle d'un polynôme Polynomial réel monovarié
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Belaïche</a>
 * @since jPicEdt 1.6
 */
public class PolynomialRealRoot
{
	double value;
	int    order;

	/**
	 * Classe implantant l'interface <code>Comparator</code> et permettant le
	 * tri des <code>PolynomialRealRoot</code> par valeur.
	 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Belaïche</a>
	 * @since jPicEdt 1.6
	 */
	static private class SortByValueComparator
		implements Comparator<PolynomialRealRoot>{
		public int compare(PolynomialRealRoot o1,PolynomialRealRoot o2){

			if(o1.value < o2.value)
				return -1;
			else if(o1.value == o2.value)
			{
				return 0;
			}
			else
				return +1;
		}
	};

	static private final SortByValueComparator SORT_BY_VALUE_COMPARATOR =
		new SortByValueComparator();

	/** Renvoie un comparateur adapté au tri par valeur des PolynomialRealRoot
	 * @since jPicEdt 1.6
	 */
	public static Comparator<PolynomialRealRoot> getSortByValueComparator(){
		return SORT_BY_VALUE_COMPARATOR;
	}


	/** Construit une PolynomialRealRoot de valeur value et d'ordre order
	 * @since jPicEdt 1.6
	 */
	public PolynomialRealRoot(double value,int order){
		this.value = value;
		this.order = order;
	}

	/** Construit une PolynomialRealRoot de valeur value et d'ordre 1
	 * @since jPicEdt 1.6
	 */
	public PolynomialRealRoot(double value){
		this.value = value;
		this.order = 1;
	}

	/** Incrémente l'ordre de la PolynomialRealRoot this.
	 * @since jPicEdt 1.6
	 */
	public void incOrder(){
		++order;
	}

	/** Obtient la valeur de la PolynomialRealRoot this.
	 * @since jPicEdt 1.6
	 */
	public double getValue(){ return value; }

	/** Affecte la valeur de la PolynomialRealRoot this.
	 * @since jPicEdt 1.6
	 */
	public void setValue(double v){ value = v; }

	/**
	 * Convertit this en chaîne de caractère à des fins de débogage.
	 * @since jPicEdt 1.6
	 */
	public String toString(){
		return ((Double)value).toString() + "(order="
			+ ((Integer)order).toString()+")";
	}


	/** test unitaire sur le tri des zero
	 * @since jPicEdt 1.6
	 */
	public static void main(String[] av){
		ArrayList<PolynomialRealRoot> l = new ArrayList<PolynomialRealRoot>();
		l.add(new PolynomialRealRoot(2.0));
		l.add(new PolynomialRealRoot(1.0));
		System.err.println("l avant sort="+l);
		Collections.sort(l,PolynomialRealRoot.getSortByValueComparator());
		System.err.println("l après sort="+l);
		l.add(new PolynomialRealRoot(.5));
		System.err.println("l add .5="+l);
		Collections.sort(l,PolynomialRealRoot.getSortByValueComparator());
		System.err.println("l sort="+l);

	}

}



/// PolynomialRealRoot.java ends here
