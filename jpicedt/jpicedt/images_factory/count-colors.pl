#!/usr/bin/env perl
# count-colors.pl --- -*- coding: utf-8 -*-
# Copyright 2012 Vincent Belaïche
#
# Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
# Version: $Id: count-colors.pl,v 1.1 2013/01/13 18:04:40 vincentb1 Exp $
# Keywords:
# X-URL: http://www.jpicedt.org/
#
# Ce logiciel est régi par la licence CeCILL soumise au droit français et respectant les principes de
# diffusion des logiciels libres. Vous pouvez utiliser, modifier et/ou redistribuer ce programme sous les
# conditions de la licence CeCILL telle que diffusée par le CEA, le CNRS et l'INRIA sur le site
# "http://www.cecill.info".
#
# En contrepartie de l'accessibilité au code source et des droits de copie, de modification et de
# redistribution accordés par cette licence, il n'est offert aux utilisateurs qu'une garantie limitée.  Pour
# les mêmes raisons, seule une responsabilité restreinte pèse sur l'auteur du programme, le titulaire des
# droits patrimoniaux et les concédants successifs.
#
# A cet égard l'attention de l'utilisateur est attirée sur les risques associés au chargement, à
# l'utilisation, à la modification et/ou au développement et à la reproduction du logiciel par l'utilisateur
# étant donné sa spécificité de logiciel libre, qui peut le rendre complexe à manipuler et qui le réserve donc
# à des développeurs et des professionnels avertis possédant des connaissances informatiques approfondies.
# Les utilisateurs sont donc invités à charger et tester l'adéquation du logiciel à leurs besoins dans des
# conditions permettant d'assurer la sécurité de leurs systèmes et ou de leurs données et, plus généralement,
# à l'utiliser et l'exploiter dans les mêmes conditions de sécurité.
#
# Le fait que vous puissiez accéder à cet en-tête signifie que vous avez pris connaissance de la licence
# CeCILL, et que vous en avez accepté les termes.
#
## Commentary:

# Compte le nombre de couleurs utilisées dans un dessin jPicEdt.

## Installation:

## Code:
use strict;
use warnings;
use feature qw(say);
use PerlIO;
use XML::Bare;


my $version='$Id: count-colors.pl,v 1.1 2013/01/13 18:04:40 vincentb1 Exp $';

my %colors = ();
my $color_count = 0;

my $inputfile;
my $quiet;
my $verbose;
my $line;
my $linenb;
my $file;
my $errornb = 0;
my %color = ();

my $i = 0;
while($i < @ARGV){
	if($i + 1 < @ARGV)
	{
		if($ARGV[$i] =~ /\A-(i|-input)\Z/)
		{
			$inputfile = $ARGV[$i+1];
			$i = $i +2;
		}
		else
		{
			goto ONE_ARG;
		}
	}
	else
	{
	  ONE_ARG:
		{
			if($ARGV[$i] =~ /\A-(q|-quiet)\Z/)
			{
				$quiet = 1;
				$i ++;
			}
			elsif($ARGV[$i] =~ /\A-(v|-verbose)\Z/)
			{
				$verbose = 1;
				$i ++;
			}
			elsif($ARGV[$i] eq "--version")
			{
				print "Version of count-colors.pl = $version\n";
				exit 0;
			}
			elsif($ARGV[$i] =~ /\A-\?|--help\Z/)
			{
				usage(0);
				exit;
			}
			else
			{
				print "Invalid remaining arguments: @ARGV[$i .. $#ARGV]\n";
				usage(-1);
			}
		}
	}
}

my $in;
if($inputfile)
{ 
	open($in,  "< :encoding(ISO-8859-1)", $inputfile)  or die "Can't open $inputfile $!";
}
else
{
	$in = \*STDIN;
}

my $ok_status = 0;
$i = 0;

my @file = ();

LINE_0: while(<$in>){
	$line = $_ ;
	$linenb++ ;
	if($line =~ /^%%Begin JPIC-XML$/)
	{
		$ok_status = 1;
		last LINE_0;
	}
}


if(!$ok_status)
{
	say STDERR $linenb, ":Can't find `%%End JPIC-XML'";
	++ $errornb;
	goto END_OF_SCRIPT;
}

$ok_status = 0;

LINE_1: while(<$in>){
	$line = $_;
	$linenb++ ;
	if($line =~ /^%%End JPIC-XML$/)
    {
		$ok_status = 1;
		last LINE_1;
    }
	elsif($line =~ /^%(.*)$/)
    {
		$file[$i ++] = $1;
		next LINE_1;
	}
	else
	{
		say STDERR  $linenb, "Ignored invalid line `",  $line, "'";
		last LINE_1;
	}
}

if(!$ok_status)
{
	goto END_OF_SCRIPT;
}


$file = join("\n", @file);

my @remaining_nodes;
push @remaining_nodes, XML::Bare::xmlin($file);

$file = undef;

while(@remaining_nodes != 0)
{
	my $tree = pop @remaining_nodes;

	my $k;
	my $v;
	while(($k, $v) = each %{$tree}){

		if(!ref($v))
        {
			if($k =~ /-color\Z/)
            {
				my $found = $colors{$v};
				if(!$found)
                {
					++ $color_count;
					$colors{$v} = 1;
				}
			}
		}
		elsif(ref($v) eq "HASH")
        {
			push @remaining_nodes, $v;
        }
		elsif(ref($v) eq "ARRAY")
        {
			foreach(@{$v}){
				push @remaining_nodes, $_;
			}
		}
		else
        {
			say STDERR "\$v = $v";
		}
	}
}

print STDOUT "$color_count";



END_OF_SCRIPT:

