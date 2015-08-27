// PolynomialRealRoot.java --- -*- coding: iso-8859-1-unix -*-

// Copyright 2008 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: PolynomialRealRoot.java,v 1.3 2013/03/27 06:49:41 vincentb1 Exp $
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
 * racine r�elle d'un polyn�me Polynomial r�el monovari�
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela�che</a>
 * @since jPicEdt 1.6
 */
public class PolynomialRealRoot
{
	double value;
	int    order;

	/**
	 * Classe implantant l'interface <code>Comparator</code> et permettant le
	 * tri des <code>PolynomialRealRoot</code> par valeur.
	 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela�che</a>
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

	/** Renvoie un comparateur adapt� au tri par valeur des PolynomialRealRoot
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

	/** Incr�mente l'ordre de la PolynomialRealRoot this.
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
	 * Convertit this en cha�ne de caract�re � des fins de d�bogage.
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
		System.err.println("l apr�s sort="+l);
		l.add(new PolynomialRealRoot(.5));
		System.err.println("l add .5="+l);
		Collections.sort(l,PolynomialRealRoot.getSortByValueComparator());
		System.err.println("l sort="+l);

	}

}



/// PolynomialRealRoot.java ends here
