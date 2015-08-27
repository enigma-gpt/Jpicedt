# HelpLang.mak --- -*- coding: iso-8859-1 -*-
# Copyright 2011/2013 Vincent Bela�che
#
# Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
# Version: $Id: helplang.Makefile,v 1.1 2013/02/26 22:16:54 vincentb1 Exp $
# Keywords:
# X-URL: http://www.jpicedt.org/
#
# Ce logiciel est r�gi par la licence CeCILL soumise au droit fran�ais et respectant les principes de
# diffusion des logiciels libres. Vous pouvez utiliser, modifier et/ou redistribuer ce programme sous les
# conditions de la licence CeCILL telle que diffus�e par le CEA, le CNRS et l'INRIA sur le site
# "http://www.cecill.info".
#
# En contrepartie de l'accessibilit� au code source et des droits de copie, de modification et de
# redistribution accord�s par cette licence, il n'est offert aux utilisateurs qu'une garantie limit�e.  Pour
# les m�mes raisons, seule une responsabilit� restreinte p�se sur l'auteur du programme, le titulaire des
# droits patrimoniaux et les conc�dants successifs.
#
# A cet �gard l'attention de l'utilisateur est attir�e sur les risques associ�s au chargement, �
# l'utilisation, � la modification et/ou au d�veloppement et � la reproduction du logiciel par l'utilisateur
# �tant donn� sa sp�cificit� de logiciel libre, qui peut le rendre complexe � manipuler et qui le r�serve donc
# � des d�veloppeurs et des professionnels avertis poss�dant des connaissances informatiques approfondies.
# Les utilisateurs sont donc invit�s � charger et tester l'ad�quation du logiciel � leurs besoins dans des
# conditions permettant d'assurer la s�curit� de leurs syst�mes et ou de leurs donn�es et, plus g�n�ralement,
# � l'utiliser et l'exploiter dans les m�mes conditions de s�curit�.
#
# Le fait que vous puissiez acc�der � cet en-t�te signifie que vous avez pris connaissance de la licence
# CeCILL, et que vous en avez accept� les termes.
#
## Commentary:
#
#
#
#
#
## Code:
#
# liste des images qu'on veut reg�n�rer
HELP_LANG_SOURCE := $(wildcard helplang.*.jpe.tex)
HELP_LANG_PNG := $(patsubst helplang.%.jpe.tex, \
	../../help-files/$(LANG_ID)/img/%.png,$(HELP_LANG_SOURCE))
HELP_LANG_EPS := $(patsubst helplang.%.jpe.tex, \
	../../help-files/$(LANG_ID)/img/%.eps,$(HELP_LANG_SOURCE))

.PHONY: all clean debug

all: $(HELP_LANG_PNG) $(HELP_LANG_EPS)


../../help-files/$(LANG_ID)/img/%.eps : helplang.%.jpe.tex help-$(LANG_ID).%.def
#	compilation LaTeX
	latex  -job-name=$(patsubst helplang.%.jpe.tex,help-$(LANG_ID).%,$<) \
		-interaction=nonstopmode '\def\LangId{$(LANG_ID)}\input' $<

#	g�n�ration du PostScript avec dvips
	dvips -E $(patsubst helplang.%.jpe.tex, help-$(LANG_ID).%.dvi,$<) \
		-o $(patsubst helplang.%.jpe.tex, ../../help-files/$(LANG_ID)/img/%.eps,$<)

../../help-files/$(LANG_ID)/img/%.png : ../../help-files/$(LANG_ID)/img/%.eps
#	conversion en PNG avec Ghostscript
	$(GS) -dBATCH -dNOPAUSE -sOutputFile=$@ -sDEVICE=pngalpha \
		-dTextAlphaBits=4 -dGraphicsAlphaBits=4 -dSAFER -r308 \
		-q $<

clean:
	rm $(HELP_LANG_PNG) $(HELP_LANG_EPS)

debug:
	$(info [info] HELP_LANG_PNG=>$(HELP_LANG_PNG))
	$(info [info] HELP_LANG_SOURCE=>$(HELP_LANG_SOURCE))
	$(info [info] HELP_LANG_EPS=>$(HELP_LANG_EPS))
	$(info [info] HelpLang/GS=>$(GS))
