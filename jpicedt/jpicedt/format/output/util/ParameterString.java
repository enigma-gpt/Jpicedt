// ParameterString.java --- -*- coding: iso-8859-1 -*-
// Copyright 2009/2011 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: ParameterString.java,v 1.5 2013/06/13 20:47:07 vincentb1 Exp $
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
package jpicedt.format.output.util;
import java.util.ArrayList;
import java.awt.Color;


/**
 * Une classe qui repr�sente une liste de param�tres donn�s sous la forme
 * <code>clef=valeur</code>, pour des param�tres PSTricks, ou TikZ. Cette
 * classe est utile lorsque des couleurs sont d�finies par l'utilisateur. Le
 * probl�me c'est que lorsque une nouvelle couleur est d�finie, ceci doit �tre
 * fait par une commande (par exemple <code>\newrgbcolor</code> en PSSTricks
 * <strong>avant</strong> la que commande encodant l'Element utilisant cette
 * couleur apparaisse dans le fichier. C'est pourquoi il est n�cessaire de
 * retourner un objet encapsualnt plusieurs cha�ne de caract�re, non seulement
 * la cha�ne de caract�re donnant la liste des param�tres, mais �galement une
 * liste des couleurs d�finies par l'utilisateur.
 * @since jPicEdt 1.6
 */
public class ParameterString {

	/* formated parameter string */
	StringBuffer paramBuf;

	/**
	 * Couleur d�finie par l'utilisateur, � encoder
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

	/** Liste des couleurs d�finies par l'utilisateur. Si null, aucune couleur
	 * n'est d�finie par l'utilsateur. Il est de la responsabilit� de
	 * l'appelant d'encoder cette liste en une commande, et de ins�rer la
	 * commande � l'endroit appropri�.
	 * @since jPicEdt 1.6
	 */
	UserDefinedColourList userDefinedColourList;

	/**
	 * Met � jour la liste de couleurs utilisateur, en l'allouant si n�cessaire.
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
	 * @return une liste contenant les couleurs d�finie par l'utilisateur.
	 */
	public UserDefinedColourList getUserDefinedColourList(){
		if (isDefinedColourList())
			return userDefinedColourList;
		else
			return new UserDefinedColourList(); // vide !
	}
} // inner class



/// ParameterString.java ends here
