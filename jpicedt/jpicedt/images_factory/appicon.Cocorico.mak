# -*- mode: makefile -*-

define JPICEDT_ICO
 -size 960x960 \
  xc:transparent \
 -fill white \
 -draw 'rectangle 480,384,960,960'\
 -draw 'rectangle 0,0,320,820'   \
 -strokewidth 48                 \
 -stroke 'rgb(85,131,157)'       \
 -draw 'arc 0,48 320,96 5,175'   \
 -draw 'arc 0,168,320,216 5,175' \
 -draw 'arc 0,288,320,336 5,175' \
 -strokewidth 0 \
\( \
 -size 576x480	\
 gradient:red-blue \
 -rotate 90  \
 -background transparent \
 -extent 960x960-320 \
 \) \
 -composite \
 \( \
 -size 960x960 \
 xc:transparent \
 -pointsize 300 \
 -font VTC-JoeleneHand-Regular-Italic \
 -fill 'rgb(232,136,36)'  \
 -stroke 'rgb(232,136,36)'  \
 -strokewidth 5 \
 -draw 'rotate -13 scale 2,3 text -30,188 j' \
 -draw 'rotate -15.5 scale 2,3 text -35,188 j' \
 -draw 'rotate -18 scale 2,3 text -40,188 j' \
 -pointsize 190 \
 -strokewidth 5 \
 -font Arial-Black \
 -stroke black \
 -fill white  \
 -draw 'scale 2,3.5 text 200,150 P'\
 -font Verdana-Bold \
 -stroke red \
 -fill black \
 -draw 'scale 2,3.5 text 300,250 E'\
 \) \
 -composite \
 -resize 48x48
endef

$(OUT):
	convert $(JPICEDT_ICO) $@