// EnvConstants.java --- -*- coding: iso-8859-1 -*-
// Copyright 2012 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: EnvConstants.java,v 1.5 2013/03/27 06:53:16 vincentb1 Exp $
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
