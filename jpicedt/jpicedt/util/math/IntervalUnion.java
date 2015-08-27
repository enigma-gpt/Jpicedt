// IntervalUnion.java --- -*- coding: iso-8859-1-unix -*-
// Copyright 2008/2013 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: IntervalUnion.java,v 1.4 2013/06/13 20:47:37 vincentb1 Exp $
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

// Installation:


/// Code:
package jpicedt.util.math;
import jpicedt.Log;
import java.util.*;

/**
 * Une union d'intervalles Interval de nombre r�els (double) sur laquelle on
 * peut faire des op�rations ensemblistes.<br>
 * Il est notable que les bornes sont toujours incluses (elle restent coll�es
 * � un intervalle quel que soit l'op�ration effectu�e).
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela�che</a>
 * @since jPicEdt 1.6
 */
public class IntervalUnion implements Iterable<Interval>
{
	LinkedList<Interval> intervals;

	/** Construit une union d'interalles �gale � x. Chaque �l�ment de x est clon�.
	 * @since jPicEdt 1.6
	 */
	public IntervalUnion(IntervalUnion x){
		intervals = new LinkedList<Interval>();
		for(Interval i : x.intervals)
			intervals.addLast(new Interval(i));
	}

	public Iterator<Interval> iterator(){ return intervals.iterator(); }

	/**
	 *@return nombre d'intervalles dans this.
	 * @since jPicEdt 1.6
	 */
	public int componentCount(){ return intervals.size(); }

	/** construit une union d'intervalles vide
	 * @since jPicEdt 1.6
	 */
	public IntervalUnion(){
		intervals = new LinkedList<Interval>();
	}

	/**
	 * @return true si l'union d'intervalles this est vide, false sinon.
	 * @since jPicEdt 1.6
	 */
	public boolean isEmpty(){
		return componentCount() == 0;
	}

	/**
	 * @return l'instance du i-�me intervalle dans l'union (sans cloneage)
	 * @since jPicEdt 1.6
	 */
	public Interval get(int i){
		return intervals.get(i);
	}
	/** construit l'union d'interval en supposant que chaque couple (x[2*i]
	 * x[2*i+1]) d�limite un intervalle, et que ces intervalles sont disjoints
	 * et ordonn�s dans l'ordre croissant
	 * @since jPicEdt 1.6
	 */
	public IntervalUnion(double[] x){
		int length = x == null ? 0 : x.length;
		intervals = new LinkedList<Interval>();

		for(int i = 0; i < length; i += 2)
		{
			Interval e = new Interval(x[i], x[i+1]);
			intervals.addLast(e);
		}
	}

	/** construit une union d' Interval contenant uniquement x.
	 *	!!!Attention!!!, x n'est pas clon�.
	 *	@param x l'unique intervalle dans l'union � sa construction.
	 * @since jPicEdt 1.6
	 */
	public IntervalUnion(Interval x){
		intervals = new LinkedList<Interval>();
		intervals.addLast(x);
	}


	/**
	 * r�unit this aux intervalles dans l'argument x.
	 *@return true si this est chang�*/
	public boolean union/*2*/(IntervalUnion x){
		boolean ret = false;
		for(Iterator<Interval> i = x.intervals.iterator(); i.hasNext(); )
		{
			Interval e = i.next();
			ret = union(e) || ret ;
		}
		return ret;
	}
	/**
	 * Unit les intervalle dans this aux intervalle dans l'argument x
	 *@param x  tableau tel que [ x[2*i], x[2*i+1] ] est un intervalle.
	 *@return true si l'union d'intervalles est chang�e
	 * @since jPicEdt 1.6
	 */
	public boolean union(double[] x){
		boolean ret = false;
		int length = x == null ? 0 : x.length;
		for(int i = 0; i < length; i += 2)
		{
			Interval e  = new Interval(x[i], x[i+1]);
			ret = union(e) || ret;
		}
		return ret;
	}

