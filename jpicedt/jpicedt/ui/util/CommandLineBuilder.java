// CommandLineBuilder.java --- -*- coding: iso-8859-1 -*-
// Copyright 2011/2012 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: CommandLineBuilder.java,v 1.5 2013/10/07 19:16:12 vincentb1 Exp $
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
package jpicedt.ui.util;

import java.lang.String;
import java.util.StringTokenizer;
import java.io.IOException;
import java.io.File;
import jpicedt.JPicEdt;
import jpicedt.MiscUtilities;
import jpicedt.ui.PEDrawingBoard;

/**
 * La classe <code>CommandLineBuilder</code> sert � former une ligne de
 * commande � partir d'une ligne contenant des macros de la forme
 * <code>{f}</code> ou <code>{p}</code>.
 * <p>
 * Le constructeur sert uniquement � effectuer quelques v�rifications
 * pr�liminaires. Lorsque une macro comme <code>{d}</code> ne peut pas �tre
 * d�velopp�e &mdash; c'est � dire qu'il n'y a pas de dessin courant, alors
 * elle n'est pas d�velopp�e et reste sous la forme <code>{d}</code>.
 * <p>
 * Voici le r�sultat des diff�rentes <code>{<var>clef</var>}</code>, �
 * supposer que que le dessin soit sauvegard� dans un fichier dont le chemin
 * complet est <code>/a/b/c0.d.e</code> et pour lequel le fichier temporaire
 * est <code>/tmp/jpicedt123.tex</code>:
 * <table border="1">
 *   <tr><b><th>clef</th><th>r�sultat</th></th><th>description</th></b></tr>
 *   <tr><code><td>b</td><td>c0.d.e</td></code><td>Nom de base du dessin</td></tr>
 *   <tr><code><td>x</td><td>c0.d</td></code><td>Nom de base du dessin sans extension</td></tr>
 *   <tr><code><td>n</td><td>c</td></code><td>Nom de base du dessin sans aucune extension</td></tr>
 *   <tr><code><td>r</td><td>c0</td></code><td>Radical du nom de base du dessin</td></tr>
 *   <tr><code><td>d</td><td>/a/b</td></code><td>R�pertoire du dessin</td></tr>
 *   <tr><code><td>f</td><td>jpicedt123</td></code><td>Nom de base du fichier temporaire</td></tr>
 *   <tr><code><td>i</td></code><td></td><td>R�pertoire des scripts de lancement de commandes externes</td></tr>
 *   <tr><code><td>j</td></code><td></td><td>R�pertoire d'installation de jPicEdt</td></tr>
 *   <tr><code><td>p</td><td>/tmp</td></code><td>R�pertoire du fichier temporaire</td></tr>
 *   <tr><code><td>u</td></code><td></td><td>R�pertoire des macros &amp; fragments utilisateur</td></tr>
 *   <tr><code><td>[</td><td>{</td></code><td>accolade ouvrante</td></tr>
 *   <tr><code><td>]</td><td>}</td></code><td>accolade fermante</td></tr>
 * </table>
 *
 *
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela�che</a>
 * @since jPicEdt 1.6
 * @version  $Id: CommandLineBuilder.java,v 1.5 2013/10/07 19:16:12 vincentb1 Exp $
 */
public class CommandLineBuilder{
	public CommandLineBuilder(){
		try {
			RunExternalCommand.createTmpFile();
		} // security to avoid "null" !
		catch (IOException ex){}
	}

