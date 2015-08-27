// CtrlPtSubsetGroup.java ---

// Copyright 2011 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: CtrlPtSubsetGroup.java,v 1.4 2013/03/27 07:03:09 vincentb1 Exp $
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
package jpicedt.graphic.model;

import  java.util.ArrayList;
import  java.util.Iterator;
import  java.util.List;
import  java.util.ListIterator;
import  java.util.Collection;

public class CtrlPtSubsetGroup implements CtrlPtSubset, java.util.List<CtrlPtSubset> {

	/** the array that contains children */
	protected ArrayList<CtrlPtSubset> children;

	// ######################################################################################################
	// Implantation des constructeurs.
	// ######################################################################################################
	public CtrlPtSubsetGroup(){
		children = new ArrayList<CtrlPtSubset>();
	}

	public CtrlPtSubsetGroup(int size){
		children = new ArrayList<CtrlPtSubset>(size);
	}

	// ######################################################################################################
	// Implantation de l'interface CtrlPtSubset.
	// ######################################################################################################
	public void translate(double dx, double dy){
		for(CtrlPtSubset cps : children){
			cps.translate(dx, dy);
		}
	}

	// ######################################################################################################
	// Implantation de l'interface java.util.List<CtrlPtSubset>.
	// ######################################################################################################
	public boolean add(CtrlPtSubset child){
		children.add(child);
		return true;
	}

	public CtrlPtSubset remove(int index){
		return children.remove(index);
	}

	public void add(int index,CtrlPtSubset cps){
		children.add(index,cps);
	}

	public CtrlPtSubset set(int index,CtrlPtSubset cps){
		return children.set(index,cps);
	}

	public CtrlPtSubset get(int index){
		return children.get(index);
	}

	public void clear(){
		children.clear();
	}

	public boolean removeAll(Collection<?> c){
		return children.removeAll(c);
	}

	public boolean addAll(Collection<? extends CtrlPtSubset> c){
		return children.addAll(c);
	}

	public boolean addAll(int index,Collection<? extends CtrlPtSubset> c){
		return children.addAll(index, c);
	}

	public boolean remove(Object o){
		return children.remove(o);
	}

	/**
	 * @return le nombre d'�l�ments dans le <code>CtrlPtSubsetGroup</code> <code>this</code>. Seuls les
	 * descendants directs list�s dans <code>children</code> sont compt�s.
	 */
	public int size(){
		return children.size();
	}

	/**
	 * @return <tt>true</tt> si le <code>CtrlPtSubsetGroup this</code> ne contient aucun
	 * �l�ments.
	 */
	public boolean isEmpty(){
		return children.isEmpty();
	}

	/**
	 * @param o L'objet dont on veut tester l'appartenance au <code>CtrlPtSubsetGroup this</code>
	 * @return <code>true</code> si le <code>CtrlPtSubset</code> pass� comme <code>o</code> est contenu dans
	 * le <code>CtrlPtSubsetGroup</code> <code>this</code>.
	 */
	public boolean contains(Object o){
		if (o==null)
			return false;
		if (children.contains(o))
			return true;
		if (!(o instanceof CtrlPtSubset))
			return false;
		CtrlPtSubset e = (CtrlPtSubset)o;
//		if (e.getParent()!=null && e.getParent()!=this) // [pending] need testing
//			return contains(e.getParent()); // reentrant
		return false;
	}

	/**
	 * @return un it�terateur sur les �l�ments du <code>CtrlPtSubsetGroup this</code>.
	 */
	public Iterator<CtrlPtSubset> iterator(){
		return children.iterator();
	}

	public java.util.List<CtrlPtSubset> subList(int fromIndex, int toIndex){
		return children.subList(fromIndex, toIndex);
	}

	public ListIterator<CtrlPtSubset> listIterator(){
		return children.listIterator();
	}

	public ListIterator<CtrlPtSubset> listIterator(int index){
		return children.listIterator(index);
	}

	/**
	 * Renvoie l'indexe de l'objet <code>o</code> parmi les �l�ments du <code>CtrlPtSubsetGroup this</code>.
	 * @param o l'object dont on cherche l'indexe.
	 * @return -1 si non trouv�
	 */
	public int lastIndexOf(Object o){
		return children.lastIndexOf(o);
	}

	/**
	 * Renvoie l'indexe de l'objet <code>o</code> parmi les �l�ments du <code>CtrlPtSubsetGroup this</code>.
	 * @param child l'object dont on cherche l'indexe.
	 * @return -1 si non trouv�
	 */
	public int indexOf(Object child){
		return children.indexOf(child);
	}

	/**
	 * @return un <code>Object[]</code> contenant tous les �l�ments de cette collection.
	 */
	public Object[] toArray(){
		return children.toArray();
	}

	/**
	 * @param a tableau dont le r�le est de sp�cifier le type <code>T</code>.
	 * @return un <code>Object[]</code> contenant tous les �l�ments de cette collection dont le type d�termin�
	 * durant l'ex�cution est le type <code>T</code> sp�cifi� des �l�ments de <code>a</code>.
	 */
	public <T> T[] toArray(T a[]){
		return children.toArray(a);
	}

	/**
	 * @return <code>true</code> si le <code>CtrlPtSubsetGroup this</code> contient tous les �l�ments de la
	 * collection <code>c</code> pass�e en argument.
	 */
	public boolean containsAll(Collection<?> c){
		Iterator<?> e = c.iterator();
		while (e.hasNext())
			if(!contains(e.next()))
				return false;
		return true;
	}

	public boolean equals(Object o){
		if (o instanceof CtrlPtSubsetGroup){
			return this.children.equals(((CtrlPtSubsetGroup)o).children);
		}
		return false;
	}

	/**
	 * Ne retient que les �l�ments de <code>CtrlPtSubsetGroup this</code> contenus dans la collection
	 * <code>c</code> pass�e en argument.
	 * @param c Collection des �l�ments � retenir.
	 * @return <code>true</code> si le <code>CtrlPtSubsetGroup this</code> a chang� suite � l'appel de cette
	 * m�thode.
	 */
	public boolean retainAll(Collection<?> c){
		boolean modified = false;
		Iterator<CtrlPtSubset> it = iterator();
		while (it.hasNext()) {
			if(!c.contains(it.next())) {
				it.remove(); // call remove, which in turns call removeChild and fire changed event.
				modified = true;
			}
		}
		return modified;
	}


}

/// CtrlPtSubsetGroup.java ends here