	/**
	 * Unit l'intervalle x � this.
	 *@param x intervalle � unir � this.
	 *@return true si l'union d'intervalles est chang�e
	 * @since jPicEdt 1.6
	 */
	public boolean union(Interval x){
		boolean ret = false;
		Interval e = null;
		boolean inserted = false;
		int ind = 0;
		// copie de s�curit�
		x = new Interval(x);
		Iterator<Interval> i= intervals.iterator();
		while(i.hasNext())
		{
			e = i.next();
			ind ++;
			if(e.max >= x.min)
			{
				if(x.max < e.min)
				{
					intervals.add(ind-1,x);
					e  = x;
					ret = true;
				}
				else if(e.min > x.min)
				{
					e.min = x.min;
					if(x.max > e.max)
					{
						e.max = x.max;
					}
					ret = true;
				}
				else if(x.max >= e.max)
				{
					e.max = x.max;
					ret = true;
				}
				// else  x est inclus dans e => rien � faire
				inserted = true;
				break;
			}
		}
		if(inserted)
		{
			// fusionne les intervalles suivants si n�cessaire
			while(ind < intervals.size())
			{
				Interval f = intervals.get(ind);
				if ( f.min <= e.max)
				{
					ret = true;
					intervals.remove(ind);
					if(f.max > e.max)
					{
						e.max = f.max;
						break;
					}
				}
				else
					break;
			}
		}
		else
		{
			// pas trouv� d'�l�ment e tel que e.max >= x.min
			intervals.addLast(x);
			ret = true;
		}
		return ret;
	}
	/**
	 * retranche au sens ensemble l'intervalle x de this.
	 * @return true si l'union d'intervalle est chang�e.
	 * @since jPicEdt 1.6
	 */
	public boolean minus(Interval x){

		boolean ret = false;
		int i;
		for(i = 0; i < intervals.size(); ++i)
		{
			Interval e = intervals.get(i);
			if(x.max >= e.min && x.min <= e.max)
			{
				// l'intersection des intervalles x et e est non vide
				if(x.min <= e.min && x.max >= e.max)
				{
					// l'intervalle e est contenu dans l'intervalle x
					intervals.remove(i);
					ret = true;
				}
				else if(x.min > e.min && x.max < e.max)
				{
					// l'intervalle x est strictement contenu dans
					// l'intervalle e.
					double e1 = e.max;
					e.max = x.min;
					x.min = x.max;
					x.max = e1;
					intervals.add(i+1,x);
					ret = true;
					break;
				}
				else if(x.max >= e.max)
				{
					e.max = x.min;
					ret = true;
				}
				else
				{
					e.min = x.max;
					ret = true;
					break;
				}
			}
			else if(e.min > x.max)
				break;
		}
		return ret;
	}

	/** Diff�rence ensembliste. Retranche de this, au sens ensembliste, chaque
	 * intervalle dans x. x est consid�r� comme une union intervalles au sens
	 * du constructeur IntervalUnion(double[]).
	 * @since jPicEdt 1.6
	 */
	public boolean minus(double[] x){
		boolean ret = false;
		int length = x == null ? 0 : x.length;
		for(int i = 0; i < length; i += 2)
		{
			Interval e = new Interval(x[i],x[i+1]);
			ret = minus(e) || ret;
		}
		return ret;
	}

	/** suppose que diviser &gt; 0 et que les intervalles de l'union
	 *	on des bornes dans [0 , diviser ]
	 *	minimise le nombre d'intervalle lorsque le min et le max se touchent
	 *	en fusionant le dernier intervalle au premier.
	 *	@return true si l'union d'intervalles this est chang�e
	 * @since jPicEdt 1.6
	 */
	public boolean moduloJoin(double diviser){
		boolean ret = false;
		if(intervals.size() >= 2)
		{
			Interval f = intervals.getFirst();
			Interval l = intervals.getLast();
			if(l.max == diviser && f.min == 0)
			{
				intervals.removeLast();
				f.min = l.min - l.max;
				ret = true;
			}
		}
		return ret;
	}

