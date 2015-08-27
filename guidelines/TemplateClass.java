// TemplateClass.java --- -*- coding: iso-8859-1 -*-
// January 1, 2000 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2006 Sylvain Reynal
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
// Version: $Id: TemplateClass.java,v 1.7 2013/06/13 20:47:57 vincentb1 Exp $
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
package xxx;

import zzz.yyy;

/**
 * This is a <b>template</b> file to be used as a digest of what we &mdash; amidst the jPicEdt team &mdash;
 * reckon is <i>good documenting practice</i> ;-)<br>
 * Feel free to improve/enrich if you feel like doing it.
 * <p>
 * A handful of guide-lines:
 * <ul>
 * <li> Id and Log tags are for use by CVS log tracking. Please leave'em here.
 * <li> Feel free to expound your comments at length: it never turns out to be enough for readers to get the
 * finer points anyway&hellip;
 * <li> Write existing documentation out neatly if necessary.
 * <li> Write it out in plain english.
 * <li> Pick new (package|class|method|field) names with care ; let them be self-explanatory enough.
 * <li> Please follow Java programming standards (browse through the Java API documentation and take a look round if necessary ;
 *      consider how THEY dit it at Sun Microsystems, it's usually a good starting point) ;
 *      in particular, <code>getField()</code>, <code>setField()</code> and
 *      <code>isProperty()</code> are standard ways of naming getters and setters.
 * <li> Refactor names as often as you feel like it, in particular in the event class splinter or grow up by
 * getting aggregated with other classes..
 * <li> Every now and then, rebuild the API documentation (<code>ant javadocs</code>) to check how it
 * looks. Might be amazing at times, i tell you ;-)
 * <li> Jump to next task only if you are totally satisfied with the way you documented your code.
 * </ul>
 * @since jPicEdt 1.4
 * @author My name
 * @version $Id: TemplateClass.java,v 1.7 2013/06/13 20:47:57 vincentb1 Exp $
 * <p>
 * $Log: TemplateClass.java,v $
 * Revision 1.7  2013/06/13 20:47:57  vincentb1
 * <2013-06-09> problème d'encodage javadocs.
 *
 * Revision 1.6  2013/03/27 07:19:09  vincentb1
 * &lt;2013-03-27&gt; Re-mise en forme en-t�te.
 *
 * Revision 1.5  2011/11/17 20:29:09  vincentb1
 * &lt;2011-11-16&gt; Mise � jour banni�re &amp; suppression blancs en fin de ligne.
 *
 * Revision 1.4  2011/07/23 05:12:31  vincentb1
 * &lt;2011-07-23&gt; Changement de la bani�re de licence en CeCiLL.
 *
 * Revision 1.3  2006/06/17 22:39:26  reynal
 * *** empty log message ***
 *
 * Revision 1.2  2003/09/17 15:20:13  reynal
 * *** empty log message ***
 *
 * Revision 1.1  2003/08/31 12:48:18  reynal
 *
 * Added a template source file which provides useful guidelines for documenting jPicEdt source code.
 *
 */
abstract public class Template extends SuperClass implements AnInterface {

	/////////////////////////////////////
	/// PUBLIC FIELDS
	////////////////////////////////////

	/**
	 * Constant field for aMethod() method. (either add documentation here, or in aMethod() documentation)
	 */
	public static final String FIRST_CONSTANT_FIELD = "field1";

	/////////////////////////////////////
	/// PROTECTED FIELDS
	////////////////////////////////////

	/**
	 * This variable serves as ...
	 */
	// i gave this variable a protected access because bla bla
	protected int aProtectedVar;

	/////////////////////////////////////
	/// PRIVATE FIELDS
	////////////////////////////////////

	// anObject is being used by method xxx() for this and that purpose ; i gave it a private access because ...
	private AClass anObject = new AClass();

	/////////////////////////////////////
	//// CONSTRUCTORS
	/////////////////////////////////////

	/**
	 * Creates a new Template object.
	 * [developper_name:pending] feature XXX is still missing.
	 * @param aParameter always add description of parameters, or at least make sure this was properly done in superclass.
	 */
	public Template(Object aParameter){
		...
	}

	/////////////////////////////////////////
	//// GETTERS/SETTERS
	/////////////////////////////////////////

	/**
	 * Returns the value of field X.
	 * @return an integer guaranteed to be positive
	 * @since jpicedt 1.4.1 (add this field only if it differs from content of @since in class preambule)
	 */
	public int getAField(){
		// give details about your algorithm here
		...
	}

	/**
	 * Returns whether this class is dumb or not.<br>
	 * This is called by method {@link #setXYZ setXYZ()} as part of the delegation mechanism for bla bla...
	 * @author another author (add this field only if you're not the main class author)
	 * @see packageX.packageY.AnotherUsefulClass#isFool
	 * @deprecated this method has been deprecated as of release 1.4.1, use {@link #isSmart isSmart()} instead.
	 */
	abstract protected boolean isDumb();

}
