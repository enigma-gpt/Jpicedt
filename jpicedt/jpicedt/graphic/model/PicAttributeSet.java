// PicAttributeSet.java --- -*- coding: iso-8859-1 -*-
// March 30, 2002 - jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: PicAttributeSet.java,v 1.21 2013/09/10 05:08:54 vincentb1 Exp $
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

import java.awt.*;
import java.util.*;

import static jpicedt.graphic.model.PicAttributeName.*;

/**
 * a class that stores non-default graphical attributes (e.g. "fill", "stroke", etc... ) in
 * a hashtable. If a key is not found in the map, we look up the resolving parent.
 * @see PicAttributeName
 * @see StyleConstants
 */
public class PicAttributeSet {

	/** an immutable attribute set that serves as the resolving parent if no resolving parent
	    is explicitly set */
	public static final DefaultAttributeSet DEFAULT_SET = new DefaultAttributeSet();

	/** a map holding name/value pairs */
	protected HashMap<PicAttributeName, Object> map;
	/** if non-null, used as the resolving parent when name is not found in current set */
	protected PicAttributeSet parent;

	/**
	 * Create a new PicAttributeSet with DEFAULT_SET as the resolving parent.
	 * @see jpicedt.graphic.model.StyleConstants
	 */
	public PicAttributeSet(){
		map = new HashMap<PicAttributeName, Object>();
		parent = DEFAULT_SET;
	}

	/**
	 * cloning constructor
	 * @param src the attribute set to be cloned ; set the resolving parent to the resolving parent
	 *        of the source.
	 */
	public PicAttributeSet(PicAttributeSet src){
		this();
		for (PicAttributeName name: src.map.keySet()){
			_setAttributeUnchecked(name, src._getAttributeUnchecked(name));
		}
		setResolveParent(src.getResolveParent());
	}

	/**
	 * Returns the resolving parent, or DEFAULT_SET if it's null.
	 */
	public PicAttributeSet getResolveParent(){
		if (this.parent == null) return DEFAULT_SET;
		else return this.parent;
	}

	/**
	 * set the resolving parent.
	 * @param parent if null, set the parent to DEFAULT_SET.
	 */
	public void setResolveParent(PicAttributeSet parent){
		if (parent==null) this.parent=DEFAULT_SET;
		else this.parent = parent;
		removeRedundantAttributes(); // remove name/value pairs defined in the new resolving parent
	}

	/**
	 * return a copy of this PicAttributeSet with the same resolving parent as the source,
	 * and which is guaranteed not to change over time.
	 */
	public PicAttributeSet copyAttributes() {
		return new PicAttributeSet(this);
	}

	/**
	 * Returns the attribute with the given name, possibly resolving to parent if "name" is not
	 * defined in this set.
	 */
	private Object _getAttributeUnchecked(PicAttributeName name){
		if (map.containsKey(name)) return map.get(name);
		else return getResolveParent()._getAttributeUnchecked(name);
	}

	/**
	 * Returns the attribute with the given name, possibly resolving to parent if "name" is not
	 * defined in this set.
	 */
	 public <T> T getAttribute(PicAttributeName<T> name){
		return (T)_getAttributeUnchecked(name); // unchecked cast, i know that...
	}

	/**
	 * Remove name/value pair already defined in the resolving parent.
	 */
	private void removeRedundantAttributes(){
		Iterator<PicAttributeName> it = getAttributeNames();
		while(it.hasNext()){
			PicAttributeName name = it.next();
			if (parent._getAttributeUnchecked(name)!=null){
				if (parent._getAttributeUnchecked(name).equals(map.get(name)))
					map.remove(name);
			}
		}
	}

	/**
	 * Add a new name/value pair to the set if it's present nor in the set, nor in the resolving parent,
	 * otherwise modify the value for the given name. If the pair then exists in the resolving parent,
	 * remove it from this set.
	 * @param name attribute name
	 * @param value attribute value
	 * @since jpicedt 1.5
	 */
	private void _setAttributeUnchecked(PicAttributeName name, Object value){
		// check that value.getClass() is assignable from name's allowedValueClass, i.e. value has the expected runtime type :
		//if (!name.getAllowedValueClass().isInstance(value))
		//	throw new IllegalArgumentException("Illegal value class: "+value.getClass()+ " when "+name.getAllowedValueClass()+" was expected !");

		//debug(name+"="+value);
		// check if name/value exist in resolving parent :
		Object resolveValue = getResolveParent()._getAttributeUnchecked(name);
		if (resolveValue != null && resolveValue.equals(value)){
			// remove from hashmap, (hence we'll resolve to parent on next call to getAttribute):
			//debug("map.remove("+name+")");
			map.remove(name);
		}
		// else check if name exist in this set :
		else if (map.containsKey(name)) {
			// if name/value is not already defined here, replace old value with new one :
			if (!map.get(name).equals(value)) {
				map.put(name,value);
				//debug("map.put("+name+")");
			}
			// otherwise leave unchanged
		}
		// otherwise add new name/value pair :
		else {
			map.put(name,value);
			//debug("map.put("+name+")");
		}
	}

	/**
	 * Add a new name/value pair to the set if it's present nor in the set, nor in the resolving parent,
	 * otherwise modify the value for the given name. If the pair then exists in the resolving parent,
	 * remove it from this set.
	 * @param name attribute name
	 * @param value attribute value
	 */
	public <T> void setAttribute(PicAttributeName<T> name, T value){
		_setAttributeUnchecked(name,value);
	}


	/**
	 * Sets several name/value pairs at a time from the given set. Key present in this set, yet which
	 * are not found in the given set, aren't overriden. Hence only non-default value are copied (in the
	 * sense that the resolving parent is the "default" set).
	 */
	public void setAttributes(PicAttributeSet src){
		Iterator<PicAttributeName> it = src.getAttributeNames();
		while(it.hasNext()){
			PicAttributeName name = it.next();
			Object value = src._getAttributeUnchecked(name);
			_setAttributeUnchecked(name, value);
		}
	}

	/**
	 * Returns the number of attributes contained in this set ; this doesn't include the attributes
	 *         of the resolving parent.
	 */
	public int getAttributeCount(){
		return map.size();
	}

	/**
	 * Returns an iterator over the names of the attributes in this attribute set ; this
	 *         doesn't iterate over the keys of the resolving parent.
	 */
	public Iterator<PicAttributeName> getAttributeNames(){
		return map.keySet().iterator();
	}

	/**
	 * Returns a text representation of this attribute set, for debugging purpose
	 */
	public String toString() {

		StringBuffer buf = new StringBuffer(100);
		buf.append("attrib_set={");
		// sort keys in ascending order :
		Comparator<PicAttributeName> c = new Comparator<PicAttributeName>(){
			public int compare(PicAttributeName o1, PicAttributeName o2){
				return o1.toString().compareTo(o2.toString());
			}
		};
		TreeSet<PicAttributeName> sortedKeys = new TreeSet<PicAttributeName>(c);
		sortedKeys.addAll(map.keySet());
		Iterator<PicAttributeName> it = sortedKeys.iterator();
		while(it.hasNext()){
			PicAttributeName name = it.next();
			buf.append("*");
			buf.append(name.toString());
			buf.append("=");
			buf.append(map.get(name));
		}
		buf.append("}");
		//buf.append("\n\tresolving_parent_set=");
		//buf.append(getResolveParent().toString());
		//buf.append(getResolveParent());
		return buf.toString();
	}

} // class