	public enum CopyIntoAllocPolicy
	{
		FORCE_ALLOC,
		REALLOC_IF_DIFFERENT_SIZE,
		REALLOC_IF_SMALLER_SIZE
	};
	/** copie l'union d'intervalles this vers un tableau (o� le tableau
	 * double[] repr�sente l'union d'intervalles au sens du construction
	 * IntervalUnion(double[])
	 * @param e tableau qu'on re-utilise selon la valeur de allocPolicy
	 * @param allocPolicy d�finit si le tableau renvoy� est reallou� ou si on
	 * essaie de re-utiliser e.
	 * @return la copie de l'union d'intervalle
	 */
	public double[] copyInto(double[] e,CopyIntoAllocPolicy allocPolicy){
		int size = intervals.size() * 2;
		if(e == null
		   || e.length < size
		   || allocPolicy == CopyIntoAllocPolicy.FORCE_ALLOC
		   || (e.length != size && allocPolicy == CopyIntoAllocPolicy.REALLOC_IF_DIFFERENT_SIZE))
			e = new double[size];
		int i = 0;
		for(Interval f : intervals)
		{
			e[i++] = f.min;
			e[i++] = f.max;
		}
		return e;
	}
	/**
	 * Intersecte this avec les intervalles dans l'argument x
	 * @since jPicEdt 1.6
	 */
	public boolean inter(IntervalUnion x){
		boolean ret =  false;
		Iterator<Interval> it = x.intervals.iterator();
		if(it.hasNext())
		{
			// au moins 1 intervalles dans x
			Interval e = it.next();
			if(it.hasNext())
			{
				// au moins 2 intervalles dans x
				IntervalUnion original = null;

				for(;;)
				{
					if(original == null)
					{
						// premi�re intersection �l�mentaire
						original = new IntervalUnion(this);
						ret = inter(e) || ret;
					}
					else
					{
						e = it.next();
						if(it.hasNext())
						{
							IntervalUnion partialIntersection =
								new IntervalUnion(original);
							ret = partialIntersection.inter(e) || ret;
							union(partialIntersection);
						}
						else
						{
							// deni�re intersection �l�mentaire
							ret = original.inter(e) || ret;
							union(original);
							break;
						}
					}
				}// for(;;)
			}
			else
				// 1 seul intervalle dans x
				ret = inter(e) || ret;

		}
		else
		{
			// z�ro intervalle dans x
			ret = (intervals.size() != 0);
			if(ret)
				intervals = new LinkedList<Interval>();
		}
		return ret;
	}

	/**
	 * @since jPicEdt 1.6
	 */
	public boolean inter(Interval x){
		boolean ret =  false;

		for(int i = 0; i < intervals.size();)
		{
			Interval e = intervals.get(i);
			if(x.min <= e.max && e.min <= x.max)
			{

				// intersection x ^ e non vide
				if(x.max < e.max)
				{
					e.max = x.max;
					ret = true;
				}
				if(x.min > e.min)
				{
					e.min = x.min;
					ret = true;
				}
				if(e.min > e.max)
				{
					ret = true;
					intervals.remove(i);
				}
				else
					++i;
			}
			else
			{
				// intersection vide
				ret = true;
				intervals.remove(i);
			}
		}
		return ret;
	}


	/**
	 * Intersecte this avec les intervalles dans l'argument x
	 * @since jPicEdt 1.6
	 */
	public boolean inter(double[] x){
		boolean ret;

		int length = x == null ? 0 : x.length;

		if((length &1) != 0)
			Log.error("inter: argument invalide");

		if(length == 0)
		{
			// z�ro intervalle dans x
			ret = intervals.size() != 0;
			intervals = new LinkedList<Interval>();
		}
		else
		{
			// au moins 1 intervalle dans x
			ret = false;
			IntervalUnion original = null;
			for(int i = 0; ; )
			{
				Interval e = new Interval( x[i], x[i+1]);
				if(i == 0)
				{
					i+=2;
					if(length > 2)
					{
						original = new IntervalUnion(this);
						ret = inter(e) || ret;;
					}
					else
					{
						ret = inter(e) || ret;
						break;
					}
				}
				else
				{
					i += 2;
					if(i == length)
					{
						// derni�re
						ret = original.inter(e) || ret;
						union(original);
						break;
					}
					else
					{
						IntervalUnion partialIntersection = new IntervalUnion(original);
						ret = partialIntersection.inter(e) || ret;
						union(partialIntersection);
					}
				}
			}
		}
		return ret;
	}

	/**
	 * Compare � un intervalle
	 * @return true si this ne comprend qu'un seul interval �gal � x.
	 * @since jPicEdt 1.6
	 */
	public boolean equals(Interval x){
		boolean ret = true;
		if(intervals.size() != 1)
		{
			ret = false;
		}
		else
		{
			ret  = intervals.get(0).equals(x);
		}
		return ret;
	}

	/**
	 * Convertit this en cha�ne de caract�re, pour d�bogage uniquement
	 * @since jPicEdt 1.6
	 */
	public String toString(){
		String ret;
		if(intervals.size() == 0)
			ret = new String("empty");
		else
		{
			ret = new String("");
			for(int i = 0; i < intervals.size(); ++i)
			{
				if(i != 0)
					ret = ret + "V";
				Interval e = intervals.get(i);
				ret = ret  + e.toString();
			}
		}
		return ret;

	}
};




/// IntervalUnion.java ends here
