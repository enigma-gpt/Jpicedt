// ParameterString.java --- -*- coding: iso-8859-1 -*-
// Copyright 2009/2011 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: ParameterString.java,v 1.5 2013/06/13 20:47:07 vincentb1 Exp $
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



/// Code:
package jpicedt.format.output.util;
import java.util.ArrayList;
import java.awt.Color;


/**
 * Une classe qui représente une liste de paramètres donnés sous la forme
 * <code>clef=valeur</code>, pour des paramètres PSTricks, ou TikZ. Cette
 * classe est utile lorsque des couleurs sont définies par l'utilisateur. Le
 * problème c'est que lorsque une nouvelle couleur est définie, ceci doit être
 * fait par une commande (par exemple <code>\newrgbcolor</code> en PSSTricks
 * <strong>avant</strong> la que commande encodant l'Element utilisant cette
 * couleur apparaisse dans le fichier. C'est pourquoi il est nécessaire de
 * retourner un objet encapsualnt plusieurs chaîne de caractère, non seulement
 * la chaîne de caractère donnant la liste des paramètres, mais également une
 * liste des couleurs définies par l'utilisateur.
 * @since jPicEdt 1.6
 */
public class ParameterString {

	/* formated parameter string */
	StringBuffer paramBuf;

	/**
	 * Couleur définie par l'utilisateur, à encoder
	 * @since jPicEdt 1.6
	 */
	static public class UserDefinedColour{
		public String name;
		public Color colour;
		public UserDefinedColour(String name,Color colour){
			this.name = name ;
			this.colour = colour;
		}
	}

	static public class UserDefinedColourList extends ArrayList<UserDefinedColour>{
		public UserDefinedColourList(int allocSize){
			super(allocSize);
		}
		public UserDefinedColourList(){}
	}

	/** Liste des couleurs définies par l'utilisateur. Si null, aucune couleur
	 * n'est définie par l'utilsateur. Il est de la responsabilité de
	 * l'appelant d'encoder cette liste en une commande, et de insérer la
	 * commande à l'endroit approprié.
	 * @since jPicEdt 1.6
	 */
	UserDefinedColourList userDefinedColourList;

	/**
	 * Met à jour la liste de couleurs utilisateur, en l'allouant si nécessaire.
	 * @since jPicEdt 1.6
	 */
	public void addUserDefinedColour(String name, Color colour){
		if(userDefinedColourList == null)
			userDefinedColourList  = new UserDefinedColourList(4);
		userDefinedColourList.add(new UserDefinedColour(name,colour));
	}

	/**
	 * @since jPicEdt 1.6
	 */
	public ParameterString(
		StringBuffer paramBuf,
		UserDefinedColourList userDefinedColourList){
		this.paramBuf = paramBuf;
		this.userDefinedColourList = userDefinedColourList;
	}

	/**
	 * @return true if there's at least one user-defined colour that need to be defined
	 */
	public boolean isDefinedColourList(){
		return (userDefinedColourList != null && userDefinedColourList.size() != 0);
	}

	/**
	 * @return the formated parameter string (w/o leading and trailing brackets)
	 */
	public StringBuffer getParameterBuffer(){
		return paramBuf;
	}

	/**
	 * @return une liste contenant les couleurs définie par l'utilisateur.
	 */
	public UserDefinedColourList getUserDefinedColourList(){
		if (isDefinedColourList())
			return userDefinedColourList;
		else
			return new UserDefinedColourList(); // vide !
	}
} // inner class



/// ParameterString.java ends here
