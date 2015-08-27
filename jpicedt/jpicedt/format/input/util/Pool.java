// Pool.java --- -*- coding: iso-8859-1 -*-
// jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2013  Sylvain Reynal
//
// Author: Sylvain Reynal
//         Département de Physique
//         École Nationale Supérieure de l'Électronique et de ses Applications (ÉNSÉA)
//         6, avenue du Ponceau
//         95014 CERGY CEDEX
//         FRANCE
//
//         Tel : +33 130 736 245
//         Fax : +33 130 736 667
//         e-mail : reynal@ensea.fr
//
// Version: $Id: Pool.java,v 1.13 2013/07/26 06:10:46 vincentb1 Exp $
// Keywords: parser
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
package jpicedt.format.input.util;

import jpicedt.graphic.PicPoint;
import jpicedt.graphic.model.*;

import java.awt.Color;
import java.io.*;
import java.util.*;

import static jpicedt.Log.*;

/**
 * Offers a means for expressions belonging to the parser-tree to share variables across the tree.  In
 * addition to storing persistent data in some predefined public fields, this class also acts as a hashtable,
 * and may thus store key/value pairs of objects of any class (these may be <code>PicAttributeSet</code>'s,
 * etc&hellip;), which may be shared across the whole parser-tree.
 * @since jpicedt 1.3
 * @author Sylvain Reynal
 * @version $Id: Pool.java,v 1.13 2013/07/26 06:10:46 vincentb1 Exp $
 */
public class Pool {

	/** either the main drawing, or a subgroup ; each new parsed Element should be added to this group */
	public PicGroup currentGroup;

	/**
	 * A stack used to store the main drawing and its subgroups ; each time a "begin group" is encountered,
	 * the current PicGroup is pushed onto the stack, and a new PicGroup is instanciated, which then
	 * represents the current PicGroup ; the opposite operations are executed in the reverse order when a "end
	 * group" is found. */
	public Stack<PicGroup> picGroupStack;

	/**
	 * Convenience used to share information (e.g. parameters, location, &hellip;) across expressions acting
	 * on the same element. Generally, an InstanciationExpression reinits "currentObj" to an instance of
	 * an Element of the proper type, then ensuing expression modify this element's attributes and/or
	 * geometry.
	 */
	public Element currentObj;

	/** A string that stores commands that couldn't be parsed in the current context (e.g. in the text file to
	  * be parsed) so that we can keep track of these commands (e.g. to be able to include them again when it
	  * comes to saving the text file to disk) */
	public StringBuffer notParsed;

	/**
	 * Key used to fetch the attribute set attached to the current element "currentObj"
	 * @see #get
	 */
	public static final Key<PicAttributeSet> CURRENT_OBJ_ATTRIBUTES = new Key<PicAttributeSet>("current_obj_attributes");


	private HashMap<Key,Object> map = new HashMap<Key,Object>();

	/**
	 * Simply call reinit()
	 */
	public Pool(){
		reinit();
	}

	/**
	 * Reset public fields values.
	 */
	public void reinit(){
		//System.out.println("Reinit' Pool...");
		currentGroup = new PicGroup();
		picGroupStack = new Stack<PicGroup>();
		notParsed = new StringBuffer();
		currentObj=null;
		map = new HashMap<Key,Object>();
	}

	/**
	 * Associates the specified value with the given key in the pool's hashmap
	 * @return old value for the given key, if any.
	 */
	public <T> T put(Key<T> key, T value){
		//debug("key="+key+", value="+value);
		return (T)map.put(key,value); // unchecked cast!
	}

	/**
	 * Return the value associated with the given key
	 */
	public <T> T get(Key<T> key){
		return (T)map.get(key); // unchecked cast!
	}

	/**
	 * Convenience for retrieving a "PicAttributeSet" value ; can be used in conjunction with
	 * static methods defined in jpicedt.graphic.model.StyleConstants to retrieve
	 * attributes values with less burden.
	 * @param key if CURRENT_OBJ_ATTRIBUTES, retrieves the attribute set of the current Element in the pool ;
	 *        any other key is acceptable as long as it corresponds to a valid entry in the Pool's hashtable,
	 *        that is, it has been previously pushed in the Pool using <code>put(key, value)</code>.
	 */
	public PicAttributeSet getAttributeSet(Key<? extends PicAttributeSet> key){
		if (key == CURRENT_OBJ_ATTRIBUTES){
			if (currentObj != null){
				return currentObj.getAttributeSet();
			}
			else return null;
		}
		return get(key); // may raise a ClassCastException if wrong key !!!
	}

	/**
	 * Add a name/value attribute pair to the attribute set with the given key, i.e.
	 * either CURRENT_OBJ_ATTRIBUTES for "currentObj", or any other key refering to
	 * an attribute set previously pushed in the Pool (e.g. PsTricks registers,&hellip;)
	 */
	public <T> void setAttribute(Key<? extends PicAttributeSet> key, PicAttributeName<T> name, T value){
		PicAttributeSet set = getAttributeSet(key);
		if (set != null) set.setAttribute(name,value);
	}

	/**
	 * @return a text description of this Pool for debugging purpose
	 */
	public String toString(){
		String s = "Pool@" + hashCode() + ":\n";
		s += "* currentGroup=" + currentGroup
			 + "\n* picGroupStack=" + picGroupStack
			 + "\n* currentObj=" + currentObj
			 + "\n* notParsed=[" + notParsed + "]\n";
		for(Iterator it = map.keySet().iterator(); it.hasNext();){
			Object k = it.next();
			s += "* " + k + ":" + map.get(k) + "\n";
		}
		return s;
	}

	/**
	 * Enforces use of strong typing for keys being pushed in the map .
	 * The typevariable T represents the value's type attached to this key.
	 */
	public static class Key<T> {
		private String str;
		/** @param str may be chosen with debugging in view (not used otherwise) */
		public Key(String str){
			this.str = str;
		}
		public String toString(){
			return str;
		}
	}

}
