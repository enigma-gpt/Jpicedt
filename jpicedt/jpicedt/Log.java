// Log.java --- -*- coding: iso-8859-1 -*-
// February 21, 2002 - jPicEdt 1.3.3, a picture editor for LaTeX.
// Copyright (C) 1999/2006 Sylvain Reynal
// Copyright (C) 2007/2013 Sylvain Reynal, Vincent Bela�che
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
// Version: $Id: Log.java,v 1.29 2013/06/13 20:47:52 vincentb1 Exp $
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
package jpicedt;

/**
 * Utility class for printing out debugging messages.
 * @author Sylvain Reynal
 * @since jpicedt 1.3
 * @version $Id: Log.java,v 1.29 2013/06/13 20:47:52 vincentb1 Exp $
 *
 */
public class Log {

	//public static final boolean DEBUG = true;
	public static final boolean DEBUG = false;

	/** Renvoie un vidage hexadecimal de <code>s</code>. Par exemple si <code>s</code> vaut
	 * <code>"A\n0"</code> alors la valeur renvoy�e est <code>"41 10 30"</code>. */
	public static String hexDump(String s){
		if(s.length() == 0)
			return "";
		StringBuffer ret = new StringBuffer(3*s.length());
		ret.append(String.format("%02X",(int)s.charAt(0)));
		for(int i=1; i < s.length(); ++i){
			ret.append(String.format(" %02X",(int)s.charAt(i)));
		}
		return ret.toString();
	}


	/**
	 * Usage : if (Log.DEBUG) Log.debug();
	 * <br>
	 * This will automatically print the calling class and method name using new JDK1.4 features.
	 */
	public static void debug(){
		Throwable t = new Throwable();
		StackTraceElement elements[] = t.getStackTrace(); // elements[0]=Log.debug
		String clazz = elements[1].getClassName(); // and elements[1]=caller.method !
		if (!enableClass(clazz)) return;
		String method = elements[1].getMethodName();
		System.out.print("--------------------------------------------------------\n* ");
		System.out.print(clazz);
		System.out.print(".");
		System.out.print(method);
		System.out.print("()\n");
	}

	/**
	 * Usage : if (Log.DEBUG) Log.debug("message");
	 * <br>
	 * This will automatically print the calling class and method name using new JDK1.4 features.
	 */
	public static void debug(String msg){
		Throwable t = new Throwable();
		StackTraceElement elements[] = t.getStackTrace(); // elements[0]=Log.debug
		String clazz = elements[1].getClassName(); // and elements[1]=caller.method !
		if (!enableClass(clazz)) return;
		String method = elements[1].getMethodName();
		System.out.print("--------------------------------------------------------\n* ");
		System.out.print(clazz);
		System.out.print(".");
		System.out.print(method);
		System.out.print("()\n\t");
		System.out.print(msg);
		System.out.print("\n");
	}

	/**
	 * Usage : if (Log.DEBUG) Log.debugAppendLn("bla bla");
	 */
	public static void debugAppendLn(String msg){
		Throwable t = new Throwable();
		StackTraceElement elements[] = t.getStackTrace();
		String clazz = elements[1].getClassName();
		if (!enableClass(clazz)) return;
		System.out.print("\n\t");
		System.out.print(msg);
		System.out.print("\n");
	}
	/**
	 * use it like this : if (Log.DEBUG) Log.debugAppend(this,"bla bla"); *
	 */
	public static void debugAppend(String msg){
		Throwable t = new Throwable();
		StackTraceElement elements[] = t.getStackTrace();
		String clazz = elements[1].getClassName();
		if (!enableClass(clazz)) return;
		System.out.print("\n\t");
		System.out.print(msg);
	}

	public static void error(String msg){
		Throwable t = new Throwable();
		StackTraceElement elements[] = t.getStackTrace();
		String clazz = elements[1].getClassName();
		String method = elements[1].getMethodName();
		System.out.print("[Error] ");
		System.out.print(clazz);
		System.out.print(".");
		System.out.print(method);
		System.out.print("()\n");
		System.out.print("\t");
		System.out.print(msg);
		System.out.print("\n");
	}

	public static void warning(String msg){
		Throwable t = new Throwable();
		StackTraceElement elements[] = t.getStackTrace();
		String clazz = elements[1].getClassName();
		String method = elements[1].getMethodName();
		System.out.print("[Warning] ");
		System.out.print(clazz);
		System.out.print(".");
		System.out.print(method);
		System.out.print("()\n\t");
		System.out.print(msg);
		System.out.print("\n");
	}

	private static boolean enableClass(String caller){
		//if (caller.startsWith("jpicedt.graphic.view")) return true;
		//if (caller.startsWith("jpicedt.graphic.io")) return true;
		//if (caller.startsWith("jpicedt.graphic.PECanvas")) return true;
		//if (caller.startsWith("jpicedt.graphic.grid")) return true;
		//if (caller.startsWith("jpicedt.graphic.toolkit.EditElementMouseTransformFactory")) return true;
		//if (caller.startsWith("jpicedt.ui.internal")) return true;
		//if (caller.startsWith("jpicedt.graphic.view.CompositeView")) return true;
		//if (caller.startsWith("jpicedt.graphic.view.HitInfo")) return true;
		//if (caller.startsWith("jpicedt.graphic.toolkit.SelectionTool")) return true;
		//if (caller.startsWith("jpicedt.widget")) return true;
		//return false;
		return true;
	}
}