	/**
	 * Construit la ligne de commande r�elle en repla�ant les occurences des
	 * macros (<code>{f}</code>, <code>{p}</code>,&hellip;) dans l'argument
	 * <code>command</code> par leur valeur en cours.
	 *
	 * @param command ligne de commande avec potentiellement des macros de la
	 * forme <code>{f}</code> ou <code>{p}</code>, ainsi que sp�cifi� dans la
	 * panneau de pr�f�rence utilisateur, sous l'onglet commande.
	 * @return La ligne de commande r�elle
	 * @since PicEdt 1.2
	 */
	public String buildCommandLine(String command){

		StringTokenizer tokenizer = new StringTokenizer(command,"{}",true); // we don't remove spaces ; tokens are returned
		StringBuffer realCommand = new StringBuffer();
		String token;
		String specialSymbolExpansion = null;
		String previousTokens = null;
		while (tokenizer.hasMoreTokens()){
			token = tokenizer.nextToken();
			if (token.equals("{")) {
				previousTokens = "{";
				if(!tokenizer.hasMoreTokens())
					break;

				// should be one of "p", "f", "i", "j", "d", "u", "[" or "]"
				token = tokenizer.nextToken();
				previousTokens = "{"+token;
				specialSymbolExpansion = null;
				if(token.length() == 1 && tokenizer.hasMoreTokens()){
					switch(token.charAt(0)){
					case 'b':
						specialSymbolExpansion = getDrawingBaseName();
						break;
					case 'x':
						specialSymbolExpansion = getDrawingBaseNameSansExt();
						break;
					case 'n':
						specialSymbolExpansion = getDrawingBaseNameNoExt();
						break;
					case 'r':
						specialSymbolExpansion = getDrawingBaseNameRadix();
						break;
					case 'd':
						specialSymbolExpansion = getDrawingDir(null);
						break;
					case 'f':
						specialSymbolExpansion = RunExternalCommand.getTmpFilePrefix();
						break;
					case 'i':
						specialSymbolExpansion = RunExternalCommand.getAddonDir();
						break;
					case 'j':
						// jPicEdt installation directory
						specialSymbolExpansion = MiscUtilities.getJPicEdtHome();
						break;
					case 'p':
						specialSymbolExpansion = RunExternalCommand.getTmpPath();
						break;
					case 'u':
						specialSymbolExpansion = JPicEdt.getUserSettingsDirectory();
						break;
					case '[':
						specialSymbolExpansion = "{";
						break;
					case ']':
						specialSymbolExpansion = "}";
						break;
					}
				}
				if(specialSymbolExpansion != null)
				{
					token = tokenizer.nextToken();
					if (token.equals("}"))
						token = specialSymbolExpansion;
					else
						token = previousTokens + token;
					previousTokens = null;
					specialSymbolExpansion = null;
				}
				else
				{
					token = previousTokens;
					previousTokens = null;
				}
			}
			realCommand.append(token);
		}
		if(previousTokens != null)
			realCommand.append(previousTokens);

		// Vincent Bela�che: doit-on vraiment faire le replace ?
		return realCommand.toString().replace('/',File.separatorChar);
	}

	/**
	 * @param defaultDir valeur � renvoyer quand il n'y a pas de dessin
	 * actif. Si �gal � <code>null</code> alors la fonction renvoie le
	 * r�pertoire courant utilisateur en telle circonstance.
	 * @return le r�pertoire du dessin actif. S'il n'y a pas de dessin actif
	 * (ou que c'est un &quot;Sans nom&quot;, alors renvoie defaultDir si &ne;
	 * <code>null</code>, ou le r�pertoire courant utilisateur sinon.
	 * @since jPicEdt 1.6
	 */
	public static String getDrawingDir(String defaultDir){
		String drawingDir = null;
		PEDrawingBoard activeBoard = JPicEdt.getMDIManager().getActiveDrawingBoard();
		if(activeBoard != null)
			drawingDir = new File(activeBoard.getTitle()).getParent();
		if(drawingDir == null)
		{
			if(defaultDir == null)
				drawingDir = System.getProperty("user.dir");
			else
				drawingDir = defaultDir;
		}
		return drawingDir;
	}


	/**
	 * <code>getDrawingBaseName</code> renvoie le nom de base du fichier o�
	 * est stock� le dessin de la planche � dessin courante, ou
	 * <code>null</code> si le dessin n'est pas dans un fichier.
	 *
	 * Par exemple si le chemin complet du fichier est
	 * <code>/mon/dossier/mondessin.tex</code>, alors la valeur renvoy�e sera
	 * <code>mondessin.tex</code>
	 *
	 * @return le <code>String</code> nom de base du fichier dessin.
	 * @since jPicEdt 1.6
	 */
	public static String getDrawingBaseName(){
		String basename = null;
		File   basenameFile = null;
		PEDrawingBoard activeBoard = JPicEdt.getMDIManager().getActiveDrawingBoard();
		if(activeBoard != null)
			basenameFile = new File(activeBoard.getTitle());
		if(basenameFile != null)
			basename = basenameFile.getName();
		return basename;
	}


