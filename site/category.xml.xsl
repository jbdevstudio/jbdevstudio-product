<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xalan" xmlns:saxon="http://saxon.sf.net/" xmlns:xhtml="http://www.w3.org/1999/xhtml" 	extension-element-prefixes="saxon" exclude-result-prefixes="xsl xalan saxon xhtml">
	<xsl:output method="html" indent="yes" />
	<xsl:template match="/">
		<xsl:apply-templates select="*"/>
	</xsl:template>
	<xsl:template match="*">
		<xsl:copy >
			<xsl:for-each select="@*">
				<xsl:copy />
			</xsl:for-each>
			<xsl:apply-templates />
		</xsl:copy>
	</xsl:template>
		<xsl:template match="feature">
			<feature url="features/{@id}_${{{@id}.version}}.jar" id="{@id}" version="${{{@id}.version}}"><xsl:apply-templates select="*"/></feature>
	</xsl:template>
</xsl:stylesheet>
