<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xalan"
	xmlns:saxon="http://saxon.sf.net/" xmlns="http://www.w3.org/1999/xhtml"
	extension-element-prefixes="saxon"><xsl:output method="text" indent="no" /><xsl:template match="//units"># This file lists features by category in the site. Not currently used by build.xml; provided in case we need something like this later.
#
<xsl:for-each select="unit">
	<xsl:if test="./properties/property[@name='org.eclipse.equinox.p2.type.category']">
		<xsl:for-each select="./provides/provided">
<xsl:value-of select="substring-after(@name,'.')" />.category.features = \
<xsl:for-each select="../../requires/required"><xsl:value-of select="substring-before(@name,'.feature.group')" /><xsl:if test="not(position()=last())">,\
</xsl:if>
</xsl:for-each>
#
</xsl:for-each></xsl:if></xsl:for-each>
</xsl:template></xsl:stylesheet>
