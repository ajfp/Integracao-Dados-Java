<?xml version="1.0" encoding="UTF-8"?>	
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"	
    xmlns:xs="http://www.w3.org/2001/XMLSchema"	
    exclude-result-prefixes="xs"	
    version="1.0">	
    <xsl:output method="html"></xsl:output>	
    <xsl:variable name="paisesFile" select="document('paises.xml')" />
    <xsl:template match="Factos">	
        <html><body>	
                <h2>Info Pais</h2>	
				<table border="1">
				<tr bgcolor="#9acd32">
				<th>ISO</th><th>Pais</th><th>Capital</th><th>Populacao</th></tr>	
                <xsl:for-each select="Pais"> 
		<tr>			
			<td width="80"> <xsl:value-of select="./@ISO"/></td>
			<td width="80"> <xsl:value-of select="$paisesFile/Paises/Pais[@ISO = current()/@ISO]/Nome"/></td>
                  	<td width="80"> <xsl:value-of select="./Capital"/></td>
			<td width="80"><xsl:value-of select="./Populacao"/></td>	
                </tr> 
 </xsl:for-each> 
 </table> 
  </body></html>
  </xsl:template> 
  </xsl:stylesheet> 