	/**
	 * Supprime la derni�re extension du nom de base renvoy� par
	 * <code>getDrawingBaseName</code>, et renvoie ce qui en reste apr�s suppression.
	 *
	 * <p>Quelques exemples:
	 * <table border="1">
	 * <tr><th>Nom de base</th><th>valeur renvoy�e</th></tr>
	 * <tr><td><code>toto.jpe.tex</code></td><td><code>toto.jpe</code></td></tr>
	 * <tr><td><code>toto0.tex</code></td><td><code>toto0</code></td></tr>
	 * <tr><td><code>toto1</code></td><td><code>toto1</code></td></tr>
	 * </table>
	 * @see #getDrawingBaseName()
	 * @return Le nom de base sans extension
	 * @since jPicEdt 1.6
	 */
	public static String getDrawingBaseNameSansExt(){
		String basenameSansExt = getDrawingBaseName();
		if(basenameSansExt != null){
			int extPos = -1;
			int nextPos = -1;
			while((nextPos = basenameSansExt.indexOf('.',extPos+1)) >= 0){
				extPos = nextPos;
			}
			if(extPos >= 0){
				basenameSansExt = basenameSansExt.substring(0,extPos);
			}
		}

		return basenameSansExt;
	}

	/**
	 * Supprime toute les extensions au sens propre du nom de base renvoy� par
	 * <code>getDrawingBaseName</code>, et renvoie ce qui reste apr�s
	 * suppression. La partie renvoy� constitue donc le radical du nom de base.
	 *
	 * <p>Quelques exemples:
	 * <table border="1">
	 * <tr><th>Nom de base</th><th>valeur renvoy�e</th></tr>
	 * <tr><td><code>toto.jpe.tex</code></td><td><code>toto</code></td></tr>
	 * <tr><td><code>toto18.jpe.tex</code></td><td><code>toto18</code></td></tr>
	 * <tr><td><code>toto0.tex</code></td><td><code>toto0</code></td></tr>
	 * <tr><td><code>toto1</code></td><td><code>toto1</code></td></tr>
	 * </table>
	 * @see #getDrawingBaseName()
	 * @return Le nom de base sans aucune extension
	 * @since jPicEdt 1.6
	 */
	public static String getDrawingBaseNameRadix(){
		String basenameRadix = getDrawingBaseName();
		if(basenameRadix != null){
			int nextPos = basenameRadix.indexOf('.');
			if(nextPos >= 0)
				basenameRadix = basenameRadix.substring(0,nextPos);
		}

		return basenameRadix;
	}


	/**
	 * Supprime toute les extensions du nom de base renvoy� par
	 * <code>getDrawingBaseName</code>, et renvoie ce qui reste apr�s
	 * suppression. Les extensions peuvent �tre consitu�es d'extensions au
	 * sens propre, ou d'un num�ro d�cimal en fin de radical du nom de base
	 *<p>
	 * En d'autres termes, cela consiste � supprimer tout num�ro d�cimal en
	 * fin du radical tel que renvoy� pas <code>getDrawingBaseNameRadix</code>
	 * et � renvoyer ce qui reste apr�s suppression.
	 * <p>Quelques exemples:
	 * <table border="1">
	 * <tr><th>Nom de base</th><th>valeur renvoy�e</th></tr>
	 * <tr><td><code>toto.jpe.tex</code></td><td><code>toto</code></td></tr>
	 * <tr><td><code>toto18.jpe.tex</code></td><td><code>toto</code></td></tr>
	 * <tr><td><code>toto0.tex</code></td><td><code>toto</code></td></tr>
	 * <tr><td><code>toto1</code></td><td><code>toto</code></td></tr>
	 * </table>
	 * @see #getDrawingBaseName()
	 * @see #getDrawingBaseNameRadix()
	 * @return Le nom de base sans aucune extension
	 * @since jPicEdt 1.6
	 */
	public static String getDrawingBaseNameNoExt(){
		String basenameNoExt = getDrawingBaseNameRadix();
		if(basenameNoExt != null){
			int extPos = basenameNoExt.length();
			while(extPos > 0){
				int c = basenameNoExt.charAt(extPos-1);
				if(c >= '0' && c <= '9')
					--extPos;
				else
					break;
			}
			basenameNoExt = basenameNoExt.substring(0,extPos);
		}

		return basenameNoExt;
	}

}

/// CommandLineBuilder.java ends here
