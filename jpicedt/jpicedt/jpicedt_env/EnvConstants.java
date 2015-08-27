// EnvConstants.java --- -*- coding: iso-8859-1 -*-
// Copyright 2012 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: EnvConstants.java,v 1.5 2013/03/27 06:53:16 vincentb1 Exp $
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
package jpicedt.jpicedt_env;

import java.lang.String;
import java.util.Properties;
import java.io.FileNotFoundException;
import java.io.*;

public class EnvConstants{
	public final static String[] APP_ICONS = {
		"Cocorico", "EightiesArentDead", "JesusChrist"};
	public final static String APP_ICON = APP_ICONS[0];

	public static void main(String[] args) throws IOException{
		boolean propertyFile = false;
		for(String arg : args){
			if(arg.equals("--store-properties"))
				propertyFile = true;
			else if(propertyFile){
				Properties props = new Properties();
				props.setProperty("appicon", APP_ICON);

				FileOutputStream fos= new FileOutputStream(arg);
				props.store(fos,"jpicedt.jpicedt_env.EnvConstants --store-properties");
				fos.close();

				propertyFile = false;
			}
		}
	}
}

/// EnvConstants.java ends here
